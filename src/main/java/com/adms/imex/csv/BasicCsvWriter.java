/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.csv;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.adms.imex.BaseWriter;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;

public class BasicCsvWriter extends BaseWriter {

	private String headerWrapString;
	private String valueWrapString;
	private String separator = ",";
	private String newLine = "\r\n";

	public void write(ResultSet rs)
			throws BatchIOException
	{
		Writer aWriter = null;
		try
		{
			aWriter = prepareWriter();

			writeHeader(rs, aWriter);

			while (rs.next())
			{
				DataRecord record = extractDataRecord(rs);

				writeDataRecord(record, aWriter);
			}

			writeFooter(rs, aWriter);
		}
		catch (Exception ex)
		{
			throw new BatchIOException(ex);
		}
		finally
		{
			disposeWriter(aWriter);
			disposeResultSet(rs);
		}
	}

	public void write(java.lang.Iterable<com.adms.imex.DataRecord> records)
			throws BatchIOException
	{
		Writer aWriter = null;
		try
		{
			aWriter = prepareWriter();

			boolean first = true;
			DataRecord firstRecord = null;

			for (DataRecord record : records)
			{
				if (first)
				{
					first = false;
					firstRecord = record;
					writerHeader(record, aWriter);
				}

				writeDataRecord(record, aWriter);
			}

			writeFooter(firstRecord, aWriter);
		}
		catch (Exception ex)
		{
			throw new BatchIOException(ex);
		}
		finally
		{
			disposeWriter(aWriter);
		}
	}

	protected void writeHeader(ResultSet rs, Writer aWriter)
			throws SQLException, IOException
	{
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < meta.getColumnCount(); ++i)
		{
			int colIdx = i + 1;
			String colName = meta.getColumnName(colIdx);

			if (buffer.length() > 0)
			{
				buffer.append(separator);
			}

			appendValue(buffer, colName, headerWrapString);
		}

		if (buffer.length() > 0)
		{
			buffer.append(newLine);
			writeString(aWriter, buffer.toString());
		}
	}

	protected void writerHeader(DataRecord record, Writer aWriter)
			throws IOException
	{
		StringBuilder buffer = new StringBuilder();

		for (String fieldName : record.getFieldNames())
		{
			if (buffer.length() > 0)
			{
				buffer.append(separator);
			}

			appendValue(buffer, fieldName, headerWrapString);
		}

		if (buffer.length() > 0)
		{
			buffer.append(newLine);
			writeString(aWriter, buffer.toString());
		}
	}

	protected void writeDataRecord(DataRecord record, Writer aWriter)
			throws BatchIOException, IOException
	{
		StringBuilder buffer = new StringBuilder();

		for (String fieldName : record.getFieldNames())
		{
			Object value = record.getFieldValue(fieldName);

			if (buffer.length() > 0)
			{
				buffer.append(separator);
			}

			if (null != value)
			{
				appendValue(buffer, value.toString(), valueWrapString);
			}
		}

		if (buffer.length() > 0)
		{
			buffer.append(newLine);
			writeString(aWriter, buffer.toString());
		}
	}

	protected void writeFooter(ResultSet rs, Writer aWriter)
	{
		// Do nothing
	}

	protected void writeFooter(DataRecord firstRecord, Writer aWriter)
	{
		// Do nothing
	}

	/**
	 * @return the valueWrapString
	 */
	public String getValueWrapString()
	{
		return valueWrapString;
	}

	/**
	 * @param valueWrapString
	 *            the valueWrapString to set
	 */
	public void setValueWrapString(String valueWrapString)
	{
		this.valueWrapString = valueWrapString;
	}

	/**
	 * @return the headerWrapString
	 */
	public String getHeaderWrapString()
	{
		return headerWrapString;
	}

	/**
	 * @param headerWrapString
	 *            the headerWrapString to set
	 */
	public void setHeaderWrapString(String headerWrapString)
	{
		this.headerWrapString = headerWrapString;
	}

	protected void appendValue(StringBuilder buffer, String value, String sign)
	{
		if (null != sign)
		{
			buffer.append(sign);
		}

		buffer.append(value);

		if (null != sign)
		{
			buffer.append(sign);
		}
	}

	/**
	 * @return the separator
	 */
	public String getSeparator()
	{
		return separator;
	}

	/**
	 * @param separator
	 *            the separator to set
	 */
	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	/**
	 * @return the newLine
	 */
	public String getNewLine()
	{
		return newLine;
	}

	/**
	 * @param newLine
	 *            the newLine to set
	 */
	public void setNewLine(String newLine)
	{
		this.newLine = newLine;
	}

}