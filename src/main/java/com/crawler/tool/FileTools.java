package com.crawler.tool;

import java.io.File;

/** @author cse
 * @version 1.0 
 * 2015-10-26 下午05:49:42
 */

public class FileTools {

	public static void deleteFile(File file){ 
		if(file==null){
			return;
		}
		if(file.exists()){                    //判断文件是否存在
			if(file.isFile()){                    //判断是否是文件
				file.delete();                       //delete()方法 你应该知道 是删除的意思;
			}else if(file.isDirectory()){              //否则如果它是一个目录
				File files[] = file.listFiles();               //声明目录下所有的文件 files[];
				for(int i=0;i<files.length;i++){            //遍历目录下所有的文件
					deleteFile(files[i]);             //把每个文件 用这个方法进行迭代
				} 
			} 
			file.delete(); 
		}else{ 
			System.out.println("所删除的文件不存在！"+'\n'); 
		} 
	}
}

