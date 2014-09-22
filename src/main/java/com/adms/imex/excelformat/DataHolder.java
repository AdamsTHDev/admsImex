package com.adms.imex.excelformat;

import java.util.List;

public interface DataHolder {

	public Object getValue();

	public void setValue(Object value);

	public void put(String fieldName, DataHolder dataHolder);

	public DataHolder get(String fieldName);

	public void putDataList(String fieldName, List<DataHolder> record);

	public List<DataHolder> getDataList(String fieldName);

	public String printValues();

	public List<String> getKeyList();
}
