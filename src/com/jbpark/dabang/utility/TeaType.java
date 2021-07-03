package com.jbpark.dabang.utility;

/**
 * 한국 전통차(예, {@code 보성녹차})의 종류 열거형
 * 
 * @author Park, JongBum
 *
 */
public enum TeaType {
	감잎차("ㄱ"), 보성녹차("ㅂ"), 율무차("ㅇ"), 비선택("");

	private String 단축명;

	private TeaType(String 단축명) {
		this.단축명 = 단축명;
	}

	public String get단축명() {
		return 단축명;
	}
	
	@Override
	public String toString() {
		return name() + "(" + get단축명() + ")";
	}
}
