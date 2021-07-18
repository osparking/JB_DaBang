package com.jbpark.dabang.store;

import java.io.Serializable;

public class NoAddressInputException extends Exception implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1368717984461308666L;

	public NoAddressInputException(String msg) {
		super(msg);
	}
}
