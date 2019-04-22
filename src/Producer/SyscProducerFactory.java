package Producer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import Common.IpNode;
import Common.Message;
import Common.MessageType;
import Common.Topic;
import Utils.Client;
import Utils.SequenceUtil;

public class SyscProducerFactory {
	//该生产者是否已申请队列
	private static ConcurrentHashMap<IpNode, Boolean> requestMap= new ConcurrentHashMap<IpNode, Boolean>();
	//重试次数
	private static int reTry_Time = 16;
	public static void setReTry_Time(int reTry_Time) {
		SyscProducerFactory.reTry_Time = reTry_Time;
	}
	//返回值为消息号+ACK
	//发送失败返回值为null
	public static String Send(Message msg,String ip,int port) {//未申请队列返回null
		IpNode ipNode = new IpNode(ip, port);
		if(requestMap.get(ipNode)==null) {
			System.out.println("未向Broker申请队列！");
			return null;
		}
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		//失败重复，reTry_Time次放弃
		for(int i=0;i<reTry_Time;i++) {
			try {
				client = new Client(ip, port);
				String result = client.SyscSend(msg);
				if(result!=null)
					return result;
				if("".equals(result))
					return null;
			} catch (IOException e) {
				System.out.println("生产者消息发送失败，正在重试第"+(i+1)+"次...");
			}
		}
		return null;
	}
	private static String SendQueueRegister(Message msg,String ip,int port) {//未申请队列成功返回null
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		try {
			client = new Client(ip, port);
			//失败重复，reTry_Time次放弃
			for(int i=0;i<reTry_Time;i++) {
				String result = client.SyscSend(msg);
				if(result!=null) {
					System.out.println("队列申请成功！");
					return result;
				}	
				if("".equals(result))
					return null;
			}
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Broker未上线！");
		}
		return null;
	}
	public static Topic RequestQueue(Topic topic,String ip,int port){//输入为一个topic，里面包含请求的队列个数
		System.out.println("请求向Broker申请队列...");
		Topic t = topic;
		Message m = new Message("RequestQueue",MessageType.REQUEST_QUEUE,t, -1);
//		System.out.println(m.getType());
		String queue = SyscProducerFactory.SendQueueRegister(m, ip, port);
		if(queue==null) {
			System.out.println("申请队列失败！");
			return t;//返回原topic
		}
		String[] l = queue.substring(7).split(" ");
		for(String i:l)
			topic.addQueueId(Integer.parseInt(i));
		IpNode ipNode = new IpNode(ip, port);
		requestMap.put(ipNode, true);
		return t;
	}
	public static void main(String[] args) {
//    	Topic topic = SyscProducerFactory.RequestQueue(new Topic("hh",10), "127.0.0.1", 81);
//		for(Integer i:topic.getQueue())
//			System.out.println(i+" ");
		SequenceUtil Sequence = new SequenceUtil();
		new Thread(){
            public void run() {
            	Topic topic = SyscProducerFactory.RequestQueue(new Topic("hh",1), "127.0.0.1", 81);
            	topic.addConsumer(new IpNode("127.0.0.1", 8888));
//            	while(true) {
            		try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		for(int i=0;i<10;i++) {
            			int num = Sequence.getSequence();
            			Message msg = new Message("hh"+num,topic, num);
            			System.out.println(SyscProducerFactory.Send(msg, "127.0.0.1", 81));
            		}
//            	}
            		
                };
		}.start();
		
	}
}
