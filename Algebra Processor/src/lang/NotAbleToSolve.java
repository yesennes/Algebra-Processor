package lang;

/**
 * Thrown when Expression tried to solve itself and couldn't.
 * @author Luke Senseney
 */
public class NotAbleToSolve extends Exception
{
	private static final long serialVersionUID=1L;

	/**
	 * @param message 
	 */
	public NotAbleToSolve(String message)
	{
		super(message);
		// TODO Auto-generated constructor stub
	}
}
