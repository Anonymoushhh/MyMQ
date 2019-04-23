package Broker;

import java.io.File;
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
import Utils.PersistenceUtil;
import Utils.SerializeUtil;
import Utils.Server;

public class Broker{
	
	private volatile int count = 0;//��¼���б��
	private int push_Time = 1000;//pushʱ��Ĭ��һ��һ��
	private boolean hasSlave = false;//�Ƿ��б��ݽڵ�
	private boolean hasQueueNum = false;//�Ƿ�ָ��������
	private boolean startPersistence = false;//�Ƿ����־û�
	private int sync_Time = 1000;//syncʱ��Ĭ��һ��һ��
	private int reTry_Time = 16;//����ʧ�����Դ���
	private int store_Time = 1000;//ˢ��ʱ����
	private ConcurrentHashMap<String,MyQueue> queueList;//�����б�
	private Filter filter;//������
	List<IpNode> index;//�����ߵ�ַ
	Map<IpNode,Client> clients;
	List<IpNode> slave;
	public Broker(int port/*,List<IpNode> index*/) throws IOException {
		init(port);
	}
	public Broker(int port/*,List<IpNode> index*/,int queueNum) throws IOException {
		hasQueueNum = true;
		init(port);
		createQueue(queueNum);
	}
	public Broker(int port,List<IpNode> slave) throws IOException {
		this.slave = slave;
		hasSlave = true;
		init(port);
	}
	public Broker(int port,int queueNum,List<IpNode> slave) throws IOException {
		this.slave = slave;
		hasSlave = true;
		hasQueueNum = true;
		init(port);
		createQueue(queueNum);
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
		if(!hasQueueNum)
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
							String s = SerializeUtil.serialize(sync);
							for(IpNode ip:slave) {
								Client client = new Client(ip.getIp(), ip.getPort());
								client.Send(s);
							}
						} catch (IOException e) {
							System.out.println("Slaveδ����!");
						}
              		}
              	}
          };
      }.start();
      //�־û�
        new Thread(){
            public void run() {
            	while(true) {
            		if(startPersistence) {
            			try {
							Thread.sleep(store_Time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
            			try {
            				String path = PersistenceUtil.class.getResource("").getPath().toString().substring(1);
            				File file = new File(path);
            				String newPath1 = file.getParentFile().getParent()+"\\QueueList.json";
            				String newPath2 = file.getParentFile().getParent()+"\\ConsumerAddress.json";
                        	PersistenceUtil.Export(PersistenceUtil.persistenceQueue(broker.queueList),newPath1);
                        	PersistenceUtil.Export(PersistenceUtil.persistenceConsumer(broker.index),newPath2);
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
            		}
            	}
                
            };
        }.start();
	}
	//�Ƿ����־û�
	public void setStartPersistence(boolean startPersistence) {
		this.startPersistence = startPersistence;
	}
	//���ö�������
	public void setQueueList(ConcurrentHashMap<String, MyQueue> queueList) {
		this.queueList = queueList;
	}
	//����ˢ��ʱ��
	public void setStore_Time(int store_Time) {
		this.store_Time = store_Time;
	}
	//����ͬ��ʱ��
	public void setSync_Time(int sync_Time) {
		this.sync_Time = sync_Time;
	}
	//����pushʱ����
	public void setPush_Time(int time) {
		push_Time = time;
	}
	//��������ʱ����
	public void setReTry_Time(int reTry_Time) {
		this.reTry_Time = reTry_Time;
	}
	public void getAll() {
		for(Entry<String, MyQueue> s:queueList.entrySet()) {
			System.out.print(s.getKey()+" ");
			s.getValue().getAll();
		}
	}
	//�ָ�Broker
	public void recover() {
		String path = PersistenceUtil.class.getResource("").getPath().toString().substring(1);
		File file = new File(path);
		String newPath1 = file.getParentFile().getParent()+"\\QueueList.json";
		String newPath2 = file.getParentFile().getParent()+"\\ConsumerAddress.json";
		ConcurrentHashMap<String,MyQueue> queueList = PersistenceUtil.Extraction(PersistenceUtil.Import(newPath1));
		this.setQueueList(queueList);
		List<IpNode> address= PersistenceUtil.ExtractionConsumer(PersistenceUtil.Import(newPath2));
		for(IpNode ipNode:address)
			addConsumer(ipNode);

	}
	//���������
	public void addConsumer(IpNode ipNode) {
		index.add(ipNode);
		Client client;
		try {
			client = new Client(ipNode.getIp(),ipNode.getPort());
			clients.put(ipNode,client);
		} catch (IOException e) {
			System.out.println("Connection Refuse.");
		}
		
	}
	//Ϊ������������Ϣ
	private void pushMessage() {
		HashMap<IpNode, List<Message>> map = filter(index,poll(1));
		for(IpNode ip:map.keySet())
				{
					List<Message> message = map.get(ip);
					for(Message m:message) {
						Client client = clients.get(ip);
						if(client!=null) {
							int i=0;
							for(i=0;i<reTry_Time;i++) {//ʧ������16��
								String ack=null;
								try {
									ack = client.SyscSend(m);
								} catch (IOException e) {
									System.out.println("����ʧ�ܣ���������...");
								}
								if(ack!=null)
									break;
							}
						if(i>=reTry_Time) {
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
	private synchronized List<Message> poll(int num/*������ȡ����*/) {
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
	private HashMap<IpNode, List<Message>> filter(List<IpNode> index,List<Message> list){
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
