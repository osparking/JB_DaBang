package com.jbpark.dabang.store;

import java.time.LocalDate;

import com.jbpark.dabang.utility.TeaType;

/**
 * 전통차
  - 상품 정보: 이미지 1장, 설명 텍스트, 제품 설명 문서(odt 파일), 
  - 가격, 용량, 제조일자, 제고 수량, Q&A, 구매 후기(평점 등)
  - 관련 기능: 상품 등록, 정보 수정
 * @author jbpar
 *
 */
public class TraditionalTea {
	private int 상품ID; // 제품 코드
	private TeaType 차종류; // 제품 이름(예, 율무차)
	private int 제고수량 = 0; // 단위: 티백
	private LocalDate 제조일 = null; // 제품 제조일자
	private String 용량; // 용량, 단위: 그램
	private double 가격; // 1포장 가격
	private String 설명; // 최대 500 글자
	
	private int 누적판매수 = 0; // 누적판매 티백 수
	private LocalDate 등록일; // 제품 등록 일자
	private int 포장티백수; // 예, 20
	// 이미지(jpg, png, bmp)
	// 설명 odt 파일

	public TraditionalTea() {
	}
	
	public TraditionalTea(int 상품ID, TeaType 차종류, 
			int 제고수량, LocalDate 제조일, String 용량, 
			double 가격, String 설명) {
		super();
		this.상품ID = 상품ID;
		this.차종류 = 차종류;
		this.제고수량 = 제고수량;
		this.제조일 = 제조일;
		this.용량 = 용량;
		this.가격 = 가격;
		this.설명 = 설명;
	}

	public int get상품ID() {
		return 상품ID;
	}

	public TeaType get차종류() {
		return 차종류;
	}

	public int get제고수량() {
		return 제고수량;
	}

	public LocalDate get제조일() {
		return 제조일;
	}

	public String get용량() {
		return 용량;
	}

	public double get가격() {
		return 가격;
	}

	public String get설명() {
		return 설명;
	}

	public int get누적판매수() {
		return 누적판매수;
	}

	public LocalDate get등록일() {
		return 등록일;
	}

	public int get포장티백수() {
		return 포장티백수;
	}
	
}
