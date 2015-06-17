package com.adms.imex.excelformat;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.adms.imex.enums.CellDataType;

public class ExcelFormatWriter {

	private DataHolder fileDataHolder;

	private FileFormatDefinition fileFormatDefinition;

	private Workbook wb;

	private HashMap<String, Integer> offsetMap = new HashMap<String, Integer>();

	public ExcelFormatWriter(ExcelFormat excelFileFormat, DataHolder fileDataHolder)
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
		InputStream templateFile = null;
		try
		{
			if (StringUtils.isNotBlank(this.fileFormatDefinition.getTemplateFile()))
			{
				templateFile = URLClassLoader.getSystemResourceAsStream(this.fileFormatDefinition.getTemplateFile());
				if (templateFile != null)
				{
					this.wb = WorkbookFactory.create(templateFile);
				}
				else
				{
					throw new Exception("Template file not found: " + this.fileFormatDefinition.getTemplateFile());
				}
			}
			else
			{
				this.wb = new XSSFWorkbook();
			}

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

							try
							{
								writeCellValue(cellDataHolder, cellDefinition);
							}
							catch (Exception e)
							{
								System.err.println("cellDataHolder: " + cellDataHolder);
								System.err.println("cellDefinition: " + cellDefinition);
								throw e;
							}
						}
					}

					if (CollectionUtils.isNotEmpty(sheetDefinition.getRecordDefinitionList()))
					{
						for (RecordDefinition recordDefinition : sheetDefinition.getRecordDefinitionList())
						{
							int currentRow = 0;
							if (recordDefinition.getBeginRow() != null)
							{
								currentRow = recordDefinition.getBeginRow();
							}
							else if (recordDefinition.getOffsetRow() != null && StringUtils.isNotBlank(recordDefinition.getOffsetFrom()))
							{
								if (offsetMap.get(sheetDefinition.getSheetName().concat(recordDefinition.getOffsetFrom())) == null)
								{
									throw new Exception("offset configure invalid (sheetName: " + sheetDefinition.getSheetName() + ", offsetFrom:" + recordDefinition.getOffsetFrom() + ")");
								}

								currentRow = offsetMap.get(sheetDefinition.getSheetName().concat(recordDefinition.getOffsetFrom())) + recordDefinition.getOffsetRow();
							}

							List<DataHolder> recordDataHolderList = sheetDataHolder.getDataList(recordDefinition.getListSourceName());
							if (recordDataHolderList == null && Boolean.TRUE.equals(recordDefinition.getSkipWhenNull()))
							{
								// do nothing
							}
							else
							{
								if (CollectionUtils.isEmpty(recordDataHolderList) && !Boolean.TRUE.equals(recordDefinition.getSkipWhenNull()))
								{
									throw new Exception("Error!! not found dataList for listSourceName: " + recordDefinition.getListSourceName());
								}

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
							offsetMap.put(sheetDefinition.getSheetName().concat(recordDefinition.getListSourceName()), currentRow);
						}
					}
					
					if (CollectionUtils.isNotEmpty(sheetDefinition.getRowHeightDefinitionList()))
					{
						for (RowHeightDefinition rowHeightDefinition : sheetDefinition.getRowHeightDefinitionList())
						{
							Row row = this.wb.getSheet(sheetDefinition.getSheetName()).getRow(rowHeightDefinition.getRow() - 1);
							if (row == null)
							{
								row = this.wb.getSheet(sheetDefinition.getSheetName()).createRow(rowHeightDefinition.getRow() - 1);
							}
							row.setHeight(rowHeightDefinition.getHeight().shortValue());
						}
					}
					
					if (CollectionUtils.isNotEmpty(sheetDefinition.getColumnWidthDefinitionList()))
					{
						for (ColumnWidthDefinition columnWidthDefinition : sheetDefinition.getColumnWidthDefinitionList())
						{
							this.wb.getSheet(sheetDefinition.getSheetName()).setColumnWidth(columnWidthDefinition.getColumn().getColumnIndex(), columnWidthDefinition.getWidth());
						}
					}
				}
			}

			if (StringUtils.isNotBlank(this.fileFormatDefinition.getTemplateFile()) && Boolean.TRUE.equals(this.fileFormatDefinition.getRemoveTemplateAfterComplete()))
			{
				for (int i = 0; i < this.wb.getNumberOfSheets(); i++)
				{
					if (this.wb.getSheetAt(i).getSheetName().equals(this.fileFormatDefinition.getTemplateSheetName()))
					{
						this.wb.removeSheetAt(i);
						break;
					}
				}
			}

			if (StringUtils.isNotBlank(this.fileFormatDefinition.getReOrderSheetName()))
			{
				for (int i = 0; i < this.wb.getNumberOfSheets(); i++)
				{
					if (this.wb.getSheetAt(i).getSheetName().equals(this.fileFormatDefinition.getReOrderSheetName()))
					{
						this.wb.cloneSheet(i);
						this.wb.removeSheetAt(i);
						this.wb.setSheetName(this.wb.getNumberOfSheets() - 1, this.fileFormatDefinition.getReOrderSheetName());
						break;
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
			try { templateFile.close(); } catch (Exception e) { }
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
		
		if (cellDefinition.getSheetDefinition().getDisplayGridlines() == null || cellDefinition.getSheetDefinition().getDisplayGridlines().equals(Boolean.TRUE))
		{
			sheet.setDisplayGridlines(true);
		}
		else
		{
			sheet.setDisplayGridlines(false);
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
		
		if (StringUtils.isNotBlank(cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateFile()) && StringUtils.isNotBlank(cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateSheetName())
				&& cellDefinition.getTemplateRow() != null && cellDefinition.getTemplateColumn() != null)
		{
			try
			{
				cs = this.wb.getSheet(cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateSheetName()).getRow(cellDefinition.getTemplateRow() - 1).getCell(cellDefinition.getTemplateColumn().getColumnIndex()).getCellStyle();
			}
			catch (Exception e)
			{
				System.err.println("error while getting CellStyle from template");
				System.err.println("1"+cellDefinition.getSheetDefinition());
				System.err.println("Template File  : "+cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateFile());
				System.err.println("Template Sheet : "+cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateSheetName());
				System.err.println("Template Row   : "+cellDefinition.getTemplateRow());
				System.err.println("Template Column: "+cellDefinition.getTemplateColumn());
//				System.err.println("6"+cellDefinition.getTemplateColumn().getColumnIndex());
//				System.err.println("7"+this.wb.getSheet(cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateSheetName()).getRow(cellDefinition.getTemplateRow() - 1));
//				System.err.println("8"+this.wb.getSheet(cellDefinition.getSheetDefinition().getFileFormatDefinition().getTemplateSheetName()).getRow(cellDefinition.getTemplateRow() - 1).getCell(cellDefinition.getTemplateColumn().getColumnIndex()).getCellStyle());
//				System.err.println("error while getting template");
//				System.err.println();
				e.printStackTrace();
			}
		}
		else if (StringUtils.isNotBlank(cellDefinition.getDataFormat()))
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

		if (cellDefinition.getFieldName() != null && cellDefinition.getFieldName().equals("AYP"))
		{
			//TODO
//			System.out.println(cellDefinition);
		}
		if ((cellDefinition.getRowMergeFrom() != null && cellDefinition.getRowMergeTo() != null) || (cellDefinition.getColumnMergeFrom() != null && cellDefinition.getColumnMergeTo() != null))
		{
			if (cellDefinition.getRowMergeFrom() != null && cellDefinition.getRowMergeTo() != null && cellDefinition.getColumnMergeFrom() != null && cellDefinition.getColumnMergeTo() != null)
			{
				cell.getSheet().addMergedRegion(new CellRangeAddress(
						cellDefinition.getRowMergeFrom() - 1, cellDefinition.getRowMergeTo() - 1,
						cellDefinition.getColumnMergeFrom().getColumnIndex(), cellDefinition.getColumnMergeTo().getColumnIndex()));
			}
			else if ((cellDefinition.getRowMergeFrom() == null && cellDefinition.getRowMergeTo() == null) || (cellDefinition.getColumnMergeFrom() != null && cellDefinition.getColumnMergeTo() != null))
			{
				if (cellDefinition.getCurrentRow() != null)
				{
					cell.getSheet().addMergedRegion(new CellRangeAddress(
							(cellDefinition.getRow() - 1) + (cellDefinition.getCurrentRow() - 1), (cellDefinition.getRow() - 1) + (cellDefinition.getCurrentRow() - 1),
							cellDefinition.getColumnMergeFrom().getColumnIndex(), cellDefinition.getColumnMergeTo().getColumnIndex()));
				}
				else
				{
					cell.getSheet().addMergedRegion(new CellRangeAddress(
							cellDefinition.getRow() - 1, cellDefinition.getRow() - 1,
							cellDefinition.getColumnMergeFrom().getColumnIndex(), cellDefinition.getColumnMergeTo().getColumnIndex()));
				}
			}
			else if ((cellDefinition.getRowMergeFrom() != null && cellDefinition.getRowMergeTo() != null) || (cellDefinition.getColumnMergeFrom() == null && cellDefinition.getColumnMergeTo() == null))
			{
				cell.getSheet().addMergedRegion(new CellRangeAddress(
						cellDefinition.getRowMergeFrom() - 1, cellDefinition.getRowMergeTo() - 1,
						cellDefinition.getColumn().getColumnIndex(), cellDefinition.getColumn().getColumnIndex()));
			}
			else
			{
				throw new Exception("Invalid merge area! " + cellDefinition);
			}
		}

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
					if (cellDataHolder.getValue() != null && cellDataHolder.getValue() instanceof java.util.Date)
					{
						cell.setCellValue((Date) cellDataHolder.getValue());
					}
					else
					{
						cell.setCellValue((Date) cellDefinition.parse((String) cellDataHolder.getValue()));
					}
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
						System.err.println("Error!! parsing error " + cellDefinition);
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
			try
			{
				if (cellDataHolder != null && cellDataHolder.getValue() != null)
				{
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(Double.valueOf(cellDataHolder.getValue().toString()));
				}
				else if (StringUtils.isNotBlank(cellDefinition.getDefaultValue()))
				{
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(Double.valueOf(cellDefinition.getDefaultValue()));
				}
				else
				{
					//blank
				}
			}
			catch (Exception e)
			{
				System.err.println("Error while reading cell: " + cellDefinition);
				throw e;
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
			else if (StringUtils.isNotBlank(cellDefinition.getDefaultValue()))
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
