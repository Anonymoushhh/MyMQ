package Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Broker.Broker;
import Common.IpNode;

public class MyBroker {

	public static void main(String[] args) {
		//����Broker(���Ӹ���)
//				try {
//					IpNode slaveIpNode = new IpNode("127.0.0.1", 83);
//					List<IpNode> list = new ArrayList<IpNode>();
//					list.add(slaveIpNode);
//					Broker broker = new Broker(81,list);
//					broker.setPushTime(1000);
//					broker.push();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		//Broker(�����Ӹ���)
				try {
					Broker broker = new Broker(81);
					broker.setPushTime(1000);
					broker.push();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
