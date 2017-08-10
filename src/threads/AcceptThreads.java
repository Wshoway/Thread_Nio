package threads;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

//����socket���͵���Ϣ
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
		// TODO �Զ����ɵķ������
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
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
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		//����Ϊ������Ϣ��
		SocketChannel channel = socket.getChannel();
		synchronized(selector){
			try {
				//ע�ᵽselector
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_WRITE);
			} catch (ClosedChannelException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			//�����߳�	
		}
		synchronized(taskMap){
			//��ӵ�����map
			taskMap.put(channel, rt);
			taskMap.notifyAll();
		}
	}

}
