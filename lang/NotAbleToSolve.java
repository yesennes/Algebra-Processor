package lang;

/**
 * Thrown when an Expression tried to solve itself but couldn't.
 * 
 * @author Luke Senseney, Nikola Istvanic
 */
public class NotAbleToSolve extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for NotAbleToSolve exception which has a String parameter.
	 * @param message The exception's message.
	 */
	public NotAbleToSolve(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * NotAbleToSolve constructor that takes both a String and a Throwable.
	 * @param message The exception's message.
	 * @param cause The cause of the exception.
	 */
	public NotAbleToSolve(String message, Throwable cause) {
		super(message, cause);
	}
}