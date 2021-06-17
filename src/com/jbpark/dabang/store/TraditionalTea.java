package com.jbpark.dabang.store;

import java.time.LocalDate;

/**
 * 전통차
  - 상품 정보: 이미지 1장, 설명 텍스트, 제품 설명 문서(odt 파일), 
  - 가격, 용량, 제조일자, 제고 수량, Q&A, 구매 후기(평점 등)
  - 관련 기능: 상품 등록, 정보 수정
 * @author jbpar
 *
 */
public class TraditionalTea {
	private String 제품코드; // 제품 코드
	private String 제품이름; // 제품 이름(예, 율무차)
	private String 제품설명; // 최대 500 글자
	private double 제품가격; // 1포장 가격
	private int 티백중량; // 용량, 단위: 그램
	private int 제고티백수 = 0; // 단위: 티백
	private int 누적판매수 = 0; // 누적판매 티백 수
	private LocalDate 제조일 = null; // 제품 제조일자
	private LocalDate 등록일; // 제품 등록 일자
	private int 포장티백수; // 예, 20
	// 이미지(jpg, png, bmp)
	// 설명 odt 파일
	
	public String get제품코드() {
		return 제품코드;
	}
	public TraditionalTea() {
//		this();
//		this.제품코드 = 제품코드;
//		this.제품이름 = 제품이름;
//		this.제품설명 = 제품설명;
//		this.제품가격 = 제품가격;
//		this.티백중량 = 티백중량;
//		this.제고티백수 = 제고티백수;
//		this.누적판매수 = 누적판매수;
//		this.제조일 = 제조일;
//		this.등록일 = 등록일;
//		this.포장티백수 = 포장티백수;		
	}
	public TraditionalTea(String 제품코드, String 제품이름, String 제품설명,
			double 제품가격, int 티백중량, int 제고티백수, int 누적판매수,
			LocalDate 제조일, LocalDate 등록일, int 포장티백수) {
		super();
		this.제품코드 = 제품코드;
		this.제품이름 = 제품이름;
		this.제품설명 = 제품설명;
		this.제품가격 = 제품가격;
		this.티백중량 = 티백중량;
		this.제고티백수 = 제고티백수;
		this.누적판매수 = 누적판매수;
		this.제조일 = 제조일;
		this.등록일 = 등록일;
		this.포장티백수 = 포장티백수;
	}
	public void set제품코드(String 제품코드) {
		this.제품코드 = 제품코드;
	}
	public String get제품이름() {
		return 제품이름;
	}
	public void set제품이름(String 제품이름) {
		this.제품이름 = 제품이름;
	}
	public String get제품설명() {
		return 제품설명;
	}
	public void set제품설명(String 제품설명) {
		this.제품설명 = 제품설명;
	}
	public double get제품가격() {
		return 제품가격;
	}
	public void set제품가격(double 제품가격) {
		this.제품가격 = 제품가격;
	}
	public int get티백중량() {
		return 티백중량;
	}
	public void set티백중량(int 티백중량) {
		this.티백중량 = 티백중량;
	}
	public int get제고티백수() {
		return 제고티백수;
	}
	public void set제고티백수(int 제고티백수) {
		this.제고티백수 = 제고티백수;
	}
	public int get누적판매수() {
		return 누적판매수;
	}
	public void set누적판매수(int 누적판매수) {
		this.누적판매수 = 누적판매수;
	}
	public LocalDate get제조일() {
		return 제조일;
	}
	public void set제조일(LocalDate 제조일) {
		this.제조일 = 제조일;
	}
	public LocalDate get등록일() {
		return 등록일;
	}
	public void set등록일(LocalDate 등록일) {
		this.등록일 = 등록일;
	}
	public int get포장티백수() {
		return 포장티백수;
	}
	public void set포장티백수(int 포장티백수) {
		this.포장티백수 = 포장티백수;
	}
}
