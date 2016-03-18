package com.crawler.fetch;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.nutz.lang.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.bean.Page;
import com.crawler.bean.Site;
import com.crawler.inject.BaseDb;
import com.crawler.inject.BerkeleyDb;
import com.crawler.parse.BaseParse;
import com.crawler.tool.Config;
import com.crawler.tool.DomainUtil;
import com.crawler.tool.FileTools;
import com.crawler.tool.MyBoolFilter;
import com.crawler.tool.MyPrint;
import com.crawler.tool.ParseFactory;
import com.crawler.tool.XmlLoader;
import com.fyqz.n2.CrawlerTaske;

/**
 * 下载全部站点
 * thor cse 
 * @version 1.0 
 * 2015-10-26 下午01:18:26
 */

public class CrawlerAll {
	private Logger logger = LoggerFactory.getLogger(CrawlerAll.class);
	private Queue<Page> linkQueue=new LinkedBlockingQueue<Page>(Config.getInt("memory.cache.size", 10000));
	private static AtomicInteger activeThread=new AtomicInteger(); //活动进程数
	private static AtomicBoolean running=new AtomicBoolean(false); //队列进程是否在运行
	private BaseDb db=new BerkeleyDb("allSite");
	private int fetch_allSite_thread_count=Config.getInt("fetch.allSite.thread.count",10); //抓取全部站点的最大线程数

	/**从数据拿到空连接错误次数如果达到10次、认为任务结束*/
	private static int failTimes_fromdb=Config.getInt("failTimes_fromdb",10); 

	public void start(){
		before();
		Map<String, Site> siteMap=XmlLoader.getInstance(true).getWebSiteMap();
		
		for (String key : siteMap.keySet()) {
			logger.info("开始下载{}的数据...",key);
			addSiteToDb(siteMap.get(key));
		}

		downSite();
		long allCount=db.count();
		after(db);
		logger.info("下载完成,本次共抓取[{}]个url",allCount);
	}


	/**下载站点*/
	private void downSite(){
		QueueThread queueThread=new QueueThread(linkQueue, db);
		queueThread.start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		down();
	}


	private void down(){
		activeThread.set(fetch_allSite_thread_count);
		for (int i = 0; i <fetch_allSite_thread_count; i++) {
			CrawlerThread crawlerThread=new CrawlerThread(linkQueue,db);
			crawlerThread.start();
		}

		while(activeThread.get()>0){
			try {
				Thread.sleep(5000);
				System.out.println("total url size="+db.count()+",current index="+db.currentIndex()+",queue memory size is="+linkQueue.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	/**把站点放到数据库*/
	private void addSiteToDb(Site site){
		db.add(new Page(site.getStartUrl(), 1));
	}


	private void before(){
		File file = Files.findFile(Config.getString("dbFileName", "temp_data"));
		FileTools.deleteFile(file);  //清空上次临时文件
		db.openDb();
		running.set(true);
	}

	private void after(BaseDb db){
		db.closeDb();
		MyBoolFilter.clear();
	}


	/**队列线程*/
	private class QueueThread extends Thread{
		private Queue<Page> queue;
		private BaseDb db;
		private int errorTimes=0;

		public QueueThread(Queue<Page> queue,BaseDb db) {
			this.queue=queue;
			this.db=db;
		}

		@Override
		public void run() {
			while(true){
				Page page=db.next();
				if(page!=null){
					//防止队列缓存数据过多
					while(queue.size()>=Config.getInt("memory_cache_size",10000)/2){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					queue.add(page);
					errorTimes=0;
				}else{
					if(errorTimes++>failTimes_fromdb){
						running.set(false);
						break;
					}else{
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}


	/**抓取线程*/
	private class CrawlerThread extends Thread{
		private Queue<Page> queue;
		private BaseDb db;

		public CrawlerThread(Queue<Page> queue,BaseDb db) {
			this.queue=queue;
			this.db=db;
		}

		@Override
		public void run() {
			while(true){
				try{
					Page page=queue.poll();
					if(page!=null){
						downPage(page);
					}else{
						if(running.get()){
							Thread.sleep(500);
						}else{
							break;
						}
					}
				}catch (Exception e) {}
			}

			activeThread.decrementAndGet();
		}


		public void downPage(Page page){
			BaseParse downParse=ParseFactory.single().getParseByUrl(DomainUtil.getDomainByUrl(page.getUrl()));
			List<Page> pageLinks=downParse.work(page);
			if(pageLinks!=null){				
				for (Page link : pageLinks) {
					db.add(link);
				}
			}
		}
	}
}

