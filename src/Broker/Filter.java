package Broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Common.IpNode;
import Common.Message;
import Common.Topic;

public class Filter {
	List<IpNode> index;//索引
	public Filter(List<IpNode> index) {
		this.index = index;
	}
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
				l.add(message);
			}
		}
		return map;
	}
	public static void main(String[] args) {
		//初始化索引
		List<IpNode> index = new ArrayList<IpNode>();
		IpNode ip1 = new IpNode("100.1.1.1", 80);
		IpNode ip2 = new IpNode("100.1.1.2", 80);
		IpNode ip3 = new IpNode("100.1.1.3", 80);
		index.add(ip1);
		index.add(ip2);
		index.add(ip3);
		//初始化Topic
		Topic t1 = new Topic("1", 10);
		Topic t2 = new Topic("2", 10);
		Topic t3 = new Topic("3", 10);
		t1.addConsumer(ip1);
		t1.addConsumer(ip2);
		t2.addConsumer(ip2);
		t2.addConsumer(ip3);
		t3.addConsumer(ip3);
		t3.addConsumer(ip1);
		//初始化Message
		List<Message> list = new ArrayList<Message>();
		Message m1 = new Message("h1", t1,1);
		Message m2 = new Message("h2", t2,2);
		Message m3 = new Message("h3", t3,3);
		list.add(m1);
		list.add(m2);
		list.add(m3);
		Filter filter = new Filter(index);
		HashMap<IpNode, List<Message>> ans = filter.filter(list);
		for(List<Message> lm:ans.values()) {
			for(Message m:lm)
				System.out.print(m.getMessage());
			System.out.println();
		}
	}
}
