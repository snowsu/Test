package com.crawler.fetch;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.nutz.lang.Files;

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

/**
 * 依次按照顺序下载各个站点 
 * @author cse
 * @version 1.0 
 * 2015-10-26 下午01:18:26
 */

public class CrawlerBySingle {

	private Queue<Page> linkQueue=new LinkedBlockingQueue<Page>(Config.getInt("memory.cache.size", 10000));
	private static AtomicInteger activeThread=new AtomicInteger(); //活动进程数
	private static AtomicBoolean running=new AtomicBoolean(false); //队列进程是否在运行

	/**从数据拿到空连接错误次数如果达到10次、认为任务结束*/
	private static int failTimes_fromdb=Config.getInt("failTimes.fromdb",10); 

	private long dbCount=0; //本次抓取的url总条数

	public void start(){

		File file = Files.findFile(Config.getString("dbFileName", "temp_data"));
		FileTools.deleteFile(file);  //清空上次临时文件

		Map<String, Site> siteMap=XmlLoader.getInstance(true).getWebSiteMap();
		for (String key : siteMap.keySet()) {
			MyPrint.printLog(CrawlerBySingle.class, "开始下载--->>"+key);
			running.set(true);
			downSite(siteMap.get(key));
			MyPrint.printLog(CrawlerBySingle.class, "下载完成===>>"+key+"---->本次共抓取["+dbCount+"]个url");
		}
	}


	/**下载站点*/
	private void downSite(Site site){
		BaseDb db=new BerkeleyDb(DomainUtil.getDomainByUrl(site.getStartUrl()));
		before(db,site);		
		QueueThread queueThread=new QueueThread(linkQueue, db);
		queueThread.start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		down(site,db);
		
		after(db);
	}



	private void down(Site site,BaseDb db){
		activeThread.set(site.getThreadCount());
		for (int i = 0; i < site.getThreadCount(); i++) {
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


	private void before(BaseDb db,Site site){
		db.openDb();
		db.add(new Page(site.getStartUrl(), 1));
	}

	private void after(BaseDb db){
		dbCount=db.count();
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

