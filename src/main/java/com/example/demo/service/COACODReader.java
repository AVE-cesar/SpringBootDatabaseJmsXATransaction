package com.example.demo.service;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ibm.mq.MQC;

//@Component
public class COACODReader {

	private final static Logger logger = LoggerFactory.getLogger(COACODReader.class);
	
	//@JmsListener(destination = "TESTBOB")
	public void receiveMessage(Message message) {
		
		TextMessage textMessage = (TextMessage)message;
		logger.info("message COA COD: {}",textMessage);
		
		try {
			
			int feedback = textMessage.getIntProperty("JMS_IBM_Feedback");
			
			if (feedback == MQC.MQFB_COA) {
				// Confirmation of arrival on the destination queue
			}
			
			if (feedback == MQC.MQFB_COD) {
				// Confirmation of delivery to the receiving application
			}
			
			Enumeration<?> prop_enum;
			prop_enum = textMessage.getPropertyNames();
			String prop;
		    while (prop_enum.hasMoreElements()) {
		        prop = (String) prop_enum.nextElement();
		        
				logger.trace("{} = {}", prop,textMessage.getStringProperty(prop));
			}
		} catch (JMSException e) {
			logger.warn("",e);
		}
	}
}
