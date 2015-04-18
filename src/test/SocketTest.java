package test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest extends Thread {
	public static volatile boolean end = false;
	public static StringBuilder msg = new StringBuilder();
	
	public void run() {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(System.in));
		String temp = null;
		boolean flag = true;
		try {
			while (flag) {
				temp = br.readLine();
				flag = !"bye".equals(temp);
				end = !flag;
				synchronized (msg) {
					msg.append(temp);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SocketTest().start();
		test();
	}
	
	public static void test() {
		Socket soc = null;
		BufferedOutputStream bos = null;
		try {
			soc = new Socket(InetAddress.getLocalHost(), 9998);
			bos = new BufferedOutputStream(soc.getOutputStream());
			while (!end) {
				synchronized (msg) {
					if (msg.length() > 0) {
						bos.write(msg.toString().getBytes());
					}
					msg.setLength(0);
				}
				bos.flush();
				try { Thread.sleep(1000); } catch (Exception e) { }
			}
			synchronized (msg) {
				if (msg.length() > 0) {
					bos.write(msg.toString().getBytes());
				}
				msg.setLength(0);
			}
			bos.flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(soc);
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
