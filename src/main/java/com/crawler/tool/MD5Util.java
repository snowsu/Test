package com.crawler.tool;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** @author cse
 * @version 1.0 
 * 2015-11-30 下午04:35:06
 */

public class MD5Util {


	/**
	 * 获取字符串的MD5
	 * @param string
	 * @return
	 */
	public static String getStringMD5(String string){
		byte[] byteString = string.getBytes(Charset.forName("utf-8"));
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(byteString);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}

