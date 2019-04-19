package Test;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Common.Message;
import Common.Topic;
import Producer.SyscProducerFactory;
import Utils.SequenceUtil;

public class Producer {

	public static void main(String[] args) throws InterruptedException {
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		for(int i=0;i<30001;i++){
			map.put(i+"", 0);
		}
		//´´½¨Producer
				SequenceUtil Sequence = new SequenceUtil();
		        Topic topic = SyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
		        topic.addConsumer(new IpNode("127.0.0.1", 8888));
		        topic.addConsumer(new IpNode("127.0.0.1", 8889));
		        for(int i=0;i<30000;i++) {
		        	new Thread(){
		                public void run() {
		                	int num = Sequence.getSequence();
				            Message msg = new Message("message"+num,topic, num);
				            SyscProducerFactory.setReTry_Time(16);
				            String string = SyscProducerFactory.Send(msg, "127.0.0.1", 81);
				            System.out.println(string);
				            if(string!=null) {
				            	String[] a = string.split(" ");
				            	map.put(a[0], map.get(a[0])+1);
				            }
		                };
		            }.start();
		        }
		        Thread.sleep(10000);
		        for(Entry<String, Integer> entry:map.entrySet())
		        	if(entry.getValue()==0)
		        		System.out.print(entry.getKey()+" ");
	}

}
