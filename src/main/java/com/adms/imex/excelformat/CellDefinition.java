package com.adms.imex.excelformat;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.adms.imex.enums.CellDataType;
import com.adms.imex.enums.ExcelColumn;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

//import com.thoughtworks.xstream.annotations.XStreamConverter;

public class CellDefinition {

	@XStreamAsAttribute
	private Integer row;

	private Integer currentRow;

	@XStreamAsAttribute
	// @XStreamConverter(ExcelColumnConverter.class)
	private ExcelColumn column;

	@XStreamAsAttribute
	private String fieldName;

	@XStreamAsAttribute
	// @XStreamConverter(CellDataTypeConverter.class)
	private CellDataType dataType;

	@XStreamAsAttribute
	private String dataFormat;

	@XStreamAsAttribute
	private Boolean autoTrim;

	private SheetDefinition sheetDefinition;

	private DecimalFormat decimalFormatter;
	private DateFormat dateFormatter;

	public Integer getRow()
	{
		return row;
	}

	public void setRow(Integer row)
	{
		this.row = row;
	}

	public Integer getCurrentRow()
	{
		return currentRow;
	}

	public void setCurrentRow(Integer currentRow)
	{
		this.currentRow = currentRow;
	}

	public ExcelColumn getColumn()
	{
		return column;
	}

	public void setColumn(ExcelColumn column)
	{
		this.column = column;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public CellDataType getDataType()
	{
		return dataType;
	}

	public void setDataType(CellDataType dataType)
	{
		this.dataType = dataType;
	}

	public String getDataFormat()
	{
		return dataFormat;
	}

	public void setDataFormat(String dataFormat)
	{
		this.dataFormat = dataFormat;
	}

	public Boolean getAutoTrim()
	{
		return autoTrim;
	}

	public void setAutoTrim(Boolean autoTrim)
	{
		this.autoTrim = autoTrim;
	}

	public SheetDefinition getSheetDefinition()
	{
		return sheetDefinition;
	}

	public void setSheetDefinition(SheetDefinition sheetDefinition)
	{
		this.sheetDefinition = sheetDefinition;
	}

	private DecimalFormat getDecimalFormatter()
			throws Exception
	{
		if (this.decimalFormatter == null)
		{
			if (StringUtils.isEmpty(this.dataFormat))
			{
				throw new Exception("missing attribute \"dataFormat\" for CellDefinition (Sheet: " + this.getSheetDefinition().getSheetName() + ", Cell: " + this.getFieldName() + ")");
			}

			this.decimalFormatter = new DecimalFormat(this.dataFormat);
		}

		return this.decimalFormatter;
	}

	private DateFormat getDateFormatter()
			throws Exception
	{
		if (this.dateFormatter == null)
		{
			if (StringUtils.isEmpty(this.getSheetDefinition().getFileFormatDefinition().getLocale()))
			{
				throw new Exception("missing attribute \"locale\" for FileDefinition");
			}

			if (StringUtils.isEmpty(this.dataFormat))
			{
				throw new Exception("missing attribute \"dataFormat\" for CellDefinition (Sheet: " + this.getSheetDefinition().getSheetName() + ", Cell: " + this.getFieldName() + ")");
			}

			Locale locale = new Locale(this.getSheetDefinition().getFileFormatDefinition().getLocale());
			this.dateFormatter = new SimpleDateFormat(this.dataFormat, locale);
		}

		return this.dateFormatter;
	}

	public String format(Object value)
			throws Exception
	{
		switch (this.dataType) {
		case NUMBER:
			return getDecimalFormatter().format(value);

		case DATE:
			return getDateFormatter().format(value);

		default:
			return null;
		}
	}

	public Object parse(String value)
			throws Exception
	{
		switch (this.dataType) {
		case NUMBER:
			Object o = getDecimalFormatter().parse(value);
			if (o instanceof Long)
				return new BigDecimal((Long) o);
			
			return getDecimalFormatter().parse(value);

		case DATE:
			return getDateFormatter().parse(value);

		default:
			return null;
		}
	}

	@Override
	public String toString()
	{
		return "CellDefinition [row=" + row + ", column=" + column + ", fieldName=" + fieldName + ", dataType=" + dataType + ", dataFormat=" + dataFormat + ", autoTrim=" + autoTrim + "]";
	}

}
