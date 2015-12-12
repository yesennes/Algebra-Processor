package lang;

/**
 * Thrown when 
 * 
 * @author Luke Senseney, Nikola Istvanic
 *
 */
public class NotEvaluatable extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * NotEvaluatable exception constructor which requires no parameters.
	 */
	public NotEvaluatable() {
	}

	/**
	 * 
	 * @param arg0
	 */
	public NotEvaluatable(String arg0) {
		super(arg0);
	}

	/**
	 * 
	 * @param arg0
	 */
	public NotEvaluatable(Throwable arg0) {
		super(arg0);
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public NotEvaluatable(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public NotEvaluatable(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
}
