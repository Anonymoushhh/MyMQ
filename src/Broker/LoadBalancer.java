package Broker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer {
	//找到前queueNum小的队列号
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

}
