package com.adms.imex.excelformat;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class DataSetDefinition {

	@XStreamAsAttribute
	private String description;

	@XStreamImplicit(itemFieldName = "DataSheet")
	private List<SheetDefinition> sheetDefinitionList;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<SheetDefinition> getSheetDefinitionList()
	{
		return sheetDefinitionList;
	}

	public void setSheetDefinitionList(List<SheetDefinition> sheetDefinitionList)
	{
		this.sheetDefinitionList = sheetDefinitionList;
	}

	@Override
	public String toString()
	{
		return "DataSetDefinition [description=" + description + ", sheetDefinitionList=" + sheetDefinitionList + "]";
	}

}
