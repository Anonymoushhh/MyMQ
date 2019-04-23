package Test;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import Common.IpNode;
import Common.Message;
import Common.Topic;
import Producer.SyscProducerFactory;
import Utils.SequenceUtil;

public class PressTest {
	
	public static void main(String[] args) {
		
		int time = 100000;//并发线程数
		final CountDownLatch send = new CountDownLatch(time);
	    final CountDownLatch timing = new CountDownLatch(time);
		
		//记录消息是否被成功发送
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		for(int i=0;i<time;i++){
			map.put(i+"", 0);
		}
		//创建Producer
		SequenceUtil Sequence = new SequenceUtil();
		Topic topic = SyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
        topic.addConsumer(new IpNode("127.0.0.1", 8888));
        long startTime=System.currentTimeMillis();   //获取开始时间
        for(int i=0;i<time;i++) {
        	new Thread(){
                public void run() {
	        int num = Sequence.getSequence();
			Message msg = new Message("message"+num,topic, num);
			SyscProducerFactory.setReTry_Time(16);//设置发送失败重试次数
			try {
				send.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String string = SyscProducerFactory.Send(msg, "127.0.0.1", 81);//同步发送
			if(string!=null) {
			    String[] a = string.split(" ");
			    map.put(a[0], map.get(a[0])+1);
			 }
			System.out.println(string);
			timing.countDown();
                }
            }.start();
            send.countDown();
        }
        try {
			timing.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        long endTime=System.currentTimeMillis(); //获取结束时间     
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    //打印未发送成功的消息序号
    for(Entry<String, Integer> entry:map.entrySet())
    	if(entry.getValue()==0)
    		System.out.print(entry.getKey()+" ");
	}

}
