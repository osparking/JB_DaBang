package com.jbpark.dabang.store;

import java.awt.Toolkit;
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
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.module.AddrSearchKey;
import com.jbpark.dabang.module.AddressMan;
import com.jbpark.dabang.module.CustomerAddress;
import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.RoadAddress;
import com.jbpark.dabang.module.SearchResult;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;
import com.jbpark.dabang.utility.TeaType;
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
	private static boolean DEBUG_MODE; 
	
	private static Connection conn = null;
	static {
		conn = AddressMan.getConnection();
		if (conn != null)
			logger.info("Connection is successful");
	};

	private void checkArgs(String[] args) {
	    for (int i = 0; i < args.length; i++) {
	        switch (args[i].charAt(0)) {
	        case '-':
	            if (args[i].length() < 2)
	                throw new IllegalArgumentException("바른 인자 아님: " + args[i]);
	            if (args[i].charAt(1) == '-') {
	            	// --opt
	                if (args[i].length() < 3)
	                    throw new IllegalArgumentException("바른 인자 아님: " + args[i]);
	            } else {
	                // -opt
	                if (args[i].equals("-D"))
	                	DEBUG_MODE = true;
	                i++;
	            }
	            break;
	        default:
	            break;
	        }
	    }		
	}
	
	Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		var jbDabang = new DaBang();
		jbDabang.checkArgs(args);
		
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
		
		CustomerInfo customer = getUserCredential(scanner);

		while (true) {
			var sb = new StringBuilder();
			
			sb.append("\n수행할 수 있는 작업 목록 - ");
			sb.append("\n\t1. 자기 주소 관리");
			sb.append("\n\t2. 차 검색 및 주문");
			sb.append("\n\t3. 로그 아웃");
			
			int option = Utility.getIntegerValue(scanner,
					sb.toString(), "수행할 작업 번호? (1~3)");
			
			switch(option) {
			case 1: 
				// 고객이 자기가 과거 입력한 주소 목록을 보고 관리한다.
				manageOwnAddress(customer);				
				break;
			case 2: 
				TeaType type = getTeaSelection(scanner);  
				if (type != null){
					processTeaPurchase(scanner, customer, type);
				}
				break;
			case 3: 
				sb = new StringBuilder("\t");
				sb.append(customer.get고객ID());
				sb.append("님 안녕히 가십시오.");
				System.out.println(sb);
				customer = null;
				Toolkit.getDefaultToolkit().beep();
				return;
			default:
				System.out.println("부적절한 옵션 번호: " + option);
				break;
			}
		}
	}
	
	public void callManageOwnAddress(CustomerInfo customer) {
		manageOwnAddress(customer);
	}
	
	private void manageOwnAddress(CustomerInfo customer) {
		// 건수 채취
		int 고객sn = customer.get고객SN();
		int page = getShowPageNumber(고객sn);
		var addresses = AddressMan.getCustomerAddresses(고객sn, page);
		
		System.out.println("채취된 페이지: " + page);
		AddressMan.showCustomerAddresses(logger, addresses);
		
		while (true) {
			int size = addresses.size();
			String options = getManageOptions(size);
			
			int count = (size > 0 ? 5 : 2);

			var sb = new StringBuilder("수행할 작업 번호? (1~");
			sb.append(count);
			sb.append(")");
			
			int intOpt = Utility.getIntegerValue(scanner,
					options, sb.toString());
			var option = AddressOption.getOption(size, intOpt);
			switch(option) {
			case LISTING:
				break;
				
			case REGISTER:
				break;
				
			case UPDATE:
				AddressMan.updateAddress(addresses, scanner);
				break;
				
			case DELETE:
				AddressMan.deleteAddress(addresses, scanner);
				break;
				
			case FINISH:
				return;
				
			default:
				System.out.println("부적절한 옵션: " + intOpt);
				break;
			}
		}		
	}
	
	/**
	 * 고객에 할 수 있는 주소 관리 옵션 문자열 생성 
	 * @param size 고객 주소 목록 크기
	 * @return 생성된 옵션 문자열
	 */
	private String getManageOptions(int size) {
		var sb = new StringBuilder();
		int idx = 1;
		
		sb.append("\n작업: ");

		if (size > 0) {
			sb.append("\n\t");
			sb.append(idx++);
			sb.append(".목록");
		}
		
		sb.append(", ");
		sb.append(idx++);
		sb.append(".등록");
		
		if (size > 0) {
			sb.append(", ");
			sb.append(idx++);
			sb.append(".수정");
			
			sb.append(", ");
			sb.append(idx++);
			sb.append(".삭제");
		}
		
		sb.append(", ");
		sb.append(idx++);
		sb.append(".종료");
		return sb.toString();
	}

	private void processTeaPurchase(Scanner scanner, CustomerInfo 
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
			DeliverAddress 배송주소 = get배송주소(scanner, customer.get고객SN());
			DateTimeFormatter dtf 
			= DateTimeFormatter.ofPattern("HH:mm");
			String timeLabel = LocalTime.now().format(dtf);
			logger.info(msg + ", 주문 시각: " + timeLabel);
			
			save상품주문(tea, teaCount, customer.get고객SN(), 배송주소);
			System.out.println(msg);
		} catch (NoInputException e) {
			System.out.println(e.getMessage() +
					" 입력을 원하지 않습니다.");
		}
	}

	private TeaType getTeaSelection(Scanner scanner) {
		TeaType type = null;
		
		do {
			String weekDay = LocalDate.now().format(DateTimeFormatter
					.ofPattern("E").withLocale(Locale.KOREAN)); //E:요일
			
			System.out.println("=".repeat(40));
			System.out.println("다음 차 종류 중에서 선택하세요:");
			System.out.println("=".repeat(40));
			System.out.println(" * : '" + weekDay + "'요일 특별 차!");			
			
			var teaList = getTeaProducts(scanner);
			int rowCount = showTeaSelection(teaList);
			
			try {
				int tNum = Utility.getIntegerValue(scanner,
						"어떤 차를 원하십니까? : ", 
						"번호(1-" + rowCount + "): ");	
				type = confirmSelection(scanner, tNum, 
						teaList.get(tNum-1).get차종류());
			} catch (TeaInputException e) {
				String msg = e.getMessage() + "를 취소하셨으니, 다시 선택해 주세요.";
				System.out.println("'" + e.getMessage() + msg);
				continue;
			}
			if (type == null) {
				if (getUserResponse("주문을 원치 않으십니까", scanner))
					break;
			} else 
				break;
		} while (true);
		
		return type;
	}

	private CustomerInfo getUserCredential(Scanner scanner) {
		while (true) {
			// 고객 가입 옵션 제시
			optional고객등록(scanner);
			try {
				CustomerInfo customer = getCustomerInfo(scanner);
				
				if (customer != null) {
					System.out.println("J.B.차방이 " +
							customer.get고객ID() + 
							"님을 환영합니다");
					return customer;
				}
			} catch (NoSuch고객Exception e) {
				System.out.println(e.getMessage());
				logger.warning(e.getMessage());
			}
		}
	}

	private int getTodaySpecial(int size) {
		return (int)ChronoUnit.DAYS.between(
				LocalDate.of(2021, 6, 22), LocalDate.now()) 
				% size;
	}
	
	private LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
	    return Instant.ofEpochMilli(dateToConvert.getTime())
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
	private ArrayList<TraditionalTea> getTeaProducts(Scanner scanner) {
		
		// 검색을 원하는지 묻고
		if (getUserResponse("차를 검색하시겠습니까?", scanner)) {
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

	private ArrayList<TraditionalTea> getAllProducts() {
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

	private ArrayList<TraditionalTea> getProductList(ResultSet rs) 
			throws SQLException {
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

	public ArrayList<TraditionalTea> callGetTeaProductList(
			String[] teaKeys) {
		return getTeaProductList(teaKeys);
	}
	
	private ArrayList<TraditionalTea> getTeaProductList(String[] 
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
	private ArrayList<TraditionalTea> unionAndWithOr(String[] teaKeys) {
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

	private ArrayList<TraditionalTea> fullTextSearch(String[] teaKeys) {
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

	private ArrayList<TraditionalTea> searchProducts(String key) {
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

	/**
	 * 두 자 이상의 단어를 최대 3개 수령하여 반환한다.
	 * @param scanner 
	 * @return
	 */
	private String[] getTeaSearchKeys(Scanner scanner) {
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

	public static CustomerInfo read전통고객(String 고객ID) {
		String getCustInfo = "select 고객SN, 고객이름, salt, password"
				+ " from 전통고객 where 고객ID = '" + 고객ID + "'";
		try {
			Statement getStmt = conn.createStatement();
			ResultSet rs = getStmt.executeQuery(getCustInfo);

			if (rs.next()) {
				var customer = new CustomerInfo();
				customer.set고객ID(고객ID);
				
				customer.set고객SN(rs.getInt(1));
				customer.set고객이름(rs.getString(2));
				customer.setSalt(rs.getBytes(3));;
				customer.setPassword(rs.getBytes(4));
				
				return customer;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
		return null;		
	}	

	private CustomerInfo getCustomerInfo(Scanner scanner) 
			throws NoSuch고객Exception {
		System.out.println("로그인 정보를 입력하세요.");	
		String 고객Id = Utility.get고객ID(scanner, "\t고객ID : ",
				DEBUG_MODE);
		System.out.print("\t비밀번호: ");
		
		if (DEBUG_MODE) {
			var customer = read전통고객(고객Id);
			System.out.println("'" + 고객Id 
						+ "'님 로그인되었습니다.");
			return customer;
		}		
		
		if (scanner.hasNext()) {
			String password = scanner.nextLine().trim();
			var customer = read전통고객(고객Id);
			
			if (customer != null) {
				boolean goodPwd = SecureMan.passwordVerified
						(password, customer.getSalt(), customer.getPassword());
				if (goodPwd) {
					System.out.println("'" + 고객Id 
							+ "'님 로그인되었습니다.");
					return customer;
				}
			}
			String msg = "고객ID 혹은 비밀번호 오류입니다.";
			throw new NoSuch고객Exception(msg);
		} 
		return null;
	}

	private void optional고객등록(Scanner scanner) {
		if (getUserResponse("계정이 없으십니까?", scanner)) {
			String 고객Id = "";
			String preFix = "사용할";
			while (true) {
				try {
					고객Id = Utility.get고객ID(scanner, 
							preFix + " 'ID'를 입력하세요 : ", false);
					try {
						get고객SN(고객Id);
						System.out.println("'" + 고객Id 
								+ "'는 사용하실 수 없습니다.");
						preFix = "다른";
					} catch (NoSuch고객Exception e) {
						System.out.print("'" + 고객Id + "'는 사용가능합니다.");
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			String password = Utility.getPassword(scanner);
			byte[] salt = SecureMan.getSalt(); 
			byte[] pwdEncd = SecureMan.encryptPassword(password, salt);
			save전통고객(고객Id, salt, pwdEncd);
		}
	}
	
	public static int save전통고객(String 고객Id, byte[] salt, byte[] pwdEncd) {
		String iSql = "insert into 전통고객"
				+ "(고객ID, 고객이름, salt, password) "
				+ "values (?, ?, ?, ?);";
		try {
			var iPs = conn.prepareStatement(iSql);
			
			iPs.setString(1, 고객Id);
			iPs.setString(2, "아무개");
			iPs.setBytes(3, salt);
			iPs.setBytes(4, pwdEncd);
			
			int inserted = iPs.executeUpdate();
			logger.config("생성된 고객ID: " + 고객Id);
			return inserted;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}	
		return 0;
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
	 * @param 고객sn
	 * @throws NoInputException
	 * @throws StopSearchingException
	 */
	private DeliverAddress get배송주소(Scanner scanner, int 고객sn)
			throws  NoInputException, StopSearchingException {
		AddressMan aMan = new AddressMan();
		int page = getShowPageNumber(고객sn);
		var addresses = AddressMan.getCustomerAddresses(고객sn, page);
		
		AddressMan.showCustomerAddresses(logger, addresses);		
		
		// 새 주소 입력 혹은 과거 주소 활용
		if (addresses.size() > 0) {
			boolean resp = getUserResponse(
					"과거 주소 중에서 선택하겠습니까?", scanner);
			if (resp)
				return useOldAddress(addresses, scanner, aMan, 고객sn);
		}  
		return acquireNewAddress(scanner, aMan, 고객sn);
	}	

	private int getShowPageNumber(int 고객sn) {
		int rows = AddressMan.getCustAddrRows(고객sn);
		int page = 1;
		if (rows > 20) {
			// 원하는 페이지 번호 입력 요구
			page = AddressMan.getWantedPage(scanner, rows);		
		}
		return page;
	}

	private DeliverAddress useOldAddress(
			List<CustomerAddress> addresses, 
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
		
		AddrSearchKey key = aMan.getAddrSearchKey(scanner);
		Integer pageNo;
		
		// 입력 가능한 페이지 번호 범위 표시
		int rows = aMan.getTotalRows(key);
		pageNo = AddressMan.getWantedPage(scanner, rows);		
		
		SearchResult searchResult = aMan.searchAddress(key, pageNo);
		searchResult.setTotalRow(rows);
		
		RoadAddress[] addresses = searchResult.getAddresses();
		
		for (RoadAddress ra : addresses) {
			if (ra != null) logger.config(ra.toString());
		}
		showResult(searchResult);
		
		int idx = Utility.getIntegerValue(scanner, 
				"도로명 주소 번호를 입력하세요.", 
				"주소 번호(1~" + searchResult.getAddrCount() + ")",
				true);
		String detailAddr = AddressMan.getDetailAddr("선택한 주소", 
				addresses[idx - 1], scanner);
		int 단지번호 = save단지번호_주소(고객SN, detailAddr, 
				addresses[idx - 1]);
		
		return new DeliverAddress(단지번호, detailAddr);
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
	
	public int save단지주소Test(RoadAddress address) {
		return save단지주소(address);
	}

	private int save단지주소(RoadAddress address) {
		String iSql = String.format("insert into 단지주소"
				+ " (관리번호, 우편번호, 도로명주소) "
				+ "values ('%s', %s, '%s');",
				address.getMgmtNumber(), 
				address.getNewZipcode(), 
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

	private void save상품주문(String tea, int teaCount, 
			int 고객SN, DeliverAddress 배송주소) {
		String iSql = "insert into 상품주문"
				+ "(상품id, 고객SN, 주문수량, 단지번호, 상세주소) "
				+ "values (?,?,?,?,?)";

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
	 * @param tea번호 
	 * @param teaType 
	 * @return 선택된 차 종류 값 혹은 null(선택 불원의 경우)
	 * @throws TeaInputException 입력 오류의 경우 발생됨
	 */
	private TeaType confirmSelection(Scanner scanner, 
						int tea번호, TeaType type) 
			throws TeaInputException {
		String teaLong = type.toString();
		int idx = teaLong.indexOf('(');
		int cp = teaLong.codePointAt(--idx);
		String sfx = SuffixChecker.has받침(
				cp, teaLong.substring(idx))
				? "을" : "를";
		String msg = tea번호 + "번 " + type + sfx + " 선택하셨습니까";
		
		if (getUserResponse(msg, scanner))
			return type;
		else
			throw new TeaInputException(type.toString());
	}

	private String 입력접수(Scanner scanner) {
		String 고객입력 = "";
		try {
			if (scanner.hasNextLine()) {
				고객입력 = scanner.nextLine();
				고객입력 = 고객입력.trim();
			} else {
				System.out.println("\n프로그램 강제 종료!");
				System.exit(0);
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
			System.out.print("y,기본)예, n) 아니오: ");
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

	private int showTeaSelection(ArrayList<TraditionalTea> teaList) {
		int todayIndex= getTodaySpecial(teaList.size());
		for (int i = 0; i < teaList.size(); i++) {
			var teaMenu = new StringBuilder(" " + (i+1) +".");
			TeaType currTea = teaList.get(i).get차종류(); 
			teaMenu.append(currTea);
			if (i == todayIndex)
				teaMenu.append("*");
			teaMenu.append(" - " + teaList.get(i).get설명());
			System.out.println(teaMenu);
		}
		
		System.out.println(" -메뉴 선택 안함([엔터])");
		System.out.println("=".repeat(40));
		return teaList.size();		
	}
}
