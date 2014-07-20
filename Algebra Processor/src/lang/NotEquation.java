package lang;

/**
 * Thrown by Expression when a action is called on a non-equation that is only for equations.
 * @author Luke Senseney
 */
public class NotEquation extends UnsupportedOperationException
{
	private static final long serialVersionUID=1L;

	public NotEquation()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override public String getMessage()
	{
		return "This is not an equation and cannot be solved";
	}
}
