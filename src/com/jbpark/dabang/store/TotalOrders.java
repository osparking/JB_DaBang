package com.jbpark.dabang.store;

public class TotalOrders {
	private static int totalOrderCount = 0;

	/**
	 * 전통차 전체 주문량을 증가시킨다.
	 * @param amount 증가시킬 주문량
	 */
	public void increaseTotalOrderCount(int amount) {
		totalOrderCount += amount;
	}
	
	/**
	 * 각 주문을 통하여 주문된 전통차 전체 주문량 
	 * @return 차 주문 총량
	 */
	public static int getOrderCount() {
		return totalOrderCount;
	}

	public static void setOrderCount(int orderCount) {
		TotalOrders.totalOrderCount = orderCount;
	}
	
}
