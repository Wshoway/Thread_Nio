package threads;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

//接收socket发送的信息
public class AcceptThreads implements Runnable{
	final private Map<SocketChannel,String> taskMap;
	final private Selector selector;
	final private Socket socket;
	
	public AcceptThreads(Map<SocketChannel,String> taskMap,Selector selector,Socket socket){
		this.taskMap = taskMap;
		this.selector = selector;
		this.socket = socket;
	}
	
	public void run() {
		// TODO 自动生成的方法存根
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		byte[] bytes = new byte[1024];
		int i;
		String rt="";
		try {
			i = inputStream.read(bytes);
			rt=new String(bytes,0,i);
			System.out.println(rt);
			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		//以上为接入信息。
		SocketChannel channel = socket.getChannel();
		synchronized(selector){
			try {
				//注册到selector
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_WRITE);
			} catch (ClosedChannelException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			//唤醒线程	
		}
		synchronized(taskMap){
			//添加到任务map
			taskMap.put(channel, rt);
			taskMap.notifyAll();
		}
	}

}
