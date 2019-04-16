package Test;

import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import Broker.MyQueue;
import Common.IpNode;
import Common.Message;
import Common.Topic;

public class DaoTest {


	public static void main(String[] args) {
		//测试用例1
		ConcurrentHashMap<String,MyQueue> queueList1 = new ConcurrentHashMap<String, MyQueue>();
		int k=0;
		for(int i=1;i<=10;i++) {
			MyQueue queue = new MyQueue();
			for(int j=1;j<=2;j++) {
				Topic t = new Topic("t1", 1);
				IpNode ipnode = new IpNode("127.0.0.1", 8888);
				t.addConsumer(ipnode);
				Message msg = new Message("hh"+i*j, t, k++);
				queue.putAtHeader(msg);				
			}
			queueList1.put((i)+"", queue);
		}
		/* 元素       队列号
		 * hh2 hh1  1
			hh4 hh2 2
			hh6 hh3 3
			hh8 hh4 4
			hh10 hh5 5
			hh12 hh6 6
			hh14 hh7 7
			hh16 hh8 8
			hh18 hh9 9
			hh20 hh10 10
		 * */
//        for(java.util.Map.Entry<String, MyQueue> entry : queueList.entrySet()) {
//        	MyQueue queue = entry.getValue();
//        	queue.getAll();
//        }
		//测试用例2
		ConcurrentHashMap<String,MyQueue> queueList2 = new ConcurrentHashMap<String, MyQueue>();
		int k2=0;
		for(int i=1;i<=10;i++) {
			MyQueue queue = new MyQueue();
			for(int j=1;j<=2;j++) {
				Topic t = new Topic("t1", 1);
				IpNode ipnode = new IpNode("127.0.0.1", 8888);
				t.addConsumer(ipnode);
				Message msg = new Message("hh"+i*j, t, k2++);
				queue.putAtHeader(msg);				
			}
			if(i!=2)
				queueList2.put((i)+"", queue);
		}
		/* 元素       队列号
		 * hh2 hh1  1
			        2
			hh6 hh3 3
			hh8 hh4 4
			hh10 hh5 5
			hh12 hh6 6
			hh14 hh7 7
			hh16 hh8 8
			hh18 hh9 9
			hh20 hh10 10
		 * */
		//测试用例3
		ConcurrentHashMap<String,MyQueue> queueList3 = new ConcurrentHashMap<String, MyQueue>();//空map
		//测试用例4
		//queueList为null
		//若参数为null或map为空，不执行存储json代码，直接返回
	}
}
