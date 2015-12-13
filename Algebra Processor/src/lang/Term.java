package lang;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a mathematical term.
 * @author Luke Senseney
 */
public class Term implements Comparable<Term>,Serializable
{
	private static final long serialVersionUID=1L;
	/**
	 * The coefficient of the Term.
	 */
	public Constant coeff;
	/**
	 * A Map of variables and their powers in it. Each key is raised to its value and multiplied into this.
	 */
	public TreeMap<Character,Constant> vars;
	/**
	 * A Map to represent things which can't be distributed, such as (x+3)^(1/2), each key is raised to its value and then multiplied into this.
	 */
	public TreeMap<Expression,Expression> undistr;
	/**
	 * Term with no Variables, and a coefficient of -1.
	 */
	public final static Term NEGATE=new Term(Constant.NEGATE);
	/**
	 * The character to use for \u03c0.
	 */
	public final static char PI='\u03c0';
	/*
	 * The String to use for e.
	 */
	public final static String E=new String(Character.toChars(0x1d4ee));
	/*
	 * The String to use for i, the \u221a(-1).
	 */
	public final static String IMAG_UNIT=new String(Character.toChars(0x1d4f2));
	/*
	 * Internally, i will be kept as this.
	 */
	final static char interImag='\u05d0';
	/*
	 * Internally, e will be kept as this.
	 */
	final static char interE='\u05d1';

	// public HashSet<Expression> restrictions=new HashSet<Expression>();
	/**
	 * Used to find the level of parentheses in a String representing a term. Also checks that this term is formatted correctly.
	 * @param term The String to find the level of parentheses in.
	 * @return The levels of parentheses in term.
	 * @throws MathFormatException If term is not formatted correctly.
	 */
	private static int findLevel(String term) throws MathFormatException
	{
		// Finds out how many levels of parentheses are in this equation, throwing errors if parentheses don't match.
		int inParen=0;
		int max=0;
		for(char c:term.toCharArray())
			if(c=='(')
			{
				inParen++;
				if(inParen>max)
					max=inParen;
			}else if(c==')')
			{
				inParen--;
				if(inParen<0)
					throw new MathFormatException("There is an unmatched close parenthese.");
			}
		if(inParen!=0)
			throw new MathFormatException("There is an unmatched start parenthese.");
		//Checks term is a correctly formatted term.
		Matcher m=ParenthesesManager.getTerm(0).matcher(term);
		int level=0;
		for(;!m.matches();level++)
		{
			if(level+1>max)
				throw new MathFormatException("The input is not a term.");
			m.usePattern(ParenthesesManager.getTerm(level+1));
		}
		return level;
	}

	/**
	 * Creates a new Term from a String. Garbage in, Garbage out; if the String is not a correctly formated Term, will attempt to read.
	 * Cannot read \u221a, use ^(1/2).
	 * @param newTerm the String to make a Term from.
	 */
	public Term(String newTerm)
	{
		this(newTerm,findLevel(newTerm));
	}

