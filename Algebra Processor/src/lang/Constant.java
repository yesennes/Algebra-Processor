package lang;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class to maintain exact accuracy when using repeating decimals, non-integer, or constants, by
 * holding a numerator, denominator, and array of Roots.
 * 
 * @author Luke Senseney
 * 
 */
public class Constant extends Number implements Comparable<Number>
{
	private static final long serialVersionUID=01L;;
	/**
	 * The numerator of this number.
	 */
	public long numerator=0;
	/**
	 * The denominator of the number.
	 */
	public long denominator=1;
	/**
	 * A array of roots in this Constant.
	 */
	public TreeMap<Integer,Constant> roots=new TreeMap<Integer,Constant>();
	/**
	 * Constant with a value of 1.
	 */
	public static final Constant ONE=new Constant(1);
	/**
	 * Constant with a value of -1
	 */
	public static final Constant NEGATE=new Constant(-1);

	/**
	 * Creates a new Constant from an long.
	 * 
	 * @param newConstant The long to make a Constant.
	 */
	public Constant(long newConstant)
	{
		numerator=newConstant;
	}

	/**
	 * Creates a new Constant from an numerator and a denominator.
	 * 
	 * @param num The new numerator.
	 * @param denom The new denominator.
	 */
	public Constant(long num,long denom)
	{
		numerator=num;
		denominator=denom;
		simplify();
	}

	/**
	 * Creates a new Constant from a double.
	 * 
	 * @param newConstant The double to make a Constant out of.
	 */
	public Constant(double newConstant)
	{
		while(newConstant%1!=0)
		{
			newConstant*=10;
			denominator*=10;
		}
		numerator=(long)newConstant;
		simplify();
	}

	public Constant(long num,long denom,TreeMap<Integer,Constant> newRoots)
	{
		numerator=num;
		denominator=denom;
		roots=newRoots;
		simplify();
	}

	/**
	 * Creates a new Constant with a value of 0.
	 */
	public Constant()
	{
	}

	@Override public double doubleValue()
	{
		double sum=1;
		for(Entry<Integer,Constant> root:roots.entrySet())
			sum*=Math.pow(root.getValue().doubleValue(),1./root.getKey());
		return sum*numerator/(double)denominator;
	}

	@Override public float floatValue()
	{
		return (float)doubleValue();
	}

