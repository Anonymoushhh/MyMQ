package Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * д����������
 * @author MOTUI
 *
 */
public class ResponeProcessor {
    //�����̳߳�
    private static ExecutorService executorService = Executors.newFixedThreadPool(1000);

    public static void ProcessorRespone(final SelectionKey key) {
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
                    System.out.println("�������յ���"+new String(attachment.toByteArray()));
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