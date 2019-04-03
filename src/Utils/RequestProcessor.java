package Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * �������Ĺ�����
 * @author MOTUI
 *
 */
public class RequestProcessor {

    //�����̳߳�
    private static ExecutorService  executorService  = Executors.newFixedThreadPool(1000);

    public static void ProcessorRequest(final SelectionKey key){
        //����̲߳�ִ��
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    SocketChannel readChannel = (SocketChannel) key.channel();
                    // I/O�����ݲ���
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len = 0;
                    while (true) {
                        buffer.clear();
                        len = readChannel.read(buffer);
                        if (len == -1) break;
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            baos.write(buffer.get());
                        }
                    }
                    //System.out.println("�������˽��յ������ݣ�"+ new String(baos.toByteArray()));
                    
                    //��������ӵ�key��
                    key.attach(baos);
                    //��ע��д������ӵ�������
                    Server.addWriteQueen(key);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}