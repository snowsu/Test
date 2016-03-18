package com.crawler.tool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.crawler.bean.Site;
import com.crawler.parse.BaseParse;

/**
 * 解析工厂 
 * @author cse
 * @version 1.0 
 * 2015-10-26 下午12:54:11
 */

public class ParseFactory {

	private Map<String, BaseParse> parseMap=Collections.synchronizedMap(new HashMap<String, BaseParse>());

	private Map<String, Site> siteMap=Collections.synchronizedMap(new HashMap<String, Site>());

	private static ParseFactory single;

	public static synchronized ParseFactory single(){
		if(single==null){
			single=new ParseFactory();
		}
		return single;
	}

	private ParseFactory() {}



	public void put(Site site){
		try {
			BaseParse parse=(BaseParse) Class.forName(site.getParseClass()).newInstance();
			parseMap.put(DomainUtil.getDomainByUrl(site.getStartUrl()), parse);
			siteMap.put(DomainUtil.getDomainByUrl(site.getStartUrl()),site);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**通过网址得到对应的解析类*/
	public BaseParse getParseByUrl(String url){
		return parseMap.get(url);
	}


	/**通过url得到对应的site*/
	public Site getSiteByUrl(String url){
		return siteMap.get(url);
	}
}

