package Broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Common.Message;
import Common.MyQueue;
import Utils.Client;
import Utils.Server;

public class Broker{
	Server server;
	Client client;
	private volatile int count = 0;
	private static ConcurrentHashMap<String,MyQueue> queueList;
	private Filter filter;//������
	List<IpNode> index;//�����ߵ�ַ
	public Broker(int port,IpNode ipnode) throws IOException {
		init(port,ipnode);
	}
	public Broker(int port,IpNode ipnode,int queueNum) throws IOException {
		init(port,ipnode);
		createQueue(queueNum);
	}
	private void init(int port,IpNode ipnode) throws IOException {
		//����������
		BrokerRequestProcessor brokerRequestProcessor = new BrokerRequestProcessor();
		BrokerResponeProcessor brokerResponeProcessor = new BrokerResponeProcessor();
		server = new Server(port,brokerRequestProcessor,brokerResponeProcessor);
		//Ϊ�����ߴ�������
		//client = new Client(ipnode.getIp(),ipnode.getPort());
		//�������п�
		queueList = new ConcurrentHashMap<String,MyQueue>();
	}
	private void createQueue(int queueNum) {
		for(int i=0;i<queueNum;i++) {
			MyQueue queue = new MyQueue();
			queueList.put((count++)+"", queue);
		}
	}
	public static void add(int queueNumber,Message value) {
		MyQueue queue = queueList.get(queueNumber+"");
		queue.putAtHeader(value);
	}
	public synchronized List<Message> poll() {
		ArrayList<Message> list = new ArrayList<Message>();
		for(MyQueue queue:queueList.values()) {
			Message message = queue.getAndRemoveTail();
			if(message!=null)
				list.add(message);
		}
		return list;
	}
	public HashMap<IpNode, List<Message>> filter(List<IpNode> index,List<Message> list){
		filter = new Filter(index);
		return filter.filter(list);
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		IpNode ipnode = new IpNode("localhost", 8080);
		Broker broker = new Broker(81, ipnode);
		broker.createQueue(10);
//		Thread.sleep(10000);
//		List<Message> list = broker.poll();
//		for(Message i:list)
//			System.out.println(i.getMessage());
	}
}
