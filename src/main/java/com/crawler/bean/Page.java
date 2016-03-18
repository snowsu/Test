package com.crawler.bean;

/** @author cse
 * @version 1.0 
 * 2015-10-26 下午01:22:59
 */

public class Page {

	private String url;
	private int depth;
	
	
	public Page() {
	}
	
	public Page(String url, int depth) {
		super();
		this.url = url;
		this.depth = depth;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}

