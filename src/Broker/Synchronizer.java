package Broker;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import Common.IpNode;

public class Synchronizer implements Serializable{
	private ConcurrentHashMap<String,MyQueue> queueList;
	private List<IpNode> index;//消费者地址
	public Synchronizer(ConcurrentHashMap<String, MyQueue> queueList, List<IpNode> index) {
		this.queueList = queueList;
		this.index = index;
	}

	public ConcurrentHashMap<String,MyQueue> getQueueList() {
		return queueList;
	}


	public List<IpNode> getIndex() {
		return index;
	}


}
