package Broker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.PRIVATE_MEMBER;

import Common.Message;
import Common.MessageType;
import Common.RegisterMessage;
import Utils.ResponseProcessor;
import Utils.SerializeUtils;

/**
 * 写操作工具类
 * @author MOTUI
 *
 */
public class SlaveResponeProcessor implements ResponseProcessor{
    //构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);
//    private static volatile int Count =0;
    public void processorRespone(final SelectionKey key,Slave slave) {
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
                    Synchronizer sync = (Synchronizer)SerializeUtils.serializeToObject(new String(attachment.toByteArray(),"ISO-8859-1"));
//                    System.out.println(sync.getQueueList());
                    slave.Sync(sync);   
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