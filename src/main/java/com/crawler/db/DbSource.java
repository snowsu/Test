package com.crawler.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

public class DbSource {

	private DbSource() {}
	
	private static DataSource dataSource=null;
	
	static{
		Properties pro=new Properties();
		try {
			pro.load(DbUtils.class.getResourceAsStream("/db.mysql.properties"));   /**从properties文件读取数据库连接池配置*/
			dataSource=BasicDataSourceFactory.createDataSource(pro);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

}
