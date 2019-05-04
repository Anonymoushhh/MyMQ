#MyMQ文档与使用指南
###MyMQ简介
&emsp;&emsp;MyMQ是一个简单版的消息队列，它的架构主要分为三部分：Producer，Broker和Consumer。
&emsp;&emsp;生产者支持同步发送消息和发送单向消息，生产者发送消息时需先通过一个消息主题向Broker申请队列，Broker根据自己的负载情况返回给生产者可用队列号，生产者将队列号添加到topic中，并用该消息主题发送消息；
&emsp;&emsp;Broker中有许多队列，每个队列中消息顺序一定，队列对消息主题Topic可以是多对多，一对多，多对一的关系，具体如何使用由使用者决定。Broker支持负载均衡和消息过滤功能，对消费者提供Push和Pull两种模式。Broker还实现了主从同步（Slave节点）和队列持久化存储与恢复来保证消息的可靠性。若消息由于网络原因发送失败时会重试，默认为16次，发送成功（返回ACK）或返回失败消息后才会发送下一条消息，以此来保证消息的有序性；
&emsp;&emsp;消费者可以同步获取消息，延时获取消息，支持Push和Pull两种模式。
&emsp;&emsp;Producer，Broker和Consumer三者支持单机和分布式环境，通过NIO的Socket通信。
&emsp;&emsp;Producer，Broker和Consumer三者均支持横向扩展，增加新的机器对旧的服务没有任何影响，保证了高可用性。

###MyMQ架构

+ Broker
+ Broker.java
+ BrokerResponseProcessor.java
+ Filter.java
+ LoadBalancer.java
+ MyQueue.java
+ Slave.java
+ SlaveResponseProcessor.java
+ Synchronizer.java

&emsp;&emsp;Broker包的作用主要是创建Broker实例对象，以及提供主从同步，负载均衡，消息过滤服务。

+ Common
+ IpNode.java
+ Message.java
+ MessageType.java
+ PullMessage.java
+ RegisterMessage.java
+ Topic.java

&emsp;&emsp;Common包定义了一些通用的类，如消息类，地址类等。

+ Consumer
+ ConsumerFactory.java
+ ConsumerResponseProcessor.java

&emsp;&emsp;消费者包定义了消费者工厂，可通过工厂方法添加消费者。

+ Producer
+ DelaySyscProducerFactory.java
+ SyscProducerFactory.java
+ UnidirectionalProducerFactory.java

&emsp;&emsp;生产者包定义了生产者工厂，支持同步生产者工厂，延时生产者工厂和单向生产者工厂。

+ Test
+ ConsumerTest.java
+ DaoTest.java
+ BrokerTest.java
+ ProducerTest.java
+ PressTest.java

&emsp;&emsp;测试包，里面包含了MyMQ的基本使用方法。

+ Utils
+ Client.java
+ DefaultRequestProcessor.java
+ DefaultResponseProcessor.java
+ JsonFormatUtil.java
+ PersistenceUtil.java
+ MessageUtil.java
+ RequestProcessor.java
+ ResponseProcessor.java
+ SequenceUtil.java
+ SerializeUtil.java
+ Server.java

&emsp;&emsp;工具包，定义了一些通用的工具类。

###MyMQ使用指南
####Broker.Broker
&emsp;&emsp;Broker为消息队列服务器节点，提供的服务有：消息存储，消息分发（Push模式与Pull模式），失败重试机制，消息过滤，负载均衡，死信队列，主从备份，持久化存储（同步或异步刷盘）与冗机恢复，横向扩展等。

