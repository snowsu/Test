package com.fyqz.n2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import net.sf.json.JSONObject;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.bean.Site;
import com.crawler.tool.Config;
import com.crawler.tool.XmlLoader;

/**
 * 
 * 
 * @description 爬虫工具对外接口处理类
 * @author gengqingbin
 * @modify gengqingbin
 * @modifyDate 2016年2月21日 下午2:47:19
 * @notes 未填写备注
 * @version 1.0.0
 *
 */
public class CrawlerFacade implements MessageListener{
	
	// 日志对象
	private static Logger logger = LoggerFactory.getLogger(CrawlerFacade.class);
	private static CrawlerFacade crawlerFacade;
	// 系统配置文件管理对象
	FileManager fileManage = FileManager.getInstance();
	private ActiveMQConnectionFactory connectionFactory = null;
	
	private Destination dest = null;
	private Connection conn = null;
	private Session session = null;
	private MessageConsumer consumer = null;
	private MessageProducer replyProducer = null;
			
	public static void main(String[] args) throws JMSException {
		crawlerFacade = CrawlerFacade.getInstance();
		
//		// 创建连接工厂  
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");  
//        // 创建连接  
//        Connection connection;  
//        try {  
//            connection = connectionFactory.createConnection();  
//            connection.start();  
//            // 创建Session  
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
//            // 创建目标，就创建主题也可以创建队列  
//            Destination destination = session.createQueue("N2_SEARCH_CRAWLER");  
//            // 创建消息消费者  
//            MessageConsumer consumer = session.createConsumer(destination);  
//            // 接收消息，参数：接收消息的超时时间，为0的话则不超时，receive返回下一个消息，但是超时了或者消费者被关闭，返回null  
//            Message message = consumer.receive(1000);  
//            if (message instanceof TextMessage) {  
//                TextMessage textMessage = (TextMessage) message;  
//                String text = textMessage.getText();  
//                logger.info("接收的消息："+"\n"+text);  
//            } else {  
//                logger.info("接收的消息："+"\n"+message);  
//            }  
//            consumer.close();  
//            session.close();  
//            connection.close();  
//        } catch (JMSException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        }  
	}
	
	private CrawlerFacade(){
		init();
	}
	
	public static CrawlerFacade getInstance(){
		if(crawlerFacade == null){
			crawlerFacade = new CrawlerFacade();
		}
		return crawlerFacade;
	}

	/**
	 * 
	 * @description 初始化MQ连接
	 * @returnType void
	 * @exception 
	 * @since  1.0.0
	 */
	private void init() {
		String mq_url = Config.getString("mq.url");
		String subject = Config.getString("mq.queue.name");
		logger.info("连接mq队列， url:{},subject:{}",mq_url,subject);
		//连接mq
		connectionFactory = new ActiveMQConnectionFactory(mq_url);
		try {
			conn = connectionFactory.createConnection();
			conn.start();
			
			// 创建session及消息的目的地，并设定交互时使用的存储方式，同时定义队列名称，客户端通过此名称连接  
	        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);  
			//消息回复对象
			replyProducer = this.session.createProducer(null);  
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); 
			// 目的地是客户用来指定他生产消息的目标还有他消费消息的来源的对象.
			dest = session.createQueue(subject);
			// 会话创建消息的生产者将消息发送到目的地
			consumer = session.createConsumer(dest);			
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	
	
	

	/**
	 * 
	 * @description 添加网址对外接口 
	 * @param jsonData
	 * @return 
	 * @returnType String
	 * @exception 
	 * @since  1.0.0
	 */
	
