package Consumer;

import java.io.IOException;

import Common.Message;
import Utils.Client;
import Utils.Server;

public class ConsumerFactory extends AbstractConsumerFactory{
//在某个端口监听
	//todo state定义是否需要回复
	static void waiting(int port,int state) throws IOException {
		new Server(port);
	}
}
