package com.jbpark.dabang.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.store.DaBang;

class TestMethods {

	@Test
	void test() {
		var daBang = new DaBang();
		RoadAddress address = new RoadAddress(
				"4111512000100050001011066", "16257", "매미소리");
		int 단지번호 = daBang.save단지주소Test(address);
		assertTrue(단지번호 > 0);
	}

}
