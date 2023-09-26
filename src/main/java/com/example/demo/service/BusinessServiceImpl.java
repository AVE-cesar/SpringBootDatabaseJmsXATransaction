package com.example.demo.service;

import static java.util.Objects.isNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.caceis.DataInvalidException;
import com.caceis.DateUtility;
import com.caceis.FieldMapping;
import com.caceis.StringUtility;
import com.caceis.TranscoManager;
import com.example.demo.StringUtils;
import com.example.demo.dao.FourSightCREToCoatyDAO;
import com.example.demo.model.Record;
import com.ibm.mq.jms.MQQueue;
import com.example.demo.model.MessageElement;

@Service
public class BusinessServiceImpl implements BusinessService {

	private final static Logger logger = LoggerFactory.getLogger(BusinessServiceImpl.class);

	@Value("${queuemanager.outputQueueName}")
	public String queueManagerOutputQueueName;
	
	@Value("${queuemanager.activateCOA}")
	public boolean queueManagerActivateCOA;
	
	@Value("${queuemanager.activateCOD}")
	public boolean queueManagerActivateCOD;
	
	@Value("${queuemanager.replyToQueueName}")
	public String queueManagerReplyToQueueName;
	
	@Value("${batchmode.size}")
	public long batchModeSize;
	
	@Autowired
	FourSightCREToCoatyDAO fourSightCREToCoatyDAO;

	//@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	ConfigurableApplicationContext applicationContext;

	@Autowired
	MQAsyncSender mqAsyncSender;
	
	// FIXME à sortir
	public static String COMMA_DELIMITER = "\\|";
	
	// formattage de date
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 

	@Override
	@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	public void doLogicJMS(String... args) throws ServiceException {
		
		Date now = new Date(System.currentTimeMillis());
		
		// FIXME passer par un autowired et plus pas l'application context
		Object obj = applicationContext.getBean("queueTemplate");
		
		if (jmsTemplate == null) jmsTemplate = (JmsTemplate)obj;
		if (jmsTemplate != null) {
			long t1 = System.currentTimeMillis();
			int nb= 10;
			for (int i = 1; i <= nb; i++) {
				logger.info("sending MQ message only JMS {}/{}",i,nb);
				jmsTemplate.convertAndSend(queueManagerOutputQueueName, "logic JMS:"+now.toGMTString() + "    " + i);
			}
			long t2 = System.currentTimeMillis();
			logger.info("total time pour {} JMS messages: en min {} en s {}",  nb, (t2-t1)/1000/60, (t2-t1)/1000);
		}
		
		// on simule une Exception pour tester le rollback complet (JDBC et JMS)
		//throw new ServiceException("marche arrière totale");
	}

	@Override
	//@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	public void doParseCsvFile(String... args) throws ServiceException {
		String inputFile = args[0];
		String maxLines = args[1];
		boolean strictMode = Boolean.parseBoolean(args[3]);
		String modeMQorFile = args[2];
		
		long t1 = System.currentTimeMillis();
		logger.info("START du process t1: {}", SDF.format(new Date(t1)));
		try {
			parseFile(inputFile, Integer.parseInt(maxLines), strictMode, modeMQorFile);
		} catch (NumberFormatException e) {
			logger.error("", e);
			throw new ServiceException(e);
		} catch (FileNotFoundException e) {
			logger.error("", e);
			throw new ServiceException(e);
		} catch (IOException e) {
			logger.error("", e);
			throw new ServiceException(e);
		} catch (DataInvalidException e) {
			logger.error("", e);
			throw new ServiceException(e);
		}
		long t2 = System.currentTimeMillis();
		logger.info("Parsing du fichier en input t2: {}", SDF.format(new Date(t2)));
		long t3 = System.currentTimeMillis();
		logger.info("MQ sending t3: " + SDF.format(new Date(t3)));
		
