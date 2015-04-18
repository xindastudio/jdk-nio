package test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	
	public static void test() {
		ServerSocket ss = null;
		Socket soc = null;
		BufferedInputStream bis = null;
		try {
			ss = new ServerSocket();
			ss.setReuseAddress(true);
			ss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 9998), 1);
			soc = ss.accept();
			bis = new BufferedInputStream(soc.getInputStream());
			byte[] b = new byte[1024];
			int len = 0;
			while (-1 != (len = bis.read(b))) {
				System.out.println(new String(b, 0, len));
			}
			System.out.println("end...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(ss);
			close(soc);
		}
	}
	
	public static void close(ServerSocket ss) {
		if (null != ss) {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Socket soc) {
		if (null != soc) {
			try {
				soc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