Method|Description
---|:--:
public Broker(int port)|构造方法，让Broker在某个port监听
public Broker(int port,int queueNum)|构造方法，显示指定初始队列数量
public Broker(int port,List<IpNode> slave)|构造方法，同时创建备份broker
public Broker(int port,int queueNum,List<IpNode> slave)|构造方法
private void init(int port)|初始化Broker，包括初始化成员变量，默认创建十个生产者队列，创建Server对象在port监听，创建一个线程与slave通信
public void setStartPersistence(boolean startPersistence)|开启或关闭持久化功能
public void setQueueList(ConcurrentHashMap<String, MyQueue> queueList)|设置队列内容，用于slave同步
public void setStore_Time(int store_Time)|设置刷盘时间
public static void setSync_Time(int sync_Time)|设置同步时间，默认1s
public void setPush_Time(int time)|设置Push时间间隔默认1s
public void setReTry_Time(int reTry_Time) |设置重试次数，默认为16
public void getAll()|打印队列内容
public void recover()|恢复Broker
public void addConsumer(IpNode ipNode)|添加消费者
private void pushMessage()|为消费者推送消息，push方法调用
public void pullMessage(IpNode ipNode)|pull模式
private synchronized void createQueue(int queueNum)|创建队列
public List<Integer> choiceQueue(int queueNum)|当生产者请求队列时，根据负载均衡选择压力最小的队列
public synchronized void add(int queueNumber,Message value)|将消息添加到某个队列中
public synchronized List<Message> poll(int num)|每个队列出队num个元素
public HashMap<IpNode, List<Message>> filter(List<IpNode> index,List<Message> list)|根据消费者信息过滤消息
####Broker.BrokerResponseProcessor
&emsp;&emsp;该类实现了ResponseProcessor接口，为Broker制定了特殊的消息响应机制。

Method|Description
---|:--:
public void processorRespone(final SelectionKey key,Broker broker)|根据不同的消息类型做出不同的反应
private void addToBroker(Message msg,Broker broker)|将消息添加到Broker
####Broker.Filter
&emsp;&emsp;消息过滤器，将消息按照消费者地址分类。

Method|Description
---|:--:
public Filter(List<IpNode> index)|构造方法，输入为全部消费者地址列表
public HashMap<IpNode, List<Message>> filter(List<Message> list)|将Message按照地址分类
####Broker.LoadBalancer
&emsp;&emsp;负载均衡器，用于为生产者选择一个合适的消息队列。

Method|Description
---|:--:
public static synchronized List<Integer> balance(ConcurrentHashMap<String,MyQueue> queueList,int queueNum)|找到前queueNum小的队列号
####Broker.MyQueue
&emsp;&emsp;消息队列类，保证了消息的顺序性。

Method|Description
---|:--:
public MyQueue()|构造方法，初始化队列
public void putAtHeader(Message value)|在队列头插入消息
public Message getAndRemoveTail()|返回并移除队列尾元素
public Message getTail()|返回队尾元素
public int size()|返回队列大小
public void getAll()|打印队列元素
public List<Message> getReverseAll()|逆序列
####Broker.Slave
&emsp;&emsp;备份节点类，用于Slave的同步或异步备份。

Method|Description
---|:--:
public Slave(int port1,int port2)|构造方法，port1为slave监听端口，port2为slaveBroker监听端口
public void Sync(Synchronizer synchronizer)|同步函数，输入为同步器
####Broker.SlaveResponseProcessor
&emsp;&emsp;用于指定备份节点的特殊消息响应机制。

Method|Description
---|:--:
public void processorRespone(final SelectionKey key,Slave slave)|根据Slave服务器的消息类型做出不同反应
####Broker.Synchronizer
&emsp;&emsp;同步器，用于Broker主从节点的同步。

Method|Description
---|:--:
public Synchronizer(ConcurrentHashMap<String, MyQueue> queueList, List<IpNode> index)|构造方法，输入为队列列表和消费者地址集合
public ConcurrentHashMap<String,MyQueue> getQueueList()|返回队列集合
public List<IpNode> getIndex()|返回消费者地址
####Common.IpNode
&emsp;&emsp;定义一个网络地址。

Method|Description
---|:--:
public IpNode(String ip, int port)|构造方法，定义一个网络地址
public String getIp()|返回ip
public int getPort()|返回端口
public void setIp(String ip)|设置ip
public void setPort(int port)|设置端口
####Common.Message
&emsp;&emsp;定义了传输的消息结构。

