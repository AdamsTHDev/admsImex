package com.adms.imex.excelformat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.adms.imex.enums.CellDataType;

public class ExcelFormatReader {

	private DataHolder fileDataHolder;

	private FileFormatDefinition fileFormatDefinition;

	private Workbook wb;

	public ExcelFormatReader(ExcelFormat excelFileFormat)
			throws Exception
	{
		if (excelFileFormat != null && excelFileFormat.getFileFormat() != null && excelFileFormat.getFileFormat().getDataSetDefinition() != null && excelFileFormat.getFileFormat().getDataSetDefinition().getSheetDefinitionList() != null)
		{
			this.fileFormatDefinition = excelFileFormat.getFileFormat();
			this.fileDataHolder = new SimpleMapDataHolder();
		}
		else
		{
			throw new Exception("invalid excelFileFormat");
		}
	}

	public DataHolder read(InputStream input)
			throws Exception
	{
		this.wb = WorkbookFactory.create(input);

		try
		{
			if (CollectionUtils.isNotEmpty(this.fileFormatDefinition.getDataSetDefinition().getSheetDefinitionList()))
			{
				for (SheetDefinition sheetDefinition : this.fileFormatDefinition.getDataSetDefinition().getSheetDefinitionList())
				{
					// do
					// {
					// }
					// while
					// (!Boolean.TRUE.equals(sheetDefinition.getRepeatSheet())
					// /*|| (sheet == null )*/);

					Sheet sheet = getSheet(sheetDefinition);

					if (sheet == null && Boolean.TRUE.equals(sheetDefinition.getSkipWhenNull()))
					{
						continue;
					}

					if (StringUtils.isBlank(sheetDefinition.getSheetName()))
					{
						sheetDefinition.setSheetName(sheet.getSheetName());
					}

					DataHolder sheetDataHolder = new SimpleMapDataHolder();
					this.fileDataHolder.put(sheetDefinition.getSheetName(), sheetDataHolder);
					if (sheetDefinition.getSheetIndex() != null)
					{
						this.fileDataHolder.setSheetNameByIndex(sheetDefinition.getSheetIndex(), sheetDefinition.getSheetName());
					}

					if (CollectionUtils.isNotEmpty(sheetDefinition.getCellDefinitionList()))
					{
						for (CellDefinition cellDefinition : sheetDefinition.getCellDefinitionList())
						{
							DataHolder cellDataHolder = new SimpleMapDataHolder();
							sheetDataHolder.put(cellDefinition.getFieldName(), cellDataHolder);

							readCellValue(cellDataHolder, cellDefinition);
						}
					}

					if (CollectionUtils.isNotEmpty(sheetDefinition.getRecordDefinitionList()))
					{
						for (RecordDefinition recordDefinition : sheetDefinition.getRecordDefinitionList())
						{
							List<DataHolder> recordList = new ArrayList<DataHolder>();
							sheetDataHolder.putDataList(recordDefinition.getListSourceName(), recordList);

							int currentRow = recordDefinition.getBeginRow();
							boolean dataRecordFlag = false;

							while (currentRow <= recordDefinition.getEndRow())
							{
								if (isBeginDataRecord(currentRow, recordDefinition.getBeginRecordCondition()))
								{
									dataRecordFlag = true;
								}

								if (dataRecordFlag && !isEndDataRecord(currentRow, recordDefinition.getEndRecordCondition()))
								{
									DataHolder recordDataHolder = new SimpleMapDataHolder();
									recordList.add(recordDataHolder);

									for (CellDefinition cellDefinition : recordDefinition.getCellDefinitionList())
									{
										DataHolder cellDataHolder = new SimpleMapDataHolder();
										recordDataHolder.put(cellDefinition.getFieldName(), cellDataHolder);

										cellDefinition.setCurrentRow(currentRow);
										readCellValue(cellDataHolder, cellDefinition);
									}
								}
								else if (dataRecordFlag && isEndDataRecord(currentRow, recordDefinition.getEndRecordCondition()))
								{
									break;
								}

								currentRow++;
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			this.wb.close();
		}

		return this.fileDataHolder;
	}

	private boolean isBeginDataRecord(int currentRow, BeginRecordCondition beginRecordCondition)
			throws Exception
	{
		boolean isDataRecord = false;

		if (beginRecordCondition != null)
		{
			beginRecordCondition.setRow(currentRow);

			Object value = readRecordConditionValue(beginRecordCondition);
			String sValue = value != null ? value.toString() : null;

			switch (beginRecordCondition.getComparator()) {
			case EQ:
				if (StringUtils.isBlank(beginRecordCondition.getCheckValue()))
				{
					throw new Exception("checkValue can not be empty while checking BeginRecordCondition with comparator = EQ");
				}
				isDataRecord = StringUtils.isNotBlank(sValue) && beginRecordCondition.getCheckValue().equalsIgnoreCase(sValue);
				break;

			case NE:
				if (StringUtils.isBlank(beginRecordCondition.getCheckValue()))
				{
					throw new Exception("checkValue can not be empty while checking BeginRecordCondition with comparator = NE");
				}
				isDataRecord = StringUtils.isBlank(sValue) || !beginRecordCondition.getCheckValue().equalsIgnoreCase(sValue);
				break;

			case BLANK:
				isDataRecord = value == null || StringUtils.isBlank(value.toString());
				break;

			default:
				throw new Exception("Comparator '" + beginRecordCondition.getComparator() + "' is not supportted yet");
				// break;
			}
		}
		else
		{
			// default to TRUE if not found tag <BeginRecordCondition />
			isDataRecord = true;
		}

		return isDataRecord;
	}

	private boolean isEndDataRecord(int currentRow, EndRecordCondition endRecordCondition)
			throws Exception
	{
		boolean isDataRecord = false;
		endRecordCondition.setRow(currentRow);

		Object value = readRecordConditionValue(endRecordCondition);
		String sValue = value != null ? value.toString() : null;

		switch (endRecordCondition.getComparator()) {
		case EQ:
			if (StringUtils.isBlank(endRecordCondition.getCheckValue()))
			{
				throw new Exception("checkValue can not be empty while checking EndRecordCondition with comparator = EQ");
			}
			isDataRecord = endRecordCondition.getCheckValue().equalsIgnoreCase(sValue);
			break;

		case NE:
			if (StringUtils.isBlank(endRecordCondition.getCheckValue()))
			{
				throw new Exception("checkValue can not be empty while checking EndRecordCondition with comparator = NE");
			}
			isDataRecord = !endRecordCondition.getCheckValue().equalsIgnoreCase(sValue);
			break;

		case BLANK:
			isDataRecord = value == null || StringUtils.isBlank(value.toString());
			break;

		default:
			throw new Exception("Comparator '" + endRecordCondition.getComparator() + "' is not supportted yet");
			// break;
		}

		return isDataRecord;
	}

	private Object readRecordConditionValue(CellDefinition recordCondition)
			throws Exception
	{
		Sheet sheet = getSheet(recordCondition);

		if (sheet != null)
		{
			Row row = null;

			if (recordCondition instanceof BeginRecordCondition)
			{
				if (Boolean.TRUE.equals(((BeginRecordCondition) recordCondition).getFirstRowAsData()))
				{
					row = sheet.getRow(recordCondition.getRow() - 1); //first row as data
				}
				else
				{
					row = sheet.getRow(recordCondition.getRow() - 2); //first row as header
				}
			}
			else
			{
				row = sheet.getRow(recordCondition.getRow() - 1);
			}

			if (row != null)
			{
				Cell cell = row.getCell(recordCondition.getColumn().getColumnIndex());

				if (cell != null)
				{
					return readCellValue(cell, recordCondition);
				}
			}
		}
		return null;
	}

	private Sheet getSheet(CellDefinition cellDefinition)
			throws Exception
	{
		return getSheet(cellDefinition, false);
	}

	private Sheet getSheet(CellDefinition cellDefinition, boolean errorWhenNull)
			throws Exception
	{
		return cellDefinition != null ? getSheet(cellDefinition.getSheetDefinition(), errorWhenNull) : null;
	}

	private Sheet getSheet(SheetDefinition sheetDefinition)
			throws Exception
	{
		return getSheet(sheetDefinition, Boolean.TRUE.equals(sheetDefinition.getSkipWhenNull()));
	}

	private Sheet getSheet(SheetDefinition sheetDefinition, boolean skipWhenNull)
			throws Exception
	{
		Sheet s = null;

		if (StringUtils.isNotBlank(sheetDefinition.getSheetName()))
		{
			s = this.wb.getSheet(sheetDefinition.getSheetName());

			if (s == null && !skipWhenNull)
			{
				throw new Exception("sheet name '" + sheetDefinition.getSheetName() + "' not found");
			}
		}
		else if (sheetDefinition.getSheetIndex() != null && sheetDefinition.getSheetIndex() > -1)
		{
			try
			{
				s = this.wb.getSheetAt(sheetDefinition.getSheetIndex());
			}
			catch (Exception e)
			{
			}

			if (s == null && !skipWhenNull)
			{
				throw new Exception("sheet index '" + sheetDefinition.getSheetIndex() + "' not found");
			}
		}

		return s;
	}

	private Row getRow(CellDefinition cellDefinition)
			throws Exception
	{
		return getRow(cellDefinition, false);
	}

	private Row getRow(CellDefinition cellDefinition, boolean errorWhenNull)
			throws Exception
	{
		Row row = null;
		Sheet sheet = getSheet(cellDefinition);

		if (sheet != null)
		{
			if (cellDefinition.getCurrentRow() != null && cellDefinition.getCurrentRow() > -1)
			{
				row = sheet.getRow(cellDefinition.getCurrentRow() - 1);
			}
			else
			{
				row = sheet.getRow(cellDefinition.getRow() - 1);
			}

			if (row == null && errorWhenNull)
			{
				throw new Exception("row '" + cellDefinition.getRow() + "' not found");
			}
		}

		return row;
	}

	private Cell getCell(CellDefinition cellDefinition)
			throws Exception
	{
		return getCell(cellDefinition, false);
	}

	private Cell getCell(CellDefinition cellDefinition, boolean errorWhenNull)
			throws Exception
	{
		Cell cell = null;
		Row row = getRow(cellDefinition);

		if (row != null)
		{
			cell = row.getCell(cellDefinition.getColumn().getColumnIndex());

			if (cell == null && errorWhenNull)
			{
				throw new Exception("cell '" + cellDefinition.getColumn().getCode() + cellDefinition.getRow() + "' not found (" + cellDefinition + ")");
			}
		}

		return cell;
	}

	private Object readCellValue(DataHolder dataHolder, CellDefinition cellDefinition)
			throws Exception
	{
		return readCellValue(dataHolder, getCell(cellDefinition), cellDefinition);
	}

	private Object readCellValue(Cell c, CellDefinition cellDefinition)
			throws Exception
	{
		return readCellValue(null, c, cellDefinition);
	}

	private Object readCellValue(DataHolder dataHolder, Cell c, CellDefinition cellDefinition)
			throws Exception
	{
		try
		{
			Object value = null;

			if (c != null)
			{
				switch (c.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC: // number & date
					if (CellDataType.DATE.equals(cellDefinition.getDataType()))
					{
						value = c.getDateCellValue();
					}
					else
					{
						value = new BigDecimal(c.getNumericCellValue());
					}
					break;

				case Cell.CELL_TYPE_STRING:
					if (CellDataType.DATE.equals(cellDefinition.getDataType()))
					{
						try
						{
							value = (Date) (StringUtils.isNotBlank(c.getStringCellValue()) ? cellDefinition.parse(c.getStringCellValue()) : null);
						}
						catch (ParseException pe)
						{
							if (CellDataType.TEXT.equals(cellDefinition.getRecoveryType()))
							{
								value = c.getStringCellValue();
							}
							else
							{
								throw pe;
							}
						}
					}
					else if (CellDataType.NUMBER.equals(cellDefinition.getDataType()))
					{
						value = (BigDecimal) (c.getStringCellValue() != null ? cellDefinition.parse(c.getStringCellValue()) : null);
					}
					else
					{
						value = c.getStringCellValue();

						if (value != null && Boolean.TRUE.equals(cellDefinition.getAutoTrim()))
						{
							value = ((String) value).replaceAll(" ", "").trim();
						}
					}
					break;

				case Cell.CELL_TYPE_FORMULA:
					if (CellDataType.DATE.equals(cellDefinition.getDataType()))
					{
						value = (Date) (c.getStringCellValue() != null ? cellDefinition.parse(c.getStringCellValue()) : null);
					}
					else if (CellDataType.NUMBER.equals(cellDefinition.getDataType()))
					{
						switch (c.getCachedFormulaResultType()) {
						case Cell.CELL_TYPE_NUMERIC:
							value = new BigDecimal(c.getNumericCellValue());
							break;

						default:
							value = (BigDecimal) (c.getStringCellValue() != null ? cellDefinition.parse(c.getStringCellValue()) : null);
							break;
						}
					}
					else
					{
						value = c.getStringCellValue();

						if (value != null && Boolean.TRUE.equals(cellDefinition.getAutoTrim()))
						{
							value = ((String) value).replaceAll(" ", "").trim();
						}
					}
					break;

				case Cell.CELL_TYPE_BLANK:

					// TODO
					break;

				case Cell.CELL_TYPE_BOOLEAN:
					value = c.getBooleanCellValue();
					// TODO
					break;

				case Cell.CELL_TYPE_ERROR:
					// TODO
					break;

				default:
					break;
				}
			}

			if (dataHolder != null)
			{
				dataHolder.setValue(value);
			}

			return value;
		}
		catch (Exception e)
		{
			System.err.println("found error while reading cell [" + cellDefinition + "]");
			throw e;
		}
	}
}
