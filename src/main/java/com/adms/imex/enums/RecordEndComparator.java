package com.adms.imex.enums;

public enum RecordEndComparator implements BaseEnum {

	EQ("EQ", "Equal"), NE("NE", "Not Equal"), GT("GT", "Greater Than"), GE("GE", "Greater Than or Equal"), LT("LT", "Less Than"), LE("LE", "Less Than or Equal"), BLANK("BLANK", "Blank Cell or Null Cell");

	private String code;
	private String description;

	public String getCode()
	{
		return this.code;
	}

	public String getDescription()
	{
		return this.description;
	}

	private RecordEndComparator(String code, String description)
	{
		this.code = code;
		this.description = description;
	}

}
