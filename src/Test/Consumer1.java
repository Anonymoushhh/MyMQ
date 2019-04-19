package Test;

import java.io.IOException;

import Common.IpNode;
import Common.Message;
import Consumer.ConsumerFactory;

public class Consumer1 {

	public static void main(String[] args) {
		IpNode ipNode3 = new IpNode("127.0.0.1", 81);
		IpNode ipNode4 = new IpNode("127.0.0.1", 8889);
    	try {
			ConsumerFactory.createConsumer(ipNode3, ipNode4, 8889);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("BrokerŒ¥…œœﬂ£°");
		}
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Message m = ConsumerFactory.getMessage(8889);
    		if(m!=null) 
				System.out.println("8889 "+m.getMessage());	

	}
	}
}