Method|Description
---|:--:
public Message(String s,Topic topic,int num)|构造方法，输入为消息内容，消息主题，消息序号
public Message(String s,int type,int num)|构造方法，输入为消息内容，消息类型，消息序号
public Message(String s,int type,Topic topic,int num)|构造方法，输入为消息内容，消息类型，消息主题，消息序号
public String getMessage()|返回消息内容
public int getType()|返回消息类型
public void setType(int type)|设置消息类型，若类型不存在，设置为默认值1
public Topic getTopic()|返回消息主题
public void setTopic(Topic topic)|设置消息主题
public int getNum()|返回消息序号
public void setNum(int num)|设置消息序号
####Common.MessageType
&emsp;&emsp;定义了消息类型。

Method|Description
---|:--:
private static Set<Integer> getSet()|返回消息类型集合
public static boolean contains(Integer i)|判断类型是否合法
####Common.PullMessage
&emsp;&emsp;一种特殊的消息，用于消费者向Broker拉取消息。

Method|Description
---|:--:
public PullMessage(IpNode ipNode,String message,int num)|构造方法，构造一个请求拉取消息的消息
public IpNode getIpNode()|获得地址信息
public int getNum()|获得消息序号
public int getType()|获得消息类型
public String getMessage()|获得消息内容
####Common.RegisterMessage
&emsp;&emsp;一种特殊的消息，用与消费者向Broker注册。

Method|Description
---|:--:
public RegisterMessage(IpNode ipNode,String message,int num)|构造方法，构造一个Consumer注册消息
public IpNode getIpNode()|返回地址信息
public int getNum()|返回消息序号
public int getType()|返回消息类型
public String getMessage()|返回消息内容
####Common.Topic
&emsp;&emsp;消息主题。

Method|Description
---|:--:
public Topic(String s,int queueNum)|构造方法，输入为主题内容，请求队列数
public Topic(String s,HashSet<Integer> queueId,HashSet<IpNode> consumer_address)|构造方法，输入为主题内容，请求队列集合，消费者集合
private List<IpNode> transform(HashSet<IpNode> set)|HashSet元素转换为线性表
private List<Integer> transformforInteger(HashSet<Integer> set)|同上
public String getTopicName()|获得主题名字
public List<Integer> getQueue()|获得队列编号
public List<IpNode> getConsumer()|获得消费者列表
public void addConsumer(IpNode ipnode)|添加消费者
public void deleteConsumer(IpNode ipnode)|删除消费者
public void addQueueId(int i)|添加队列
public int getQueueNum()|获得请求队列数
####Consumer.ConsumerFactory
&emsp;&emsp;消费者工厂类，用于创建消费者。

Method|Description
---|:--:
private static void register(IpNode ipNode1,IpNode ipNode2)|消费者向Broker注册，输入为目的地址，本地地址
private static void waiting(int port)|消费者在某个端口监听消息
public static void createConsumer(IpNode ipNode1,IpNode ipNode2)|向Broker申请创建消费者
public static ConcurrentLinkedQueue<Message> getList(int port)|返回某个在某个端口监听的消息队列
public static Message getMessage(int port)|返回在某个端口的消息
public static void Pull(IpNode ipNode1,IpNode ipNode2)|请求拉取消息
####Consumer.ConsumerResponeProcessor
&emsp;&emsp;为消费者指定特殊的消息响应机制。

Method|Description
---|:--:
public void processorRespone(final SelectionKey key,int port)|消费者对消息的监听处理方法
####Producer.SyscProducerFactory
&emsp;&emsp;同步生产者工厂。

Method|Description
---|:--:
public static void setReTry_Time(int reTry_Time)|设置重试次数
private static String SendQueueRegister(Message msg,String ip,int port)|发送队列注册消息，失败返回null，成功返回 RequestQueue ACK
public static Topic RequestQueue(Topic topic,String ip,int port)|请求申请队列，输入为一个topic和目的地址，里面包含请求的队列个数
public static String Send(Message msg,String ip,int port)|发送消息
####Producer.DelaySyscProducerFactory
&emsp;&emsp;延时生产者工厂。

Method|Description
---|:--:
public static void setDelay_Time(int delay_Time)|设置延时发送时间，其余方法同上
####Producer.UndirectionalProducerFactory
&emsp;&emsp;单向消息生产者工厂。
&emsp;&emsp;API同SyscProducerFactory。
####Utils.Client
&emsp;&emsp;NIO通信模型客户端类，用于发送消息和接受回复。

