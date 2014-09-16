package com.adms.imex.util;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adms.imex.excelformat.CellDefinition;
import com.adms.imex.excelformat.DataSetDefinition;
import com.adms.imex.excelformat.ExcelColumnConverter;
import com.adms.imex.excelformat.FileFormatDefinition;
import com.adms.imex.excelformat.RecordDefinition;
import com.adms.imex.excelformat.SheetDefinition;
import com.thoughtworks.xstream.converters.Converter;

public class TestXmlParser {

	private InputStream fileFormat;
	private Map<String, Class<?>> aliasTypeMap;
	private List<Converter> converterList;

	@Before
	public void setUp()
			throws Exception
	{
		this.fileFormat = URLClassLoader.getSystemResourceAsStream("FileFormat.xml");
		System.out.println(this.fileFormat);

		this.aliasTypeMap = new HashMap<String, Class<?>>();
		this.aliasTypeMap.put("FileFormat", FileFormatDefinition.class);
		this.aliasTypeMap.put("DataSet", DataSetDefinition.class);
		this.aliasTypeMap.put("DataSheet", SheetDefinition.class);
		this.aliasTypeMap.put("DataRecord", RecordDefinition.class);
		this.aliasTypeMap.put("DataCell", CellDefinition.class);

		this.converterList = new ArrayList<Converter>();
		this.converterList.add(new ExcelColumnConverter());
	}

	@After
	public void tearDown()
			throws Exception
	{
		if (this.fileFormat != null)
		{
			this.fileFormat.close();
		}
	}

	@Test
	public void testFromXml()
	{
		System.out.println(new XmlParser().fromXml(this.fileFormat, this.aliasTypeMap, this.converterList));
		fail("Not yet implemented");
	}

}
