package lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class representing a mathematical term.
 * 
 * @author Luke Senseney
 * 
 */
public class Term implements Comparable<Term>
{
	/**
	 * The coefficient of the Term.
	 */
	public Constant coeff=Constant.ONE.clone();
	/**
	 * A Map of variables and their powers in it. Each key is raised to its value and multiplied into this.
	 */
	public TreeMap<Character,Constant> vars;
	/**
	 * A Map to represent things which can't be distributed, such as (x+3)^(1/2), each key is raised to its 
	 * value and then multiplied into this.
	 */
	public TreeMap<Expression,Expression> undistr;
	/**
	 * Term with no Variables, and a coefficient of -1.
	 */
	public static Term NEGATE=new Term(Constant.NEGATE);
	
	//public HashSet<Expression> restrictions=new HashSet<Expression>();

	/**
	 * Creates a new Term from a String. Garbage in, Garbage out; if the String is not a correctly
	 * formated Term, will attempt to read.
	 * 
	 * @param newTerm the String to make a Term from.
	 */
	public Term(String newTerm)
	{// start constructor
		vars=new TreeMap<Character,Constant>();
		undistr=new TreeMap<Expression,Expression>();
		int inParentheses=0;
		boolean inverse=false,raised=false,pasRaise=false;
		String base="",power="";
		char var='\0';
		Expression finalPow;
		for(char current:newTerm.toCharArray())
		{
			if(current=='(')
				inParentheses++;
			if(raised)
				if(current=='^'||power.equals("-")||power.equals("")||General.hasNumber(current)&&!pasRaise||inParentheses!=0)
				{
					if(!General.hasNumber(current))
						pasRaise=true;
					if(current=='^')
						pasRaise=false;
					power+=current;
				}else
				{
					finalPow=new Expression(power);
					if(finalPow.isConstant())
						if(base!="")
						{
							Constant multi=new Constant(Double.valueOf(base));
							multi.raise(finalPow.terms.get(0).coeff);
							if(!inverse)
								coeff.multiply(multi);
							else
								coeff.divide(multi);
							if(base.charAt(0)=='-'&&Math.pow(Double.valueOf(base),Double.valueOf(base))>0)
								coeff.numerator-=coeff.numerator;
							base="";
						}else
						{
							if(inverse)
								finalPow.terms.get(0).coeff.numerator-=finalPow.terms.get(0).coeff.numerator;
							addExponent(var,finalPow.terms.get(0).coeff);
							var='\0';
						}
					else
						if(base!="")
						{
							undistr.put(new Expression(base),new Expression(inverse?"-("+power+')':power));
							base="";
						}else
						{
							undistr.put(new Expression(String.valueOf(var)),new Expression(inverse?"-("+power+')':power));;
							var='\0';
						}
					power="";
					raised=false;
				}
			if(!raised)
				if(General.hasNumber(current))
					base+=current;
				else
					if(current=='^')
						raised=true;
					else
					{
						if(!base.equals(""))
						{
							if(base.equals("-"))
								base="-1";
							if(!inverse)
								coeff.multiply(new Constant(Double.valueOf(base)));
							else
								coeff.divide(new Constant(Double.valueOf(base)));
							inverse=false;
							base="";
						}
						if(var!='\0')
						{
							addExponent(var,inverse?Constant.NEGATE.clone():Constant.ONE.clone());
							var='\0';
						}
						var=!(current=='*'||current=='/')?current:'\0';
					}
			if(current==')')
				inParentheses--;
			if((current=='/'||current=='*')&&inParentheses==0)
				inverse=current=='/';
		}
		if(!power.equals("")||!base.equals(""))
			if(raised)
			{
				if(power.equals("-"))
					power="-1";
				finalPow=new Expression(power);
				if(finalPow.isConstant())
					if(base!="")
					{
						Constant multi=new Constant(Double.valueOf(base));
						multi.raise(finalPow.terms.get(0).coeff);
						if(!inverse)
							coeff.multiply(multi);
						else
							coeff.divide(multi);
						if(base.charAt(0)=='-'&&Math.pow(Double.valueOf(base),finalPow.terms.get(0).coeff.doubleValue())>0)
							coeff.numerator=-coeff.numerator;
					}else
					{
						if(inverse)
							finalPow.terms.get(0).coeff.numerator=-finalPow.terms.get(0).coeff.numerator;
						addExponent(var,finalPow.terms.get(0).coeff);
						var='\0';
					}
				else
					if(base!="")
						addExponent(new Expression(base),new Expression(inverse?"-("+power+')':power));
					else
					{
						addExponent(new Expression(String.valueOf(var)),new Expression(inverse?"-("+power+')':power));
						var='\0';
					}
			}else
				if(!inverse)
					coeff.multiply(new Constant(Double.valueOf(base)));
				else
					coeff.divide(new Constant(Double.valueOf(base)));
		if(var!='\0')
			addExponent(var,new Constant(inverse?-1:1));
		simplifyTerm();
	}

