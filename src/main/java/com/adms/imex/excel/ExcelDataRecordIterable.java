/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.excel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.adms.imex.DataRecord;
import com.adms.imex.RIDataRecord;

public class ExcelDataRecordIterable implements Iterable<DataRecord> {

	private final Workbook workbook;
	private final BufferedInputStream reader;
	private final ExcelDataRecordIterator iterator;

	private String sheetName;
	private boolean firstRowAsColumnNames;
	private int numOfSkippedLine;

	private boolean eof;
	private String[] columnNames;

	private int[] dateColumns = {};

	public ExcelDataRecordIterable(InputStream reader)
			throws IOException
	{
		if (reader instanceof BufferedInputStream)
		{
			this.reader = (BufferedInputStream) reader;
		}
		else
		{
			this.reader = new BufferedInputStream(reader);
		}

		workbook = null;
		iterator = new ExcelDataRecordIterator();
	}

	public ExcelDataRecordIterable(Workbook workbook)
			throws IOException
	{
		this.workbook = workbook;
		reader = null;
		iterator = new ExcelDataRecordIterator();
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
	 * @return the eof
	 */
	public boolean isEof()
	{
		return eof;
	}

	/**
	 * @param eof
	 *            the eof to set
	 */
	public void setEof(boolean eof)
	{
		this.eof = eof;
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
	public void setDateColumns(int[] dateColumns)
	{
		this.dateColumns = dateColumns;
	}

	protected class ExcelDataRecordIterator implements Iterator<DataRecord> {
		// Create a new Excel document reference: HSFWorkbook workbook = new
		// HSSFWorkbook(new
		// FileInputStream(fileToBeRead));.
		// Refer to the sheet: By default, the first sheet in the Excel document
		// is at reference 0:
		// HSSFSheet sheet = workbook.getSheetAt(0);. A sheet can also be
		// referred to by name. Let's
		// assume that the Excel sheet has the default name "Sheet1". It can be
		// referred to as
		// follows: HSSFSheet sheet = workbook.getSheet("Sheet1");.
		// Refer to a row: HSSFRow row = sheet.getRow(0);.
		// Refer to a cell in the row: HSSFCell cell = row.getCell((short)0);.
		// Get the values in that cell: cell.getStringCellValue();.

		private final Workbook workbook;
		private final Sheet sheet;
		private final int rowCnt;

		private int idx;
		private int skipped;

		protected ExcelDataRecordIterator()
				throws IOException
		{
			try
			{
				if (null != ExcelDataRecordIterable.this.workbook)
				{
					this.workbook = ExcelDataRecordIterable.this.workbook;
				}
				else
				{
					workbook = WorkbookFactory.create(ExcelDataRecordIterable.this.reader);
				}
				if (null == sheetName)
				{
					sheet = workbook.getSheetAt(0);
				}
				else
				{
					sheet = workbook.getSheet(sheetName);
				}
				rowCnt = sheet.getLastRowNum() + 1;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				throw new IOException(ex.getLocalizedMessage());
			}
		}

		public boolean hasNext()
		{
			if (skipped < numOfSkippedLine)
			{
				idx = numOfSkippedLine;
				skipped = numOfSkippedLine;
			}

			if (null == columnNames && firstRowAsColumnNames)
			{
				if (idx >= rowCnt)
				{
					close();
					return false;
				}

				columnNames = extractColumnNames(idx++);
			}

			if (idx < rowCnt)
			{
				return null != sheet.getRow(idx);
			}
			else
			{
				return false;
			}
		}

		public DataRecord next()
		{
			if (hasNext())
			{
				Row row = sheet.getRow(idx++);

				return extractRecord(row);
			}
			else
			{
				close();
				throw new NoSuchElementException();
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

		private String[] extractColumnNames(int idx)
		{
			String[] names = null;
			Row row = sheet.getRow(idx);
			int lastCellNum = row.getLastCellNum();

			if (lastCellNum != -1)
			{
				names = new String[lastCellNum];
				int i = 0;
				for (Cell cell : row)
				{
					// Do something here
					String name = cell.getRichStringCellValue().getString();

					names[i++] = name;
				}
			}

			return names;
		}

		private DataRecord extractRecord(Row row)
		{
			int lastCellNum = row.getLastCellNum();

			if (lastCellNum == -1)
			{
				return null;
			}

			if (null == columnNames)
			{
				columnNames = new String[lastCellNum];

				for (int i = 0; i < lastCellNum; ++i)
				{
					columnNames[i] = Integer.toString(i);
				}
			}

			Object[] fieldValues = new Object[columnNames.length];

			for (int i = 0; i < columnNames.length; ++i)
			{
				Object value = null;
				Cell cell = row.getCell(i);

				if (null != cell)
				{
					int type = cell.getCellType();

					switch (type) {
					case Cell.CELL_TYPE_BLANK:
						value = null;
						break;
					case Cell.CELL_TYPE_ERROR:
						value = null;
						break;
					case Cell.CELL_TYPE_STRING:
						value = cell.getRichStringCellValue().getString();
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell))
						{
							value = cell.getDateCellValue();
						}
						else
						{
							value = cell.getNumericCellValue();
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					case Cell.CELL_TYPE_FORMULA:
						value = cell.getCellFormula();
						break;
					default:
						value = cell.getRichStringCellValue().getString();
					}
				}

				fieldValues[i] = value;
			}

			return new RIDataRecord(columnNames, fieldValues);
		}

		@SuppressWarnings("unused")
		private boolean isDateColumn(int idx)
		{
			for (int i = 0; i < dateColumns.length; ++i)
			{
				if (dateColumns[i] == idx)
				{
					return true;
				}
			}

			return false;
		}

	}

}