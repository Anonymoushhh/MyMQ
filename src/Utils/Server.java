package Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import Broker.Broker;
import Broker.BrokerResponeProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * NIO ��������
 * 
 * @author MOTUI
 * 
 */
public class Server {
	ServerSocketChannel serverSocketChannel = null;
    //�洢SelectionKey�Ķ���
    private static List<SelectionKey> writeQueen = new ArrayList<SelectionKey>();
    private static Selector selector = null;
    RequestProcessor requestProcessor;
    ResponseProcessor responeProcessor;
    Broker broker;
    public Server(int port,RequestProcessor requestProcessor,ResponseProcessor responeProcessor) throws IOException {
    	this.requestProcessor = requestProcessor;
    	this.responeProcessor = responeProcessor;
    	init(port);
    }
    public Server(int port,RequestProcessor requestProcessor,ResponseProcessor responeProcessor,Broker broker) throws IOException {
    	this.requestProcessor = requestProcessor;
    	this.responeProcessor = responeProcessor;
    	this.broker = broker;
    	init(port);
    }
    //���SelectionKey������
    public static void addWriteQueen(SelectionKey key){
        synchronized (writeQueen) {
            writeQueen.add(key);
            //�������߳�
            selector.wakeup();
        }
    }
    void init(int port) throws IOException {
    	// 1.����ServerSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        // 2.�󶨶˿�
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 3.����Ϊ������
        serverSocketChannel.configureBlocking(false);
        // 4.����ͨ��ѡ����
        selector = Selector.open();

        /*
         * 5.ע���¼�����
         * 
         *  sel:ͨ��ѡ����
         *  ops:�¼����� ==>SelectionKey:��װ�࣬�����¼����ͺ�ͨ�������ĸ��������ͱ�ʾ�����¼�����
         *  SelectionKey.OP_ACCEPT ��ȡ����      SelectionKey.OP_CONNECT ����
         *  SelectionKey.OP_READ ��           SelectionKey.OP_WRITE д
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        start(port);
    }
//    public static void main(String[] args) throws IOException {
//    	DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
//    	RegisterResponeProcessor defaultResponeProcessor = new RegisterResponeProcessor();
//    	new Server(15000,defaultRequestProcessor,defaultResponeProcessor);
//    }
    void start(int port) throws IOException {
    	while (true) {
            System.out.println("�������ˣ����ڼ���"+port+"�˿�");
            // 6.��ȡ����I/Oͨ��,����ж��ٿ��õ�ͨ��
            int num = selector.select();
            if (num > 0) { // �ж��Ƿ���ڿ��õ�ͨ��
                // ������е�keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                // ʹ��iterator�������е�keys
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                // ����������ǰI/Oͨ��
                while (iterator.hasNext()) {
                    // ��õ�ǰkey
                    SelectionKey key = iterator.next();
                    // ����iterator��remove()�������������Ƴ���ǰI/Oͨ������ʶ��ǰI/Oͨ���Ѿ�����
                    iterator.remove();
                    // �ж��¼����ͣ�����Ӧ�Ĵ���
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = ssChannel.accept();

//                        System.out.println("��������"+ socketChannel.getRemoteAddress());

                        // ��ȡ�ͻ��˵�����
                        // ���÷�����״̬
                        socketChannel.configureBlocking(false);
                        // ע�ᵽselector(ͨ��ѡ����)
                        socketChannel.register(selector, SelectionKey.OP_READ);

                    } else if (key.isReadable()) {
                        //ȡ�����¼��ļ��
                        key.cancel();
                        //���ö�����������
                        requestProcessor.processorRequest(key);
                    } else if (key.isWritable()) {
                        //ȡ�����¼��ļ��
                        key.cancel();
                        //����д����������
                        if("Broker.BrokerResponeProcessor".equals(responeProcessor.getClass().getName()))
                        	responeProcessor.processorRespone(key,broker);
                        else if("Consumer.ConsumerResponeProcessor".equals(responeProcessor.getClass().getName())) {
//                        	System.out.println("here");
                        	responeProcessor.processorRespone(key,port);
                        }
                        	
                        else
                        	responeProcessor.processorRespone(key);
                    }
                }
            }else{
                synchronized (writeQueen) {
                    while(writeQueen.size() > 0){
                        SelectionKey key = writeQueen.remove(0);
                        //ע��д�¼�
                        SocketChannel channel = (SocketChannel) key.channel();
                        Object attachment = key.attachment();
                        channel.register(selector, SelectionKey.OP_WRITE,attachment);
                    }
                }
            }
        }
    }
}