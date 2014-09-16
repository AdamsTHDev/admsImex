/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.excel;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

import com.adms.imex.BaseReader;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;

public class ExcelReader extends BaseReader {
	private Workbook workbook;
	private String sheetName;
	private boolean firstRowAsColumnNames;
	private int numOfSkippedLine;
	private int[] dateColumns = {};

	@SuppressWarnings("deprecation")
	public Iterable<DataRecord> read(Object... params)
			throws BatchIOException
	{
		InputStream reader = null;
		try
		{
			ExcelDataRecordIterable iterable = null;
			if (null != this.workbook)
			{
				iterable = new ExcelDataRecordIterable(this.workbook);
			}
			else
			{
				reader = prepareStream();
				iterable = new ExcelDataRecordIterable(reader);
			}

			iterable.setFirstRowAsColumnNames(firstRowAsColumnNames);
			iterable.setNumOfSkippedLine(numOfSkippedLine);
			iterable.setDateColumns(dateColumns);

			if (null != sheetName)
			{
				iterable.setSheetName(sheetName);
			}

			return iterable;
		}
		catch (Exception ex)
		{
			disposeReader(reader);
			throw new BatchIOException(ex);
		}
	}

	public Workbook getWorkbook()
	{
		return workbook;
	}

	public void setWorkbook(Workbook workbook)
	{
		this.workbook = workbook;
	}

	/**
	 * @return the sheetName
	 */
	public String getSheetName()
	{
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            the sheetName to set
	 */
	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
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
	 * @return the dateColumns
	 */
	@Deprecated
	public int[] getDateColumns()
	{
		return dateColumns;
	}

	/**
	 * @param dateColumns
	 *            the dateColumns to set
	 */
	@Deprecated
	public void setDateColumns(int... dateColumns)
	{
		this.dateColumns = dateColumns;
	}

}
