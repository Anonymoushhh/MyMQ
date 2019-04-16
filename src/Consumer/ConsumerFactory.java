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
import Common.RegisterMessage;
import Utils.Client;
import Utils.DefaultRequestProcessor;
import Utils.ResponseProcessor;
import Utils.Server;

public class ConsumerFactory extends AbstractConsumerFactory{
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> map = new ConcurrentHashMap<Integer,ConcurrentLinkedQueue<Message>>();
//在某个端口监听
	private static void waiting(int port) throws IOException {
		DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
		ConsumerResponeProcessor consumerResponeProcessor = new ConsumerResponeProcessor();
		new Thread(){
            public void run() {
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
	private static void register(IpNode ipNode1,IpNode ipNode2) throws IOException {
		Client client = new Client(ipNode1.getIp(), ipNode1.getPort());
		RegisterMessage msg = new RegisterMessage(ipNode2, "topic1", 1);
		System.out.println(client.SyscSend(msg));
	}
	public static ConcurrentLinkedQueue<Message> getList(int port){
		return map.get(port);
	}
	public static Message getMessage(int port) {//为空则返回null
		return ConsumerFactory.getList(port).poll();
		
	}
	public static void createConsumer(IpNode ipNode1/*Broker地址*/,IpNode ipNode2/*本地地址*/,int port/*本地监听端口*/) throws IOException {
		if(map.containsKey(port)) {
			System.out.println("端口已被占用!");
			return;
		}
		ConsumerFactory.register(ipNode1,ipNode2);
		ConsumerFactory.waiting(port);
		map.put(port, new ConcurrentLinkedQueue<Message>());
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);
		ConsumerFactory.createConsumer(ipNode1, ipNode2, 8888);
		new Thread(){
            public void run() {
            	while(true) {
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		Message m = ConsumerFactory.getMessage(8888);
            		if(m!=null) 
        				System.out.println(m.getMessage());
            	}
                };
		}.start();
//		ResponseProcessor consumerResponeProcessor = new ConsumerResponeProcessor();
//		System.out.println(consumerResponeProcessor.getClass().getName());
//		ResponseProcessor brokerResponeProcessor = new BrokerResponeProcessor();
//		System.out.println(brokerResponeProcessor.getClass().getName());
	}
}
