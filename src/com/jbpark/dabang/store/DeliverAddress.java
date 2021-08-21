package com.jbpark.dabang.store;

import lombok.Getter;

@Getter
public class DeliverAddress {
	private int 단지번호;
	private String 상세주소;
	/**
	 * @param 단지번호
	 * @param 상세주소
	 */
	public DeliverAddress(int 단지번호, String 상세주소) {
		this.단지번호 = 단지번호;
		this.상세주소 = 상세주소;
	}
	public void set상세주소(String 상세주소) {
		this.상세주소 = 상세주소;
	}
}