Method|Description
---|:--:
public Client(String ip,int port)|构造方法，输入为目标地址
private void init(String ip,int port)|Client初始化
public String SyscSend(String msg)|同步发送字符串消息
public void Send(String msg)|单向发送字符串
public String SyscSend(Message msg)|同步发送消息对象
public void Send(Message msg)|单向发送消息对象
public String receive()|接受消息
####Utils.DefaultRequestProcessor
&emsp;&emsp;默认的请求接收响应类。

Method|Description
---|:--:
public void processorRequest(final SelectionKey key,Server server)|默认的请求处理方法
####Utils.DefaultResponeProcessor
&emsp;&emsp;默认的请求回复响应类。

Method|Description
---|:--:
public void processorRespone(final SelectionKey key)|默认的请求响应方法
####Utils.RequestProcessor接口
&emsp;&emsp;请求接收响应接口。

Method|Description
---|:--:
public void processorRequest(final SelectionKey key,Server server)|消息处理方法
####Utils.ResponseProcessor接口
&emsp;&emsp;请求回复响应接口。

Method|Description
---|:--:
default void processorRespone(final SelectionKey key)|默认空实现，为实现接口的类服务
default void processorRespone(final SelectionKey key,Broker broker)|默认空实现，为实现接口的类服务
default void processorRespone(final SelectionKey key,int port)|默认空实现，为实现接口的类服务
default void processorRespone(final SelectionKey key,Slave slave)|默认空实现，为实现接口的类服务
####Utils.SequenceUtil
&emsp;&emsp;生成唯一序列号的工具类（单机唯一）。

Method|Description
---|:--:
public synchronized int getSequence()|返回一个唯一的序列化（单机环境下唯一）
####Utils.SerializeUtil
&emsp;&emsp;序列化工具类。

Method|Description
---|:--:
public static String serialize(Object obj)|对象序列化为字符串
public static Object serializeToObject(String str)|字符串反序列化为对象
####Utils.Server
&emsp;&emsp;NIO通信模型服务器类，在某个端口上监听消息。

Method|Description
---|:--:
public Server(int port,RequestProcessor requestProcessor,ResponseProcessor responeProcessor)|构造方法，创建一个服务端对象
public Server(int port,RequestProcessor requestProcessor,ResponseProcessor responeProcessor,Broker broker)|构造方法，创建一个服务端对象，并为某个Broker服务
public Server(int port,RequestProcessor requestProcessor,ResponseProcessor responeProcessor,Slave slave)|构造方法，创建一个服务端对象，并为某个Slave服务
public void addWriteQueen(SelectionKey key)|添加SelectionKey到队列
void init(int port)|在某个端口上创建Server服务，初始化Server
void start(int port)|在某个端口上开始监听

###使用示例

