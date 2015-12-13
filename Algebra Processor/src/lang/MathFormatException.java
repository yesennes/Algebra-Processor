package lang;

/**
 * Thrown when a String was not properly formated for whichever class attempted to parse it.
 * @author Luke Senseney
 */
public class MathFormatException extends IllegalArgumentException
{
	/**
	 * 
	 */
	private static final long serialVersionUID=1L;

	/**
	 * 
	 */
	public MathFormatException()
	{}

	/**
	 * @param message
	 */
	public MathFormatException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public MathFormatException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MathFormatException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
