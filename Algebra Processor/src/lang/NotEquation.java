package lang;

public class NotEquation extends RuntimeException
{
	private static final long serialVersionUID=1L;

	public NotEquation()
	{
	}

	public NotEquation(String arg0)
	{
		super(arg0);
	}

	public NotEquation(Throwable arg0)
	{
		super(arg0);
	}

	public NotEquation(String arg0,Throwable arg1)
	{
		super(arg0,arg1);
	}

	public NotEquation(String arg0,Throwable arg1,boolean arg2,boolean arg3)
	{
		super(arg0,arg1,arg2,arg3);
	}
}