####Producer
```java
SequenceUtil Sequence = new SequenceUtil();//新建一个序列号工具类实例
//创建一个消息主题Topic（包含Topic名称和请求队列个数）向Broker请求分配队列，

//同步消息示例
//返回值为一个新的Topic，里面包含了分配的队列编号
Topic topic = SyscProducerFactory.RequestQueue(new Topic("topic",1)/*请求队列的Topic*/, "127.0.0.1", 81);
//为消息主题添加消费者地址
topic.addConsumer(new IpNode("127.0.0.1", 8888));
int num = Sequence.getSequence();//获得全局唯一的序号
Message msg = new Message("message"+num,topic, num);//定义消息，指定消息内容，主题和序号
SyscProducerFactory.setReTry_Time(16);//设置发送失败重试次数
String string = SyscProducerFactory.Send(msg, "127.0.0.1", 81);//同步发送

//延时消息示例
Topic topic2 = DelaySyscProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
topic.addConsumer(new IpNode("127.0.0.1", 8888));
int num2 = Sequence.getSequence();//获得全局唯一的序号
Message msg2 = new Message("message"+num2,topic2, num2);//定义消息，指定消息内容，主题和序号
DelaySyscProducerFactory.setDelay_Time(1000);//设置延时发送时间
String string2 = DelaySyscProducerFactory.Send(msg2, "127.0.0.1", 81);//延时发送消息
System.out.println(string2);

//单向消息示例
Topic topic3 = UnidirectionalProducerFactory.RequestQueue(new Topic("topic",1), "127.0.0.1", 81);
topic.addConsumer(new IpNode("127.0.0.1", 8888));
int num3 = Sequence.getSequence();//获得全局唯一的序号
Message msg3 = new Message("message"+num3,topic3, num3);//定义消息，指定消息内容，主题和序号
UnidirectionalProducerFactory.Send(msg3, "127.0.0.1", 81);
```
####Broker
```java
//创建Broker(主从复制，push模式)
try {
		IpNode slaveIpNode = new IpNode("127.0.0.1", 83);//备份服务器地址
		List<IpNode> list = new ArrayList<IpNode>();
		list.add(slaveIpNode);
		Broker broker = new Broker(81,list);创建Broker节点，在81端口监听
		//push模式
		broker.setPush_Time(1000);//设置Broker推送时间
		broker.push();//创建推送服务
		} catch (IOException e) {
			e.printStackTrace();
	}
```
```
//创建Broker(非主从复制，push模式)
		//Broker(非主从复制)
				try {
					Broker broker = new Broker(81);
					broker.setPush_Time(1000);
					broker.setReTry_Time(16);
					broker.setSync_Time(1000);
					broker.setStore_Time(1000);
					broker.setStartPersistence(true);
					broker.push();
				} catch (IOException e) {
					e.printStackTrace();
				}
```
```
//创建Broker(非主从复制,pull模式)
	try {
		Broker broker = new Broker(81);
		} catch (IOException e) {
			e.printStackTrace();
	}
```
Consumer
```
//创建Consumer（Push模式）
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);//消费者地址
		try {
			ConsumerFactory.createConsumer(ipNode1, ipNode2);
		} catch (IOException e1) {
			System.out.println("Broker未上线！");
		}
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Message m1 = ConsumerFactory.getMessage(8888);
    		if(m1!=null) 
				System.out.println("消费者"+ipNode2.getIp()+ipNode2.getPort()+"收到消息："+m1.getMessage());	
		}
```
```
//创建Consumer（Pull模式）
		IpNode ipNode3 = new IpNode("127.0.0.1", 81);
		IpNode ipNode4 = new IpNode("127.0.0.1", 8888);
    	try {
			ConsumerFactory.createConsumer(ipNode3, ipNode4);
		} catch (IOException e) {
			System.out.println("Broker未上线！");
		}
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		ConsumerFactory.Pull(ipNode3, ipNode4);
	}
```


###主要架构与功能实现详解

####消息结构
```
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	private int num;//消息序号
	private String message;//消息
	private int type;//消息类型
	private Topic topic;//消息主题
	...
	}
```
&emsp;&emsp;Message类实现了序列化接口，每个Message有消息序号（该序号是否具有唯一性由使用者决定），消息内容，消息类型和消息主题。消息内容由使用者自己定义，可以是某个手机号（用于给该手机号发送短信）或订单信息（用于更新数据库）等等。消息类型定义了5种：
```
	public static final int ONE_WAY = 0;//单向消息
	public static final int REPLY_EXPECTED = 1;//需要得到回复的消息
	public static final int REQUEST_QUEUE = 2;//请求包,用户生产者向Broker申请队列
	public static final int REGISTER = 3;//用于消费者向Broker注册
	public static final int PULL = 4;//用于消费者向Broker注册
```
&emsp;&emsp;消息主题类Topic定义如下：
```
	private HashSet<Integer> queueId;//该Topic在Broker中对应的queueId
	private HashSet<IpNode> consumer_address;//该Topic对应的cunsumer
	String topic_name;//主题名称
	int queueNum;//请求队列数
```
&emsp;&emsp;该类同样实现了序列化接口，主要用于记录消息主题名称，请求队列数，请求队列号和消费者地址。当用户首次定义一个Topic时，需要向Broker申请分配可用的消息队列号，之后将可用的队列号存储进Topic中，以后使用该Topic时就无需申请队列。

