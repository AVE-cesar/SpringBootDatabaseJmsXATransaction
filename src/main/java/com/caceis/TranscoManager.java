package com.caceis;

public class TranscoManager {

	public static String transco(String objet_type, String transco_in, String transco_out, String event_code) {
		if ("Code_Evenement".equals(objet_type) && "4SF_Event".equals(transco_in) && "Coaty_Titre".equals(transco_out)) {		
			if ("CRS".equals(event_code)) {
				return "TRS";
			}
			if ("CRU".equals(event_code)) {
				return "TRU";
			}
			if ("CS".equals(event_code)) {
				return "TS";
			}
			if ("CU".equals(event_code)) {
				return "TU";
			}
		}	
		return null;
	}

}
