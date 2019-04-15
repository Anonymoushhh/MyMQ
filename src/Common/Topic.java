package Common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Topic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//�洢�ṹ��ΪHashSet��һ��Ϊ�˷���ȥ�أ�����Ϊ�˲��ҿ���
	private HashSet<Integer> queueId;//��Topic��Broker�ж�Ӧ��queueId
	private HashSet<IpNode> consumer_address;//��Topic��Ӧ��cunsumer
	private HashSet<IpNode> producer_address;
	private HashSet<IpNode> broker_address;
//	private HashSet<String> nameserver_address;
//	Client client; 
	String queue_name;//��������
	int queueNum;//���������
	public Topic(String s/*��������*/,int queueNum) {
		queue_name = s;
		this.queueNum = queueNum;
		queueId = new HashSet<Integer>();
		consumer_address = new HashSet<IpNode>();
		producer_address = new HashSet<IpNode>();
		broker_address = new HashSet<IpNode>();
//		nameserver_address = new HashSet<String>();
	}
	public Topic(String s/*��������*/,HashSet<Integer> queueId,HashSet<IpNode> consumer_address) {
		queue_name = s;
		queueId = new HashSet<Integer>();
		consumer_address = new HashSet<IpNode>();
	}
	//HashSetԪ��ת��Ϊ���Ա�
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
		return transformforInteger(queueId);
	}
	public List<IpNode> getConsumer(){
		return transform(consumer_address);
	}
	public void addConsumer(IpNode ipnode) {
		consumer_address.add(ipnode);
	}
	public void addQueueId(int i) {
		queueId.add(i);
	}
	public List<IpNode> getProducer(){
		return transform(producer_address);
	}
	public int getQueueNum() {
		return queueNum; 
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
