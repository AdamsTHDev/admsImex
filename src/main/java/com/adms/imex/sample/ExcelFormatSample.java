package com.adms.imex.sample;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLClassLoader;

import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class ExcelFormatSample {

	public static void main(String[] args)
			throws Exception
	{
		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream("FileFormat.xml");
		InputStream sampleReport = URLClassLoader.getSystemResourceAsStream("sampleReport.xlsx");

		ExcelFormat ex = new ExcelFormat(fileFormat);
		DataHolder dataHolder = ex.readExcel(sampleReport);

		System.out.println(dataHolder.get("Sheet1").get("reportAsOf").getValue());
		System.out.println(dataHolder.get("Sheet1").get("testDateString").getValue());
		System.out.println(dataHolder.get("Sheet1").getDataList("personList").size());

		for (DataHolder person : dataHolder.get("Sheet1").getDataList("personList"))
		{
			System.out.println(person.get("index").getValue() + ", " + person.get("firstName").getValue() + ", " + person.get("total").getValue());
		}

		fileFormat.close();
		sampleReport.close();

		OutputStream sampleOutput = new FileOutputStream("D:/testOutput.xlsx");
		ex.writeExcel(sampleOutput, dataHolder);
		sampleOutput.close();
	}

}
