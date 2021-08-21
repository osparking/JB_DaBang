package com.jbpark.dabang.test;

import org.junit.jupiter.api.Test;

import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.store.DaBang;
import com.jbpark.dabang.store.상품관리;

class Test상품관리 {

	@Test
	void test_searchAndOrder() {
		var customer = DaBang.read전통고객("myself");
		
		try {
			상품관리.searchAndOrder(customer);
		} catch (StopSearchingException e) {
			System.out.println("StopSearchingException");
		}
	}
}
