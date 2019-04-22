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
	//����ֵΪ��Ϣ��+ACK
	//����ʧ�ܷ���ֵΪnull
	public static void Send(Message msg,String ip,int port) {//δ������з���null
		IpNode ipNode = new IpNode(ip, port);
		if(requestMap.get(ipNode)==null) {
			System.out.println("δ��Broker������У�");
		}
		Client client;
		if(msg.getType()!=MessageType.ONE_WAY&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.ONE_WAY);
		//ʧ���ظ���reTry_Time�η���
		for(int i=0;i<reTry_Time;i++) {
			try {
				client = new Client(ip, port);
				client.SyscSend(msg);
				break;
			} catch (IOException e) {
				System.out.println("��������Ϣ����ʧ�ܣ��������Ե�"+(i+1)+"��...");
			}
		}
	}
	private static String SendQueueRegister(Message msg,String ip,int port) {//δ������з���null
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		try {
			client = new Client(ip, port);
			//ʧ���ظ���reTry_Time�η���
			for(int i=0;i<reTry_Time;i++) {
				String result = client.SyscSend(msg);
				if(result!=null) {
					System.out.println("��������ɹ���");
					return result;
				}	
				if("".equals(result))
					return null;
				
			}
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Brokerδ���ߣ�");
		}
		return null;
	}
	public static Topic RequestQueue(Topic topic,String ip,int port){//����Ϊһ��topic�������������Ķ��и���
		System.out.println("������Broker�������...");
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
