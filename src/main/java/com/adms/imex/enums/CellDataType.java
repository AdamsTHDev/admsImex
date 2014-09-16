package com.adms.imex.enums;

public enum CellDataType implements BaseEnum {

	TEXT("TEXT", "Text or String value"), NUMBER("NUMBER", "Number"), DATE("DATE", "Date"), BOOLEAN("BOOLEAN", "Boolean"), BLANK("BLANK", "Blank"), FORMULAR("FORMULAR", "Formular"), ERROR("ERROR", "Error");

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

	private CellDataType(String code, String description)
	{
		this.code = code;
		this.description = description;
	}

}
