package com.jbpark.dabang.store;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import com.jbpark.dabang.module.AddrSearchKey;
import com.jbpark.dabang.module.AddressMan;
import com.jbpark.dabang.module.CustomerAddress;
import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;

public class 주소관리 {

	/**
	 * 고객 주소 선택 받아 상세 주소 입력 및 DB 저장
	 * @param scanner
	 * @param 고객sn
	 * @throws NoInputException
	 * @throws StopSearchingException
	 */
	static DeliverAddress get배송주소(Scanner scanner, int 고객sn)
			throws  NoInputException, StopSearchingException {
		int page = AddressMan.getShowPageNumber(scanner, 고객sn);
		var addresses = AddressMan.getCustomerAddresses(고객sn, page);
		
		AddressMan.showCustomerAddresses(DaBang.getLogger(), addresses);		
		
		// 새 주소 입력 혹은 과거 주소 활용
		if (addresses.size() > 0) {
			boolean resp = DaBang.getUserResponse(
					"과거 주소 중에서 선택하겠습니까?", scanner);
			if (resp)
				return useOldAddress(addresses, scanner, 고객sn);
		}  
		return acquireNewAddress(scanner, 고객sn);
	}

	static DeliverAddress acquireNewAddress(Scanner scanner, int 고객SN)	
					throws StopSearchingException, 
							NoInputException {
		// 새 주소 입력
		System.out.println("주소 검색키를 입력하세요.");
		
		AddrSearchKey key = AddressMan.getAddrSearchKey(scanner);
		Integer pageNo;
		
		// 입력 가능한 페이지 번호 범위 표시
		int rows = AddressMan.getTotalRows(key);
		pageNo = AddressMan.getWantedPage(scanner, rows);		
		
		List<RoadAddress> addresses = AddressMan.get20AddressList(key, pageNo);
		
		for (RoadAddress ra : addresses) {
			if (ra != null) DaBang.getLogger().config(ra.toString());
		}
		showResult(addresses);
		
		int idx = Utility.getIntegerValue(scanner, 
				"도로명 주소 번호를 입력하세요.", 
				"주소 번호(1~" + addresses.size() + ")",
				true);
		String detailAddr = AddressMan.getDetailAddr("선택한 주소", 
				addresses.get(idx - 1), scanner);
		int 단지번호 = save단지번호_주소(고객SN, detailAddr, 
				addresses.get(idx - 1));
		
		return new DeliverAddress(단지번호, detailAddr);
	}

	static private void showResult(List<RoadAddress> addresses) {
		String msg = "표시 행: " + addresses.size();
		
		DaBang.getLogger().config(msg);
		System.out.println(msg);
		
		int i = 1;
		for (var ra : addresses) {
			System.out.println("\t" + (i++) + ra);
		}			
	}

	static private DeliverAddress useOldAddress(
			List<CustomerAddress> addresses, 
			Scanner scanner, int 고객SN) {
		// 사용할 과거 주소 번호 요구
		int idx = -1;
		
		while (true) {
			try {
				idx = Utility.getIntegerValue(scanner, 
						"사용할 과거 주소 번호를 입력하세요.", 
						"주소 번호(1~" + addresses.size() + ")",
						true);
				break;
			} catch (NoInputException e) {
				System.out.println("입력 내용은 부적절한 주소 번호입니다.");
			}
		}
		idx--;
		// 세부 주소 변경의향 확인
		CustomerAddress addr = addresses.get(idx);
		
		
		System.out.println("[선택한 주소]"); 
		System.out.println("\t-도로명주소:" + addr.get도로명주소()); 
		System.out.println("\t-세부주소:" + addr.get상세주소());
		
		boolean use그대로 = DaBang.getUserResponse(
				"선택한 위 주소를 그대로 사용하겠습니까?", scanner);

		// 새 세부주소 요구
		DeliverAddress deliverAddress = new DeliverAddress(
				addr.get단지번호(), addr.get상세주소());
		
		if (!use그대로) {
			String 상세주소 = AddressMan.getDetailAddr("선택한 주소", 
					addr, scanner); 
			deliverAddress.set상세주소(상세주소);
			save고객주소(고객SN, addr.get단지번호(), 상세주소);
		}
		return deliverAddress;
	}

	static private int save고객주소(int 고객SN, int 단지번호, 
			String detailedAddr) {
		String iSql = String.format("insert into "
				+ "고객주소(고객SN, 단지번호, 상세주소) "
				+ "values (%s, %s, '%s');",
				고객SN, 단지번호, detailedAddr);
		
		try (var stmt = DaBang.getConnection().createStatement()){
			return stmt.executeUpdate(iSql);
		} catch (SQLException e) {
			e.printStackTrace();
			DaBang.getLogger().severe(e.getMessage());
		}		
		return 0;	
	}

	static private int get단지주소번호(String mgmtNumber) {
		String sql = "select c.단지번호 "
				+ "from 단지주소 c "
				+ "where c.관리번호 = ?";
		try {
			var ps = DaBang.getConnection().prepareStatement(sql);
			ps.setString(1, mgmtNumber);
			ResultSet rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DaBang.getLogger().severe(e.getMessage());
		}
		return 0;
	}
	
	static private int save단지번호_주소(int 고객SN, 
			String detailedAddr, RoadAddress address) {
		// 관리번호 단지주소 등록 여부 판단
		int 단지번호 = get단지주소번호(address.getMgmtNumber());
		
		if (단지번호 < 1) {
			// 비등록이면, 단지주소 등록(삽입)
			단지번호 = save단지주소(address);
		}
		// 고객주소 행 삽입(단지주소자동번호 등 사용)
		save고객주소(고객SN, 단지번호, detailedAddr);
		return 단지번호;
	}

	static public int save단지주소Test(RoadAddress address) {
		return save단지주소(address);
	}

	static private int save단지주소(RoadAddress address) {
		String iSql = String.format("insert into 단지주소"
				+ " (관리번호, 우편번호, 도로명주소) "
				+ "values ('%s', %s, '%s');",
				address.getMgmtNumber(), 
				address.getNewZipcode(), 
				address.getRoadName());
		ResultSet rs = null;
		
		try (var stmt = DaBang.getConnection().createStatement()){
			stmt.executeUpdate(iSql, 
					Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DaBang.getLogger().severe(e.getMessage());
		}		
		return 0;
	}
}
