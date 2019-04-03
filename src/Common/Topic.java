package Common;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Utils.Client;

public class Topic {
	//�洢�ṹ��ΪHashSet��һ��Ϊ�˷���ȥ�أ�����Ϊ�˲��ҿ���
	private HashSet<String> queue_num;
	private HashSet<String> consumer_address;
	private HashSet<String> producer_address;
	private HashSet<String> broker_address;
	private HashSet<String> nameserver_address;
	Client client; 
	public Topic(String s/*��������*/,int queueNum) {
		queue_num = new HashSet<String>();
		consumer_address = new HashSet<String>();
		producer_address = new HashSet<String>();
		broker_address = new HashSet<String>();
		nameserver_address = new HashSet<String>();
	}
	//HashSetԪ��ת��Ϊ���Ա�
	private List<String> transform(HashSet<String> set) {
		List<String> list= new LinkedList<String>();
		for(String s : queue_num)
			list.add(s);
		return list;
	}
	List<String> getQueue() {
		return transform(queue_num);
	}
	List<String> getConsumer(){
		return transform(consumer_address);
	}
	List<String> getProducer(){
		return transform(producer_address);
	}
	List<String> getBroker(){
		return transform(broker_address);
	}
	List<String> getNameServer(){
		return transform(nameserver_address);
	}
	private boolean isFind(String s,HashSet<String> set) {
		return set.contains(s);
	}
	boolean isExistQueue(String s) {
		return isFind(s,queue_num);
	}
	boolean isExistConsumer(String s) {
		return isFind(s,consumer_address);
	}
	boolean isExistProducer(String s) {
		return isFind(s,producer_address);
	}
	boolean isExistNameServer(String s) {
		return isFind(s,nameserver_address);
	}
	boolean isExistBroker(String s) {
		return isFind(s,broker_address);
	}
}
