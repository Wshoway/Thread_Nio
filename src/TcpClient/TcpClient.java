package TcpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
//测试发送msg并接收
public class TcpClient extends Thread{
	private int num;
	TcpClient(int num){
		this.num=num;
	}
	public static void main(String[] args) throws IOException, IOException{
		for(int i=0;i<10000;i++){
			TcpClient tcpClient = new TcpClient(i);
			tcpClient.start();
		}
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		try {
			runs("this is NO."+num+" Thread");
		} catch (UnknownHostException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static void runs(String msg) throws IOException, UnknownHostException {
		Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 80);
	
		OutputStream outputstream =socket.getOutputStream();
		InputStream inputstream =socket.getInputStream();
		outputstream.write(msg.getBytes());
		
		byte[] b=new byte[1024];
		int n;
		
		while((n=(inputstream.read(b)))!=-1){
			System.out.println(new String(b,0,n));
		}
		
		socket.close();
	}
}
