package Test;

import java.io.IOException;

import Broker.Broker;

public class MyBroker {

	public static void main(String[] args) {
		//´´½¨Broker
				Broker broker;
				try {
					broker = new Broker(81);
					broker.setPushTime(1);
					broker.push();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}

}
