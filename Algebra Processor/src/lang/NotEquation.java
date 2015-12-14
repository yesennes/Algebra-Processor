package lang;

/**
 * Thrown by Expression when an action is called on a non-equation that is only for equations.
 * 
 * @author Luke Senseney, Nikola Istvanic
 */
public class NotEquation extends UnsupportedOperationException {
	private static final long serialVersionUID = 1L;

	/**
	 * NotEquation exception constructor which takes in no parameters.
	 */
	public NotEquation() {
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return "This is not an equation and cannot be solved";
	}
}
