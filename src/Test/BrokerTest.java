package Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Broker.Broker;
import Common.IpNode;

public class BrokerTest {

	public static void main(String[] args) {
		//创建Broker(主从复制，push模式)
//				try {
//					IpNode slaveIpNode = new IpNode("127.0.0.1", 83);
//					List<IpNode> list = new ArrayList<IpNode>();
//					list.add(slaveIpNode);
//					Broker broker = new Broker(81,list);
//					broker.setPush_Time(1000);
//					broker.push();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
		//Broker(非主从复制，push模式)
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
		//Broker(非主从复制,pull模式)
//				try {
//					Broker broker = new Broker(81);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
	}

}
