package com.jbpark.dabang.store;

import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Logger;

import com.jbpark.dabang.module.AddressMan;
import com.jbpark.dabang.module.DBCPDataSource;
import com.jbpark.dabang.module.NoInputException;
import com.jbpark.dabang.module.NoSuch고객Exception;
import com.jbpark.dabang.module.StopSearchingException;
import com.jbpark.dabang.module.Utility;
import com.jbpark.dabang.module.고객계정;
import com.jbpark.utility.JLogger;
import com.jbpark.utility.SecureMan;

//@formatter:off
/**
 * 이 프로그램은 한국 <strong>전통차</strong> 온라인 쇼핑몰을 구현한다. 이 프로그램의 깃허브 저장소는 다음과 같다.
 * 
 * @see <a href="https://github.com/osparking/JB_DaBang"> JB_DaBang 깃허브 저장소</a>
 * @see <a href="https://github.com/osparking/JB_module"> JB_module 깃허브 저장소</a>
 * @see <a href=
 *      "https://github.com/osparking/JB_module/blob/main/src/com/jbpark/dabang/module/TeaType.java">차
 *      종류</a>
 * @author 박종범(Park, JongBum)
 * @version 1.0.0
 *
 */
public class DaBang {
	static Logger logger = JLogger.getLogger();
	private static boolean DEBUG_MODE;

	public static Logger getLogger() {
		return logger;
	}

	private void checkArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].length() < 2)
					throw new IllegalArgumentException(
							"바른 인자 아님: " + args[i]);
				if (args[i].charAt(1) == '-') {
					// --opt
					if (args[i].length() < 3)
						throw new IllegalArgumentException(
								"바른 인자 아님: " + args[i]);
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

	static Scanner scanner = new Scanner(System.in);

	public static Scanner getScanner() {
		return scanner;
	}

	public static void main(String[] args) {
		var jbDabang = new DaBang();
		jbDabang.checkArgs(args);

		try { // (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				try {
					jbDabang.serveOneCustomer(scanner);
					for (int i = 0; i < 3; i++) {
						System.out.println(".");
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
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

			switch (option) {
			case 1:
				// 고객이 자기가 과거 입력한 주소 목록을 보고 관리한다.
				manageOwnAddress(customer);
				break;
			case 2:
				상품관리.searchAndOrder(customer);
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
		var addresses = AddressMan.getNshowAddresses(logger, 
				scanner, 고객sn);

		while (true) {
			int size = addresses.size();
			String options = getManageOptions(size);
			var sb = new StringBuilder("수행할 작업 번호? (1~");

			sb.append((size > 0 ? 5 : 2));
			sb.append(")");

			int intOpt = Utility.getIntegerValue(scanner, 
					options, sb.toString());
			var option = AddressOption.getOption(size, intOpt);
			switch (option) {
			case LISTING:
				addresses = AddressMan.getNshowAddresses(logger, 
						scanner, 고객sn);
				break;

			case REGISTER:
				try {
					주소관리.acquireNewAddress(scanner, 고객sn);
				} catch (StopSearchingException e) {
					e.printStackTrace();
				} catch (NoInputException e) {
					e.printStackTrace();
				}
				break;

			case UPDATE:
				AddressMan.updateAddress(addresses, scanner);
				break;

			case DELETE:
				AddressMan.deleteAddress(addresses, scanner);
				break;

			case FINISH:
				return; // 상위 메뉴로 복귀

			default:
				System.out.println("부적절한 옵션: " + intOpt);
				break;
			}
		}
	}

	/**
	 * 고객에 할 수 있는 주소 관리 옵션 문자열 생성
	 * 
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

	private CustomerInfo getUserCredential(Scanner scanner) {
		while (true) {
			// 고객 가입 옵션 제시
			optional고객등록(scanner);
			try {
				CustomerInfo customer = getCustomerInfo();

				if (customer != null) {
					System.out.println("J.B.차방이 " 
				+ customer.get고객ID() + "님을 환영합니다");
					return customer;
				}
			} catch (NoSuch고객Exception e) {
				System.out.println(e.getMessage());
				logger.warning(e.getMessage());
			}
		}
	}

	private CustomerInfo getCustomerInfo() 
			throws NoSuch고객Exception {
		System.out.println("로그인 정보를 입력하세요.");
		String 고객Id = Utility.get고객ID(scanner, "\t고객ID : ",
				DEBUG_MODE);
		System.out.print("\t비밀번호: ");

		if (DEBUG_MODE) {
			var customer = read전통고객(고객Id);
			System.out.println("'" + 고객Id + "'님 로그인되었습니다.");
			return customer;
		}
		if (scanner.hasNext()) {
			String password = scanner.nextLine().trim();
			var customer = read전통고객(고객Id);

			if (customer != null) {
				boolean goodPwd = SecureMan.passwordVerified(
						password, customer.getSalt(),
						customer.getPassword());
				if (goodPwd) {
					System.out.println("'" + 고객Id + "'님 로그인되었습니다.");
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
					고객Id = Utility.get고객ID(scanner, preFix 
							+ " 'ID'를 입력하세요 : ", false);
					try {
						고객계정.get고객SN(고객Id);
						System.out.println("'" + 고객Id 
								+ "'는 사용하실 수 없습니다.");
						preFix = "다른";
					} catch (NoSuch고객Exception e) {
						System.out.print("'" + 고객Id +
								"'는 사용가능합니다.");
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			String password = Utility.getPassword(scanner);
			byte[] salt = SecureMan.getSalt();
			byte[] pwdEncd = SecureMan.encryptPassword(password, 
					salt);
			고객계정.save전통고객(고객Id, salt, pwdEncd);
		}
	}

	static private String 입력접수(Scanner scanner) {
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
	 * 고객의 의사를 확인한다. 고객 입력 문자가 'Y', 'y', 'N', 'n' 
	 * 문자 혹은 [엔터] 키가 아니면 재 입력을 요구한다.
	 * 
	 * @param type    고객이 선택한 차 종류
	 * @param scanner 고객 입력 접수용 참조
	 * @return 참: 'Y', 'y', [엔터]키 일 때, 거짓: 'N', 'n'일 때.
	 */
	static boolean getUserResponse(String question, 
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
			validInput = "y".equals(input) || "n".equals(input) 
					|| (input != null && input.isEmpty());
		} while (!validInput);

		if (input != null) {
			input = input.trim().toLowerCase();
			if (input.length() == 0 || input.equals("y"))
				return true;
		}
		assert "n".equals(input) : "'부정' 의사 표시로 부적절한 문자 입력!";
		return false;
	}
}
