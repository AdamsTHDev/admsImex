package com.adms.imex.excelformat;

import com.adms.imex.enums.ExcelColumn;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author kampon.pan
 *
 */
public class ColumnWidthDefinition {

	@XStreamAsAttribute
	private Integer width;

	@XStreamAsAttribute
	private ExcelColumn column;

	private SheetDefinition sheetDefinition;

	public Integer getWidth()
	{
		return width;
	}

	public void setWidth(Integer width)
	{
		this.width = width;
	}

	public ExcelColumn getColumn()
	{
		return column;
	}

	public void setColumn(ExcelColumn column)
	{
		this.column = column;
	}

	public SheetDefinition getSheetDefinition()
	{
		return sheetDefinition;
	}

	public void setSheetDefinition(SheetDefinition sheetDefinition)
	{
		this.sheetDefinition = sheetDefinition;
	}

	@Override
	public String toString()
	{
		return "ColumnWidthDefinition [width=" + width + ", column=" + column + "]";
	}

}
