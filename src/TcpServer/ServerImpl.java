package TcpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import threads.AcceptThreads;
import threads.GetSocketThread;
import threads.SendThread;

public class ServerImpl {
	private Map<SocketChannel,String> taskMap;
	private Selector selector;
	private static ServerImpl si;
	//�����߳�
	private SendThread sendThread;
	//�����߳�
	private GetSocketThread gst;
	//�̳߳أ��̶��̳߳��������10
	private ExecutorService acceptThreadPool = Executors.newFixedThreadPool(10); 
	private ServerSocket serversocket;
	private boolean open=false;
	//˽�й��캯��
	private ServerImpl(){
		taskMap = new HashMap<SocketChannel,String>();
		try {
			selector = Selector.open();
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(80));
			serversocket = ssc.socket();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		gst=new GetSocketThread(serversocket,this);
		sendThread = new SendThread(taskMap, selector);	
	}
	//����ģʽ
	static public ServerImpl getServer(){
		if(si==null){
			si = new ServerImpl();
		}
		return si;
	}
	//��ʼ����
	public void go(){
		//��������
		if(open){
			return;
		}
		gst.start();
		sendThread.start();
		open=true;
	}
	//����
	public void stop(){
		//�رս���
		open=false;
		acceptThreadPool.shutdown();
		gst.interrupt();
		sendThread.interrupt();;
	}
	//���������̣߳�ʹ���̳߳�
	public void startAcceptThread(Socket socket){
		acceptThreadPool.execute(new AcceptThreads(taskMap, selector, socket));
	}
	
}
