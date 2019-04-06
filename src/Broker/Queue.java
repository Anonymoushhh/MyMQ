package Broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Common.Message;
import Common.MyQueue;
import Utils.Client;
import Utils.Server;

public class Queue{
	Server server;
	Client client;
	private volatile int count = 0;
	private ConcurrentHashMap<String,MyQueue> queueList;
	public Queue(int port,IpNode ipnode) throws IOException {
		init(port,ipnode);
	}
	public Queue(int port,IpNode ipnode,int queueNum) throws IOException {
		init(port,ipnode);
		createQueue(queueNum);
	}
	private void init(int port,IpNode ipnode) throws IOException {
		//监听生产者
		server = new Server(port);
		//为消费者创建连接
		client = new Client(ipnode.getIp(),ipnode.getPort());
		//创建队列库
		queueList = new ConcurrentHashMap<String,MyQueue>();
	}
	private void createQueue(int queueNum) {
		for(int i=0;i<queueNum;i++) {
			MyQueue queue = new MyQueue();
			queueList.put((count++)+"", queue);
		}
	}
	public void add(int queueNumber,Message value) {
		MyQueue queue = queueList.get(queueNumber+"");
		queue.putAtHeader(value);
	}
	public synchronized List<Message> poll() {
		ArrayList<Message> list = new ArrayList<Message>();
		for(MyQueue queue:queueList.values()) {
			Message message = queue.getAndRemoveTail();
			list.add(message);
		}
		return list;
	}
}
