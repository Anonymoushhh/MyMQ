package Broker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.Message;
import Utils.ResponseProcessor;
import Utils.SerializeUtils;

/**
 * 写操作工具类
 * @author MOTUI
 *
 */
public class BrokerResponeProcessor implements ResponseProcessor{
    //构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void processorRespone(final SelectionKey key) {
        //拿到线程并执行
        executorService.submit(new Runnable() {
            @Override
            public void run() {
            	SocketChannel writeChannel = null;
                try {
                    // 写操作
                    writeChannel = (SocketChannel) key.channel();
                    //拿到客户端传递的数据
                    ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //System.out.println("服务器收到："+new String(attachment.toByteArray()));
                    Message msg = (Message)SerializeUtils.serializeToObject(new String(attachment.toByteArray(),"ISO-8859-1")); 
                    System.out.println(msg.getMessage());
                    String message = msg.getNum()+"ACK";
                    //Broker.add(0, msg);
                    buffer.put(message.getBytes("ISO-8859-1"));
                    buffer.flip();
                    writeChannel.write(buffer);
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