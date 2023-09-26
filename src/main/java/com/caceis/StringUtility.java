package com.caceis;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Description: Generated Code of the class StringUtilityImpl
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002 - 2009
 * </p>
 * <p>
 * Company: Vermeg
 * </p>
 * .
 * 
 * @author Palmyra Code Generation Tool
 * @version 12.0
 */

public class StringUtility {

	private static final String ZERO = "0";

	private static final String NONE = "NONE";

	private static final String PROP_EXTENSION = "extension";

	/** The Constant LEFT. */
	private static final String LEFT = "LEFT";

	/** The Constant RIGHT. */
	private static final String RIGHT = "RIGHT";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2327550963734496047L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caceis.cid.ctrl.StringUtility#formatString(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static String formatString(final String chaine, final String taille, final String pad, final String sensPad, final String sensTroncate) {

		String result = null;
		if (chaine != null) {
			final int taillePad = Integer.parseInt(taille);
			final int length = chaine.length();
			final int pads = taillePad - length;
			if (RIGHT.equalsIgnoreCase(sensPad)) {
				if (pads < 0) {
					result = troncateChaine(chaine, sensTroncate, taillePad, length);
				} else if (pads == 0) {
					result = chaine;
				} else {
					result = padRight(chaine, taillePad, pad.toCharArray()[0]);
				}
			} else if (LEFT.equalsIgnoreCase(sensPad)) {
				if (pads < 0) {
					result = troncateChaine(chaine, sensTroncate, taillePad, length);
				} else if (pads == 0) {
					result = chaine;
				} else {
					result = padLeft(chaine, taillePad, pad.toCharArray()[0]);
				}
			} else {
				if (pads < 0) {
					result = troncateChaine(chaine, sensTroncate, taillePad, length);
				} else {
					result = chaine;
				}

			}
		}
		return result;

	}

	/**
	 * Troncate chaine.
	 * 
	 * @param chaine
	 *            the chaine
	 * @param sensTroncate
	 *            the sens troncate
	 * @param taillePad
	 *            the taille pad
	 * @param length
	 *            the length
	 * @return the string
	 */
	private static String troncateChaine(final String chaine, final String sensTroncate, final int taillePad, final int length) {
		String result;
		if (RIGHT.equalsIgnoreCase(sensTroncate)) {
			result = chaine.substring(length - taillePad, length);
		} else if (LEFT.equalsIgnoreCase(sensTroncate)) {
			result = chaine.substring(0, taillePad);
		} else {
			result = chaine;
		}
		return result;
	}

	/**
	 * Pad right.
	 * 
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param pad
	 *            the pad
	 * @return the string
	 */
	public static String padRight(final String str, final int size, final char pad) {

		String result = null;
		if (str != null) {
			final int pads = size - str.length();
			if (pads <= 0) {
				result = str; // returns original String when possible
			} else {
				result = str.concat(padding(pads, pad));
			}
		}
		return result;

	}

	/**
	 * Pad left.
	 * 
	 * @param str
	 *            the str
	 * @param size
	 *            the size
	 * @param pad
	 *            the pad
	 * @return the string
	 */
	protected static String padLeft(final String str, final int size, final char pad) {
		String result = null;
		if (str != null) {
			final int pads = size - str.length();
			if (pads <= 0) {
				result = str; // returns original String when possible
			} else {
				result = padding(pads, pad).concat(str);
			}
		}
		return result;

	}

	/**
	 * Padding.
	 * 
	 * @param repeat
	 *            the repeat
	 * @param padChar
	 *            the pad char
	 * @return the string
	 * @throws IndexOutOfBoundsException
	 *             the index out of bounds exception
	 */
	private static String padding(final int repeat, final char padChar) throws IndexOutOfBoundsException {
		final char[] buf = new char[repeat];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = padChar;
		}
		return new String(buf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caceis.cid.ctrl.StringUtility#getStringFromBytes(byte[])
	 */
	public String getStringFromBytes(final byte[] bytes) throws ServiceException {
		return bytes == null ? null : new String(bytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caceis.cid.ctrl.StringUtility#isNumeric(java.lang.String)
	 */
	public Boolean isNumeric(final String chaine) throws ServiceException {
		Boolean isNumber = false;
		if (chaine != null) {
			try {
				new BigDecimal(chaine);
				isNumber = true;
			} catch (final Exception e) {
				isNumber = false;
			}
		}
		return isNumber;
	}

	/**
	 * Format decimal number.
	 * 
	 * @param number
	 *            the number
	 * @param decimal
	 *            the decimal
	 * @param decSeperator
	 *            the dec seperator
	 * @param taille
	 *            the taille
	 * @param pad
	 *            the pad
	 * @param sensPad
	 *            the sens pad
	 * @param sensTroncate
	 *            the sens troncate
	 * @return the string
	 * @throws ServiceException
	 *             the service exception
	 */
	public static String formatDecimalNumber(final String number, final String decimal, final String decSeperator, final String taille, final String pad, final String sensPad, final String sensTroncate)  {
		String result = null;
		final int decInt = Integer.parseInt(decimal);
		if (number == null) {
			result = null;
		} else {
			final int idx = number.indexOf(decSeperator);
			if (idx == -1) {
				if (decInt==0) {
					result = formatString(number, taille, pad, sensPad, sensTroncate);
				} else {
					final Integer somme = number.length() + decInt;
					final String finalNumber = formatString(number, somme.toString(), ZERO, RIGHT, NONE);
					result = formatString(finalNumber, taille, pad, sensPad, sensTroncate);
				}
			} else {
				final String dec = number.substring(idx + 1);
				final String ent = number.substring(0, idx);
				if (dec.length() == decInt) {
					result = formatString(ent + dec, taille, pad, sensPad, sensTroncate);
				} else if (dec.length() < decInt) {
					final Integer diff = decInt - dec.length();
					final String finalNumber = formatString(ent + dec, Integer.toString(((ent + dec).length() + diff)), ZERO, RIGHT, NONE);
					result = formatString(finalNumber, taille, pad, sensPad, sensTroncate);
				} else {
					final String decFinal = dec.substring(0, decInt);
					result = formatString(ent + decFinal, taille, pad, sensPad, sensTroncate);

				}

			}

		}

		return result;
	}

	public String getValeurMDM(final String chaine, final String champ) throws ServiceException {

		if (chaine != null) {
			final String[] split = chaine.split("\\|");
			for (final String str : split) {
				final String[] obj = str.split(":");
				if (obj[0].equals(champ)) {
					return obj[1] != null && obj[1].equals("null") ? null : obj[1];
				}
			}
		}
		return null;
	}

	public String formatStr(final String str) throws ServiceException {
		String result = null;
		if (str != null) {
			result = str.trim().toUpperCase();
		}
		return result;
	}

	public String replaceStrCMDTY(final String str) throws ServiceException {
		String result = null;
		if (str != null) {
			if (str.toUpperCase().contains("CMDTY")) {
				result = str.replace("CMDTY", "COMDTY");
			} else {
				result = str;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caceis.cid.ctrl.StringUtility#getFirstElementFieldValue(java.util
	 * .Collection, java.lang.String)
	 */
	/*
	public String getFirstElementFieldValue(final Collection<CompositeValue> collection, final String fieldName) throws ServiceException {

		if (!collection.isEmpty()) {
			final CompositeValue first = collection.iterator().next();
			if (first.get(fieldName) != null) {
				return first.get(fieldName).toString();
			}
		}
		return null;
	}

	public String getLastElementFieldValue(final Collection<CompositeValue> collection, final String fieldName) throws ServiceException {

		if (!collection.isEmpty()) {
			ArrayList<CompositeValue> list = new ArrayList<>(collection);
			CompositeValue compositeValue = list.get(collection.size()-1);//get the last one
			if (compositeValue.get(fieldName) != null) {
				return compositeValue.get(fieldName).toString();
			}
		}
		return null;
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caceis.cid.ctrl.StringUtility#getPropertyFromFileName(java.lang.String ,
	 * java.lang.String)
	 */
	public String getPropertyFromFileName(final String fileName, final String property) throws ServiceException {
		if (fileName != null) {
			if (PROP_EXTENSION.equals(property)) {
				final int indexExt = fileName.lastIndexOf('.');
				if (indexExt > 0) {
					return fileName.substring(indexExt + 1);
				}
			}
		}
		return null;
	}

	public String replaceString(final String chaine, final String text1, final String text2) throws ServiceException {
		String result = null;
		if (chaine != null) {
			result = chaine.replace(text1, text2);
		}
		return result;

	}

	/**
	 * Mapper la collection CompositeFees vers une structure plate
	 */
	public String getCompositeFeesData(final String key, final String feesTypeValue/*, final CompositeValue compositeFees*/) throws ServiceException {
		String result = null;
		/*
		// Type de frais
		final String feesType = "feesType.feesType";
		// Champ à déterminer
		final String keyCriteria = key + "." + key;
		// Parcourir la séquence CompositeFees
		final Collection<Object> collection = ((VKeys) compositeFees).values();
		for (final Object object : collection) {
			if (feesTypeValue.equals(((VKeys) object).get(feesType))) {
				result = (String) ((VKeys) object).get(keyCriteria);
				break;
			}
		}*/
		return result;
	}

	public Boolean comparFormatString(String chaine, String taille, String pad, String sensPad, String sensTroncate, String value) throws ServiceException {
		if (value != null) {
			//return value.equals(StringUtilityCaller.formatString(chaine, taille, pad, sensPad, sensTroncate));
		}
		return false;
	}

	/**
	 * This method checks if the first string has the same sequence of the second
	 * string
	 * 
	 * @param str1
	 *            first string
	 * @param str2
	 *            second string
	 * @return true if its contains and false if does not contains
	 */
	public Boolean isContains(String str1, String str2) throws ServiceException {
		if (str1 != null && str2 != null) {
			return str1.contains(str2);
		}
		return false;
	}

	/**
	 * This method takes the string from the position 1 to position 2
	 * 
	 * @param str
	 * @param pos1
	 *            position 1
	 * @param pos2
	 *            position 2
	 * @return an extraction from string from pos1 to pos2
	 */
	public String getSubString(String str, String pos1, String pos2) throws ServiceException {
		if (str != null) {
			return str.substring(Integer.valueOf(pos1), Integer.valueOf(pos2)).toString();
		}
		return null;

	}

	public static String getTranslatedMessage(final String codeErreur) {
		if (codeErreur == null) {
			return null;
		}
		String error = "";
		
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caceis.cid.ctrl.StringUtility#getStringFromProperties(java.lang.String ,
	 * java.lang.String)
	 */
	public String getStringFromProperties(String propertieID, String resourcePath) throws ServiceException {
		final Properties prop = new Properties();
		String properties = null;
		final InputStream in = StringUtility.class.getResourceAsStream(resourcePath);
		if (in == null) {

			final String message = getTranslatedMessage("");

			throw new ServiceException(message);
		}
		try {
			prop.load(in);
			properties = prop.getProperty(propertieID);

		} catch (final IOException ex) {
			throw new ServiceException(ex);
		} finally {
			tryToCloseTheStream(in);
		}
		return properties;

	}

	private static void tryToCloseTheStream(final InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (final IOException e) {
		}
	}

	/**
	 * This method set the String to upper case or lower case.
	 * 
	 * @param str
	 * @param type
	 * @return the variable "str" set according to the type (upper case or lower
	 *         case).
	 */
	public String setStringCase(String str, String type) throws ServiceException {

		if (str != null && type != null) {
			if (type.equalsIgnoreCase("UPPER")) {
				return str.toUpperCase();
			} else if (type.equalsIgnoreCase("LOWER")) {
				return str.toLowerCase();
			} else {
				return str;
			}
		}

		return null;
	}

	public Integer getLength(String str) throws ServiceException {
		return str == null ? 0 : str.length();
	}

}// end Class
