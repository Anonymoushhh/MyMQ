package Producer;

import java.io.IOException;

import Common.Message;
import Utils.Client;

public class SyscProducerFactory extends AbstractProducerFactory{
	//����ֵΪACK��
	//todo String��Ϊmessage
	static String Send(String msg,String ip,int port) {
		Client client;
		try {
			client = new Client(ip, port);
			//ʧ���ظ������η���
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
