package com.adms.imex.excelformat;

import com.adms.imex.enums.RecordEndComparator;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class BeginRecordCondition extends CellDefinition {

	@XStreamAsAttribute
	private RecordEndComparator comparator;

	@XStreamAsAttribute
	private String checkValue;

	@XStreamAsAttribute
	private String checkType;

	@XStreamAsAttribute
	private Boolean firstRowAsData;

	public RecordEndComparator getComparator()
	{
		return comparator;
	}

	public void setComparator(RecordEndComparator comparator)
	{
		this.comparator = comparator;
	}

	public String getCheckValue()
	{
		return checkValue;
	}

	public void setCheckValue(String checkValue)
	{
		this.checkValue = checkValue;
	}

	public String getCheckType()
	{
		return checkType;
	}

	public void setCheckType(String checkType)
	{
		this.checkType = checkType;
	}

	public Boolean getFirstRowAsData()
	{
		return firstRowAsData;
	}

	public void setFirstRowAsData(Boolean firstRowAsData)
	{
		this.firstRowAsData = firstRowAsData;
	}

	@Override
	public String toString()
	{
		return "BeginRecordCondition [comparator=" + comparator + ", checkValue=" + checkValue + ", checkType=" + checkType + ", firstRowAsData=" + firstRowAsData + "]";
	}

}