		logger.info("total time en min: {} et en sec {}", (t3-t1)/1000/60, (t3-t1)/1000);
	}

	private void parseFile(String filename, int nbLines, boolean strictMode, String modeMQorFile) throws FileNotFoundException, IOException, DataInvalidException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			long count = 0;
			
			List<Record> recordsForNextBatch = new ArrayList();
			
			while ((line = br.readLine()) != null) {
				
				// on bypasse le header
				if (count == 0) {
					logger.info("on écarte le HEADER: {}", line);
					count++;
					continue;
				}
				
				String[] values = line.split(COMMA_DELIMITER, -1);
								
				String[] SanitizedValues = new String[values.length];
				
				int i = 0;
				for(String value:values) {
		            String sanitizedValue = StringUtils.replaceBlankByNull(value);
		            SanitizedValues[i] = sanitizedValue;
		            i++;
		        }
				
				Record record = doMapping1And2(count, Arrays.asList(SanitizedValues), strictMode, modeMQorFile, count, filename, line);
				
				if (record != null) {
					recordsForNextBatch.add(record);
				}
				
				if (recordsForNextBatch.size() >= batchModeSize){
					logger.info("On traite un premier lot de taille: {}", recordsForNextBatch.size());
					
					// on sauvegarde par lots + envoi JMS
					List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
					insertRowIntoDatabase (recordsForNextBatchCOPY);
					
					// on vide le stock pour le prochain lot
					recordsForNextBatch.clear();
				} 
				
				count++;
				if (nbLines !=-1 && count > nbLines) {
					break;
				}
			} // fin du while sur les lignes du fichier
			
			// il peut rester des lignes à traiter, un lot incomplet, on doit le traiter
			if (!recordsForNextBatch.isEmpty()) {
				logger.info("On traite le dernier lot incomplet de taille: {} < taille des lots: {}", recordsForNextBatch.size(), batchModeSize);
				
				// on sauvegarde par lots + envoi JMS
				List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
				insertRowIntoDatabase (recordsForNextBatchCOPY);
			}
		} 
	}

	private Record doMapping1And2(long lineNumber, List<String> oneRecord, boolean strictMode, String modeMQorFile, long count,
			String filename, String line) throws DataInvalidException {
			
			// on s'occupe d'une ligne
			MessageElement messageElement = new MessageElement();
			
			logger.info("On traite la ligne: {}", (lineNumber+1));
			logger.debug("contenu de la ligne: {}", oneRecord);
				
			boolean sendMQ = "MQ".equalsIgnoreCase(modeMQorFile) ? true: false;
			Record record = doMapping1And2ForALine(oneRecord, lineNumber, sendMQ, messageElement, strictMode, count, filename, line); 
			
//			lineNumber++;
			logger.debug("on vient de finir la ligne: {}", (lineNumber+1));
			lineNumber++;
			
			return record;
	}

	private Record doMapping1And2ForALine(List<String> oneRecord, long lineNumber, boolean sendMQ,
			MessageElement messageElement, boolean strictMode, long count, String filename, String line) throws DataInvalidException {
		
		int i = 0;
		String Accounting_date = oneRecord.get(i++);
		String activity_id = oneRecord.get(i++);
		String actual_stock_date = oneRecord.get(i++);
		String agreed_sett_date = oneRecord.get(i++);
		String agreed_stock_date = oneRecord.get(i++);
		String Bulk_id = oneRecord.get(i++);
		String ca_description = oneRecord.get(i++);
		String cancel_flag = oneRecord.get(i++);
		String cash_collateral_id = oneRecord.get(i++);
		String cash_medium = oneRecord.get(i++);
		String cash_mirror_ind = oneRecord.get(i++);
		String cash_payment_id = oneRecord.get(i++);
		String cash_rate = oneRecord.get(i++);
		String cash_rate_type = oneRecord.get(i++);
		String Ccy_day_number = oneRecord.get(i++);
		String client_bank_account = oneRecord.get(i++);
		String client_bank_code = oneRecord.get(i++);
		String client_code = oneRecord.get(i++);
		String client_depot_acoount = oneRecord.get(i++);
		String client_depot_code = oneRecord.get(i++);
		String client_type = oneRecord.get(i++);
		String close_date = oneRecord.get(i++);
		String clt_bk_account_cur = oneRecord.get(i++);
		String collateral_movement_id = oneRecord.get(i++);
		String Collateral_type = oneRecord.get(i++);
		String corporate_action_type = oneRecord.get(i++);
		String cpt1_branch_code = oneRecord.get(i++);
		String cpt1_Cash_Account = oneRecord.get(i++);
		String cpt1_cash_cur = oneRecord.get(i++);
		String cpt1_REF = oneRecord.get(i++);
		String cpt1_ref2 = oneRecord.get(i++);
		String cpt1_Sec_Account = oneRecord.get(i++);
		String cpt1_type = oneRecord.get(i++);
		String cpt2_branch_code = oneRecord.get(i++);
		String cpt2_ref1 = oneRecord.get(i++);
		String cpt2_ref2 = oneRecord.get(i++);
		String cpt3_branch_code = oneRecord.get(i++);
		String cpt3_cash_account = oneRecord.get(i++);
		String cpt3_ref = oneRecord.get(i++);
		String cpt3_ref1 = oneRecord.get(i++);
		String cpt3_ref2 = oneRecord.get(i++);
		String cpt3_sec_account = oneRecord.get(i++);
		String cpt3_type = oneRecord.get(i++);
		String dealing_capacity = oneRecord.get(i++);
		String dec_instrument_ccy = oneRecord.get(i++);
		String dec_loan_curreny = oneRecord.get(i++);
		String dec_sett_ccy = oneRecord.get(i++);
		String direction = oneRecord.get(i++);
		String div_direction = oneRecord.get(i++);
		String dividend_rate = oneRecord.get(i++);
		String dividend_value = oneRecord.get(i++);
		String event_code = oneRecord.get(i++);
		String event_id = oneRecord.get(i++);
		String ext_event_id = oneRecord.get(i++);
		String external_Pay_ind = oneRecord.get(i++);
		String Fee_rate_type = oneRecord.get(i++);
		String haircut = oneRecord.get(i++);
		String initial_price = oneRecord.get(i++);
		String input_date = oneRecord.get(i++);
		String instrument_currency_code = oneRecord.get(i++);
		String internal_account = oneRecord.get(i++);
		String internal_narrative = oneRecord.get(i++);
		String isin_code = oneRecord.get(i++);
		String ISIN_name = oneRecord.get(i++);
		String loan_currency_code = oneRecord.get(i++);
		String loan_id = oneRecord.get(i++);
		String loan_type = oneRecord.get(i++);
		String main_sec_type = oneRecord.get(i++);
		String margin = oneRecord.get(i++);
		String market_code = oneRecord.get(i++);
		String market_index_code = oneRecord.get(i++);
		String Maturity_date = oneRecord.get(i++);
		String mnt_cash_coll_per = oneRecord.get(i++);
		String mnt_cash_collateral = oneRecord.get(i++);
		String mnt_collateral_direction = oneRecord.get(i++);
		String mnt_gross = oneRecord.get(i++);
		String mnt_loan_direction = oneRecord.get(i++);
		String mnt_pending_value = oneRecord.get(i++);
		String mnt_loan_pend_direction = oneRecord.get(i++);
		String mnt_stock_coll_per = oneRecord.get(i++);
		String mnt_settled_value = oneRecord.get(i++);
		String mnt_stock_collateral = oneRecord.get(i++);
		String mnt_tot_coll_per = oneRecord.get(i++);
		String mnt_value = oneRecord.get(i++);
		String narrative = oneRecord.get(i++);
		String narrative_confirm = oneRecord.get(i++);
		String net_ref = oneRecord.get(i++);
		String netting_ind = oneRecord.get(i++);
		String netting_narrative = oneRecord.get(i++);
		String non_cash_collateral_id = oneRecord.get(i++);
		String origin_ind = oneRecord.get(i++);
		String own_bank_account = oneRecord.get(i++);
		String own_bank_code = oneRecord.get(i++);
		String own_depot_account = oneRecord.get(i++);
		String own_depot_code = oneRecord.get(i++);
		String payment_type = oneRecord.get(i++);
		String pending_quantity = oneRecord.get(i++);
		String pnl_direction = oneRecord.get(i++);
		String pool_id = oneRecord.get(i++);
		String pool_type = oneRecord.get(i++);
		String price = oneRecord.get(i++);
		String price_cotation_type = oneRecord.get(i++);
		String price_type = oneRecord.get(i++);
		String product_type = oneRecord.get(i++);
		String profit_centre_mnemonic = oneRecord.get(i++);
		String Qty_unit_nominal = oneRecord.get(i++);
		String quantity = oneRecord.get(i++);
		String rate = oneRecord.get(i++);
		String rate_type = oneRecord.get(i++);
		String ref_company_code = oneRecord.get(i++);
		String ref_origin = oneRecord.get(i++);
		String reversal_flag = oneRecord.get(i++);
		String security_type = oneRecord.get(i++);
		String sett_currency_code = oneRecord.get(i++);
		String settlement_direction = oneRecord.get(i++);
		String settlement_id = oneRecord.get(i++);
		String spread = oneRecord.get(i++);
		String statement_id = oneRecord.get(i++);
		String statement_payment_id = oneRecord.get(i++);
		String status = oneRecord.get(i++);
		String stock_medium = oneRecord.get(i++);
		String total_ai = oneRecord.get(i++);
		String trade_Acc_Ref = oneRecord.get(i++);
		String trade_date = oneRecord.get(i++);
		String transaction_id = oneRecord.get(i++);
		String underling_sec_type = oneRecord.get(i++);
		String underlying_currency = oneRecord.get(i++);
		String underlying_Isin = oneRecord.get(i++);
		String underlying_name = oneRecord.get(i++);
		String underlying_quantity = oneRecord.get(i++);
		String unpaid_ai = oneRecord.get(i++);
		String user_code = oneRecord.get(i++);
		String user_validation = oneRecord.get(i++);
		String value_date = oneRecord.get(i++);
		String ex_date = oneRecord.get(i++);
		String client_bank_type = oneRecord.get(i++);
		String company_bank_type = oneRecord.get(i++);
		String contractual_ca = oneRecord.get(i++);
		String link_loan = oneRecord.get(i++);
		String package_id = oneRecord.get(i++);
		String Event_timestamp = oneRecord.get(i++);
		String closed_date = oneRecord.get(i++);
		String Send_instruction_flag = oneRecord.get(i++);
		String Callable = oneRecord.get(i++);
		String Evergreen = oneRecord.get(i++);
		String Equilend = oneRecord.get(i++);
		String Broker_rating_name_1 = oneRecord.get(i++);
		String Broker_rating_term_1 = oneRecord.get(i++);
		String Broker_rating_band_1 = oneRecord.get(i++);
		String Collateral_Mechanism = oneRecord.get(i++);
		String All_In_Price = oneRecord.get(i++);
		String Hot_Stock = oneRecord.get(i++);
		String Hurdle_rate = oneRecord.get(i++);
		String Instrument_trading_comment = oneRecord.get(i++);
		String Instrument_rating_name_1 = oneRecord.get(i++);
		String Instrument_rating_Term_1 = oneRecord.get(i++);
		String Instrument_rating_band_1 = oneRecord.get(i++);
		String Market_cap = oneRecord.get(i++);
		String Average_Trade_Volume = oneRecord.get(i++);
		String Sector = oneRecord.get(i++);
		String cpt2_ref3 = oneRecord.get(i++);
		String cpt2_ref4 = oneRecord.get(i++);
		String cpt2_ref5 = oneRecord.get(i++);
		String MIFID_Brokers = oneRecord.get(i++);
		String MIFID_Rates = oneRecord.get(i++);
		String MIFID_Comment = oneRecord.get(i++);
		String Is_ca_outcome = oneRecord.get(i++);
		String Underlying_loan_id = oneRecord.get(i++);
		String Underlying_link_loan = oneRecord.get(i++);
		String External_ca_id = oneRecord.get(i++);
		String Ca_option_code = oneRecord.get(i++);
		String Is_underlying_right = oneRecord.get(i++);
		String Underlying_right_tradable = oneRecord.get(i++);
		String cpt1_cash_account_2 = oneRecord.get(i++);
		String cpt1_branch_code_2 = oneRecord.get(i++);
		String cpt1_cash_cur2 = oneRecord.get(i++);
		String cpt1_bank_type_2 = oneRecord.get(i++);
		String Retrocession_flag = oneRecord.get(i++);
		String Cpt3_name = oneRecord.get(i++);
		String Cpt4_ref = oneRecord.get(i++);
		String Cpt4_name = oneRecord.get(i++);
		String Netting_trade_type = oneRecord.get(i++);
		String Cpt2_sec_account = oneRecord.get(i++);
		String Payment_key = oneRecord.get(i++);
		String Broker_PSET = oneRecord.get(i++);
		String Cpt1_custcod = oneRecord.get(i++);
		String Cpt2_custcod = oneRecord.get(i++);
		String Loan_trade_date = oneRecord.get(i++);
		String Loan_value_date = oneRecord.get(i++);
		String Initial_quantity = oneRecord.get(i++);
		String Initial_gross_price = oneRecord.get(i++);
		String Collateral_schedule = oneRecord.get(i++);
		String Input_user = oneRecord.get(i++);
		String CPT3_WGS_REF = oneRecord.get(i++);
		String COUNTRY_OF_MARKET = oneRecord.get(i++);
		String minimum_fee_amount = oneRecord.get(i++);
		String underlying_unit_quotation = oneRecord.get(i++);
		String ca_option_type = oneRecord.get(i++);
		String triparty_flag = oneRecord.get(i++);
		String triparty_agent = oneRecord.get(i++);
		String triparty_bic = oneRecord.get(i++);
		String escrow_account = oneRecord.get(i++);
		String cs_code = oneRecord.get(i++);
		String cs_label = oneRecord.get(i++);
		String pool_quantity = oneRecord.get(i++);
		String Cpy_sec_acct_coll_glob = oneRecord.get(i++);
		String Cpy_sec_acct_coll_entt = oneRecord.get(i++);
		String own_depot_nostro_code = oneRecord.get(i++);
		String client_depot_nostro_code = oneRecord.get(i++);
		String own_depot_ldd_code = oneRecord.get(i++);
		String client_depot_ldd_code = oneRecord.get(i++);
		String cpy_sec_acct_com = oneRecord.get(i++);
		String cpy_bic_ref = oneRecord.get(i++);
		String sec_pos_service_ind = oneRecord.get(i++);
		String sec_col_service_ind = oneRecord.get(i++);
		String fnd_adm_service_ind = oneRecord.get(i++);
		String mid_off_service_ind = oneRecord.get(i++);
		String mof_pos_service_ind = oneRecord.get(i++);
		String csh_act_service_ind = oneRecord.get(i++);
		String cpt2_entity_code = oneRecord.get(i++);
		String cpt2_coll_custcod = oneRecord.get(i++);
		String x_entity_nccollat_alloc = oneRecord.get(i++);
		String cpt2_trad_custcod = oneRecord.get(i++);
		String cpt2_client_xref_swift = oneRecord.get(i++);
		String trade_over_dividend = oneRecord.get(i++);
		// les champs suivants ne sont pas dans le fichier en input
		// le header et les lignes n'ont pas la même taille
		/*
		String trade_type = oneRecord.get(i++);
		String xref_equ = oneRecord.get(i++);
		String seme_client = oneRecord.get(i++);
		String bic_emetteur = oneRecord.get(i++);
		String mico = oneRecord.get(i++);
		String MARGIN_TYPE = oneRecord.get(i++);
		String TRANSFER_MODE = oneRecord.get(i++);
		String SAFEKEEPER_CODE = oneRecord.get(i++);
		String SAFEKEEPER_ROLE = oneRecord.get(i++);
		String REUSED = oneRecord.get(i++);
		String LEI_CPT = oneRecord.get(i++);
		String LEI_FND = oneRecord.get(i++);
		String ARG_ID = oneRecord.get(i++);
		String GIVER_TAKER = oneRecord.get(i++);*/
		
		// FIXME filtrage des lignes inutiles
		if ("AIL".equalsIgnoreCase(event_code) || "AIC".equalsIgnoreCase(event_code)) {
			if ("O".equalsIgnoreCase(origin_ind)) {
				logger.debug("on PREND la ligne: {}, {}", event_code, origin_ind);
			} else {
				logger.debug("on ne prend pas la ligne: {}, {}",event_code, origin_ind);
				return null;
			}
		} else {
			logger.debug("on ne prend pas la ligne: {}, {}",event_code, origin_ind);
			return null;
		}
		
		//--------------------------------------------------------------------
		// mapping 1
		
		String counterpart3CashAcctCur = "AG".equalsIgnoreCase(dealing_capacity) ? cpt1_cash_cur2:"";
		String counterpart3Ref2 = CPT3_WGS_REF;
		String counterpart2AccTypeFMT = client_bank_type;
		String counterpart3RefBIC = cpt3_ref2;
		String transitSecondFMT = "AG".equalsIgnoreCase(dealing_capacity) ? cpt1_bank_type_2:"";
		String counterpart1CashAccFMT = "AG".equalsIgnoreCase(dealing_capacity) ? company_bank_type:"";
		String transitSecond = "AG".equalsIgnoreCase(dealing_capacity) ? cpt1_cash_account_2:"";
		String transitSecondBRH = "AG".equalsIgnoreCase(dealing_capacity) ? cpt1_branch_code_2:"";
		String transitSecondCur = "AG".equalsIgnoreCase(dealing_capacity) ? cpt1_cash_cur2:"";
		String counterpart2Ref3 = cpt2_ref3;
		String counterpart2CustCode = Cpt2_custcod;
		String counterpart2SecAccEff = Cpt2_sec_account;
		String counterpart3Name = Cpt3_name;
		String counterpart4ClCode = ("AIL".equalsIgnoreCase(event_code) || "SAS".equalsIgnoreCase(event_code) || "SAU".equalsIgnoreCase(event_code) || "SLS".equalsIgnoreCase(event_code) || "SLU".equalsIgnoreCase(event_code)) ? Cpt4_ref : "";
		String counterpart4Name = Cpt4_name;
		String couterpart1CustCode = Cpt1_custcod;
		String nettingTradeType = Netting_trade_type;
		String paymentKey = Payment_key;
		String retrocessionFlag = Retrocession_flag;
		String cancelFlag = "Y".equalsIgnoreCase(cancel_flag) ? "true":"false";
		String secCustody = FieldMapping.calculateSecCustody(event_code, origin_ind, Collateral_type, client_type, stock_medium);
		String mdmLoanId = loan_id;
		String labCashRef4 = internal_narrative;
		String settlementDate = agreed_sett_date;
		String counterpart2CashAcctCurr = clt_bk_account_cur;
		String payCashWay = direction;
		String payCashDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String loanFeeRetroAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String loanDividendAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String loanAdjAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String loanAccrualIntAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String feesBillingAmtDecimal = dec_sett_ccy == null?"0":dec_sett_ccy;
		String collatTotalAccAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String collatAccrualAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String collatAccIntTotalAmtDecimal = dec_sett_ccy;
		String cocaIntBillingAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String cocaAmtDecimal = dec_sett_ccy==null?"0":dec_sett_ccy;
		String loanDividendAmtWay = div_direction;
		String loanDividendAmt = dividend_value==null?"0":dividend_value;
		String extEventId = ext_event_id;
		String eventType = "SBS".equalsIgnoreCase(event_code) ? "SAS" :("SBU".equalsIgnoreCase(event_code) ? "SAU" :(event_code!=null? ((("CS".equalsIgnoreCase(event_code)|"CRS".equalsIgnoreCase(event_code)|"CU".equalsIgnoreCase(event_code)|"CRU".equalsIgnoreCase(event_code)) && ("STOCK".equalsIgnoreCase(Collateral_type) && "COLL_ALLOC".equalsIgnoreCase(stock_medium)))? TranscoManager.transco("Code_Evenement","4SF_Event","Coaty_Titre", event_code):event_code):" "));
		String transactionDate = input_date;
		String secPriceCur = loan_currency_code==null?instrument_currency_code:loan_currency_code;
		String secHaircut = haircut==null?"0":haircut;
		String secBondInd = main_sec_type;
		String payCashPOF = ("Y".equalsIgnoreCase(Send_instruction_flag)?"true":"false");
		String mtmLoanPdgAmtCur = instrument_currency_code;
		String mtmLoanCovTotAmtCur = instrument_currency_code;
		String mtmLoanCovCSAmtWay = mnt_loan_direction;
		String mtmLoanCovCSAmtDecimal = dec_instrument_ccy==null?"0":dec_instrument_ccy;
		String mtmLoanCovCSAmtCur = instrument_currency_code;
		String mtmLoanCovCCAmtWay = mnt_loan_direction;
		String mtmLoanCovCCAmtCur = instrument_currency_code;
		String mtmLoanCovCCAmt = mnt_cash_collateral==null?"0":mnt_cash_collateral;
		String mtmLoanAmtWay = mnt_loan_direction;
		String mtmLoanAmtCur = instrument_currency_code;
		String maturityDate = ("CDS".equalsIgnoreCase(event_code)||"CDU".equalsIgnoreCase(event_code)||"CBS".equalsIgnoreCase(event_code)||"CBU".equalsIgnoreCase(event_code))?ex_date:Maturity_date;
		String loanType = loan_type;
		String loanPctCCCov = mnt_cash_coll_per==null?"0":mnt_cash_coll_per;
		String labCashRef3 = internal_narrative;
		String secIsinCode = isin_code;
		String coseAmtWay = mnt_collateral_direction;
		String coseAmtCurr = instrument_currency_code;
		String cocaIntBillingAmtWay = ("SCS".equalsIgnoreCase(event_code) || "SCU".equalsIgnoreCase(event_code))? settlement_direction : mnt_collateral_direction;
		String cocaAmtWay = mnt_collateral_direction;
		String mtmLoanPdgAmtWay = mnt_loan_pend_direction;
		String mtmLoanPdgAmt = mnt_pending_value==null?"0":mnt_pending_value;
		String mtmLoanCovTotAmtWay = mnt_loan_direction;
		String mtmLoanAmnt = mnt_settled_value==null?"0":mnt_settled_value;
		String loanPctCSCov = mnt_stock_coll_per==null?"0":mnt_stock_coll_per;
		String payCashAmt = mnt_value==null?"0":mnt_value;
		String mtmLoanCovTotAmt = "0";
		String mtmLoanCovCSAmount = mnt_stock_collateral==null?"0":mnt_stock_collateral;
		String loanPctCovTot = mnt_tot_coll_per==null?"0":mnt_tot_coll_per;
		String loanFeeRetroAmt = mnt_value==null?"0":mnt_value;
		String loanAdjAmt = mnt_value==null?"0":mnt_value;
		String labCashRef2 = narrative;
		String feesBillingAmt = mnt_value==null?"0":mnt_value;
		String coseAmt = mnt_value==null?"0":mnt_value;
		String cocaIntBillingAmt = mnt_value==null?"0":mnt_value;
		String cocaAmt = mnt_value==null?"0":mnt_value;
		String poolType = pool_type;
		String poolId = pool_id;
		String payCashNet = "Y".equalsIgnoreCase(netting_ind)?"true":"false";
		String payCashDVP = payment_type!=null && payment_type.equalsIgnoreCase("DVP")?"true":"false";
		String nonCashCollId = non_cash_collateral_id;
		String netRefId = net_ref;
		String loanFeeRetroAmtWay = pnl_direction;
		String loanAdjAmtWay = pnl_direction;
		String loanAccrualInterestAmtWay = pnl_direction;
		String labCashRef1 = netting_narrative;
		String feesBillingAmtWay = pnl_direction;
		String collatTotalAccAmtWay = pnl_direction;
		String collatAccrualIntTotalAmtWay = pnl_direction;
		String collatAccrualAmtWay = pnl_direction;
		String secPrice = price==null?"0":price;
		String profitCenter = profit_centre_mnemonic;
		String feeRate = FieldMapping.calculateFeeRate(rate);
		String counterpart2RefCode = ref_company_code;
		String counterpart1RefCode = ref_company_code;
		String tradeDate = trade_date;
		String sec2Quantity = underlying_quantity==null?"0":underlying_quantity;
		String sec2ShortIsinCode = underlying_name;
		String sec2IsinCode = underlying_Isin;
		String sec2Cur = underlying_currency;
		String sec2BondInd = underling_sec_type;
		String loanStatus = status;
		String collatTotalAccAmt = total_ai==null?"0":total_ai;
		String collatAccIntTotalAmt = total_ai==null?"0":total_ai;
		String valueDate = value_date;
		String opeValidatorUser = user_validation;
		String opeCreatorUser = user_code;
		String loanAccrualInterestAmt = unpaid_ai==null?"0":unpaid_ai;
		String collatAccrualAmt = unpaid_ai==null?"0":unpaid_ai;
		String feeRateDec = rate!=null?"8":"0";
		String secQuantityDecimal = quantity==null?"0":"2";
		String sec2QuantityDec = underlying_quantity==null?"0":"2";
		String mtmLoanAmtDecimal = dec_instrument_ccy==null?"0":dec_instrument_ccy;
		String mtmLoanPdgAmtDec = dec_instrument_ccy==null?"0":dec_instrument_ccy;
		String secShortIsinCode = ISIN_name;
		String sttlId = settlement_id;
		String stmtId = statement_id;
		String reversalFlag = "Y".equalsIgnoreCase(reversal_flag) ? "true":"false";
		String payCashCurr = sett_currency_code;
		String loanFeeRetroAmtCurr = sett_currency_code;
		String loanDividendAmtCurr = sett_currency_code;
		String loanAdjAmtCurr = sett_currency_code;
		String loanAccrualIntAmtCurr = sett_currency_code;
		String feesBillingAmtCurr = sett_currency_code;
		String extEventIdOrg = ref_origin;
		String collatcashAmtCurr = sett_currency_code;
		String collatTotalAccAmtCurr = sett_currency_code;
		String collatAccrualAmtCurr = sett_currency_code;
		String collatAccIntTotalAmtCurr = sett_currency_code;
		String cocaIntBillingAmtCurr = sett_currency_code;
		String loanPctCSCovDecimal = mnt_cash_coll_per==null?"0":"8";
		String loanPctCCCovDecimal = mnt_cash_coll_per==null?"0":"8";
		String loanPctCovTotDec = mnt_tot_coll_per==null?"0":"8";
		String activityId = activity_id;
		String secQuantity = quantity==null?"0":quantity;
		String secMaginDec = margin==null?"0":"8";
		String counterpart1CashAcc = FieldMapping.calculateCounterpart1CashAcc(cpt1_cash_account_2, own_bank_account, dealing_capacity);
		String opeOriginalFlag = origin_ind;
		String corporateDescr = ca_description;
		String counterpart2Type = client_type;
		String counterpart2SecAcct = ("CBLP".equalsIgnoreCase(ref_company_code) || "AMIA".equalsIgnoreCase(ref_company_code))? client_code:Cpt2_sec_account;
		String counterpart2CashAcct = FieldMapping.calculateCounterpart2CashAcct(client_bank_account, Send_instruction_flag);
		String collMvtId = collateral_movement_id;
		String counterpart1Type = cpt1_type;
		String counterpart1Ref2 = cpt1_ref2;
		String counterpart1CashAccCur = cpt1_cash_cur;
		String mirrCashInd = cash_mirror_ind;
		String cashCollId = cash_collateral_id;
		String accountingDate = Accounting_date;
		String interestRateType = cash_rate_type;
		String interestRate = FieldMapping.calculateInterestRate(cash_rate, strictMode);
		String counterpart1Branch = cpt1_branch_code;
		String counterpart1 = cpt1_REF;
		String corporateActionType = corporate_action_type;
		String counterpart3SecAcct = ("STOCK".equalsIgnoreCase(Collateral_type) && !"Y".equalsIgnoreCase(triparty_flag))? Cpy_sec_acct_coll_entt:cpt3_sec_account;
		String counterpart3CashAcct = FieldMapping.calculateCounterpart3CashAcct(cpt3_cash_account, cpt1_cash_account_2, dealing_capacity);
		String counterpart3Branch = "PR".equalsIgnoreCase(dealing_capacity) ? cpt3_branch_code: cpt1_branch_code_2;
		String counterpart3 = FieldMapping.calculateCounterpart3(cpt3_ref2);
		String counterpart2Ref2 = cpt2_ref2;
		String counterpart2Branch = cpt2_branch_code;
		String counterpart2 = cpt2_ref1;
		String counterpart3Type = cpt3_type;
		String counterpart3RefCode = ref_company_code;
		String dealCapacity = dealing_capacity;
		String secHaircutDec = haircut==null?"0":"8";
		String mtmLoanCovTotAmtDec = dec_instrument_ccy==null?"0":dec_instrument_ccy;
		String mtmLoanCovCCAmtDecimal = dec_instrument_ccy== null?"0":dec_instrument_ccy;
		String interestRateDecimal = cash_rate==null?"0":"8";
		String coseAmtDecimal = dec_instrument_ccy==null?"0":dec_instrument_ccy;
		String secPriceDec = FieldMapping.calculateSecPriceDec(price);
		String cancelledReference = ref_origin;
		String businessReference = ext_event_id;
		String secCur = instrument_currency_code;
		String intSpreadDec = spread==null?"0":"8";
		String secRateDivDec = dividend_rate==null?"0":"8";
		String secIntPriceDec = FieldMapping.calculateSecIntPriceDec(initial_price);
		String productType = product_type;
		String globalBusinessRef = trade_Acc_Ref;
		String feeRateType = rate_type;
		String secRateDiv = dividend_rate==null?"0":dividend_rate;
		String secMagin = margin==null?"0":margin;
		String secIntPrice = initial_price==null?"0":initial_price;
		String loanId = (("CS".equalsIgnoreCase(event_code) || "CRS".equalsIgnoreCase(event_code) || "CU".equalsIgnoreCase(event_code) || "CRU".equalsIgnoreCase(event_code)) && "STOCK".equalsIgnoreCase(Collateral_type) )?"C"+collateral_movement_id : loan_id;
		String intSpread = FieldMapping.calculateIntSpread(spread);
		String wayCode = direction;
		String secPriceType = price_type;
		String intIndexCode = market_index_code;
		String counterpart1SecAcct = ("STOCK".equalsIgnoreCase(Collateral_type) && !"Y".equalsIgnoreCase(triparty_flag))?Cpy_sec_acct_coll_glob:cpt1_Sec_Account;
		String rptRefCashCli = csh_act_service_ind;
		String securityType = triparty_flag;
		String secNostroId = ("STOCK".equalsIgnoreCase(Collateral_type) && !"Y".equalsIgnoreCase(triparty_flag))?own_depot_ldd_code:"";
		String secNostroAcc = ("STOCK".equalsIgnoreCase(Collateral_type) &&  !"Y".equalsIgnoreCase(triparty_flag))?("-NON-DISPO-".equalsIgnoreCase(Broker_PSET) || "".equalsIgnoreCase(Broker_PSET) ? "":own_depot_nostro_code):client_depot_nostro_code;
		String labCashRef5 = internal_narrative;
		String add1AmtCur = event_code;
		String add1Amt = Broker_PSET;

		// FIXME filtrage COATY
		logger.trace("rptRefCashCli: {}", rptRefCashCli);
		logger.trace("opeOriginalFlag: {}", opeOriginalFlag);
		logger.trace("counterpart1Branch: {}", counterpart1Branch);
		logger.trace("counterpart2Branch: {}", counterpart2Branch);
		logger.trace("dealCapacity: {}", dealCapacity);
		logger.trace("eventType: {}", eventType);
		if ((rptRefCashCli == null || "Y".equalsIgnoreCase(rptRefCashCli)) && 
				(
		                (
		                ( "O".equalsIgnoreCase(opeOriginalFlag) && ("18129".equalsIgnoreCase(counterpart1Branch) || "27051".equalsIgnoreCase(counterpart1Branch) || "18451".equalsIgnoreCase(counterpart1Branch) 
		                		|| "53284".equalsIgnoreCase(counterpart1Branch) || "53283".equalsIgnoreCase(counterpart1Branch)) 
		                && "AG".equalsIgnoreCase(dealCapacity) ) || 
		                ("C".equalsIgnoreCase(opeOriginalFlag) && ("18129".equalsIgnoreCase(counterpart2Branch) || "27051".equalsIgnoreCase(counterpart2Branch) || "18451".equalsIgnoreCase(counterpart2Branch) 
		                		|| "53284".equalsIgnoreCase(counterpart2Branch) || "53283".equalsIgnoreCase(counterpart2Branch))
		                && "AG".equalsIgnoreCase(dealCapacity))
		                ) ||
		                ("O".equalsIgnoreCase(opeOriginalFlag) && "PR".equalsIgnoreCase(dealCapacity) && add1Amt == null ) | 
		                ("O".equalsIgnoreCase(opeOriginalFlag) && "PR".equalsIgnoreCase(dealCapacity)) || 
		                (
		                               ("C".equalsIgnoreCase(opeOriginalFlag) && ("18129".equalsIgnoreCase(counterpart2Branch) || "27051".equalsIgnoreCase(counterpart2Branch) || "18451".equalsIgnoreCase(counterpart2Branch) || "53284".equalsIgnoreCase(counterpart2Branch) 
		                            		   || "ET004".equalsIgnoreCase(counterpart2Branch) || "ET999".equalsIgnoreCase(counterpart2Branch) || "11894".equalsIgnoreCase(counterpart2Branch) || "53283".equalsIgnoreCase(counterpart2Branch) )
		                               )
		                && "PR".equalsIgnoreCase(dealCapacity)) || (("XS".equalsIgnoreCase(eventType)||"XRS".equalsIgnoreCase(eventType)) && "C".equalsIgnoreCase(opeOriginalFlag))
						)) {
			logger.trace("une bonne ligne pour COATY");
		} else {
			logger.trace("une MAUVAISE ligne pour COATY");
		}
		
		
		//--------------------------------------------------------------------
		// mapping 2
		messageElement.C_TRANSIT_SECD_BRH = "AG".equalsIgnoreCase(dealCapacity) && !isNull(transitSecondBRH)?transitSecondBRH :"     ";
		messageElement.C_TRANSIT_SECD	=	"AG".equalsIgnoreCase(dealCapacity) && !isNull(transitSecond )?transitSecond :"                                   ";
		messageElement.C_TRANSIT_SECD_CUR	=	"AG".equalsIgnoreCase(dealCapacity) && !isNull(transitSecondCur )?transitSecondCur :"   "	;
		messageElement.I_RETRO_FLAG	=	!isNull(retrocessionFlag)?retrocessionFlag:" "	;
		messageElement.C_TRANSIT_SECD_FMT = FieldMapping.calculateFmtFields(transitSecondFMT, dealCapacity);
		messageElement.C_CPTY1_CSHACC_FMT = FieldMapping.calculateFmtFields(counterpart1CashAccFMT, dealCapacity);
		messageElement.C_CPTY2_ACCTYP_FMT = FieldMapping.calculateFmtFields(counterpart2AccTypeFMT, dealCapacity);
		messageElement.C_CPTY2_SECACC_EFF	=	!isNull(counterpart2SecAccEff)?counterpart2SecAccEff:"                                   "	;
		messageElement.C_CPTY4_CL_CODE	=	!isNull(counterpart4ClCode)?counterpart4ClCode:"                    "	;
		messageElement.C_CPTY1_CUSTCOD	=	!isNull(couterpart1CustCode)?couterpart1CustCode:"                    "	;
		messageElement.C_CPTY2_CUSTCOD	=	!isNull(counterpart2CustCode)?counterpart2CustCode:"                    "	;
		messageElement.L_CPTY3_NAME	=	!isNull(counterpart3Name)?counterpart3Name :"                                   "	;
		messageElement.L_CPTY4_NAME	=	!isNull(counterpart4Name)?counterpart4Name:"                                   "	;
		messageElement.C_CPTY2_XREF_3	=	!isNull(counterpart2Ref3)?counterpart2Ref3:"                    "	;
		messageElement.C_CPTY3_XREF_BIC	=	!isNull(counterpart3RefBIC)?counterpart3RefBIC:"                    "	;
		messageElement.C_NETT_TRADE_TYPE	=	!isNull(nettingTradeType)?nettingTradeType:"   "	;
		messageElement.C_PAYMT_KEY	=	!isNull(paymentKey)?paymentKey:"                    "	;
		// FIXME compile pas à cause de reserve
		String reserve = null;
		messageElement.C_RESERVE_65	=	!isNull(reserve)?reserve:"								  								";
		// FIXME compile pas à cause de secRestricAcc
		String secRestricAcc = null;
		messageElement.C_SEC_RESTRIC_ACC	=	isNull(secRestricAcc)?"   ":secRestricAcc	;
		messageElement.A_CC_ACCR_FEE_TOT_AMT	=	StringUtility.formatDecimalNumber(collatTotalAccAmt, collatTotalAccAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_ACCR_FEE_TOT_AMTCUR	=	(!isNull(collatTotalAccAmtCurr)) ?collatTotalAccAmtCurr:"   "	;
		messageElement.N_CC_ACCR_FEE_TOT_AMTDEC	=	(!isNull(collatTotalAccAmtDecimal)) ?collatTotalAccAmtDecimal:"0"	;
		messageElement.C_CC_ACCR_FEE_TOT_AMT_WAY	=	!(isNull(collatTotalAccAmtWay)) ?collatTotalAccAmtWay:"C"	;
		messageElement.A_CC_ACCRINT_TOT_AMT	=	StringUtility.formatDecimalNumber(collatAccIntTotalAmt, collatAccIntTotalAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_ACCRINT_TOT_AMTCUR	=	!(isNull(collatAccIntTotalAmtCurr))?collatAccIntTotalAmtCurr:"   "	;
		messageElement.N_CC_ACCRINT_TOT_AMTDEC	=	!(isNull(collatAccIntTotalAmtDecimal)) ?collatAccIntTotalAmtDecimal:"0"	;
		messageElement.C_CC_ACCRINT_TOT_AMT_WAY	=	!(isNull(collatAccrualIntTotalAmtWay)) ?collatAccrualIntTotalAmtWay:" "	;
		messageElement.A_PAYCASH_AMT	=	(isNull(payCashCurr) | isNull(payCashWay) )?"000000000000000" :StringUtility.formatDecimalNumber(payCashAmt, payCashDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_PAYCASH_CUR	=	(!isNull(payCashCurr))?payCashCurr:"   "	;
		messageElement.N_PAYCASH_DEC	=	(!isNull(payCashDecimal))?payCashDecimal:"0"	;
		messageElement.C_PAYCASH_AMT_WAY	=	((!isNull(payCashWay)))?payCashWay:"C"	;
		messageElement.RPT_REF_CASH_CLI	=	isNull(rptRefCashCli)?"                ":rptRefCashCli	;
		// FIXME compile pas à cause de rptRefCashBk
		String rptRefCashBk = null;
		messageElement.RPT_REF_CASH_BK	=	isNull(rptRefCashBk)?"                ":rptRefCashBk	;
		// FIXME compile pas à cause de rptSecLabRef1
		String rptSecLabRef1 = null;
		messageElement.RPT_LAB_SEC_REF1	=	isNull(rptSecLabRef1) & isNull(counterpart1SecAcct) ?"                              ":counterpart1SecAcct	;
		// FIXME compile pas à cause de rptSecRecRef1
		String rptSecRecRef1 = null;
		messageElement.RPT_SEC_REC_REF1	=	isNull(rptSecRecRef1) & isNull(loanId) ?"                              ":loanId	;
		messageElement.A_LN_MTM_AMT	=	isNull(mtmLoanAmtCur) | isNull(mtmLoanAmtWay) ? "000000000000000" : StringUtility.formatDecimalNumber(mtmLoanAmnt, mtmLoanAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_MTM_AMT_CUR	=	(!isNull(mtmLoanAmtCur))? mtmLoanAmtCur:"   "	;
		messageElement.N_LN_MTM_AMT_DEC	=	(!isNull(mtmLoanAmtDecimal) )? mtmLoanAmtDecimal:"0"	;
		messageElement.C_LN_MTM_AMT_WAY	=	(!isNull(mtmLoanAmtWay) )? mtmLoanAmtWay:" "	;
		messageElement.A_LN_FEEBILL_AMT	=	StringUtility.formatDecimalNumber(feesBillingAmt, feesBillingAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.A_CC_ACCR_AMT	=	StringUtility.formatDecimalNumber(collatAccrualAmt, collatAccrualAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_ACCR_AMT_CUR	=	!(isNull(collatAccrualAmtCurr))? collatAccrualAmtCurr:"   "	;
		messageElement.N_CC_ACCR_AMT_DEC	=	!(isNull(collatAccrualAmtDecimal)) ?collatAccrualAmtDecimal:"0"	;
		messageElement.D_TRN_DTE	=	transactionDate!=null?(DateUtility.dateFormat(transactionDate, "yyyyMMdd")):"        "	;
		messageElement.C_CC_ACCR_AMT_WAY	=	!(isNull(collatAccrualAmtWay)) ?collatAccrualAmtWay:"C"	;
		// FIXME compile pas à cause de rptRefCashRef1
		String rptRefCashRef1 = null;
		messageElement.RPT_REF_CASH_REF1	=	isNull(rptRefCashRef1)?"                ":rptRefCashRef1	;
		messageElement.D_MAT_DTE	=	maturityDate!=null?(DateUtility.dateFormat(maturityDate, "yyyyMMdd")):"        "	;
		messageElement.Filler	=	"                                                                                                                                                                                                                            "	;
		messageElement.C_LN_MTM_COV_TOT_AMT_CUR	=	((!isNull(mtmLoanCovTotAmtCur)))?mtmLoanCovTotAmtCur:"   "	;
		messageElement.N_LN_MTM_COV_TOT_AMT_DEC	=	(!isNull(mtmLoanCovTotAmtDec))?mtmLoanCovTotAmtDec:"0"	;
		messageElement.C_LN_MTM_COV_TOT_AMT_WAY	=	(!isNull(mtmLoanCovTotAmtWay))?mtmLoanCovTotAmtWay:"C"	;
		messageElement.A_LN_FEE_RET_AMT	=	(isNull(loanFeeRetroAmtCurr) |  isNull(loanFeeRetroAmtWay)) ? "000000000000000" : StringUtility.formatDecimalNumber(loanFeeRetroAmt, loanFeeRetroAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_FEE_RET_AMTCUR	=	((!isNull(loanFeeRetroAmtCurr)))?loanFeeRetroAmtCurr:"   "	;
		messageElement.N_LN_FEE_RET_AMTDEC	=	((!isNull(loanFeeRetroAmtDecimal)))?loanFeeRetroAmtDecimal:"0"	;
		messageElement.C_LN_FEE_RET_AMTWAY	=	((!isNull(loanFeeRetroAmtWay)))?loanFeeRetroAmtWay:"C"	;
		messageElement.A_CC_CASH_AMT	=	(isNull(cocaAmtWay) | isNull(collatcashAmtCurr) )? "000000000000000" : StringUtility.formatDecimalNumber(cocaAmt, cocaAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_CASH_AMT_CUR	=	((!isNull(collatcashAmtCurr)))?collatcashAmtCurr:"   "	;
		messageElement.N_CC_CASH_AMT_DEC	=	(!isNull(cocaAmtDecimal)) ?cocaAmtDecimal:"0"	;
		messageElement.C_CC_CASH_AMT_WAY	=	(!isNull(cocaAmtWay))?cocaAmtWay:"C"	;
		messageElement.A_CC_INTBILL_AMT	=	(isNull(cocaIntBillingAmtCurr) | isNull(cocaIntBillingAmtWay) )?"000000000000000" : StringUtility.formatDecimalNumber(cocaIntBillingAmt, cocaIntBillingAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_INTBILL_AMTCUR	=	(!isNull(cocaIntBillingAmtCurr))?cocaIntBillingAmtCurr:"   "	;
		messageElement.N_CC_INTBILL_DEC	=	(!isNull(cocaIntBillingAmtDecimal)) ?cocaIntBillingAmtDecimal:"0"	;
		messageElement.C_CC_INTBILL_AMTWAY	=	(!isNull(cocaIntBillingAmtWay))?cocaIntBillingAmtWay:"C"	;
		messageElement.A_CC_SEC_AMT	=	(isNull(coseAmtWay) | isNull(coseAmtCurr) )?"000000000000000" : StringUtility.formatDecimalNumber(coseAmt, coseAmtDecimal , ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CC_SEC_CUR	=	((!isNull(coseAmtCurr))) ?coseAmtCurr:"   "	;
		messageElement.N_CC_SEC_DEC	=	((!isNull(coseAmtDecimal)) )?coseAmtDecimal:"0"	;
		messageElement.C_CC_SEC_AMT_WAY	=	((!isNull(coseAmtWay))) ?coseAmtWay:"C"	;
		messageElement.L_CPTY2_TYP_4SF	=	isNull(counterpart2Type)?"       ":counterpart2Type	;
		messageElement.C_CPTY2_SECACCT	=	isNull(counterpart2SecAcct)?"                    ":counterpart2SecAcct	;
		messageElement.C_CPTY2_REF_CODE	=	isNull(counterpart2RefCode)?"    ":counterpart2RefCode	;
		messageElement.A_LN_MTM_COV_CS_AMT	=	StringUtility.formatDecimalNumber(mtmLoanCovCSAmount, mtmLoanCovCSAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_MTM_COV_CS_AMT_CUR	=	(!isNull(mtmLoanCovCSAmtCur))?mtmLoanCovCSAmtCur:"   "	;
		messageElement.N_LN_MTM_COV_CS_AMT_DEC	=	(!isNull(mtmLoanCovCSAmtDecimal))?mtmLoanCovCSAmtDecimal:"0"	;
		messageElement.C_LN_MTM_COV_CS_AMT_WAY	=	(!isNull(mtmLoanCovCSAmtWay))?mtmLoanCovCSAmtWay:"C"	;
		messageElement.C_LN_PCT_COV_CC	=	StringUtility.formatDecimalNumber(loanPctCCCov, loanPctCCCovDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_PCT_COV_CC_DEC	=	(!isNull(loanPctCCCovDecimal))?loanPctCCCovDecimal:"0"	;
		messageElement.C_SEC_PRICE_CUR	=	(!isNull(secPriceCur) & (!isNull(secPrice)))?secPriceCur:"   "	;
		messageElement.C_LN_PCT_CS_COV	=	StringUtility.formatDecimalNumber(loanPctCSCov, loanPctCSCovDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_PCT_COV_CS_DEC	=	(!isNull(loanPctCSCovDecimal)) ?loanPctCSCovDecimal:"0"	;
		messageElement.A_LN_MTM_COV_CC_AMT	=	StringUtility.formatDecimalNumber(mtmLoanCovCCAmt, mtmLoanCovCCAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_MTM_COV_CC_AMT_CUR	=	(!isNull(mtmLoanCovCCAmtCur))?mtmLoanCovCCAmtCur:"   "	;
		messageElement.N_LN_MTM_COV_CC_AMT_DEC	=	(!isNull(mtmLoanCovCCAmtDecimal))?mtmLoanCovCCAmtDecimal:"0"	;
		messageElement.C_LN_MTM_COV_CC_AMT_WAY	=	(!isNull(mtmLoanCovCCAmtWay))?mtmLoanCovCCAmtWay:"C"	;
		messageElement.C_LN_PCT_COV_TOT	=	((isNull(loanPctCovTot) || "0".equalsIgnoreCase(loanPctCovTot)) || (isNull(loanId) & (!"TRD".equalsIgnoreCase(eventType)))) ? "000000000000000" : StringUtility.formatDecimalNumber(loanPctCovTot, loanPctCovTotDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_PCT_COV_TOT_DEC	=	(!isNull(loanPctCovTotDec))?loanPctCovTotDec:"0"	;
		messageElement.C_LN_FEEBILL_AMTWAY	=	(!isNull(feesBillingAmtWay))?feesBillingAmtWay:"C"	;
		messageElement.C_SEC_CUR	=	(!isNull(secCur))?secCur:"   "	;
		messageElement.Q_SEC_QTY_DEC	=	(!isNull(secQuantityDecimal))?secQuantityDecimal:"0"	;
		messageElement.C_SEC_INT_PRICE	=	StringUtility.formatDecimalNumber(secIntPrice, secIntPriceDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_SEC_INTPRICE_DEC	=	(!isNull(secIntPriceDec))?secIntPriceDec:"0"	;
		messageElement.C_SEC_PRICE	=	StringUtility.formatDecimalNumber(secPrice, secPriceDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_SEC_PRICE_DEC	=	(!isNull(secPriceDec) & (!isNull(secPrice)) )?secPriceDec:"0"	;
		messageElement.T_SEC_PRICE_TYP	=	(!isNull(secPriceType))?secPriceType:" "	;
		messageElement.C_SEC_RATE_DIVID	=	StringUtility.formatDecimalNumber(secRateDiv, secRateDivDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_SEC_RATEDIVID_DEC	=	(!isNull(secRateDivDec))?secRateDivDec:"0"	;
		messageElement.C_SEC2_ISIN	=	(!isNull(sec2IsinCode))?sec2IsinCode:"            "	;
		messageElement.L_SEC2_SHT_DES	=		FieldMapping.calculateLSecShtDes(sec2ShortIsinCode);
		messageElement.C_SEC2_BOND_IND	=	(!isNull(sec2BondInd))?sec2BondInd:" "	;
		messageElement.C_SEC2_CUR	=	(!isNull(sec2Cur)) ?sec2Cur:"   "	;
		messageElement.Q_SEC2_QTY	=	StringUtility.formatDecimalNumber(sec2Quantity, sec2QuantityDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.Q_SEC2_QTY_DEC	=	isNull(sec2QuantityDec)?"0":sec2QuantityDec	;
		messageElement.C_CPTY3_CASHIBAN	=	"                                  "	;
		messageElement.C_INT_SPREAD	=	StringUtility.formatDecimalNumber(intSpread, intSpreadDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_INT_SPREAD_DEC	=	isNull(intSpreadDec)?"0":intSpreadDec	;
		messageElement.C_SEC_MARGIN	=	StringUtility.formatDecimalNumber(secMagin, secMaginDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_SEC_MARGIN_DEC	=	isNull(secMaginDec)?"0":secMaginDec	;
		messageElement.T_SEC_HRT	=	StringUtility.formatDecimalNumber(secHaircut, secHaircutDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.T_SEC_HRT_DEC	=	isNull(secHaircutDec)?"0":secHaircutDec	;
		messageElement.C_CORP_ACT_TYP	=	isNull(corporateActionType)?"   ":corporateActionType	;
		messageElement.C_CORP_DESCR	=	(!isNull(corporateDescr))?corporateDescr:"                                        "	;
		messageElement.L_SEC_SHT_DES	=		FieldMapping.calculateLSecShtDes(secShortIsinCode);;
		messageElement.C_SEC_BOND_IND	=	(!isNull(secBondInd))?secBondInd:" "	;
		messageElement.C_CPTY1_CASHACCT_CUR	=	isNull(counterpart1CashAccCur)?"   ":counterpart1CashAccCur	;
		messageElement.C_CPTY1_REF_CODE	=	(!isNull(counterpart1RefCode)&& (!(isNull(nonCashCollId))&&(("CS".equalsIgnoreCase(eventType)||"OSC".equalsIgnoreCase(eventType))||("CRS".equalsIgnoreCase(eventType)||"ORS".equalsIgnoreCase(eventType))||("CU".equalsIgnoreCase(eventType)||"OUS".equalsIgnoreCase(eventType))||("CRU".equalsIgnoreCase(eventType)||"ORU".equalsIgnoreCase(eventType)))))?"    ":counterpart1RefCode	;
		messageElement.C_CPTY2_BRH	=	isNull(counterpart2Branch)?"     ":counterpart2Branch	;
		messageElement.C_CPTY2	=	isNull(counterpart2)?"          ":counterpart2	;
		messageElement.C_CPTY2_REF2	=	isNull(counterpart2Ref2)?"          ":counterpart2Ref2	;
		messageElement.C_CPTY2_CASHACCT	=	isNull(counterpart2CashAcct)?"                    ":counterpart2CashAcct	;
		messageElement.C_CPTY2_CASHACCT_CUR	=	(! isNull(counterpart2CashAcctCurr))?counterpart2CashAcctCurr:"   "	;
		messageElement.L_CPTY3_TYP_4S	=	isNull(counterpart3Type)?"       ":counterpart3Type	;
		messageElement.C_CPTY3_SECACCT	=	isNull(counterpart3SecAcct)?"                    ":counterpart3SecAcct	;
		messageElement.C_CPTY3_CASHACCT	=	isNull(counterpart3CashAcct)?"                    ":counterpart3CashAcct	;
		messageElement.C_CPTY3_CASHACCT_CUR	=	isNull(counterpart3CashAcctCur)?"   ":counterpart3CashAcctCur	;
		messageElement.C_CPTY3_REF_CODE	=	isNull(counterpart3RefCode)?"    ":counterpart3RefCode	;
		messageElement.C_SEC_ISIN	=	!isNull(secIsinCode)?secIsinCode:"            "	;
		messageElement.Q_SEC_QTY	=	StringUtility.formatDecimalNumber(secQuantity, secQuantityDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_SEC_CUSTODY	=	isNull(secCustody)?"    ":secCustody	;
		// FIXME compile pas à cause de secModeAcc
		String secModeAcc = null;
		messageElement.C_SEC_MODE_ACC	=	isNull(secModeAcc)?" ":secModeAcc	;
		messageElement.C_SEC_NOSTRO_ID	=	isNull(secNostroId)?"    ":secNostroId	;
		messageElement.C_SEC_NOSTRO_ACC	=	isNull(secNostroAcc)?"                    ":secNostroAcc	;
		messageElement.C_CPTY1_CASHIBAN	=	"                                  "	;
		messageElement.C_CPTY2_CASHIBAN	=	"                                  "	;
		messageElement.A_LN_ACCRINT_AMT	=	StringUtility.formatDecimalNumber(loanAccrualInterestAmt, loanAccrualIntAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_ACCRINT_AMTCUR	=	(!isNull(loanAccrualIntAmtCurr))?loanAccrualIntAmtCurr:"   "	;
		messageElement.C_INT_INDEX_CODE	=	isNull(intIndexCode)?"                    ":intIndexCode	;
		messageElement.N_LN_ACCRINT_AMTDEC	=	(!isNull(loanAccrualIntAmtDecimal))?loanAccrualIntAmtDecimal:"0"	;
		messageElement.C_LN_ACCRINT_AMT_WAY	=	( (!isNull(loanAccrualInterestAmtWay)))?loanAccrualInterestAmtWay:"C"	;
		messageElement.C_LN_FEEBILL_AMTCUR	=	(!isNull(feesBillingAmtCurr))?feesBillingAmtCurr:"   "	;
		messageElement.N_LN_FEEBILL_AMTDEC	=	(!isNull(feesBillingAmtDecimal))?feesBillingAmtDecimal:"0"	;
		messageElement.A_LN_DIV_AMT	=	StringUtility.formatDecimalNumber(loanDividendAmt, loanDividendAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_CPTY_BRH3	=	isNull(counterpart3Branch)?"     ":counterpart3Branch	;
		messageElement.C_CPTY3	=	isNull(counterpart3)?"          ":counterpart3	;
		messageElement.C_CPTY3_REF2	=	isNull(counterpart3Ref2)?"          ":counterpart3Ref2	;
		messageElement.C_LOAN_TPE	=	isNull(loanType)?" ":loanType	;
		messageElement.C_POOL_TPE	=	!isNull(poolType)?poolType:" "	;
		messageElement.C_PRD_TYPE	=	(isNull(productType))?" ":productType	;
		messageElement.C_DEAL_CAP	=	isNull(dealCapacity)?"  ":dealCapacity	;
		messageElement.C_STATUS	=	isNull(loanStatus)?" ":loanStatus	;
		messageElement.C_FEE_RATE	=	StringUtility.formatDecimalNumber(feeRate, feeRateDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_FEE_RATE_DEC	=	!isNull(feeRateDec)?feeRateDec:"0"	;
		messageElement.C_FEE_RATE_TYP	=	(!isNull(feeRateType))?feeRateType:" "	;
		messageElement.C_INT_RATE_DEC	=	!isNull(interestRateDecimal)?interestRateDecimal:"0"	;
		messageElement.C_INT_RATE_TYP	=	(!isNull(interestRateType))?interestRateType:" "	;
		messageElement.C_LN_DIV_AMTCUR	=	(!isNull(loanDividendAmtCurr))?loanDividendAmtCurr:"   "	;
		messageElement.N_LN_DIV_AMTDEC	=	((!isNull(loanDividendAmtDecimal)))?loanDividendAmtDecimal:"0"	;
		messageElement.C_LN_DIV_AMTWAY	=	(!isNull(loanDividendAmtWay))?loanDividendAmtWay:"C"	;
		messageElement.A_LN_ADJ_AMT	=	(isNull(loanAdjAmtCurr) || isNull(loanAdjAmtWay) ) ? "000000000000000": StringUtility.formatDecimalNumber(loanAdjAmt, loanAdjAmtDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_ADJ_AMTCUR	=	!isNull(loanAdjAmtCurr)?loanAdjAmtCurr:"   "	;
		messageElement.C_LN_ADJ_AMTWAY	=	(!isNull(loanAdjAmtWay)) ?loanAdjAmtWay:"C"	;
		messageElement.N_LN_ADJ_AMTDEC	=	!isNull(loanAdjAmtDecimal)?loanAdjAmtDecimal:"0"	;
		messageElement.C_CPTY1_CASHACCT	=	isNull(counterpart1CashAcc)?"                    ":counterpart1CashAcc	;
		messageElement.A_LN_MTM_COV_TOT_AMT	=	StringUtility.formatDecimalNumber(mtmLoanCovTotAmt, mtmLoanCovTotAmtDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.RPT_LAB_CASH_REF1	=	FieldMapping.caclulateRptLabCashRef(labCashRef1);
		messageElement.RPT_LAB_CASH_REF2	=	FieldMapping.caclulateRptLabCashRef(labCashRef2);
		messageElement.RPT_LAB_CASH_REF3	=	FieldMapping.caclulateRptLabCashRef(labCashRef3);
		messageElement.RPT_LAB_CASH_REF4	=	FieldMapping.caclulateRptLabCashRef(labCashRef4);
		messageElement.RPT_LAB_CASH_REF5	=	FieldMapping.caclulateRptLabCashRef(labCashRef5);
		messageElement.D_ACC_BUS_DTE	=	accountingDate!=null?(DateUtility.dateFormat(accountingDate, "yyyyMMdd")):"        "	;
		messageElement.D_TRD_DTE	=	tradeDate!=null?(DateUtility.dateFormat(tradeDate, "yyyyMMdd")):"        "	;
		messageElement.D_THR_SET_DTE	=	settlementDate!=null?(DateUtility.dateFormat(settlementDate, "yyyyMMdd")):"        "	;
		messageElement.C_INT_RATE	=	StringUtility.formatDecimalNumber(interestRate, interestRateDecimal, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.D_VAL	=	valueDate!=null?(DateUtility.dateFormat(valueDate, "yyyyMMdd")):"        "	;
		messageElement.A_LN_MTM_PDG_AMT	=	StringUtility.formatDecimalNumber(mtmLoanPdgAmt, mtmLoanPdgAmtDec, ".", "15", "0", "LEFT", "NONE")	;
		messageElement.C_LN_MTM_PDG_AMT_CUR	=	(!isNull(mtmLoanPdgAmtCur) )? mtmLoanPdgAmtCur:"   "	;
		messageElement.N_LN_MTM_PDG_AMT_DEC	=	(!isNull(mtmLoanPdgAmtDec)) ? mtmLoanPdgAmtDec:"0"	;
		messageElement.C_LN_MTM_PDG_AMT_WAY	=	(!isNull(mtmLoanPdgAmtWay) && (!isNull(loanId) &&("TRD".equalsIgnoreCase(eventType))) )? mtmLoanPdgAmtWay:" "	;
		messageElement.M_LNPDGX_AMT	=	"000000000000000"	;
		messageElement.C_LNPDGX_CUR	=	"   "	;
		messageElement.Q_LNPDGX_DEC	=	"0"	;
		messageElement.C_LNPDGX_WAY	=	" "	;
		messageElement.M_LNCOVCSX_AMT	=	"000000000000000"	;
		messageElement.C_LNCOVCSX_CUR	=	"   "	;
		messageElement.Q_LNCOVCSX_DEC	=	"0"	;
		messageElement.C_LNCOVCSX_WAY	=	" "	;
		messageElement.C_CCACCX_CUR	=	"   "	;
		messageElement.Q_CCACCX_DEC	=	"0"	;
		messageElement.C_CCACCX_WAY	=	" "	;
		messageElement.M_CCACCX_AMT	=	"000000000000000"	;
		messageElement.M_LNACCXINT_AMT	=	"000000000000000"	;
		messageElement.C_LNACCXINT_CUR	=	"   "	;
		messageElement.M_ADD1_AMT	=	"000000000000000"	;
		messageElement.C_ADD1_CUR	=	"   "	;
		messageElement.Q_ADD1_DEC	=	"0"	;
		messageElement.C_ADD1_WAY	=	" "	;
		messageElement.M_ADD2_AMT	=	"000000000000000"	;
		messageElement.C_ADD2_CUR	=	"   "	;
		messageElement.Q_ADD2_DEC	=	"0"	;
		messageElement.Q_LNACCXINT_DEC	=	"0"	;
		messageElement.C_LN_ACCXINT_WAY	=	" "	;
		messageElement.C_ADD2_WAY	=	" "	;
		messageElement.C_POOL_ID	=	!isNull(poolId)?poolId:"                "	;
		messageElement.C_STMT_ID	=	isNull(stmtId)?"                ":stmtId	;
		messageElement.C_STTL_ID	=	!isNull(sttlId)?sttlId:"                "	;
		messageElement.C_NETT_REF_ID	=	!isNull(netRefId)?netRefId:"                "	;
		messageElement.C_EXT_EVENT_ID	=	isNull(extEventId)?"                ":extEventId	;
		messageElement.C_ACTIVITY_ID	=	isNull(activityId)?"                ":activityId	;
		messageElement.C_OPE_VLDY_USER	=	isNull(opeValidatorUser)?"      ":opeValidatorUser	;
		messageElement.C_OPE_CRE_USER	=	isNull(opeCreatorUser)?"      ":opeCreatorUser	;
		messageElement.C_OPE_ORIG_FLG	=	isNull(opeOriginalFlag)?"                ":opeOriginalFlag	;
		messageElement.C_LOAN_ID	=	isNull(loanId)?"                ":loanId	;
		messageElement.C_EXT_EVENT_ID_ORG	=	isNull(extEventIdOrg)?"                ":extEventIdOrg	;
		messageElement.C_PAYCASH_NET	=	"false".equalsIgnoreCase(payCashNet)?"N":("true".equalsIgnoreCase(payCashNet)?"Y":"N")	;
		messageElement.C_COL_MVM_ID	=	isNull(collMvtId)?"                ":collMvtId	;
		messageElement.C_NON_CSH_COL_ID	=	isNull(nonCashCollId)?"                ":nonCashCollId	;
		messageElement.C_CSH_COL_ID	=	isNull(cashCollId)?"                ":cashCollId	;
		messageElement.C_MDM_LOAN_ID	=	isNull(mdmLoanId)?"                ":mdmLoanId	;
		// FIXME compile pas à cause de recordId
		String recordIdAsaString = "";
		messageElement.C_MESS_ID	=	StringUtility.formatString((recordIdAsaString+""),"16","0","LEFT","NONE")	;
		messageElement.I_REVERSAL_FLAG	=	"false".equalsIgnoreCase(reversalFlag)?"N":("true".equalsIgnoreCase(reversalFlag)?"Y":"N")	;
		messageElement.I_CANCEL_FLAG	=	"false".equalsIgnoreCase(cancelFlag)?"N":("true".equalsIgnoreCase(cancelFlag)?"Y":"N")	;
		messageElement.C_GLB_BUS_REEF	=	isNull(globalBusinessRef)?"                ":globalBusinessRef	;
		messageElement.C_PFT	=	isNull(profitCenter)?"   ":StringUtility.formatString(profitCenter, "3", "", "", "LEFT")	;
		messageElement.C_EVT_TYP	=	eventType!=null?eventType:"   "	;
		messageElement.C_DIRECTION	=	isNull(wayCode)?" ":wayCode	;
		messageElement.C_PAYCASH_DVP	=	isNull(payCashDVP)?"N":("true".equalsIgnoreCase(payCashDVP)?"Y":"N")	;
		messageElement.C_PAYCASH_POF	=	isNull(payCashPOF)?"N":("true".equalsIgnoreCase(payCashPOF)?"Y":"N")	;
		messageElement.C_MIRRCASH_IND	=	mirrCashInd==null?"N":mirrCashInd	;
		messageElement.C_CPTY1_SECACCT	=	isNull(counterpart1SecAcct)?"                    ":counterpart1SecAcct	;
		messageElement.L_CPTY1_TYP_4SF	=	isNull(counterpart1Type)?"       ":counterpart1Type	;
		messageElement.C_CPTY1_REF2	=	isNull(counterpart1Ref2)?"          ":counterpart1Ref2	;
		messageElement.C_CPTY1	=	isNull(counterpart1)?"          ":counterpart1	;
		messageElement.C_CPTY1_BRH	=	isNull(counterpart1Branch)?"     ":counterpart1Branch	;

		// on insert dans la db 
		//insertRowIntoDatabase(count, filename, line,  messageElement.toMQString(false));
		//insertRowIntoDatabaseAsync (count, filename, line,  messageElement.toMQString(false));
		
		// on stock les lignes dans une liste pour les jouer par lots et gagner du temps
		Record record = new Record();
		record.setFilename(filename);
		record.setLineNumber(count);
		record.setLine(line);
		record.setMqCre(messageElement.toMQString(false));
		
		return record;
	}

	@Transactional
	private void insertRowIntoDatabaseAsync(List<Record> records) {
		mqAsyncSender.doInsertRecords(records);
		//mqAsyncSender.doSend(records);	
	}
	
	@Transactional
	private void insertRowIntoDatabase(List<Record> records) {
		// on stocke par lots les records en DBs
		fourSightCREToCoatyDAO.createBatchRecord(records);
	}
	
	private void insertRowIntoDatabaseAsync(long count, String filename, String line, String mqString) {
		// appel Asynchrone
		mqAsyncSender.doSend(count, filename);
	}

	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	//@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	private void insertRowIntoDatabase(long count, String filename, String line, String mqString) {
		// TODO Auto-generated method stub
		Date now = new Date(System.currentTimeMillis());
		Record record = new Record(null, filename);
		fourSightCREToCoatyDAO.createRecord(record);
		
		// FIXME mettre ailleurs
		Object obj = applicationContext.getBean("queueTemplate");
		
		if (jmsTemplate == null) jmsTemplate = (JmsTemplate)obj;
		if (jmsTemplate != null) {
			logger.info("sending MQ message only JMS {} avec COA: {} et COD: {}",count,queueManagerActivateCOA, queueManagerActivateCOD);
			
			if (queueManagerActivateCOA || queueManagerActivateCOD) {
				// soit COA soit COD sont activés
				jmsTemplate.convertAndSend(queueManagerOutputQueueName, filename +": "+count, new MessagePostProcessor() {
					
					@Override
					public Message postProcessMessage(Message message) throws JMSException {

			            message.setJMSReplyTo((Destination) new MQQueue(queueManagerReplyToQueueName));
			            if (queueManagerActivateCOA) {
			            	// ou com.ibm.mq.MQC.MQRO_COA_WITH_FULL_DATA
			            	message.setIntProperty("JMS_IBM_Report_COA", com.ibm.mq.MQC.MQRO_COA_WITH_DATA );
			            }
			            if (queueManagerActivateCOD) {
			            	message.setIntProperty("JMS_IBM_Report_COD",com.ibm.mq.MQC.MQRO_COD_WITH_DATA );
			            }
						
			            return message;
			        }
			    });
			} else {
				// ni COA ni COD
				jmsTemplate.convertAndSend(queueManagerOutputQueueName, filename +": "+count);
			}
		}
	}
	
	public void doRetry(String... args) throws ServiceException {
		doSending(args);
	}

	@Override
	public void doLogic(String... args)  {
		
	}

	@Override
	public void doSending(String[] args) throws ServiceException {
		// on récupère les lignes à envoyer en DBs pour ce fichier 
		String filename = args[0];
		int attempt = Integer.parseInt(args[4]);
		
		// on fait un check car on doit voir des lignes car on est sur un RETRY donc une deuxième tentative au moins
		long nb = fourSightCREToCoatyDAO.getRecordCountByFilename(filename);
		if (nb == 0) {
			logger.warn("On doit trouver des anciennces lignes dans la db sur un RETRY pour le fichier: {} et la tentative: {}", filename, attempt);
			
			// on doit lancer le process du début:
			doParseCsvFile(args);
			return;
		}
		
		List<Record> records = fourSightCREToCoatyDAO.getRecordByFilenameAndByProcessed(filename);
		
		logger.debug("{} Lignes à envoyer pour le fichier: {}", records.size(), filename);
		
		// on envoie ces lignes via JMS en lot et en asynchrone
		List<Record> recordsForNextBatch = new ArrayList();
		int j = 0;
		for (int i = 0; i < records.size(); i++) {
			String line = records.get(i).getLine();
			
			String mqCre = StringUtils.generateCRE(records.get(i));
			records.get(i).setMqCre(mqCre);
			
			recordsForNextBatch.add(records.get(i));
			j++;
			
			if (j >= batchModeSize){
				logger.info("On traite un premier lot de taille: {}", batchModeSize);
				
				List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
				
				mqAsyncSender.doSend(recordsForNextBatchCOPY);
				
				// on vide le stock pour le prochain lot
				recordsForNextBatch.clear();
				
				j=0;
			}
		} // fin du for
		
		// il reste peut être quelques lignes non traitées en lot
		if (j > 0) {
			logger.info("On traite le dernier lot de taille: {}", j);
			List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
			
			mqAsyncSender.doSend(recordsForNextBatchCOPY);
			recordsForNextBatch.clear();
		}
		
	}
	
	private void showRecords (List<Record> records) {
		for (int i = 0; i < records.size(); i++) {
			logger.debug("id: {}\t\traw_cre: {}", records.get(i).getId(), records.get(i).getLine());
		}
	}

}
