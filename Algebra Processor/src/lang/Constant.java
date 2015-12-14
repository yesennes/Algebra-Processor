package lang;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class to maintain exact accuracy when using repeating decimals, non-integer, or constants, by holding a numerator,
 * denominator, and array of Roots.
 * 
 * @author Luke Senseney
 */
public class Constant extends Number implements Comparable<Number>, Serializable {
	private static final long serialVersionUID = 01L;
	/**
	 * The numerator of this number.
	 */
	long numerator = 0;
	/**
	 * The denominator of this number.
	 */
	long denominator = 1;
	/**
	 * A Map of roots in this constant. For each key, value^(1/key) is multiplied into this constant.
	 */
	TreeMap<Integer, Constant> roots = new TreeMap<>();
	/**
	 * Constant with a value of 1.
	 */
	public static final Constant ONE = new Constant(1);
	/**
	 * Constant with a value of -1.
	 */
	public static final Constant NEGATE = new Constant(-1);

	/**
	 * Creates a new Constant from an long.
	 * @param numerator The long to make a Constant.
	 */
	public Constant(long numerator) {
		this.numerator = numerator;
	}

	/**
	 * Creates a new Constant from an numerator and a denominator.
	 * @param numerator The new numerator.
	 * @param denominator The new denominator.
	 */
	public Constant(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
		simplify();
	}

	/**
	 * Creates a new Constant from a double.
	 * @param newConstant The double to make a Constant out of.
	 */
	public Constant(double newConstant) {
		while(newConstant % 1 != 0) {
			newConstant *= 10;
			denominator *= 10;
		}
		numerator = (long)newConstant;
		simplify();
	}

	/**
	 * Creates an new constant from a numerator, denominator and roots.
	 * @param numerator The numerator.
	 * @param denominator The denominator.
	 * @param roots The roots.
	 */
	public Constant(long numerator, long denominator, TreeMap<Integer, Constant> roots) {
		this.numerator = numerator;
		this.denominator = denominator;
		this.roots = roots;
		simplify();
	}

