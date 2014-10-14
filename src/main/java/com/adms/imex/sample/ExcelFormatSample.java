package com.adms.imex.sample;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.util.List;

import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

public class ExcelFormatSample {

	public static void main(String[] args)
			throws Exception
	{
		InputStream fileFormat = URLClassLoader.getSystemResourceAsStream("FileFormat.xml");
		InputStream sampleReport = URLClassLoader.getSystemResourceAsStream("sampleReport.xlsx");

		ExcelFormat ex = new ExcelFormat(fileFormat);
		DataHolder fileDataHolder = ex.readExcel(sampleReport);

		List<String> sheetNames = fileDataHolder.getKeyList();
		for (String sheetName : sheetNames)
		{
			DataHolder sheetDataHolder = fileDataHolder.get(sheetName);

			System.out.println(sheetDataHolder.get("reportAsOf").getValue());
			System.out.println(sheetDataHolder.get("testDateString").getValue());
			
			System.out.println(sheetDataHolder.getDataList("personList").size());

			List<DataHolder> personList = sheetDataHolder.getDataList("personList");
			for (DataHolder person : personList)
			{
				System.out.println(person.printValues());
			}
			
			System.out.println(sheetDataHolder.getDataList("testList").size());

			List<DataHolder> testList = sheetDataHolder.getDataList("testList");
			for (DataHolder test : testList)
			{
				System.out.println(test.printValues());
			}
		}

		fileFormat.close();
		sampleReport.close();

		OutputStream sampleOutput = new FileOutputStream("D:/testOutput.xlsx");
		ex.writeExcel(sampleOutput, fileDataHolder);
		sampleOutput.close();
	}

}
