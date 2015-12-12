package lang;

/**
 * Unchecked exception thrown when 
 * 
 * @author Luke Senseney, Nikola Istvanic
 */
public class DifferentRoots extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for DifferentRoots exception, taking in no parameters.
	 */
	public DifferentRoots()	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "The Constants being added together had different roots and cannot be";
	}
}