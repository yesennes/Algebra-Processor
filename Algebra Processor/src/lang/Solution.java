package lang;

import java.util.ArrayList;
import java.util.Collections;

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
	public ArrayList<Expression> value;

	/**
	 * Creates a new solution.
	 * 
	 * @param newLetter The variable for this solution.
	 * @param newValue Expression that is equal to newLetter.
	 */
	public Solution(char newLetter,Expression newValue)
	{
		letter=newLetter;
		value=new ArrayList<Expression>(Collections.singleton(newValue));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String rtn=letter+"=";
		for(Expression current:value)
			rtn+=current.toString()+" or ";
		return rtn.substring(0,rtn.length()-4);
	}
}