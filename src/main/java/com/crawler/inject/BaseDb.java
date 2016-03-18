package com.crawler.inject;

import com.crawler.bean.Page;

/** @author wyy 
 * @version 1.0 
 * 2015-10-26 下午01:22:21
 */

public abstract class BaseDb {

	public String dbName;

	public BaseDb(String dbName) {
		this.dbName=dbName;
	}

	/**打开数据库*/
	public abstract void openDb();

	/**清空数据库*/
	public abstract boolean deleteDb();

	/**添加记录*/
	public abstract boolean add(Page page);

	/**获取下一个记录*/
	public abstract Page next();
	
	/**判断是否存在该数据*/
	public abstract boolean exist(Page page);

	/**关闭数据库*/
	public abstract void closeDb();
	
	/**当前数据库数据总数*/
	public abstract long count();
	
	/**当前指针位置*/
	public abstract long currentIndex();
}

