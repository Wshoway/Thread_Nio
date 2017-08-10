package threads;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
//ʹ��NIO�ش�����
public class SendThread extends Thread{
	//���ѡ������map
	final private Map<SocketChannel,String> taskMap;
	final private Selector selector;
	
	public SendThread(Map<SocketChannel,String> taskMap,Selector selector){
		this.taskMap = taskMap;
		this.selector = selector;
	}
	
	//���Ƿ�������
	public void run() {
		// TODO �Զ����ɵķ������
		super.run();
		Set<SelectionKey> selectedKeys;
		while(true){
			//��û��������ȴ�����
			synchronized(taskMap){
				if(taskMap.isEmpty()){
					try {
						System.out.println("STOP!!!!");
						taskMap.wait();
					} catch (InterruptedException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			//��ȡ���о�����
			int n=0;
			synchronized(selector){
				try {
					n = selector.select();
				} catch (IOException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}	
			}
			//���û����ô�ͼ���
			if(n>0){
				selectedKeys = selector.selectedKeys();
			}else{
				try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
				continue;
			}
			//���ҿɷ��͵Ĳ�����
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				if(key.isWritable()){
					
					SocketChannel channel =(SocketChannel)key.channel();
					//���淢�͸������һ�һ����
					String msg =null;
					//���̰߳�ȫ���������Ҫ��Ȼ�������
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
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
					}
					iterator.remove();
					try {
						key.cancel();
						channel.close();
						channel.socket().close();
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
		}
	}
}
