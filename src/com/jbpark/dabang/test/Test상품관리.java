package com.jbpark.dabang.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.store.DaBang;
import com.jbpark.dabang.store.DeliverAddress;
import com.jbpark.dabang.store.상품관리;

class Test상품관리 {

	@Test
	void test_buyNow_first() {
		int 상품ID = 6; // 보성녹차
		int 주문수량 = 2;
		int 고객SN = 6; // myself
		
		int 단지번호 = 16;
		String 상세주소 = "102호";
		var address = new DeliverAddress(단지번호, 상세주소);
		
		int result = 상품관리.buyNow(상품ID, 주문수량, 고객SN, 
				address);
		assertEquals(result, 1);
	}
	
	@Test
	@Disabled("not restful")
	void test_searchAndOrder() {
		var customer = DaBang.read전통고객("myself");
		
		try {
			상품관리.searchAndOrder(customer);
		} catch (StopSearchingException e) {
			System.out.println("StopSearchingException");
		}
	}
}
