package Consumer;

import java.io.IOException;

import Common.Message;
import Utils.Client;
import Utils.Server;

public class ConsumerFactory extends AbstractConsumerFactory{
//��ĳ���˿ڼ���
	//todo state�����Ƿ���Ҫ�ظ�
	static void waiting(int port,int state) throws IOException {
		new Server(port);
	}
}
