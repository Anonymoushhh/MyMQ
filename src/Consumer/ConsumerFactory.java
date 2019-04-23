package Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import Broker.BrokerResponeProcessor;
import Common.IpNode;
import Common.Message;
import Common.MessageType;
import Common.PullMessage;
import Common.RegisterMessage;
import Utils.Client;
import Utils.DefaultRequestProcessor;
import Utils.ResponseProcessor;
import Utils.Server;

public class ConsumerFactory {
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> map = new ConcurrentHashMap<Integer,ConcurrentLinkedQueue<Message>>();
//在某个端口监听
	private static void waiting(int port) throws IOException {
		DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
		ConsumerResponeProcessor consumerResponeProcessor = new ConsumerResponeProcessor();
		new Thread(){
            public void run() {
            	System.out.println("Consumer在本地端口"+port+"监听...");
            	try {
					new Server(port,defaultRequestProcessor,consumerResponeProcessor);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                };
		}.start();
		
	}
	//向Broker注册
	private static void register(IpNode ipNode1/*目的地址*/,IpNode ipNode2/*本地地址*/){
		System.out.println("正在注册Consumer...");
		Client client;
		try {
			client = new Client(ipNode1.getIp(), ipNode1.getPort());
			RegisterMessage msg = new RegisterMessage(ipNode2, "register", 1);
			if(client.SyscSend(msg)!=null)
				System.out.println("注册成功!");
			else
				System.out.println("注册失败！");
		} catch (IOException e) {
			System.out.println("Connection Refuse.");
		}
		
	}
	public static ConcurrentLinkedQueue<Message> getList(int port){
		return map.get(port);
	}
	public static Message getMessage(int port) {//为空则返回null
		return ConsumerFactory.getList(port).poll();
		
	}
	//拉取消息
	public static void Pull(IpNode ipNode1/*目的地址*/,IpNode ipNode2/*本地地址*/) {
		System.out.println("正在拉取消息...");
		Client client;
		try {
			client = new Client(ipNode1.getIp(), ipNode1.getPort());
			PullMessage msg = new PullMessage(ipNode2, "pull", 1);
			String ack = client.SyscSend(msg);
			if(ack!=null) {
				Message m = ConsumerFactory.getMessage(ipNode2.getPort());
        		if(m!=null) {
        			System.out.println("消息拉取成功！");
        			System.out.println(m.getMessage());
        		}else 
        			System.out.println("消息拉取失败！");
    				
			}
		} catch (IOException e) {
			System.out.println("Connection Refuse.");
		}
	}
	public static void createConsumer(IpNode ipNode1/*Broker地址*/,IpNode ipNode2/*本地地址*/) throws IOException {
		if(map.containsKey(ipNode2.getPort())) {
			System.out.println("端口已被占用!");
			return;
		}
		ConsumerFactory.register(ipNode1,ipNode2);
		ConsumerFactory.waiting(ipNode2.getPort());
		map.put(ipNode2.getPort(), new ConcurrentLinkedQueue<Message>());
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);
		ConsumerFactory.createConsumer(ipNode1, ipNode2);
		new Thread(){
            public void run() {
            	while(true) {
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//            		Message m = ConsumerFactory.getMessage(8888);
//            		if(m!=null) 
//        				System.out.println(m.getMessage());
            		ConsumerFactory.Pull(ipNode1,ipNode2);
            	}
                };
		}.start();
//		ResponseProcessor consumerResponeProcessor = new ConsumerResponeProcessor();
//		System.out.println(consumerResponeProcessor.getClass().getName());
//		ResponseProcessor brokerResponeProcessor = new BrokerResponeProcessor();
//		System.out.println(brokerResponeProcessor.getClass().getName());
	}
}
