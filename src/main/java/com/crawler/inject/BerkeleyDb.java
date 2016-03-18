package com.crawler.inject;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.nutz.lang.Files;

import com.crawler.bean.Page;
import com.crawler.tool.BerkeleyTool;
import com.crawler.tool.Config;
import com.crawler.tool.FileTools;
import com.crawler.tool.MyBoolFilter;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/** @author wyy 
 * @version 1.0 
 * 2015-10-26 下午01:42:29
 */

public class BerkeleyDb extends BaseDb {

	private EnvironmentConfig envConfig = null;
	private Database db0 = null; 
	private Environment dbEnvironment;
	private Cursor cursor=null;

	private BerkeleyTool dbTool=BerkeleyTool.instance(UUID.randomUUID().toString().replace("-", ""));

	public BerkeleyDb(String dbName) {
		super(dbName);
	}

	@Override
	public boolean add(Page page) {
		if(exist(page)){
			return false;
		}		
		try {
			DatabaseEntry dataKey = new DatabaseEntry(page.getUrl().getBytes("UTF-8"));
			DatabaseEntry dataValue = new DatabaseEntry(new byte[]{(byte)page.getDepth()});			
			OperationStatus status = db0.put(null, dataKey, dataValue);
			if (status == OperationStatus.SUCCESS) {				
				dbTool.insert(String.valueOf(dbTool.count()+1),page.getUrl());				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}


//	@Override
//	public boolean exist(Page page) {
//		try {
//			DatabaseEntry datakey = new DatabaseEntry(page.getUrl().getBytes("UTF-8"));
//			DatabaseEntry data = new DatabaseEntry();
//			OperationStatus status = db0.get(null, datakey, data,LockMode.READ_COMMITTED);
//
//			if (status == OperationStatus.SUCCESS){
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
	@Override
	public boolean exist(Page page) {
		return MyBoolFilter.getFilter().containsAndAdd(page.getUrl());
	}


	@Override
	public void closeDb() {
		if(cursor!=null){
			cursor.close();
		}
		dbTool.dropDb();
		db0.close();
		dbEnvironment.cleanLog();
		dbEnvironment.close();
		cursorIndex.set(0);
	}

	@Override
	public boolean deleteDb() {
		return false;
	}
	


	private AtomicLong cursorIndex=new AtomicLong(0);

	@Override
	public Page next(){
		String url=dbTool.get(String.valueOf(cursorIndex.incrementAndGet()));
		if(StringUtils.isNotEmpty(url)){
			int v=get(url);
			if(v>0){
				return new Page(url, v);
			}else{
				cursorIndex.decrementAndGet();
			}
		}else{
			cursorIndex.decrementAndGet();
		}
		return null;
	}



	/**得到该key对应的value*/
	public int get(String key) {
		try {
			DatabaseEntry datakey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry data = new DatabaseEntry();
			OperationStatus status = db0.get(null, datakey, data,LockMode.READ_COMMITTED);
			if (status == OperationStatus.SUCCESS){
				return data.getData()[0];			
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}





	@Override
	public void openDb() {
		envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);			
		envConfig.setDurability(Durability.COMMIT_WRITE_NO_SYNC);

		//每次启动前先清空数据库
		File file = Files.findFile(Config.getString("dbFileName", "temp_data")+File.separator+dbName);

		if(file==null||!file.exists()){
			file=new File(Config.getString("dbFileName", "temp_data")+File.separator+dbName);
			file.mkdirs();
		}else{
			FileTools.deleteFile(file);
			file=new File(Config.getString("dbFileName", "temp_data")+File.separator+dbName);
			file.mkdirs();
		}

		dbEnvironment = new Environment(file, envConfig);
		EnvironmentMutableConfig c = dbEnvironment.getMutableConfig();
		c.setCachePercent(6);
		dbEnvironment.setMutableConfig(c);	

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setReadOnly(false);
		dbConfig.setTransactional(true);

		db0= dbEnvironment.openDatabase(null, dbName,dbConfig);	
		dbTool=BerkeleyTool.instance(UUID.randomUUID().toString().replace("-", ""));
	}

	@Override
	public long count() {
		return db0.count();
	}

	@Override
	public long currentIndex() {
		return cursorIndex.get();
	}

}

