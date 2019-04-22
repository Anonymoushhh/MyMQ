package Broker;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import Common.IpNode;

public class Synchronizer implements Serializable{

	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,MyQueue> queueList;
	private List<IpNode> index;//�����ߵ�ַ
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