	/**
	 * Creates a new term from newTerm, when the levels of parentheses are known
	 * @param newTerm The String to be turned into a term.
	 * @param findLevel The level of parentheses in newTerm.
	 * @throws MathFormatException If newTerm is not formatted correctly or findLevel is lower than the level of parentheses.
	 */
	public Term(String newTerm,int level) throws MathFormatException
	{
		this(Constant.ONE);
		newTerm=newTerm.replace(IMAG_UNIT,String.valueOf(interImag));
		newTerm=newTerm.replace(E,String.valueOf(interE));
		int neg=0,i=0;
		for(;newTerm.charAt(i)=='-'||newTerm.charAt(i)=='+';i++)
			if(newTerm.charAt(i)=='-')
				neg++;
		if(neg%2==1)
			coeff=Constant.NEGATE;
		newTerm=newTerm.substring(i);
		String number="[\\d\\.]+";
		String var="[^\\d\\.()\\^]";
		Pattern b=Pattern.compile("("+number+")|("+var+")|("+ParenthesesManager.getParen(level)+")");
		Matcher m=b.matcher(newTerm);
		boolean inverse=false;
		try
		{
			while(m.lookingAt())
			{
				MatchResult base=m.toMatchResult();
				m.region(m.end(),newTerm.length());
				m.usePattern(Pattern.compile("\\^-?(?:("+number+")|("+var+")|("+ParenthesesManager.getParen(level)+"))"));
				ArrayList<MatchResult> expos=new ArrayList<MatchResult>();
				while(m.lookingAt())
				{
					expos.add(m.toMatchResult());
					m.region(m.end(),newTerm.length());
				}
				if(expos.isEmpty())
				{
					if(base.group(1)!=null)
						coeff=coeff.multiply(inverse?Constant.valueOf(base.group(1)).invert():Constant.valueOf(base.group(1)));
					else if(base.group(2)!=null)
						addExponent(base.group(2).charAt(0),inverse?Constant.NEGATE:Constant.ONE);
					else
					{
						String paren=base.group(3);
						paren=paren.substring(1,paren.length()-1);
						undistr.put(new Expression(paren),(inverse?Expression.NEGATIVE:Expression.ONE).clone());
					}
				}else
				{
					Constant pow=Constant.ONE;
					Expression power=Expression.ONE.clone();
					for(i=expos.size()-1;i>-1;i--)
					{
						MatchResult cur=expos.get(i);
						boolean negative=cur.group().charAt(1)=='-';
						if(cur.group(1)!=null)
						{
							Constant p=Constant.valueOf(cur.group(1));
							if(pow!=null)
							{
								pow=p.raise(pow);
								if(negative)
									pow=pow.negate();
							}else
							{
								power=new Expression(p).raise(power);
								if(negative)
									power=power.negate();
							}
						}else
						{
							Expression p;
							if(cur.group(2)!=null)
								p=new Expression(cur.group(2).charAt(0));
							else
							{
								String paren=cur.group(3);
								paren.substring(1,paren.length()-1);
								p=new Expression(paren);
							}
							if(pow!=null)
							{
								power=p.raise(pow);
								pow=null;
							}else
								power=p.raise(power);
							if(negative)
								power=power.negate();
						}
					}
					if(pow!=null)
						if(base.group(1)!=null)
							coeff=coeff.multiply(Constant.valueOf(base.group(1)).raise(inverse?pow.negate():pow));
						else if(base.group(2)!=null)
							vars.put(base.group(2).charAt(0),inverse?pow.negate():pow);
						else
						{
							String paren=base.group(3);
							paren=paren.substring(1,paren.length()-1);
							undistr.put(new Expression(paren),new Expression(inverse?pow.negate():pow));
						}
					else
					{
						if(base.group(1)!=null)
							undistr.put(new Expression(Constant.valueOf(base.group(1))),inverse?power.negate():power);
						else if(base.group(2)!=null)
							undistr.put(new Expression(base.group(2).charAt(0)),inverse?power.negate():power);
						else
							undistr.put(new Expression(base.group(3)),inverse?power.negate():power);
					}
				}
				if(!m.hitEnd())
				{
					if(newTerm.charAt(m.regionStart())=='*')
					{
						m.region(m.regionStart()+1,m.regionEnd());
						inverse=false;
					}else if(newTerm.charAt(m.regionStart())=='/')
					{
						m.region(m.regionStart()+1,m.regionEnd());
						inverse=true;
					}else
						inverse=false;
					if(newTerm.charAt(m.regionStart())=='-')
					{
						coeff=coeff.negate();
						m.region(m.regionStart()+1,m.regionEnd());
					}
				}
				m.usePattern(b);
			}
			simplifyTerm();
		}catch(IllegalStateException e)
		{
			throw new MathFormatException(newTerm+" is not formatted correctly.");
		}
	}

	/**
	 * Creates a new Term from a Constant and an map of variables.
	 * @param newCoeff Constant that becomes the coefficient of the Term.
	 * @param newVars Map that becomes the variables of the Term.
	 */
	public Term(Constant newCoeff,Map<Character,Constant> newVars)
	{
		this(newCoeff,newVars,new TreeMap<Expression,Expression>());
	}

