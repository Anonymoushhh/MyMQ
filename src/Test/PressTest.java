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
		
		int time = 100000;//�����߳���
		final CountDownLatch send = new CountDownLatch(time);
	    final CountDownLatch timing = new CountDownLatch(time);
		
		//��¼��Ϣ�Ƿ񱻳ɹ�����
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		for(int i=0;i<time;i++){
			map.put(i+"", 0);
		}
		//����Producer
		SequenceUtil Sequence = new SequenceUtil();
		Topic topic = SyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
        topic.addConsumer(new IpNode("127.0.0.1", 8888));
        long startTime=System.currentTimeMillis();   //��ȡ��ʼʱ��
        for(int i=0;i<time;i++) {
        	new Thread(){
                public void run() {
	        int num = Sequence.getSequence();
			Message msg = new Message("message"+num,topic, num);
			SyscProducerFactory.setReTry_Time(16);//���÷���ʧ�����Դ���
			try {
				send.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String string = SyscProducerFactory.Send(msg, "127.0.0.1", 81);//ͬ������
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
        long endTime=System.currentTimeMillis(); //��ȡ����ʱ��     
        System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms");
    //��ӡδ���ͳɹ�����Ϣ���
    for(Entry<String, Integer> entry:map.entrySet())
    	if(entry.getValue()==0)
    		System.out.print(entry.getKey()+" ");
	}

}
