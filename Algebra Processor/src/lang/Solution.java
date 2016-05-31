package lang;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents all solutions for a variable.
 * 
 * @author Luke Senseney
 */
public class Solution implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * The variable this is a solution for.
	 */
	public char letter;
	/**
	 * Expressions which are equal to the variable.
	 */
	public Set<Expression> value;
	/**
	 * Represents whether this has all possible solutions for letter, or if others remain unknown.
	 */
	boolean allPossible = true;

	/**
	 * Creates a new solution.
	 * @param letter The variable for this solution.
	 * @param value Expression that is equal to newLetter.
	 */
	public Solution(char letter, Expression value) {
		this.letter = letter;
		this.value = new HashSet<>(Collections.singleton(value));
	}

	/**
	 * Creates a new Solution for a character with a HashSet of values.
	 * @param letter the variable this is a solution for
	 * @param value the values of newLetter
	 */
	public Solution(char letter, Set<Expression> value) {
		this.letter = letter;
		this.value = value;
	}
	
	/**
	 * Creates a new solution for a variable with no values.
	 * @param letter The variable this is a solution for.
	 */
	public Solution(char letter) {
		this.letter = letter;
		value = new HashSet<>();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String rtn = letter + "=";
		for(Expression current : value) {
			rtn += current + " or ";
		}
		return rtn.substring(0, rtn.length() - 4);
	}
    @Override
    public int hashCode(){
        return (int)letter + value.hashCode() << 8;
    }

	@Override
    public boolean equals(Object o){
        if(o instanceof Solution) {
            Solution s = (Solution)o;
            return letter == s.letter && value.equals(s.value);
        }
        return false;
    }
	
	/**
	 * @param places decimal places to round to.
	 * @return A String representation of this with decimals rounded to places rather than fractions and \u03c0,
	 * e and roots rounded to decimals.
	 */
	public String approx(int places) {
		String rtn = letter + "=";
		for(Expression current : value) {
			rtn += current.approx().toStringDecimal(places) + " or ";
		}
		return rtn.substring(0, rtn.length() - 4);
	}
}
