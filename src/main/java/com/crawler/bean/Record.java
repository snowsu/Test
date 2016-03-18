package com.crawler.bean;

import java.util.Date;


/** @author cse
 * @version 1.0 
 * 2015-10-26 下午01:25:09
 */

public class Record {

	private String url;
	private String title;
	private String content;
	private Date crawlTime;
	//文件来源
	private String source;
	//是否已发布
	private boolean isPublish;
	
	
	public Date getCrawlTime() {
		return crawlTime;
	}
	public void setCrawlTime(Date crawlTime) {
		this.crawlTime = crawlTime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public boolean getIsPublish() {
		return isPublish;
	}
	public void setIsPublish(boolean isPublish) {
		this.isPublish = isPublish;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

	@Override
	public String toString() {
		return url+"--->"+title;
	}
}

