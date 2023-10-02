package com.example.demo.tools;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.demo.ForSightCREtoCOATY;
import com.example.demo.dao.FourSightCREToCoatyDAO;
import com.example.demo.dao.FourSightCREToCoatyDAOImpl;
import com.example.demo.service.ImporterService;
import com.example.demo.service.ImporterServiceImpl;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class COATYDailyExtractImport implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(ForSightCREtoCOATY.class);

	public static long START_TIME = System.currentTimeMillis();

	// formattage de date
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Autowired
	ConfigurableApplicationContext applicationContext;

	@Bean
	public ImporterService importerService() {
		return new ImporterServiceImpl();
	}

	@Bean
	public FourSightCREToCoatyDAO fourSightCREToCoatyDAO() {
		FourSightCREToCoatyDAO dao = new FourSightCREToCoatyDAOImpl(
				(DataSource) applicationContext.getBean(DataSource.class));

		return dao;
	}

	public static void main(String[] args) {
		SpringApplication.run(COATYDailyExtractImport.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.trace("Let's inspect the beans provided by Spring Boot:");

		String[] beanNames = applicationContext.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			logger.trace(beanName);
		}

		START_TIME = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		logger.info("START: {}", SDF.format(new Date(t1)));

		importerService().doImport(args);

		long t3 = System.currentTimeMillis();
		logger.info("STOP: " + SDF.format(new Date(t3)));
	}
}
