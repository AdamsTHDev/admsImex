package com.adms.imex.excelformat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class FileFormatDefinition {

	@XStreamAsAttribute
	private String fileName;

	@XStreamAsAttribute
	private String outputFileName;

	@XStreamAsAttribute
	private String templateFile;

	@XStreamAsAttribute
	private String templateSheetName;

	@XStreamAsAttribute
	private Boolean removeTemplateAfterComplete;

	@XStreamAsAttribute
	private String fileType;

	@XStreamAsAttribute
	private String locale;

	@XStreamAlias("DataSet")
	private DataSetDefinition dataSetDefinition;

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getOutputFileName()
	{
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName)
	{
		this.outputFileName = outputFileName;
	}

	public String getTemplateFile()
	{
		return templateFile;
	}

	public String getTemplateSheetName()
	{
		return templateSheetName;
	}

	public void setTemplateSheetName(String templateSheetName)
	{
		this.templateSheetName = templateSheetName;
	}

	public Boolean getRemoveTemplateAfterComplete()
	{
		return removeTemplateAfterComplete;
	}

	public void setRemoveTemplateAfterComplete(Boolean removeTemplateAfterComplete)
	{
		this.removeTemplateAfterComplete = removeTemplateAfterComplete;
	}

	public void setTemplateFile(String templateFile)
	{
		this.templateFile = templateFile;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return dataSetDefinition;
	}

	public void setDataSetDefinition(DataSetDefinition dataSetDefinition)
	{
		this.dataSetDefinition = dataSetDefinition;
	}

	@Override
	public String toString()
	{
		return "FileFormatDefinition [fileName=" + fileName + ", outputFileName=" + outputFileName + ", templateFile" + templateFile + ", templateSheetName=" + templateSheetName + ", removeTemplateAfterComplete=" + removeTemplateAfterComplete + ", fileType=" + fileType + ", locale=" + locale + ", dataSetDefinition=" + dataSetDefinition + "]";
	}

}
