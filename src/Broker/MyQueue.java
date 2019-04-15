package Broker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import Common.Message;
import Common.Topic;
import Utils.Client;

public class MyQueue {
	volatile static int count = 1;
	private ConcurrentLinkedDeque<Message> queue;

	public MyQueue() {
		queue = new ConcurrentLinkedDeque<Message>();
	}
	public void putAtHeader(Message value) {
		queue.addFirst(value);
	}
	public Message getAndRemoveTail() {
		return queue.pollLast();
	}	
	public int size() {
		return queue.size();
	}
	public void getAll() {
		Iterator<Message> iterator = queue.iterator();
		while(iterator.hasNext()){
			System.out.print(iterator.next().getTopic().getQueueName()+" ");
		}
		System.out.println();
	}
	//逆序输出
	public List<Message> getReverseAll() {
		Iterator<Message> iterator = queue.iterator();
		LinkedList<Message> list = new LinkedList<Message>();
		while(iterator.hasNext()){
			list.addFirst(iterator.next());
		}
		return list;
	}
	public static void main(String[] args) throws InterruptedException {
		MyQueue queue = new MyQueue();
		//使用线程模拟用户 并发访问
        for (int i = 0; i < 1; i++) {
            new Thread(){
                public void run() {
                	for (int i=0;i<100;i++) {
                		queue.putAtHeader(new Message("", new Topic((count++)+"", i),1));
                	}
                };
            }.start();
        }
        Thread.sleep(1000);
        List<Message> m = queue.getReverseAll();
        Iterator<Message> iterator = m.iterator();
		while(iterator.hasNext()){
			System.out.print(iterator.next().getTopic().getQueueName()+" ");
		}
	}
}
