package yeelp.stoicdummy.util;

import java.util.Arrays;

public final class StringUtils {

	private StringUtils() {
		throw new RuntimeException("class not to be instantiated!");
	}
	
	public static String convertToRomanNumerals(int a) {
		if(a <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(int i = a; i >= 100; sb.append("C"), i -= 100);
		int rem = a % 100;
		if(rem >= 90) {
			sb.append("XC");
			rem -= 90;
		}
		else if (rem >= 50) {
			sb.append("L");
			rem -= 50;
		}
		else if(rem >= 40) {
			sb.append("XL");
			rem -= 40;
		}
		for(; rem >= 10; sb.append("X"), rem -= 10);
		if(rem >= 9) {
			sb.append("IX");
			rem -= 9;
		}
		else if (rem >= 5) {
			sb.append("V");
			rem -= 5;
		}
		else if (rem >= 4) {
			sb.append("IV");
			rem -= 4;
		}
		for(; rem > 0; sb.append("I"), rem--);
		return sb.toString();
	}
	
	public static String pad(String toPad, char pad, int totalLength) {
		if(toPad.length() >= totalLength - 1) {
			return toPad;
		}
		int space = totalLength - toPad.length();
		int frontPad = space/2;
		int backPad = space - frontPad;
		char[] front = new char[frontPad];
		char[] back = new char[backPad];
		Arrays.fill(front, pad);
		Arrays.fill(back, pad);
		return String.valueOf(front) + toPad + String.valueOf(back);
	}
	
	public static String number(int n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n);
		char lastDigit = sb.charAt(sb.length() - 1);
		switch(lastDigit) {
			case '1':
				return sb.append("st").toString();
			case '2':
				return sb.append("nd").toString();
			case '3':
				return sb.append("rd").toString();
			default:
				return sb.append("th").toString();
		}
	}
}
