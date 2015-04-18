package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class ServerSocketChannelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocketChannel ssc = null;
		Selector sel = null;
		try {
			sel = SelectorProvider.provider().openSelector();
			
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), 9999));
			
			ssc.register(sel, SelectionKey.OP_ACCEPT);
			
			Iterator<SelectionKey> itr = null;
			SelectionKey selKey = null;
			long timeout = 5000;
			int timeoutCount = 0;
			int selCount = 0;
			while (timeoutCount < 4) {
				selCount = sel.select(timeout);
				if (selCount > 0) {
					itr = sel.selectedKeys().iterator();
					while (itr.hasNext()) {
						selKey = itr.next();
						itr.remove();
						if (selKey.isAcceptable()) {
							new SocketChannelTest(((ServerSocketChannel)selKey.channel()).accept()).start();
						}
					}
					timeoutCount = 0;
				} else {
					timeoutCount++;
					//System.out.println("select timeout " + timeoutCount + " ...");
				}
			}
			//System.out.println("main server end...");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(ssc);
			close(sel);
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
	
	public static void close(ServerSocketChannel ssc) {
		if (null != ssc) {
			try {
				ssc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
