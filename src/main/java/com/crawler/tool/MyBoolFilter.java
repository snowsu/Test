package com.crawler.tool;

import org.apache.commons.lang.RandomStringUtils;
import org.nlpcn.commons.lang.bloomFilter.BloomFilter;

/** @author cse 
 * @version 1.0 
 * 2015-5-28 上午09:36:01
 */

public class MyBoolFilter {

	private static BloomFilter filter;

	static{
		try {
			filter=new BloomFilter(8);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BloomFilter getFilter() {
		return filter;
	}




	public static void clear(){
		try {
			filter=new BloomFilter(8);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		for (int i = 0; i < 10000000; i++) {
			if(i%10000==0){
				System.out.println(i);
			}
			MyBoolFilter.getFilter().containsAndAdd(RandomStringUtils.random(128));
		}

		System.out.println(MyBoolFilter.getFilter().containsAndAdd("a"));
	}
}

