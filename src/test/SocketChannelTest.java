package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketChannelTest extends Thread {
	private SocketChannel sc = null;
	private InetSocketAddress ip = null;
	private ByteBuffer bb = null;
	private boolean isClient = false;
	private ConcurrentLinkedQueue<String> msgQueue = null;
	private volatile boolean isEnd = false;
	
	public SocketChannelTest(SocketChannel sc) {
		this.sc = sc;
		this.setDaemon(false);
		msgQueue = new ConcurrentLinkedQueue<String>();
	}
	
	public SocketChannelTest(SocketChannel sc, InetSocketAddress ip) {
		this.sc = sc;
		this.ip = ip;
		isClient = true;
		this.setDaemon(false);
		msgQueue = new ConcurrentLinkedQueue<String>();
	}
	
	public void addMsg(String msg) {
		msgQueue.add(msg);
		isEnd = msg.toUpperCase().startsWith("BYE");
	}
	
	private String getMsg() {
		return msgQueue.poll();
	}
	
	/** (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Selector sel = null;
		try {
			sel = SelectorProvider.provider().openSelector();
			sc.configureBlocking(false);
			if (isClient) {
				sc.register(sel, SelectionKey.OP_CONNECT);
				sc.connect(ip);
			} else {
				sc.register(sel, SelectionKey.OP_READ);
			}
			long timeout = 2000;
			int selCount = 0;
			SelectionKey key = null;
			Iterator<SelectionKey> itr = null;
			bb = ByteBuffer.allocate(15);
			int readCount = 0;
			while (!isEnd) {
				selCount = sel.select(timeout);
				if (selCount > 0) {
					itr = sel.selectedKeys().iterator();
					while (itr.hasNext()) {
						key = itr.next();
						itr.remove();
						//key是可以同时可读可写等等
						if (key.isConnectable()) {
							((SocketChannel)key.channel()).finishConnect();
							//System.out.println("connection...");
						}
						if (key.isReadable()) {
							readCount = readMsg((SocketChannel)key.channel());
							isEnd = -1 == readCount;
						}
						if (key.isWritable()) {
							writeMsg((SocketChannel)key.channel());
						}
						/*if (key.isReadable() || key.isWritable()) {
							if (msgQueue.isEmpty()) {
								key.interestOps(SelectionKey.OP_READ);
							} else {
								key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
							}
						}*/
					}
				}
				if ((isClient && sc.isConnected()) || !isClient) {
					if (msgQueue.isEmpty()) {
						sc.register(sel, SelectionKey.OP_READ);
						//System.out.println("can read...");
					} else {
						sc.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						//System.out.println("can read and write...");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(sc);
			close(sel);
		}
		System.out.println((isClient ? "client " : "server ") + "end...");
	}
	
	private int readMsg(SocketChannel sc) throws IOException {
		int readCount = 0;
		
		readCount = sc.read(bb);
		if (readCount > 0) {
			bb.flip();
			CharsetDecoder decoder = Charset.forName("GBK").newDecoder();
			CharBuffer cb = CharBuffer.allocate(9);
			boolean endOfInput = false;
			CoderResult cr = null;
			StringBuilder msg = new StringBuilder();
			do {
				if ((cr = decoder.decode(bb, cb, endOfInput)).isError()) {
					cr.throwException();
				}
				cb.flip();
				msg.append(cb.toString());
				cb.clear();
			} while (cr.isOverflow());
			System.out.print("from " + (isClient ? "server " : "client " ) + sc.socket().getRemoteSocketAddress().toString() + " : " + msg.toString());
			if (!isClient) {
				msgQueue.add(msg.toString());
			}
			bb.compact();
		}
		
		return readCount;
	}
	
	private void writeMsg(SocketChannel sc) throws IOException {
		String msg = getMsg();
		if (null != msg) {
			sc.write(ByteBuffer.wrap(msg.getBytes()));
		}
		//System.out.println("write msg...");
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
	
	public static void close(SocketChannel sc) {
		if (null != sc) {
			try {
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		SocketChannelTest client = new SocketChannelTest(SocketChannel.open(), new InetSocketAddress(InetAddress.getLocalHost(), 9999));
		client.start();
		BufferedReader br = null;
		String msg = null;
		while (client.isAlive()) {
			br = new BufferedReader(new InputStreamReader(System.in));
			try {
				msg = br.readLine();
				client.addMsg(msg + "\n");
				if ("BYE".equals(msg.toUpperCase())) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
