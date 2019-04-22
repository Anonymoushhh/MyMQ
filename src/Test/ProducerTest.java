package Test;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Common.Message;
import Common.Topic;
import Producer.DelaySyscProducerFactory;
import Producer.SyscProducerFactory;
import Producer.UnidirectionalProducerFactory;
import Utils.SequenceUtil;

public class ProducerTest {

	public static void main(String[] args) throws InterruptedException {
		//记录消息是否被成功发送
//		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
//		for(int i=0;i<30;i++){
//			map.put(i+"", 0);
//		}
		//创建Producer
				SequenceUtil Sequence = new SequenceUtil();
		        
//		        topic.addConsumer(new IpNode("127.0.0.1", 8889));
				
				Topic topic = SyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
		        topic.addConsumer(new IpNode("127.0.0.1", 8888));
		        int num = Sequence.getSequence();
				Message msg = new Message("message"+num,topic, num);
				SyscProducerFactory.setReTry_Time(16);//设置发送失败重试次数
				String string = SyscProducerFactory.Send(msg, "127.0.0.1", 81);//同步发送
				System.out.println(string);
				
				Topic topic2 = DelaySyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
		        topic2.addConsumer(new IpNode("127.0.0.1", 8888));
				int num2 = Sequence.getSequence();//获得全局唯一的序号
				Message msg2 = new Message("message"+num2,topic2, num2);//定义消息，指定消息内容，主题和序号
				DelaySyscProducerFactory.setDelay_Time(1000);//设置延时发送时间
				String string2 = DelaySyscProducerFactory.Send(msg2, "127.0.0.1", 81);//延时发送消息
				System.out.println(string2);
				
				Topic topic3 = UnidirectionalProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
		        topic3.addConsumer(new IpNode("127.0.0.1", 8888));
				int num3 = Sequence.getSequence();//获得全局唯一的序号
				Message msg3 = new Message("message"+num2,topic3, num3);//定义消息，指定消息内容，主题和序号
				UnidirectionalProducerFactory.Send(msg3, "127.0.0.1", 81);
				
//				if(string!=null) {
//				    String[] a = string.split(" ");
//				    map.put(a[0], map.get(a[0])+1);
//				 }
//				for(int i=0;i<1;i++) {
//					new Thread(){
//						  public void run() {
//		                };
//		            }.start();
//	}
		        //打印未发送成功的消息序号
//		        Thread.sleep(10000);
//		        for(Entry<String, Integer> entry:map.entrySet())
//		        	if(entry.getValue()==0)
//		        		System.out.print(entry.getKey()+" ");
	}

}
