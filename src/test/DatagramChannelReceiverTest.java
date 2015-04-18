package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class DatagramChannelReceiverTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatagramChannel recv = null;
		Selector sel = null;
		try {
			recv = DatagramChannel.open();
			recv.configureBlocking(false);
			recv.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), 9999));
			sel = SelectorProvider.provider().openSelector();
			recv.register(sel, SelectionKey.OP_READ);
			boolean isEnd = false;
			long timeout = 5000;
			int selCount = 0;
			Iterator<SelectionKey> itr = null;
			SelectionKey key = null;
			String msg = null;
			ByteBuffer bb = ByteBuffer.allocate(1024);
			while (!isEnd) {
				selCount = sel.select(timeout);
				if (selCount > 0) {
					itr = sel.selectedKeys().iterator();
					while (itr.hasNext()) {
						key = itr.next();
						itr.remove();
						if (key.isReadable()) {
							recv.receive(bb);
							bb.flip();
							msg = new String(bb.array(), 0, bb.limit());
							isEnd = msg.startsWith("bye");
							System.out.print("receive from client : " + msg);
							bb.clear();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(recv);
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
