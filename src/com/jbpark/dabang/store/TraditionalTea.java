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
	String 제품코드; // 제품 코드
	String 제품이름; // 제품 이름(예, 율무차)
	String 제품설명; // 최대 500 글자
	double 제품가격; // 1포장 가격
	int 티백중량; // 용량, 단위: 그램
	int 제고티백수; // 단위: 티백
	int 누적판매수; // 누적판매 티백 수
	LocalDate 제조일; // 제품 제조일자
	LocalDate 등록일; // 제품 등록 일자
	int 포장티백수; // 예, 20
	// 이미지(jpg, png, bmp)
	// 설명 odt 파일
}