	@Override public int intValue()
	{
		return (int)doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	@Override public long longValue()
	{
		return (long)doubleValue();
	}

	/**
	 * Adds a Root to this Constant.
	 * 
	 * @param a The Root to add.
	 */
	public void addRoot(Integer root,Constant in)
	{
		Constant was=roots.put(root,in);
		if(was!=null)
			in.multiply(was);
	}

	/**
	 * Simplify the fractions and the Roots.
	 */
	public void simplify()
	{
		ArrayList<Entry<Integer,Constant>> toAdd=new ArrayList<Entry<Integer,Constant>>();
		for(Entry<Integer,Constant> current:roots.entrySet())
		{
			for(Entry<Integer,Constant> root:current.getValue().roots.entrySet())
				toAdd.add(new SimpleEntry<Integer,Constant>(current.getKey()*root.getKey(),root.getValue()));
			current.getValue().roots=new TreeMap<Integer,Constant>();
		}
		for(int i=0;i<toAdd.size();i++)
		{
			Entry<Integer,Constant> current=toAdd.get(i);
			for(Entry<Integer,Constant> root:current.getValue().roots.entrySet())
				toAdd.add(new SimpleEntry<Integer,Constant>(current.getKey()*root.getKey(),root.getValue()));
			current.getValue().roots=new TreeMap<Integer,Constant>();
		}
		for(Entry<Integer,Constant> current:toAdd)
			addRoot(current.getKey(),current.getValue());
		ArrayList<Entry<Integer,Constant>> entry=new ArrayList<Entry<Integer,Constant>>(roots.entrySet());
		for(int I=0;I<roots.size();I++)
		{
			Entry<Integer,Constant> current=entry.get(I);
			Constant extract=extract(current.getKey(),current.getValue());
			if(!extract.equals(ONE))
				current.getValue().divide(extract);
			extract.numerator=(long)Math.pow(extract.numerator,1./current.getKey());
			extract.denominator=(long)Math.pow(extract.denominator,1./current.getKey());
			current.getValue().numerator=current.getValue().numerator*current.getValue().denominator;
			denominator=denominator*current.getValue().denominator;
			current.getValue().denominator=1;
			if(!extract.equals(ONE))
				multiply(extract);
		}
		Collection<Constant> value=roots.values();
		while(value.remove(ONE));
		Constant one=roots.remove(1);
		if(one!=null)
		{
			multiply(one);
		}
		if(denominator<0)
		{
			numerator=-numerator;
			denominator=-denominator;
		}
		double divide=General.gcd(numerator,denominator);
		numerator=(long)(numerator/divide);
		denominator=(long)(denominator/divide);
	}

	/**
	 * Multiplies this by a.
	 * 
	 * @param a The Constant to multiply this by.
	 */
	public void multiply(Constant a)
	{
		numerator=numerator*a.numerator;
		denominator=denominator*a.denominator;
		for(Entry<Integer,Constant> current:a.roots.entrySet())
			addRoot(current.getKey(),current.getValue());
		simplify();
	}

	/**
	 * Multiplies a shallow copy of a by b and returns it.
	 * 
	 * @param a Constant to be multiplied
	 * @param b Constant to be multiplied
	 * @return a*b
	 */
	public static Constant multiply(Constant a,Constant b)
	{
		a=a.clone();
		a.multiply(b);
		return a;
	}

	/**
	 * Multiplies this by a.
	 * 
	 * @param a The long to multiply this by.
	 */
	public void multiply(long a)
	{
		numerator=numerator*a;
		simplify();
	}

	/**
	 * Divides this by a.
	 * 
	 * @param a The Constant to divide this by.
	 */
	public void divide(Constant a)
	{
		Constant clone=a.clone();
		clone.invert();
		multiply(clone);
		simplify();
	}

	/**
	 * Divides this by a.
	 * 
	 * @param a The long to divide this by.
	 */
	public void divide(long a)
	{
		denominator=denominator*a;
		simplify();
	}

	/**
	 * Adds a to this.
	 * 
	 * @param a Constant to add to this.
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public void add(Constant a) throws DifferentRoots
	{
		if(!roots.equals(a.roots))
			throw new DifferentRoots("The irrational part of the Constants are different");
		long lcm=General.lcm(denominator,a.denominator);
		numerator=numerator*lcm/denominator+a.numerator*lcm/a.denominator;
		denominator=lcm;
		a.denominator=lcm;
		simplify();
	}

	/**
	 * Subtracts a from this.
	 * 
	 * @param a Constant to subtract from this.
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public void subtract(Constant a) throws DifferentRoots
	{
		if(!roots.equals(a.roots))
			throw new DifferentRoots("The irrational part of the Constants are different");
		long lcm=General.lcm(denominator,a.denominator);
		numerator=numerator*lcm/denominator-a.numerator*lcm/a.denominator;
		denominator=lcm;
		a.denominator=lcm;
		simplify();
	}

	/**
	 * Checks to see if this is rational.
	 * 
	 * @return If this is rational, true, else false.
	 */
	public boolean isRat()
	{
		return roots.size()==0;
	}

	/**
	 * Raises this to power. Note: ignores roots in power. For expressions like 5^(5^(1/2)), use
	 * Term.
	 * 
	 * @param power the power to raise this by
	 */
	public void raise(Constant power)
	{
		if(power.numerator<0)
		{
			invert();
			numerator=(long)Math.pow(numerator,-power.numerator);
			denominator=(long)Math.pow(denominator,-power.numerator);
		}else
		{
			numerator=(long)Math.pow(numerator,power.numerator);
			denominator=(long)Math.pow(denominator,power.numerator);
		}
		if(power.denominator>1)
		{
			roots=new TreeMap<Integer,Constant>(Collections.singletonMap((int)power.denominator,this.clone()));
			numerator=1;
			denominator=1;
		}
		simplify();
	}

	/**
	 * Inverts this, or sets this equal to 1/this
	 */
	public void invert()
	{
		long temp=numerator;
		numerator=denominator;
		denominator=temp;
		for(Constant current:roots.values())
			current.invert();
		simplify();
	}

	/**
	 * Gets the greatest common divisor of two Constants
	 * 
	 * @param a A Constant to get the gcd of.
	 * @param b The other Constant to get the gcd of.
	 * @return The gcde of a and b.
	 */
	public static Constant gcd(Constant a,Constant b)
	{
		Constant ans=new Constant(General.gcd(a.numerator,b.numerator),a.denominator*b.denominator/General.gcd(a.denominator,b.denominator));
		for(Entry<Integer,Constant> current:a.roots.entrySet())
		{
			Constant other=b.roots.get(current.getKey());
			Constant gcd=Constant.gcd(current.getValue(),other);
			if(!gcd.equals(ONE))
				ans.addRoot(current.getKey(),gcd);
		}
		ans.simplify();
		return ans;
	}

	@Override public Constant clone()
	{
		Constant clone=new Constant();
		clone.numerator=numerator;
		clone.denominator=denominator;
		clone.roots=new TreeMap<Integer,Constant>(roots);
		return clone;
	}

	/**
	 * Checks if this is equal to another Constant.
	 * 
	 * @param a Constant to check if this is equal to.
	 * @return If the Constants equal, true, else false.
	 */
	@Override public boolean equals(Object o)
	{
		try{
			Number a=(Number)o;
			return a.doubleValue()==doubleValue();
		}catch(ClassCastException e)
		{
			return false;
		}
	}
	
	@Override public int hashCode()
	{
		return (int)doubleValue()*(1<<16);
	}
	
	@Override public int compareTo(Number o)
	{
		if(o==null)
			return 1;
		double diff=doubleValue()-o.doubleValue();
		if(diff>0)
			return 1;
		else
			return diff<0?-1:0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		String r="";
		for(Entry<Integer,Constant> current:roots.entrySet())
		{
			if(current.getKey()==2)
				r+="\u221a";
			else if(current.getKey()==3)
				r+="\u221b";
			else if(current.getKey()==4)
				r+="\u221c";
			else
				r+=(char)(8304+current.getKey())+"\u221a";
			r+='('+current.getValue().toString()+')';
		}
		if(denominator!=1)
			r+="/"+denominator;
		if(roots.size()==0||(numerator!=1&&numerator!=-1))
			r=numerator+r;
		else if(numerator==-1)
			r="-"+r;
		return r;
	}

	/**
	 * Gets the highest integer that is a factor of inRoot, and when rooted by root is an integer.
	 * 
	 * @return The highest integer that is a factor of inRoot, and when rooted by root is an
	 *         integer.
	 */
	public static Constant extract(int root,Constant inRoot)
	{
		int i;
		for(i=(int)Math.pow(inRoot.numerator,1./root);inRoot.numerator/Math.pow(i,root)%1!=0;i--);
		Constant answer=new Constant(Math.pow(i,root));
		for(i=(int)Math.pow(inRoot.denominator,1./root);inRoot.denominator/Math.pow(i,root)%1!=0;i--);
		answer.divide((int)Math.pow(i,root));
		return answer;
	}
}// Glory to God
