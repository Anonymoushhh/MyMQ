package Producer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Common.Message;
import Common.MessageType;
import Common.Topic;
import Utils.Client;

public class UnidirectionalProducerFactory {

	private static ConcurrentHashMap<IpNode, Boolean> requestMap= new ConcurrentHashMap<IpNode, Boolean>();
	private static int reTry_Time = 16;
	public static void setReTry_Time(int reTry_Time) {
		UnidirectionalProducerFactory.reTry_Time = reTry_Time;
	}
	//返回值为消息号+ACK
	//发送失败返回值为null
	public static void Send(Message msg,String ip,int port) {//未申请队列返回null
		IpNode ipNode = new IpNode(ip, port);
		if(requestMap.get(ipNode)==null) {
			System.out.println("未向Broker申请队列！");
		}
		Client client;
		if(msg.getType()!=MessageType.ONE_WAY&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.ONE_WAY);
		//失败重复，reTry_Time次放弃
		for(int i=0;i<reTry_Time;i++) {
			try {
				client = new Client(ip, port);
				client.SyscSend(msg);
				break;
			} catch (IOException e) {
				System.out.println("生产者消息发送失败，正在重试第"+(i+1)+"次...");
			}
		}
	}
	private static String SendQueueRegister(Message msg,String ip,int port) {//未申请队列返回null
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
		String queue = UnidirectionalProducerFactory.SendQueueRegister(m, ip, port);
		String[] l = queue.substring(7).split(" ");
		for(String i:l)
			topic.addQueueId(Integer.parseInt(i));
		IpNode ipNode = new IpNode(ip, port);
		requestMap.put(ipNode, true);
		return t;
	}

}
