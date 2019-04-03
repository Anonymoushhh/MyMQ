package Producer;

import java.io.IOException;

import Utils.Client;

public class DelaySyscProducerFactory extends AbstractProducerFactory{
	public static String Send(String msg,String ip,int port,long delaytime) throws InterruptedException {
		Client client;
		Thread.sleep(delaytime);
		try {
			client = new Client(ip, port);
			//失败重复，三次放弃
			for(int i=0;i<3;i++) {
				String result = client.SyscSend(msg);
				if(result!=null)
					return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
}
