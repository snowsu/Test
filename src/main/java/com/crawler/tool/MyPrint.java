package com.crawler.tool;

import org.apache.log4j.Logger;

/**打印*/
public class MyPrint {

	/**输出到控制台*/
	public static void print(Object obj){
		if(Config.log()){
			System.out.println(obj);
		}
	}

	public static void printError(String errorMessage) {
		System.err.println("----------wyySpiderError----------------");
		System.err.println(errorMessage);
		System.err.println("===============================================");
	}


	public static void printError(Exception ex){
		System.err.println("----------wyySpiderError----------------"+ex.getMessage());
		ex.printStackTrace();
		System.err.println("===============================================");
	}


	public static void printError(Object obj,Exception ex){
		System.err.println("----------wyySpiderError----------------"+obj.toString()+"-------------------------"+ex.getMessage());
		ex.printStackTrace();
		System.err.println("===============================================");
	}



	/**打印日志*/
	public static void printLog(Class<?> cls,String message){
		Logger logger=Logger.getLogger(cls);
		logger.error(message);
	}

}