####消息存储
```
public class MyQueue implements Serializable{
	private static final long serialVersionUID = 1L;
	private ConcurrentLinkedDeque<Message> queue;
	}
```
&emsp;&emsp;MyQueue定义了消息存储队列，它的实现是一个同步的双向队列，一个Broker中可以同时存在一个或多个队列。

####消息过滤
```
public HashMap<IpNode, List<Message>> filter(List<Message> list) {
		//将Message按照分发地址分类
		HashMap<IpNode, List<Message>> map = new HashMap<IpNode, List<Message>>();
		//初始化
		for(IpNode address:index) {
			if(map.get(address)==null) {
				map.put(address, new ArrayList<Message>());
			}
		}
		//遍历消息，将每条message分类
		Iterator<Message> iterator = list.iterator();
		while(iterator.hasNext()) {
			Message message = iterator.next();
			//每个message可能有很多消费者
			List<IpNode> consumer_address = message.getTopic().getConsumer();
			Iterator<IpNode> it = consumer_address.iterator();
			while(it.hasNext()) {
				IpNode address = it.next();
				List<Message> l = map.get(address);
				if(l!=null)
					l.add(message);
			}
		}
		return map;
	}
```
&emsp;&emsp;过滤器的主要作用就是将要发送的消息按照消费者地址分类，一个消息可能有一个或多个消费者。

####消息分发（Push模式与Pull模式）
```
//为消费者推送消息
	private void pushMessage() {
		HashMap<IpNode, List<Message>> map = filter(index,poll(1));
		for(IpNode ip:map.keySet())
				{
					List<Message> message = map.get(ip);
					for(Message m:message) {
						Client client = clients.get(ip);
						if(client!=null) {
							int i=0;
							for(i=0;i<reTry_Time;i++) {//失败重试三次
								String ack=null;
								try {
									ack = client.SyscSend(m);
								} catch (IOException e) {
									System.out.println("发送失败！正在重试...");
								}
								if(ack!=null)
									break;
							}
						if(i>=reTry_Time) {
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
	    				e.printStackTrace();
	    			}
	        		pushMessage();
	        	}
	    		
	        };
	}.start();
	}
```
&emsp;&emsp;push模式启动一个线程，每次push过程是所有队列出队一个元素，使用过滤器将所有消息分类，发送给相应的消费者，如果发送失败则重试一定次数（默认16次），次数达到上限后依然失败的话会进入死信队列，并告知相应的生产者。

####负载均衡
```
public static List<Integer> balance(ConcurrentHashMap<String,MyQueue> queueList,int queueNum){
		//此时queueList的size一定大于queueNum
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<queueNum;i++) {
			int index = 0;
			int min = Integer.MAX_VALUE;
			for(java.util.Map.Entry<String, MyQueue> entry:queueList.entrySet()) {
				if(entry.getValue().size()<min&&!list.contains(Integer.valueOf(entry.getKey()))) {
					min = entry.getValue().size();
					index = Integer.valueOf(entry.getKey());
				}
			}
			list.add(index);
		}
		return list;
	}
```

&emsp;&emsp;负载均衡器提供一个负载均衡的方法，遍历队列找到前queueNum小的队列号。

####主从备份
```
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
							String s = SerializeUtil.serialize(sync);
							for(IpNode ip:slave) {
								Client client = new Client(ip.getIp(), ip.getPort());
								client.Send(s);
							}
						} catch (IOException e) {
							System.out.println("Slave未上线!");
						}
              		}
              	}
          };
      }.start();
```
&emsp;&emsp;Broker会在init方法中创建一个线程。如果创建带Slave节点备份的消息队列的话,该线程会不停的向Slave节点同步消息，同步不可保证强一致性。

####持久化存储（同步或异步刷盘）与冗机恢复

```
//持久化
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
```
&emsp;&emsp;Broker在init方法中创建一个线程。如果用户开启持久化功能，该线程会每隔一段时间将队列内容写入磁盘，存储格式为2个json，一个存队列内容，一个存消费者地址。
&emsp;&emsp;若不幸冗机，用户可根据recover方法来恢复Broker。

