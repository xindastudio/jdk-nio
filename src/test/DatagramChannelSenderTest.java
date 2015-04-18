package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class DatagramChannelSenderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatagramChannel sender = null;
		Selector sel = null;
		try {
			sender = DatagramChannel.open();
			sender.configureBlocking(false);
			sender.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9999));
			sel = SelectorProvider.provider().openSelector();
			sender.register(sel, SelectionKey.OP_WRITE);
			boolean isEnd = false;
			long timeout = 5000;
			int selCount = 0;
			Iterator<SelectionKey> itr = null;
			SelectionKey key = null;
			String msg = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (!isEnd) {
				selCount = sel.select(timeout);
				if (selCount > 0) {
					itr = sel.selectedKeys().iterator();
					while (itr.hasNext()) {
						key = itr.next();
						itr.remove();
						if (key.isWritable()) {
							msg = br.readLine();
							isEnd = "bye".equals(msg);
							msg = msg + "\n";
							sender.write(ByteBuffer.wrap(msg.getBytes()));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(sender);
			close(sel);
		}
		System.out.println("receiver end...");
	}
	
	public static void close(DatagramChannel dc) {
		if (null != dc) {
			try {
				dc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Selector sel) {
		if (null != sel) {
			try {
				sel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
