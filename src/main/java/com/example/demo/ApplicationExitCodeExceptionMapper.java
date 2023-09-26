package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.stereotype.Component;

import com.example.demo.dao.FourSightCREToCoatyDAOImpl;

/**
 * Pour reprendre le contrôle du code de sortie de l'application en cas d'exception.
 * @author bave
 *
 */
@Component
public class ApplicationExitCodeExceptionMapper implements ExitCodeExceptionMapper {

	private final static Logger logger = LoggerFactory.getLogger(FourSightCREToCoatyDAOImpl.class);
	
	@Override
	public int getExitCode(Throwable exception) {
		logger.debug("Exit Code");
		return -1;
	}

}
