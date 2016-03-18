package com.fyqz.n2;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.tool.Config;

/**
 * 
 * 
 * @description 爬虫运行主类
 * @author gengqingbin
 * @modify gengqingbin
 * @modifyDate 2016-3-7 下午2:52:36
 * @notes 未填写备注
 * @version 1.0.0
 *
 */
public class Crawler {
	private static Logger logger = LoggerFactory.getLogger(Crawler.class);
	private Scheduler sched;
	//门脸模式对象，监听mq的消息
	private CrawlerFacade facade;
	private static Crawler crawler;
	private CronTrigger trigger;
	private Crawler() {
		// TODO Auto-generated constructor stub
		facade = CrawlerFacade.getInstance();
	}
	/**
	 * 
	 * @description 单例，只启动一次任务
	 * @return 
	 * @returnType Crawler
	 * @exception 
	 * @since  1.0.0
	 */
	public static Crawler getInstance(){
		if(crawler == null){
			crawler = new Crawler();
		}
		return crawler;
	}
	/**
	 * 
	 * @description 启动任务
	 * @throws Exception 
	 * @returnType void
	 * @exception 
	 * @since  1.0.0
	 */
	public void start() throws Exception{		
		//取任务定时的周期
        String period = Config.getString("crawler.period");
        logger.info("任务周期:{},启动定时任务...",period);
        //创建LzstoneTimeTask的定时任务
        JobDetail jobDetail = new JobDetail("lzstoneJob",sched.DEFAULT_GROUP,CrawlerTaske.class);
        //目标 创建任务计划 0 0 0 * * ? 代表每天的0点触发
        trigger = new CronTrigger("lzstoneTrigger","lzstone",period);
        //初始化Scheduler  
        sched = new org.quartz.impl.StdSchedulerFactory().getScheduler();
        sched.scheduleJob(jobDetail,trigger);
        sched.start();
	}
	
	
	
	/**
	 * 
	 * @description 重新设置任务执行周期
	 * @param cronExpression 执行周期表达式
	 * @throws ParseException 
	 * @returnType void
	 * @exception 
	 * @since  1.0.0
	 */
	public void resetJob(String cronExpression) throws ParseException{
		trigger.setCronExpression(cronExpression);
	}
	
	//停止
	public void stop() throws Exception{
	    sched.shutdown();
	}
	
	/**
	 * 
	 * @description 任务启动类
	 * @param args 
	 * @returnType void
	 * @exception 
	 * @since  1.0.0
	 */
	public static void main(String[] args) {
		
		logger.info("开始启动爬虫...");
		Crawler crawler = Crawler.getInstance();
		try {
			crawler.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		logger.info("启动完成...");
		
//		if(args.length > 1){
//			if(StringUtils.equals(args[0], "start")){
//				logger.info("开始启动爬虫...");
//				Crawler crawler = Crawler.getInstance();
//				try {
//					crawler.start();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					logger.error(e.getMessage());
//				}
//				logger.info("启动完成...");
//			}else if(StringUtils.equals(args[0], "stop")){
//				System.exit(0);
//				logger.info("停止爬虫系统完成...");
//			}
//		}
		
	}	
}
