package com.jbpark.dabang.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;
import com.jbpark.dabang.utility.TeaType;

import jbpark.utility.SuffixChecker;

public class 바구니일 {

	public static void searchAndOrder(Scanner scanner, 
			Connection conn, CustomerInfo customer) 
					throws StopSearchingException {
		바구니일.conn = conn;
		TeaType type = getTeaSelection(scanner);  
//		if (type != null){
//			processTeaPurchase(scanner, customer, type);
//		}
	}
	
	static TeaType getTeaSelection(Scanner scanner) {
		TeaType type = null;
		
		do {
			String weekDay = LocalDate.now().format(DateTimeFormatter
					.ofPattern("E").withLocale(Locale.KOREAN)); //E:요일
			
			System.out.println("=".repeat(40));
			System.out.println("다음 차 종류 중에서 선택하세요:");
			System.out.println("=".repeat(40));
			System.out.println(" * : '" + weekDay + "'요일 특별 차!");			
			
			/**
			 * 차 상품 전체 목록 혹은 입력되는 검색 키로 검색한 결과 확보 
			 */
			var teaList = getTeaProducts(scanner);
			int rowCount = showTeaSelection(teaList);
			
			if (rowCount == 0) {
				System.out.println("검색된 차가 없습니다.");
				continue;
			}
			try {
				int tNum = Utility.getIntegerValue(scanner,
						"어떤 차를 원하십니까? : ", 
						"번호(1-" + rowCount + "): ");
				if (tNum >= 1 && tNum <= rowCount) {
					type = confirmSelection(scanner, tNum, 
							teaList.get(tNum-1).get차종류());
				} else {
					System.out.println(tNum + "은 부적절한 선택입니다.");
					continue;
				}
			} catch (TeaInputException e) {
				String msg = e.getMessage() + "를 취소하셨으니, 다시 선택해 주세요.";
				System.out.println("'" + e.getMessage() + msg);
				continue;
			}
			if (type == null) {
				if (DaBang.getUserResponse("주문을 원치 않으십니까", scanner))
					break;
			} else 
				break;
		} while (true);
		
		return type;
	}

	/**
	 * 고객으로부터 선택하는 차 종류 텍스트를 입력받는다.
	 * 
	 * @param scanner 고객 입력 차 종류 텍스트 스캐너
	 * @param tea번호 
	 * @param teaType 
	 * @return 선택된 차 종류 값 혹은 null(선택 불원의 경우)
	 * @throws TeaInputException 입력 오류의 경우 발생됨
	 */
	static private TeaType confirmSelection(Scanner scanner, 
						int tea번호, TeaType type) 
			throws TeaInputException {
		String teaLong = type.toString();
		int idx = teaLong.indexOf('(');
		int cp = teaLong.codePointAt(--idx);
		String sfx = SuffixChecker.has받침(
				cp, teaLong.substring(idx))
				? "을" : "를";
		String msg = tea번호 + "번 " + type + sfx + " 선택하셨습니까";
		
		if (DaBang.getUserResponse(msg, scanner))
			return type;
		else
			throw new TeaInputException(type.toString());
	}

	static private TeaType getTodaySpecial(int size) {
		TeaType[] teaTypes = TeaType.values();
		int idx = (int)ChronoUnit.DAYS.between(
				LocalDate.of(2021, 6, 22), LocalDate.now()) 
				% TeaType.values().length;
		TeaType teaType = teaTypes[idx]; 
		return teaType;
	}

	static private int showTeaSelection(ArrayList<TraditionalTea> teaList) {
		TeaType todayTea= getTodaySpecial(teaList.size());
		for (int i = 0; i < teaList.size(); i++) {
			var teaMenu = new StringBuilder(" " + (i+1) +".");
			TeaType currTea = teaList.get(i).get차종류(); 
			teaMenu.append(currTea);
			if (currTea == todayTea)
				teaMenu.append("*");
			teaMenu.append(" - " + teaList.get(i).get설명());
			System.out.println(teaMenu);
		}
		
		System.out.println(" -메뉴 선택 안함([엔터])");
		System.out.println("=".repeat(40));
		return teaList.size();		
	}
	
