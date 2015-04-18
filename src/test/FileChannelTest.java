package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class FileChannelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//copyFile2("F:/��Ҫѧϰ.txt", "F:/temp.txt");
		test();
	}
	
	public static void test() {
		FileChannel fc = null;
		ByteBuffer bb = null;
		FileChannel des = null;
		try {
			fc = new FileInputStream("F:/��Ҫѧϰ.txt").getChannel();
			bb = null;
			int len = 0;
			long position = len;
			/*
			bb = ByteBuffer.allocate(16);
			fc.read(bb);
			System.out.println(bb.toString());
			//*/
			/*ֱ����ByteBuffer�������������,��Ϊ���Ļᱻ�ض�
			bb = ByteBuffer.allocate(16);
			while (-1 != (len = fc.read(bb, position))) {
				bb.flip();
				//System.out.println("\n[" + len + "][" + bb.position() + "][" + bb.limit() + "][" + position + "]");
				System.out.print(new String(bb.array(), 0, bb.limit()));
				bb.clear();
				position += len;
			}
			//*/
			/*
			bb = ByteBuffer.allocate(16);
			List<Byte> l = new ArrayList<Byte>();
			while (-1 != (len = fc.read(bb, position))) {
				bb.flip();
				//System.out.println("\n[" + len + "][" + bb.position() + "][" + bb.limit() + "][" + position + "]");
				for (int i = 0; i < bb.limit(); i++) {
					l.add(bb.get());
				}
				bb.clear();
				position += len;
			}
			if (l.size() > 0) {
				byte[] temp = new byte[l.size()];
				for (int i = 0; i < l.size(); i++) {
					temp[i] = l.get(i);
				}
				System.out.print(new String(temp));
			}
			//*/
			/*
			System.out.println("\nsize : " + fc.size());
			System.out.println("\nposition : " + position);
			//*/
			//*ͨ��Charset������ı��ضϳ������������
			File desFile = new File("F:/temp.txt");
			if (!desFile.exists() || !desFile.isFile()) {
				if (!desFile.createNewFile()) {
					return;
				}
			}
			des = new FileOutputStream(desFile).getChannel();
			
			bb = ByteBuffer.allocate(16);
			CharBuffer cb = CharBuffer.allocate(16);
			Charset charset = Charset.forName("GBK");
			CharsetDecoder decoder = charset.newDecoder();
			CoderResult cr = null;
			
			CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
			ByteBuffer dst = ByteBuffer.allocate(8);
			boolean endOfInput = false;
			while (-1 != (len = fc.read(bb, position))) {
				bb.flip();
				endOfInput = bb.limit() < bb.capacity();
				if ((cr = decoder.decode(bb, cb, endOfInput)).isError()) {
					cr.throwException();
				}
				bb.compact();//�ܹؼ���һ��
				cb.flip();
				//System.out.print(new String(cb.array(), 0, cb.limit()));
				do {
					if ((cr = encoder.encode(cb, dst, endOfInput)).isError()) {
						cr.throwException();
					}
					dst.flip();
					des.write(dst);
					dst.clear();
				} while (cr.isOverflow());
				cb.clear();
				position += len;
			}
			//*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(fc);
			close(des);
		}
	}
	
	public static void close(FileChannel fc) {
		if (null != fc) {
			try {
				fc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void copyFile1(String srcFilePath, String desFilePath) {
		FileChannel src = null;
		FileChannel des = null;
		try {
			File srcFile = new File(srcFilePath), desFile = new File(desFilePath);
			if (!srcFile.exists() || !srcFile.isFile()) {
				return;
			}
			if (!desFile.exists() || !desFile.isFile()) {
				if (!desFile.createNewFile()) {
					return;
				}
			}
			src = new FileInputStream(srcFile).getChannel();
			des = new FileOutputStream(desFile).getChannel();
			long startTime = System.currentTimeMillis();
			//����Ŀռ侢�����У��Դ��ļ��������������ٶ�д����
			ByteBuffer dst = ByteBuffer.allocate(5028048);
			int len = 0;
			long position = 0;
			while (-1 != (len = src.read(dst, position))) {
				position += len;
				dst.flip();
				des.write(dst);
				dst.clear();
			}
			des.force(true);
			System.out.println("used time : " + (System.currentTimeMillis() - startTime));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(src);
			close(des);
		}
	}
	
	/**
	 * ����2�ȷ���1Ч���Ը�
	 * @param srcFilePath
	 * @param desFilePath
	 */
	public static void copyFile2(String srcFilePath, String desFilePath) {
		FileChannel src = null;
		FileChannel des = null;
		try {
			File srcFile = new File(srcFilePath), desFile = new File(desFilePath);
			if (!srcFile.exists() || !srcFile.isFile()) {
				return;
			}
			if (!desFile.exists() || !desFile.isFile()) {
				if (!desFile.createNewFile()) {
					return;
				}
			}
			src = new FileInputStream(srcFile).getChannel();
			des = new FileOutputStream(desFile).getChannel();
			long startTime = System.currentTimeMillis();
			MappedByteBuffer dst = src.map(MapMode.READ_ONLY, 0, src.size());
			des.write(dst);
			des.force(true);
			System.out.println("used time : " + (System.currentTimeMillis() - startTime));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(src);
			close(des);
		}
	}

}
