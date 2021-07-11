package com.jbpark.dabang.store;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

import com.jbpark.dabang.utility.TeaType;

public class TeaProduct implements Comparable<TeaProduct> {
	private TeaType teaType;
	private BigDecimal 가격;
	private LocalDate 제품등록일;
	private int 재고수량;
	private LocalDate 생산일;
	private float 제품중량;
	private String 중량설명;
	private float 고객평점;
	private String 제품설명;

	public static void main(String[] args) {
		TeaProduct[] products = new TeaProduct[3];

		// @formatter:off
		products[0] = new TeaProduct(TeaType.감잎, 
				new BigDecimal(1_2000), LocalDate.of(2020, 12, 2),
				LocalDate.of(2020, 10, 20), 4.1f);
		products[1] = new TeaProduct(TeaType.율무차, 
				new BigDecimal(1_2000), LocalDate.of(2021, 6, 20),
				LocalDate.of(2020, 10, 20), 4.5f);
		products[2] = new TeaProduct(TeaType.보성녹차, 
				new BigDecimal(1_2000), LocalDate.of(2021, 2, 11),
				LocalDate.of(2020, 10, 20), 4.8f);
		// @formatter:on

		Random rand = new Random();
		StringBuffer 정렬기준 = new StringBuffer("정렬기준: ");
		
		if (rand.nextDouble() > 0.5) {
			정렬기준.append("제품등록일 최근순 => ");
			Arrays.sort(products);
		} else {
			정렬기준.append("고객평가 감소순 => ");
			Arrays.sort(products, new CompareTeaCustEval());
		}
		System.out.println(정렬기준);
		for (TeaProduct product : products) {
			System.out.println(product);
		}
	}

	/**
	 * 추후 제품을 정렬할 중요한 4 필드를 입력받아 제품을 생성한다.
	 * @param teaType
	 * @param 가격
	 * @param 제품등록일
	 * @param 생산일
	 * @param 고객평점
	 */
	public TeaProduct(TeaType teaType, BigDecimal 가격, LocalDate 제품등록일, LocalDate 생산일, float 고객평점) {
		super();
		this.teaType = teaType;
		this.가격 = 가격;
		this.제품등록일 = 제품등록일;
		this.생산일 = 생산일;
		this.고객평점 = 고객평점;
	}
	
	/**
	 * 제품의 모든 필드를 입력받아 제품을 생성한다.
	 * @param teaType
	 * @param 가격
	 * @param 제품등록일
	 * @param 재고수량
	 * @param 생산일
	 * @param 제품중량
	 * @param 중량설명
	 * @param 고객평점
	 * @param 제품설명
	 */
	public TeaProduct(TeaType teaType, BigDecimal 가격, LocalDate 제품등록일, int 재고수량, LocalDate 생산일, float 제품중량, String 중량설명,
			float 고객평점, String 제품설명) {
		super();
		this.teaType = teaType;
		this.가격 = 가격;
		this.제품등록일 = 제품등록일;
		this.재고수량 = 재고수량;
		this.생산일 = 생산일;
		this.제품중량 = 제품중량;
		this.중량설명 = 중량설명;
		this.고객평점 = 고객평점;
		this.제품설명 = 제품설명;
	}

	public TeaType getTeaType() {
		return teaType;
	}

	public void setTeaType(TeaType teaType) {
		this.teaType = teaType;
	}

	public BigDecimal get가격() {
		return 가격;
	}

	public void set가격(BigDecimal 가격) {
		this.가격 = 가격;
	}

	public LocalDate get제품등록일() {
		return 제품등록일;
	}

	public void set제품등록일(LocalDate 제품등록일) {
		this.제품등록일 = 제품등록일;
	}

	public int get재고수량() {
		return 재고수량;
	}

	public void set재고수량(int 재고수량) {
		this.재고수량 = 재고수량;
	}

	public LocalDate get생산일() {
		return 생산일;
	}

	public void set생산일(LocalDate 생산일) {
		this.생산일 = 생산일;
	}

	public float get제품중량() {
		return 제품중량;
	}

	public void set제품중량(float 제품중량) {
		this.제품중량 = 제품중량;
	}

	public String get중량설명() {
		return 중량설명;
	}

	public void set중량설명(String 중량설명) {
		this.중량설명 = 중량설명;
	}

	public float get고객평점() {
		return 고객평점;
	}

	public void set고객평점(float 고객평점) {
		this.고객평점 = 고객평점;
	}

	public String get제품설명() {
		return 제품설명;
	}

	public void set제품설명(String 제품설명) {
		this.제품설명 = 제품설명;
	}

	@Override
	public int compareTo(TeaProduct o) {
		return o.제품등록일.compareTo(this.get제품등록일());
	}

	@Override
	public String toString() {
		return "TeaProduct [teaType=" + teaType + ", 가격=" + 가격 + ", 제품등록일=" + 제품등록일 + ", 생산일=" + 생산일 + ", 고객평점=" + 고객평점
				+ "]";
	}

}
