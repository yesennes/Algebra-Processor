package lang;

/**
 * Unused
 * @author Luke Senseney
 */
public class SystemEquations
{// Start class
	private Expression[] equations=new Expression[0];
	@SuppressWarnings("unused") private Solution[] solutions=new Solution[0];

	public SystemEquations()
	{
	}

	public SystemEquations(String firstEquation)
	{
		this.addEquation(firstEquation);
	}

	public void addEquation(String newEquation)
	{// Start addEquation
		this.equations=General.addToArray(new Expression(newEquation),this.equations);
	}
}// Glory to God