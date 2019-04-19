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
import Common.PullMessage;
import Common.RegisterMessage;
import Utils.ResponseProcessor;
import Utils.SerializeUtils;

/**
 * 写操作工具类
 * @author MOTUI
 *
 */
public class BrokerResponeProcessor implements ResponseProcessor{
    //构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);
//    private static volatile int Count =0;
    public void processorRespone(final SelectionKey key,Broker broker) {
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
//                    System.out.println(msg.getMessage());
//                    System.out.println(msg.getType());
//                    System.out.println(msg.getNum()+"号消息");
                    if(msg.getType()==MessageType.REQUEST_QUEUE) {
                    	Integer queueNum = Integer.valueOf(msg.getTopic().getQueueNum());
                    	//需要回复
                    	List<Integer> number = broker.choiceQueue(queueNum);//分配的queueNumber
                    	String message=msg.getNum()+" ACK";
//                    	System.out.println("here");
                    	for(Integer i:number)
                    		message += " "+i;
                    	buffer.put(message.getBytes("ISO-8859-1"));
                        buffer.flip();
                        writeChannel.write(buffer);
                    }else if(msg.getType()==MessageType.REPLY_EXPECTED) {
                    	addToBroker(msg, broker);
                    	//需要回复
                    	String message = msg.getNum()+" ACK";
                    	buffer.put(message.getBytes("ISO-8859-1"));
                        buffer.flip();
                        writeChannel.write(buffer);
                    }else if(msg.getType()==MessageType.ONE_WAY) {
                    	addToBroker(msg, broker);//不回复
                    }else if(msg.getType()==MessageType.REGISTER) {
                    	RegisterMessage registerMessage = (RegisterMessage)msg;
                    	broker.addConsumer(registerMessage.getIpNode());
                    	String message = msg.getMessage()+" ACK";
                    	buffer.put(message.getBytes("ISO-8859-1"));
                        buffer.flip();
                        writeChannel.write(buffer);
                    }else if(msg.getType()==MessageType.PULL) {
                    	PullMessage pullMessage = (PullMessage)msg;
                    	broker.pullMessage(pullMessage.getIpNode());
                    	String message = msg.getMessage()+" ACK";
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
    private void addToBroker(Message msg,Broker broker) {
    	List<Integer> list = msg.getTopic().getQueue();
    	for(Integer i:list) {
//    		System.out.println(msg.getNum()+"号消息的count是"+i);
    		broker.add(i, msg);
      }
    }
}