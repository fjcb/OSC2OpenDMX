package com.fjacob.osc2dmx.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ExceptionLog {

	public static void log(String source, String error) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String dateTime = sdf.format(time);
		
		String output = "<" + dateTime + "> [" + source + "] " + error;
		System.out.println("Writing to Log... " + output);
		
		
		try {
			FileWriter log = new FileWriter(Constants.LOG_FILE, true);		//file, append = true
			PrintWriter writer = new PrintWriter(log);
			writer.println(output);								//write to file and close it
			writer.close();
		} catch (IOException e) {
			System.err.println("ExceptionLog cannot write to log file!");
			e.printStackTrace();
		}
		
	}
	
}
