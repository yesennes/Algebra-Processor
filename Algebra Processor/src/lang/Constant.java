package lang;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class to maintain exact accuracy when using repeating decimals, non-integer, or constants, by holding a numerator, denominator, and array of Roots.
 * @author Luke Senseney
 */
public class Constant extends Number implements Comparable<Number>,Serializable
{
	private static final long serialVersionUID=01L;
	/**
	 * The numerator of this number.
	 */
	public BigInteger numerator=BigInteger.ZERO;
	/**
	 * The denominator of the number.
	 */
	public BigInteger denominator=BigInteger.ONE;
	/**
	 * A Map of roots in this constant. For each key value^(1/key) is multiplied into this constant.
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
	 * @param newConstant The long to make a Constant.
	 */
	public Constant(long newConstant)
	{
		numerator=BigInteger.valueOf(newConstant);
	}

	/**
	 * Creates a new Constant from an numerator and a denominator.
	 * @param num The new numerator.
	 * @param denom The new denominator.
	 */
	public Constant(long num,long denom)
	{
		numerator=BigInteger.valueOf(num);
		denominator=BigInteger.valueOf(denom);
		simplify();
	}

	/**
	 * Creates a new Constant from a double.
	 * @param newConstant The double to make a Constant out of.
	 */
	public Constant(double newConstant)
	{
		if(newConstant%1!=0)
		{
			String s=String.valueOf(newConstant);
			int i=s.indexOf('.');
			numerator=new BigInteger(s.substring(0,i)+s.substring(i+1));
			denominator=BigInteger.TEN.pow(s.length()-i-1);
		}else
			numerator=BigInteger.valueOf((long)newConstant);
		simplify();
	}

	/**
	 * Creates an new constant from a numerator, denominator and roots
	 * @param num The numerator
	 * @param denom The denominator
	 * @param newRoots The roots
	 */
	public Constant(long num,long denom,TreeMap<Integer,Constant> newRoots)
	{
		numerator=BigInteger.valueOf(num);
		denominator=BigInteger.valueOf(denom);
		roots=newRoots;
		simplify();
	}

	/**
	 * Creates a new Constant with a value of 0.
	 */
	public Constant()
	{}

	/**
	 * @param add
	 * @param lcm
	 */
	public Constant(BigInteger num,BigInteger denom)
	{
		numerator=num;
		denominator=denom;
	}

