/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract class BaseWriter implements BatchWriter {
	private String outputFile;

	private String encoding;
	private Writer writer;
	private Map<String, Class<?>> typeMap;

	public BaseWriter()
	{
	}

	protected void disposeWriter(Closeable writer)
	{
		if (null != writer)
		{
			try
			{
				writer.close();
			}
			catch (Exception info)
			{
				info.printStackTrace(System.out);
			}
			finally
			{
				writer = null;
			}
		}
	}

	protected void disposeResultSet(ResultSet rs)
	{
		if (null != rs)
		{
			try
			{
				rs.close();
			}
			catch (Exception ignore)
			{
				// Do nothing
			}
		}
	}

	public String getEncoding()
	{
		return encoding;
	}

	public String getFile()
	{
		return outputFile;
	}

	public Map<String, Class<?>> getTypeMap()
	{
		return typeMap;
	}

	public Writer getWriter()
	{
		return writer;
	}

	protected Writer prepareWriter()
			throws UnsupportedEncodingException, FileNotFoundException
	{
		if (null != writer)
		{
			return writer;
		}
		else
		{
			return new OutputStreamWriter(new FileOutputStream(getFile()), getEncoding());
		}
	}

	protected OutputStream prepareStream()
			throws UnsupportedEncodingException, FileNotFoundException
	{
		return new FileOutputStream(getFile());
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public void setFile(String outputFile)
	{
		this.outputFile = outputFile;
	}

	public void setTypeMap(Map<String, Class<?>> typeMap)
	{
		this.typeMap = typeMap;
	}

	public void setWriter(Writer writer)
	{
		this.writer = writer;
	}

	protected DataRecord extractDataRecord(ResultSet rs)
			throws SQLException
	{
		return new RIDataRecord(rs, getTypeMap());
	}

	protected void writeString(Writer writer, String string)
			throws IOException
	{
		writer.write(string);
	}

	public boolean isUpdatable()
	{
		return false;
	}
}
