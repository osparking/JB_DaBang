package com.jbpark.dabang.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.store.CustomerInfo;
import com.jbpark.dabang.store.DaBang;
import com.jbpark.dabang.store.상품관리;
import com.jbpark.dabang.store.주소관리;

class TestMethods {

	@Test
	void testManageOwnAddress() {
		var customer = new CustomerInfo();
		customer.set고객ID("myself");
		customer.set고객SN(8);
		var daBang = new DaBang();
		daBang.callManageOwnAddress(customer);
	}
	
	@Test
	@Disabled
	void read전통고객test() {
		String custId = "myself";
		var customer = DaBang.read전통고객(custId);
		assertTrue(customer.get고객ID().equals(custId));		
	}
	
	@Test
	@Disabled("관리번호 유일 값으로 바꾼 뒤까지...")
	void test() {
		RoadAddress address = new RoadAddress(
				"4111513400101110023010418", "16257", "입추공기");
		int 단지번호 = 주소관리.save단지주소Test(address);
		assertTrue(단지번호 > 0);
	}
	
	@Test
	@Disabled
	void test_getTeaProductList() {
		String [] keys = {"전라", "녹차"};
		
		var list = 상품관리.callGetTeaProductList(keys);
		assertTrue(list.size() == 2);
	}
}
