package Common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Topic implements Serializable{

	private static final long serialVersionUID = 1L;
	//存储结构都为HashSet，一来为了方便去重，二来为了查找快速
	private HashSet<Integer> queueId;//该Topic在Broker中对应的queueId
	private HashSet<IpNode> consumer_address;//该Topic对应的cunsumer
	String topic_name;//主题名称
	int queueNum;//请求队列数
	public Topic(String s/*主题名称*/,int queueNum) {
		topic_name = s;
		this.queueNum = queueNum;
		queueId = new HashSet<Integer>();
		consumer_address = new HashSet<IpNode>();
	}
	public Topic(String s/*主题名称*/,HashSet<Integer> queueId,HashSet<IpNode> consumer_address) {
		topic_name = s;
		this.queueId = queueId;
		this.consumer_address = consumer_address;
	}
	//HashSet元素转换为线性表
	private List<IpNode> transform(HashSet<IpNode> set) {
		List<IpNode> list= new LinkedList<IpNode>();
		for(IpNode s : set)
			list.add(s);
		return list;
	}
	private List<Integer> transformforInteger(HashSet<Integer> set) {
		List<Integer> list= new LinkedList<Integer>();
		for(Integer s : set)
			list.add(s);
		return list;
	}
	public String getTopicName() {
		return topic_name;
	}
	public List<Integer> getQueue() {
		return transformforInteger(queueId);
	}
	public List<IpNode> getConsumer(){
		return transform(consumer_address);
	}
	public void addConsumer(IpNode ipnode) {
		consumer_address.add(ipnode);
	}
	public void deleteConsumer(IpNode ipnode) {
		consumer_address.remove(ipnode);
	}
	public void addQueueId(int i) {
		queueId.add(i);
	}
	public int getQueueNum() {
		return queueNum; 
	}
//	List<String> getNameServer(){
//		return transform(nameserver_address);
//	}
//	private boolean isFind(String s,HashSet<String> set) {
//		return set.contains(s);
//	}
//	public boolean isExistQueue(String s) {
//		return isFind(s,queue_num);
//	}
//	public boolean isExistConsumer(String s) {
//		return isFind(s,consumer_address);
//	}
//	public boolean isExistProducer(String s) {
//		return isFind(s,producer_address);
//	}
////	boolean isExistNameServer(String s) {
////		return isFind(s,nameserver_address);
////	}
//	public boolean isExistBroker(String s) {
//		return isFind(s,broker_address);
//	}
}