	/**
	 * 두 자 이상의 단어를 최대 3개 수령하여 반환한다.
	 * @param scanner 
	 * @return
	 */
	static private String[] getTeaSearchKeys(Scanner scanner) {
		System.out.println("차 상품 검색키를 두 자 이상 단어 최대 3개까지 입력하세요.");
		System.out.println("각 단어는 공백으로 분리할 것. (예, 전라도 보성 녹차");
		System.out.print("\t: ");
		if (scanner.hasNextLine()) {
			String keys = scanner.nextLine();
			String[] searchKeys = keys.split("[ |\t]");
			return searchKeys;
		}
		return null;
	}

	public ArrayList<TraditionalTea> callGetTeaProductList(
			String[] teaKeys) {
		return getTeaProductList(teaKeys);
	}
	
	static private ArrayList<TraditionalTea> getTeaProductList(String[] 
			teaKeys) {
		if (teaKeys.length == 1) {
			//	1 개 : like 절로 검색  
			return searchProducts(teaKeys[0]);		
		} else {
			OptionalInt maxWordLen = Arrays.asList(teaKeys)
					.stream().mapToInt(String::length).max();
			if (maxWordLen.isPresent() && 
					maxWordLen.getAsInt() >= 3) {
				return fullTextSearch(teaKeys);
			} else {
				return unionAndWithOr(teaKeys);			
			}
		}
	}

