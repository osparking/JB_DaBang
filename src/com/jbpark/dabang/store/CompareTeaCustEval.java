package com.jbpark.dabang.store;

import java.util.Comparator;

public class CompareTeaCustEval implements Comparator<TeaProduct> {

	@Override
	public int compare(TeaProduct tea1, TeaProduct tea2) {
		return Float.compare(tea2.get고객평점(), tea1.get고객평점());
	}

}
