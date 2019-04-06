package Broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Common.IpNode;
import Common.Message;
import Common.Topic;

public class Filter {
	HashMap<Topic,IpNode> index;//ÄæÏòË÷Òý
	public Filter(HashMap<Topic,IpNode> map) {
		this.index = map;
	}
	public HashMap<IpNode, List<Message>> filter(List<Message> list) {
		HashMap<IpNode, List<Message>> map = new HashMap<IpNode, List<Message>>();
		for(IpNode address:index.values()) {
			if(map.get(address)==null) {
				map.put(address, new ArrayList<Message>());
			}
		}
		Iterator<Message> iterator = list.iterator();
		while(iterator.hasNext()) {
			Message message = iterator.next();
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
		HashMap<Topic,IpNode> index = new HashMap<Topic,IpNode>();
		Topic t1 = new Topic("1", 10);
		Topic t2 = new Topic("2", 10);
		Topic t3 = new Topic("3", 10);
	}

}
