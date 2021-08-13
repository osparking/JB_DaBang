package com.jbpark.dabang.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.store.DaBang;

class TestMethods {

	@Test
	void read전통고객test() {
		String custId = "myself";
		var customer = DaBang.read전통고객(custId);
		assertTrue(customer.get고객ID().equals(custId));
		
	}
	@Test
	@Disabled("관리번호 유일 값으로 바꾼 뒤까지...")
	void test() {
		var daBang = new DaBang();
		RoadAddress address = new RoadAddress(
				"4111513400101110023010418", "16257", "입추공기");
		int 단지번호 = daBang.save단지주소Test(address);
		assertTrue(단지번호 > 0);
	}
	
	@Test
	void test_getTeaProductList() {
		DaBang daBang = new DaBang();
		String [] keys = {"전라", "녹차"};
		
		var list = daBang.callGetTeaProductList(keys);
		assertTrue(list.size() == 2);
	}
}
