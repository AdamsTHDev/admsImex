package com.adms.imex.util;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;

public class XmlParser {

	public String toXml(Object object, Map<String, Class<?>> aliasTypeMap, List<Converter> converterList)
	{
		return getXStream(aliasTypeMap, converterList).toXML(object);
	}

	public Object fromXml(String xml, Map<String, Class<?>> aliasTypeMap, List<Converter> converterList)
	{
		return getXStream(aliasTypeMap, converterList).fromXML(xml);
	}

	public Object fromXml(File xml, Map<String, Class<?>> aliasTypeMap, List<Converter> converterList)
	{
		return getXStream(aliasTypeMap, converterList).fromXML(xml);
	}

	public Object fromXml(InputStream xml, Map<String, Class<?>> aliasTypeMap, List<Converter> converterList)
	{
		return getXStream(aliasTypeMap, converterList).fromXML(xml);
	}

	private XStream getXStream(Map<String, Class<?>> aliasTypeMap, List<Converter> converterList)
	{
		XStream xs = new XStream();
		xs.setMode(XStream.NO_REFERENCES);
		xs.autodetectAnnotations(true);

		if (aliasTypeMap != null)
		{
			for (String key : aliasTypeMap.keySet())
			{
				xs.alias(key, aliasTypeMap.get(key));
			}
		}

		if (CollectionUtils.isNotEmpty(converterList))
		{
			for (Converter converter : converterList)
			{
				xs.registerConverter(converter);
			}
		}

		return xs;
	}
}
