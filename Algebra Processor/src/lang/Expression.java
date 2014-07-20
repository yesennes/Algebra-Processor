package lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class that represents a mathematical expression, or a equation if isEquation is true.
 * 
 * @author Luke Senseney
 * 
 */
public class Expression implements Comparable<Expression>
{
	/**
	 * Term array that the sum of is this expression, or are equal to 0 if isEquation.
	 */
	public ArrayList<Term> terms;
	/**
	 * If true, it is treated like a equation with the array of Terms set equal to 0. Default is false.
	 */
	public boolean isEquation=false;
	/**
	 * Expression with no terms
	 */
	public static final Expression ZERO=new Expression();
	/**
	 * Expression with a single term, 1
	 */
	public static final Expression ONE=new Expression(new Term(Constant.ONE));
	/**
	 * Expression with a single term, -1
	 */
	public static final Expression NEGATE=new Expression(Term.NEGATE);

	/**
	 * Creates a new Expression from a string. If there is a "=" in the string isEquation is true. Cannot handle equations with more than one "=" i.e. a=b=c. Garbage in, Garbage out, if the String is not a correctly formated expression or equation,
	 * will attempt to read or throw exception.
	 * 
	 * @param newEquation The string to make a equation from.
	 */
	public Expression(String newEquation)
	{
		newEquation=newEquation.replace(new String(Character.toChars(120050)),"\u05D0");
		terms=new ArrayList<Term>(5);
		int inParentheses=0;
		boolean leftSide=true,inverse=false,raised=false,wasParen=false,inParen=false,pastParen=false;
		String power="",coeff="",paren="";
		char charBefore='\0';
		for(char current:newEquation.toCharArray())
		{
			if((current=='+'||current=='-'||current=='=')&&inParentheses==0&&charBefore!='/'&&charBefore!='*'&&charBefore!='^')
			{
				if(charBefore!='+'&&charBefore!='-'&&charBefore!='='&&charBefore!='\0')
				{
					if(coeff.startsWith("/"))
						coeff='1'+coeff;
					if(!leftSide)
						coeff='-'+coeff;
					if(paren.equals(""))
						terms.add(new Term(coeff));
					else
						if(power.equals(""))
							if(coeff.equals(""))
								terms.addAll((!inverse?new Expression(paren).terms:raise(new Expression(paren),NEGATE)));
							else
								terms.addAll(distribute(!inverse?new Expression(paren):new Expression(raise(new Expression(paren),NEGATE)),new Expression(coeff)));
						else
							if(coeff.equals(""))
								terms.addAll(!inverse?raise(new Expression(paren),new Expression(power)):raise(new Expression(paren),new Expression(distribute(new Expression(power),NEGATE))));
							else
								terms.addAll(distribute(new Expression(raise(new Expression(paren),!inverse?new Expression(power):new Expression(distribute(new Expression(power),NEGATE)))),
										new Expression(coeff)));
					coeff="";
					power="";
					paren="";
					inverse=false;
					raised=false;
					wasParen=false;
					inParen=false;
					pastParen=false;
				}
				if(current=='-')
					coeff+='-';
				if(current=='=')
				{
					leftSide=false;
					isEquation=true;
				}
			}else
				if(raised)
					if((current=='*'||current=='/')&&inParentheses==0)
					{
						raised=false;
						coeff=coeff+current;
					}else
					{
						if(current=='(')
							inParentheses++;
						else
							if(current==')')
								inParentheses--;
						power=power+current;
					}
				else
					if(!inParen)
					{
						if(current=='(')
						{
							if(charBefore!='^'&&inParentheses==0&&!pastParen)
							{
								inParen=true;
								if(charBefore=='/')
									inverse=true;
							}else
								coeff=coeff+current;
							inParentheses++;
						}else
							if(current=='^'&&charBefore==')'&&wasParen)
								raised=true;
							else
							{
								if(current==')')
									inParentheses--;
								coeff=coeff+current;
							}
						wasParen=false;
					}else
						if(current==')')
						{
							inParentheses--;
							if(inParentheses!=0)
								paren=paren+current;
							else
							{
								wasParen=true;
								inParen=false;
								pastParen=true;
							}
						}else
						{
							if(current=='(')
								inParentheses++;
							paren=paren+current;
						}
			charBefore=current;
		}
		if(coeff.startsWith("/"))
			coeff='1'+coeff;
		if(paren.equals(""))
		{
			Term toAdd=new Term(coeff);
			if(!leftSide)
				toAdd.coeff.numerator*=-1;
			terms.add(toAdd);
		}else
		{
			ArrayList<Term> toAdd;
			if(power.equals(""))
				if(coeff.equals(""))
					toAdd=!inverse?new Expression(paren).terms:raise(new Expression(paren),NEGATE);
				else
					toAdd=distribute(!inverse?new Expression(paren):new Expression(raise(new Expression(paren),NEGATE)),new Expression(coeff));
			else
				if(coeff.equals(""))
					toAdd=raise(new Expression(paren),!inverse?new Expression(power):new Expression(distribute(new Expression(power),NEGATE)));
				else
					toAdd=raiseAndDistribute(new Expression(paren),new Expression(coeff),!inverse?new Expression(power):new Expression(distribute(new Expression(power),NEGATE)));
			if(!leftSide)
				toAdd=distribute(toAdd,NEGATE.terms);
			terms.addAll(toAdd);
		}
		simplifyTerms();
	}

