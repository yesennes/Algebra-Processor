package lang;

public class DifferentRoots extends RuntimeException
{
	private static final long serialVersionUID=1L;

	public DifferentRoots()
	{
	}

	public DifferentRoots(String arg0)
	{
		super(arg0);
	}

	public DifferentRoots(Throwable arg0)
	{
		super(arg0);
	}

	public DifferentRoots(String arg0,Throwable arg1)
	{
		super(arg0,arg1);
	}

	public DifferentRoots(String arg0,Throwable arg1,boolean arg2,boolean arg3)
	{
		super(arg0,arg1,arg2,arg3);
	}
}
