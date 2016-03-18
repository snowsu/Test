package com.crawler.db.dao;

import org.apache.commons.lang.StringUtils;

import com.crawler.bean.Record;
import com.crawler.db.DbBase;
import com.crawler.tool.Config;

/** @author cse 
 * @version 1.0 
 * 2015-6-8 下午02:37:49
 */

public class RecordDao extends DbBase{

	@Override
	public String getTableName() {
		return Config.getString("record_table");
	}



	public boolean saveRecord(Record...records) throws Exception {
		String sql="insert DELAYED ignore into "+getTableName()+"(art_url,art_title,art_content,search_time,art_source,is_publish) values(?,?,?,?,?,?)";
		Object[][] paramList=new Object[records.length][StringUtils.countMatches(sql, "?")];
		for (int i = 0; i < records.length; i++) {
			Record record=records[i];
			Object[] obj=new Object[]{
					record.getUrl(),
					record.getTitle(),
					record.getContent(),
					record.getCrawlTime(),
					record.getSource(),
					record.getIsPublish()
			};
			
			paramList[i]=obj;
		}
		return super.addBatch(sql, paramList);
	}

}

