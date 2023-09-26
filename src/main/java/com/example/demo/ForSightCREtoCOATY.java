package com.example.demo;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.Executor;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.example.demo.service.BusinessService;
import com.example.demo.service.ServiceException;
import com.ibm.mq.jms.MQXAQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import oracle.jdbc.xa.client.OracleXADataSource;

// https://gayerie.dev/docs/spring/spring/spring_tx.html

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class ForSightCREtoCOATY implements CommandLineRunner {
	
	private final static Logger logger = LoggerFactory.getLogger(ForSightCREtoCOATY.class);

	public static long START_TIME= System.currentTimeMillis();
	
	// on récupère les variables du fichier application.properties: la datasource
	@Value("${datasource.url}")
	private String datasourceUrl;
	@Value("${datasource.login}")
	private String datasourceLogin;
	@Value("${datasource.pwd}")
	private String datasourcePwd;
	@Value("${datasource.uniqueResourceName}")
	private String datasourceUniqueResourceName;
	@Value("${datasource.minPoolSize}")
	private int datasourceMinPoolSize;
	@Value("${datasource.maxPoolSize}")
	private int datasourceMaxPoolSize;
	@Value("${datasource.poolSize}")
	private int datasourcePoolSize;
	
	// on récupère les variables du fichier application.properties: le queue manager
	@Value("${queuemanager.host}")
	private String queueManagerHost;
	@Value("${queuemanager.name}")
	private String queueManagerName;
	@Value("${queuemanager.port}")
	private int queueManagerPort;
	@Value("${queuemanager.channelName}")
	private String queueManagerChannelName;
	@Value("${queuemanager.transportType}")
	private int queueManagerTransportType;
	@Value("${queuemanager.CCSID}")
	private int queueManagerCCSID;
	@Value("${queuemanager.applicationName}")
	private String queueManagerApplicationName;
	
	@Autowired
	BusinessService businessService;
	
	@Autowired
	ConfigurableApplicationContext applicationContext;
	
	// formattage de date
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		
	public static void main(String[] args) {
		SpringApplication.run(ForSightCREtoCOATY.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		CommandLineUtils.showCommandLineParameters(args);
		/*	pour tester les transactions	
		logger.info(">>>>>>>>>>>>>>>>>on démarre notre logic:");
		try {
			businessService.doLogic();
		} catch (Exception e) {
			logger.error("",e);
		}
		
		businessService.doLogicJMS();
		*/
		START_TIME= System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		logger.info("START: {}", SDF.format(new Date(t1)));
		
		int RETRY = Integer.parseInt(args[4]);
		if (RETRY == 1) {		
			businessService.doParseCsvFile(args);
			businessService.doSending(args);
		} else {
			logger.info("En mode reprise");
			businessService.doRetry(args);
		}
		long t3 = System.currentTimeMillis();
		logger.info("STOP: " + SDF.format(new Date(t3)));
	}
	
	@Bean(name="datasource_1")
	public DataSource datasource_1() throws SQLException {
		OracleXADataSource oracleXADataSource = new OracleXADataSource();
		
		oracleXADataSource.setURL(datasourceUrl);
		oracleXADataSource.setUser(datasourceLogin);
		oracleXADataSource.setPassword(datasourcePwd);
		
		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(oracleXADataSource);
        xaDataSource.setUniqueResourceName(datasourceUniqueResourceName);
        xaDataSource.setMinPoolSize(datasourceMinPoolSize);
        xaDataSource.setMaxPoolSize(datasourceMaxPoolSize);
        xaDataSource.setPoolSize(datasourcePoolSize);
        
        xaDataSource.setTestQuery("select 1 from dual");
        
        return xaDataSource;
	}
	
	@Bean
	public MQXAQueueConnectionFactory mqQueueConnectionFactory() {
		MQXAQueueConnectionFactory mqQueueConnectionFactory = new MQXAQueueConnectionFactory();
		try {	
			mqQueueConnectionFactory.setHostName(queueManagerHost);
			mqQueueConnectionFactory.setQueueManager(queueManagerName);
			mqQueueConnectionFactory.setPort(queueManagerPort);
			mqQueueConnectionFactory.setChannel(queueManagerChannelName);
			
			mqQueueConnectionFactory.setAppName(queueManagerApplicationName);
			
			mqQueueConnectionFactory.setTransportType(queueManagerTransportType);
			mqQueueConnectionFactory.setCCSID(queueManagerCCSID);	
			
			
		} catch (Exception e) {
			logger.error("",e);
		}
		return mqQueueConnectionFactory;
	}
	
	@Bean
	public JmsTemplate queueTemplate(MQXAQueueConnectionFactory mqQueueConnectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(/*mqQueueConnectionFactory*/);
		
		jmsTemplate.setReceiveTimeout(3000);
		jmsTemplate.setSessionTransacted(true);
		jmsTemplate.setSessionAcknowledgeMode(0);
		
		CachingConnectionFactory cc = new CachingConnectionFactory();
        cc.setTargetConnectionFactory(mqQueueConnectionFactory);
        jmsTemplate.setConnectionFactory(cc);
		
		return jmsTemplate;
	}

	@Bean(name="transactionManager", destroyMethod = "close", initMethod = "init")
	public UserTransactionManager transactionManager(){
	    UserTransactionManager userTransactionManager = new UserTransactionManager();
	    return userTransactionManager;
	}

	
	@Bean(name = "userTransaction")
	public UserTransaction userTransaction(){
	    
	    UserTransaction userTransaction = new UserTransactionImp ();
	    return userTransaction;
	}

	@Bean("jtaTransactionManager")
	public JtaTransactionManager jtaTransactionManager(@Qualifier("transactionManager") UserTransactionManager userTransactionManager, 
			@Qualifier("userTransaction") UserTransaction userTransaction) {
	    JtaTransactionManager transactionManager = new JtaTransactionManager();
	    transactionManager.setTransactionManager(userTransactionManager);
	    transactionManager.setUserTransaction(userTransaction);
	    
	    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\t\tCreating JTA Transaction Manager.");
	    return transactionManager;
	}
	
	/**
	 * Pool de thead pour les tâches asynchrones
	 * @return
	 */
	//@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(20);
	    executor.setMaxPoolSize(20);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("_BAD_CACEIS_Async-");
	    
	    
	    executor.initialize();
	    
	    return executor;
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			logger.trace("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				logger.trace(beanName);
			}

		};
	}
	
	@Bean
	public ExitCodeGenerator exitCodeGenerator() {
	    return new ExitCodeGenerator() {
	        @Override
	        public int getExitCode() {
	            return 42;
	        }
	    };
	}
}
