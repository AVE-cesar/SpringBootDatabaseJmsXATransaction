package com.example.demo;

import com.caceis.StringUtility;
import com.example.demo.model.Record;

public class StringUtils {

	public static String replaceBlankByNull(String value) {
		if (value == null) return value;
		else 
		return "".equalsIgnoreCase(value.trim()) ? null : value;
	}
	
	public static String generateCRE(Record record) { 
		// on ajoute l'id dans le message sortant avant envoi
		String idFormatted = StringUtility.formatString((record.getId()+""),"16","0","LEFT","NONE");
		String prefix = record.getMqCre().substring(0, 18);
		String sufix = record.getMqCre().substring(34);
		String message = prefix + idFormatted+sufix;
		
		return message;
	}
}
