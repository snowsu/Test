package com.crawler.parse;

import java.util.Date;
import java.util.Map;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;

import com.crawler.bean.Record;
import com.crawler.bean.Site;
import com.crawler.db.dao.RecordDao;
import com.crawler.tool.Config;
import com.crawler.tool.XmlLoader;

/**
 * @author cse
 * @version 1.0 2015-10-26 下午12:53:12
 */

public class SimpleParse extends BaseParse {

	RecordDao dao = new RecordDao();

	@Override
	public void after(Record record) {
		try {
			boolean flag = false;
			String[] keyWords = Config.getString("keyWords").split(",");
			for (int i = 0; i < keyWords.length; i++) {
				// 是否包含关键词
				if (record.getContent().contains(keyWords[i])) {
					flag = true;
					break;
				}
			}
			if (flag) {
				dao.saveRecord(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Record parseRecord(String url, String title, String html) {
		try {
			Record record = new Record();

			Map<String, Site> webSiteMap = XmlLoader.getInstance()
					.getWebSiteMap();
			for (String key : webSiteMap.keySet()) {
				if (url.startsWith(key)) {
					Site site = webSiteMap.get(key);
					record.setSource(site.getSiteName());
					break;
				}
			}

			News news = ContentExtractor.getNewsByHtml(html, url);
			record.setUrl(url);
			record.setTitle(title);
			if (Config.getBoolean("fatch_with_tag", false)) {
				record.setContent(news.getContentHtml());
			} else {
				record.setContent(news.getContent());
			}
			record.setCrawlTime(new Date());
			record.setIsPublish(Boolean.valueOf("0"));
			System.out.println(record);
			return record;
		} catch (Exception e) {
		}
		return null;
	}

}
