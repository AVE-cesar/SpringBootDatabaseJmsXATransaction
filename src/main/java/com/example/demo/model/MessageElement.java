package com.example.demo.model;

import com.caceis.StringUtility;

public class MessageElement {
	
	private static int i = 0;
	
	private static final char SPACE_CAR = ' ';
	
	public String C_TRANSIT_SECD_BRH="";
	public String C_TRANSIT_SECD="";
	public String C_TRANSIT_SECD_CUR="";
	public String I_RETRO_FLAG="";
	public String C_TRANSIT_SECD_FMT="";
	public String C_CPTY1_CSHACC_FMT="";
	public String C_CPTY2_ACCTYP_FMT="";
	public String C_CPTY2_SECACC_EFF="";
	public String C_CPTY4_CL_CODE="";
	public String C_CPTY1_CUSTCOD="";
	public String C_CPTY2_CUSTCOD="";
	public String L_CPTY3_NAME="";
	public String L_CPTY4_NAME="";
	public String C_CPTY2_XREF_3="";
	public String C_CPTY3_XREF_BIC="";
	public String C_NETT_TRADE_TYPE="";
	public String C_PAYMT_KEY="";
	public String C_RESERVE_65="";
	public String C_SEC_RESTRIC_ACC="";
	public String A_CC_ACCR_FEE_TOT_AMT="";
	public String C_CC_ACCR_FEE_TOT_AMTCUR="";
	public String N_CC_ACCR_FEE_TOT_AMTDEC="";
	public String C_CC_ACCR_FEE_TOT_AMT_WAY="";
	public String A_CC_ACCRINT_TOT_AMT="";
	public String C_CC_ACCRINT_TOT_AMTCUR="";
	public String N_CC_ACCRINT_TOT_AMTDEC="";
	public String C_CC_ACCRINT_TOT_AMT_WAY="";
	public String A_PAYCASH_AMT="";
	public String C_PAYCASH_CUR="";
	public String N_PAYCASH_DEC="";
	public String C_PAYCASH_AMT_WAY="";
	public String RPT_REF_CASH_CLI="";
	public String RPT_REF_CASH_BK="";
	public String RPT_LAB_SEC_REF1="";
	public String RPT_SEC_REC_REF1="";
	public String A_LN_MTM_AMT="";
	public String C_LN_MTM_AMT_CUR="";
	public String N_LN_MTM_AMT_DEC="";
	public String C_LN_MTM_AMT_WAY="";
	public String A_LN_FEEBILL_AMT="";
	public String A_CC_ACCR_AMT="";
	public String C_CC_ACCR_AMT_CUR="";
	public String N_CC_ACCR_AMT_DEC="";
	public String D_TRN_DTE="";
	public String C_CC_ACCR_AMT_WAY="";
	public String RPT_REF_CASH_REF1="";
	public String D_MAT_DTE="";
	public String Filler="";
	public String C_LN_MTM_COV_TOT_AMT_CUR="";
	public String N_LN_MTM_COV_TOT_AMT_DEC="";
	public String C_LN_MTM_COV_TOT_AMT_WAY="";
	public String A_LN_FEE_RET_AMT="";
	public String C_LN_FEE_RET_AMTCUR="";
	public String N_LN_FEE_RET_AMTDEC="";
	public String C_LN_FEE_RET_AMTWAY="";
	public String A_CC_CASH_AMT="";
	public String C_CC_CASH_AMT_CUR="";
	public String N_CC_CASH_AMT_DEC="";
	public String C_CC_CASH_AMT_WAY="";
	public String A_CC_INTBILL_AMT="";
	public String C_CC_INTBILL_AMTCUR="";
	public String N_CC_INTBILL_DEC="";
	public String C_CC_INTBILL_AMTWAY="";
	public String A_CC_SEC_AMT="";
	public String C_CC_SEC_CUR="";
	public String N_CC_SEC_DEC="";
	public String C_CC_SEC_AMT_WAY="";
	public String L_CPTY2_TYP_4SF="";
	public String C_CPTY2_SECACCT="";
	public String C_CPTY2_REF_CODE="";
	public String A_LN_MTM_COV_CS_AMT="";
	public String C_LN_MTM_COV_CS_AMT_CUR="";
	public String N_LN_MTM_COV_CS_AMT_DEC="";
	public String C_LN_MTM_COV_CS_AMT_WAY="";
	public String C_LN_PCT_COV_CC="";
	public String C_LN_PCT_COV_CC_DEC="";
	public String C_SEC_PRICE_CUR="";
	public String C_LN_PCT_CS_COV="";
	public String C_LN_PCT_COV_CS_DEC="";
	public String A_LN_MTM_COV_CC_AMT="";
	public String C_LN_MTM_COV_CC_AMT_CUR="";
	public String N_LN_MTM_COV_CC_AMT_DEC="";
	public String C_LN_MTM_COV_CC_AMT_WAY="";
	public String C_LN_PCT_COV_TOT="";
	public String C_LN_PCT_COV_TOT_DEC="";
	public String C_LN_FEEBILL_AMTWAY="";
	public String C_SEC_CUR="";
	public String Q_SEC_QTY_DEC="";
	public String C_SEC_INT_PRICE="";
	public String C_SEC_INTPRICE_DEC="";
	public String C_SEC_PRICE="";
	public String C_SEC_PRICE_DEC="";
	public String T_SEC_PRICE_TYP="";
	public String C_SEC_RATE_DIVID="";
	public String C_SEC_RATEDIVID_DEC="";
	public String C_SEC2_ISIN="";
	public String L_SEC2_SHT_DES="";
	public String C_SEC2_BOND_IND="";
	public String C_SEC2_CUR="";
	public String Q_SEC2_QTY="";
	public String Q_SEC2_QTY_DEC="";
	public String C_CPTY3_CASHIBAN="";
	public String C_INT_SPREAD="";
	public String C_INT_SPREAD_DEC="";
	public String C_SEC_MARGIN="";
	public String C_SEC_MARGIN_DEC="";
	public String T_SEC_HRT="";
	public String T_SEC_HRT_DEC="";
	public String C_CORP_ACT_TYP="";
	public String C_CORP_DESCR="";
	public String L_SEC_SHT_DES="";
	public String C_SEC_BOND_IND="";
	public String C_CPTY1_CASHACCT_CUR="";
	public String C_CPTY1_REF_CODE="";
	public String C_CPTY2_BRH="";
	public String C_CPTY2="";
	public String C_CPTY2_REF2="";
	public String C_CPTY2_CASHACCT="";
	public String C_CPTY2_CASHACCT_CUR="";
	public String L_CPTY3_TYP_4S="";
	public String C_CPTY3_SECACCT="";
	public String C_CPTY3_CASHACCT="";
	public String C_CPTY3_CASHACCT_CUR="";
	public String C_CPTY3_REF_CODE="";
	public String C_SEC_ISIN="";
	public String Q_SEC_QTY="";
	public String C_SEC_CUSTODY="";
	public String C_SEC_MODE_ACC="";
	public String C_SEC_NOSTRO_ID="";
	public String C_SEC_NOSTRO_ACC="";
	public String C_CPTY1_CASHIBAN="";
	public String C_CPTY2_CASHIBAN="";
	public String A_LN_ACCRINT_AMT="";
	public String C_LN_ACCRINT_AMTCUR="";
	public String C_INT_INDEX_CODE="";
	public String N_LN_ACCRINT_AMTDEC="";
	public String C_LN_ACCRINT_AMT_WAY="";
	public String C_LN_FEEBILL_AMTCUR="";
	public String N_LN_FEEBILL_AMTDEC="";
	public String A_LN_DIV_AMT="";
	public String C_CPTY_BRH3="";
	public String C_CPTY3="";
	public String C_CPTY3_REF2="";
	public String C_LOAN_TPE="";
	public String C_POOL_TPE="";
	public String C_PRD_TYPE="";
	public String C_DEAL_CAP="";
	public String C_STATUS="";
	public String C_FEE_RATE="";
	public String C_FEE_RATE_DEC="";
	public String C_FEE_RATE_TYP="";
	public String C_INT_RATE_DEC="";
	public String C_INT_RATE_TYP="";
	public String C_LN_DIV_AMTCUR="";
	public String N_LN_DIV_AMTDEC="";
	public String C_LN_DIV_AMTWAY="";
	public String A_LN_ADJ_AMT="";
	public String C_LN_ADJ_AMTCUR="";
	public String C_LN_ADJ_AMTWAY="";
	public String N_LN_ADJ_AMTDEC="";
	public String C_CPTY1_CASHACCT="";
	public String A_LN_MTM_COV_TOT_AMT="";
	public String RPT_LAB_CASH_REF1="";
	public String RPT_LAB_CASH_REF2="";
	public String RPT_LAB_CASH_REF3="";
	public String RPT_LAB_CASH_REF4="";
	public String RPT_LAB_CASH_REF5="";
	public String D_ACC_BUS_DTE="";
	public String D_TRD_DTE="";
	public String D_THR_SET_DTE="";
	public String C_INT_RATE="";
	public String D_VAL="";
	public String A_LN_MTM_PDG_AMT="";
	public String C_LN_MTM_PDG_AMT_CUR="";
	public String N_LN_MTM_PDG_AMT_DEC="";
	public String C_LN_MTM_PDG_AMT_WAY="";
	public String M_LNPDGX_AMT="";
	public String C_LNPDGX_CUR="";
	public String Q_LNPDGX_DEC="";
	public String C_LNPDGX_WAY="";
	public String M_LNCOVCSX_AMT="";
	public String C_LNCOVCSX_CUR="";
	public String Q_LNCOVCSX_DEC="";
	public String C_LNCOVCSX_WAY="";
	public String C_CCACCX_CUR="";
	public String Q_CCACCX_DEC="";
	public String C_CCACCX_WAY="";
	public String M_CCACCX_AMT="";
	public String M_LNACCXINT_AMT="";
	public String C_LNACCXINT_CUR="";
	public String M_ADD1_AMT="";
	public String C_ADD1_CUR="";
	public String Q_ADD1_DEC="";
	public String C_ADD1_WAY="";
	public String M_ADD2_AMT="";
	public String C_ADD2_CUR="";
	public String Q_ADD2_DEC="";
	public String Q_LNACCXINT_DEC="";
	public String C_LN_ACCXINT_WAY="";
	public String C_ADD2_WAY="";
	public String C_POOL_ID="";
	public String C_STMT_ID="";
	public String C_STTL_ID="";
	public String C_NETT_REF_ID="";
	public String C_EXT_EVENT_ID="";
	public String C_ACTIVITY_ID="";
	public String C_OPE_VLDY_USER="";
	public String C_OPE_CRE_USER="";
	public String C_OPE_ORIG_FLG="";
	public String C_LOAN_ID="";
	public String C_EXT_EVENT_ID_ORG="";
	public String C_PAYCASH_NET="";
	public String C_COL_MVM_ID="";
	public String C_NON_CSH_COL_ID="";
	public String C_CSH_COL_ID="";
	public String C_MDM_LOAN_ID="";
	public String C_MESS_ID="";
	public String I_REVERSAL_FLAG="";
	public String I_CANCEL_FLAG ="";
	public String C_GLB_BUS_REEF="";
	public String C_PFT="";
	public String C_EVT_TYP="";
	public String C_DIRECTION="";
	public String C_PAYCASH_DVP="";
	public String C_PAYCASH_POF="";
	public String C_MIRRCASH_IND="";
	public String C_CPTY1_SECACCT="";
	public String L_CPTY1_TYP_4SF="";
	public String C_CPTY1_REF2="";
	public String C_CPTY1="";
	public String C_CPTY1_BRH="";

