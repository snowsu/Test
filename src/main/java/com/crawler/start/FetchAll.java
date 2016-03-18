package com.crawler.start;

import java.util.Timer;
import java.util.TimerTask;

import com.crawler.fetch.CrawlerAll;
import com.crawler.tool.Config;

/** @author cse
 * @version 1.0 
 * 2015-10-28 下午04:08:53
 */

public class FetchAll {

	public static void main(String[] args) {

		CrawlerAll all=new CrawlerAll();
		all.start();
	}
}

