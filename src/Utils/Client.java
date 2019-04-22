package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Common.IpNode;
import Common.Message;
import Common.MessageType;
import Common.RegisterMessage;
import Common.Topic;

/**
 *  NIO 客户端
 */
public class Client {
	SocketChannel socketChannel = null;
	volatile static int count;
	private String ip;
	private int port;
	public Client(String ip,int port) throws IOException {
		this.ip = ip;
		this.port = port;
	}
    public static void main(String[] args) throws IOException {
//    	IpNode ipNode = new IpNode("127.0.0.1", 81);
//		Client client = new Client(ipNode.getIp(), ipNode.getPort());
//		RegisterMessage msg = new RegisterMessage(ipNode, "topic1", 1);
//		System.out.println(client.SyscSend(msg));
//        //使用线程模拟用户 并发访问
//        for (int i = 0; i < 1; i++) {
//            new Thread(){
//                public void run() {
//                    try {
//                    	Client client = new Client("127.0.0.1",8088);
//						Topic t = new Topic("t1", 15);
////						synchronized (Client.class) {
////							t.addQueueId(count);
////						System.out.println(1);
////							String string = SerializeUtils.serialize(msg);
//							for(int i=0;i<10;i++) {
//								Message msg = new Message("hh",t, count++);
//								System.out.println(client.SyscSend(msg));
//							}
////						}
//						
//						//System.out.println(string);
//						
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
////						e.printStackTrace();
//						System.out.println("Connection Refuse.");
//					}
//                };
//            }.start();
//        }
    }
    private void init(String ip,int port) throws IOException {
    	//1.创建SocketChannel
		socketChannel=SocketChannel.open();
        //2.连接服务器
        socketChannel.connect(new InetSocketAddress(ip,port));
    }
    public String SyscSend(String msg) throws IOException{  
    		init(ip,port);
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes("ISO-8859-1"));
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
        
    	return receive();
    }
    public void Send(String msg) throws IOException{  
    		init(ip,port);
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
//            buffer.clear();
            buffer.put(msg.getBytes("ISO-8859-1"));
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
    }
    //发送对象
    public String SyscSend(Message msg) throws IOException{  	
    		init(ip,port);
    		String string = SerializeUtil.serialize(msg);
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
//            buffer.clear();
            buffer.put(string.getBytes("ISO-8859-1"));
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
    	return receive();
    }
    public void Send(Message msg) throws IOException{  	
    		init(ip,port);
    		String string = SerializeUtil.serialize(msg);
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
//            buffer.clear();
            buffer.put(string.getBytes("ISO-8859-1"));
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
    }
    public String receive() {
    	try {
//    	init(ip,port);
    	//读数据
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        int len = 0;
        while (true) {
            buffer.clear();
            len = socketChannel.read(buffer);
            if (len == -1)
                break;
            buffer.flip();
            while (buffer.hasRemaining()) {
                bos.write(buffer.get());
            }
        }
        return new String(bos.toByteArray());
    	}catch(IOException e) {
    		System.out.println("Connection Refuse.");
    	}
		return null;
    }
}
