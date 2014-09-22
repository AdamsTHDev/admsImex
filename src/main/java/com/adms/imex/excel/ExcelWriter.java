/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adms.imex.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adms.imex.BaseWriter;
import com.adms.imex.BatchIOException;
import com.adms.imex.DataRecord;

public class ExcelWriter extends BaseWriter {

	private String sheetName = "Sheet1";
	private boolean writeHeader = true;

	public enum EXCEL_VERSION {
		VERSION_2003, VERSION_2007
	};

	private EXCEL_VERSION excelVersion;

	public void write(ResultSet rs)
			throws BatchIOException
	{
		OutputStream aWriter = null;
		Workbook workbook = prepreWorkbook();
		Sheet sheet = workbook.createSheet(getSheetName());
		try
		{
			if (writeHeader)
			{
				writeHeader(rs, sheet);
			}

			while (rs.next())
			{
				DataRecord record = extractDataRecord(rs);

				writeDataRecord(record, sheet);
			}

			writeFooter(rs, sheet);

			aWriter = prepareStream();

			workbook.write(aWriter);
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
		OutputStream aWriter = null;
		Workbook workbook = prepreWorkbook();
		Sheet sheet = workbook.createSheet(getSheetName());
		try
		{
			boolean first = true;
			DataRecord firstRecord = null;

			for (DataRecord record : records)
			{
				if (first)
				{
					first = false;
					firstRecord = record;

					if (writeHeader)
					{
						writerHeader(record, sheet);
					}
				}

				writeDataRecord(record, sheet);
			}

			writeFooter(firstRecord, sheet);

			aWriter = prepareStream();

			workbook.write(aWriter);
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

	Workbook prepreWorkbook()
	{
		Workbook wb = null;
		if (getExcelVersion().equals(EXCEL_VERSION.VERSION_2003))
		{
			wb = new HSSFWorkbook();
		}
		else
		{
			wb = new XSSFWorkbook();
		}

		return wb;
	}

	// Create a workbook: HSSFWorkbook workbook = new HSSFWorkbook();
	//  Create a new worksheet in the workbook and name the worksheet
	// "Java Excels":
	// HSSFSheet sheet = workbook.createSheet("Java Excels");
	// Create a new row in the sheet: HSSFRow row =
	// sheet.createRow((short)0);
	// Create a cell in the row: HSSFCell cell = row.createCell((short) 0);
	// Put some content in the cell: cell.setCellValue("Have a Cup of XL");
	// Write the workbook into the filesystem:
	// workbook.write(fileOutputStream);
	protected void writeHeader(ResultSet rs, Sheet sheet)
			throws SQLException, IOException
	{
		ResultSetMetaData meta = rs.getMetaData();
		Row row = sheet.createRow(0);

		for (int i = 0; i < meta.getColumnCount(); ++i)
		{
			int colIdx = i + 1;
			String colName = meta.getColumnName(colIdx);
			Cell cell = row.createCell(i);

			CreationHelper factory = sheet.getWorkbook().getCreationHelper();
			cell.setCellValue(factory.createRichTextString(colName));
		}
	}

	protected void writerHeader(DataRecord record, Sheet sheet)
	{
		Row row = sheet.createRow(0);

		for (int i = 0; i < record.getFieldNames().length; ++i)
		{
			String colName = record.getFieldNames()[i];
			Cell cell = row.createCell(i);

			CreationHelper factory = sheet.getWorkbook().getCreationHelper();
			cell.setCellValue(factory.createRichTextString(colName));
		}
	}

	protected void writeDataRecord(DataRecord record, Sheet sheet)
			throws BatchIOException
	{
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);

		for (int i = 0; i < record.getFieldNames().length; ++i)
		{
			Object value = record.getFieldValue(i);
			Cell cell = row.createCell(i);

			if (null != value)
			{
				// Calendar
				// Date
				// String
				// boolean
				// double
				if (value instanceof Calendar)
				{
					cell.setCellValue((Calendar) value);
					formatAsDate(cell);
				}
				else if (value instanceof Timestamp)
				{
					cell.setCellValue(((Timestamp) value));
					formatAsTimestamp(cell);
				}
				else if (value instanceof Date)
				{
					cell.setCellValue((Date) value);
					formatAsDate(cell);
				}
				else if ((value instanceof Boolean))
				{
					cell.setCellValue((Boolean) value);
				}
				else if (value instanceof Number)
				{
					cell.setCellValue(((Number) value).doubleValue());
				}
				else
				{
					CreationHelper factory = sheet.getWorkbook().getCreationHelper();
					cell.setCellValue(factory.createRichTextString(value.toString()));
				}
			}
		}
	}

	protected void writeFooter(ResultSet rs, Sheet sheet)
	{
		// Do nothing
	}

	protected void writeFooter(DataRecord firstRecord, Sheet sheet)
	{
		// Do nothing
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
	 * @return the writeHeader
	 */
	public boolean isWriteHeader()
	{
		return writeHeader;
	}

	/**
	 * @param writeHeader
	 *            the writeHeader to set
	 */
	public void setWriteHeader(boolean writeHeader)
	{
		this.writeHeader = writeHeader;
	}

	private void formatAsDate(Cell cell)
	{
		CellStyle aStyle = getDateStyle(cell.getSheet().getWorkbook());
		cell.setCellStyle(aStyle);
	}

	private void formatAsTimestamp(Cell cell)
	{
		CellStyle aStyle = getTimestampStyle(cell.getSheet().getWorkbook());
		cell.setCellStyle(aStyle);
		// formatAsDate(cell);
	}

	private final Map<Workbook, CellStyle> dateStyles = new WeakHashMap<Workbook, CellStyle>();

	private CellStyle getDateStyle(Workbook workbook)
	{

		CellStyle style = dateStyles.get(workbook);
		if (null == style)
		{
			style = workbook.createCellStyle();
			CreationHelper factory = workbook.getCreationHelper();
			style.setDataFormat(factory.createDataFormat().getFormat("m/d/yy"));
			dateStyles.put(workbook, style);
		}

		return style;
	}

	private final Map<Workbook, CellStyle> timestampStyles = new WeakHashMap<Workbook, CellStyle>();

	private CellStyle getTimestampStyle(Workbook workbook)
	{
		CellStyle style = timestampStyles.get(workbook);
		if (null == style)
		{
			style = workbook.createCellStyle();
			CreationHelper factory = workbook.getCreationHelper();
			style.setDataFormat(factory.createDataFormat().getFormat("m/d/yy h:mm"));
			timestampStyles.put(workbook, style);
		}

		return style;
	}

	public EXCEL_VERSION getExcelVersion()
	{
		return excelVersion;
	}

	public void setExcelVersion(EXCEL_VERSION excelVersion)
	{
		this.excelVersion = excelVersion;
	}

}