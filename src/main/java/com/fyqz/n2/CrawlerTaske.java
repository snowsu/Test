package com.fyqz.n2;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.fetch.CrawlerAll;

public class CrawlerTaske implements Job {
	private Logger logger = LoggerFactory.getLogger(CrawlerTaske.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		CrawlerAll all = new CrawlerAll();	
		logger.debug("启动所有爬虫对象");
		all.start();
	}

}
