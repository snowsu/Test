package com.crawler.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/***
 * 配置文件加载类
 * @author cse
 *
 */
public class Config {


	private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static Properties props = new Properties();

	static{
		loadPro("config.properties");
		loadPro("db.mysql.properties");
		print();
	}


	/**获取格式化时间*/
	public static String getFormatTime(String...pattern){
		if(pattern!=null&&pattern.length>0){
			return new SimpleDateFormat(pattern[0]).format(new Date());
		}else{
			return format.format(new Date());
		}
	}

	public static int getInt(String key){
		return Integer.parseInt(props.getProperty(key,"1"));
	}

	public static int getInt(String key,int def){
		return Integer.parseInt(props.getProperty(key,def+""));
	}


	public static long getLong(String key){
		return Long.parseLong(props.getProperty(key,"1"));
	}

	public static long getLong(String key,long def){
		return Long.parseLong(props.getProperty(key,def+""));
	}


	/**获取string形的*/
	public static String getString(String key){
		return props.getProperty(key);
	}

	/**获取string形的*/
	public static String getString(String key,String def){
		return StringUtils.isEmpty(props.getProperty(key))?def:props.getProperty(key);
	}


	public static boolean getBoolean(String key){
		return Boolean.valueOf(getString(key,"false"));
	}

	public static boolean getBoolean(String key,boolean def){
		return Boolean.valueOf(getString(key, def+""));
	}

	public static void setProperty(String key,String value){
		props.setProperty(key, value);
	}


	/**是否打印log*/
	public static boolean log(){
		return getBoolean("log");
	}


	/**获取存储图片的文件夹名字*/
	public static String getImgDir(){
		return "weiboImg";
	}


	private static void print(){
		System.out.println("----------------------------config----------------------------------");		
		Set<Object> keys=props.keySet();
		for (Object key : keys) {
			System.out.println(key+"       "+props.get(key));
		}
		System.out.println("--------------------------------------------------------------------");
	}


	//加载property文件
	private static void loadPro(String proName){
		InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(proName);		
		try {
			props.load(fis);
			fis.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}




	/**设置错误信息的输出地址*/
	public static void setErrorPath(){
		File file=new File("error"+File.separator+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		try {
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			FileOutputStream errror = new FileOutputStream(file);
			System.setErr(new PrintStream(errror));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
