package com.adms.imex.excelformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.adms.imex.util.XmlParser;
import com.thoughtworks.xstream.converters.Converter;

public class ExcelFormat {

	private FileFormatDefinition fileFormat;

	public ExcelFormat(File fileFormat)
			throws FileNotFoundException
	{
		this(new FileInputStream(fileFormat));
	}

	public ExcelFormat(InputStream fileFormat)
	{
		this.fileFormat = (FileFormatDefinition) new XmlParser().fromXml(fileFormat, getAliasTypeMap(), getConverterList());
		setRevertReference();
	}

	public FileFormatDefinition getFileFormat()
	{
		return fileFormat;
	}

	private void setRevertReference()
	{
		for (SheetDefinition s : this.fileFormat.getDataSetDefinition().getSheetDefinitionList())
		{
			s.setFileFormatDefinition(this.fileFormat);

			if (CollectionUtils.isNotEmpty(s.getCellDefinitionList()))
			{
				for (CellDefinition c : s.getCellDefinitionList())
				{
					c.setSheetDefinition(s);
				}
			}

			if (CollectionUtils.isNotEmpty(s.getRecordDefinitionList()))
			{
				for (RecordDefinition r : s.getRecordDefinitionList())
				{
					r.setSheetDefinition(s);

					if (CollectionUtils.isNotEmpty(r.getCellDefinitionList()))
					{
						for (CellDefinition c : r.getCellDefinitionList())
						{
							c.setSheetDefinition(s);
						}
					}
				}
			}
		}
	}

	private Map<String, Class<?>> getAliasTypeMap()
	{
		Map<String, Class<?>> aliasTypeMap = new HashMap<String, Class<?>>();
		aliasTypeMap.put("FileFormat", FileFormatDefinition.class);
		aliasTypeMap.put("DataSet", DataSetDefinition.class);
		aliasTypeMap.put("DataSheet", SheetDefinition.class);
		aliasTypeMap.put("DataRecord", RecordDefinition.class);
		aliasTypeMap.put("DataCell", CellDefinition.class);

		return aliasTypeMap;
	}

	private List<Converter> getConverterList()
	{
		List<Converter> converterList = new ArrayList<Converter>();

		return converterList;
	}

	private boolean isValidFileFormat()
			throws Exception
	{
		if (this.fileFormat == null)
		{
			throw new Exception("fileFormat is null, call setFileFormat() first!");
		}

		return true;
	}

	public void writeExcel(OutputStream output, DataHolder fileDataHolder)
			throws Exception
	{
		if (isValidFileFormat())
		{
			new ExcelFileFormatWriter(this, fileDataHolder).write(output);
		}
	}

	public DataHolder readExcel(InputStream input)
			throws Exception
	{
		if (!isValidFileFormat())
		{
			throw new Exception("invalid fileFormat");
		}

		return new ExcelFormatReader(this).read(input);
	}
}
