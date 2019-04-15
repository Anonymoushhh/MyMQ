package Producer;

import java.io.IOException;
import java.util.List;

import Common.Message;
import Common.MessageType;
import Common.Topic;
import Utils.Client;

public class SyscProducerFactory extends AbstractProducerFactory{
	//����ֵΪ��Ϣ��+ACK
	//����ʧ�ܷ���ֵΪnull
	public static String Send(Message msg,String ip,int port) {
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		try {
			client = new Client(ip, port);
			//ʧ���ظ������η���
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
	public static Topic RequestQueue(Topic topic,String ip,int port){//����Ϊһ��topic�������������Ķ��и���
		Topic t = topic;
		Message m = new Message("RequestQueue",MessageType.REQUEST_QUEUE,t, -1);
//		System.out.println(m.getType());
		String queue = SyscProducerFactory.Send(m, ip, port);
//		System.out.println(queue.substring(7));
		String[] l = queue.substring(7).split(" ");
		for(String i:l)
			topic.addQueueId(Integer.parseInt(i));
		return t;
	}
	public static void main(String[] args) {
		Topic topic = SyscProducerFactory.RequestQueue(new Topic("hh",10), "127.0.0.1", 81);
		for(Integer i:topic.getQueue())
			System.out.println(i+" ");
	}
}
