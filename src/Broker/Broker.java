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
	private static int push_Time = 1000;//pushʱ��Ĭ��һ��һ��
	private static volatile boolean hasSlave = false;
	private static int sync_Time = 1000;//syncʱ��Ĭ��һ��һ��
	private static int reTry_Time = 16;//����ʧ�����Դ���
	private ConcurrentHashMap<String,MyQueue> queueList;
	private Filter filter;//������
	List<IpNode> index;//�����ߵ�ַ
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
		System.out.println("Broker���������ڱ���"+port+"�˿ڼ�����");
		//��ʼ������
		index = new ArrayList<IpNode>();
		//�����ͻ��˼���
		clients = new HashMap<IpNode,Client>();
		//�������п�
		queueList = new ConcurrentHashMap<String,MyQueue>();
		//Ĭ�ϴ���ʮ������
		createQueue(10);
		//����������
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
        //slaveͬ��
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
							System.out.println("Slaveδ����!");
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
      //Ϊ�����ߴ�������
//        for(IpNode ip:this.index) {
//        	Client client = new Client(ip.getIp(),ip.getPort());
//    		clients.put(ip,client);
//        }
	}
	//���ö�������
	public void setQueueList(ConcurrentHashMap<String, MyQueue> queueList) {
		this.queueList = queueList;
	}
	//����ͬ��ʱ��
	public static void setSync_Time(int sync_Time) {
		Broker.sync_Time = sync_Time;
	}
	//����pushʱ����
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
	//���������
	public void addConsumer(IpNode ipNode) throws IOException {
		index.add(ipNode);
		Client client = new Client(ipNode.getIp(),ipNode.getPort());
		clients.put(ipNode,client);
	}
	//Ϊ������������Ϣ
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
							for(i=0;i<reTry_Time;i++) {//ʧ����������
								String ack=null;
								try {
									ack = client.SyscSend(m);
								} catch (IOException e) {
									System.out.println("����ʧ�ܣ���������...");
								}
//								System.out.println("here");
								//System.out.println(ack);
								if(ack!=null)
									break;
							}
//							System.out.println(3);
						if(i>=3) {
							//todo �������Ŷ���
						}
						}else {
							System.out.println("�����߲�����");
							//todo �������Ŷ���
						}					
					}		
				}
	}
	//pushģʽ
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
	//pullģʽ
	public void pullMessage(IpNode ipNode) {
		List<Message> list = new ArrayList<Message>();
		//���Ҷ����������Ϣ���ҵ���ӦipNode����Ϣ
		for(MyQueue queue:queueList.values()) {
			if(queue.getTail()!=null) {
				List<IpNode> l = queue.getTail().getTopic().getConsumer();
				if(l.contains(ipNode)&&l.size()==1)//��Ϣ������ֻ��һ��������Ϣ����
					list.add(queue.getAndRemoveTail());
				else if(l.contains(ipNode)&&l.size()>1) {//��Ϣ�����߲�ֹһ������ɾ����������ߣ���������Ϣ���͸���
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
					for(i=0;i<reTry_Time;i++) {//ʧ������reTry_Time��
						String ack=null;
						try {
							ack = client.SyscSend(m);
							System.out.println(ack);
						} catch (IOException e) {
							System.out.println("����ʧ�ܣ��������Ե�"+(i+1)+"��...");
						}
						if(ack!=null)
							break;
					}
					if(i>=reTry_Time) {
						//todo �������Ŷ���
					}
				}
			} catch (IOException e1) {
				System.out.println("�����߲�����");
				//todo�������Ŷ���
			}
							
		}
	}
	//��������
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
	//ΪTopicѡ�����
	public List<Integer> choiceQueue(int queueNum) {
		if(queueNum>queueList.size())
			createQueue(queueNum-queueList.size());
		return LoadBalancer.balance(queueList, queueNum);
	}
	//����Ϣ��ӵ�ĳ��������
	public synchronized void add(int queueNumber,Message value) {
		MyQueue queue = queueList.get(queueNumber+"");
		queue.putAtHeader(value);
	}
	//���г��ӣ����ж��о�����һ��Ԫ��
	public synchronized List<Message> poll(int num/*������ȡ����*/) {
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
	//����
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
		//����choiceQueue
//		List<Integer> l = broker.choiceQueue(9);
//		for(Integer i:l) 
//			System.out.print(i+" ");
		//����getAll
//		for(MyQueue q:queueList.values()) {
//			q.getAll();
//		}
		//ģ�����Message�����
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
