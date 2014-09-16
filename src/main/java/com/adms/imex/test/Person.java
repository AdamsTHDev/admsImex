package com.adms.imex.test;

import java.util.Date;
import java.util.List;

public class Person {

	private String firstName;
	private String lastName;
	private Date dob;
	private String address;
	private List<Phone> phoneList;
	private int age;

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public Date getDob()
	{
		return dob;
	}

	public void setDob(Date dob)
	{
		this.dob = dob;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public List<Phone> getPhoneList()
	{
		return phoneList;
	}

	public void setPhoneList(List<Phone> phoneList)
	{
		this.phoneList = phoneList;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	@Override
	public String toString()
	{
		return "TestXtream [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", address=" + address + ", phoneList=" + phoneList + ", age=" + age + "]";
	}
}
