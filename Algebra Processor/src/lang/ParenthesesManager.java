package lang;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Luke Senseney
 *
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
	
	static Pattern getContent(int level)
	{
		
		addUpTo(level);
		return levelOfParen.get(level);
	}
	
	static Pattern getParen(int level)
	{
		addUpTo(level);
		return paren.get(level);
	}
	
	static Pattern getTerm(int level)
	{
		addUpTo(level);
		return levelOfParenTerm.get(level);
	}
	
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
