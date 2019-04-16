package Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 写操作工具类
 * @author MOTUI
 *
 */
public class DefaultResponeProcessor implements ResponseProcessor{
    //构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10000);

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
                    System.out.println("服务器收到："+new String(attachment.toByteArray()));
                    String message = new String(attachment.toByteArray())+"ACK";
                    buffer.put(message.getBytes());
                    buffer.flip();
                    writeChannel.write(buffer);
                    
                } catch (IOException e) {
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