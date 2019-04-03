package Producer;

import java.io.IOException;

import Utils.Client;

public class UnidirectionalProducerFactory extends AbstractProducerFactory{

	public static void Send(String msg,String ip,int port) {
		Client client;
		try {
			client = new Client(ip, port);
			client.Send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
