package com.adms.imex.excelformat;

import com.adms.imex.enums.RecordEndComparator;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
//import com.thoughtworks.xstream.annotations.XStreamConverter;

public class EndRecordCondition extends CellDefinition {

	@XStreamAsAttribute
//	@XStreamConverter(RecordEndComparatorConverter.class)
	private RecordEndComparator comparator;

	@XStreamAsAttribute
	private String checkValue;

	@XStreamAsAttribute
	private String checkType;

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

	@Override
	public String toString()
	{
		return "EndRecordCondition [comparator=" + comparator + ", checkValue=" + checkValue + ", checkType=" + checkType + ", getRow()=" + getRow() + ", getColumn()=" + getColumn() + ", getFieldName()=" + getFieldName() + ", getDataType()=" + getDataType() + ", getDataFormat()=" + getDataFormat()
				+ "]";
	}

}
