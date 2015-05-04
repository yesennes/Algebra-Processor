package lang;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Luke Senseney
 * Class to provide regex patterns to deal with terms with any given number of levels of parentheses.
 */
class ParenthesesManager
{

	private static ArrayList<Pattern> levelOfParen=new ArrayList<Pattern>();
	private static ArrayList<Pattern> paren=new ArrayList<Pattern>();
	private static ArrayList<Pattern> levelOfParenTerm=new ArrayList<Pattern>();
	static{
		levelOfParen.add(Pattern.compile("(?:[/*\\^]-|[^()+-])"));
		paren.add(Pattern.compile("\\(\\)"));
		levelOfParenTerm.add(Pattern.compile("[+\\-]*"+levelOfParen.get(0)+"+(?=[+\\-)]|$)"));
	}
	
	/**
	 * @param level Level of parentheses.
	 * @return A Pattern that matches parentheses surrounding term(s) with level-1 levels of parentheses or a single character allowed allowed in a term.
	 */
	static Pattern getContent(int level)
	{
		addUpTo(level);
		return levelOfParen.get(level);
	}
	
	/**
	 * @param level Level of parentheses.
	 * @return A Pattern that matches parentheses surrounding term(s) with level-1 levels of parentheses.
	 */
	static Pattern getParen(int level)
	{
		addUpTo(level);
		return paren.get(level);
	}
	
	/**
	 * @param level Level of parentheses
	 * @return A Pattern that matches a term with level levels of parentheses.
	 */
	static Pattern getTerm(int level)
	{
		addUpTo(level);
		return levelOfParenTerm.get(level);
	}
	
	/**
	 * Constructs patterns upto level levels of parentheses.
	 * @param level Patterns to be constructed up to.
	 */
	private static void addUpTo(int level)
	{
		for(int i=levelOfParen.size();i<=level;i++)
		{
			paren.add(Pattern.compile("\\((?:"+levelOfParenTerm.get(i-1)+")+\\)"));
			levelOfParen.add(Pattern.compile("(?:"+levelOfParen.get(0)+"|"+paren.get(i)+")"));
			levelOfParenTerm.add(Pattern.compile("[+\\-]*"+levelOfParen.get(i).pattern()+"+(?=[+\\-)]|$)"));
		}
	}
}//Glory to God
