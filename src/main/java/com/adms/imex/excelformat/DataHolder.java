package com.adms.imex.excelformat;

import java.math.BigDecimal;
import java.util.List;

public interface DataHolder {

	public Object getValue();
	
	public String getStringValue();
	
	public Integer getIntValue();
	
	public BigDecimal getDecimalValue();
	
	public DataHolder setValue(Object value);

	public void put(String fieldName, DataHolder dataHolder);

	public DataHolder get(String fieldName);

	public void remove(String fieldName);

	public void putDataList(String fieldName, List<DataHolder> record);

	public List<DataHolder> getDataList(String fieldName);

	public String printValues();

	public List<String> getKeyList();
	
	public void setSheetNameByIndex(int index, String sheetName);
	
	public String getSheetNameByIndex(int index);

	public void removeSheetByIndex(int index);
}
