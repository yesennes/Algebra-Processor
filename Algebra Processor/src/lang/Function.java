package lang;

import java.util.ArrayList;

/**
 * THIS CLASS HAS NOT BEEN FINISHED AND IS NOT RECOGNIZED BY EXPRESSION OR TERM
 * A class to represent Mathematical functions. In order for the Algebra Processor to recognize a
 * function, please register it with registerFunc(Function o).
 * 
 * @author Luke Senseney
 */
public abstract class Function {
	/**
	 * An ArrayList of recognized Functions, used by Expression to recognize functions i.e. so
	 * sin(parameter) is the geometric sine, not s*i*n*(parameter)
	 * @see registerFunc
	 */
	static ArrayList<Function> recognizeFunc = new ArrayList<>(0);
	
	public static void registerFunc(Function o) {
		recognizeFunc.add(o);
	}
	
	protected Expression[] input;

	/**
	 * Attempts to evaluate the function.
	 * @return An Expression which is mathematically equal to this function.
	 * @throws NotEvaluatable If the function cannot be evaluated.
	 */
	public abstract Term evaluate() throws NotEvaluatable;

	/**
	 * Creates a Term with the inverse function of this with otherSide as the parameter.
	 * @param otherSide Generally the other side of the equation which is being solved, becomes the parameters.
	 * @return a Function that is the inverse of this, with otherSide as the parameter.
	 */
	public abstract Function invert(Expression otherSide);

	/**
	 * Gets the signature, normally three letters which represent this Function.
	 * @return This Function's signature.
	 */
	public abstract String getSig();

	@Override
	public String toString() {
		String ans = getSig() + '(';
		for(Expression current : input) {
			ans += current;
		}
		return ans;
	}
}