	/**
	 * Creates a constant from a numerator, denominator and roots.
	 * @param num The numerator of this constant.
	 * @param denom The denominator of this constant.
	 * @param root The roots of this constant.
	 */
	public Constant(BigInteger num,BigInteger denom,TreeMap<Integer,Constant> root)
	{
		numerator=num;
		denominator=denom;
		roots=root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#doubleValue()
	 */
	@Override public double doubleValue()
	{
		double sum=1;
		for(Entry<Integer,Constant> root:roots.entrySet())
			sum*=Math.pow(root.getValue().doubleValue(),1./root.getKey());
		return sum*numerator.doubleValue()/denominator.doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#floatValue()
	 */
	@Override public float floatValue()
	{
		return (float)doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#intValue()
	 */
	@Override public int intValue()
	{
		return (int)doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#longValue()
	 */
	@Override public long longValue()
	{
		return (long)doubleValue();
	}

	/**
	 * Adds a root to this Constant.
	 * @param root The root to be added
	 * @param in The constant to be in the root
	 */
	public void addRoot(Integer root,Constant in)
	{
		Constant was=roots.put(root,in);
		if(was!=null)
			in.multiply(was);
	}

	/**
	 * Simplify the fractions and the roots.
	 */
	public void simplify()
	{
		// Removes all roots from inside each root, and adds it to the proper root. i.e. takes (2^(1/2))^(1/2) and turns it to 2^(1/4)
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
		// Sees if anything can be pulled out of the roots. For instance takes the square root of 12 and turns it into 2 root 3.
		// Then rationalizes the denominator of the root.
		for(Entry<Integer,Constant> current:roots.entrySet())
		{
			// Finds what can be extracted from the current root.
			Constant extract=extract(current.getKey(),current.getValue());
			// Takes it out from inside the root.
			if(!extract.equals(ONE))
				current.setValue(current.getValue().divide(extract));
			// Changes extract to what must be multiplied to the coeff.
			extract.numerator=BigInteger.valueOf((long)Math.pow(extract.numerator.doubleValue(),1./current.getKey().doubleValue()));
			extract.denominator=BigInteger.valueOf((long)Math.pow(extract.denominator.doubleValue(),1./current.getKey().doubleValue()));
			current.getValue().numerator=current.getValue().numerator.multiply(current.getValue().denominator);
			denominator=denominator.multiply(current.getValue().denominator);
			current.getValue().denominator=BigInteger.ONE;
			if(!extract.equals(ONE))
			{
				Constant toBe=multiply(extract);
				numerator=toBe.numerator;
				denominator=toBe.denominator;
				roots=toBe.roots;
			}
		}
		Collection<Constant> value=roots.values();
		// Removes all roots with a value of one.
		while(value.remove(ONE));
		// Keeps the denominator positive.
		if(denominator.compareTo(BigInteger.ZERO)<0)
		{
			numerator=numerator.negate();
			denominator=denominator.negate();
		}
		// Reduces the fraction. Does not call .divide(Constant) to avoid recursion
		BigInteger divide=numerator.gcd(denominator);
		numerator=numerator.divide(divide);
		denominator=denominator.divide(divide);
	}

	/**
	 * Multiplies this by a.
	 * @param a The Constant to multiply this by.
	 * @return this*a
	 */
	public Constant multiply(Constant a)
	{
		Constant c=clone();
		c.numerator=c.numerator.multiply(a.numerator);
		c.denominator=c.denominator.multiply(a.denominator);
		for(Entry<Integer,Constant> current:a.roots.entrySet())
			c.addRoot(current.getKey(),current.getValue());
		c.simplify();
		return c;
	}

	/**
	 * Multiplies this by a.
	 * @param a The long to multiply this by.
	 * @return this*a
	 */
	public Constant multiply(long a)
	{
		Constant c=clone();
		c.numerator=c.numerator.multiply(BigInteger.valueOf(a));
		c.simplify();
		return c;
	}

	/**
	 * Divides this by a.
	 * @param a The Constant to divide this by.
	 * @return this/a
	 */
	public Constant divide(Constant a)
	{
		return multiply(a.invert());
	}

	/**
	 * Divides this by a.
	 * @param a The long to divide this by.
	 * @return this/a
	 */
	public Constant divide(long a)
	{
		Constant c=clone();
		c.denominator=c.denominator.multiply(BigInteger.valueOf(a));
		c.simplify();
		return c;
	}

	/**
	 * Adds a to this.
	 * @param a Constant to add to this.
	 * @return this+a
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public Constant add(Constant a) throws DifferentRoots
	{
		if(!roots.equals(a.roots))
			throw new DifferentRoots();
		// Finds the lcm, adds the numerators/denominators and multiplies by the lcm, then sets the denominator to lcm.
		BigInteger lcm=denominator.multiply(a.denominator).divide(denominator.gcd(a.denominator));
		return new Constant(numerator.multiply(lcm).divide(denominator).add(a.numerator.multiply(lcm).divide(a.denominator)),lcm);
	}

	/**
	 * Subtracts a from this.
	 * @param a Constant to subtract from this.
	 * @return this-a
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public Constant subtract(Constant a) throws DifferentRoots
	{
		return add(a.negate());
	}

	/**
	 * @return -this
	 */
	public Constant negate()
	{
		return multiply(NEGATE);
	}

	/**
	 * Checks to see if this is rational.
	 * @return If this is rational, true, else false.
	 */
	public boolean isRat()
	{
		return roots.size()==0;
	}

	/**
	 * Raises this to power. For expressions like 5^(5^(1/2)), use Term.
	 * @param power the power to raise this by
	 * @return this^power
	 * @throws OverflowException If power was too big to be an exponent.
	 * @throws IllegalArgumentException If power has roots.
	 */
	public Constant raise(Constant power) throws OverflowException,IllegalArgumentException
	{
		if(!power.roots.isEmpty())
			throw new IllegalArgumentException(power+" had roots and was an exponent.");
		if(power.doubleValue()>Integer.MAX_VALUE)
			throw new OverflowException("Power was to big to be an exponent.");
		if(power.numerator.compareTo(BigInteger.valueOf(Integer.MAX_VALUE))>0)
		{
			BigInteger div=power.numerator.multiply(BigInteger.valueOf(Integer.MAX_VALUE));
			power.numerator=BigInteger.valueOf(Integer.MAX_VALUE);
			power.denominator=power.denominator.divide(div);
		}
		if(power.denominator.compareTo(BigInteger.valueOf(Integer.MAX_VALUE))>0)
		{
			BigInteger div=power.denominator.multiply(BigInteger.valueOf(Integer.MAX_VALUE));
			power.denominator=BigInteger.valueOf(Integer.MAX_VALUE);
			power.numerator=power.numerator.divide(div);
		}
		Constant c=clone();
		// If power is less than one, flips this, and raises by -power, else just raises by power.
		if(power.numerator.compareTo(BigInteger.ZERO)<0)
		{
			c=c.invert();
			c.numerator=c.numerator.pow(-power.numerator.intValueExact());
			c.denominator=c.denominator.pow(-power.numerator.intValueExact());
		}else
		{
			c.numerator=c.numerator.pow(power.numerator.intValueExact());
			c.denominator=c.denominator.pow(power.numerator.intValueExact());
		}
		// If power has a denominator, puts this in a root in a new Constant.
		if(power.denominator.compareTo(BigInteger.ONE)>0)
		{
			c.roots=new TreeMap<Integer,Constant>(Collections.singletonMap(power.denominator.intValueExact(),c.clone()));
			c.numerator=BigInteger.ONE;
			c.denominator=BigInteger.ONE;
		}
		c.simplify();
		return c;
	}

	/**
	 * Inverts this
	 * @return 1/this
	 */
	public Constant invert()
	{
		@SuppressWarnings("unchecked") Constant c=new Constant(denominator,numerator,(TreeMap<Integer,Constant>)roots.clone());
		c.roots.replaceAll((t,u)->u.invert());
		c.simplify();
		return c;
	}

	/**
	 * Gets the greatest common divisor of two Constants
	 * @param a A Constant to get the gcd of.
	 * @param b The other Constant to get the gcd of.
	 * @return The gcd of a and b.
	 */
	public static Constant gcd(Constant a,Constant b)
	{
		Constant ans=new Constant(a.numerator.gcd(b.numerator),a.denominator.multiply(b.denominator).divide(a.denominator.gcd(b.denominator)));
		for(Entry<Integer,Constant> current:a.roots.entrySet())
		{
			Constant other=b.roots.get(current.getKey());
			if(other!=null)
			{
				Constant gcd=Constant.gcd(current.getValue(),other);
				if(!gcd.equals(ONE))
					ans.addRoot(current.getKey(),gcd);
			}
		}
		ans.simplify();
		return ans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
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
	 * @param a Constant to check if this is equal to.
	 * @return If the Constants equal, true, else false.
	 */
	@Override public boolean equals(Object o)
	{
		try
		{
			Number a=(Number)o;
			return a.doubleValue()==doubleValue();
		}catch(ClassCastException e)
		{
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode()
	{
		return (int)doubleValue()*(1<<16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
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
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		StringBuffer r=new StringBuffer();
		// If there is nothing in the roots, or numerator isn't 1 or -1,adds the numerator to this. If the numerator is
		// -1 and there are roots, just add the minus sign to this.
		if(numerator.equals(BigInteger.ONE.negate())&&roots.size()!=0)
			r.append("-");
		else if(roots.size()==0||!numerator.equals(BigInteger.ONE))
			r.append(numerator);
		for(Entry<Integer,Constant> current:roots.entrySet())
		{
			// Generates the proper root symbol for the root, and then whats in the root.
			if(current.getKey()<5)
				r.append((char)(0x2218+current.getKey()));
			else
				r.append((char)(0x2070+current.getKey())+"\u221a");
			r.append('('+current.getValue().toString()+')');
		}
		if(!denominator.equals(BigInteger.ONE))
			r.append("/").append(denominator);
		return r.toString();
	}

	/**
	 * Parses a String into a Constant.
	 * @param s String to make into a Constant
	 * @return s as a Constant.
	 * @throws NumberFormatException If s is not a properly formatted number.
	 */
	public static Constant valueOf(String s) throws NumberFormatException
	{
		return new Constant(Double.valueOf(s));
	}

	/**
	 * Finds the x, where x is the highest number that meets these requirements: is a factor of inRoot, and is an rational number when raise to 1/root. This makes the returned Constant for simplifying roots, i.e. to simplify 12^(1/2) call
	 * extract(2,12) it will return 4. divide the 12 by for to get 3, then raise 4 to 1/2 to get 2. The simplified version is 2(3)^1/2
	 * @return The highest integer that is a factor of inRoot, and when rooted by root is an integer.
	 */
	private static Constant extract(int root,Constant inRoot)
	{
		if(inRoot.numerator.abs().doubleValue()==Double.POSITIVE_INFINITY||inRoot.denominator.doubleValue()==Double.POSITIVE_INFINITY)
			return Constant.ONE;
		int i;
		// Finds the highest number, that raised to
		for(i=(int)Math.pow(inRoot.numerator.abs().doubleValue(),1./root);!inRoot.numerator.mod(BigInteger.valueOf((long)Math.pow(i,root))).equals(BigInteger.ZERO);i--);
		Constant answer=new Constant(Math.pow(i,root));
		for(i=(int)Math.pow(inRoot.denominator.doubleValue(),1./root);!inRoot.denominator.mod(BigInteger.valueOf((long)Math.pow(i,root))).equals(BigInteger.ZERO);i--);
		answer.divide((int)Math.pow(i,root));
		return answer;
	}
}// Glory to God
