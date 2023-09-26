package com.caceis;

public class NumberUtility {
	
	/**
	 * Convert a string to float ignoring the decimal separator (comma or dot)
	 * @param value.
	 * @return converted float value, 0 if value is null or empty
	 */
	public static float convertToFloat(String value) {
		if (value != null && !"".equals(value.trim())) {
			value = value.replace(",", ".");
			try {
				return Float.parseFloat(value);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return 0f;
		
	}
	
	
	public static String returnPositiveValueStringFormat(String value) {
		if (value == null) {
			return "0";
		}
		else {
			value = value.replace(",", ".");
			float value_f= convertToFloat(value);
			if (value_f <= 0) {
				return "0";
			}
			return value;
		}
	}

}
