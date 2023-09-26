package com.example.demo.service;

public interface BusinessService {

	void doLogic(String... args) throws ServiceException;
	
	void doLogicJMS(String... args) throws ServiceException;
	
	void doParseCsvFile(String... args) throws ServiceException;

	void doRetry(String... args) throws ServiceException;

	void doSending(String[] args) throws ServiceException;
}
