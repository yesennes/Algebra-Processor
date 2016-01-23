package lang;

/**
 * Exception thrown when a calculation could not be completed because a number was too big.
 *
 * @author Luke Senseney
 */
public class OverflowException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create a new OverflowException
	 * @param number The number that was too big.
	 */
	public OverflowException(String number) {
		super(number + " was to big. Later version will accept arbitrarily large values");
	}
}
