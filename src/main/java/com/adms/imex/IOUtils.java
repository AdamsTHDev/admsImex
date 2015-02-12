/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOUtils {

	public static String readFileContent(String fileName)
			throws IOException
	{
		return readFileContent(fileName, null);
	}

	public static String readFileContent(String fileName, String charset)
			throws IOException
	{
		File f = new File(fileName);

		return readFileContent(f, charset);
	}

	public static String readFileContent(File f)
			throws IOException
	{
		return readFileContent(f, null);
	}

	public static String readFileContent(File f, String charset)
			throws IOException
	{

		Reader reader;
		if (null == charset)
		{
			reader = new BufferedReader(new FileReader(f));
		}
		else
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
		}

		try
		{
			CharBuffer buffer = CharBuffer.allocate((int) f.length());

			int cnt = reader.read(buffer);
			buffer.position(0);

			return buffer.subSequence(0, cnt).toString();
		}
		finally
		{
			IOUtils.close(reader);
		}
	}

	public static String readContent(InputStream in)
			throws IOException
	{
		Reader reader = new InputStreamReader(in);

		return readContent(reader);
	}

	public static String readContent(InputStream in, String charset)
			throws IOException
	{
		Reader reader = new InputStreamReader(in, charset);

		return readContent(reader);
	}

	public static String readContent(Reader reader)
			throws IOException
	{
		try
		{
			StringBuilder buffer = new StringBuilder();
			int size = 4096;
			int cnt;
			char[] data = new char[size];

			while (0 < (cnt = reader.read(data)))
			{
				if (cnt < size)
				{
					buffer.append(data, 0, cnt);
				}
				else
				{
					buffer.append(data);
				}
			}

			return buffer.toString();
		}
		finally
		{
			IOUtils.close(reader);
		}
	}

	public static void writeFileContexnt(String fileName, String content)
			throws IOException
	{
		writeFileContexnt(fileName, content, null);
	}

	public static void writeFileContexnt(String fileName, String content, String charset)
			throws IOException
	{
		File file = new File(fileName);

		writeFileContexnt(file, content, charset);
	}

	public static void writeFileContexnt(File file, String content)
			throws IOException
	{
		writeFileContexnt(file, content, null);
	}

	public static void writeFileContexnt(File file, String content, String charset)
			throws IOException
	{
		Writer writer;
		if (null != charset)
		{
			writer = new OutputStreamWriter(new FileOutputStream(file), charset);
		}
		else
		{
			writer = new FileWriter(file);
		}

		try
		{
			writer.write(content);
			writer.flush();
		}
		finally
		{
			IOUtils.close(writer);
		}
	}

	public static void close(Closeable io)
	{
		if (null != io)
		{
			try
			{
				io.close();
			}
			catch (Exception ignore)
			{
				Logger.getLogger(IOUtils.class.getName()).log(Level.WARNING, ignore.getLocalizedMessage(), ignore);
			}
		}
	}

	public static void copyFile(String src, String dest)
			throws IOException
	{
		copyFile(new File(src), new File(dest));
	}

	public static void copyFile(File src, File dest)
			throws IOException
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel srcCh = null;
		FileChannel destCh = null;
		try
		{
			createPaths(dest);
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			
			srcCh = fis.getChannel();
			destCh = fos.getChannel();

			destCh.lock();

			ByteBuffer buffer = srcCh.map(MapMode.READ_ONLY, 0, src.length());

			destCh.write(buffer);
		}
		finally
		{
			IOUtils.close(srcCh);
			IOUtils.close(destCh);
			fis.close();
			fos.close();
		}
	}

	public static void writeStreamData(String file, InputStream in)
			throws IOException
	{
		writeStreamData(new File(file), in);
	}

	public static void writeStreamData(File file, InputStream in)
			throws IOException
	{

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		try
		{
			final int bufferSize = 4096;
			byte[] buffer = new byte[bufferSize];
			int cnt;

			while (0 < (cnt = in.read(buffer)))
			{
				out.write(buffer, 0, cnt);
			}

			out.flush();
		}
		finally
		{
			IOUtils.close(in);
			IOUtils.close(out);
		}
	}

	private static void createPaths(File dest)
	{
		File path = dest.getParentFile();

		if (null != path)
		{
			path.mkdirs();
		}
	}

}