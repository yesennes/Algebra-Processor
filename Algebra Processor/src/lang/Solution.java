package lang;

import java.util.Collections;
import java.util.HashSet;

/**
 * Class that represents all solutions for a variable.
 * 
 * @author Luke Senseney
 * 
 */
public class Solution
{
	/**
	 * The variable this is a solution for.
	 */
	public char letter;
	/**
	 * Expressions which are equal the the variable.
	 */
	public HashSet<Expression> value;
	public boolean allPossible=true;

	/**
	 * Creates a new solution.
	 * 
	 * @param newLetter The variable for this solution.
	 * @param newValue Expression that is equal to newLetter.
	 */
	public Solution(char newLetter,Expression newValue)
	{
		letter=newLetter;
		value=new HashSet<Expression>(Collections.singleton(newValue));
	}

	/**
	 * @param iso
	 * @param arrayList
	 */
	public Solution(char newLetter,HashSet<Expression> newValue)
	{
		letter=newLetter;
		value=newValue;
	}
	
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
			rtn+=current.toString()+" or ";
		return rtn.substring(0,rtn.length()-4);
	}
}