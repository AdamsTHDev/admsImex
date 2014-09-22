package com.adms.imex.excelformat;

import org.apache.commons.lang3.StringUtils;

import com.adms.imex.enums.CellDataType;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CellDataTypeConverter implements Converter {

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type)
	{
		return CellDataType.class.isAssignableFrom(type);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		if (source != null && CellDataType.class.isAssignableFrom(source.getClass()))
		{
			writer.addAttribute("dataType", ((CellDataType) source).getCode());
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		return StringUtils.isNotBlank(reader.getValue()) ? CellDataType.valueOf(reader.getValue().toUpperCase()) : null;
	}

}
