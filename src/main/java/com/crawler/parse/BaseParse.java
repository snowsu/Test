package com.crawler.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.crawler.bean.Page;
import com.crawler.bean.Record;
import com.crawler.bean.Site;
import com.crawler.tool.DomainUtil;
import com.crawler.tool.HtmlUtil;
import com.crawler.tool.ParseFactory;

/** @author cse 
 * @version 1.0 
 * 2015-10-26 下午12:52:44
 */

public abstract class BaseParse {

	/**根据page得到页面内的连接*/
	public List<Page> work(Page page){	
		String baseUrl=DomainUtil.getDomainByUrl(page.getUrl());
		Site site=ParseFactory.single().getSiteByUrl(baseUrl);
		Document doc=getHtmlByUrl(page.getUrl());

		Record record=null;

		for (String string :site.getRegexList()) {
			if(page.getUrl().matches(string)){
				record=parseRecord(doc.baseUri(),doc.title(),doc.html());
				break;
			}
		}

		if(record!=null){
			after(record);
		}

		if(page.getDepth()+1<=site.getDepth()){
			return getAllLinks(doc, page.getDepth(), baseUrl);
		}
		return null;
	};

	/**获取网页源码*/
	public Document getHtmlByUrl(String url){
		return HtmlUtil.getHtmlByUrl(url);
	}

	/**解析文档*/
	public abstract Record parseRecord(String url,String title,String html);

	/**用户保存、事后处理record*/
	public abstract void after(Record record); 

	/**抽取本页面所有连接*/
	public List<Page> getAllLinks(Document doc,int depth,String baseUrl){
		List<Page> result=new ArrayList<Page>();
		Elements elList=doc.select("a[href]");
		String url=null;
		for (Element element : elList) {
			url=element.attr("abs:href");
			url=StringUtils.substringBeforeLast(url,"#");
			if(DomainUtil.getDomainByUrl(url).equals(baseUrl)){
				if(isFile(url)){
					continue;
				}
				Page page=new Page(url,depth+1);
				result.add(page);
			}
		}
		return result;
	}


	private String fileRegex=".*\\.(jpg|png|gif|jpeg|bmp|mp3|mp4|swf|mpeg|pdf|doc|zip|rar|docx|xls|sql|txt).*";

	/**判断是否是文件*/
	public boolean isFile(String url){
		if(url.matches(fileRegex)){
			return true;
		}
		return false;
	}
}

