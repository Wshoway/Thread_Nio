package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import TcpServer.ServerImpl;
//��ȡ���ӵ�Socket
public class GetSocketThread extends Thread {
	final private ServerImpl si;
	final private ServerSocket ss;
	public GetSocketThread(ServerSocket ss,ServerImpl si){
		this.ss=ss;
		this.si=si;
	}
	@Override
	public void run() {
		// TODO �Զ����ɵķ������
		super.run();
		while(true){
			Socket accept = null;
			try {
				accept = ss.accept();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			if(accept!=null){
				si.startAcceptThread(accept);
			}
		}
	}
}
