/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.csv;

import java.io.Reader;

import com.adms.imex.BaseReader;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;

public class BasicCsvReader extends BaseReader {

	private boolean firstRowAsColumnNames;
	private int numOfSkippedLine;
	private String headerWrapString;
	private String valueWrapString;
	private String separator = ",";

	public Iterable<DataRecord> read(Object... params)
			throws BatchIOException
	{
		Reader reader = null;
		try
		{
			reader = prepareReader();

			CsvDataRecordIterable iterable = new CsvDataRecordIterable(reader);

			iterable.setFirstRowAsColumnNames(firstRowAsColumnNames);
			iterable.setHeaderWrapString(headerWrapString);
			iterable.setNumOfSkippedLine(numOfSkippedLine);
			iterable.setSeparator(separator);
			iterable.setValueWrapString(valueWrapString);

			return iterable;
		}
		catch (Exception ex)
		{
			disposeReader(reader);
			throw new BatchIOException(ex);
		}
	}

	/**
	 * @return the firstRowAsColumnNames
	 */
	public boolean isFirstRowAsColumnNames()
	{
		return firstRowAsColumnNames;
	}

	/**
	 * @param firstRowAsColumnNames
	 *            the firstRowAsColumnNames to set
	 */
	public void setFirstRowAsColumnNames(boolean firstRowAsColumnNames)
	{
		this.firstRowAsColumnNames = firstRowAsColumnNames;
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

}
