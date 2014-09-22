/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.adms.imex.DataRecord;
import com.adms.imex.RIDataRecord;

public class CsvDataRecordIterable implements Iterable<DataRecord> {

	private final BufferedReader reader;
	private final CsvDataRecordIterator iterator;

	private boolean firstRowAsColumnNames;
	private int numOfSkippedLine;
	private String headerWrapString;
	private String valueWrapString;
	private String separator = ",";

	private boolean eof;
	private int skipped;
	private String[] columnNames;

	public CsvDataRecordIterable(Reader reader)
	{
		if (reader instanceof BufferedReader)
		{
			this.reader = (BufferedReader) reader;
		}
		else
		{
			this.reader = new BufferedReader(reader);
		}

		iterator = new CsvDataRecordIterator();
	}

	public void close()
	{
		if (!eof)
		{
			eof = true;

			if (null != reader)
			{
				try
				{
					reader.close();
				}
				catch (Exception ignore)
				{
					// Do nothing
				}
			}
		}
	}

	public Iterator<DataRecord> iterator()
	{
		return iterator;
	}

	/**
	 * @return the reader
	 */
	public Reader getReader()
	{
		return reader;
	}

	/**
	 * @return the eof
	 */
	public boolean isEof()
	{
		return eof;
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
	 * @return the firstNameAsColumnName
	 */
	public boolean isFirstRowAsColumnNames()
	{
		return firstRowAsColumnNames;
	}

	/**
	 * @param firstNameAsColumnName
	 *            the firstNameAsColumnName to set
	 */
	public void setFirstRowAsColumnNames(boolean firstNameAsColumnName)
	{
		this.firstRowAsColumnNames = firstNameAsColumnName;
	}

	/**
	 * @return the numOfSkippedLine
	 */
	public int getNumOfSkippedLine()
	{
		return numOfSkippedLine;
	}

	/**
	 * @param numOfSkippedLine
	 *            the numOfSkippedLine to set
	 */
	public void setNumOfSkippedLine(int numOfSkippedLine)
	{
		this.numOfSkippedLine = numOfSkippedLine;
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

	protected class CsvDataRecordIterator implements Iterator<DataRecord> {
		private DataRecord record;

		public boolean hasNext()
		{
			if (null == record && !eof)
			{
				try
				{
					while (skipped < numOfSkippedLine)
					{
						++skipped;
						String line = reader.readLine();

						if (null == line)
						{
							close();
							return false;
						}
					}

					if (null == columnNames && firstRowAsColumnNames)
					{
						String line = reader.readLine();

						if (null == line)
						{
							close();
							return false;
						}

						columnNames = extractColumnNames(line);
					}

					record = extractDataRecord(reader);
				}
				catch (IOException ex)
				{
					throw new RuntimeException(ex);
				}
			}

			// FALL THROUGH
			return (null != record);
		}

		public DataRecord next()
		{
			try
			{
				if (hasNext())
				{
					return record;
				}
				else
				{
					throw new NoSuchElementException();
				}
			}
			finally
			{
				record = null;
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		protected DataRecord extractDataRecord(BufferedReader reader)
				throws IOException
		{
			String line = reader.readLine();
			if (null != line)
			{
				String[] columnValues = extractColumnValues(line);

				if (null == columnNames)
				{
					columnNames = new String[columnValues.length];

					for (int i = 0; i < columnNames.length; ++i)
					{
						columnNames[i] = Integer.toString(i);
					}
				}

				return new RIDataRecord(columnNames, columnValues);
			}
			else
			{
				close();
				return null;
			}
		}

		protected String[] extractColumnNames(String line)
		{
			String[] parts = line.split(separator);

			for (int i = 0; i < parts.length; ++i)
			{
				parts[i] = unwrap(parts[i], headerWrapString);
			}

			return parts;
		}

		protected String[] extractColumnValues(String line)
		{
			String[] parts = line.split(separator);

			for (int i = 0; i < parts.length; ++i)
			{
				parts[i] = unwrap(parts[i], valueWrapString);
			}

			return parts;
		}

		private String unwrap(String string, String sign)
		{
			if (null != sign && string.startsWith(sign) && string.endsWith(sign))
			{
				string = string.substring(sign.length()).substring(0, string.length() - sign.length() - sign.length());
			}

			return string;
		}

	}

}