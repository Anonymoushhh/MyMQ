package Consumer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.Message;
import Common.MessageType;
import Utils.ResponseProcessor;
import Utils.SerializeUtils;

/**
 * д����������
 * @author MOTUI
 *
 */
public class ConsumerResponeProcessor implements ResponseProcessor{
    //�����̳߳�
    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);

    public void processorRespone(final SelectionKey key,int port) {
        //�õ��̲߳�ִ��
        executorService.submit(new Runnable() {
            @Override
            public void run() {
            	SocketChannel writeChannel = null;
                try {
                    // д����
                    writeChannel = (SocketChannel) key.channel();
                    //�õ��ͻ��˴��ݵ�����
                    ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    Message msg = (Message)SerializeUtils.serializeToObject(new String(attachment.toByteArray(),"ISO-8859-1"));    
                    ConcurrentLinkedQueue<Message> list = ConsumerFactory.getList(port);
//                    System.out.println(msg.getMessage());
                    list.add(msg);
//                    System.out.println("here");
                    if(msg.getType()==MessageType.REPLY_EXPECTED) {
                    	//��Ҫ�ظ�
//                    	System.out.println("hehe");
                    	String message = msg.getNum()+" ACK";
                    	buffer.put(message.getBytes("ISO-8859-1"));
                        buffer.flip();
                        writeChannel.write(buffer);
                    }
                    
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                	try {
						writeChannel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            }
        });
    }
    
}