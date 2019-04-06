package Common;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Utils.Client;

public class Topic {
	//存储结构都为HashSet，一来为了方便去重，二来为了查找快速
	private HashSet<Integer> queue_num;
	private HashSet<IpNode> consumer_address;
	private HashSet<IpNode> producer_address;
	private HashSet<IpNode> broker_address;
//	private HashSet<String> nameserver_address;
	Client client; 
	String queue_name;
	int queueNum;
	public Topic(String s/*主题名称*/,int queueNum) {
		queue_name = s;
		this.queueNum = queueNum;
		queue_num = new HashSet<Integer>();
		consumer_address = new HashSet<IpNode>();
		producer_address = new HashSet<IpNode>();
		broker_address = new HashSet<IpNode>();
//		nameserver_address = new HashSet<String>();
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
	public String getQueueName() {
		return queue_name;
	}
	public List<Integer> getQueue() {
		return transformforInteger(queue_num);
	}
	public List<IpNode> getConsumer(){
		return transform(consumer_address);
	}
	public void addConsumer(IpNode ipnode) {
		consumer_address.add(ipnode);
	}
	public List<IpNode> getProducer(){
		return transform(producer_address);
	}
	public List<IpNode> getBroker(){
		return transform(broker_address);
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
