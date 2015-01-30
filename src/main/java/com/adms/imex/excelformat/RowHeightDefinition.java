package com.adms.imex.excelformat;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class RowHeightDefinition {

	@XStreamAsAttribute
	private Integer height;

	@XStreamAsAttribute
	private Integer row;

	private SheetDefinition sheetDefinition;

	public Integer getHeight()
	{
		return height;
	}

	public void setHeight(Integer height)
	{
		this.height = height;
	}

	public Integer getRow()
	{
		return row;
	}

	public void setRow(Integer row)
	{
		this.row = row;
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
		return "RowHeightDefinition [height=" + height + ", row=" + row + "]";
	}

}
