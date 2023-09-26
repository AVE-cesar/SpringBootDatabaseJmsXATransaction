package com.example.demo.service;

/**
 * Exception propre à cette application.
 * 
 * @author bave
 *
 */
public class ServiceException extends Exception {

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5464148981315513546L;

}