	public String addWebSite(String jsonData){
		logger.debug("增加网址 开始,jsonData:{}",jsonData);
		//创建返回对象
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//转成MAP
		JSONObject jsonObject = JSONObject.fromObject(jsonData);          
        Map<String, Object> siteMap = JSONObject.fromObject(jsonObject);  
        
        //转换成site对象
        Site site = new Site();
		site.setStartUrl((String) siteMap.get("startUrl"));
		site.setDepth(Integer.valueOf(String.valueOf(siteMap.get("depth"))));
		site.setParseClass("com.crawler.parse.SimpleParse");
		site.setThreadCount(5);
		site.setSiteName((String) siteMap.get("siteName"));
		List<String> regexList = (List<String>) siteMap.get("regexList");
		site.setRegexList(regexList);
		
        //设置默认属性
      	try {
      		//更新缓存数据列表
			boolean result = XmlLoader.getInstance().addWebSite(site);
			if(result){
				resultMap.put("status", "success");
			} else{
				resultMap.put("status", "error");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultMap.put("status", "error");
			resultMap.put("errInfo", e.getMessage());
			e.printStackTrace();
		} finally{
			
		}
      	
      	JSONObject resultJson = JSONObject.fromObject(resultMap);
      	//返回操作结果
      	logger.info("增加网址 成功,jsonData:{}",resultJson);      	
		return resultJson.toString();
	}
	
	/**
	 * 
	 * @description 设置关键词接口
	 * @param jsonData
	 * @return 
	 * @returnType String
	 * @exception 
	 * @since  1.0.0
	 */
	public String setKeyWords(String jsonData){
		logger.debug("设置关键词接口开始,jsonData:{}",jsonData);
		//创建返回对象
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//转成MAP
		JSONObject jsonObject = JSONObject.fromObject(jsonData);          
        Map<String, Object> jsonMap = JSONObject.fromObject(jsonObject);  
        String keyWords = (String) jsonMap.get("keyWords");
     
        boolean result = fileManage.setKeyWord(keyWords);
        if(result){
        	resultMap.put("status", "success");
        }else{
        	resultMap.put("status", "error");
        }
        
    	JSONObject resultJson = JSONObject.fromObject(resultMap);
      	//返回操作结果
      	logger.info("设置关键词操作成功,jsonData:{}",resultJson);      	
		return resultJson.toString();
	}

	/**
	 * 
	 * @description 设置标签
	 * @param jsonData
	 * @return 
	 * @returnType String
	 * @exception 
	 * @since  1.0.0
	 */
	public String setTag(String jsonData){
		logger.debug("设置标签接口开始,jsonData:{}",jsonData);
		//创建返回对象
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//转成MAP
		JSONObject jsonObject = JSONObject.fromObject(jsonData);          
        Map<String, Object> jsonMap = JSONObject.fromObject(jsonObject);  
        String tag = (String) jsonMap.get("tag");
     
        boolean result = fileManage.setTag(tag);
        if(result){
        	resultMap.put("status", "success");
        }else{
        	resultMap.put("status", "error");
        }
        
    	JSONObject resultJson = JSONObject.fromObject(resultMap);
      	//返回操作结果
      	logger.info("设置标签操作成功,jsonData:{}",resultJson);      	
		return resultJson.toString();
	}
	
	/**
	 * 
	 * @description 设置周期
	 * @param jsonData
	 * @return 
	 * @returnType String
	 * @exception 
	 * @since  1.0.0
	 */
	public String setPeriod(String jsonData){
		logger.debug("设置周期接口开始,jsonData:{}",jsonData);
		//创建返回对象
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//转成MAP
		JSONObject jsonObject = JSONObject.fromObject(jsonData);          
        Map<String, Object> jsonMap = JSONObject.fromObject(jsonObject);  
        String period = (String) jsonMap.get("period");
     
        boolean result = fileManage.setPeriod(period);
        if(result){
        	resultMap.put("status", "success");
        }else{
        	resultMap.put("status", "error");
        }
        
    	JSONObject resultJson = JSONObject.fromObject(resultMap);
      	//返回操作结果
      	logger.info("设置周期操作成功,jsonData:{}",resultJson);      	
		return resultJson.toString();
	}
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		TextMessage txtMessage = (TextMessage)message;
        try {
        	String jsonResult = txtMessage.getText();
        	logger.info ("get message " + jsonResult);
        	System.out.println("get message " + jsonResult);
        	
        	//创建返回对象
    		Map<String,Object> resultMap = new HashMap<String,Object>();
    		//转成MAP
    		JSONObject jsonObject = JSONObject.fromObject(jsonResult);        
    		Map<String, Object> jsonMap = JSONObject.fromObject(jsonObject);  
    		//操作类型
    		String oper = String.valueOf(jsonMap.get("oper"));
    		//操作数据
    		String jsonData = String.valueOf(jsonMap.get("data"));
    		//操作结果
    		String result = null;
    		if(StringUtils.equals(oper, "addWebSite")){	//添加网站
    			result = this.addWebSite(jsonData);    			
    		} else if(StringUtils.equals(oper, "modifyWebSite")){	//修改网址
    			
    		} else if(StringUtils.equals(oper, "listWebSite")){	//查询网址
    			
    		} else if(StringUtils.equals(oper, "delWebSite")){	//删除网址
    			
    		} else if(StringUtils.equals(oper, "setKeyWords")){	//设置关键词
    			result = this.setKeyWords(jsonData);
    		}else if(StringUtils.equals(oper, "setPeriod")){	//设置周期
    			result = this.setPeriod(jsonData);
    		}else if(StringUtils.equals(oper, "setTag")){	//设置标签
    			result = this.setTag(jsonData);
    		}
    		
    		//定义返回对象
    		TextMessage response = this.session.createTextMessage(); 
    		response.setJMSCorrelationID(message.getJMSCorrelationID());
    		response.setText(result);
    		replyProducer.send(message.getJMSReplyTo(), response);
    		
        } catch (JMSException e) {
        	logger.error("error {}", e);
        }
	}
}