	/**
	 * Creates a new Expression from a array of Terms
	 * 
	 * @param newTerms Terms to make a equation from.
	 */
	public Expression(Term...newTerms)
	{
		this(Arrays.asList(newTerms));
	}

	/**
	 * Creates a new Expression from a collection of Terms
	 * 
	 * @param newTerms Terms to make a equation from.
	 */
	public Expression(Collection<Term> newTerms)
	{
		terms=new ArrayList<Term>(newTerms);
		simplifyTerms();
	}

	/**
	 * Creates a new Expression from a array of Terms, is a equation if equation is true.
	 * 
	 * @param newTerms Terms to make a equation from.
	 * @param equation Sets isEquation to this.
	 */
	public Expression(Collection<Term> newTerms,boolean equation)
	{
		this(newTerms);
		isEquation=equation;
	}

	/**
	 * Raises an Expression to another Expression.
	 * 
	 * @param expression the base
	 * @param power the exponent
	 * @return expression<sup>power</sup>
	 */
	public static ArrayList<Term> raise(Expression expression,Expression power)
	{
		if(power.isConstant())
		{
			if(expression.terms.size()==1)
				// If power is constant and expression is a single Term, use
				// Term.raise(Term,Constant)
				return new Expression(Term.raise(expression.terms.get(0),power.terms.get(0).coeff)).terms;
			Constant pow=power.terms.get(0).coeff;
			if(pow.equals(new Constant()))
				// If power is 0 return 1
				return ONE.terms;
			// Keep a copy of the original, because expression will be set to another object
			Expression original=expression;
			// Multiply expression by itself one times 1 less than the absolute value of the numerator of pow.
			for(long i=Math.abs(pow.numerator);i>1;i--)
				expression=new Expression(distribute(expression,original));
			// If pow is negative or it has a denominator, this cannot be distributed, so it needs to be in the undistr of a Term. This packages
			// it into a term
			if(pow.numerator<0||pow.denominator>1)
			{
				TreeMap<Expression,Expression> d=new TreeMap<Expression,Expression>();
				// sets the numerator of pow to 1 or -1 depending on sign.
				pow.numerator=pow.numerator<0?-1:1;
				d.put(expression,new Expression(new Term(pow)));
				return new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),d)).terms;
			}
		}
		if(expression.terms.size()==1)
		{
			Term exp=expression.terms.get(0);
			// Goes from (a^b)^power to a^(b*power)
			for(Entry<Expression,Expression> current:exp.undistr.entrySet())
			{
				current.setValue(new Expression(distribute(current.getValue(),power)));
				current.getValue().simplifyTerms();
			}
			// If there is something to this term other than undistr, puts that in an undistr raised to power
			if(exp.vars.size()!=0&&!exp.coeff.equals(Constant.ONE))
				exp.undistr.put(new Expression(new Term(exp.coeff,expression.terms.get(0).vars)),power);
			// Essentially removes every thing in this term except the undistr, which has already been raised to power.
			return new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),exp.undistr)).terms;
		}
		// expression^pow could not be simplified in any manner. This just puts into the undistr of a new term.
		TreeMap<Expression,Expression> d=new TreeMap<Expression,Expression>(Collections.singletonMap(expression,power));
		return new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),d)).terms;
	}

	/**
	 * Raises an expression to another expression. It is the equivalent of
	 * 
	 * <code>return raise(new Expression(expression),new Expression(multiplier));</code>
	 * 
	 * @param expression the base
	 * @param power the exponent
	 * @return expression<sup>power</sup>
	 */
	public static ArrayList<Term> raise(Collection<Term> expression,Collection<Term> power)
	{
		return raise(new Expression(expression),new Expression(power));
	}

	/**
	 * Multiplies an Expression by another.
	 * 
	 * @param expression The main
	 * @param multiplier multiplies expression by this.
	 * @return (expression)*(multiplier)
	 */
	public static ArrayList<Term> distribute(Expression expression,Expression multiplier)
	{
		ArrayList<Term> retrn=new ArrayList<Term>(expression.terms.size()*multiplier.terms.size());
		// Multiplies each term in expression by each term in multiplier.
		for(Term exp:expression.terms)
			for(Term multi:multiplier.terms)
				retrn.add(Term.multiply(exp,multi));
		return retrn;
	}

	/**
	 * Multiplies an expression by another. It is the equivalent of
	 * 
	 * <code>distribute(new Expression(expression),new Expression(multiplier))<code>
	 * 
	 * @param expression The main
	 * @param multiplier multiplies expression by this.
	 * @return (expression)*(multiplier)
	 */
	public static ArrayList<Term> distribute(Collection<Term> expression,Collection<Term> multiplier)
	{
		return distribute(new Expression(expression),new Expression(multiplier));
	}

	/**
	 * Raises an Expression to another and then multiplies by another.
	 * 
	 * @param expression the base
	 * @param multiplier the expression the be multiplied.
	 * @param power the exponent
	 * @return (expression<sup>power</sup>)*multiplier
	 */
	public static ArrayList<Term> raiseAndDistribute(Expression expression,Expression multiplier,Expression power)
	{
		return distribute(new Expression(raise(expression,power)),multiplier);
	}

	/**
	 * Raises an expression to another and then multiplies by another. It is the equivalent of
	 * 
	 * <code>raiseAndDistribute(new Expression(expression),new Expression(multiplier),new Expression(power));<code>
	 * 
	 * @param expression the base
	 * @param multiplier the expression the be multiplied.
	 * @param power the exponent
	 * @return (expression<sup>power</sup>)*multiplier
	 */
	public static ArrayList<Term> raiseAndDistribute(Collection<Term> expression,Collection<Term> multiplier,Collection<Term> power)
	{
		return raiseAndDistribute(new Expression(expression),new Expression(multiplier),new Expression(power));
	}

	/**
	 * Gets the greatest common denominator of the gcd of the terms in each
	 * 
	 * @param a One Expression to find the gcd of.
	 * @param b The other Expression to find the gcd of.
	 * @return The greatest common denominator of the gcd of the terms in each
	 */
	public static Expression gcd(Expression a,Expression b)
	{
		//Factors each Expression
		ArrayList<Expression> faca=a.factor();
		ArrayList<Expression> facb=b.factor();
		Expression gcd=new Expression();
		//If both have a single Term factor, adds the gcd of that to gcd and removes those factors.
		if(faca.get(0).terms.size()==1&&facb.get(0).terms.size()==0)
		{
			gcd.terms.add(Term.gcd(faca.get(0).terms.get(0),facb.get(0).terms.get(0)));
			faca.remove(0);
			facb.remove(0);
		}
		//Finds all identical factors multiplies them into gcd
		for(Expression exp:faca)
		{
			int i=facb.indexOf(exp);
			if(i>-1)
			{
				gcd=new Expression(distribute(gcd,exp));
				facb.remove(i);
			}
		}
		return gcd;
	}

	/**
	 * Combines like Terms, removes 0 terms and distributes what it can.
	 */
	public void simplifyTerms()
	{
		// Finds all undistr in terms that can be distributed, removes them from the Term, and distributes them
		// and adds them to this.
		for(int i=0;i<terms.size();i++)
		{
			Iterator<Entry<Expression,Expression>> iter=terms.get(i).undistr.entrySet().iterator();
			while(iter.hasNext())
			{
				Entry<Expression,Expression> current=iter.next();
				if(current.getValue().isConstant()&&current.getValue().terms.get(0).coeff.numerator>1)
				{
					iter.remove();
					terms.addAll(raiseAndDistribute(current.getKey(),current.getValue(),new Expression(terms.get(i))));
				}
			}
		}
		// Combines like terms by checking each term against each after them.
		for(int i=0;i<terms.size();i++)
			for(int j=terms.size()-1;j>i;j--)
				try
				{
					if(Term.isLikeTerm(terms.get(j),terms.get(i)))
					{
						terms.get(i).coeff.add(terms.get(j).coeff);
						terms.remove(j);
					}
				}catch(DifferentRoots e)
				{}
		// Removes all terms with the coefficient of 0.
		terms.removeIf(t -> t.coeff.equals(new Constant()));
		Collections.sort(terms);
	}

	/**
	 * Factors out the gcd, and if the remaining expression is a quadratic, factors that with the quadratic formula.
	 * If the expression has a factor which is a single Term, it will be first in the ArrayList
	 * @return The factors of this. None will be equations.
	 */
	public ArrayList<Expression> factor()
	{
		ArrayList<Expression> ans=new ArrayList<Expression>();
		Term fact=Term.gcd(terms);
		for(Term current:terms)
		{
			for(Entry<Character,Constant> on:current.vars.entrySet())
				if(on.getValue().compareTo(new Constant())<0&&!(fact.vars.containsKey(on.getKey())&&fact.vars.get(on.getKey()).compareTo(on.getValue())>=0))
					fact.addExponent(on.getKey(),on.getValue());
			for(Entry<Expression,Expression> on:current.undistr.entrySet())
				if(on.getValue().isConstant())
				{
					Constant ons=on.getValue().terms.get(0).coeff;
					if(!fact.undistr.containsKey(on.getKey())||!fact.undistr.get(on.getKey()).isConstant())
						fact.addExponent(on.getKey(),on.getValue());
					else
						if(fact.undistr.get(on.getKey()).terms.get(0).coeff.compareTo(ons)<0)
							fact.undistr.put(on.getKey(),on.getValue());
				}
		}
		if(!fact.equals(new Term(new Constant(1))))
		{
			Expression add=new Expression(fact);
			ans.add(add);
		}
		Expression remaining=new Expression(Expression.raiseAndDistribute(new Expression(fact),clone(),Expression.NEGATE));
		final Constant two=new Constant(2);
		if(remaining.getDegree().equals(two)&&getVars().size()==1)
		{
			char factBy='\0';
			Iterator<Term> term=terms.iterator();
			while(term.hasNext()&&factBy=='\0')
			{
				Term current=term.next();
				Iterator<Entry<Character,Constant>> it=current.vars.entrySet().iterator();
				while(it.hasNext()&&factBy=='\0')
				{
					Entry<Character,Constant> var=it.next();
					if(var.getValue().equals(two))
						factBy=var.getKey();
				}
			}
			Expression a=new Expression(),b=new Expression(),c=new Expression();
			for(Term current:remaining.terms)
			{
				Term noVar=current.clone();
				Constant pow=noVar.vars.remove(factBy);
				if(pow==null)
					c.terms.add(noVar);
				else
					if(pow.equals(Constant.ONE))
						b.terms.add(noVar);
					else
						if(pow.equals(two))
							a.terms.add(noVar);
			}
			final Expression expTwo=new Expression(new Term(two));
			Expression bfourac=new Expression(raise(b,expTwo));
			ArrayList<Term> fourac=distribute(new Expression(new Term(new Constant(-4))),new Expression(distribute(a,c)));
			bfourac.terms.addAll(fourac);
			bfourac.simplifyTerms();
			Term discrim=new Term(Constant.ONE);
			discrim.addExponent(bfourac,new Expression(new Term(new Constant(1,2))));
			discrim.simplifyTerm();
			Expression plus=new Expression(),minus;
			plus.terms.addAll(b.terms);
			minus=plus.clone();
			plus.terms.add(discrim);
			minus.terms.add(Term.multiply(Term.NEGATE,discrim));
			plus.simplifyTerms();
			minus.simplifyTerms();
			plus=new Expression(raiseAndDistribute(distribute(expTwo,a),plus.terms,Expression.NEGATE.terms));
			minus=new Expression(raiseAndDistribute(distribute(expTwo,a),minus.terms,Expression.NEGATE.terms));
			plus.terms.add(new Term(factBy));
			minus.terms.add(new Term(factBy));
			plus.simplifyTerms();
			minus.simplifyTerms();
			ans.add(plus);
			ans.add(minus);
			return ans;
		}
		if(!remaining.equals(Expression.ONE)&&ans.size()>0)
			ans.add(remaining);
		return ans;
	}

	/**
	 * Solves the Expression for each variable as best as possible.
	 * 
	 * @return An array of solutions, one for each variable.
	 * @throws NotEquation If the Expression is not an Equation.
	 */
	public HashSet<Solution> solve() throws NotEquation
	{
		if(!isEquation)
			throw new NotEquation();
		HashSet<Solution> solutions=new HashSet<Solution>();
		ArrayList<Expression> facts=factor();
		Expression first=new Expression();
		for(Expression current:facts)
		{
			HashSet<Character> vars=getVars();
			vars.remove('\u05D0');
			for(char cur:vars)
				try
				{
					Expression s=current.solveFact(cur);
					boolean found=false;
					for(Solution find:solutions)
						if(find.letter==cur)
						{
							found=true;
							((Object)first).equals((Object)s);
							find.value.add(s);
						}
					if(!found)
					{
						first=s;
						solutions.add(new Solution(cur,s));
					}
				}catch(NotAbleToSolve e)
				{}
		}
		return solutions;
	}

	/**
	 * Solves the equation for the specified variable
	 * 
	 * @param iso The variable to solve for.
	 * @return The solution for iso
	 * @throws NotEquation If this isn't an equation.
	 */
	public Solution solveFor(char iso) throws NotEquation
	{
		if(!isEquation)
			throw new NotEquation();
		Solution s=new Solution(iso);
		ArrayList<Expression> factors=factor();
		for(Expression current:factors)
			try
			{
				s.value.add(current.solveFact(iso));
			}catch(NotAbleToSolve e)
			{
				s.allPossible=false;
			}
		return s;
	}

	private Expression solveFact(char iso) throws NotEquation,NotAbleToSolve
	{
		Constant firstPow=null,secondPow=null;
		boolean firstOther=false,secondOther=false;
		boolean inUndistr=false;
		for(Term current:terms)
		{
			Constant pow=current.vars.get(iso);
			boolean other=pow!=null&&(current.vars.size()>1||current.undistr.size()>0);
			if(pow==null)
				pow=new Constant();
			if(firstPow==null||pow.equals(firstPow))
			{
				firstPow=pow;
				firstOther=other||firstOther;
			}else
				if(secondPow==null||pow.equals(secondPow))
				{
					secondPow=pow;
					secondOther=other||secondOther;
				}else
					throw new NotAbleToSolve("The equation is to the second degree or higher.");
			for(Entry<Expression,Expression> exp:current.undistr.entrySet())
			{
				if(exp.getKey().getVars().contains(iso))
				{
					if(!pow.equals(new Constant()))
						throw new NotAbleToSolve(iso+"is multiplied by itself raised to variable or the root of the sum of itself and another expression");
					inUndistr=true;
				}
				if(exp.getValue().getVars().contains(iso))
					throw new NotAbleToSolve(iso+"is in an exponent. Logarithms will be added in future versions.");
			}
		}
		if(firstPow!=null&&secondPow!=null)
		{
			if(!firstPow.equals(new Constant())&&!secondPow.equals(new Constant()))
				throw new NotAbleToSolve("This equation requires factoring");
			if((!firstPow.equals(new Constant())||!secondPow.equals(new Constant()))&&inUndistr)
				throw new NotAbleToSolve("This equation has "+iso+" added to the sum it and something else, raised to something.");
		}
		ArrayList<Term> hasVar=new ArrayList<Term>(terms.size()/3+1);
		ArrayList<Term> noVar=new ArrayList<Term>(2*terms.size()/3+1);
		Constant root=Constant.ONE;
		for(Term currentTerm:terms)
			if(currentTerm.getVars().contains(new Character(iso)))
				hasVar.add(currentTerm);
			else
				noVar.add(currentTerm);
		Collections.sort(hasVar,new SortByChar(iso));
		ArrayList<Term> divide=new ArrayList<Term>(hasVar.size());
		for(Term now:hasVar)
		{
			Term a=now.clone();
			Constant r=a.vars.remove(iso);
			if(!r.equals(Constant.ONE))
				root=r;
			a.coeff.multiply(Constant.NEGATE);
			divide.add(a);
		}
		root.invert();
		if(root.equals(Constant.ONE))
			return new Expression(raiseAndDistribute(new Expression(divide),new Expression(noVar),NEGATE));
		else
			return new Expression(raise(new Expression(raiseAndDistribute(new Expression(divide),new Expression(noVar),NEGATE)),new Expression(new Term(root))));
	}

	/**
	 * Checks to see if this Expression is a single Term that is a constant.
	 * 
	 * @return If this is a constant true, else false.
	 */
	public boolean isConstant()
	{
		return terms.size()==1&&terms.get(0).isConstant();
	}

	/**
	 * Gets the variables in this.
	 * 
	 * @return All variables in this.
	 */
	public HashSet<Character> getVars()
	{
		HashSet<Character> vars=new HashSet<Character>();
		for(Term current:terms)
			vars.addAll(current.getVars());
		return vars;
	}

	/**
	 * Gets the degree of the equation, or the highest power of a variable.
	 * 
	 * @return the degree of the equation.
	 */
	public Constant getDegree()
	{
		Constant degree=new Constant();
		for(Term current:terms)
			for(Constant pow:current.vars.values())
				if(pow.compareTo(degree)>0)
					degree=pow;
		return degree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override public int compareTo(Expression o)
	{
		return toString().compareTo(o.toString());
	}

	/**
	 * Checks if this is the same expression or equation as a.
	 * 
	 * @param a Expression to check if this is the same as.
	 * @return If this is the same as a, true, else false.
	 */
	@Override public boolean equals(Object a)
	{
		try
		{
			Expression b=(Expression)a;
			return terms.equals(b.terms)&&isEquation==b.isEquation;
		}catch(ClassCastException e)
		{
			return false;
		}
	}

	@Override public int hashCode()
	{
		return terms.hashCode()<<1|(isEquation?1:0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		if(terms.isEmpty())
			return "0";
		String output="";
		for(Term current:terms)
			output=output+current.toString();
		if(output.startsWith("+"))
			output=output.substring(1);
		if(isEquation)
			output=output+"=0";
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override public Expression clone()
	{
		@SuppressWarnings("unchecked") Expression a=new Expression((Collection<Term>)terms.clone(),isEquation);
		a.terms.replaceAll(Term::clone);
		return a;
	}

	/**
	 * @author Luke Senseney
	 *
	 */
	private static final class SortByChar implements Comparator<Term>
	{
		char sortBy;

		SortByChar(char sort)
		{
			sortBy=sort;
		}

		@Override public int compare(Term o1,Term o2)
		{
			Constant c1=o1.vars.get(sortBy);
			Constant c2=o2.vars.get(sortBy);
			if(c1==null)
				if(c2==null)
					return 0;
				else
					return 1;
			return c1.compareTo(c2);
		}
	}
}// Glory to God
