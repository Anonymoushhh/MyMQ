package Test;

import java.io.IOException;

import Common.IpNode;
import Common.Message;
import Consumer.ConsumerFactory;

public class ConsumerTest {

	public static void main(String[] args) {
		//����Consumer
		IpNode ipNode1 = new IpNode("127.0.0.1", 81);
		IpNode ipNode2 = new IpNode("127.0.0.1", 8888);//�����ߵ�ַ
		try {
			ConsumerFactory.createConsumer(ipNode1, ipNode2);
		} catch (IOException e1) {
			System.out.println("Brokerδ���ߣ�");
		}
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Message m1 = ConsumerFactory.getMessage(8888);
    		if(m1!=null) 
				System.out.println("������"+ipNode2.getIp()+ipNode2.getPort()+"�յ���Ϣ��"+m1.getMessage());	
		}
	}

}
