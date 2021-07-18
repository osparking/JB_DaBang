package com.jbpark.dabang.store;

import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.module.AddressMan;
import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.module.SearchResult;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;
import com.jbpark.dabang.utility.TeaType;
import com.jbpark.utility.JB_DabangDB;
import com.jbpark.utility.JLogger;

import jbpark.utility.SuffixChecker;

//@formatter:off
/**
 * 이 프로그램은 한국 <strong>전통차</strong> 온라인 쇼핑몰을 구현한다. 
 * 이 프로그램의 깃허브 저장소는 다음과 같다.
 * 
 * @see <a href="https://github.com/osparking/JB_DaBang">
 * 	JB_DaBang 깃허브 저장소</a>
 * @see <a href="https://github.com/osparking/JB_module">
 * 	JB_module 깃허브 저장소</a>
 * @see <a href=
 *      "https://github.com/osparking/JB_module/blob/main/src/com/jbpark/dabang/module/TeaType.java">차
 *      종류</a>
 * @author 박종범(Park, JongBum)
 * @version 1.0.0
 *
 */
public class DaBang {
	private static Logger logger = JLogger.getLogger();
	
	private static Connection conn = JB_DabangDB.getConnection();

	public static void main(String[] args) {
		var jbDabang = new DaBang();
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				jbDabang.serveOneCustomer(scanner);
				for (int i = 0; i<3; i++) {
					System.out.println(".");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
				}
			}
		} catch (StopSearchingException e) {
			logger.info(e.getMessage());
		} finally {
			logger.removeHandler(null);
		}
	}

	private void serveOneCustomer(Scanner scanner) throws StopSearchingException {
		System.out.println("다음 손님 어서오세요...");
		System.out.println("J.B.차방이 당신을 환영합니다");

		TeaType type = null;  
		
		do {
			showTeaSelection();
			try {
				type = getTeaSelection(scanner);
			} catch (TeaInputException te) {
				String msg = "'는 잘못된 입력입니다. 다시 선택해 주세요.";
				System.out.println("'" + te.getMessage() + msg);
				continue;
			}
			if (type == null) {
				if (getUserResponse("주문을 원치 않으십니까",
						scanner))
					break;
			} else 
				break;
		} while (true);
		
		if (type == null)
			System.out.println("안녕히 가십시오.");
		else {
			try {
				int teaCount = Utility.getIntegerValue(scanner,
						"몇 잔을 원하십니까? : ", "구매 수량");
				int 고객ID = Utility.getIntegerValue(scanner,
						"고객님 ID는 무엇입니까? : ", "고객ID");
				
				String tea = type.name();
				int idx = tea.length() - 1;
				int cp = tea.codePointAt(idx);
				String msg = 고객ID + "번 고객님의 '" 
						+ tea
						+ (SuffixChecker.has받침(cp, 
							tea.substring(idx)) ? "'을 " : "'를 ") 
					+ teaCount + "잔 준비할께요.";
				/**
				 * 고객 주소 입력
				 */
				acquireCustomerAddress(scanner, 고객ID);
				DateTimeFormatter dtf 
				= DateTimeFormatter.ofPattern("HH:mm");
				String timeLabel = LocalTime.now().format(dtf);
				logger.info(msg + ", 주문 시각: " + timeLabel);
				
				storeIntoMariaDb(tea, teaCount, 고객ID);
				System.out.println(msg);
			} catch (NoInputException e) {
				System.out.println(e.getMessage() +
						" 입력을 원하지 않습니다.");
			}
			
		}
		Toolkit.getDefaultToolkit().beep();
	}

	private void acquireCustomerAddress(Scanner scanner, int 고객id)
			throws  NoInputException, StopSearchingException {
		AddressMan aMan = new AddressMan();
		
		try {
			SearchResult searchResult = aMan.search(scanner);
			for (RoadAddress ra : searchResult.getAddresses()) {
				if (ra != null) logger.config(ra.toString());
			}
			showResult(searchResult);
			int selection = Utility.getIntegerValue(scanner, 
					"도로명 주소 번호를 입력하세요.", 
					"주소 번호(1~" + searchResult.getAddressCount(),
					true);
			System.out.println("선택한 주소: " +
					searchResult.getAddresses()[selection - 1]);
			System.out.println("상세주소를 입력하세요.");
			System.out.print("상세주소: ");
			if (scanner.hasNextLine()) {
				String detailedAddr = scanner.nextLine();
				System.out.println("입력한 상세주소: " + detailedAddr);
			}
		} catch(NoInputException e) {
			throw new NoInputException("고객 주소");
		}
		
	}

	private void showResult(SearchResult searchResult) {
		String msg = "표시 행: " + searchResult.getAddressCount() +
					 ", 전체 행: " + searchResult.getTotalRow();
		
		logger.config(msg);
		System.out.println(msg);
		
		RoadAddress[] addresses = searchResult.getAddresses();
		for (int i = 0; i < addresses.length; i++) {
			if (addresses[i] == null) {
				searchResult.setAddressCount(i);
				break;
			}
			System.out.println("\t" + (i + 1) + addresses[i]);
		}			
	}

	private void storeIntoMariaDb(String tea, 
			int teaCount, int 고객id) {
		String iSql = "insert into 상품주문"
				+ "(상품id, 고객id, 주문수량) values (?,?,?)";
		try {
			var iPs = conn.prepareStatement(iSql);
			int 상품id = get상품IDfromDB(tea);
			
			iPs.setInt(1, 상품id);
			iPs.setInt(2, 고객id);
			iPs.setInt(3, teaCount);
			
			int inserted = iPs.executeUpdate();
			logger.config("주문 DB 저장 건수: " + inserted);
			logger.config(tea + " 구매, 고객ID: " + 고객id 
					+ ", " + teaCount);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
	}

	private int get상품IDfromDB(String tea) {
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
			logger.severe(e.getMessage());
		}
		return 0;
	}

	private int geIntegerValue(Scanner scanner,
			String qLong, String qNoun) {
		int count = 1;
		System.out.println(qLong);
		while (true) {
			String line = null;
			System.out.print(qNoun + " : ");
			try {
				if (scanner.hasNextLine()) {
					line = scanner.nextLine();
					count = Integer.parseInt(line.trim());
					break;
				}
			} catch(NumberFormatException e) {
				System.out.println("입력된 " + qNoun + " '" 
						+ line.trim() + "'은 부적절합니다.");
				System.out.println("다시 입력하십시오...");
			}
		}
		return count;
	}

	/**
	 * 고객으로부터 선택하는 차 종류 텍스트를 입력받는다.
	 * 
	 * @param scanner 고객 입력 차 종류 텍스트 스캐너
	 * @return 선택된 차 종류 값 혹은 null(선택 불원의 경우)
	 * @throws TeaInputException 입력 오류의 경우 발생됨
	 */
	private TeaType getTeaSelection(Scanner scanner) 
			throws TeaInputException {
		String selection = 입력접수(scanner);

		if (selection.isEmpty()) {
			return null;
		}
		for (var type : TeaType.values()) {
			if (type.get단축명().equals(selection) || 
					type.name().indexOf(selection) >= 0) {
				String teaLong = type.toString();
				int idx = teaLong.indexOf('(');
				int cp = teaLong.codePointAt(--idx);
				String sfx = SuffixChecker.has받침(
						cp, teaLong.substring(idx))
						? "을" : "를";
				String msg = type + sfx + " 선택하셨습니까";
				boolean resp = getUserResponse(msg, scanner);
				
				if (resp)
					return type;
				else
					return null;
			}
		}
		throw new TeaInputException(selection);
	}

	private String 입력접수(Scanner scanner) {
		String 고객입력 = "";
		try {
			if (scanner.hasNextLine()) {
				고객입력 = scanner.nextLine();
				고객입력 = 고객입력.trim();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return 고객입력;
	}

	/**
	 * 고객의 의사를 확인한다. 고객 입력 문자가 'Y', 'y', 'N', 'n' 문자 
	 * 혹은 [엔터] 키가 아니면 재 입력을 요구한다.
	 * 
	 * @param type    고객이 선택한 차 종류
	 * @param scanner 고객 입력 접수용 참조
	 * @return 참: 'Y', 'y', [엔터]키 일 때, 
	 * 		   거짓: 'N', 'n'일 때.
	 */
	private boolean getUserResponse(String question, 
			Scanner scanner) {
		String input;
		boolean validInput = true;
		
		do {
			if (!validInput) {
				Toolkit.getDefaultToolkit().beep();
				System.out.println("입력 오류입니다. 다시 입력해 주세요");
			}
			System.out.println(question + "?");
			System.out.print("Y/y/[엔터]=예; N/n=아니오: ");
			input = 입력접수(scanner);
			if (input != null) {
				input = input.trim().toLowerCase();
			}
			validInput = "y".equals(input) 
					|| "n".equals(input)
					|| (input != null && input.isEmpty());
		} while (!validInput);

		if (input != null) {
			input = input.trim().toLowerCase();
			if (input.length() == 0 
					|| input.equals("y"))
				return true;
		}
		assert "n".equals(input)
				: "'부정' 의사 표시로 부적절한 문자 입력!";
		return false;
	}

	private void showTeaSelection() {
		LocalDate today = LocalDate.now();
		String weekDay = today.format(DateTimeFormatter
				.ofPattern("E").withLocale(Locale.KOREAN)); //E:요일
		
		System.out.println("=".repeat(40));
		System.out.println("다음 차 종류 중에서 선택하세요:");
		System.out.println("=".repeat(40));
		System.out.println(" * : '" + weekDay + "'요일 특별 차!");

		TeaType[] teaTypes = TeaType.values();
		long specialInx = ChronoUnit.DAYS.between(
				LocalDate.of(2021, 6, 22), today) 
				% teaTypes.length;
		int teaCount = teaTypes.length;
		
		for (int i = 0; i < teaCount; i++) {
			var teaMenu = new StringBuffer(" -");
			teaMenu.append(teaTypes[i]);
			if (i == specialInx)
				teaMenu.append("*");
			System.out.println(teaMenu);
		}
		System.out.println(" -메뉴 선택 안함([엔터])");
		System.out.println("=".repeat(40));
		System.out.print("단축명(ㄱ-ㅎ), 이름(일부/전부): ");
	}
}
