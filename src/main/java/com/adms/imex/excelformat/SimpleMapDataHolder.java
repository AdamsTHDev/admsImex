package com.adms.imex.excelformat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.ListOrderedMap;

public class SimpleMapDataHolder implements DataHolder {

	private Map<String, Object> dataHolderMap;
	private Object value;

	public SimpleMapDataHolder()
	{
		this.dataHolderMap = new ListOrderedMap<String, Object>();
	}

	public String printValues()
	{
		StringBuilder sb = new StringBuilder("");

		if (this.dataHolderMap != null)
		{
			int size = this.dataHolderMap.keySet().size();
			int count = 1;
			for (String key : this.dataHolderMap.keySet())
			{
				sb.append("[").append(key).append(":").append(((DataHolder) this.dataHolderMap.get(key)).getValue()).append("]").append(count++ < size ? ", " : "");
			}
		}

		return sb.toString();
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void put(String fieldName, DataHolder value)
	{
		this.dataHolderMap.put(fieldName, value);
	}

	public DataHolder get(String fieldName)
	{
		return (DataHolder) this.dataHolderMap.get(fieldName);
	}

	public void putDataList(String fieldName, List<DataHolder> record)
	{
		this.dataHolderMap.put(fieldName, record);
	}

	@SuppressWarnings("unchecked")
	public List<DataHolder> getDataList(String fieldName)
	{
		return (List<DataHolder>) this.dataHolderMap.get(fieldName);
	}

	public List<String> getKeyList()
	{
		return CollectionUtils.collate(this.dataHolderMap.keySet(), new HashSet<String>());
	}

}
