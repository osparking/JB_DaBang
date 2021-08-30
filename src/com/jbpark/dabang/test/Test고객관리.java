package com.jbpark.dabang.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.jbpark.dabang.store.CustomerInfo;
import com.jbpark.dabang.store.DaBang;

class Test고객관리 {

	/**
	 * 고객 ID로 고객 정보 채취 test case
	 */
	@Test
	void testRead전통고객() {
		String 고객ID = "myself";
		String 고객이름 = "아무개";
		CustomerInfo customer = DaBang.read전통고객(고객ID);
		
		assertTrue(customer != null && 
				고객이름.equals(customer.get고객이름()));
	}
}
