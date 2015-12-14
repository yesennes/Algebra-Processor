package lang;

/**
 * Thrown when a String was not properly formated for whichever class attempted to parse it.
 * 
 * @author Luke Senseney, Nikola Istvanic
 */
public class MathFormatException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for MathFormatException that takes no parameters.
	 */
	public MathFormatException() {
	}

	/**
	 * Message of this exception.
	 * @param message The exception's message.
	 */
	public MathFormatException(String message) {
		super(message);
	}

	/**
	 * Constructor for MathFormatException which takes a throwable cause.
	 * @param cause Throwable which is thrown with the exception.
	 */
	public MathFormatException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor for MathFormatException which takes in a String and Throwable.
	 * @param message The exception's message.
	 * @param cause Throwable thrown along with the exception.
	 */
	public MathFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
