package com.adms.imex.excelformat;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adms.imex.enums.CellDataType;

public class ExcelFileFormatWriter {

	private DataHolder fileDataHolder;

	private FileFormatDefinition fileFormatDefinition;

	private Workbook wb;

	public ExcelFileFormatWriter(ExcelFormat excelFileFormat, DataHolder fileDataHolder)
			throws Exception
	{
		if (excelFileFormat != null && excelFileFormat.getFileFormat() != null && excelFileFormat.getFileFormat().getDataSetDefinition() != null && excelFileFormat.getFileFormat().getDataSetDefinition().getSheetDefinitionList() != null)
		{
			this.fileFormatDefinition = excelFileFormat.getFileFormat();
			this.fileDataHolder = fileDataHolder;
		}
		else
		{
			throw new Exception("invalid excelFileFormat");
		}
	}

	public void write(OutputStream output)
			throws Exception
	{
		this.wb = new XSSFWorkbook();

		try
		{
			if (CollectionUtils.isNotEmpty(this.fileFormatDefinition.getDataSetDefinition().getSheetDefinitionList()))
			{
				for (SheetDefinition sheetDefinition : this.fileFormatDefinition.getDataSetDefinition().getSheetDefinitionList())
				{
					if (StringUtils.isBlank(sheetDefinition.getSheetName()))
					{
						sheetDefinition.setSheetName(this.fileDataHolder.getSheetNameByIndex(sheetDefinition.getSheetIndex()));
					}
					DataHolder sheetDataHolder = this.fileDataHolder.get(sheetDefinition.getSheetName());

					if (sheetDataHolder == null && Boolean.TRUE.equals(sheetDefinition.getSkipWhenNull()))
					{
						continue;
					}

					if (CollectionUtils.isNotEmpty(sheetDefinition.getCellDefinitionList()))
					{
						for (CellDefinition cellDefinition : sheetDefinition.getCellDefinitionList())
						{
							DataHolder cellDataHolder = sheetDataHolder.get(cellDefinition.getFieldName());

							writeCellValue(cellDataHolder, cellDefinition);
						}
					}

					if (CollectionUtils.isNotEmpty(sheetDefinition.getRecordDefinitionList()))
					{
						for (RecordDefinition recordDefinition : sheetDefinition.getRecordDefinitionList())
						{
							int currentRow = recordDefinition.getBeginRow();

							List<DataHolder> recordDataHolderList = sheetDataHolder.getDataList(recordDefinition.getListSourceName());
							for (DataHolder recordDataHolder : recordDataHolderList)
							{
								for (CellDefinition cellDefinition : recordDefinition.getCellDefinitionList())
								{
									DataHolder cellDataHolder = recordDataHolder.get(cellDefinition.getFieldName());
									cellDefinition.setCurrentRow(currentRow);
									writeCellValue(cellDataHolder, cellDefinition);
								}

								currentRow++;
							}
						}
					}
				}
			}

			this.wb.write(output);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			this.wb.close();
		}
	}

	private Sheet getSheet(CellDefinition cellDefinition)
	{
		String sheetName = null;
		if (StringUtils.isNotBlank(cellDefinition.getSheetDefinition().getOutputSheetName()))
		{
			sheetName = cellDefinition.getSheetDefinition().getOutputSheetName();
		}
		else
		{
			sheetName = cellDefinition.getSheetDefinition().getSheetName();
		}

		Sheet sheet = this.wb.getSheet(sheetName);

		if (sheet == null)
		{
			sheet = this.wb.createSheet(sheetName);
		}

		return sheet;
	}

	private Row getRow(CellDefinition cellDefinition)
	{
		Sheet sheet = getSheet(cellDefinition);
		int rowIndex = (cellDefinition.getCurrentRow() != null ? cellDefinition.getCurrentRow() : cellDefinition.getRow()) - 1;
		Row row = sheet.getRow(rowIndex);

		if (row == null)
		{
			row = sheet.createRow(rowIndex);
		}

		return row;
	}

	private Cell getCell(CellDefinition cellDefinition)
	{
		Row row = getRow(cellDefinition);
		Cell cell = row.getCell(cellDefinition.getColumn().getColumnIndex());

		if (cell == null)
		{
			cell = row.createCell(cellDefinition.getColumn().getColumnIndex());
		}

		return cell;
	}

	private CellStyle getCellStyle(CellDefinition cellDefinition)
	{
		CellStyle cs = null;
		if (StringUtils.isNotBlank(cellDefinition.getDataFormat()))
		{
			cs = this.wb.createCellStyle();
			cs.setDataFormat(this.wb.getCreationHelper().createDataFormat().getFormat(cellDefinition.getDataFormat().trim()));
		}

		return cs;
	}

	private void writeCellValue(DataHolder cellDataHolder, CellDefinition cellDefinition)
			throws Exception
	{
		Cell cell = getCell(cellDefinition);

		// for boolean, double, string, calendar, date
		switch (cellDefinition.getDataType()) {
		case BLANK:
			cell.setCellType(Cell.CELL_TYPE_BLANK);
			break;

		case BOOLEAN:

			break;

		case DATE:
			if (cellDataHolder.getValue() != null)
			{
				try
				{
					cell.setCellValue((Date) cellDefinition.parse((String) cellDataHolder.getValue()));
				}
				catch (ClassCastException e)
				{
					if (CellDataType.TEXT.equals(cellDefinition.getRecoveryType()))
					{
						cell.setCellValue(cellDataHolder.getValue().toString());
					}
					else
					{
						throw e;
					}
				}
				catch (ParseException e)
				{
					if (CellDataType.TEXT.equals(cellDefinition.getRecoveryType()))
					{
						cell.setCellValue(cellDataHolder.getValue().toString());
					}
					else
					{
						throw e;
					}
				}
			}
			break;

		case ERROR:
			// TODO
			break;

		case FORMULAR:

			break;

		case NUMBER:
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			if (cellDataHolder.getValue() != null)
			{
				cell.setCellValue(Double.valueOf(cellDataHolder.getValue().toString()));
			}
			break;

		case TEXT:
			cell.setCellType(Cell.CELL_TYPE_STRING);
			if (cellDataHolder != null && cellDataHolder.getValue() != null)
			{
				if (Boolean.TRUE.equals(cellDefinition.getAutoTrim()))
				{
					cell.setCellValue(String.valueOf(cellDataHolder.getValue()).trim());
				}
				else
				{
					cell.setCellValue(String.valueOf(cellDataHolder.getValue()));
				}
			}
			else
			{
				cell.setCellValue(cellDefinition.getDefaultValue());
			}
			break;

		default:
			throw new Exception("Unsupported data type! " + cellDataHolder.getValue());
		}

		// TODO
		cell.setCellStyle(getCellStyle(cellDefinition));
		// cell.setCellComment(comment);
		// cell.setCellErrorValue(value);
		// cell.setCellFormula(formula);
		// cell.setCellType(cellType);
		// cell.setHyperlink(link);
	}

}
