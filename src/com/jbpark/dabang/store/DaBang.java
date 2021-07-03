package com.jbpark.dabang.store;

import java.awt.Toolkit;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jbpark.dabang.utility.TeaType;

import jbpark.utility.JB_FileHandler;
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
	private static Logger logger 
		= Logger.getLogger("com.jbpark.dabang");
	{
		logger.setLevel(Level.INFO);
		logger.setUseParentHandlers(false);
		int LOG_ROTATION_COUNT = 10;
		JB_FileHandler handler;
		try {
			String logFile = "D:/LOG/JB_Dabang"; 
			System.out.println("로그파일: " 
					+ logFile + ".*.log.*");
			handler = new JB_FileHandler(
					logFile + ".%g.log", 0, 
					LOG_ROTATION_COUNT);
			handler.setLevel(Level.INFO);
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
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
		}
	}

	private void serveOneCustomer(Scanner scanner) {
		System.out.println("다음 손님 어서오세요...");
		System.out.println("J.B.차방이 당신을 환영합니다");

		TeaType type = TeaType.비선택;
		do {
			showTeaSelection();
			try {
				type = getTeaSelection(scanner);
				if (type == TeaType.비선택) {
					if (getUserResponse("주문을 원치 않으십니까", scanner)) {
						break;
					}
					type = null; // 차 선택 변경
				}
			} catch (TeaInputException te) {
				String msg = "'는 잘못된 입력입니다. 다시 선택해 주세요.";
				System.out.println("'" + te.getMessage() + msg);
			}
		} while (type == null);
		//@formatter:off	
		if (type == TeaType.비선택)
			System.out.println("안녕히 가십시오.");
		else {
			String tea = type.name();
			int idx = tea.length() - 2;
			int cp = tea.codePointAt(idx);
			String msg = "당신이 주문한 '" 
					+ tea
					+ (SuffixChecker.has받침(cp, 
						tea.substring(idx)) ? "'를" : "'을") 
				+ " 준비할께요.";
			DateTimeFormatter dtf 
				= DateTimeFormatter.ofPattern("HH:mm");
			String timeLabel = LocalTime.now().format(dtf);
			logger.info(msg + ", 주문 시각: " + timeLabel);
			System.out.println(msg);
		}
		//@formatter:off	

		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * 고객에게 원하는 차 종류를 입력받는다.
	 * 
	 * @param scanner
	 * @return 고객이 선택한 차 종류, null(입력 오류 혹은 입력한 제품 
	 * 			확인 거부 때)
	 */
	private TeaType getTeaSelection(Scanner scanner) 
			throws TeaInputException {
		String selection = 입력접수(scanner);

		if (selection.isEmpty()) {
			return TeaType.비선택;
		}
		for (var type : TeaType.values()) {
			if (type.get단축명().equals(selection) || 
					type.name().indexOf(selection) >= 0) {
				String msg = "을 선택하셨습니까";
				boolean resp = getUserResponse(type + msg, scanner);
				if (resp)
					return type;
				else
					return TeaType.비선택;
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
	 * 고객이 원하는 전통차가 맞는지 확인한다. 단, 입력 값이 Y/y/예, N/n/안, 
	 * 혹은, [엔터]가 아니면 다시 입력하도록 요구한다.
	 * 
	 * @param type    고객이 선택한 차 종류
	 * @param scanner 고객 입력 접수용 참조
	 * @return 맞으면 참, 아니면 거짓
	 */
	private boolean getUserResponse(String question, Scanner scanner) {

		String input;
		boolean validInput = true;
		do {
			if (!validInput) {
				Toolkit.getDefaultToolkit().beep();
				System.out.println("입력오류입니다. 다시 입력해 주세요");
			}
			System.out.println(question + "?");
			System.out.print("Y/y/예/[엔터]=예; N/n/안=아니오: ");
			input = 입력접수(scanner);
			if (input != null) {
				input = input.trim().toLowerCase();
			}
			validInput = "y".equals(input) 
					|| "n".equals(input) 
					|| "예".equals(input) 
					|| "안".equals(input)
					|| (input != null && input.isEmpty());
		} while (!validInput);

		if (input != null) {
			input = input.trim().toLowerCase();
			if (input.length() == 0 
					|| input.equals("y") 
					|| input.equals("예"))
				return true;
		}
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

			if (teaTypes[i] == TeaType.비선택) {
				System.out.println(" -메뉴 선택 안함([엔터])");
				break;
			} else {
				teaMenu.append(teaTypes[i]);
				if (i == specialInx)
					teaMenu.append("*");
				System.out.println(teaMenu);
			}
		}
		System.out.println("=".repeat(40));
		System.out.print("단축명(ㄱ-ㅎ), 이름(일부/전부): ");
	}
}
