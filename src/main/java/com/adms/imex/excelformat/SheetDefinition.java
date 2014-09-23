package com.adms.imex.excelformat;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class SheetDefinition extends CellDefinitionContainer {

	@XStreamAsAttribute
	private String sheetName;

	@XStreamAsAttribute
	private Integer sheetIndex;

	@XStreamAsAttribute
	private Boolean skipWhenNull;

	@XStreamImplicit(itemFieldName = "DataRecord")
	private List<RecordDefinition> recordDefinitionList;

	private FileFormatDefinition fileFormatDefinition;

	public String getSheetName()
	{
		return sheetName;
	}

	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

	public Integer getSheetIndex()
	{
		return sheetIndex;
	}

	public void setSheetIndex(Integer sheetIndex)
	{
		this.sheetIndex = sheetIndex;
	}

	public Boolean getSkipWhenNull()
	{
		return skipWhenNull;
	}

	public void setSkipWhenNull(Boolean skipWhenNull)
	{
		this.skipWhenNull = skipWhenNull;
	}

	public List<RecordDefinition> getRecordDefinitionList()
	{
		return recordDefinitionList;
	}

	public void setRecordDefinitionList(List<RecordDefinition> recordDefinitionList)
	{
		this.recordDefinitionList = recordDefinitionList;
	}

	public FileFormatDefinition getFileFormatDefinition()
	{
		return fileFormatDefinition;
	}

	public void setFileFormatDefinition(FileFormatDefinition fileFormatDefinition)
	{
		this.fileFormatDefinition = fileFormatDefinition;
	}

	@Override
	public String toString()
	{
		return "SheetDefinition [sheetName=" + sheetName + ", sheetIndex=" + sheetIndex + ", skipWhenNull=" + skipWhenNull + ", recordDefinitionList=" + recordDefinitionList + ", cellDefinitionList=" + cellDefinitionList + "]";
	}

}
