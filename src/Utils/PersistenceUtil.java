package Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Broker.MyQueue;
import Common.IpNode;
import Common.Message;
import Common.Topic;
import Test.DaoTest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PersistenceUtil {

	
	public static JSONArray persistenceQueue(ConcurrentHashMap<String,MyQueue> queueList) {
		//队列列表
		JSONArray Queue_List = new JSONArray();
		Iterator iterator = queueList.keySet().iterator();   
		MyQueue myqueue = new MyQueue();		
		//遍历broker得到各个队列
		while (iterator.hasNext()){    
			//消息列表
    		JSONArray Message_List = new JSONArray();      		
	        String key = (String) iterator.next(); 
	        myqueue = queueList.get(key);
	        LinkedList<Message> list = (LinkedList<Message>) myqueue.getReverseAll();        
	        //遍历每个消息队列，获得消息
	        Iterator<Message> queue_iterator = list.iterator();
	        while(queue_iterator.hasNext()) {
	        	//得到每个消息
	        	Message message = queue_iterator.next();        	
	        	int number = message.getNum();
	        	String mes = message.getMessage();
	        	int type = message.getType();
	        	Topic topic = message.getTopic();
	        	//得到每个消息的主题信息
	        	List<Integer> QueueId = topic.getQueue();
	        	String QueueName = topic.getTopicName();
	        	List<IpNode> Consumer = topic.getConsumer();
	        	//得到每个消息的主题的Ip列表
	        	Iterator<Integer> QueueId_iterator = QueueId.iterator();
	        	JSONArray QueueId_List = new JSONArray();
	        	while(QueueId_iterator.hasNext()) {
	        		Integer integer = QueueId_iterator.next();
	        		QueueId_List.add(integer);
	        	}
	        	//得到每个消息的主题的consumer信息
	        	Iterator<IpNode> Con_iterator = Consumer.iterator();
	        	JSONArray Consumer_List = new JSONArray();
	        	while (Con_iterator.hasNext()) {
					IpNode ipNode = Con_iterator.next();
					String ip = ipNode.getIp();
					int port = ipNode.getPort();					
					JSONObject IpNode_JSON = new JSONObject();
					IpNode_JSON.put("ip", ip);
					IpNode_JSON.put("port", port);					
					Consumer_List.add(IpNode_JSON);
				}	        	
	        	//消息主题json对象
	        	JSONObject MessageTopic = new JSONObject();
	        	MessageTopic.put("TopicName", QueueName);
	        	MessageTopic.put("QueueId_List", QueueId_List);
	        	MessageTopic.put("Consumer_List", Consumer_List);	        	
	        	//消息json对象
	    		JSONObject Message = new JSONObject();
	    		Message.put("MessageNum", number);
	    		Message.put("MessageContent", mes);
	    		Message.put("MessageType", type);
	    		Message.put("MessageTopic", MessageTopic);    		
	    		Message_List.add(Message);
	        }
	        //队列json
    		JSONObject Queue = new JSONObject();
    		Queue.put("QueueName", key);
    		Queue.put("Message_List", Message_List);  		
    		Queue_List.add(Queue);
	    }      
		return Queue_List;
	}
	
	//导出成JSON文件
	public static boolean Export(JSONArray list,String FilePath) throws IOException {
		File file = new File(FilePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		String jsonString = JsonFormatUtil.formatJson(list.toString());
		//System.out.println(jsonString);
        
		// 将格式化后的字符串写入文件
        Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        write.write(jsonString);
        write.flush();
        write.close();

//	    System.out.println("Done");  
	    
	    return true;
	}
	
	//读取JSON文件
	public static String Import(String fileName) {
		String jsonStr = "";
	    try {
	        File jsonFile = new File(fileName);
	        FileReader fileReader = new FileReader(jsonFile);

	        Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
	        int ch = 0;
	        StringBuffer sb = new StringBuffer();
	        while ((ch = reader.read()) != -1) {
	            sb.append((char) ch);
	        }
	        fileReader.close();
	        reader.close();
	        jsonStr = sb.toString();
	        return jsonStr;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public static List<IpNode> ExtractionConsumer(String json){
		List<IpNode> list = new ArrayList<IpNode>();
		JSONArray Consumer_List = JSONArray.fromObject(json);
		Iterator Consumer_List_iterator = Consumer_List.iterator();  
		while(Consumer_List_iterator.hasNext()) {
			JSONObject ipNode = (JSONObject) Consumer_List_iterator.next();
			String ip = ipNode.getString("ip");
    		int port = ipNode.getInt("port");
    		list.add(new IpNode(ip, port));
		}
		return list;
	}
	public static ConcurrentHashMap<String,MyQueue> Extraction(String json) {
		//队列列表
		ConcurrentHashMap<String,MyQueue> QueueList = new ConcurrentHashMap<>();
		//消息列表
		MyQueue myQueue = new MyQueue();
		JSONArray Queue_List = JSONArray.fromObject(json);
		Iterator Queue_List_iterator = Queue_List.iterator();   
		while(Queue_List_iterator.hasNext()) {
			JSONObject Queue = (JSONObject) Queue_List_iterator.next();
			//得到队列的名称和消息列表
			String queuename = Queue.getString("QueueName");
			JSONArray Message_List = Queue.getJSONArray("Message_List");
			
			System.out.println("队列名称："+queuename);
			System.out.println("该队列的消息列表"+Message_List.toString());
			
			Iterator Message_List_Iterator = Message_List.iterator();
			//遍历每个队列中的每个消息
			while(Message_List_Iterator.hasNext()) {
				JSONObject Message = (JSONObject) Message_List_Iterator.next();
				
				int messagenum =  Message.getInt("MessageNum");
				String messagecontent = Message.getString("MessageContent");
				int messagetype = Message.getInt("MessageType");
				JSONObject messagetopic = new JSONObject();
				messagetopic = Message.getJSONObject("MessageTopic");
				
				//主题所占用的队列id
				List<Integer> QueueId = messagetopic.getJSONArray("QueueId_List");
				HashSet<Integer> HQueueId = transformforInteger(QueueId);
				for (Object object : HQueueId) {
					System.out.println("hashset"+object);
				}
				System.out.println("主题所占用的队列id"+QueueId);
				//主题名称
	        	String TopicName = messagetopic.getString("TopicName");
	        	
	        	//主题对应的消费者
	        	List<IpNode> Consumer = messagetopic.getJSONArray("Consumer_List");
	        	List<IpNode> Consumer_List = new LinkedList<IpNode>();
	        	System.out.println("主题对应的消费者:"+Consumer.toString());
	        	
	        	Iterator Consumer_Iterator = Consumer.iterator();
	        	while(Consumer_Iterator.hasNext()) {
	        		JSONObject ipNode = (JSONObject) Consumer_Iterator.next();
	        		
	        		String ip = ipNode.getString("ip");
	        		int port = ipNode.getInt("port");
	        		
	        		IpNode ipnode = new IpNode(ip, port);
	        		Consumer_List.add(ipnode);
	        	}
	        	//将List转换成HashSet以创建Topic对象
	        	HashSet<IpNode> HConsumer = transform(Consumer_List);

	        	//创建Topic对象
	        	Topic topic = new Topic(TopicName, HQueueId, HConsumer);
	        	
	        	//创建Message对象
	        	Message message = new Message(messagecontent, topic, messagenum);
	        	message.setType(messagetype);
	        	
	        	myQueue.putAtHeader(message);
			}
			QueueList.put(queuename, myQueue);
		}
		System.out.println("从json取出数据转成对象："+QueueList);
		return QueueList;
}
	private static HashSet<IpNode> transform(List<IpNode> list) {
		HashSet<IpNode> set = new HashSet<IpNode>();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()) {
			set.add((IpNode) iterator.next());
		}
		return set;
	}
	
	private static HashSet<Integer> transformforInteger(List<Integer> list) {
		HashSet<Integer> set = new HashSet<Integer>();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()) {
			set.add((Integer) iterator.next());
		}
		return set;
	}
	public static JSONArray persistenceConsumer(List<IpNode> consumerAddress) {
		JSONArray Consumer_List = new JSONArray();
		Iterator<IpNode> it = consumerAddress.iterator();
		while (it.hasNext()) {
			IpNode ipNode = it.next();
			String ip = ipNode.getIp();
			int port = ipNode.getPort();
			JSONObject IpNode_JSON = new JSONObject();
			IpNode_JSON.put("ip", ip);
			IpNode_JSON.put("port", port);
			Consumer_List.add(IpNode_JSON);
		}
		return Consumer_List;
	}
	public static void main(String[] args) throws IOException {
		//测试用例1
		ConcurrentHashMap<String,MyQueue> queueList1 = new ConcurrentHashMap<String, MyQueue>();
		int k=0;
		for(int i=1;i<=1;i++) {
			MyQueue queue = new MyQueue();
			for(int j=1;j<2;j++) {
				Topic t = new Topic("t1", 1);
				IpNode ipnode = new IpNode("127.0.0.1", 8888);
				t.addConsumer(ipnode);
				t.addQueueId(1);
				Message msg = new Message("hh"+i*j, t, k++);
				queue.putAtHeader(msg);				
			}
			queueList1.put((i)+"", queue);
			}
		System.out.println("将对象转为json存入文件...");
		String path = PersistenceUtil.class.getResource("").getPath().toString().substring(1);
		File file = new File(path);
		String newPath1 = file.getParentFile().getParent()+"\\QueueList.json";
		String newPath2 = file.getParentFile().getParent()+"\\ConsumerAddress.json";
		PersistenceUtil.Export(PersistenceUtil.persistenceQueue(queueList1),newPath1);
		ConcurrentHashMap<String,MyQueue> List = PersistenceUtil.Extraction(PersistenceUtil.Import(newPath1));	
		Iterator iterator = List.keySet().iterator();   
		while (iterator.hasNext()) {
			String key = (String) iterator.next(); 
	        MyQueue myqueue = List.get(key);
	        LinkedList<Message> list = (LinkedList<Message>) myqueue.getReverseAll();        
	        //遍历每个消息队列，获得消息
	        Iterator<Message> queue_iterator = list.iterator();
	        while(queue_iterator.hasNext()) {
	        	//得到每个消息
	        	Message message = queue_iterator.next();        	
	        	int number = message.getNum();
	        	String mes = message.getMessage();
	        	int type = message.getType();
	        	Topic topic = message.getTopic();        	
	        	//测试用
	        	System.out.println("消息序号："+number);
	        	System.out.println("消息："+mes);
	        	System.out.println("消息类型："+type);
	        	System.out.println("消息主题："+topic);       	
	        	//得到每个消息的主题信息
	        	String QueueName = topic.getTopicName();
	        	List<Integer> QueueId = topic.getQueue();
	        	List<IpNode> Consumer = topic.getConsumer();  	
	        	//测试用
	        	System.out.println("该消息的主题所在的队列Id："+QueueId);
	        	System.out.println("该消息的主题的名称："+QueueName);
	        	System.out.println("该消息的主题的consumer："+Consumer);
		}

	}
}
}


