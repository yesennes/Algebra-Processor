package lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class that represents a mathematical expression, or a Equation if isEquation is true.
 * 
 * @author Luke Senseney
 * 
 */
public class Expression implements Comparable<Expression>,BetterCloneable<Expression>
{
	/**
	 * Term array that is added to make this Expression
	 */
	public ArrayList<Term> terms;
	/**
	 * If true, it is treated like a Equation with the array of Terms set equal to 0.
	 */
	public boolean isEquation=false;
	/**
	 * Expression with a single term, 1
	 */
	public static final Expression ONE=new Expression(new Term(Constant.ONE));
	/**
	 * Expression with a single term, 0
	 */
	public static final Expression ZERO=new Expression(new Term(new Constant(0)));
	/**
	 * Expression with a single term, -1
	 */
	public static final Expression NEGATE=new Expression(Term.NEGATE);

	/**
	 * Creates a new Expression from a string. If there is a "=" in the string isEquation is true.
	 * Cannot handle equations with more than one "=" i.e. a=b=c. Garbage in, Garbage out, if the
	 * String is not a correctly formated expression or equation, will attempt to read.
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
		if(!leftSide)
			coeff='-'+coeff;
		if(paren.equals(""))
			terms.add(new Term(coeff));
		else
			if(power.equals(""))
				if(coeff.equals(""))
					terms.addAll(!inverse?new Expression(paren).terms:raise(new Expression(paren),NEGATE));
				else
					terms.addAll(distribute(!inverse?new Expression(paren):new Expression(raise(new Expression(paren),NEGATE)),new Expression(coeff)));
			else
				if(coeff.equals(""))
					terms.addAll(raise(new Expression(paren),!inverse?new Expression(power):new Expression(distribute(new Expression(power),NEGATE))));
				else
					terms.addAll(raiseAndDistribute(new Expression(paren),new Expression(coeff),!inverse?new Expression(power):new Expression(distribute(new Expression(power),NEGATE))));
		simplifyTerms();
	}

	/**
	 * Creates a new Expression from a array of Terms
	 * 
	 * @param newTerms Terms to make a equation from.
	 */
	public Expression(Term...newTerms)
	{
		terms=new ArrayList<Term>(Arrays.asList(newTerms));
		simplifyTerms();
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
	 * @param terms2 Terms to make a equation from.
	 * @param equation Sets isEquation to this.
	 */
	public Expression(Collection<Term> newTerms,boolean equation)
	{
		terms=new ArrayList<Term>(newTerms);
		simplifyTerms();
		isEquation=equation;
	}

	/**
	 * Raises expression to power.
	 * @param expression the base
	 * @param power the exponent
	 * @return expression^power
	 */
	public static ArrayList<Term> raise(Expression expression,Expression power)
	{
		if(power.isConstant())
			if(expression.terms.size()==1)
			{
				expression.terms.get(0).coeff.raise(power.terms.get(0).coeff);
				for(Constant current:expression.terms.get(0).vars.values())
					current.multiply(power.terms.get(0).coeff);
				for(Entry<Expression,Expression> current:expression.terms.get(0).undistr.entrySet())
					current.setValue(new Expression(distribute(current.getValue(),power)));
			}else
			{
				Expression original=expression;
				if(power.terms.get(0).coeff.numerator==0)
					expression=ONE;
				for(int i=1;i<Math.abs(power.terms.get(0).coeff.numerator);i++)
					expression=new Expression(distribute(expression,original));
				if(power.terms.get(0).coeff.numerator<0)
				{
					TreeMap<Expression,Expression> d=new TreeMap<Expression,Expression>();
					Constant p=new Constant(power.terms.get(0).coeff.denominator);
					p.invert();
					p.multiply(Constant.NEGATE);
					d.put(expression,new Expression(new Term(p)));
					expression=new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),d));
				}
			}
		else
			if(expression.terms.size()==1)
			{
				for(Entry<Expression,Expression> current:expression.terms.get(0).undistr.entrySet())
				{
					current.setValue(new Expression(distribute(current.getValue(),power)));
					current.getValue().simplifyTerms();
				}
				if(expression.terms.get(0).vars.size()!=0&&!expression.terms.get(0).coeff.equals(Constant.ONE))
					expression.terms.get(0).undistr.put(new Expression(new Term(expression.terms.get(0).coeff,expression.terms.get(0).vars)),power);
				expression=new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),expression.terms.get(0).undistr));
			}else
			{
				TreeMap<Expression,Expression> d=new TreeMap<Expression,Expression>(Collections.singletonMap(expression,power));
				expression=new Expression(new Term(Constant.ONE,new TreeMap<Character,Constant>(),d));
			}
		return expression.terms;
	}

	/**
	 * Raises expression to power.
	 * @param expression the base
	 * @param power the exponent
	 * @return expression^power
	 */
	public static ArrayList<Term> raise(Collection<Term> expression,Collection<Term> multiplier)
	{
		return raise(new Expression(expression),new Expression(multiplier));
	}

	/**
	 * Raises an expression to another and then multiplies by power.
	 * @param expression the base
	 * @param multiplier the expression the be multiplied.
	 * @param power the exponent
	 * @return (expression^power)*multiplier
	 */
	public static ArrayList<Term> raiseAndDistribute(Expression expression,Expression multiplier,Expression power)
	{
		return distribute(new Expression(raise(expression,power)),multiplier);
	}

	/**
	 * Raises an expression to another and then multiplies by power.
	 * @param expression the base
	 * @param multiplier the expression the be multiplied.
	 * @param power the exponent
	 * @return (expression^power)*multiplier
	 */
	public static ArrayList<Term> raiseAndDistribute(Collection<Term> expression,Collection<Term> multiplier,Collection<Term> power)
	{
		return raiseAndDistribute(new Expression(expression),new Expression(multiplier),new Expression(power));
	}

	/**
	 * Raises an Expression to an Expression and then multiplies it by another.
	 * 
	 * @param expression The main
	 * @param multiplier multiplies expression by this.
	 * @return (expression)*(multiplier)
	 */
	public static ArrayList<Term> distribute(Expression expression,Expression multiplier)
	{
		ArrayList<Term> retrn=new ArrayList<Term>(expression.terms.size()*multiplier.terms.size());
		for(Term exp:expression.terms)
			for(Term multi:multiplier.terms)
				retrn.add(Term.multiply(exp,multi));
		return retrn;
	}

	/**
	 * Raises an Expression to an Expression and then multiplies it by another.
	 * 
	 * @param expression The main
	 * @param multiplier multiplies expression by this.
	 * @param power Raises expression to this.
	 * @return (expression)^(power)*(multiplier)
	 */
	public static ArrayList<Term> distribute(Collection<Term> expression,Collection<Term> multiplier)
	{
		return distribute(new Expression(expression),new Expression(multiplier));
	}

	/**
	 * Combines like Terms, and if isEquation, divides by the greatest common divisor of all Terms.
	 */
	public void simplifyTerms()
	{// Start simplifyTerms
		for(int i=0;i<terms.size();i++)
			for(int j=terms.size()-1;j>i;j--)
				try{
					if(Term.isLikeTerm(terms.get(j),terms.get(i)))
					{
						terms.get(i).coeff.add(terms.get(j).coeff);
						terms.remove(j);
					}
				}catch(DifferentRoots e)
				{
				}
		for(int i=terms.size()-1;i>-1;i--)
			if(terms.get(i).coeff.equals(new Constant()))
				terms.remove(i);
		Collections.sort(terms);
	}

	/**
	 * Checks if this is the same expression or equation as a.
	 * 
	 * @param a Expression to check if this is the same as.
	 * @return If this is the same as a, true, else false.
	 */
	public boolean equals(Object a)
	{
		Expression b=(Expression)a;
		if(terms.size()==b.terms.size())
			for(int i=0;i<terms.size();i++)
			{
				if(!terms.get(i).equals(b.terms.get(i)))
					return false;
			}
		else
			return false;
		return isEquation==b.isEquation;
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

	public int compareTo(Expression o)
	{
		return toString().compareTo(o.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Expression clone()
	{
		Expression a=new Expression(terms,isEquation);
		for(Term current:a.terms)
			current=current.clone();
		return a;
	}
	
	public ArrayList<Expression> factor()
	{
		ArrayList<Expression> ans=new ArrayList<Expression>();
		Term denom=new Term(Constant.ONE);
		for(Term current:terms)
		{
			for(Map.Entry<Character,Constant> on:current.vars.entrySet())
				if(on.getValue().compareTo(new Constant())<0&&!(denom.vars.containsKey(on.getKey())&&denom.vars.get(on.getKey()).compareTo(on.getValue())>=0))
					;
		}
		Term fact=Term.gcd(terms);
		ans.add(new Expression(fact));
		Expression remaining=new Expression(Expression.raiseAndDistribute(new Expression(fact),clone(),Expression.NEGATE));
		
		return ans;
	}

	/**
	 * Solves the Expression for each variable.
	 * 
	 * @return An array of solutions, one for each variable.
	 * @throws NotEquation If the Expression is not an Equation.
	 */
	public ArrayList<Solution> solve() throws NotEquation
	{
		if(!isEquation)
			throw new NotEquation();
		ArrayList<Solution> solutions=new ArrayList<Solution>(2);
		HashSet<Character> vars=getVars();
		vars.remove('\u05D0');
		for(char current:vars)
		{
			solutions.add(solveFor(current));
		}
		return solutions;
	}
	
	public Solution solveFor(char iso) throws NotEquation
	{
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
			root=a.vars.remove(iso);
			a.coeff.multiply(Constant.NEGATE);
			divide.add(a);
		}
		root.invert();
		if(root.equals(Constant.ONE))
			return new Solution(iso,new Expression(raiseAndDistribute(new Expression(divide),new Expression(noVar),NEGATE)));
		else
			return new Solution(iso,new Expression(raise(new Expression(raiseAndDistribute(new Expression(divide),new Expression(noVar),NEGATE)),new Expression(new Term(root)))));
	}

	/**
	 * Gets the variables in that
	 * 
	 * @return An array of char representing the variables in the Expression, contains no
	 *         duplicates.
	 */
	public HashSet<Character> getVars()
	{
		HashSet<Character> vars=new HashSet<Character>();
		for(Term current:terms)
			vars.addAll(current.getVars());
		return vars;
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
		Term gcda=Term.gcd(a.terms);
		Term gcdb=Term.gcd(b.terms);
		Expression newa=new Expression(raiseAndDistribute(new Expression(gcda),a,NEGATE));
		Expression newb=new Expression(raiseAndDistribute(new Expression(gcdb),b,NEGATE));
		return newa.equals(newb)?new Expression(distribute(newa,new Expression(Term.gcd(gcda,gcdb)))):new Expression(Term.gcd(gcda,gcdb));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String output="";
		for(Term current:terms)
			output=output+current.toString();
		if(output.startsWith("+"))
			output=output.substring(1);
		if(isEquation)
			output=output+"=0";
		return output;
	}

	/**
	 * @author Luke Senseney
	 *
	 */
	private static final class SortByChar implements Comparator<Term>
	{
		char sortBy;
		
		public SortByChar(char sort)
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