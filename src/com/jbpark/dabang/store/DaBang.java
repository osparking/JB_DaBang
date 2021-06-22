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
		jbDabang.showTeaSelection();

		TeaType type = null;
		Scanner scanner = new Scanner(System.in);
		do {
			type = jbDabang.getTeaSelection(scanner);
			if (type == null) {
				System.out.println("입력 오류입니다. 다시 입력하세요 (-: ");
			}
		} while (type == null);
		scanner.close();
		
		System.out.println("당신이 주문한 " + type + "을 준비할께요.");
	}

	private TeaType getTeaSelection(Scanner scanner) {
		//@formatter:off
		String selection = scanner.nextLine();
		
		selection = selection.trim();
		for (var type : TeaType.values()) {
			if (type.get단축명().equals(selection) || 
					type.name().indexOf(selection) >= 0) {
				System.out.println(type + "?");
				if (selectionConfirmed(type, scanner))
					return type;
			}
		}
		return null;
		//@formatter:on
	}

	/**
	 * 고객이 원하는 전통차가 맞는지 확인한다.
	 * 
	 * @param type    고객이 선택한 차 종류
	 * @param scanner 고객 입력 접수용 참조
	 * @return 맞으면 참, 아니면 거짓
	 */
	private boolean selectionConfirmed(TeaType type, Scanner scanner) {
		System.out.println(type + "을 선택하셨습니까?[Y/n] : ");

		String input = scanner.nextLine();

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
		long specialInx = ChronoUnit.DAYS.between(today, 
				LocalDate.of(2021, 6, 22)) % teaTypes.length;
		//@formatter:on

		for (int i = 0; i < teaTypes.length; i++) {
			var teaMenu = new StringBuffer(" -");

			teaMenu.append(teaTypes[i]);
			if (i == specialInx)
				teaMenu.append("*");
			System.out.println(teaMenu);
		}
		System.out.println("=".repeat(40));
		System.out.print("단축명(ㄱ-ㅎ), 이름(일부/전부) :");
	}
}
