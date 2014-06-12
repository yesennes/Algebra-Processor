package lang;

import java.util.Arrays;
import java.util.Collection;

/**
 * Class that provides methods for dealing with arrays, text and numbers.
 * 
 * @author Luke Senseney
 * 
 */
public class General
{
	/**
	 * Adds a double value to a double array.
	 * 
	 * @param value value to be added.
	 * @param array the array it must be added to.
	 * @return An array with value added to the end.
	 */
	public static double[] addToArray(double value,double[] array)
	{
		double[] copy=new double[array.length+1];
		System.arraycopy(array,0,copy,0,array.length);
		copy[copy.length-1]=value;
		return copy;
	}

	/**
	 * Adds a char value to a char array.
	 * 
	 * @param value value to be added.
	 * @param array the array it must be added to.
	 * @return An array with value added to the end.
	 */
	public static char[] addToArray(char value,char[] array)
	{
		char[] copy=new char[array.length+1];
		System.arraycopy(array,0,copy,0,array.length);
		copy[copy.length-1]=value;
		return copy;
	}

	/**
	 * Adds a T value to a T array.
	 * 
	 * @param value Value to be added.
	 * @param array The array it must be added to.
	 * @return An array with value added to the end.
	 */
	public static <T>T[] addToArray(T value,T[] array)
	{
		T[] copy=Arrays.copyOf(array,array.length+1);
		copy[copy.length-1]=value;
		return copy;
	}

	/**
	 * Concatenates two arrays.
	 * 
	 * @param array Array to concatenate to.
	 * @param array2 Array to concatenate on the end of array.
	 * @return The concatenation of array and array2.
	 */
	public static char[] concatenate(char[] array,char[] array2)
	{
		char[] copy=Arrays.copyOf(array,array.length+array2.length);
		System.arraycopy(Arrays.copyOf(array2,array2.length),0,copy,array.length,array2.length);
		return(copy);
	}

	/**
	 * Concatenates two arrays.
	 * 
	 * @param array Array to concatenate to.
	 * @param array2 Array to concatenate on the end of array.
	 * @return The concatenation of array and array2.
	 */
	public static double[] concatenate(double[] array,double[] array2)
	{
		double[] copy=Arrays.copyOf(array,array.length+array2.length);
		System.arraycopy(Arrays.copyOf(array2,array2.length),0,copy,array.length,array2.length);
		return(copy);
	}

	/**
	 * Concatenates two arrays.
	 * 
	 * @param array Array to concatenate to.
	 * @param array2 Array to concatenate on the end of array.
	 * @return The concatenation of array and array2.
	 */
	public static <T extends BetterCloneable<T>>T[] concatenate(T[] array,T[] array2)
	{
		T[] copy=Arrays.copyOf(array,array.length+array2.length);
		System.arraycopy(Arrays.copyOf(array2,array2.length),0,copy,array.length,array2.length);
		for(int i=0;i<copy.length;i++)
			copy[i]=copy[i].clone();;
		return(copy);
	}

	/**
	 * Deletes the value at index from array.
	 * 
	 * @param index The index at which to delete.
	 * @param array The array to delete from.
	 * @return An array with the value deleted.
	 */
	public static char[] delFromArray(int index,char[] array)
	{
		char[] copy=new char[array.length-1];
		System.arraycopy(array,0,copy,0,index);
		System.arraycopy(array,index+1,copy,index,copy.length-index);
		return copy;
	}

	/**
	 * Deletes the value at index from array.
	 * 
	 * @param index The index at which to delete.
	 * @param array The array to delete from.
	 * @return An array with the value deleted.
	 */
	public static double[] delFromArray(int index,double[] array)
	{
		double[] copy=new double[array.length-1];
		System.arraycopy(array,0,copy,0,index);
		System.arraycopy(array,index+1,copy,index,copy.length-index);
		return copy;
	}

	/**
	 * Deletes the value at index from array.
	 * 
	 * @param index The index at which to delete.
	 * @param array The array to delete from.
	 * @return An array with the value deleted.
	 */
	public static <T>T[] delFromArray(int index,T[] array)
	{
		T[] copy=Arrays.copyOf(array,array.length-1);
		System.arraycopy(array,index+1,copy,index,copy.length-index);
		return copy;
	}

	public static <T>T[] swap(int index1,int index2,T[] array)
	{
		T temp=array[index1];
		array[index1]=array[index2];
		array[index2]=temp;
		return array;
	}

	public static boolean hasNumber(char a)
	{
		return a=='-'||a=='.'||a=='0'||a=='1'||a=='2'||a=='3'||a=='4'||a=='5'||a=='6'||a=='7'||a=='8'||a=='9';
	}

	public static Constant max(Constant[] a)
	{
		Constant[] b=Arrays.copyOf(a,a.length);
		Arrays.sort(b);
		try
		{
			return b[0];
		}catch(ArrayIndexOutOfBoundsException e)
		{
			return new Constant();
		}
	}

	public static <T extends Comparable<T>> T max(Collection<T> a)
	{
		T max=null;
		for(T current:a)
			if(max==null)
				max=current;
			else
				if(max.compareTo(current)<0)
					max=current;
		return max;
	}

	public static String removeDecimal(double a)
	{
		String b=String.valueOf(a);
		while((b.charAt(b.length()-1)=='0'||b.charAt(b.length()-1)=='.')&&b.indexOf(".")!=-1)
		{
			b=b.substring(0,b.length()-1);
		}
		return b;
	}

	public static int gcd(double a,double b)
	{
		if(a%1!=0||b%1!=0)
			if(1/(a%1)%1==0&&1/(b%1)%1==0)
				return (int)(1/(a%1)*1/(b%1)/gcd(1/(a%1),1/(b%1)));
			else
				return 1;
		while(b!=0)
		{
			double temp=b;
			b=a%b;
			a=temp;
		}
		return (int)Math.abs(a);
	}

	public static int lcm(double a,double b)
	{
		a=a/gcd(a,b);
		return (int)(a*b);
	}
}// Glory to God