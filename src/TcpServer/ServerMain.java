package TcpServer;

import java.io.IOException;
//���ԣ����� ������
public class ServerMain extends Thread{
	public static void main(String[] args) throws IOException, IOException{
		ServerImpl si = ServerImpl.getServer();
		si.go();
	}
}
