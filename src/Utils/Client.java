package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *  NIO 客户端
 */
public class Client {
	SocketChannel socketChannel = null;
	static int count;
	public Client(String ip,int port) throws IOException {
		init(ip,port);
	}
    public static void main(String[] args) {
        //使用线程模拟用户 并发访问
        for (int i = 0; i < 10; i++) {
            new Thread(){
                public void run() {
                    try {
						Client client = new Client("localhost",8989);
						System.out.println(client.SyscSend(""+count++));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                };
            }.start();
        }
    }
    void init(String ip,int port) throws IOException {
    	//1.创建SocketChannel
		socketChannel=SocketChannel.open();
        //2.连接服务器
        socketChannel.connect(new InetSocketAddress(ip,port));
    }
    public String SyscSend(String msg){  	
    	try {
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return receive();
    }
    public void Send(String msg){  	
    	try {
            //写数据
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String receive() {
    	try {
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
    		e.printStackTrace();
    	}
		return null;
    }
}