	/**
	 * 주어진 길이 2 이하의 검색 키를 일단 AND로 연결한 조건의 결과에
	 * OR로 연결한 조건의 결과를 합(union)하여 결과로 반환
	 * @param teaKeys 검색키
	 * @return 차 상품 목록
	 */
	static private ArrayList<TraditionalTea> unionAndWithOr(String[] teaKeys) {
		var sqlBldr = new StringBuilder();
		sqlBldr.append("SELECT * FROM 전통차 where ");
		sqlBldr.append("설명 like ? ");
		for (int i = 1; i<teaKeys.length; i++) {
			sqlBldr.append("&& 설명 like ? "); // AND로 연결
		}
		sqlBldr.append("union ");
		sqlBldr.append("SELECT * FROM 전통차 where ");
		sqlBldr.append("설명 like ? ");
		for (int i = 1; i<teaKeys.length; i++) {
			sqlBldr.append("|| 설명 like ? "); // OR로 연결
		}
		try (PreparedStatement pstmt = 
				conn.prepareStatement(sqlBldr.toString())) {
			for (int sq = 0; sq < 2; sq++) {
				for (int i = 0; i < teaKeys.length; i++) {
					int idx = (i+1)+sq*teaKeys.length;
					pstmt.setString(idx, "%" + teaKeys[i] + "%");			
				}
			}
			ResultSet rs = pstmt.executeQuery();
			
			return getProductList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static private ArrayList<TraditionalTea> getProductList(
			ResultSet rs) throws SQLException {
		var teaList = new ArrayList<TraditionalTea>();				
		
		while (rs.next()) {
			TeaType type = TeaType.valueOf(
					rs.getString("차이름"));
			Date date제조 = rs.getDate("제조일");
			LocalDate 제조일 = 
					convertToLocalDateViaMilisecond(date제조);
			
			teaList.add(new TraditionalTea(
					rs.getInt("상품ID"), type,
					rs.getInt("제고수량"), 제조일, 
					rs.getString("용량"), rs.getDouble("가격"),
					rs.getString("설명")));
		}
		return teaList;
	}
	
	static private LocalDate convertToLocalDateViaMilisecond(
			Date dateToConvert) {
	    return Instant.ofEpochMilli(dateToConvert.getTime())
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}

	static private ArrayList<TraditionalTea> fullTextSearch(String[] teaKeys) {
		String sql = "SELECT * FROM 전통차 WHERE MATCH(설명) AGAINST(?)"
				+ " order by 차이름";	
		String commaSepStrings = String.join(",", teaKeys);
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, commaSepStrings);
			
			ResultSet rs = pstmt.executeQuery();
			
			return getProductList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static private ArrayList<TraditionalTea> searchProducts(String key) {
		String sql = "SELECT 상품ID, 차이름, 제고수량, 제조일, 용량,"
				+ "가격, 설명 FROM 전통차 "
				+ "where 설명 like ? order by 차이름";	
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, "%" + key + "%");
			
			ResultSet rs = pstmt.executeQuery();
			
			return getProductList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	static private ArrayList<TraditionalTea> getAllProducts() {
		try (Statement stmt = conn.createStatement()) {
			String query = "SELECT 상품ID, 차이름, 제고수량, "
					+ "제조일, 용량, 가격, 설명 "
					+ "FROM 전통차 order by 차이름";				
			ResultSet rs = stmt.executeQuery(query);	

			return getProductList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 차 상품 전체 목록 혹은 입력되는 검색 키로 차 상품을 채취한다.
	 * @param scanner
	 * @return 차 상품 목록
	 */
	static private ArrayList<TraditionalTea> getTeaProducts(Scanner scanner) {
		
		// 검색을 원하는지 묻고
		if (DaBang.getUserResponse("차를 검색하시겠습니까?", scanner)) {
			String[] teaKeys = getTeaSearchKeys(scanner);
			if (teaKeys.length > 0 &&
					teaKeys[0].length() > 1) {
				return getTeaProductList(teaKeys);
			} else {
				return getAllProducts();
			}
		} else {
			return getAllProducts();
		}
	}

	static private void processTeaPurchase(Scanner scanner, CustomerInfo 
			customer, TeaType type) throws StopSearchingException {
		
		try {
			int teaCount = Utility.getIntegerValue(scanner,
					"몇 잔을 원하십니까? : ", "구매 수량");
			String tea = type.name();
			int idx = tea.length() - 1;
			int cp = tea.codePointAt(idx);
			String msg = customer.get고객ID() + " 고객님의 '" 
				+ tea + (SuffixChecker.has받침(cp, 
					tea.substring(idx)) ? "'을 " : "'를 ")
				+ teaCount + "잔 준비합니다.";
			/**
			 * 고객 주소 입력
			 */
			DeliverAddress 배송주소 = 주소관리.get배송주소(scanner, customer.get고객SN());
			DateTimeFormatter dtf 
			= DateTimeFormatter.ofPattern("HH:mm");
			String timeLabel = LocalTime.now().format(dtf);
			DaBang.getLogger().info(msg + ", 주문 시각: " + timeLabel);
			
			save상품주문(tea, teaCount, customer.get고객SN(), 배송주소);
			System.out.println(msg);
		} catch (NoInputException e) {
			System.out.println(e.getMessage() +
					" 입력을 원하지 않습니다.");
		}
	}

	private static Connection conn = null;	

	static private void save상품주문(String tea, int teaCount, 
			int 고객SN, DeliverAddress 배송주소) {
		String iSql = "insert into 상품주문"
				+ "(상품id, 고객SN, 주문수량, 단지번호, 상세주소) "
				+ "values (?,?,?,?,?)";
		Logger logger = DaBang.getLogger();
		try {
			var iPs = conn.prepareStatement(iSql);
			int 상품id = get상품IDfromDB(tea);
			
			iPs.setInt(1, 상품id);
			iPs.setInt(2, 고객SN);
			iPs.setInt(3, teaCount);
			iPs.setInt(4, 배송주소.단지번호);
			iPs.setString(5, 배송주소.상세주소);
			
			int inserted = iPs.executeUpdate();
			logger.config("주문 DB 저장 건수: " + inserted);
			logger.config(tea + " 구매, 고객SN: " + 고객SN 
					+ ", " + teaCount);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
	}
	
	static private int get상품IDfromDB(String tea) {
		String sql = "select 상품ID from 전통차 "
				+ "where 전통차.차이름 = ?";
		try {
			var ps = conn.prepareStatement(sql);
			ps.setString(1, tea);
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
}
