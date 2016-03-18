package com.crawler.tool;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Files;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


/** @author cse 
 * @version 1.0 
 * 2015-8-28 
 */

public class BerkeleyTool {


	public static void main(String[] args) {

		BerkeleyTool.instance().insert("a", "c");
		BerkeleyTool.instance().dropDb();

		BerkeleyTool.instance().insert("a", "c");
		BerkeleyTool.instance(true).insert("a", "d");
		BerkeleyTool.instance(true).insert("a", "d");
		BerkeleyTool.instance(true).insert("a", "d");
		BerkeleyTool.instance(true).insert("c", "8");
		System.out.println(BerkeleyTool.instance(true).getMul("a"));
		BerkeleyTool.instance(true).del("a");
		System.out.println(BerkeleyTool.instance(true).getMul("c"));
		System.out.println(BerkeleyTool.instance(true).getMul("a"));
		BerkeleyTool.instance(true).insert("c", "7");
		System.out.println(BerkeleyTool.instance(true).getMul("c"));

		System.out.println(BerkeleyTool.instance(true).next());

	}



	private static BerkeleyTool instance;
	private static EnvironmentConfig envConfig = null;
	private static Database db0 = null; 
	private static Environment dbEnvironment;
	private boolean followMulValue=false; //允许一个key有多个value
	private static Cursor cursor=null;

	private static Environment CreateEnvironment(String dbName) {
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
		return dbEnvironment;
	}

	private static void CreateEnvironmentConfig() {
		if (envConfig == null) {
			envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
			envConfig.setAllowCreate(true);			
			envConfig.setDurability(Durability.COMMIT_WRITE_NO_SYNC);
		}
	}

	private static DatabaseConfig CreateDatabaseConfig() {
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setReadOnly(false);
		dbConfig.setTransactional(true);
		dbConfig.setSortedDuplicates(instance.followMulValue);
		return dbConfig;
	}

	private static Database CreateDatabase(String dbname) {
		CreateEnvironmentConfig();
		Environment dbEnvironment = CreateEnvironment(dbname);
		Database db = dbEnvironment.openDatabase(null, dbname,CreateDatabaseConfig());		
		return db;
	}

	private BerkeleyTool() {}

	private BerkeleyTool(String dbName) {
		init(dbName);
	}


	public static synchronized BerkeleyTool instance() {
		if(instance==null){
			instance=new BerkeleyTool();
		}
		if (db0 == null) {
			synchronized (BerkeleyTool.class) {
				if (db0 == null) {
					db0 = CreateDatabase("db0");
					cursor = db0.openCursor(null, CursorConfig.DEFAULT);
				}
			}
		}
		return instance;
	}



	public static synchronized BerkeleyTool instance(String dbName) {
		if(instance==null){
			instance=new BerkeleyTool();
		}
		if (db0 == null) {
			synchronized (BerkeleyTool.class) {
				if (db0 == null) {
					db0 = CreateDatabase(dbName);
					cursor = db0.openCursor(null, CursorConfig.DEFAULT);
				}
			}
		}
		return instance;
	}


	/**是否允许一个key对应多个value*/
	public static synchronized BerkeleyTool instance(boolean followMulValue) {
		if(instance==null){
			instance=new BerkeleyTool();
			instance.followMulValue=followMulValue;
		}
		if (db0 == null) {
			synchronized (BerkeleyTool.class) {
				if (db0 == null) {
					db0 = CreateDatabase("db0");
					cursor = db0.openCursor(null, CursorConfig.DEFAULT);
				}
			}
		}
		return instance;
	}


	private static void init(String dbName) {
		if (db0 == null) {
			synchronized (BerkeleyTool.class) {
				if (db0 == null) {
					db0 = CreateDatabase(dbName);
				}
			}
		}
	}


	/**插入key,value*/
	public void insert(String key, String value) {
		try {
			DatabaseEntry dataKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry dataValue = new DatabaseEntry(value.getBytes("UTF-8"));			
			OperationStatus status = db0.put(null, dataKey, dataValue);
			if (status != OperationStatus.SUCCESS) {
				System.out.println("insert faild,status is " + status);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**得到该key对应的value*/
	public String get(String key) {
		try {
			DatabaseEntry datakey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry data = new DatabaseEntry();
			OperationStatus status = db0.get(null, datakey, data,LockMode.READ_COMMITTED);
			if (status == OperationStatus.SUCCESS){
				return new String(data.getData(),"UTF-8");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	/**获得该key对应的多个值*/
	public List<String> getMul(String key){
		List<String> result=new ArrayList<String>();
		Cursor cursor  = db0.openCursor(null, null);  
		try {
			DatabaseEntry foundKey = new DatabaseEntry(key.getBytes("utf-8"));
			DatabaseEntry foundData = new DatabaseEntry();  	
			OperationStatus retVal = cursor.getSearchKey(foundKey, foundData, LockMode.DEFAULT);
			while (retVal == OperationStatus.SUCCESS) {
				result.add(new String(foundData.getData(), "UTF-8"));  
				retVal = cursor.getNextDup(foundKey, foundData, LockMode.DEFAULT);  
			}  
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		cursor.close();

		return result;
	}



	/**删除该key*/
	public boolean del(String key) {
		try {
			DatabaseEntry dataKey = new DatabaseEntry(key.getBytes("UTF-8"));
			OperationStatus status = db0.delete(null,dataKey);
			if (status == OperationStatus.SUCCESS){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}



	/**是否存在该key*/
	public boolean exist(String key) {
		try {
			DatabaseEntry datakey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry data = new DatabaseEntry();
			OperationStatus status = db0.get(null, datakey, data,LockMode.READ_COMMITTED);

			if (status == OperationStatus.SUCCESS){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	private DatabaseEntry key = new DatabaseEntry();
	private DatabaseEntry value = new DatabaseEntry();


	/**获取下一个value*/
	public String next() {
		while (true) {
			if (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				try {
					return new String(value.getData(), "UTF-8");
				} catch (Exception ex) {
					continue;
				}
			} else {
				return null;
			}
		}
	}




	public long count(){		
		return db0.count();
	}

	/***是否把以前的数据缓冲到硬盘*/
	public long count(boolean saveToDisk){
		if(saveToDisk){
			dbEnvironment.sync();

		}
		return db0.count();
	}

	public void dropDb(){
		close();
		instance=null;
		db0=null;
	}

	public void close(){
		cursor.close();
		db0.close();
		dbEnvironment.cleanLog();
		dbEnvironment.close();
	} 

}

