package Producer;

import java.io.IOException;

import Common.Message;
import Common.MessageType;
import Utils.Client;

public class UnidirectionalProducerFactory extends AbstractProducerFactory{

	public static void Send(Message msg,String ip,int port) {
		Client client;
		if(msg.getType()!=MessageType.ONE_WAY)
			msg.setType(MessageType.ONE_WAY);
		try {
			client = new Client(ip, port);
			client.Send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
