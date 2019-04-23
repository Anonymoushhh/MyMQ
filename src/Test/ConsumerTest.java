package Test;

import java.io.IOException;

import Common.IpNode;
import Common.Message;
import Consumer.ConsumerFactory;

public class ConsumerTest {

	public static void main(String[] args) {
		//创建Consumer
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);//消费者地址
		try {
			ConsumerFactory.createConsumer(ipNode1, ipNode2);
		} catch (IOException e1) {
			System.out.println("Broker未上线！");
		}
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Message m1 = ConsumerFactory.getMessage(8888);
    		if(m1!=null) 
				System.out.println("消费者"+ipNode2.getIp()+ipNode2.getPort()+"收到消息："+m1.getMessage());	
		}
	}

}