```
//恢复Broker
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
```
####生产者工厂(这里以延时同步工厂为例)
```
private static ConcurrentHashMap<IpNode, Boolean> requestMap= new ConcurrentHashMap<IpNode, Boolean>();
	private static int reTry_Time = 16;
	private static int Delay_Time = 2000;//延时时间默认2s
```
&emsp;&emsp;requestMap用于记录该消费者地址是否已向Broker注册，reTry_Time定义发送失败重试的次数，Delay_Time定义了延时发送时间。
&emsp;&emsp;生产者需先向Broker申请队列：
```
public static Topic RequestQueue(Topic topic,String ip,int port){//输入为一个topic，里面包含请求的队列个数
		System.out.println("请求向Broker申请队列...");
		Topic t = topic;
		Message m = new Message("RequestQueue",MessageType.REQUEST_QUEUE,t, -1);
		String queue = DelaySyscProducerFactory.SendQueueRegister(m, ip, port);
		String[] l = queue.substring(7).split(" ");
		for(String i:l)
			topic.addQueueId(Integer.parseInt(i));
		IpNode ipNode = new IpNode(ip, port);
		requestMap.put(ipNode, true);
		return t;
	}
```
&emsp;&emsp;申请队列时向Broker发送一个MessageType.REQUEST_QUEUE类型的消息：
```
private static String SendQueueRegister(Message msg,String ip,int port) {//未申请队列返回null
		Client client;
	if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		try {
			client = new Client(ip, port);
			//失败重复，reTry_Time次放弃
			for(int i=0;i<reTry_Time;i++) {
				String result = client.SyscSend(msg);
				if(result!=null) {
					System.out.println("队列申请成功！");
					return result;
				}	
				if("".equals(result))
					return null;
			}
		} catch (IOException e) {
			System.out.println("Broker未上线！");
		}
		return null;
	}
```
&emsp;&emsp;Broker收到该消息后会返回可用的消息队列序号，生产者工厂将这些消息序号添加到topic中，之后就可用该topic发送消息了：
```
//发送成功返回值为消息号+ACK
//发送失败返回值为null
	public static String Send(Message msg,String ip,int port) {//未申请队列返回null
		IpNode ipNode = new IpNode(ip, port);
		if(requestMap.get(ipNode)==null) {
			System.out.println("未向Broker申请队列！");
			return null;
		}
		//等待Delay_Time秒
		try {
			Thread.sleep(Delay_Time);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return null;
		}
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED&&msg.getType()!=MessageType.REQUEST_QUEUE)
			msg.setType(MessageType.REPLY_EXPECTED);
		//失败重复，reTry_Time次放弃
		for(int i=0;i<reTry_Time;i++) {
			try {
				client = new Client(ip, port);
				String result = client.SyscSend(msg);
				if(result!=null)
					return result;
				if("".equals(result))
					return null;
			} catch (IOException e) {
				System.out.println("生产者消息发送失败，正在重试第"+(i+1)+"次...");
			}
		}
		return null;
	}
```
&emsp;&emsp;若发送成功返回值为消息号+空格+ACK，发送失败返回值为null。
####消费者工厂
```
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Message>> map = new ConcurrentHashMap<Integer,ConcurrentLinkedQueue<Message>>();
```
&emsp;&emsp;这个map用于缓存Broker发来的消息，键为本地端口号，值为该消费者的消息缓冲区。
&emsp;&emsp;消费者工厂调用createConsumer向Broker注册消费者：
```
public static void createConsumer(IpNode ipNode1/*Broker地址*/,IpNode ipNode2/*本地地址*/) throws IOException {
		if(map.containsKey(ipNode2.getPort())) {
			System.out.println("端口已被占用!");
			return;
		}
		ConsumerFactory.register(ipNode1,ipNode2);
		ConsumerFactory.waiting(ipNode2.getPort());
		map.put(ipNode2.getPort(), new ConcurrentLinkedQueue<Message>());
	}
```
&emsp;&emsp;register方法向Broker发送注册消息：
```
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
```
&emsp;&emsp;waiting方法的作用是在某个端口监听，接受消息队列发送来的消息。
```
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
```
