package com.crawler.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.jsoup.select.Elements;
import org.nutz.lang.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.bean.Site;
import com.fyqz.n2.FileManager;

/**解析xml配置类*/
public class XmlLoader {
	// 日志对象
	private static Logger logger = LoggerFactory.getLogger(XmlLoader.class);
	/** 网址参数配置文件 */
	private static final String webSitePath =Config.getString("xmlPath","crawler.xml");

	private static Map<String, Site> webSiteMap =Collections.synchronizedMap(new LinkedHashMap<String, Site>());

	private static XmlLoader single;

	private XmlLoader() {}
	
	
	public static XmlLoader getInstance(boolean reload){
		if(reload){
			single=null;
			webSiteMap.clear();
		}
		if(single==null){
			single=new XmlLoader();
			try {
				single.load(webSitePath);
				print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return single;
	}
	

	public static XmlLoader getInstance(){
		if(single==null){
			single=new XmlLoader();
			try {
				single.load(webSitePath);
				print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return single;
	}

	/**
	 * 加载并解析crawler.xml
	 * @param file
	 * @throws Exception
	 */
	public void load(String file) throws Exception{
		Document doc=readXml(webSitePath);
		Elements xmlList=doc.select("webSites webSite");
		for (Element element : xmlList) {
			System.out.println("加载---"+element.text());
			Site site=parseSite(element);
			webSiteMap.put(site.getStartUrl(), site);
			
			ParseFactory.single().put(site);
		}
	}

	/**解析site*/
	private Site parseSite(Element siteEle){
		Site site=new Site();
		site.setStartUrl(siteEle.attr("startUrl"));
		site.setParseClass(siteEle.attr("parseClass"));
		site.setDepth(Integer.valueOf(siteEle.attr("depth"))); //如果深度<0、则全站抓取
		site.setThreadCount(Integer.valueOf(siteEle.attr("threadCount")));	
		site.setSiteName(siteEle.attr("siteName"));
		List<String> regexList=new ArrayList<String>();
		Elements cateEleList=siteEle.select("regexList regex");
		for (Element element : cateEleList) {
			regexList.add(element.text());
		}

		site.setRegexList(regexList);
		return site;
	}

	/**获取本地xml并格式化*/
	public Document readXml(String name) throws Exception{
		String html=FileUtils.readFileToString(Files.findFile(name), "utf-8");
		Parser parse=new Parser(new XmlTreeBuilder());
		Document doc=parse.parseInput(html,"");
		return doc;
	}




	private static void print(){
		MyPrint.printLog(XmlLoader.class, "---------------------------------------------");

		for (String key : webSiteMap.keySet()) {
			System.out.println(key);
			System.out.println("webSiteMap的"+key+"值是------------------"+JSONObject.fromObject(webSiteMap.get(key)));
		}

		MyPrint.printLog(XmlLoader.class, "---------------------------------------------");
	}

	public Map<String, Site> getWebSiteMap() {
		return webSiteMap;
	}
	
	/**
	 * 
	 * @description 添加一个搜索网站
	 * @param site
	 * @return 
	 * @throws Exception 
	 * @returnType boolean
	 * @exception 
	 * @since  1.0.0
	 */
	public boolean addWebSite(Site site) throws Exception{
		boolean result = true;
		//判断网址是否已存在
		if(webSiteMap.containsKey(site.getStartUrl())){
			result = false;
			logger.info("网址已存在");
		}else{
			//添加网站对象到MAP中
			webSiteMap.put(site.getStartUrl(), site);
			//修改配置文件
			result = FileManager.getInstance().addWebSite(site);	
		}			
		return result;
	}
}
