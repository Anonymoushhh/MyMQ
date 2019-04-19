package Broker;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import Common.IpNode;
import Utils.Client;
import Utils.DefaultRequestProcessor;
import Utils.Server;

public class Slave {
	public Broker broker;
	public Slave(int port1/*slave监听端口*/,int port2/*slaveBroker监听端口*/) {
		System.out.println("Slave已启动，在"+port1+"端口监听");
		new Thread(){
            public void run() {
            	try {
        			broker = new Broker(port2);
        			System.out.println("SlaveBroker已启动，在"+port2+"端口监听");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            };
        }.start();
		
		DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
		SlaveResponeProcessor slaveResponeProcessor = new SlaveResponeProcessor();
		Slave slave = this;
        new Thread(){
            public void run() {
                	try {
						new Server(port1, defaultRequestProcessor, slaveResponeProcessor,slave);
					} catch (IOException e) {
						e.printStackTrace();
					}
            };
        }.start();
	}
	public void Sync(Synchronizer synchronizer) {
		ConcurrentHashMap<String, MyQueue> queueList = synchronizer.getQueueList();
		broker.setQueueList(queueList);
		List<IpNode> index = synchronizer.getIndex();
		for(IpNode ipNode:index) {
			try {
				broker.addConsumer(ipNode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		Slave s = new Slave(83,84);
		new Thread(){
            public void run() {
            	while(true) {
            		try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//            		if(s.broker!=null)
//                		s.broker.getAll();
            	}
            };
        }.start();
	}

}
