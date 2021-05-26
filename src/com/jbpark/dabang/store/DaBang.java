package com.jbpark.dabang.store;

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
		String menu = String.join("/", 
				TeaType.감잎차.toString(), 
				TeaType.보성녹차.toString(), 
				TeaType.율무차.toString() );
		
		System.out.println(menu);
		System.out.println("원하는 차 종류를 입력: ");
		
		Scanner scanner = new Scanner(System.in);		
		int 제품번호 = scanner.nextInt();
		System.out.println("제품번호 " + 제품번호 + "를 선택하셨습니다.");
		scanner.close();
		
		return ;
	}
}
