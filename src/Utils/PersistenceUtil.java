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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Broker.MyQueue;
import Common.IpNode;
import Common.Message;
import Common.Topic;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PersistenceUtil {

	
	public static JSONArray persistence(ConcurrentHashMap<String,MyQueue> queueList) {
		//�����б�
		JSONArray Queue_List = new JSONArray();
		
		
		
		Iterator iterator = queueList.keySet().iterator();   
		MyQueue myqueue = new MyQueue();
		
		//����broker�õ���������
		while (iterator.hasNext()){    
			//��Ϣ�б�
    		JSONArray Message_List = new JSONArray();  
    		
	        String key = (String) iterator.next(); 
	        //System.out.println("key:"+key);
	        
	        myqueue = queueList.get(key);

	        LinkedList<Message> list = (LinkedList<Message>) myqueue.getReverseAll();
	        
	        //����ÿ����Ϣ���У������Ϣ
	        Iterator<Message> queue_iterator = list.iterator();
	        while(queue_iterator.hasNext()) {
	        	//�õ�ÿ����Ϣ
	        	Message message = queue_iterator.next();
	        	
	        	int number = message.getNum();
	        	String mes = message.getMessage();
	        	int type = message.getType();
	        	Topic topic = message.getTopic();
	        	
	        	//������
//	        	System.out.println("��Ϣ��ţ�"+number);
//	        	System.out.println("��Ϣ��"+mes);
//	        	System.out.println("��Ϣ���ͣ�"+type);
//	        	System.out.println("��Ϣ���⣺"+topic);
	        	
	        	//�õ�ÿ����Ϣ��������Ϣ
	        	List<Integer> QueueId = topic.getQueue();
	        	String QueueName = topic.getTopicName();
	        	List<IpNode> Consumer = topic.getConsumer();
	        	
	        	//������
//	        	System.out.println("����Ϣ���������ڵĶ���Id��"+QueueId);
//	        	System.out.println("����Ϣ����������ƣ�"+QueueName);
//	        	System.out.println("����Ϣ�������consumer��"+Consumer);
	        	
	        	//�õ�ÿ����Ϣ�������Ip�б�
	        	Iterator<Integer> QueueId_iterator = QueueId.iterator();
	        	JSONArray QueueId_List = new JSONArray();
	        	while(QueueId_iterator.hasNext()) {
	        		Integer integer = QueueId_iterator.next();
	        		QueueId_List.add(integer);
	        	}
	        	//System.out.println(QueueId_List);
	        	
	        	//�õ�ÿ����Ϣ�������consumer��Ϣ
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
	        	
	        	//��Ϣ����json����
	        	JSONObject MessageTopic = new JSONObject();
	        	MessageTopic.put("TopicName", QueueName);
	        	MessageTopic.put("QueueId_List", QueueId_List);
	        	MessageTopic.put("Consumer_List", Consumer_List);
	        	
	        	//��Ϣjson����
	    		JSONObject Message = new JSONObject();
	    		Message.put("MessageNum", number);
	    		Message.put("MessageContent", mes);
	    		Message.put("MessageType", type);
	    		Message.put("MessageTopic", MessageTopic);
	    		
	    		Message_List.add(Message);
	        }
	        //����json
    		JSONObject Queue = new JSONObject();
    		Queue.put("QueueName", key);
    		Queue.put("Message_List", Message_List);
    		
    		Queue_List.add(Queue);
	    }      
		//System.out.println(JsonFormatTool.formatJson(Queue_List.toString()));
		return Queue_List;
	}
	
	//������JSON�ļ�
	public static boolean Export(JSONArray list,String FilePath) throws IOException {
		File file = new File(FilePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		String jsonString = JsonFormatUtil.formatJson(list.toString());
		//System.out.println(jsonString);
        
		// ����ʽ������ַ���д���ļ�
        Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        write.write(jsonString);
        write.flush();
        write.close();

	    System.out.println("Done");  
	    
	    return true;
	}
	
	//��ȡJSON�ļ�
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
	
	public static ConcurrentHashMap<String,MyQueue> Extraction(String json) {
		//�����б�
		ConcurrentHashMap<String,MyQueue> QueueList = new ConcurrentHashMap<>();
		//��Ϣ�б�
		MyQueue myQueue = new MyQueue();
		//List<Message> MessageList = new LinkedList<Message>();
		
		
		JSONArray Queue_List = JSONArray.fromObject(json);
		//System.out.println(Queue_List);
		Iterator Queue_List_iterator = Queue_List.iterator();   
		while(Queue_List_iterator.hasNext()) {
			JSONObject Queue = (JSONObject) Queue_List_iterator.next();
			//�õ����е����ƺ���Ϣ�б�
			String queuename = Queue.getString("QueueName");
			JSONArray Message_List = Queue.getJSONArray("Message_List");
			
			System.out.println("�������ƣ�"+queuename);
			System.out.println("�ö��е���Ϣ�б�"+Message_List.toString());
			
			Iterator Message_List_Iterator = Message_List.iterator();
			//����ÿ�������е�ÿ����Ϣ
			while(Message_List_Iterator.hasNext()) {
				JSONObject Message = (JSONObject) Message_List_Iterator.next();
				
				int messagenum =  Message.getInt("MessageNum");
				String messagecontent = Message.getString("MessageContent");
				int messagetype = Message.getInt("MessageType");
				JSONObject messagetopic = new JSONObject();
				messagetopic = Message.getJSONObject("MessageTopic");
				
				//������ռ�õĶ���id
				List<Integer> QueueId = messagetopic.getJSONArray("QueueId_List");
				HashSet<Integer> HQueueId = transformforInteger(QueueId);
				for (Object object : HQueueId) {
					System.out.println("hashset"+object);
				}
				System.out.println("������ռ�õĶ���id"+QueueId);
				//��������
	        	String TopicName = messagetopic.getString("TopicName");
	        	
	        	//�����Ӧ��������
	        	List<IpNode> Consumer = messagetopic.getJSONArray("Consumer_List");
	        	List<IpNode> Consumer_List = new LinkedList<IpNode>();
	        	System.out.println("�����Ӧ��������:"+Consumer.toString());
	        	
	        	Iterator Consumer_Iterator = Consumer.iterator();
	        	while(Consumer_Iterator.hasNext()) {
	        		JSONObject ipNode = (JSONObject) Consumer_Iterator.next();
	        		
	        		String ip = ipNode.getString("ip");
	        		int port = ipNode.getInt("port");
	        		
	        		IpNode ipnode = new IpNode(ip, port);
	        		Consumer_List.add(ipnode);
	        	}
	        	//��Listת����HashSet�Դ���Topic����
	        	HashSet<IpNode> HConsumer = transform(Consumer_List);

	        	//����Topic����
	        	Topic topic = new Topic(TopicName, HQueueId, HConsumer);
	        	
	        	//����Message����
	        	Message message = new Message(messagecontent, topic, messagenum);
	        	message.setType(messagetype);
	        	
	        	myQueue.putAtHeader(message);
			}
			QueueList.put(queuename, myQueue);
		}
		System.out.println("��jsonȡ������ת�ɶ���"+QueueList);
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
	
	public static void main(String[] args) throws IOException {
		//��������1
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
		System.out.println("������תΪjson�����ļ���"+queueList1);
		PersistenceUtil.Export(PersistenceUtil.persistence(queueList1),"D://result.json");
	    //System.out.println(Persistence.Import("F://result.json"));
		ConcurrentHashMap<String,MyQueue> List = PersistenceUtil.Extraction(PersistenceUtil.Import("D://result.json"));
		
		Iterator iterator = List.keySet().iterator();   
		while (iterator.hasNext()) {
			String key = (String) iterator.next(); 
	        //System.out.println("key:"+key);
	        
	        MyQueue myqueue = List.get(key);

	        LinkedList<Message> list = (LinkedList<Message>) myqueue.getReverseAll();
	        
	        //����ÿ����Ϣ���У������Ϣ
	        Iterator<Message> queue_iterator = list.iterator();
	        while(queue_iterator.hasNext()) {
	        	//�õ�ÿ����Ϣ
	        	Message message = queue_iterator.next();
	        	
	        	int number = message.getNum();
	        	String mes = message.getMessage();
	        	int type = message.getType();
	        	Topic topic = message.getTopic();
	        	
	        	//������
	        	System.out.println("��Ϣ��ţ�"+number);
	        	System.out.println("��Ϣ��"+mes);
	        	System.out.println("��Ϣ���ͣ�"+type);
	        	System.out.println("��Ϣ���⣺"+topic);
	        	//System.out.println(topic.getQueue());
	        	
	        	//�õ�ÿ����Ϣ��������Ϣ
	        	String QueueName = topic.getTopicName();
	        	List<Integer> QueueId = topic.getQueue();
	        	List<IpNode> Consumer = topic.getConsumer();
	        	
	        	//������
	        	//System.out.println("����Ϣ���������ڵĶ���Id��"+QueueId);
	        	System.out.println("����Ϣ����������ƣ�"+QueueName);
	        	System.out.println("����Ϣ�������consumer��"+Consumer);
	        	
	        	//�õ�ÿ����Ϣ�������Ip�б�
	        	//Iterator<Integer> QueueId_iterator = QueueId.iterator();
//	        	JSONArray QueueId_List = new JSONArray();
//	        	while(QueueId_iterator.hasNext()) {
//	        		Integer integer = QueueId_iterator.next();
//	        		QueueId_List.add(integer);
//	        		System.out.println("Ip�б�"+integer);
	        	//}
		}
//		Iterator i2 = queueList1.keySet().iterator();
//		
//		boolean flag = true;
//		while (i1.hasNext()&&i2.hasNext()){    
//	        String key1 = (String) i1.next(); 
//	        String key2 = (String) i2.next();
//	        if (key1.equals(key2)) {
//				MyQueue q1 = List.get(key1);
//				MyQueue q2 = queueList1.get(key2);
//				
//				LinkedList<Message> m1 = (LinkedList<Message>) q1.getReverseAll();
//				LinkedList<Message> m2 = (LinkedList<Message>) q2.getReverseAll();
//		        
//		        Iterator<Message> mi1 = m1.iterator();
//		        Iterator<Message> mi2 = m2.iterator();
//		        boolean f = true;
//		        while(mi1.hasNext()&&mi2.hasNext()) {
//		        	f = Compare(mi1.next(), mi2.next());
//		        }
//		        if (!f) {
//					flag = false;
//				}
//	        }
//	        else {
//	        	flag = false;
//	        }
//		}
//		System.out.println("�Ƿ���ͬ��"+flag);
	}
	
//	public static boolean Compare(Message oldm,Message newm) {
//		if(oldm.getNum()==newm.getNum()&&oldm.getType()==newm.getType()&&oldm.getMessage().equals(newm.getMessage())) {
//			Topic oldt = oldm.getTopic();
//			Topic newt = newm.getTopic();
//			if(oldt.getConsumer().equals(newt.getConsumer()) && oldt.getQueueName().equals(newt.getQueueName()) && oldt.getQueue().equals(newt.getQueue()))
//				return true;
//		}
//		return false;
//	}
}
}


