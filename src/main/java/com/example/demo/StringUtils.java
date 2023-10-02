package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caceis.StringUtility;
import com.example.demo.model.Record;

public class StringUtils {

	private final static Logger logger = LoggerFactory.getLogger(StringUtils.class);

	public static String replaceBlankByNull(String value) {
		if (value == null)
			return value;
		else
			return "".equalsIgnoreCase(value.trim()) ? null : value;
	}

	public static String generateCRE(Record record) {
		// on ajoute l'id dans le message sortant avant envoi
		String idFormatted = StringUtility.formatString((record.getId() + ""), "16", "0", "LEFT", "NONE");
		String prefix = record.getMqCre().substring(0, 18);
		String sufix = record.getMqCre().substring(34);
		String message = prefix + idFormatted + sufix;

		return message;
	}

	public static void extractSensitiveInfoFromLine(String line) {
		// String testLine = "LN20500793 NY0000000000000001L20500793 L20500793 6778
		// 969618085CX 969618085C 1205359032 SECFIN CTRDBDFCNNNN181290224446000 BNK
		// 05000590220 CBFP2705102392060000011206 CPT CBFP27051 0011206 CPT CBFPL
		// TPRO0000000040000008F0000000000000000
		// 000000000000000000001000000000080000000000000000 FR0011619436FRTR 2 1 4 05 25
		// 2YEUR000013500000000200009913250000090000998274430009EURV0000100000000008 Y
		// 0000000000000000202309272023092820230927202309272049123120230927000000000000000EUR2D000013476704805EUR2C000013476704805EUR2C0000100000000008000000000000000EUR2C0000000000000008000000000000000EUR2C0000103460000008000000000000000EUR2C000013476704805EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2C000000000000000EUR2
		// 000013476704805EUR2C000000000000000 0 000000000000000 0 000000000000000 0
		// 000000000000000 0 000000000000000 0 000000000000000 0 I L20500793 05000590220
		// CBF-BDF ";

		int[] fixedSizes = { 16, 1, 1, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 6, 6, 1, 3, 3, 1, 1, 1, 1, 1,
				5, 10, 10, 7, 20, 20, 34, 3, 4, 5, 10, 10, 7, 20, 20, 34, 3, 4, 5, 10, 10, 7, 20, 20, 34, 3, 4, 1, 1, 1,
				2, 1, 15, 1, 1, 15, 1, 1, 20, 15, 1, 15, 1, 15, 1, 3, 40, 12, 18, 1, 3, 15, 1, 15, 1, 15, 1, 3, 1, 15,
				1, 12, 18, 1, 3, 15, 1, 8, 8, 8, 8, 8, 8, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 1, 15, 3, 1, 1, 15,
				1, 15, 3, 1, 1, 15, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15,
				3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15,
				3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 15, 3, 1, 1, 16, 16, 16, 65, 65, 65, 65, 65, 4, 1, 4, 20, 3, 30, 30,
				5, 35, 3, 10, 10, 10, 35, 20, 20, 20, 35, 35, 20, 20, 3, 20, 1, 65, 220 };
		int start = 0;
		int end = 0;
		for (int i = 0; i < fixedSizes.length; i++) {

			end = start + fixedSizes[i];
			end = Math.min(end, line.length());
			logger.trace("{} {}", end, line.length());
			logger.debug("item {} taille {}= ({})", i + 1, fixedSizes[i], line.substring(start, end));

			start = end;

		}
	}
}
