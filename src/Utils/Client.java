package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Common.Message;
import Common.Topic;

/**
 *  NIO �ͻ���
 */
public class Client {
	SocketChannel socketChannel = null;
	static int count;
	public Client(String ip,int port) throws IOException {
		init(ip,port);
	}
    public static void main(String[] args) {
        //ʹ���߳�ģ���û� ��������
        for (int i = 0; i < 1; i++) {
            new Thread(){
                public void run() {
                    try {
						Client client = new Client("127.0.0.1",81);
						Topic t = new Topic("t1", 1);
						Message msg = new Message("hh", t, 1);
						String string = SerializeUtils.serialize(msg);
						//System.out.println(string);
						System.out.println(client.SyscSend(string));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                };
            }.start();
        }
    }
    void init(String ip,int port) throws IOException {
    	//1.����SocketChannel
		socketChannel=SocketChannel.open();
        //2.���ӷ�����
        socketChannel.connect(new InetSocketAddress(ip,port));
    }
    public String SyscSend(String msg){  	
    	try {
            //д����
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes("ISO-8859-1"));
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
            //д����
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes("ISO-8859-1"));
            buffer.flip();
            socketChannel.write(buffer);
            socketChannel.shutdownOutput();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String receive() {
    	try {
    	//������
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
