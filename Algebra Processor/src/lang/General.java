package lang;

import java.util.Arrays;
import java.util.Collection;

/**
 * Class that provides methods for dealing with arrays, text and numbers.
 * 
 * @author Luke Senseney
 */
public class General {
	/*
	public static boolean hasNumber(char a) {
		return a == '-' || a == '.' || a =='0' || a == '1' || a == '2' || a == '3' || a == '4' || a == '5' || a == '6'
				|| a == '7' || a == '8' || a == '9';
	}
	*/

	/*
	public static Constant max(Constant[] a) {
		Constant[] b = Arrays.copyOf(a, a.length);
		Arrays.sort(b);
		try {
			return b[0];
		} catch(ArrayIndexOutOfBoundsException e) {
			return new Constant();
		}
	}
	*/

	/*
	public static <T extends Comparable<? super T>> T max(Collection<T> a) {
		T max = null;
		for(T current : a) {
			if(max == null) {
				max = current;
			} else {
				if(max.compareTo(current) < 0) {
					max = current;
				}
			}
		}
		return max;
	}
	*/

	/*
	public static String removeDecimal(double a) {
		String b = String.valueOf(a);
		while((b.charAt(b.length() - 1) == '0' || b.charAt(b.length() - 1) == '.') && b.indexOf(".") != -1) {
			b = b.substring(0, b.length() - 1);
		}
		return b;
	}
	*/
	
	public static double round(double a,int places) {
		return Math.round(a * Math.pow(10, places)) / Math.pow(10, places);
	}

	public static int gcd(double a, double b) {
		if(a % 1 != 0 || b % 1 != 0) {
			if(1 / (a % 1) % 1 == 0 && 1 / (b % 1) % 1 == 0) {
				return (int)(1 / (a % 1) * 1 / (b % 1) / gcd(1 / (a % 1), 1 / (b % 1)));
			} else {
				return 1;
			}
		}
		while(b != 0) {
			double temp = b;
			b = a % b;
			a = temp;
		}
		return (int)Math.abs(a);
	}

	public static int lcm(double a, double b) {
		a = a / gcd(a, b);
		return (int)(a * b);
	}
}
