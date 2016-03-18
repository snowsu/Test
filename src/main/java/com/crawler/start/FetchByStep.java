package com.crawler.start;

import java.util.Timer;
import java.util.TimerTask;
import com.crawler.fetch.CrawlerBySingle;

/** @author cse
 * @version 1.0 
 * 2015-10-28 下午04:10:42
 */

public class FetchByStep {

	public static void main(String[] args) {
		new Timer().schedule(new TimerTask(){
			public void run() {
				CrawlerBySingle single=new CrawlerBySingle();
				single.start();
			}
		},0,20*60*1000);
	}
}

