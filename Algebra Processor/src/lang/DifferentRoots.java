package lang;

public class DifferentRoots extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DifferentRoots() {
	}

	@Override
    public String getMessage() {
		return "The Constants being added together had different roots and cannot be";
	}
}
