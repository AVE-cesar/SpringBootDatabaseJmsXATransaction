package com.caceis;

import static java.util.Objects.isNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.ForSightCREtoCOATY;

public class FieldMapping {
	
	// Recuperation de notre logger.
	private final static Logger logger = LoggerFactory.getLogger(FieldMapping.class);
		
	public static String calculateFeeRate(String rate) {
		return NumberUtility.returnPositiveValueStringFormat(rate);
	}
	
	public static String calculateCounterpart1CashAcc(String cpt1_Cash_Account, String own_bank_account, String dealing_capacity) {
		String result="";
		
		int len=0;
		int len1=0;
		if (cpt1_Cash_Account != null) {
			len = cpt1_Cash_Account.length();
		}
		if (own_bank_account != null) {
			len1 = own_bank_account.length();
		}
		
		if(len < 21 && "PR".equals(dealing_capacity)) {
		    result = cpt1_Cash_Account;
		}

		if( len1< 21 && "AG".equals(dealing_capacity)) {
			result = own_bank_account; 
		}
		return result;
	}
	
	public static String calculateCounterpart2CashAcct(String client_bank_account, String Send_instruction_flag) {
		String result="";
		int len = 0;
		if (client_bank_account != null) {
			len= client_bank_account.length();
		}

		if(len < 21 && "N".equals(Send_instruction_flag)) {
			result = client_bank_account;
		}
		return result;

	}
	
	public static String calculateInterestRate(String cash_rate, boolean strictMode) throws DataInvalidException {
		String value= null;
		
		if (strictMode && "V".equalsIgnoreCase(cash_rate)) {
			throw new DataInvalidException();
		}
		
		value = NumberUtility.returnPositiveValueStringFormat(cash_rate);
		return value;
	}
	
	public static String calculateCounterpart3CashAcct(String cpt3_cash_account, String cpt1_cash_account_2, String dealing_capacity) {
		String result="";
		int len = 0;
		int len1 = 0;
		
		if (cpt3_cash_account != null) {
			len = cpt3_cash_account.length();
		}
		
		if (cpt1_cash_account_2 != null) {
			len1 = cpt1_cash_account_2.length();
		}

		if( "PR".equals(dealing_capacity) && len < 21 ) {
			return cpt3_cash_account;
		}
		
		if( "AG".equals(dealing_capacity) && len1<21 ) {
			return cpt1_cash_account_2;
		}
		return result;
	}
	
	public static String calculateCounterpart3(String cpt3_ref) {
		int len = 0;
		if (cpt3_ref != null) {
			len = cpt3_ref.length();
		}

		if(len < 11 ) {
			return cpt3_ref;
		}
		return "";
	}
	
	public static String calculateSecPriceDec(String price) {
		if (isNull(price)) { 
			return "0";
		}
		else {
			float price_fl = NumberUtility.convertToFloat(price);
			if (price_fl > 999999) {
				return "6";
			}
			else {
				return "9";
			}
		}
	}
	
	public static String calculateSecIntPriceDec(String initial_price) {
		if (isNull(initial_price)) {
			return "0";
		}
		else {
			float initial_price_fl = NumberUtility.convertToFloat(initial_price);
			if (initial_price_fl > 999999) {
				return "6"; 
			}
			else {
				return "9";
			}
		}
	}
	
	public static String calculateIntSpread(String spread) {
		return NumberUtility.returnPositiveValueStringFormat(spread);
	}
	
	public static String calculateSecCustody(String event_code, String origin_ind, String Collateral_type, String client_type, String stock_medium) {
		boolean client_type_cheked = false;
		if ("FDM".equalsIgnoreCase(client_type) || "CUS".equalsIgnoreCase(client_type) || "BNK".equalsIgnoreCase(client_type)) {
			client_type_cheked = true;
		}
		
		if ("C".equals(origin_ind)) {
			if ("AIL".equalsIgnoreCase(event_code) || 
				"AIC".equalsIgnoreCase(event_code) ||
				"TRD".equalsIgnoreCase(event_code) ||
				("CRS".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && client_type_cheked) ||
				("CS".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && client_type_cheked) ||
				("CU".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && client_type_cheked) ||
				("CRU".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && client_type_cheked) ||
				
				("CRS".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && !"COLL_ALLOC".equalsIgnoreCase(stock_medium)) ||
				("CS".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && !"COLL_ALLOC".equalsIgnoreCase(stock_medium)) ||
				("CU".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && !"COLL_ALLOC".equalsIgnoreCase(stock_medium)) ||
				("CRU".equalsIgnoreCase(event_code) && "STOCK".equalsIgnoreCase(Collateral_type) && !"COLL_ALLOC".equalsIgnoreCase(stock_medium))) {
				return "I";
			}
		}
		else {
			if ("RV".equalsIgnoreCase(event_code) || 
				"RUV".equalsIgnoreCase(event_code) ||
				"RS".equalsIgnoreCase(event_code) ||
				"RUS".equalsIgnoreCase(event_code) ||
				"LV".equalsIgnoreCase(event_code) ||
				"LUV".equalsIgnoreCase(event_code) ||
				"LSS".equalsIgnoreCase(event_code) ||
				"LUS".equalsIgnoreCase(event_code) ||
				(("CS".equalsIgnoreCase(event_code) || "CU".equalsIgnoreCase(event_code) || "CRS".equalsIgnoreCase(event_code) || "CRU".equalsIgnoreCase(event_code)) && "STOCK".equalsIgnoreCase(Collateral_type) && !"COLL_ALLOC".equalsIgnoreCase(stock_medium) && "O".equalsIgnoreCase(origin_ind))
				){
				return "I";
			}
		}
		return "O";
		
	}
	
	public static String calculateFmtFields(String fmtField, String dealCapacity) {
		int len = 0;
		String infoChamp = null;
		if ("AG".equalsIgnoreCase(dealCapacity) && fmtField!= null) { 
			infoChamp = fmtField;
			len = infoChamp.length();
		}
		if (infoChamp != null && len>10) {
			return extractSimple(infoChamp,0,10);
		}
		if (infoChamp != null && len<=10) {
			return StringUtility.formatString(infoChamp, "10", " ", "RIGHT", "RIGHT");
		}
		if (infoChamp == null) {
			return "          ";
		}
		return null;
	}
	
	private static String extractSimple(String value, int i, int j) {
		return value.substring(i, j);
	}
	
	public static String calculateLSecShtDes(String secShortIsinCode) {
		int len = 0;
		if (secShortIsinCode != null) {
			len = secShortIsinCode.length();
		}
		if (secShortIsinCode != null && len>18 ) {
			return extractSimple(secShortIsinCode,0,18);
		}
		if (secShortIsinCode == null) {
			return "                  ";
		}
		if (secShortIsinCode != null && len<=18 ) {
			return StringUtility.formatString(secShortIsinCode , "18", "  ", "RIGHT", "RIGHT");
		}
		return null;
	}
	public static String caclulateRptLabCashRef(String labCashRef) {
		int len = 0;
		if (labCashRef != null) {
			len = labCashRef.length();
		}
		if (labCashRef != null && len>65 ) {
			return extractSimple(labCashRef,0,65);
		}
		if (labCashRef != null && len<=65) {
			return StringUtility.formatString(labCashRef, "65", " ", "RIGHT", "RIGHT");
		}
		if (labCashRef == null) {
			return "                                                                 ";
		}
		return null;
	}
}
