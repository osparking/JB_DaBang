package com.jbpark.dabang.store;

import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.module.AddressMan;
import com.jbpark.dabang.module.CustomerAddress;
import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.module.SearchResult;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;
import com.jbpark.dabang.utility.TeaType;
import com.jbpark.utility.CustomerInfo;
import com.jbpark.utility.JLogger;
import com.jbpark.utility.SecureMan;

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
	
	private static Connection conn = null;
	static {
		conn = AddressMan.getConnection();
		if (conn != null)
			logger.info("Connection is successful");
	};

	public static void main(String[] args) {
		var jbDabang = new DaBang();
		
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				try {
					jbDabang.serveOneCustomer(scanner);
					for (int i = 0; i<3; i++) {
						System.out.println(".");
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
					}
				} catch (StopSearchingException e) {
					logger.info(e.getMessage());
				}
			}
		} finally {
			logger.removeHandler(null);
		}
	}

	private void serveOneCustomer(Scanner scanner) 
			throws StopSearchingException {
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
				int 고객SN = 0;
				
				while (true) {
					// 고객 가입 옵션 제시
					optional고객등록(scanner);
					try {
						if (loginSucceeded(scanner))
							break;
					} catch (NoSuch고객Exception e) {
						System.out.println(e.getMessage());
						logger.warning(e.getMessage());
					}
				}
				
				String tea = type.name();
				int idx = tea.length() - 1;
				int cp = tea.codePointAt(idx);
				String msg = 고객SN + " 고객님의 '" 
						+ tea
						+ (SuffixChecker.has받침(cp, 
							tea.substring(idx)) ? "'을 " : "'를 ") 
					+ teaCount + "잔 준비할께요.";
				/**
				 * 고객 주소 입력
				 */
				DeliverAddress 배송주소 = get배송주소(scanner, 고객SN);
				DateTimeFormatter dtf 
				= DateTimeFormatter.ofPattern("HH:mm");
				String timeLabel = LocalTime.now().format(dtf);
				logger.info(msg + ", 주문 시각: " + timeLabel);
				
				storeIntoMariaDb(tea, teaCount, 고객SN, 배송주소);
				System.out.println(msg);
			} catch (NoInputException e) {
				System.out.println(e.getMessage() +
						" 입력을 원하지 않습니다.");
			}
		}
		Toolkit.getDefaultToolkit().beep();
	}

	private boolean loginSucceeded(Scanner scanner) 
			throws NoSuch고객Exception {
		System.out.println("다름 로그인 정보를 입력하세요.");	
		String 고객Id = Utility.get고객ID(scanner, "\t고객ID : ");
		System.out.print("\t비밀번호: ");
		if (scanner.hasNext()) {
			String password = scanner.nextLine().trim();
			var customer = SecureMan.read전통고객(고객Id);
			
			if (customer != null) {
				boolean goodPwd = SecureMan.passwordVerified
						(password, customer);
				if (goodPwd) {
					int 고객SN = get고객SN(고객Id);
					return true;
				}
			}
			String msg = "고객ID 혹은 비밀번호 오류입니다.";
			throw new NoSuch고객Exception(msg);
		}
		return false;
	}

	private void optional고객등록(Scanner scanner) {
		if (getUserResponse("계정이 없으십니까?", scanner)) {
			String 고객Id = "";
			String preFix = "사용할";
			while (true) {
				고객Id = Utility.get고객ID(scanner, 
						preFix + " 'ID'를 입력하세요 : ");
				try {
					get고객SN(고객Id);
					System.out.println("'" + 고객Id 
							+ "'는 사용하실 수 없습니다.");
					preFix = "다른";
				} catch (NoSuch고객Exception e) {
					System.out.print("'" + 고객Id + "'는 사용가능합니다.");
					break;
				}
			}
			String password = Utility.getPassword(scanner);
			byte[] salt = SecureMan.getSalt(); 
			byte[] pwdEncd = SecureMan.encryptPassword(password, salt);
			SecureMan.save전통고객(고객Id, salt, pwdEncd);
		}
	}

	private int get고객SN(String 고객Id) throws NoSuch고객Exception {
		String getSNsql = "select 고객SN "
				+ "from 전통고객 "
				+ "where 고객ID = '" + 고객Id +"'";
		try {
			Statement getStmt = conn.createStatement();
			ResultSet rs = getStmt.executeQuery(getSNsql);

			if (rs.next()) {
				return rs.getInt(1);
			} else {
				String msg = "아이디 '" + 고객Id + "'인 고객은 없습니다.";
				throw new NoSuch고객Exception(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
		return 0;
	}

	/**
	 * 고객 주소 선택 받아 상세 주소 입력 및 DB 저장
	 * @param scanner
	 * @param 고객SN
	 * @throws NoInputException
	 * @throws StopSearchingException
	 */
	private DeliverAddress get배송주소(Scanner scanner, int 고객SN)
			throws  NoInputException, StopSearchingException {
		AddressMan aMan = new AddressMan();
		
		// 고객 과거 주소 목록 표시
		var addresses = aMan.displayCustomerAddresses(고객SN, logger);
		
		// 새 주소 입력 혹은 과거 주소 활용
		if (addresses.size() > 0) {
			boolean resp = getUserResponse(
					"과거 주소 중에서 선택하겠습니까?", scanner);
			if (resp)
				return useOldAddress(addresses, scanner, aMan, 고객SN);
		}  
		return acquireNewAddress(scanner, aMan, 고객SN);
	}

	private DeliverAddress useOldAddress(
			ArrayList<CustomerAddress> addresses, 
			Scanner scanner, AddressMan aMan, int 고객SN) {
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
		
		boolean use그대로 = getUserResponse(
				"선택한 위 주소를 그대로 사용하겠습니까?", scanner);

		// 새 세부주소 요구
		DeliverAddress deliAddr = new DeliverAddress(
				addr.get단지번호(), addr.get상세주소());
		
		if (!use그대로) {
			System.out.println("사용할 '세부주소'를 입력하세요.");
			System.out.print("세부주소: ");
			if (scanner.hasNextLine()) {
				String 상세주소 = scanner.nextLine().trim();
				deliAddr.set상세주소(상세주소);
				save고객주소(고객SN, addr.get단지번호(), 상세주소);
			}
		}
		return deliAddr;
	}

	private DeliverAddress acquireNewAddress(Scanner scanner, 
			AddressMan aMan, int 고객SN)	
					throws StopSearchingException, 
							NoInputException {
		// 새 주소 입력
		System.out.println("배송지 주소를 입력하세요.");
		SearchResult searchResult = aMan.search(scanner);
		RoadAddress[] addresses = searchResult.getAddresses();
		for (RoadAddress ra : addresses) {
			if (ra != null) logger.config(ra.toString());
		}
		showResult(searchResult);
		int idx = Utility.getIntegerValue(scanner, 
				"도로명 주소 번호를 입력하세요.", 
				"주소 번호(1~" + searchResult.getAddrCount() + ")",
				true);
		System.out.println("선택한 주소: " + addresses[idx - 1]);
		System.out.println("상세주소를 입력하세요.");
		System.out.print("상세주소: ");
		
		String 상세주소 = "";
		
		if (scanner.hasNextLine()) {
			상세주소 = scanner.nextLine();
			System.out.println("입력한 상세주소: " + 상세주소);
		}
		int 단지번호 = save단지번호_주소(고객SN, 상세주소, 
				addresses[idx - 1]);
		return new DeliverAddress(단지번호, 상세주소);
	}
	
	private class DeliverAddress {
		int 단지번호;
		String 상세주소;
		/**
		 * @param 단지번호
		 * @param 상세주소
		 */
		public DeliverAddress(int 단지번호, String 상세주소) {
			this.단지번호 = 단지번호;
			this.상세주소 = 상세주소;
		}
		public void set상세주소(String 상세주소) {
			this.상세주소 = 상세주소;
		}
	}
	
	private int save단지번호_주소(int 고객SN, 
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

	private int save고객주소(int 고객SN, int 단지번호, 
			String detailedAddr) {
		String iSql = String.format("insert into "
				+ "고객주소(고객SN, 단지번호, 상세주소) "
				+ "values (%s, %s, '%s');",
				고객SN, 단지번호, detailedAddr);
		
		try (var stmt = conn.createStatement()){
			return stmt.executeUpdate(iSql);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}		
		return 0;	
	}

	private int save단지주소(RoadAddress address) {
		String iSql = String.format("insert into 단지주소"
				+ "(관리번호, 도로명주소) values ('%s', '%s');",
				address.getMgmtNumber(), 
				address.getRoadName());
		ResultSet rs = null;
		
		try (var stmt = conn.createStatement()){
			stmt.executeUpdate(iSql, 
					Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}		
		return 0;
	}

	private int get단지주소번호(String mgmtNumber) {
		String sql = "select c.단지번호 "
				+ "from 단지주소 c "
				+ "where c.관리번호 = ?";
		try {
			var ps = conn.prepareStatement(sql);
			ps.setString(1, mgmtNumber);
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

	private void showResult(SearchResult searchResult) {
		String msg = "표시 행: " + searchResult.getAddrCount() +
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

	private void storeIntoMariaDb(String tea, int teaCount, 
			int 고객SN, DeliverAddress 배송주소) {
		//formatter:off
		String iSql = "insert into 상품주문"
				+ "(상품id, 고객SN, 주문수량, 단지번호, 상세주소) "
				+ "values (?,?,?,?,?)";
		//formatter:on
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
