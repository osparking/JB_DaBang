package com.jbpark.dabang.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Scanner;

import com.jbpark.dabang.utility.TeaType;

/**
 * 이 프로그램은 한국 <strong>전통차</strong> 온라인 쇼핑몰을 구현한다. 이 프로그램의 깃허브 저장소는 다음과 같다.
 * 
 * @see <a href="https://github.com/osparking/JB_DaBang">JB_DaBang 깃허브 저장소</a>
 * @see <a href="https://github.com/osparking/JB_module">JB_module 깃허브 저장소</a>
 * @see <a href=
 *      "https://github.com/osparking/JB_module/blob/main/src/com/jbpark/dabang/module/TeaType.java">차
 *      종류</a>
 * @author 박종범(Park, JongBum)
 * @version 1.0.0
 *
 */
public class DaBang {
	public static void main(String[] args) {
		var jbDabang = new DaBang();

		System.out.println("J.B. 차방이 당신을 환영합니다");

		TeaType type = TeaType.비선택;
		Scanner scanner = new Scanner(System.in);
		do {
			if (type == TeaType.입력오류) {
				System.out.println("잘못된 입력입니다. 다시 선택해 주세요.");
			}
			jbDabang.showTeaSelection();
			type = jbDabang.getTeaSelection(scanner);
			if (type == TeaType.비선택) {
				if (jbDabang.getUserResponse(
						"주문을 원치 않으십니까", scanner)) {
					break;
				}
				type = null; // 차 선택 변경 
			}
		} while (type == null || type == TeaType.입력오류);
		scanner.close();
		if (type == TeaType.비선택)
			System.out.println("안녕히 가십시오.");
		else 
			System.out.println(
					"당신이 주문한 " + type.name() + "을 준비할께요.");
	}

	/**
	 * 고객에게 원하는 차 종류를 입력받는다.
	 * @param scanner
	 * @return 고객이 선택한 차 종류 혹은 null(입력 오류 혹은 입력한 
	 * 제품 확인 거부 때)
	 */
	private TeaType getTeaSelection(Scanner scanner) {
		//@formatter:off
		String selection = 입력접수(scanner);

		if (selection.isEmpty()) {
			return TeaType.비선택;
		}
		for (var type : TeaType.values()) {
			if (type.get단축명().equals(selection) || 
					type.name().indexOf(selection) >= 0) {
				if (getUserResponse(type + "을 선택하셨습니까", scanner))
					return type;
				else
					return TeaType.비선택;
			}
		}
		return TeaType.입력오류;
		//@formatter:on
	}

	private String 입력접수(Scanner scanner) {
		String 고객입력 = "";
		try {
			고객입력 = scanner.nextLine();
			고객입력 = 고객입력.trim();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return 고객입력;
	}

	/**
	 * 고객이 원하는 전통차가 맞는지 확인한다.
	 * 
	 * @param type    고객이 선택한 차 종류
	 * @param scanner 고객 입력 접수용 참조
	 * @return 맞으면 참, 아니면 거짓
	 */
	private boolean getUserResponse(String question, Scanner scanner) {
		System.out.println(question + "?[Y/n] : ");

		String input = 입력접수(scanner);

		if (input != null) {
			input = input.trim().toLowerCase();
			if (input.length() == 0 || input.equals("y"))
				return true;
		}
		return false;
	}

	private void showTeaSelection() {
		LocalDate today = LocalDate.now();
		//@formatter:off
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
		//@formatter:on
		int teaCount = teaTypes.length;
		for (int i = 0; i < teaCount; i++) {
			var teaMenu = new StringBuffer(" -");

			if (teaTypes[i] == TeaType.비선택) {
				System.out.println(" -메뉴 선택 안함([엔터])");
				break;
			}
			else {
				teaMenu.append(teaTypes[i]);
				if (i == specialInx)
					teaMenu.append("*");
				System.out.println(teaMenu);
			}
		}
		System.out.println("=".repeat(40));
		System.out.print("단축명(ㄱ-ㅎ), 이름(일부/전부) :");
	}
}
