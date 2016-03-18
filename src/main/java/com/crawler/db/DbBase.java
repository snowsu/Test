package com.crawler.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;


public abstract class DbBase {


	/**获取当前表名*/
	public abstract String getTableName();

	/*************************************************一些通用的增删改查方法***********************************************************************************/	

	/**添加*/
	public boolean add(String sql,Object...params){
		return excuteSQL(sql, params);
	}

	/**批量添加*/
	public boolean addBatch(String sql,Object[][] paramList){
		return excuteSQLBatch(sql, paramList);
	}


	/**删除*/
	public boolean deleteBySql(String sql,Object...params){
		return excuteSQL(sql, params);
	}

	/**批量删除*/
	public boolean deleteBatch(String sql,Object[][] paramList){
		return excuteSQLBatch(sql, paramList);
	}


	/**更新*/
	public boolean updateBySql(String sql,Object...params){
		return excuteSQL(sql, params);
	}

	/**批量更新*/
	public boolean updateBatch(String sql,Object[][] paramList){
		return excuteSQLBatch(sql, paramList);
	}


	/**查询多条数据*/
	public <T> List<T> search(String sql,Class<T> cls,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new BeanListHandler<T>(cls),params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**查询多条数据*/
	public <T> List<T> searchColumn(String sql,Class<T> cls,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new ColumnListHandler<T>(),params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**查询一条数据*/
	public <T> T searchOne(String sql,Class<T> cls,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new BeanHandler<T>(cls),params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**查询一组数据*/
	public List<Map<String, Object>> search(String sql,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new MapListHandler(),params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**查询一行数据*/
	public Map<String, Object> searchLine(String sql,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new MapHandler(),params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**查询唯一结果*/
	public <T> T getUnion(String sql,Class<T> cls,Object...params){
		QueryRunner runn=new QueryRunner(DbSource.getDataSource());
		try {
			return runn.query(sql,new ScalarHandler<T>(1),params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**添加、修改、删除*/
	public boolean excuteSQL(String sql,Object...params){
		try {
			QueryRunner runn=new QueryRunner(DbSource.getDataSource());
			runn.update(sql,params);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**批量添加、修改、删除*/
	public boolean excuteSQLBatch(String sql,Object[][] paramList){		
		try {			
			QueryRunner runn=new QueryRunner(DbSource.getDataSource());
			runn.batch(sql, paramList);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**获取当前表中数据的总条数*/
	public long count(){
		return getUnion("select count(*) from "+getTableName(),Long.class);
	}


	/**清空表数据*/
	public boolean truncateTable(){
		return updateBySql("truncate table "+getTableName());
	}


	/**
	 * 插入后，得到id号
	 * @param sql
	 * @param params
	 * @return 如果返回值>0说明插入成功，如果返回值=0说明没值
	 */
	public long addAndGetId(String sql,Object...params){
		try {
			QueryRunner runn=new QueryRunner(DbSource.getDataSource());
			int rows=runn.update(sql, params);
			if(rows>0){
				return runn.query("select last_insert_id()",new ScalarHandler<Long>(1)).longValue();
			}else{
				return 0L;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}		
	}


}
