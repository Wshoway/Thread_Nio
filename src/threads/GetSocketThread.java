package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import TcpServer.ServerImpl;
//获取连接的Socket
public class GetSocketThread extends Thread {
	final private ServerImpl si;
	final private ServerSocket ss;
	public GetSocketThread(ServerSocket ss,ServerImpl si){
		this.ss=ss;
		this.si=si;
	}
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		while(true){
			Socket accept = null;
			try {
				accept = ss.accept();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			if(accept!=null){
				si.startAcceptThread(accept);
			}
		}
	}
}
