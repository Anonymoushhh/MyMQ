package Test;

import java.io.IOException;

import Common.IpNode;
import Common.Message;
import Consumer.ConsumerFactory;

public class Consumer {

	public static void main(String[] args) {
		//´´½¨Consumer
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);
		try {
			ConsumerFactory.createConsumer(ipNode1, ipNode2, 8888);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message m = ConsumerFactory.getMessage(8888);
    		if(m!=null) 
				System.out.println(m.getMessage());
				
		}
		

	}

}
