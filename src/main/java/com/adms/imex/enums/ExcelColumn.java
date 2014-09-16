package com.adms.imex.enums;

public enum ExcelColumn implements BaseEnum {

	A("A", 0), B("B", 1), C("C", 2), D("D", 3), E("E", 4),
	F("F", 5), G("G", 6), H("H", 7), I("I", 8), J("J", 9),
	K("K", 10), L("L", 11), M("M", 12), N("N", 13), O("O", 14),
	P("P", 15), Q("Q", 16), R("R", 17), S("S", 18), T("T", 19),
	U("U", 20), V("V", 21), W("W", 22), X("X", 23), Y("Y", 24), Z("Z", 25),

	AA("AA", 26), AB("AB", 27), AC("AC", 28), AD("AD", 29), AE("AE", 30),
	AF("AF", 31), AG("AG", 32), AH("AH", 33), AI("AI", 34), AJ("AJ", 35),
	AK("AK", 36), AL("AL", 37), AM("AM", 38), AN("AN", 39), AO("AO", 40),
	AP("AP", 41), AQ("AQ", 42), AR("AR", 43), AS("AS", 44), AT("AT", 45),
	AU("AU", 46), AV("AV", 47), AW("AW", 48), AX("AX", 49), AY("AY", 50), AZ("AZ", 51);

	private String code;
	private int columnIndex;
	private String description;

	public String getCode()
	{
		return this.code;
	}

	public int getColumnIndex()
	{
		return this.columnIndex;
	}

	public String getDescription()
	{
		return this.description;
	}

	private ExcelColumn(String code, int columnIndex)
	{
		this.code = code;
		this.columnIndex = columnIndex;
	}

}
