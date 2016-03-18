package com.crawler.parse;

import java.io.File;
import java.net.URL;
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
import com.crawler.tool.MD5Util;
import com.crawler.tool.MyBoolFilter;
import com.crawler.tool.ParseFactory;

/**
 * 下载图片的解析类 
 * @author cse 
 * @version 1.0 
 * 2015-11-27 下午03:32:41
 */

public class DownFileParse extends BaseParse{

	@Override
	public void after(Record record) {

	}

	@Override
	public Record parseRecord(String url, String title, String html) {
		return null;
	}



	/**根据page得到页面内的连接*/
	@Override
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
			getImgs(doc, page.getDepth(), baseUrl);
			return getAllLinks(doc, page.getDepth(), baseUrl);
		}
		return null;
	};



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



	/**下载所有的图片*/
	private void getImgs(Document doc,int depth,String baseUrl){
		Elements elList=doc.select("img[src]");
		String url=null;
		for (Element element : elList) {
			url=element.attr("abs:src");
			if(DomainUtil.getDomainByUrl(url).equals(baseUrl)){
				downFile(url, baseUrl);
			}
		}
	}


	private String fileRegex=".*\\.(jpg|png|gif|jpeg|bmp|mp3|mp4|swf|mpeg|pdf|doc|zip|rar|docx|xls|sql|txt).*";

	/**判断是否是文件*/
	public boolean isFile(String url){
		if(url.matches(fileRegex)){
			return true;
		}
		return false;
	}


	/**下载文件*/
	private void downFile(String url,String baseUrl){
		try {
			boolean hasDown=MyBoolFilter.getFilter().containsAndAdd(url);
			if(!hasDown){
				System.out.println(url);
				String prefix=StringUtils.substringAfterLast(url, ".");
				if(StringUtils.isEmpty(prefix)){
					prefix="jpg";
				}
				String path=new StringBuffer().append("down").append(File.separator).append(baseUrl).append(File.separator).append(MD5Util.getStringMD5(url)).append(".").append(prefix).toString();			
				HtmlUtil.copyURLToFile(new URL(url), new File(path));
			}
		} catch (Exception e) {
		}
	}

}

