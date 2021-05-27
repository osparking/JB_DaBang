package com.jbpark.dabang.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import com.jbpark.dabang.module.TeaType;

public class DaBang {

	public static void main(String[] args) {
		/**
		 * stringbuilder ==> stringbuffer로
		 */
		TeaType tea = TeaType.보성녹차;
		System.out.println("JB 다방에 환영합니다");
		TeaType teaTypes[] = TeaType.values();

		/**
		 * 나중에 Enum to List 사용하여 깔끔하게 정리
		 */
		String menu = String.join("/", TeaType.감잎차.toString() + (TeaType.감잎차.ordinal() + 1),
				TeaType.보성녹차.toString() + (TeaType.보성녹차.ordinal() + 1),
				TeaType.율무차.toString() + (TeaType.율무차.ordinal() + 1));

		System.out.println(menu);
		int idx = LocalDate.now().getDayOfYear() % teaTypes.length;

		String weekDay = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E") // E => Weekday
				.withLocale(Locale.KOREAN));
		System.out.println(weekDay + "요일 스페셜: " + teaTypes[idx]);

		System.out.println("원하는 차 종류를 입력: ");

		Scanner scanner = new Scanner(System.in);

		int 제품번호 = scanner.nextInt();

		TeaType teaType = TeaType.values()[제품번호 - 1];
		switch (teaType) {
		case 감잎차:
			System.out.println("감잎차를 선택하셨습니다.");
			break;
		case 보성녹차:
			System.out.println("보성녹차를 선택하셨습니다.");
			break;
		case 율무차:
			System.out.println("율무차를 선택하셨습니다.");
			break;
		default:
			break;
		}
		scanner.close();

		return;
	}
}
