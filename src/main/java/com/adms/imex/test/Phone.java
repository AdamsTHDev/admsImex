package com.adms.imex.test;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Phone {

	@XStreamAsAttribute
	String phoneType;
	String phoneNo;

	public String getPhoneType()
	{
		return phoneType;
	}

	public void setPhoneType(String phoneType)
	{
		this.phoneType = phoneType;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	@Override
	public String toString()
	{
		return "Phone [phoneType=" + phoneType + ", phoneNo=" + phoneNo + "]";
	}

}