	/**
	 * Creates a new Term from a Constant.
	 * @param newCoeff the Constant that becomes the coefficient of the new Term.
	 */
	public Term(Constant newCoeff)
	{
		coeff=newCoeff;
		undistr=new TreeMap<Expression,Expression>();
		vars=new TreeMap<Character,Constant>();
	}

	/**
	 * Creates a new Term from a Constant, map of variables, and a map of Expressions.
	 * @param newCoeff Constant that becomes the coefficient of the Term.
	 * @param newVars Map that becomes the variables of the Term.
	 * @param newUndistr Map of Expression that becomes
	 */
	public Term(Constant newCoeff,Map<Character,Constant> newVars,Map<Expression,Expression> newUndistr)
	{
		coeff=newCoeff;
		vars=new TreeMap<Character,Constant>(newVars);
		undistr=new TreeMap<Expression,Expression>(newUndistr);
		simplifyTerm();
	}

	/**
	 * Creates a new Term from a variable.
	 * @param var a character which will be added to this to the power of one.
	 */
	public Term(char var)
	{
		this(Constant.ONE,Collections.singletonMap(var,Constant.ONE));
	}

	/**
	 * Combines variables and bases that are the same and adds powers of these. Moves some things out of undistr and into the coeff and
	 * vars. Note: does nothing with (x+1)^2 and other similar evaluable undistrs because they turn into multiple Terms.
	 */
	public void simplifyTerm()
	{
		// Finds any entries in undistr that are a single Term raised to a Constant, and multiplies them into this.
		// Term to be multiplied into this.
		Term toBe=new Term(new Constant(1));
		boolean removed=false;
		Iterator<Entry<Expression,Expression>> iter=undistr.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Expression,Expression> current=iter.next();
			if(current.getKey().terms.size()==1&&current.getValue().isConstant())
			{
				toBe=toBe.multiply(current.getKey().terms.get(0).raise(current.getValue().terms.get(0).coeff));
				iter.remove();
				removed=true;
				//Does Euler's Identity
			}else if(current.getKey().equals(new Expression(Term.interE))&&current.getValue().equals(new Expression(Term.interImag).multiply(new Expression(Term.PI))))
			{
				iter.remove();
				coeff=coeff.negate();
			}
		}
		Collection<Expression> removeFrom=undistr.values();
		// Removes all pairs in undistr with an power of zero.
		while(removeFrom.remove(Expression.ZERO));
		if(removed)
		{
			toBe=toBe.multiply(this);
			coeff=toBe.coeff;
			vars=toBe.vars;
			undistr=toBe.undistr;
		}
		// Looks in the coeff to see if any (-1)^(1/2) can be turned in to i.
		Constant inRoot=coeff.roots.get(new Constant(2));
		if(inRoot!=null&&inRoot.numerator.compareTo(BigInteger.ZERO)<0)
		{
			coeff.roots.get(new Constant(2)).numerator=coeff.roots.get(new Constant(2)).numerator.negate();
			addExponent(interImag,Constant.ONE);
		}
		// Looks at the power of i for anything that can be simplified. i.e. takes i^2 and turns it to -1.
		Constant imaginary=vars.get(interImag);
		if(imaginary!=null)
			switch(imaginary.numerator.mod(BigInteger.valueOf(4)).intValue())
			{
				case 0:
					imaginary.numerator=BigInteger.ZERO;
					break;
				case 1:
					imaginary.numerator=BigInteger.ONE;
					break;
				case 2:
					imaginary.numerator=BigInteger.ZERO;
					coeff=coeff.negate();
					break;
				case 3:
					imaginary.numerator=BigInteger.ONE;
					coeff=coeff.negate();
			}
		Collection<Constant> values=vars.values();
		// Removes any variables to the 0th power.
		while(values.remove(new Constant()));
		// If this contains a zero base in undistr, sets it to 0
		if(undistr.containsKey(Expression.ZERO))
		{
			coeff=new Constant();
			vars=new TreeMap<Character,Constant>();
			undistr=new TreeMap<Expression,Expression>();
		}
	}

	/**
	 * Checks to see if two terms are like Terms.
	 * @param a A Term to check if is like.
	 * @param b The other Term to check if is like.
	 * @return If the Terms are like, true, else false.
	 */
	public static boolean isLikeTerm(Term a,Term b)
	{
		return a.vars.equals(b.vars)&&a.undistr.equals(b.undistr)&&a.coeff.roots.equals(b.coeff.roots);
	}

	/**
	 * Adds exponent to the current exponent of var. simplifyTerm() may need to be called after this.
	 * @param var The variable to change the exponent of
	 * @param exponent The exponent to be added to var.
	 */
	void addExponent(char var,Constant exponent)
	{
		vars.merge(var,exponent,Constant::add);
	}

	/**
	 * Adds all of the value in toAdd to the value of the corresponding key
	 * @param toAdd values to be added
	 */
	void addExponents(Map<Character,Constant> toAdd)
	{
		for(Entry<Character,Constant> current:toAdd.entrySet())
			addExponent(current.getKey(),current.getValue().clone());
		simplifyTerm();
	}

	/**
	 * Adds power to the current exponent of base. simplifyTerm() may need to be called after this.
	 * @param base The expression to change the exponent of
	 * @param power The exponent to be added to base.
	 */
	void addExponent(Expression base,Expression power)
	{
		undistr.merge(base,power,Expression::add);
	}

	/**
	 * Adds all of the values in toAdd to the value of the corresponding key
	 * @param toAdd values to be added
	 */
	void addExponent(Map<Expression,Expression> toAdd)
	{
		for(Entry<Expression,Expression> current:toAdd.entrySet())
			addExponent(current.getKey().clone(),current.getValue().clone());
		simplifyTerm();
	}

	/**
	 * Finds the greatest common denominator of two terms.
	 * @param a A Term to find the gcd of.
	 * @param b The other Term to find the gcd of.
	 * @return A Term which is the gcd of the a and b.
	 */
	public static Term gcd(Term a,Term b)
	{
		TreeMap<Character,Constant> var=new TreeMap<Character,Constant>();
		TreeMap<Expression,Expression> undis=new TreeMap<Expression,Expression>();
		// Goes through all the variables in each term and finds variables that are the same and adds the highest power to var.
		if(a.vars.size()!=0&&b.vars.size()!=0)
		{
			Iterator<Entry<Character,Constant>> aVar=a.vars.entrySet().iterator();
			Iterator<Entry<Character,Constant>> bVar=b.vars.entrySet().iterator();
			Entry<Character,Constant> currentB=bVar.next();
			// Takes advantage of the fact that both are sorted. Loops through a, advancing b until its value is greater than or equal a's
			do
			{
				Entry<Character,Constant> currentA=aVar.next();
				// Advances b until its value is greater than or equal to a's
				while(bVar.hasNext()&&currentA.getKey()>currentB.getKey())
					currentB=bVar.next();
				// If both variables are equal, adds the lowest to var.
				if(currentA.getKey()==currentB.getKey())
					var.put(currentA.getKey(),currentA.getValue().compareTo(currentB.getValue())>0?currentB.getValue():currentA.getValue());
			}while(aVar.hasNext());
		}
		// Does the same as above, except for undistr
		if(a.undistr.size()!=0&&b.undistr.size()!=0)
		{
			Iterator<Entry<Expression,Expression>> aUn=a.undistr.entrySet().iterator();
			Iterator<Entry<Expression,Expression>> bUn=b.undistr.entrySet().iterator();
			Entry<Expression,Expression> curB=bUn.next();
			do
			{
				Entry<Expression,Expression> curA=aUn.next();
				while(bUn.hasNext()&&curA.getKey().compareTo(curB.getKey())>0)
					curB=bUn.next();
				if(curA.getKey().equals(curB.getKey()))
					undis.put(curA.getKey(),Expression.gcd(curA.getValue(),curB.getValue()));
			}while(aUn.hasNext());
		}
		return new Term(Constant.gcd(a.coeff,b.coeff),var,undis);
	}

	/**
	 * Gets the greatest common denominator of a Term array.
	 * @param a the Term array to find the gcd of.
	 * @return a Term which is the gcd of a.
	 */
	public static Term gcd(Term[] a)
	{
		Term newTerm=a[0];
		for(int i=1;i<a.length;i++)
			newTerm=Term.gcd(a[i],newTerm);
		return newTerm;
	}

	/**
	 * Gets the greatest common denominator of a list of Term.
	 * @param a the Term array to find the gcd of.
	 * @return a Term which is the gcd of a.
	 */
	public static Term gcd(Iterable<Term> a)
	{
		Iterator<Term> i=a.iterator();
		if(!i.hasNext())
			return new Term(Constant.ONE);
		Term newTerm=i.next();
		while(i.hasNext())
			newTerm=Term.gcd(i.next(),newTerm);
		return newTerm;
	}

	/**
	 * Checks if two Terms equal each other.
	 * @param a Term to check if this is equal to.
	 * @return If the Terms are equal, true, else false.
	 */
	@Override public boolean equals(Object other)
	{
		if(other instanceof Term)
		{
			Term a=(Term)other;
			return coeff.equals(a.coeff)&&Term.isLikeTerm(this,a);
		}else if(other instanceof Expression)
		{
			Expression a=(Expression)other;
			return a.terms.size()==1&&a.terms.get(0).equals(this);
		}else if(other instanceof Constant)
			return isConstant()&&coeff.equals(other);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode()
	{
		return undistr.hashCode()+vars.hashCode()+coeff.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override public Term clone()
	{
		Term a=new Term(coeff.clone());
		a.addExponents(vars);
		a.addExponent(undistr);
		return a;
	}

	/**
	 * Checks if the Term is a constant
	 * @return if the Term is an constant, true, else false.
	 */
	public boolean isConstant()
	{
		return vars.size()==0&&undistr.size()==0&&coeff.isRat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override public int compareTo(Term o)
	{
		// Compares terms in a somewhat arbitrary order, first by which has the highest power, then by each power, then by undistr.
		Constant varMax=vars.isEmpty()?null:Collections.max(vars.values());
		Constant oVarMax=o.vars.isEmpty()?null:Collections.max(o.vars.values());
		if(varMax!=null)
		{
			int compare=varMax.compareTo(oVarMax);
			if(compare>0)
				return -1;
			if(compare<0)
				return 1;
			if(o.vars.size()>vars.size())
				return 1;
			Iterator<Entry<Character,Constant>> oVar=o.vars.entrySet().iterator();
			Entry<Character,Constant> oCurrent;
			for(Entry<Character,Constant> current:vars.entrySet())
			{
				if(!oVar.hasNext())
					return -1;
				oCurrent=oVar.next();
				if(current.getKey()<oCurrent.getKey())
					return -1;
				if(current.getKey()>oCurrent.getKey())
					return 1;
				if(current.getValue().compareTo(oCurrent.getValue())<0)
					return -1;
				if(current.getValue().compareTo(oCurrent.getValue())>0)
					return 1;
			}
			if(oVar.hasNext())
				return 1;
		}else if(oVarMax!=null)
			return 1;
		if(o.undistr.size()>undistr.size())
			return 1;
		Iterator<Entry<Expression,Expression>> oUndis=o.undistr.entrySet().iterator();
		for(Entry<Expression,Expression> current:undistr.entrySet())
		{
			if(!oUndis.hasNext())
				return -1;
			Entry<Expression,Expression> oCurrent=oUndis.next();
			if(current.getKey().compareTo(oCurrent.getKey())<0)
				return -1;
			if(current.getKey().compareTo(oCurrent.getKey())>0)
				return 1;
			if(current.getValue().compareTo(oCurrent.getValue())<0)
				return -1;
			if(current.getValue().compareTo(oCurrent.getValue())>0)
				return 1;
		}
		return coeff.compareTo(o.coeff)>0?-1:coeff.compareTo(o.coeff)<0?1:0;
	}

	/**
	 * Multiplies two Terms.
	 * @param a A Term to multiply.
	 * @param b The other Term to multiply.
	 * @return A Term which is the product of a and b.
	 */
	public Term multiply(Term b)
	{
		Term a=clone();
		a.coeff=a.coeff.multiply(b.coeff);
		a.addExponents(b.vars);
		a.addExponent(b.undistr);
		return a;
	}

	/**
	 * @param divisor What this is to be divided by
	 * @return this/divisor
	 */
	public Term divide(Term divisor)
	{
		return multiply(divisor.negate());
	}

	/**
	 * @return -this
	 */
	public Term negate()
	{
		Term c=clone();
		c.coeff=c.coeff.negate();
		return c;
	}

	/**
	 * @return 1/this
	 */
	public Term invert()
	{
		return raise(Constant.NEGATE);
	}

	/**
	 * Raises a Term to a Constant
	 * @param a the term to be the base
	 * @param b the constant to be the exponent
	 * @return a<sup>b</sup>
	 */
	public Term raise(Constant b)
	{
		Term a=clone();
		//raises all parts of this to b
		a.coeff=a.coeff.raise(b);
		for(Entry<Character,Constant> current:a.vars.entrySet())
			current.setValue(current.getValue().multiply(b));
		for(Entry<Expression,Expression> current:a.undistr.entrySet())
			current.setValue(current.getValue().multiply(new Expression(new Term(b))));
		a.simplifyTerm();
		return a;
	}

	/**
	 * Gets all variables in this.
	 * @return A HashSet with the variables in this, no duplicates.
	 */
	public HashSet<Character> getVars()
	{
		HashSet<Character> var=new HashSet<Character>(vars.keySet());
		for(Expression current:undistr.keySet())
			for(Term curTerm:current.terms)
				var.addAll(curTerm.vars.keySet());
		var.remove(interImag);
		var.remove(interE);
		var.remove(PI);
		return var;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		StringBuffer output=new StringBuffer();
		if(coeff.compareTo(new Constant())>=0)
			output.append("+");
		boolean isConst=isConstant();
		// If the numerator of this term is not 1 or -1, or this term is just 1 or -1, add the numerator of coeff to the start of this term.
		// If the numerator is -1 and there are other variables, add a minus sign to the start.
		if(coeff.numerator.equals(BigInteger.ONE.negate())&&!isConst)
			output.append("-");
		else if(!coeff.numerator.equals(BigInteger.ONE)||isConst)
			output.append(coeff.numerator);
		// Goes through the roots of the coeff, generates the proper char(s) for it and adds them to output.
		for(Entry<Integer,Constant> current:coeff.roots.entrySet())
		{
			if(current.getKey()==2)
				output.append("\u221a");
			else if(current.getKey()==3)
				output.append("\u221b");
			else if(current.getKey()==4)
				output.append("\u221c");
			else
				output.append((char)(8304+current.getKey())+"\u221a");
			output.append('(').append(current.getValue()).append(')');
		}
		// Adds all the variables to the output
		for(Entry<Character,Constant> current:vars.entrySet())
		{
			output.append(current.getKey());
			if(!current.getValue().equals(Constant.ONE))
				output.append("^").append(current.getValue());
		}
		// Goes through all the undistr, and depending on if the base or power isn't a single number or variable, adds parentheses around it.
		for(Entry<Expression,Expression> current:undistr.entrySet())
		{
			Term base=current.getKey().terms.get(0);
			if(current.getKey().terms.size()==1&&base.coeff.denominator.equals(BigInteger.ONE)&&(base.coeff.numerator.equals(BigInteger.ONE)||base.vars.size()==0)&&base.undistr.size()==0)
				output.append(current.getKey()).append('^');
			else
				output.append('(').append(current.getKey()).append(")^");
			Term pow=current.getValue().terms.get(0);
			if(current.getValue().terms.size()==1&&pow.coeff.denominator.equals(BigInteger.ONE)&&(pow.coeff.numerator.equals(BigInteger.ONE)||pow.vars.size()==0)&&pow.undistr.size()==0)
				output.append(current.getValue());
			else
				output.append('(').append(current.getValue()).append(")");
		}
		if(!coeff.denominator.equals(BigInteger.ONE))
			output.append("/").append(coeff.denominator);
		// Replaces all of the substitute for the imaginary unit and e with the characters that represent it.
		return output.toString().replace(String.valueOf(interImag),IMAG_UNIT).replace(String.valueOf(interE),E);
	}

	/**
	 * Returns a String representation of this, but with decimals rounded to places rather than fractions.
	 * @param places Number of places to round to.
	 * @return The approximation of this.
	 */
	public String toStringDecimal(int places)
	{
		NumberFormat format=NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(places);
		StringBuffer output=new StringBuffer();
		if(coeff.compareTo(new Constant())>=0)
			output.append("+");
		boolean isConst=isConstant();
		// If the numerator of this term is not 1 or -1, or this term is just 1 or -1, add the numerator of coeff to the start of this term.
		// If the numerator is -1 and there are other variables, add a minus sign to the start.
		if(coeff.numerator.equals(BigInteger.ONE.negate())&&!isConst&&coeff.denominator.equals(BigInteger.ONE))
			output.append("-");
		else if(!coeff.numerator.equals(BigInteger.ONE)||isConst)
			output.append(format.format(coeff.doubleValue()));
		// Goes through the roots of the coeff, generates the proper char(s) for it and adds them to output.
		for(Entry<Integer,Constant> current:coeff.roots.entrySet())
		{
			switch(current.getKey())
			{
				case 2:
					output.append("\u221a");
					break;
				case 3:
					output.append("\u221b");
					break;
				case 4:
					output.append("\u221c");
					break;
				default:
					output.append((char)(8304+current.getKey())+"\u221a");
			}
			output.append('(').append(current.getValue()).append(')');
		}
		// Adds all the variables to the output
		for(Entry<Character,Constant> current:vars.entrySet())
		{
			output.append(current.getKey());
			if(!current.getValue().equals(Constant.ONE))
				output.append("^").append(current.getValue());
		}
		// Goes through all the undistr, and depending on if the base or power isn't a single number or variable, adds parentheses around it.
		for(Entry<Expression,Expression> current:undistr.entrySet())
		{
			Term base=current.getKey().terms.get(0);
			if(current.getKey().terms.size()==1&&base.coeff.denominator.equals(BigInteger.ONE)&&(base.coeff.numerator.equals(BigInteger.ONE)||base.vars.size()==0)&&base.undistr.size()==0)
				output.append(current.getKey().toStringDecimal(places)).append('^');
			else
				output.append('(').append(current.getKey().toStringDecimal(places)).append(")^");
			Term pow=current.getValue().terms.get(0);
			if(current.getValue().terms.size()==1&&pow.coeff.denominator.equals(BigInteger.ONE)&&(pow.coeff.numerator.equals(BigInteger.ONE)||pow.vars.size()==0)&&pow.undistr.size()==0)
				output.append(current.getValue().toStringDecimal(places));
			else
				output.append('(').append(current.getValue().toStringDecimal(places)).append(")");
		}
		// Replaces all of the substitute for the imaginary unit with the characters that represent it.
		return output.toString().replace(String.valueOf(interImag),IMAG_UNIT);
	}

	/**
	 * @return This but with \u03c0, e and all roots approximated to decimals.
	 */
	public Term approx()
	{
		Term retrn=clone();
		TreeMap<Expression,Expression> m=new TreeMap<Expression,Expression>();
		//Goes through eache undistr and approxiamtes it.
		retrn.undistr.forEach((base,pow)->{
			Expression b=base.approx();
			Expression p=pow.approx();
			if(b.isConstant()&&p.isConstant())
				retrn.coeff=retrn.coeff.multiply(new Constant(Math.pow(b.terms.get(0).coeff.doubleValue(),p.terms.get(0).coeff.doubleValue())));
			else
				m.put(b,p);
		});
		retrn.undistr=m;
		retrn.simplifyTerm();
		//double to be multiplied into this.
		double appro=1;
		//multiplies approximation of \u03c0 and e into appro
		Constant power=retrn.vars.remove(PI);
		if(power!=null)
			appro*=Math.pow(Math.PI,power.doubleValue());
		power=retrn.vars.remove(interE);
		if(power!=null)
			appro*=Math.pow(Math.E,power.doubleValue());
		//approximates all of the roots.
		for(Map.Entry<Integer,Constant> cur:retrn.coeff.roots.entrySet())
			appro*=Math.pow(cur.getValue().doubleValue(),1./cur.getKey());
		retrn.coeff.roots.clear();
		retrn.coeff=retrn.coeff.multiply(new Constant(appro));
		return retrn;
	}
}// Glory to God