	// FIXME faire cette méthode avec tous les champs vers MQ
	// méthode auto-générée à partir du fichier Excel de CID (onglet FORMAT OUT)
	public String toMQString(boolean debugMode) {
		String mq = StringUtility.padRight(C_GLB_BUS_REEF,16,SPACE_CAR)
		 + addDebugInfo(debugMode) + StringUtility.padRight(I_CANCEL_FLAG,1,SPACE_CAR)
		 + addDebugInfo(debugMode) + StringUtility.padRight(I_REVERSAL_FLAG,1,SPACE_CAR)
		 + addDebugInfo(debugMode) + StringUtility.padRight(C_MESS_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) + StringUtility.padRight(C_MDM_LOAN_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) + StringUtility.padRight(C_LOAN_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CSH_COL_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_NON_CSH_COL_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_COL_MVM_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_POOL_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_STMT_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_STTL_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_NETT_REF_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_EXT_EVENT_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_EXT_EVENT_ID_ORG,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_ACTIVITY_ID,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_OPE_CRE_USER,6,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_OPE_VLDY_USER,6,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_OPE_ORIG_FLG,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_EVT_TYP,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PFT,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_DIRECTION,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYCASH_POF,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_MIRRCASH_IND,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYCASH_DVP,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYCASH_NET,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_BRH,5,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_REF2,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_CPTY1_TYP_4SF,7,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_SECACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_CASHACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_CASHIBAN,34,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_CASHACCT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_REF_CODE,4,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_BRH,5,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_REF2,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_CPTY2_TYP_4SF,7,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_SECACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_CASHACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_CASHIBAN,34,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_CASHACCT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_REF_CODE,4,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY_BRH3,5,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_REF2,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_CPTY3_TYP_4S,7,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_SECACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_CASHACCT,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_CASHIBAN,34,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_CASHACCT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_REF_CODE,4,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LOAN_TPE,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_POOL_TPE,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PRD_TYPE,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_DEAL_CAP,2,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_STATUS,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_FEE_RATE,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_FEE_RATE_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_FEE_RATE_TYP,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_RATE,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_RATE_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_RATE_TYP,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_INDEX_CODE,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_SPREAD,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_INT_SPREAD_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_MARGIN,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_MARGIN_DEC,1,SPACE_CAR)
		 // 70
		 + addDebugInfo(debugMode) +  StringUtility.padRight(T_SEC_HRT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(T_SEC_HRT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CORP_ACT_TYP,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CORP_DESCR,40,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_ISIN,12,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_SEC_SHT_DES,18,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_BOND_IND,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_SEC_QTY,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_SEC_QTY_DEC,1,SPACE_CAR)
		 // 80
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_INT_PRICE,15,SPACE_CAR)
		 // 81
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_INTPRICE_DEC,1,SPACE_CAR)
		 // 82
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_PRICE,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_PRICE_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_PRICE_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(T_SEC_PRICE_TYP,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_RATE_DIVID,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_RATEDIVID_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC2_ISIN,12,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_SEC2_SHT_DES,18,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC2_BOND_IND,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC2_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_SEC2_QTY,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_SEC2_QTY_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_VAL,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_ACC_BUS_DTE,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_TRD_DTE,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_THR_SET_DTE,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_MAT_DTE,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(D_TRN_DTE,8,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_MTM_PDG_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_PDG_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_MTM_PDG_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_PDG_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_MTM_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_MTM_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_MTM_COV_CS_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_CS_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_MTM_COV_CS_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_CS_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_CS_COV,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_COV_CS_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_MTM_COV_CC_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_CC_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_MTM_COV_CC_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_CC_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_COV_CC,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_COV_CC_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_MTM_COV_TOT_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_TOT_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_MTM_COV_TOT_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_MTM_COV_TOT_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_COV_TOT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_PCT_COV_TOT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_ACCRINT_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_ACCRINT_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_ACCRINT_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_ACCRINT_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_FEEBILL_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_FEEBILL_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_FEEBILL_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_FEEBILL_AMTWAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_FEE_RET_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_FEE_RET_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_FEE_RET_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_FEE_RET_AMTWAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_DIV_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_DIV_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_DIV_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_DIV_AMTWAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_LN_ADJ_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_ADJ_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_LN_ADJ_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_ADJ_AMTWAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_CASH_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_CASH_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_CASH_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_CASH_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_INTBILL_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_INTBILL_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_INTBILL_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_INTBILL_AMTWAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_SEC_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_SEC_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_SEC_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_SEC_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_ACCR_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCR_AMT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_ACCR_AMT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCR_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_ACCR_FEE_TOT_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCR_FEE_TOT_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_ACCR_FEE_TOT_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCR_FEE_TOT_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_CC_ACCRINT_TOT_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCRINT_TOT_AMTCUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_CC_ACCRINT_TOT_AMTDEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CC_ACCRINT_TOT_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(A_PAYCASH_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYCASH_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(N_PAYCASH_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYCASH_AMT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_LNPDGX_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LNPDGX_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_LNPDGX_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LNPDGX_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_LNCOVCSX_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LNCOVCSX_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_LNCOVCSX_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LNCOVCSX_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_CCACCX_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CCACCX_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_CCACCX_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CCACCX_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_LNACCXINT_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LNACCXINT_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_LNACCXINT_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_LN_ACCXINT_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_ADD1_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_ADD1_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_ADD1_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_ADD1_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(M_ADD2_AMT,15,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_ADD2_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Q_ADD2_DEC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_ADD2_WAY,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_REF_CASH_CLI,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_REF_CASH_BK,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_REF_CASH_REF1,16,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_CASH_REF1,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_CASH_REF2,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_CASH_REF3,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_CASH_REF4,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_CASH_REF5,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_CUSTODY,4,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_MODE_ACC,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_NOSTRO_ID,4,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_NOSTRO_ACC,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_SEC_RESTRIC_ACC,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_SEC_REC_REF1,30,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(RPT_LAB_SEC_REF1,30,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_TRANSIT_SECD_BRH,5,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_TRANSIT_SECD,35,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_TRANSIT_SECD_CUR,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_TRANSIT_SECD_FMT,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_CSHACC_FMT,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_ACCTYP_FMT,10,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_SECACC_EFF,35,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY4_CL_CODE,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY1_CUSTCOD,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_CUSTCOD,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_CPTY3_NAME,35,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(L_CPTY4_NAME,35,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY2_XREF_3,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_CPTY3_XREF_BIC,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_NETT_TRADE_TYPE,3,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_PAYMT_KEY,20,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(I_RETRO_FLAG,1,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(C_RESERVE_65,65,SPACE_CAR)
		 + addDebugInfo(debugMode) +  StringUtility.padRight(Filler,220,SPACE_CAR);
		
		i=0;
		return mq;
	}

	private String addDebugInfo(boolean debugMode) {
		if (debugMode) {
		return "_"+(i++)+"_";
		} else {
			return "";
		}
	}

}

