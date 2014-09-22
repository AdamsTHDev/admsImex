package com.adms.imex.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.adms.imex.util.XmlParser;

public class TestXtream {

	private static Map<String, Class<?>> aliasTypeMap;

	public static void main(String[] args)
			throws Exception
	{

		Person obj = new Person();
		obj.setFirstName("aaa");
		obj.setLastName("bbb");
		obj.setDob(new Date());
		obj.setAddress("123/456 555");
		obj.setAge(23);
		obj.setPhoneList(new ArrayList<Phone>());

		Phone phone = null;
		phone = new Phone();
		phone.setPhoneType("home");
		phone.setPhoneNo("1111111111");
		obj.getPhoneList().add(phone);
		phone = new Phone();
		phone.setPhoneType("office");
		phone.setPhoneNo("2222222222");
		obj.getPhoneList().add(phone);
		phone = new Phone();
		phone.setPhoneType("mobile");
		phone.setPhoneNo("3333333333");
		obj.getPhoneList().add(phone);

		aliasTypeMap = new HashMap<String, Class<?>>();
		aliasTypeMap.put("Person", Person.class);
		aliasTypeMap.put("Phone", Phone.class);

		String xml = new XmlParser().toXml(obj, aliasTypeMap, null);
		System.out.println(xml);
		System.out.println(new XmlParser().fromXml(xml, aliasTypeMap, null));
	}

}
