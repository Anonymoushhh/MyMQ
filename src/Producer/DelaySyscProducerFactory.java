package Producer;

import java.io.IOException;

import Common.Message;
import Common.MessageType;
import Utils.Client;

public class DelaySyscProducerFactory extends AbstractProducerFactory{
	public static String Send(Message msg,String ip,int port,long delaytime) throws InterruptedException {
		Client client;
		if(msg.getType()!=MessageType.REPLY_EXPECTED)
			msg.setType(MessageType.REPLY_EXPECTED);
		Thread.sleep(delaytime);
		try {
			client = new Client(ip, port);
			//失败重复，三次放弃
			for(int i=0;i<3;i++) {
				String result = client.SyscSend(msg);
				if(result!=null&&"ACK".equals(result.substring(result.length()-3)))
					return result;
				if("".equals(result))
					return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
}
