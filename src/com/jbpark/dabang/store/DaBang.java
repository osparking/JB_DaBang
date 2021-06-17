package com.jbpark.dabang.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import com.jbpark.dabang.utility.TeaType;

/**
 * 이 프로그램은 한국 <strong>전통차</strong> 온라인 쇼핑몰을 구현한다.
 * 이 프로그램의 깃허브 저장소는 다음과 같다.
 * 
 * @see <a href="https://github.com/osparking/JB_DaBang">JB_DaBang 깃허브 저장소</a>
 * @see <a href="https://github.com/osparking/JB_module">JB_module 깃허브 저장소</a>
 * @see <a href="https://github.com/osparking/JB_module/blob/main/src/com/jbpark/dabang/module/TeaType.java">차 종류</a>
 * @author 박종범(Park, JongBum)
 * @version 1.0.0
 *
 */
public class DaBang {

	public static void main(String[] args) {
		/**
		 * stringbuilder ==> stringbuffer로
		 */
		var tea = TeaType.보성녹차;
		System.out.println("JB 다방에 환영합니다");
		var teaTypes = TeaType.values();

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
