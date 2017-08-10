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
	//发送线程
	private SendThread sendThread;
	//接收线程
	private GetSocketThread gst;
	//线程池，固定线程池最大数量10
	private ExecutorService acceptThreadPool = Executors.newFixedThreadPool(10); 
	private ServerSocket serversocket;
	private boolean open=false;
	//私有构造函数
	private ServerImpl(){
		taskMap = new HashMap<SocketChannel,String>();
		try {
			selector = Selector.open();
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(80));
			serversocket = ssc.socket();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		gst=new GetSocketThread(serversocket,this);
		sendThread = new SendThread(taskMap, selector);	
	}
	//单例模式
	static public ServerImpl getServer(){
		if(si==null){
			si = new ServerImpl();
		}
		return si;
	}
	//开始接收
	public void go(){
		//开启接收
		if(open){
			return;
		}
		gst.start();
		sendThread.start();
		open=true;
	}
	//结束
	public void stop(){
		//关闭接收
		open=false;
		acceptThreadPool.shutdown();
		gst.interrupt();
		sendThread.interrupt();;
	}
	//开启接收线程，使用线程池
	public void startAcceptThread(Socket socket){
		acceptThreadPool.execute(new AcceptThreads(taskMap, selector, socket));
	}
	
}