	/**
	 * Creates a new Constant with a value of 0.
	 */
	public Constant() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		double sum = 1;
		for(Entry<Integer, Constant> root : roots.entrySet()) {
			sum *= Math.pow(root.getValue().doubleValue(), 1. / root.getKey());
		}
		return sum * numerator / (double)denominator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return (long)doubleValue();
	}

	/**
	 * Adds a root to this Constant.
	 * @param root The root to be added
	 * @param in The constant to be in the root
	 */
	public void addRoot(Integer root, Constant in) {
		Constant was = roots.put(root, in);
		if(was != null) {
			in.multiply(was);
		}
	}

	/**
	 * Simplify the fractions and the radicals.
	 */
	public void simplify() {
		// Removes all roots from inside each root, and adds them to the proper root. i.e. takes (2^(1/2))^(1/2)
		// and turns it to 2^(1/4)
		ArrayList<Entry<Integer, Constant>> toAdd = new ArrayList<>();
		for(Entry<Integer, Constant> current : roots.entrySet()) {
			for(Entry<Integer, Constant> root : current.getValue().roots.entrySet()) {
				toAdd.add(new SimpleEntry<>(current.getKey() * root.getKey(), root.getValue()));
			}
			current.getValue().roots = new TreeMap<>();
		}
		for(int i = 0; i < toAdd.size(); i++) {
			Entry<Integer, Constant> current = toAdd.get(i);
			for(Entry<Integer, Constant> root : current.getValue().roots.entrySet()) {
				toAdd.add(new SimpleEntry<>(current.getKey() * root.getKey(), root.getValue()));
			}
			current.getValue().roots = new TreeMap<>();
		}
		for(Entry<Integer, Constant> current : toAdd) {
			addRoot(current.getKey(), current.getValue());
		}
		// Sees if anything can be pulled out of the roots. For instance takes the square root of 12 and turns it into
		// 2 root 3. Then rationalizes the denominator of the root.
		for(Entry<Integer, Constant> current : roots.entrySet()) {
			// Finds what can be extracted from the current root.
			Constant extract = extract(current.getKey(), current.getValue());
			// Takes it out from inside the root.
			if(!extract.equals(ONE)) {
				current.setValue(current.getValue().divide(extract));
			}
			// Changes extract to what must be multiplied to the coeff.
			extract.numerator = (long)Math.pow(extract.numerator, 1. / current.getKey());
			extract.denominator = (long)Math.pow(extract.denominator, 1. / current.getKey());
			current.getValue().numerator *= current.getValue().denominator;
			denominator *= current.getValue().denominator;
			current.getValue().denominator = 1;
			if(!extract.equals(ONE)) {
				Constant toBe = multiply(extract);
				numerator = toBe.numerator;
				denominator = toBe.denominator;
				roots = toBe.roots;
			}
		}
		Collection<Constant> value = roots.values();
		// Removes all roots with a value of one.
		value.removeIf(ONE::equals);
		// Keeps the denominator positive.
		if(denominator < 0) {
			numerator =- numerator;
			denominator =- denominator;
		}
		// Reduces the fraction. Does not call .divide(Constant) to avoid recursion
		double divide = General.gcd(numerator, denominator);
		numerator = (long)(numerator / divide);
		denominator = (long)(denominator / divide);
	}

	/**
	 * Multiplies this by a.
	 * @param a The Constant to multiply this by.
	 * @return this*a
	 */
	public Constant multiply(Constant a) {
		Constant c = clone();
		c.numerator *= a.numerator;
		c.denominator *= a.denominator;
		for(Entry<Integer, Constant> current : a.roots.entrySet()) {
			c.addRoot(current.getKey(), current.getValue());
		}
		c.simplify();
		return c;
	}

	/**
	 * Multiplies this by a.
	 * @param a The long to multiply this by.
	 * @return this*a
	 */
	public Constant multiply(long a) {
		Constant c = clone();
		c.numerator *= a;
		c.simplify();
		return c;
	}

	/**
	 * Divides this by a.
	 * @param a The Constant to divide this by.
	 * @return this/a
	 */
	public Constant divide(Constant a) {
		return multiply(a.invert());
	}

	/**
	 * Divides this by a.
	 * @param a The long to divide this by.
	 * @return this/a
	 */
	public Constant divide(long a) {
		Constant c = clone();
		c.numerator /= a;
		c.simplify();
		return c;
	}

	/**
	 * Adds a to this.
	 * @param a Constant to add to this.
	 * @return this+a
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public Constant add(Constant a) throws DifferentRoots {
		if(!roots.equals(a.roots)) {
			throw new DifferentRoots();
		}
		// Finds the lcm, adds the numerators/denominators and multiplies by the lcm, then sets the denominator to lcm.
		long lcm = General.lcm(denominator, a.denominator);
		return new Constant(numerator * lcm / denominator + a.numerator * lcm / a.denominator, lcm);
	}

	/**
	 * Subtracts a from this.
	 * @param a Constant to subtract from this.
	 * @return this-a
	 * @throws DifferentRoots If the Roots of the Constants are different.
	 */
	public Constant subtract(Constant a) throws DifferentRoots {
		return add(a.negate());
	}

	/**
	 * @return -this
	 */
	public Constant negate() {
		return multiply(NEGATE);
	}

	/**
	 * Checks to see if this is rational.
	 * @return If this is rational, true, else false.
	 */
	public boolean isRat() {
		return roots.size() == 0;
	}

	/**
	 * Raises this to power. Note: ignores roots in power. For expressions like 5^(5^(1/2)), use Term.
	 * @param power the power to raise this by
	 * @return this^power
	 */
	public Constant raise(Constant power) {
		Constant c = clone();
		// If power is less than one, flips this, and raises by -power, else just raises by power.
		if(power.numerator < 0) {
			c = c.invert();
			c.numerator = (long)Math.pow(c.numerator, -power.numerator);
			c.denominator = (long)Math.pow(c.denominator, -power.numerator);
		} else {
			c.numerator = (long)Math.pow(c.numerator, power.numerator);
			c.denominator = (long)Math.pow(c.denominator, power.numerator);
		}
		// If power has a denominator, puts this in a root in a new Constant.
		if(power.denominator > 1) {
			c.roots = new TreeMap<Integer, Constant>(Collections.singletonMap((int)power.denominator, this.clone()));
			c.numerator = 1;
			c.denominator = 1;
		}
		c.simplify();
		return c;
	}

	/**
	 * Inverts this.
	 * @return 1/this
	 */
	@SuppressWarnings("unchecked")
	public Constant invert() {
		Constant c = new Constant(denominator, numerator, (TreeMap<Integer, Constant>)roots.clone());
		c.roots.replaceAll((t, u) -> u.invert());
		c.simplify();
		return c;
	}

	/**
	 * Gets the greatest common divisor of two Constants.
	 * @param a A Constant to get the gcd of.
	 * @param b The other Constant to get the gcd of.
	 * @return The gcd of a and b.
	 */
	public static Constant gcd(Constant a, Constant b) {
		Constant ans = new Constant(
				General.gcd(a.numerator, b.numerator), a.denominator * b.denominator / General.gcd(
						a.denominator, b.denominator));
		for(Entry<Integer, Constant> current : a.roots.entrySet()) {
			Constant other = b.roots.get(current.getKey());
			Constant gcd = Constant.gcd(current.getValue(), other);
			if(!gcd.equals(ONE)) {
				ans.addRoot(current.getKey(), gcd);
			}
		}
		ans.simplify();
		return ans;
	}

	/**
	 * NOT AN ACTUAL JAVADOC, JUST AN ALERT THAT THIS METHOD NEEDS AN ACTUAL JAVADOC.
	 */
	@Override
	public Constant clone() {
		Constant clone = new Constant();
		clone.numerator = numerator;
		clone.denominator = denominator;
		clone.roots = new TreeMap<>(roots);
		return clone;
	}

	/**
	 * Checks if this is equal to another Constant.
	 * @param a Constant to check if this is equal to.
	 * @return If the Constants equal, true, else false.
	 */
	@Override
	public boolean equals(Object o) {
		try {
			Number a = (Number)o;
			return a.doubleValue() == doubleValue();
		} catch(ClassCastException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int)(numerator >> 32 + denominator >> 32 + numerator + denominator) + roots.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Number o) {
		if(o == null) {
			return 1;
		}
		double diff = doubleValue() - o.doubleValue();
		if(diff > 0) {
			return 1;
		} else {
			return diff < 0 ? -1 : 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer r = new StringBuffer();
		// If there is nothing in the roots, or numerator isn't 1 or -1,adds the numerator to this. If the numerator is
		// -1 and there are roots, just add the minus sign to this.
		if(roots.size() == 0 || (numerator != 1 && numerator != -1)) {
			r.append(numerator);
		} else if(numerator == -1) {
			r.append("-");
		}
		for(Entry<Integer, Constant> current : roots.entrySet()) {
			// Generates the proper root symbol for the root, and then what's in the root.
			if(current.getKey() < 5) {
				r.append((char)(0x2218 + current.getKey()));
			} else {
				r.append((char)(0x2070 + current.getKey()) + "\u221a");
			}
			r.append('(' + current.getValue().toString() + ')');
		}
		if(denominator != 1) {
			r.append("/").append(denominator);
		}
		return r.toString();
	}
	
	/**
	 * NOT AN ACTUAL JAVADOC, I JUST WROTE THIS IN TO GET YOUR ATTENTION THAT THIS METHOD LACKS AN ACTUAL JAVADOC.
	 * @param s
	 * @return
	 */
	public static Constant valueOf(String s) {
		return new Constant(Double.valueOf(s));
	}

	/**
	 * Finds the x, where x is the highest number that meets these requirements: is a factor of inRoot and is an
	 * rational number when raised to 1/root. This makes the returned Constant for simplifying roots, i.e. to simplify
	 * 12^(1/2) call extract(2,12) it will return 4. Divide the 12 by for to get 3, then raise 4 to 1/2 to get 2. The
	 * simplified version is 2(3)^1/2
	 * @return The highest integer that is a factor of inRoot, and when rooted by root is an integer.
	 */
	private static Constant extract(int root, Constant inRoot) {
		int i;
		// Finds the highest number, that raised to
		for(i = (int)Math.pow(Math.abs(inRoot.numerator), 1. / root); inRoot.numerator / Math.pow(i, root) % 1 != 0; i--);
		Constant answer = new Constant(Math.pow(i, root));
		for(i = (int)Math.pow(Math.abs(inRoot.denominator), 1. / root); inRoot.denominator / Math.pow(i, root) % 1 != 0; i--);
		answer.divide((int)Math.pow(i, root));
		return answer;
	}
}