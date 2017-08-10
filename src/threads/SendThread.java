package threads;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
//使用NIO回传数据
public class SendThread extends Thread{
	//组合选择器和map
	final private Map<SocketChannel,String> taskMap;
	final private Selector selector;
	
	public SendThread(Map<SocketChannel,String> taskMap,Selector selector){
		this.taskMap = taskMap;
		this.selector = selector;
	}
	
	//就是发送数据
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		Set<SelectionKey> selectedKeys;
		while(true){
			//若没有任务，则等待唤醒
			synchronized(taskMap){
				if(taskMap.isEmpty()){
					try {
						System.out.println("STOP!!!!");
						taskMap.wait();
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			//获取所有就绪项
			int n=0;
			synchronized(selector){
				try {
					n = selector.select();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}	
			}
			//如果没有那么就继续
			if(n>0){
				selectedKeys = selector.selectedKeys();
			}else{
				try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				continue;
			}
			//查找可发送的并发送
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				if(key.isWritable()){
					
					SocketChannel channel =(SocketChannel)key.channel();
					//对面发送个请求我回一个。
					String msg =null;
					//非线程安全必须用这个要不然会出事情
					synchronized(taskMap){
						msg = taskMap.get(channel);
						taskMap.remove(channel);
					}
				
					ByteBuffer bf = ByteBuffer.allocate(1024);
					bf.clear();
					bf.put(msg.getBytes());
					bf.flip();
					while(bf.hasRemaining()){
						try {
							channel.write(bf);
						} catch (IOException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
					iterator.remove();
					try {
						key.cancel();
						channel.close();
						channel.socket().close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
		}
	}
}
