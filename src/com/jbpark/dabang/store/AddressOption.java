package com.jbpark.dabang.store;

public enum AddressOption {
	WRONG,
	LISTING,
	REGISTER,
	UPDATE,
	DELETE,
	FINISH;
	
	public static AddressOption getOption(int size, int intOpt) {
		AddressOption option = WRONG;
		
		switch (intOpt) {
		case 1:
			if (size == 0)
				option = REGISTER;
			else 
				option = LISTING;
			break;
			
		case 2:
			if (size == 0)
				option = FINISH;
			else 
				option = REGISTER;
			break;
		case 3:
			option = UPDATE;
			break;
		case 4:
			option = DELETE;
			break;
		case 5:
			option = FINISH;
			break;
		}
		return option;
	}
}
