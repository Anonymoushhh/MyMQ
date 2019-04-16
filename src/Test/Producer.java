package Test;

import Common.IpNode;
import Common.Message;
import Common.Topic;
import Producer.SyscProducerFactory;
import Utils.SequenceUtil;

public class Producer {

	public static void main(String[] args) {
		//´´½¨Producer
				SequenceUtil Sequence = new SequenceUtil();
//				new Thread(){
//		            public void run() {
		            	Topic topic = SyscProducerFactory.RequestQueue(new Topic("hh",1), "127.0.0.1", 81);
		            	topic.addConsumer(new IpNode("127.0.0.1", 8888));
//		            	while(true) {
//		            		try {
//								Thread.sleep(5000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
		            		for(int i=0;i<10000;i++) {
		            			int num = Sequence.getSequence();
		            			Message msg = new Message("hh"+num,topic, num);
		            			SyscProducerFactory.Send(msg, "127.0.0.1", 81);
		            		}
//		            	}
		            		
//		                };
//				}.start();

	}

}
