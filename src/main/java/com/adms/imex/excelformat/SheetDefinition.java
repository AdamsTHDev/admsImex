package com.adms.imex.excelformat;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class SheetDefinition extends CellDefinitionContainer {

	@XStreamAsAttribute
	private String sheetName;

	@XStreamAsAttribute
	private String outputSheetName;

	@XStreamAsAttribute
	private Integer sheetIndex;

	@XStreamAsAttribute
	private Boolean skipWhenNull;

	@XStreamAsAttribute
	private Boolean repeatSheet;

	@XStreamAsAttribute
	private Boolean repeatUntilNull;

	@XStreamAsAttribute
	private Integer repeatUntilIndex;

	@XStreamAsAttribute
	private String repeatUntilSheetname;

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

	public String getOutputSheetName()
	{
		return outputSheetName;
	}

	public void setOutputSheetName(String outputSheetName)
	{
		this.outputSheetName = outputSheetName;
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

	public Boolean getRepeatSheet()
	{
		return repeatSheet;
	}

	public void setRepeatSheet(Boolean repeatSheet)
	{
		this.repeatSheet = repeatSheet;
	}

	public Boolean getRepeatUntilNull()
	{
		return repeatUntilNull;
	}

	public void setRepeatUntilNull(Boolean repeatUntilNull)
	{
		this.repeatUntilNull = repeatUntilNull;
	}

	public Integer getRepeatUntilIndex()
	{
		return repeatUntilIndex;
	}

	public void setRepeatUntilIndex(Integer repeatUntilIndex)
	{
		this.repeatUntilIndex = repeatUntilIndex;
	}

	public String getRepeatUntilSheetname()
	{
		return repeatUntilSheetname;
	}

	public void setRepeatUntilSheetname(String repeatUntilSheetname)
	{
		this.repeatUntilSheetname = repeatUntilSheetname;
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
		return "SheetDefinition [sheetName=" + sheetName + ", outputSheetName=" + outputSheetName + ", sheetIndex=" + sheetIndex + ", skipWhenNull=" + skipWhenNull + ", recordDefinitionList=" + recordDefinitionList + "]";
	}

}
