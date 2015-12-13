package lang;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents all solutions for a variable.
 * 
 * @author Luke Senseney
 * 
 */
public class Solution implements Serializable
{
	private static final long serialVersionUID=1L;
	/**
	 * The variable this is a solution for.
	 */
	public char letter;
	/**
	 * Expressions which are equal the the variable.
	 */
	public Set<Expression> value;
	/**
	 * Represents whether this has all possible solution for letter, or if others remain unknown
	 */
	public boolean allPossible=true;

	/**
	 * Creates a new solution.
	 * @param newLetter The variable for this solution.
	 * @param newValue Expression that is equal to newLetter.
	 */
	public Solution(char newLetter,Expression newValue)
	{
		letter=newLetter;
		value=new HashSet<Expression>(Collections.singleton(newValue));
	}

	/**
	 * Creates a new Solution for a character with a HashSet of values.
	 * @param newLetter the variable this is a solution for
	 * @param newValue the values of newLetter
	 */
	public Solution(char newLetter,Set<Expression> newValue)
	{
		letter=newLetter;
		value=newValue;
	}
	
	/**
	 * Creates a new solution for a variable with no values.
	 * @param newLetter The variable this is a solution for.
	 */
	public Solution(char newLetter)
	{
		letter=newLetter;
		value=new HashSet<Expression>();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString()
	{
		String rtn=letter+"=";
		for(Expression current:value)
			rtn+=current+" or ";
		return rtn.substring(0,rtn.length()-4);
	}
	
	/**
	 * @param places decimal places to round to.
	 * @return A String representation of this with decimals rounded to places rather than fractions and \u03c0, e and roots rouned to decimals.
	 */
	public String approx(int places)
	{
		String rtn=letter+"=";
		for(Expression current:value)
			rtn+=current.approx().toStringDecimal(places)+" or ";
		return rtn.substring(0,rtn.length()-4);
	}
}// Glory to God