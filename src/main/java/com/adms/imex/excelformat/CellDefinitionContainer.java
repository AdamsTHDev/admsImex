package com.adms.imex.excelformat;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public abstract class CellDefinitionContainer {

	@XStreamImplicit(itemFieldName = "DataCell")
	protected List<CellDefinition> cellDefinitionList;

	public List<CellDefinition> getCellDefinitionList()
	{
		return cellDefinitionList;
	}

	public void setCellDefinitionList(List<CellDefinition> cellDefinitionList)
	{
		this.cellDefinitionList = cellDefinitionList;
	}

}