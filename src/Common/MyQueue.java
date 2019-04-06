package Common;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

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
	public void getAll() {
		Iterator<Message> iterator = queue.iterator();
		while(iterator.hasNext()){
			System.out.print(iterator.next().getTopic().getQueueName()+" ");
		}
		System.out.println();
	}
	public static void main(String[] args) throws InterruptedException {
		MyQueue queue = new MyQueue();
		//使用线程模拟用户 并发访问
        for (int i = 0; i < 1; i++) {
            new Thread(){
                public void run() {
                	for (int i=0;i<100;i++) {
                		queue.putAtHeader(new Message("", new Topic(((count++)+""),1)));
                		if(Math.random()>0.5)
                			queue.getAndRemoveTail();
                	}
                };
            }.start();
        }
        Thread.sleep(1000);
        queue.getAll();
	}
}