	/**
	 * Creates a new Term from a Constant and an map of variables.
	 * 
	 * @param newCoeff Constant that becomes the coefficient of the Term.
	 * @param newVars Map that becomes the variables of the Term.
	 */
	public Term(Constant newCoeff,Map<Character,Constant> newVars)
	{
		this(newCoeff,newVars,new TreeMap<Expression,Expression>());
	}

	/**
	 * Creates a new Term from a Constant.
	 * 
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
	 * 
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
	 * Combines variables and bases that are the same and adds powers of these.
	 */
	public void simplifyTerm()
	{// start simplifyTerm
		Term toBe=new Term(new Constant(1));
		boolean removed=false;
		Iterator<Entry<Expression,Expression>> iter=undistr.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Expression,Expression> current=iter.next();
			if(current.getKey().terms.size()==1&&current.getValue().isConstant())
			{
				toBe=multiply(toBe,Term.raise(current.getKey().terms.get(0),current.getValue().terms.get(0).coeff));
				iter.remove();
				removed=true;
			}
		}
		Collection<Expression> removeFrom=undistr.values();
		while(removeFrom.remove(Expression.ZERO));
		if(removed)
		{
			toBe=multiply(toBe,this);
			coeff=toBe.coeff;
			vars=toBe.vars;
			undistr=toBe.undistr;
		}
		Constant inRoot=coeff.roots.get(new Constant(2));
		if(inRoot!=null&&inRoot.numerator<0)
		{
			coeff.roots.get(new Constant(2)).numerator*=-1;
			addExponent('\u05D0',Constant.ONE);
		}
		Constant imaginary=vars.get('\u05D0');
		if(imaginary!=null)
			switch((int)imaginary.numerator%4)
			{
				case 0:
					imaginary.numerator=0;
					break;
				case 1:
					imaginary.numerator=1;
					break;
				case 2:
					imaginary.numerator=0;
					coeff.multiply(Constant.NEGATE);
					break;
				case 3:
					imaginary.numerator=1;
					coeff.multiply(Constant.NEGATE);
			}
		Collection<Constant> values=vars.values();
		while(values.remove(new Constant()));
		if(undistr.containsKey(Expression.ZERO))
		{
			coeff=new Constant();
			vars=new TreeMap<Character,Constant>();
			undistr=new TreeMap<Expression,Expression>();
		}
	}

	/**
	 * Checks to see if two terms are like Terms.
	 * 
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
	 * 
	 * @param var The variable to change the exponent of
	 * @param exponent The exponent to be added to var.
	 */
	void addExponent(char var,Constant exponent)
	{
		Constant wasThere=vars.put(var,exponent);
		if(wasThere!=null)
		{
			exponent.add(wasThere);
/*			if(wasThere.numerator<0&&exponent.numerator>0)
				restrictions.add(new Expression(new Term(var)));*/
		}
	}

	/**
	 * Adds all of the value in toAdd to the value of the corresponding key
	 * 
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
	 * 
	 * @param base The expression to change the exponent of
	 * @param power The exponent to be added to base.
	 */
	void addExponent(Expression base,Expression power)
	{
		Expression wasThere=undistr.put(base,power);
		if(wasThere!=null)
		{
			power.terms.addAll(wasThere.terms);
			power.simplifyTerms();
		}
	}

	/**
	 * Adds all of the values in toAdd to the value of the corresponding key
	 * 
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
	 * 
	 * @param a A Term to find the gcd of.
	 * @param b The other Term to find the gcd of.
	 * @return A Term which is the gcd of the a and b.
	 */
	public static Term gcd(Term a,Term b)
	{
		TreeMap<Character,Constant> var=new java.util.TreeMap<Character,Constant>();
		TreeMap<Expression,Expression> undis=new TreeMap<Expression,Expression>();
		if(a.vars.size()!=0&&b.vars.size()!=0)
		{
			Iterator<Entry<Character,Constant>> aVar=a.vars.entrySet().iterator();
			Iterator<Entry<Character,Constant>> bVar=b.vars.entrySet().iterator();
			Entry<Character,Constant> currentB=bVar.next();
			do
			{
				Entry<Character,Constant> currentA=aVar.next();
				while(bVar.hasNext()&&currentA.getKey()>currentB.getKey())
					currentB=bVar.next();
				if(currentA.getKey()==currentB.getKey())
					var.put(currentA.getKey(),currentA.getValue().compareTo(currentB.getValue())>0?currentB.getValue():currentA.getValue());
			}while(aVar.hasNext());
		}
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
	 * 
	 * @param a the Term array to find the gcd of.
	 * @return a Term which is the gcd of a.
	 */
	public static Term gcd(Term[] a)
	{
		Term newTerm=a[0];
		for(Term current:Arrays.copyOfRange(a,1,a.length))
			newTerm=Term.gcd(current,newTerm);
		return newTerm;
	}

	/**
	 * Gets the greatest common denominator of a list of Term.
	 * 
	 * @param a the Term array to find the gcd of.
	 * @return a Term which is the gcd of a.
	 */
	public static Term gcd(Iterable<Term> a)
	{
		Term newTerm=null;
		for(Term current:a)
			if(newTerm==null)
				newTerm=current;
			else
				newTerm=Term.gcd(current,newTerm);
		return newTerm==null?new Term(new Constant()):newTerm;
	}

	/**
	 * Checks if two Terms equal each other.
	 * 
	 * @param a Term to check if this is equal to.
	 * @return If the Terms are equal, true, else false.
	 */
	@Override public boolean equals(Object other)
	{
		Term a=(Term)other;
		return coeff.equals(a.coeff)&&Term.isLikeTerm(this,a);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode()
	{
		return undistr.hashCode()+vars.hashCode()+coeff.hashCode();
	}

	/*
	 * (non-Javadoc)
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
	 * 
	 * @return if the Term is an constant, true, else false.
	 */
	public boolean isConstant()
	{
		return vars.size()==0&&undistr.size()==0&&coeff.isRat();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override public int compareTo(Term o)
	{
		Constant varMax=General.max(vars.values());
		Constant oVarMax=General.max(o.vars.values());
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
		}else
			if(oVarMax!=null)
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
	 * 
	 * @param a A Term to multiply.
	 * @param b The other Term to multiply.
	 * @return A Term which is the product of a and b.
	 */
	public static Term multiply(Term a,Term b)
	{
		a=a.clone();
		a.coeff.multiply(b.coeff);
		a.addExponents(b.vars);
		a.addExponent(b.undistr);
		return new Term(a.coeff,a.vars,a.undistr);
	}
	
	/**
	 * Raises a Term to a Constant
	 * @param a the term to be the base
	 * @param b the constant to be the exponent
	 * @return a<sup>b</sup>
	 */
	public static Term raise(Term a,Constant b)
	{
		a=a.clone();
		a.coeff.raise(b);
		for(Constant current:a.vars.values())
			current.multiply(b);
		for(Entry<Expression,Expression> current:a.undistr.entrySet())
			current.setValue(new Expression(Expression.distribute(current.getValue(),new Expression(new Term(b)))));
		return a;
	}

	/**
	 * Gets all variables in this.
	 * 
	 * @return A HashSet with the variables in this, no duplicates.
	 */
	public HashSet<Character> getVars()
	{
		HashSet<Character> var=new HashSet<Character>(vars.keySet());
		for(Expression current:undistr.keySet())
			for(Term curTerm:current.terms)
				var.addAll(curTerm.vars.keySet());
		return var;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		String output="";
		if(coeff.compareTo(new Constant())>=0)
			output+="+";
		boolean isConst=isConstant();
		if(coeff.numerator==-1&&!isConst)
				output+="-";
		else if(coeff.numerator!=1||isConst)
			output+=coeff.numerator;
		for(Entry<Integer,Constant> current:coeff.roots.entrySet())
		{
			if(current.getKey()==2)
				output+="\u221a";
			else if(current.getKey()==3)
				output+="\u221b";
			else if(current.getKey()==4)
				output+="\u221c";
			else
				output+=(char)(8304+current.getKey())+"\u221a";
			output+='('+current.getValue().toString()+')';
		}
		for(Entry<Character,Constant> current:vars.entrySet())
		{
			output+=current.getKey();
			if(!current.getValue().equals(Constant.ONE))
				output+="^"+current.getValue();
		}
		for(Entry<Expression,Expression> current:undistr.entrySet())
			if(current.getKey().terms.size()>1)
				output+='('+current.getKey().toString()+")^"+(current.getValue().terms.size()==1?current.getValue().toString():"("+current.getValue()+')');
			else
				output+=current.getKey().toString()+'^'+(current.getValue().terms.size()==1?current.getValue().toString():"("+current.getValue()+')');
		if(coeff.denominator!=1)
			output+="/"+coeff.denominator;
		output=output.replace("\u05D0",new String(Character.toChars(120050)));
		return output;
	}
}// Glory to God