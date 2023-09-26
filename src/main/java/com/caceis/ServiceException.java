package com.caceis;

import java.io.IOException;

public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2253047276831584339L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(IOException ex) {
		super(ex);
	}

}
