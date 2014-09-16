/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract class BaseReader implements BatchReader {
	private String inputFile;
	private String encoding;
	private InputStream stream;
	private Reader reader;
	private Map<String, Class<?>> typeMap;

	public String getEncoding()
	{
		return encoding;
	}

	public String getFile()
	{
		return inputFile;
	}

	public InputStream getStream()
	{
		return stream;
	}

	public void setStream(InputStream stream)
	{
		this.stream = stream;
	}

	public Reader getReader()
	{
		return reader;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public void setFile(String inputFile)
	{
		this.inputFile = inputFile;
	}

	public void setReader(Reader reader)
	{
		this.reader = reader;
	}

	protected void disposeReader(Closeable writer)
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

	protected Reader prepareReader()
			throws UnsupportedEncodingException, FileNotFoundException
	{
		if (null != reader)
		{
			return reader;
		}
		else if (null != stream)
		{
			return new InputStreamReader(stream);
		}
		else
		{
			return new InputStreamReader(new FileInputStream(getFile()), getEncoding());
		}
	}

	protected InputStream prepareStream()
			throws UnsupportedEncodingException, FileNotFoundException
	{
		if (null != stream)
		{
			return stream;
		}
		else
		{
			return new FileInputStream(getFile());
		}
	}

	protected DataRecord extractDataRecord(ResultSet rs)
			throws SQLException
	{
		return new RIDataRecord(rs, getTypeMap());
	}

	/**
	 * @return the typeMap
	 */
	public Map<String, Class<?>> getTypeMap()
	{
		return typeMap;
	}

	/**
	 * @param typeMap
	 *            the typeMap to set
	 */
	public void setTypeMap(Map<String, Class<?>> typeMap)
	{
		this.typeMap = typeMap;
	}

}