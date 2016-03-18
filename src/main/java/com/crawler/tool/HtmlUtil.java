package com.crawler.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;


/**下载网页，解析网页
 * 
 * @author cse
 * 
 */
public class HtmlUtil {

	private static String UA = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C))";
	private static List<String> errorMessList=new ArrayList<String>();
	private static Map<String, String> specialCodeMap=new HashMap<String, String>();

	static{
		initErrorMess();
		specialCodeMap.put("&amp;", "&");
		specialCodeMap.put("&lt;", "<");
		specialCodeMap.put("&gt;", ">");
		specialCodeMap.put("&quot;", "\"");
		specialCodeMap.put("&nbsp;", " ");
		specialCodeMap.put("&copy;", "©");
		specialCodeMap.put("&reg", "®");
	}



	/**html特殊字符转意思*/
	public static String htmlFilter(String html){
		for (String key : specialCodeMap.keySet()) {
			html=StringUtils.replace(html, key, specialCodeMap.get(key));
		}

		return html;
	}


	/**
	 * @param url 需要抓取的网址
	 * @return 抓取结果
	 * @throws Exception
	 */
	public static Document getHtmlSourceByUrl(String url)throws Exception{
		if(StringUtils.isEmpty(url))return null;
		return getDocByGet(url);		
	}
	
	/**获取url网页文档*/
	public static Document getHtmlByUrl(String url){	
		if(StringUtils.isEmpty(url)){
			return null;
		}else{
			return getDocByGet(url);
		}
	}

	/**根据rss地址获取网页文档*/
	public static Document getHtmlByRss(String url){
		Parser parse=new Parser(new XmlTreeBuilder());
		Document doc=null;
		try {
			doc = parse.parseInput(getCon(url).downHTMLByGet(),"");
		} catch (Exception e) {
			MyPrint.printError(url,e);
		}
		return doc;
	}




	/**下载一个文件到指定目录*/
	public static void copyURLToFile(URL source, File destination){
		if ((destination.getParentFile() != null) && (!(destination.getParentFile().exists()))){
			destination.getParentFile().mkdirs();
		}

		FileOutputStream output=null;
		InputStream input=null;

		try {			
			URLConnection conn=source.openConnection();	
			conn.addRequestProperty("Referer", "http://"+source.getHost());
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			conn.setConnectTimeout(Config.getInt("timeOut",5000)); //设置连接超时时间		
			conn.setReadTimeout(Config.getInt("timeOut",5000));
			input=conn.getInputStream();
			output= new FileOutputStream(destination);
			IOUtils.copy(input, output);
		}catch(Exception ex){
//			MyPrint.printError(ex);
		}finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}

	}


	private static Document getDocByGet(String url){
		Document doc=new Document("");
		for (int i = 1; i <=Config.getInt("retryTimes"); i++) {
			try{
				doc= getCon(url).get();   /**得到网页信息*/
			}catch(Exception ex){
//				MyPrint.printError(url+" 第 "+i+"次调取失败"+ex.getMessage(),ex);				
			}finally{
				if(!htmlError(doc)){
					break;
				}
			}
		}
		return doc;
	}



	private static Connection getCon(String url) throws Exception{
		return Jsoup.connect(url)	
		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.header("Referer", "http://"+new URL(url).getHost()+"/")
		.userAgent(UA)
		.ignoreContentType(true)
		.followRedirects(true)
		.ignoreHttpErrors(true)
		.timeout(Config.getInt("crawler.socket.timeout",5000));
	}

	/**初始化错误信息*/
	private static void initErrorMess(){
		errorMessList.add("出错了");
		errorMessList.add("不存在");
		errorMessList.add("404");
		errorMessList.add("500");
	}



	private static boolean htmlError(Document doc){

		if(StringUtils.isEmpty(doc.title()))return true;

		for (String error : errorMessList) {
			if(doc.title().contains(error)){
				return true;
			}
		}

		return false;
	}




	public static byte[] doHttpPost(String url2,byte[] t) throws Exception {
		byte[] data=null;
		try {
			URL url = new URL(url2);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			OutputStream output=conn.getOutputStream();
			output.write(t);
			InputStream input=conn.getInputStream();
			int length=0,len=256,off=0;
			byte[] buffer=new byte[len];			
			do{
				length=input.read(buffer, 0, len);				
				if(length>0){
					if(data==null){
						data=new byte[length];
					}
					else{
						byte[] temp=new byte[data.length+length];
						for(int i=0;i<data.length;i++){
							temp[i]=data[i];
						}
						data=temp;
					}
					for(int i=0;i<length;i++){
						data[i+off]=buffer[i];
					}
				}
				off+=length;
			}while(length>0);

			output.close();
			input.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

}
