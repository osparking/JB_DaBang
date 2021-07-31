package com.jbpark.dabang.store;

public class NoSuch고객Exception extends Exception {

	private static final long serialVersionUID = -3790646415921432472L;
	public NoSuch고객Exception(String userId) {
		super("아이디 '" + userId + "'인 고객은 없습니다.");
	}
}
