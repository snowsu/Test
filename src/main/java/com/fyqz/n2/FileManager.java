package com.fyqz.n2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.crawler.bean.Site;

/**
 * 
 * 
 * @description 配置文件修改类
 * @author gengqingbin
 * @modify gengqingbin
 * @modifyDate 2016年2月19日 下午10:10:19
 * @notes 未填写备注
 * @version 1.0.0
 *
 */
public class FileManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 网址 xml配置文件
	private String crawlerFile = "crawler.xml";
	// 全局配置文件
	private String configFile = "config.properties";
	// 单例模式
	private static FileManager fm = new FileManager();

	private FileManager() {

	}

	public static FileManager getInstance() {
		if (fm == null) {
			fm = new FileManager();
		}
		return fm;
	}

	// 添加网址
	public boolean addWebSite(Site site) throws DocumentException, SAXException, IOException {
		logger.debug("修改{}配置文件，增加网址:{} 开始",crawlerFile,site.getStartUrl());
		// 读取XML文件
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("UTF-8");  
		String path = getConfigPath();  
		//系统参数配置文件
		File cf = new File(path+crawlerFile); 
		Document document = saxReader.read(cf);
		// 获取根元素
		Element webSites = document.getRootElement();
		
		// 创建webSite子节点
		Element webSite = webSites.addElement("webSite");
		// 添加属性
		webSite.addAttribute("startUrl", site.getStartUrl());
		webSite.addAttribute("parseClass", "com.crawler.parse.SimpleParse");
		webSite.addAttribute("depth", String.valueOf(site.getDepth()));
		webSite.addAttribute("threadCount", "5");
		webSite.addAttribute("siteName", site.getSiteName());

		// 添加regexList子节点
		Element regexList = webSite.addElement("regexList");
		// 添加regex子节点
		for (String regexStr : site.getRegexList()) {
			Element regex = regexList.addElement("regex");
			regex.addText(regexStr);
		}
		
		//写入文件
		FileOutputStream output = new FileOutputStream(cf); 		
		OutputFormat format = OutputFormat.createPrettyPrint();  
        // 利用格式化类对编码进行设置  
        format.setEncoding("UTF-8");  
       
        XMLWriter writer = new XMLWriter(output, format);  
        writer.write(document);  
        writer.flush();  
        writer.close(); 
        logger.info("增加网址:{} 成功",crawlerFile,site.getStartUrl());
		return true;
	}
	
	public boolean setKeyWord(String keyWords) {
		logger.debug("修改{}配置文件，设置关键字:{} 开始",configFile,keyWords);
		String path = getConfigPath();  
		//系统参数配置文件
		File cf = new File(path+configFile); 
		PropertiesConfiguration config;
		try {
			config = new PropertiesConfiguration(cf);
			//设置关键字
			config.setProperty("keyWords",keyWords); 
			config.save(cf); //保存文件
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("设置关键字:{} 成功",keyWords);
		return true;
	}
	
	
	
	public boolean setPeriod(String period) {
		logger.debug("修改{}配置文件，设置周期:{} 开始",configFile,period);
		String path = getConfigPath();  
		//系统参数配置文件
		File cf = new File(path+configFile); 
		PropertiesConfiguration config;
		try {
			config = new PropertiesConfiguration(cf);
			//设置周期
			config.setProperty("crawler.period",period); 
			config.save(cf); //保存文件
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("设置周期:{} 成功",period);
		return true;
	}
	
	
	public boolean setTag(String tag) {
		logger.debug("修改{}配置文件，设置标签:{} 开始",configFile,tag);
		String path = getConfigPath();  
		//系统参数配置文件
		File cf = new File(path+configFile); 
		PropertiesConfiguration config;
		try {
			config = new PropertiesConfiguration(cf);
			//设置标签
			config.setProperty("fatch_with_tag",tag); 
			config.save(cf); //保存文件
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("设置标签:{} 成功",tag);
		return true;
	}
	
	/**
	 * 
	 * @description 获取配置文件路径
	 * @return 
	 * @returnType String
	 * @exception 
	 * @since  1.0.0
	 */
	private String getConfigPath(){		
		//获取配置文件路径 bin目录
		String path = URLDecoder.decode(FileManager.class.getResource("/").getPath());  
		//获取config文件路径
		path = path.substring(1);
		return path;
	}

		
	public static void main(String[] args) {
		FileManager fm = FileManager.getInstance();
		
		fm.setKeyWord("I am l,33,66");
		
		Site site = new Site();
		site.setStartUrl("http://www.sina.com/");
		site.setParseClass("com.crawler.parse.SimpleParse");
		site.setDepth(5);
		site.setThreadCount(6);
		site.setSiteName("新浪");
		List<String> regexList = new ArrayList<String>();
		regexList.add("http://www.sina.com/[0-9]*.html");
		site.setRegexList(regexList);
		try {
			fm.addWebSite(site);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
