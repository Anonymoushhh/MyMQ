package Broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.event.IIOReadWarningListener;

import Common.IpNode;
import Common.Message;
import Common.Topic;
import Consumer.ConsumerFactory;
import Utils.Client;
import Utils.DefaultRequestProcessor;
import Utils.SerializeUtils;
import Utils.Server;

public class Broker{
	
	private static volatile int count = 0;
	private static int push_Time = 1000;//push时间默认一秒一次
	private static volatile boolean hasSlave = false;
	private static int sync_Time = 1000;//sync时间默认一秒一次
	private static int reTry_Time = 16;//发送失败重试次数
	private ConcurrentHashMap<String,MyQueue> queueList;
	private Filter filter;//过滤器
	List<IpNode> index;//消费者地址
	Map<IpNode,Client> clients;
	List<IpNode> slave;
	public Broker(int port/*,List<IpNode> index*/) throws IOException {
		init(port);
	}
	public Broker(int port/*,List<IpNode> index*/,int queueNum) throws IOException {
		init(port);
		createQueue(queueNum);
	}
	public Broker(int port,List<IpNode> slave) throws IOException {
		this.slave = slave;
		hasSlave = true;
		init(port);
	}
	private void init(int port/*,List<IpNode> index*/) throws IOException {
		System.out.println("Broker已启动，在本地"+port+"端口监听。");
		//初始化索引
		index = new ArrayList<IpNode>();
		//创建客户端集合
		clients = new HashMap<IpNode,Client>();
		//创建队列库
		queueList = new ConcurrentHashMap<String,MyQueue>();
		//默认创建十个队列
		createQueue(10);
		//监听生产者
		DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
		BrokerResponeProcessor brokerResponeProcessor = new BrokerResponeProcessor();
		Broker broker = this;
		new Thread(){
            public void run() {
                try {
                	new Server(port,defaultRequestProcessor,brokerResponeProcessor,broker);
				} catch (IOException e) {
					// TODO Auto-generated catch block					
					e.printStackTrace();
				}
            };
        }.start();
        //slave同步
        new Thread(){
          public void run() {
              	while(true) {
              		if(hasSlave) {
              			try {
							Thread.sleep(sync_Time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
              			Synchronizer sync = new Synchronizer(queueList, index);
              			try {
							String s = SerializeUtils.serialize(sync);
							for(IpNode ip:slave) {
								Client client = new Client(ip.getIp(), ip.getPort());
								client.Send(s);
							}
						} catch (IOException e) {
//							e.printStackTrace();
							System.out.println("Slave未上线!");
						}
              		}
              	}
          };
      }.start();
//        new Thread(){
//            public void run() {
//                try {
//                	new Server(15000,defaultRequestProcessor,registerResponeProcessor);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            };
//        }.start();
      //为消费者创建连接
//        for(IpNode ip:this.index) {
//        	Client client = new Client(ip.getIp(),ip.getPort());
//    		clients.put(ip,client);
//        }
	}
	//设置队列内容
	public void setQueueList(ConcurrentHashMap<String, MyQueue> queueList) {
		this.queueList = queueList;
	}
	//设置同步时间
	public static void setSync_Time(int sync_Time) {
		Broker.sync_Time = sync_Time;
	}
	//设置push时间间隔
	public void setPushTime(int time) {
		push_Time = time;
	}
	public void setReTry_Time(int reTry_Time) {
		Broker.reTry_Time = reTry_Time;
	}
	public void getAll() {
		for(Entry<String, MyQueue> s:queueList.entrySet()) {
			System.out.print(s.getKey()+" ");
			s.getValue().getAll();
		}
	}
	//添加消费者
	public void addConsumer(IpNode ipNode) throws IOException {
		index.add(ipNode);
		Client client = new Client(ipNode.getIp(),ipNode.getPort());
		clients.put(ipNode,client);
	}
	//为消费者推送消息
	private void pushMessage() {
		HashMap<IpNode, List<Message>> map = filter(index,poll(1));
//		IpNode ipnode = new IpNode("127.0.0.1", 8888);
//		for(Message m:map.get(ipnode))
//			System.out.println(m.getType());
//		System.out.println("here");
		for(IpNode ip:map.keySet())
				{
					List<Message> message = map.get(ip);
					for(Message m:message) {
//						System.out.println(m.getType());
						Client client = clients.get(ip);
//						System.out.println(1);
						if(client!=null) {
							int i=0;
//							System.out.println(2);
							for(i=0;i<reTry_Time;i++) {//失败重试三次
								String ack=null;
								try {
									ack = client.SyscSend(m);
								} catch (IOException e) {
									System.out.println("发送失败！正在重试...");
								}
//								System.out.println("here");
								//System.out.println(ack);
								if(ack!=null)
									break;
							}
//							System.out.println(3);
						if(i>=3) {
							//todo 进入死信队列
						}
						}else {
							System.out.println("消费者不存在");
							//todo 进入死信队列
						}					
					}		
				}
	}
	//push模式
	public void push() {
		new Thread(){
	        public void run() {
	        	while(true) {
	        		try {
	    				Thread.sleep(push_Time);
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	        		pushMessage();
	        	}
	    		
	        };
	}.start();
	}
	//pull模式
	public void pullMessage(IpNode ipNode) {
		List<Message> list = new ArrayList<Message>();
		//查找队列最外层消息，找到对应ipNode的消息
		for(MyQueue queue:queueList.values()) {
			if(queue.getTail()!=null) {
				List<IpNode> l = queue.getTail().getTopic().getConsumer();
				if(l.contains(ipNode)&&l.size()==1)//消息消费者只有一个，该消息出队
					list.add(queue.getAndRemoveTail());
				else if(l.contains(ipNode)&&l.size()>1) {//消息消费者不止一个，则删除这个消费者，并将该消息推送给它
					Message m = queue.getTail();
					m.getTopic().deleteConsumer(ipNode);
					list.add(m);
				}
			}
		}
		for(Message m:list) {
			try {
				Client client = new Client(ipNode.getIp(), ipNode.getPort());
				if(client!=null) {
					int i=0;
					for(i=0;i<reTry_Time;i++) {//失败重试reTry_Time次
						String ack=null;
						try {
							ack = client.SyscSend(m);
							System.out.println(ack);
						} catch (IOException e) {
							System.out.println("发送失败！正在重试第"+(i+1)+"次...");
						}
						if(ack!=null)
							break;
					}
					if(i>=reTry_Time) {
						//todo 进入死信队列
					}
				}
			} catch (IOException e1) {
				System.out.println("消费者不存在");
				//todo进入死信队列
			}
							
		}
	}
	//创建队列
	private synchronized void createQueue(int queueNum) {
		int k=0;
		for(int i=1;i<=queueNum;i++) {
			MyQueue queue = new MyQueue();
//			if(slave!=null) {
//				for(int j=1;j<=2;j++) {
//					Topic t = new Topic("t1", 1);
//					IpNode ipnode = new IpNode("127.0.0.1", 8888);
//					t.addConsumer(ipnode);
//					Message msg = new Message("hh", t, k++);
//					queue.putAtHeader(msg);				
//				}
//			}
			queueList.put((count++)+"", queue);
		}
	}
	//为Topic选择队列
	public List<Integer> choiceQueue(int queueNum) {
		if(queueNum>queueList.size())
			createQueue(queueNum-queueList.size());
		return LoadBalancer.balance(queueList, queueNum);
	}
	//将消息添加到某个队列中
	public synchronized void add(int queueNumber,Message value) {
		MyQueue queue = queueList.get(queueNumber+"");
		queue.putAtHeader(value);
	}
	//队列出队，所有队列均出队一个元素
	public synchronized List<Message> poll(int num/*设置拉取轮数*/) {
		ArrayList<Message> list = new ArrayList<Message>();
		for(int i=0;i<num;i++) {
			for(MyQueue queue:queueList.values()) {
				if(queue.getTail()!=null) {
					List<IpNode> l = queue.getTail().getTopic().getConsumer();
					for(IpNode j:l) {
						if(!index.contains(j))
							return list;
					}
				}
				Message message = queue.getAndRemoveTail();
				if(message!=null)
					list.add(message);
			}
		}
		return list;
	}
	//过滤
	public HashMap<IpNode, List<Message>> filter(List<IpNode> index,List<Message> list){
		filter = new Filter(index);
		return filter.filter(list);
	}
	public static void main(String[] args) throws IOException, InterruptedException {
//		IpNode ipnode = new IpNode("127.0.0.1", 8088);
//		List<IpNode> list = new ArrayList<IpNode>();
//		list.add(ipnode);
//		Broker broker;
//			broker = new Broker(81);
//			broker.push();
//		broker.createQueue(10);
//		Topic t = new Topic("t1", 1);
//		Message msg = new Message("hh", t, 0);
//		Client client = broker.clients.get(ipnode);
//		System.out.println(client.SyscSend(msg));
//		Thread.sleep(5000);
//		while(true) {
//			broker.push();
//			Thread.sleep(2000);
//		}

//		broker.push();
//		broker.push();
		//测试choiceQueue
//		List<Integer> l = broker.choiceQueue(9);
//		for(Integer i:l) 
//			System.out.print(i+" ");
		//测试getAll
//		for(MyQueue q:queueList.values()) {
//			q.getAll();
//		}
		//模拟接受Message并添加
//		Topic t = new Topic("t1", 1);
//		Message msg = new Message("hh", t, 1);
//		Broker.add(0, msg);
//		System.out.println(2);
//		Thread.sleep(10000);
//		List<Message> list = broker.poll();
//		System.out.println(list.size());
//		for(Message i:list)
//			System.out.println(i.getMessage());
	}
}
