package com.adms.imex.excelformat;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.ListOrderedMap;

public class SimpleMapDataHolder implements DataHolder {

	private Map<String, Object> dataHolderMap;
	private Object value;
	private String[] sheetNames = new String[99];

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
	
	public String getStringValue() {
		return (String) (null == value ? value : String.valueOf(value));
	}
	
	public Integer getIntValue() {
		return (Integer) (null == value ? value : Integer.parseInt(getStringValue()));
	}
	
	public BigDecimal getDecimalValue() {
		return (BigDecimal) (null == value ? value : new BigDecimal(getStringValue()));
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

	public void remove(String fieldName)
	{
		this.dataHolderMap.remove(fieldName);
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
	
	public void setSheetNameByIndex(int index, String sheetName)
	{
		this.sheetNames[index] = sheetName;
	}
	
	public String getSheetNameByIndex(int index)
	{
		return this.sheetNames[index];
	}

}
