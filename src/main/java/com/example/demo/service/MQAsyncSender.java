package com.example.demo.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.ForSightCREtoCOATY;
import com.example.demo.dao.FourSightCREToCoatyDAO;
import com.example.demo.model.Record;
import com.ibm.mq.jms.MQQueue;

@Service
public class MQAsyncSender {

	// formattage de date
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 	
		
	private final static Logger logger = LoggerFactory.getLogger(MQAsyncSender.class);
	
	@Autowired
	FourSightCREToCoatyDAO fourSightCREToCoatyDAO;

	JmsTemplate jmsTemplate;
	
	@Value("${queuemanager.outputQueueName}")
	public String queueManagerOutputQueueName;
	
	@Value("${queuemanager.activateCOA}")
	public boolean queueManagerActivateCOA;
	
	@Value("${queuemanager.activateCOD}")
	public boolean queueManagerActivateCOD;
	
	@Value("${queuemanager.replyToQueueName}")
	public String queueManagerReplyToQueueName;
	
	@Autowired
	ConfigurableApplicationContext applicationContext;
	
	@Async/*("taskExecutor")*/
	@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	public void doSend(long count, String filename) {

		// https://spring.io/guides/gs/async-method/
		
		Date now = new Date(System.currentTimeMillis());
		Record record = new Record(null, filename);
		logger.info("insert record into database: {}", count);
		fourSightCREToCoatyDAO.createRecord(record);
		
		List<Record> records = new ArrayList();
		records.add(new Record("Batch 1"));
		records.add(new Record("Batch 2"));
		records.add(new Record("Batch 3"));
		records.add(new Record("Batch 4"));
		records.add(new Record("Batch 5"));
		records.add(new Record("Batch 6"));
		
		fourSightCREToCoatyDAO.createBatchRecord(records);
		
		
		// FIXME mettre ailleurs: postConstruct
		Object obj = applicationContext.getBean("queueTemplate");
						
		if (jmsTemplate == null) jmsTemplate = (JmsTemplate)obj;
		List<String> messages = new ArrayList();
		messages.add(" 1/3 en lot: "+count);
		messages.add(" 2/3 en lot: "+count);
		messages.add(" 3/3 en lot: "+count);
		
		
		ProducerCallback producerCallback = (session, producer) -> {
		    Queue queue = session.createQueue(queueManagerOutputQueueName);
		    for (String messageText : messages) {
		        producer.send(queue, session.createTextMessage(messageText));
		    }
		    return null;
		};
		jmsTemplate.execute(producerCallback);
				
		logger.info("sending MQ message only JMS {} avec COA: {} et COD: {}",count, queueManagerActivateCOA, queueManagerActivateCOD);
		jmsTemplate.convertAndSend(queueManagerOutputQueueName, filename +": "+count);
		
		//return CompletableFuture.completedFuture(new Boolean(true));
		long t3 = System.currentTimeMillis();
		logger.info("total time en min: {} et en sec {}", (t3-ForSightCREtoCOATY.START_TIME)/1000/60, (t3-ForSightCREtoCOATY.START_TIME)/1000);
	}
	
	@Async
	@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	public void doInsertRecords(List<Record> records) {
		if (records.size() == 0) {
			logger.info("Le lot NE peut PAS être vide et pourtant: {}", records.size());
			return;
		}
		Date now = new Date(System.currentTimeMillis());
		logger.info("insert records by batch into database: {}", records.size());
		fourSightCREToCoatyDAO.createBatchRecord(records);		
	}
	
	@Async
	@Transactional(rollbackFor = ServiceException.class, timeout=299, readOnly=false)
	public void doSend(List<Record> records) {

		if (records.size() == 0) {
			logger.info("Le lot NE peut PAS être vide et pourtant: {}", records.size());
			return;
		}
		Date now = new Date(System.currentTimeMillis());
		//logger.info("insert records by batch into database: {}", records.size());
		//fourSightCREToCoatyDAO.createBatchRecord(records);
		
		// FIXME mettre ailleurs: postConstruct
		Object obj = applicationContext.getBean("queueTemplate");
						
		if (jmsTemplate == null) jmsTemplate = (JmsTemplate)obj;
		
		List<String> messages = new ArrayList();
		for (int i = 0; i < records.size(); i++) {
			logger.debug("On envoie le message suivant: [{}]", records.get(i).getMqCre());
			messages.add(records.get(i).getMqCre());	
		}
		
		ProducerCallback producerCallback = null;
		if (queueManagerActivateCOA || queueManagerActivateCOD) {
			// soit COA soit COD sont activés
			producerCallback = (session, producer) -> {
			    Queue queue = session.createQueue("queue:///" + queueManagerOutputQueueName + "?targetClient=1");
			    for (int i = 0; i < messages.size(); i++){
			    	TextMessage message = session.createTextMessage(messages.get(i));  	
			    	
			    	logger.info("La queue de retour pour les COD/COD est: {}", queueManagerReplyToQueueName);
			    	message.setJMSReplyTo((Destination) new MQQueue(queueManagerReplyToQueueName));
			    	
			    	if (queueManagerActivateCOA) {
		            	// ou com.ibm.mq.MQC.MQRO_COA_WITH_FULL_DATA
		            	message.setIntProperty("JMS_IBM_Report_COA", com.ibm.mq.MQC.MQRO_COA);
		            }
		            if (queueManagerActivateCOD) {
		            	message.setIntProperty("JMS_IBM_Report_COD",com.ibm.mq.MQC.MQRO_COD);
		            }
			    	
			        producer.send(queue, message);
			        records.get(i).setIdQueueMessage(message.getJMSMessageID().substring(3));
			    }
			    return null;
			};
		} else {
			// ni COA ni COD
			producerCallback = (session, producer) -> {
			    Queue queue = session.createQueue("queue:///" + queueManagerOutputQueueName + "?targetClient=1");
			    for (String messageText : messages) {
			    	TextMessage message = session.createTextMessage(messageText);
			        producer.send(queue, message);
			    }
			    return null;
			};
		}
		logger.info("sending MQ message by batch avec COA: {} et COD: {}", queueManagerActivateCOA, queueManagerActivateCOD);
		jmsTemplate.execute(producerCallback);
		
		logger.info("Updating Processed column");
		fourSightCREToCoatyDAO.updateBatchRecord(records);
		
		long t3 = System.currentTimeMillis();
		logger.info("total time en min: {} et en sec {}", (t3-ForSightCREtoCOATY.START_TIME)/1000/60, (t3-ForSightCREtoCOATY.START_TIME)/1000);
	}

}
