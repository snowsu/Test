package com.crawler.bean;

import java.util.List;

/** @author cse
 * @version 1.0 
 * 2015-10-26 下午01:04:41
 */

public class Site {

	/**入口网址*/
	private String startUrl;
	
	/**解析类*/
	private String parseClass;
	
	/**抓取深度*/
	private int depth;
	
	/**匹配规则*/
	private List<String> regexList;
	
	/**启动多少线程*/
	private int threadCount;
	
	/**网站的名称*/
	private String siteName;
	
	

	

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getParseClass() {
		return parseClass;
	}

	public void setParseClass(String parseClass) {
		this.parseClass = parseClass;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
		if(depth<0){
			this.depth=Integer.MAX_VALUE;
		}
	}

	public List<String> getRegexList() {
		return regexList;
	}

	public void setRegexList(List<String> regexList) {
		this.regexList = regexList;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
	
	
}

