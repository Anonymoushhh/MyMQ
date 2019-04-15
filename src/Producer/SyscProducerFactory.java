package Producer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.midi.Sequence;

import Common.IpNode;
import Common.Message;
import Common.MessageType;
import Common.Topic;
import Consumer.ConsumerFactory;
import Utils.Client;
import Utils.SequenceUtil;

public class SyscProducerFactory extends AbstractProducerFactory{
	private static ConcurrentHashMap<IpNode, Boolean> requestMap= new ConcurrentHashMap<IpNode, Boolean>();
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
		try {
			client = new Client(ip, port);
			//失败重复，三次放弃
			for(int i=0;i<3;i++) {
				String result = client.SyscSend(msg);
				if(result!=null)
					return result;
				if("".equals(result))
					return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String SendQueueRegister(Message msg,String ip,int port) {//未申请队列返回null
		IpNode ipNode = new IpNode(ip, port);
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		try {
			client = new Client(ip, port);
			//失败重复，三次放弃
			for(int i=0;i<3;i++) {
				String result = client.SyscSend(msg);
				if(result!=null)
					return result;
				if("".equals(result))
					return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Topic RequestQueue(Topic topic,String ip,int port){//输入为一个topic，里面包含请求的队列个数
		Topic t = topic;
		Message m = new Message("RequestQueue",MessageType.REQUEST_QUEUE,t, -1);
//		System.out.println(m.getType());
		String queue = SyscProducerFactory.SendQueueRegister(m, ip, port);
		System.out.println(queue);
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
            	Topic topic = SyscProducerFactory.RequestQueue(new Topic("hh",10), "127.0.0.1", 81);
            	while(true) {
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		for(int i=0;i<10;i++) {
            			Message msg = new Message("hh"+Sequence.getSequence(),topic, Sequence.getSequence());
            			System.out.println(SyscProducerFactory.Send(msg, "127.0.0.1", 81));
            		}
            	}
            		
                };
		}.start();
		
	}
}
