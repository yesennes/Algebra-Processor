package lang;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

/**
 * Class that represents a mathematical expression, or a equation if isEquation is true.
 *
 * @author Luke Senseney
 */
public class Expression implements Comparable<Expression>, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/**
	 * Term array that the sum of is this expression, or are equal to 0 if isEquation.
	 */
	ArrayList<Term> terms;
	/**
	 * If true, it is treated like a equation with the array of Terms set equal to 0. Default is false.
	 */
	public boolean isEquation = false;
	/**
	 * Expression with no terms.
	 */
	public static final Expression ZERO = new Expression();
	/**
	 * Expression with a single term, 1.
	 */
	public static final Expression ONE = new Expression(new Term(Constant.ONE));
	/**
	 * Expression with a single term, -1.
	 */
	public static final Expression NEGATIVE = new Expression(Term.NEGATE);

	/**
	 * Creates a new Expression from a string. If there is a "=" in the string isEquation is true. Cannot handle
	 * equations with more than one "=" i.e. a = b = c. Garbage in, Garbage out, if the String is not a correctly formated
	 * expression or equation, will attempt to read or throw exception. Cannot read \u221a, use ^(1/2).
	 * @param newExpression The string to make a equation from.
	 * @throws MathFormatException if newExpression is not formatted correctly.
	 */
	public Expression(String newExpression) throws MathFormatException {
		terms = new ArrayList<>();
		// Removes all white space and replaces i and e with their proper respective characters.
		newExpression = newExpression.replaceAll("\\s", "").replace(Term.IMAG_UNIT, String.valueOf(Term.interImag))
				.replace(Term.E, String.valueOf(Term.interE));
		String[] split = newExpression.split("=");
		if(split.length > 2) {
			throw new MathFormatException("There are too may \"=\" in the entered String");
		}
		// Parses the left side of this equation or all of this expression.
		terms = notEquation(split[0]).terms;
		// Subtracts the left side of this equation from the right.
		if(split.length > 1) {
			terms = subtract(notEquation(split[1])).terms;
			isEquation = true;
		} else {
			// Calls simplify terms if subtract is not called. Subtract calls simplifyTerms itself.
			simplifyTerms();
		}
	}

	/**
	 * Changes a string which contains no "=" into an Expression.
	 * @param s String to turn into an Expression.
	 * @return The Expression represented by s.
	 * @throws MathFormatException If s is not formatted correctly.
	 */
	private static Expression notEquation(String s) throws MathFormatException {
		// Finds out how many levels of parentheses are in this equation, throwing errors if parentheses don't match.
		int inParen = 0;
		int max = 0;
		for(char c : s.toCharArray()) {
			if(c == '(') {
				inParen++;
				if(inParen > max) {
					max = inParen;
				}
			} else if(c == ')') {
				inParen--;
				if(inParen < 0) {
					throw new MathFormatException("There is an unmatched close parenthese.");
				}
			}
		}
		if(inParen != 0) {
			throw new MathFormatException("There is an unmatched start parenthese.");
		}
		Expression retrn = ZERO.clone();
		// Separates into terms. It is impossible to make a regex pattern in java that matches an arbitrary number
		// (please notify me if it isn't) of levels of parentheses so it starts from 0 and steps up.
		Matcher findParen = ParenthesesManager.getTerm(0).matcher(s);
		// Each iteration finds one term.
		while(!findParen.hitEnd()) {
			// Looks for the level of parentheses for the next term.
			int i = 0;
			findParen.usePattern(ParenthesesManager.getTerm(0));
			for(; !findParen.lookingAt(); i++) {
				if(i + 1 > max) {
					throw new MathFormatException(
							"There is an error in your formatting starting somewhere near the start of "
					+ s.substring(findParen.regionStart()));
				}
				findParen.usePattern(ParenthesesManager.getTerm(i + 1));
			}
			// Adds the term to the equation.
			retrn.terms.add(new Term(findParen.group(), i));
			findParen.region(findParen.end(), s.length());
		}
		return retrn;
	}

	/**
	 * Creates a new Expression from a array of Terms
	 * @param newTerms Terms to make a equation from.
	 */
	public Expression(Term...newTerms) {
		this(Arrays.asList(newTerms));
	}

	/**
	 * Creates an Expression from a constant.
	 * @param c The constant to be made into an equation.
	 */
	public Expression(Constant c) {
		terms = new ArrayList<>(Collections.singletonList(new Term(c)));
	}

	/**
	 * Creates a new Expression from a collection of Terms
	 * @param newTerms Terms to make a equation from.
	 */
	public Expression(Collection<Term> newTerms) {
		terms = new ArrayList<>(newTerms);
		simplifyTerms();
	}

	/**
	 * Creates a new Expression from an array of Terms, is an equation if equation is true.
	 * @param newTerms Terms to make an equation from.
	 * @param equation Sets isEquation to this.
	 */
	public Expression(Collection<Term> newTerms, boolean equation) {
		this(newTerms);
		isEquation = equation;
	}

	/**
	 * Creates a new expression from a character.
	 * @param var The char to be made into an equation.
	 */
	public Expression(char var) {
		this(new Term(var));
	}

	/**
	 * Raises this to another Expression.
	 * @param power The exponent.
	 * @return this<sup>power</sup>. Should contain no references to this or power.
	 */
	public Expression raise(Expression power) {
		if(power.isConstant()) {
			return raise(power.terms.get(0).coeff);
		}
		if(terms.size() == 1) {
			Term exp = terms.get(0).clone();
			// Goes from (a^b)^power to a^(b*power)
			for(Entry<Expression, Expression> current : exp.undistr.entrySet()) {
				current.setValue(current.getValue().multiply(power));
				current.getValue().simplifyTerms();
			}
			// If there is something to this term other than undistr, puts that in an undistr raised to power
			if(exp.vars.size() != 0 && !exp.coeff.equals(Constant.ONE)) {
				exp.undistr.put(new Expression(new Term(exp.coeff, terms.get(0).vars)), power);
			}
			// Essentially removes every thing in this term except the undistr, which has already been raised to power.
			return new Expression(new Term(Constant.ONE.clone(), new TreeMap<>(), exp.undistr));
		}
		// expression^pow could not be simplified in any manner. This just puts into the undistr of a new term.
		TreeMap<Expression, Expression> d = new TreeMap<>(Collections.singletonMap(this.clone(), power));
		return new Expression(new Term(Constant.ONE.clone(), new TreeMap<>(), d));
	}

	/**
	 * Raises this to a Term.
	 * @param pow the exponent
	 * @return this<sup>power</sup>. should contain no references to this or power.
	 */
	public Expression raise(Constant pow) {
		if(terms.size() == 1) {
			// If power is constant and expression is a single Term, use
			// Term.raise(Term, Constant)
			return new Expression(terms.get(0).raise(pow));
		}
		if(pow.equals(new Constant())) {
			// If power is 0 return 1
			return ONE.clone();
		}
		Expression retrn = clone();
		// Multiply expression by itself one times 1 less than the absolute value of the numerator of pow.
		for(BigInteger i = pow.getNumerator().abs(); i.compareTo(BigInteger.ONE) > 0; i = i.subtract(BigInteger.ONE)) {
			retrn = retrn.multiply(this);
		}
		// If pow is negative or it has a denominator, this cannot be distributed, so it needs to be in the undistr of a
		// Term. This packages it into a term.
		if(pow.getNumerator().compareTo(BigInteger.ZERO) < 0 || pow.getDenominator().compareTo(BigInteger.ONE) > 0) {
			TreeMap<Expression, Expression> d = new TreeMap<>();
			// sets the numerator of pow to 1 or -1 depending on sign.
			pow.setNumerator(pow.getNumerator().compareTo(BigInteger.ZERO) < 0 ? BigInteger.ONE.negate() : BigInteger.ONE);
			d.put(retrn, new Expression(new Term(pow)));
			return new Expression(new Term(Constant.ONE, new TreeMap<>(), d));
		}
		return retrn;
	}

	/**
	 * Multiplies this by another Expression.
	 * @param multiplier Expression to be multiplied by this.
	 * @return this*(multiplier). Should contain no references to this or multiplier.
	 */
	public Expression multiply(Expression multiplier) {
		Expression retrn = new Expression();
		// Multiplies each term in expression by each term in multiplier.
		for(Term exp : terms) {
			for(Term multi : multiplier.terms) {
				retrn.terms.add(exp.multiply(multi));
			}
		}
		retrn.simplifyTerms();
		return retrn;
	}

	/**
	 * Multiplies this by a Term
	 * @param multiplier Term to be multiplied by this.
	 * @return this*multiplier. Should contain no references to this or multiplier.
	 */
	public Expression multiply(Term multiplier) {
		Expression retrn = new Expression();
		// Multiplies each term in expression by the multiplier.
		for(Term exp : terms) {
			retrn.terms.add(exp.multiply(multiplier));
		}
		retrn.simplifyTerms();
		return retrn;
	}

	/**
	 * Divides this by divisor.
	 * @param divisor Expression this should be divided by.
	 * @return this/divisor. Should contain no references to divisor or this.
	 */
	public Expression divide(Expression divisor) {
		return multiply(divisor.invert());
	}

	/**
	 * Adds an Expression to this.
	 * @param toAdd the Expression to be added
	 * @return this+toAdd. Should contain no references to this or toAdd,
	 */
	public Expression add(Expression toAdd) {
		Expression retrn = clone();
		retrn.terms.addAll(toAdd.clone().terms);
		retrn.simplifyTerms();
		return retrn;
	}

	/**
	 * Adds a Term to this.
	 * @param toAdd the Term to be added
	 * @return this+toAdd. Should contain no references to this or toAdd.
	 */
	public Expression add(Term toAdd) {
		Expression retrn = clone();
		retrn.terms.add(toAdd);
		retrn.simplifyTerms();
		return retrn;
	}

	/**
	 * Subtracts an Expression from this.
	 * @param toSubtract the Expression to be subtracted
	 * @return this-toSubract. Should contain no references to this or toSubtract.
	 */
	public Expression subtract(Expression toSubtract) {
		return add(toSubtract.negate());
	}

	/**
	 * Subtracts a Term from this.
	 * @param toSubtract The Term to be subtracted.
	 * @return this-toSubract. Should contain no references to this or toSubtract.
	 */
	public Expression subtract(Term toSubtract) {
		return add(toSubtract.multiply(Term.NEGATE));
	}

	/**
	 * @return -this
	 */
	public Expression negate() {
		return multiply(Expression.NEGATIVE);
	}

	/**
	 * @return 1/this
	 */
	public Expression invert() {
		return raise(Constant.NEGATE);
	}

	/**
	 * Gets the greatest common denominator of the gcd of the terms in each
	 * @param a One Expression to find the gcd of.
	 * @param b The other Expression to find the gcd of.
	 * @return The greatest common denominator of the gcd of the terms in each.
	 */
	public static Expression gcd(Expression a, Expression b) {
		// Factors each Expression
		ArrayList<Expression> faca = a.factor();
		ArrayList<Expression> facb = b.factor();
		Expression gcd = new Expression();
		// If both have a single Term factor, adds the gcd of that to gcd and removes those factors.
		if(faca.get(0).terms.size() == 1 && facb.get(0).terms.size() == 0) {
			gcd.terms.add(Term.gcd(faca.get(0).terms.get(0), facb.get(0).terms.get(0)));
			faca.remove(0);
			facb.remove(0);
		} else {
			gcd = ONE;
		}
		// Finds all identical factors multiplies them into gcd
		for(Expression exp : faca) {
			int i = facb.indexOf(exp);
			if(i > -1) {
				gcd = gcd.multiply(exp);
				facb.remove(i);
			}
		}
		return gcd;
	}

	/**
     * Evaluates the Expression/function at the given Constant.
     * @param x The x-coordinate where the function is being evaluated.
     */
    //TODO code evauluate in term.
    //public void evaluate(char variable, Constant value) {
    //    for (Term term : terms) {
    //        term.evaluate(variable, value);
    //    }
    //    simplifyTerms();
    //}

    /**
	 * Combines like Terms, removes 0 terms, and distributes what it can.
	 */
	public void simplifyTerms() {
		// Finds all undistr in terms that can be distributed, removes them from the Term, and distributes them
		// and adds them to this.
		for(int i = 0; i < terms.size(); i++) {
			Iterator<Entry<Expression, Expression>> iter = terms.get(i).undistr.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Expression, Expression> current = iter.next();
				if(current.getValue().isConstant()
						&& (current.getValue().terms.get(0).coeff.getDenominator().equals(BigInteger.ONE)
								&& current.getValue().terms.get(0).coeff.getNumerator().compareTo(BigInteger.ONE) >= 0)) {
					iter.remove();
					terms.addAll(new Expression(terms.get(i)).multiply(current.getKey()
							.raise(current.getValue().terms.get(0).coeff)).terms);
					terms.remove(i--);
				}
			}
		}
		// Combines like terms by checking each term against each after them.
		for(int i = 0; i < terms.size(); i++) {
			for(int j = terms.size() - 1; j > i; j--) {
				try {
					if(Term.isLikeTerm(terms.get(j), terms.get(i))) {
						terms.get(i).coeff = terms.get(i).coeff.add(terms.get(j).coeff);
						terms.remove(j);
					}
				} catch(DifferentRoots e) {

				}
			}
		}
		// Removes all terms with the coefficient of 0.
		terms.removeIf(t -> t.coeff.equals(new Constant()));
		Collections.sort(terms);
	}

	/**
	 * Factors out the gcd, and if the remaining expression is a quadratic, factors that with the quadratic formula. If
	 * the expression has a factor which is a single Term, it will be first in the ArrayList.
	 * @return The factors of this. None will be equations.
	 */
	public ArrayList<Expression> factor() {
		// The list of factors to be returned
		ArrayList<Expression> ans = new ArrayList<>();
		Term fact = Term.gcd(terms);
		// Takes all variables and expressions raised to a negative power and multiplies them to fact.
		for(Term current : terms) {
			for(Entry<Character, Constant> on : current.vars.entrySet()) {
				// Adds this variable and power to fact if it is negative and fact doen't already have it.
				if(on.getValue().compareTo(new Constant()) < 0 && !(fact.vars.containsKey(on.getKey())
						&& fact.vars.get(on.getKey()).compareTo(on.getValue()) >= 0)) {
					fact.addExponent(on.getKey(), on.getValue());
				}
			}
			// Takes all undistr with a negative power and multiplies them into the factor.
			for(Entry<Expression, Expression> on : current.undistr.entrySet()) {
				if(on.getValue().isConstant()) {
					Constant ons = on.getValue().terms.get(0).coeff;
					if(ons.compareTo(new Constant()) < 0) {
						// Multiplies into fact if fact doesn't already have it or if it has a lower power
						if(!fact.undistr.containsKey(on.getKey()) || !fact.undistr.get(on.getKey()).isConstant()) {
							fact.addExponent(on.getKey(), on.getValue());
						} else if(fact.undistr.get(on.getKey()).terms.get(0).coeff.compareTo(ons) < 0) {
							fact.undistr.put(on.getKey(), on.getValue());
						}
					}
				}
			}
		}
		// Adds fact to the list of factors if it isn't one
		if(!fact.equals(new Term(new Constant(1)))) {
			Expression add = new Expression(fact);
			ans.add(add);
		}
		// Divides the expression by fact in order and proceeds to factor that.
		Expression remaining = divide(new Expression(fact));
		final Constant two = new Constant(2);
		// Checks to see if this is a quadratic.
		if(remaining.getDegree().equals(two) && getVars().size() == 1) {
			// Finds the variable to factor by.
			char factBy = '\0';
			Iterator<Term> term = terms.iterator();
			while(factBy == '\0') {
				Term current = term.next();
				HashSet<Character> chars = current.getVars();
				if(chars.size() > 0) {
					factBy = chars.iterator().next();
				}
			}
			// Looks for the a b and c of the quadratic equation.
			Expression a = new Expression(), b = new Expression(), c = new Expression();
			// Goes through each term and adds the coeff of factBy to the proper variable a, b, or c.
			for(Term current : remaining.terms) {
				Term noVar = current.clone();
				Constant pow = noVar.vars.remove(factBy);
				if(pow == null) {
					c.terms.add(noVar);
				} else if(pow.equals(Constant.ONE)) {
					b.terms.add(noVar);
				} else if(pow.equals(two)) {
					a.terms.add(noVar);
				}
			}
			final Expression expTwo = new Expression(new Term(two));
			// Plugs a, b, and c into the quadratic formula.
			Term discrim = new Term(Constant.ONE.clone());
			// Puts b^2-4ac in to a square root
			discrim.addExponent(b.raise(expTwo).subtract(new Expression(new Constant(4)).multiply(a.multiply(c))),
					new Expression(new Constant(1, 2)));
			discrim.simplifyTerm();
			// Expression that hold each factor. Plus will hold x+(b+(b^2-4ac)^1/2)/2a, while minus will hold
			// x+(b-(b^2-4ac)/2a
			Expression plus = new Expression(), minus;
			// Adds b to plus
			plus = plus.add(b);
			minus = plus.clone();
			// Adds (b^2-4ac)^1/2 to plus
			plus = plus.add(discrim);
			// Subtracts (b^2-4ac)^1/2 from minus
			minus = minus.subtract(discrim);
			// Creates the 1/2a
			Expression negTwoA = expTwo.multiply(a).invert();
			// Divides both plus and minus by 2a
			plus = negTwoA.multiply(plus);
			minus = negTwoA.multiply(minus);
			// The solutions of a quadratic are the answers generated by the quadratic formula in school
			// However, the factors are x+(b+-(b^2-4ac)^1/2)/2a. This adds the "x" to the factors.
			plus = plus.add(new Term(factBy));
			minus = minus.add(new Term(factBy));
			// Adds the factors to the list of factors.
			ans.add(plus);
			ans.add(minus);
			return ans;
		}
		// If what is left after being divided by the gcd is not one, add it to the factor. However, if the
		// Expression is one, then the gcd will be one, so it won't be added. Then this won't be added, returning
		// an empty list. To prevent this, it checks if there is nothing in the list and adds if there isn't.
		if(ans.size() == 0 || !remaining.equals(Expression.ONE)) {
			ans.add(remaining);
		}
		return ans;
	}

	/**
	 * Solves the Expression for each variable as best as possible.
	 * @return A HashSet of solutions, one for each variable.
	 * @throws NotEquation If the Expression is not an Equation.
	 */
	public HashSet<Solution> solve() throws NotEquation {
		if(!isEquation) {
			throw new NotEquation();
		}
		// HashSet to hold list of solutions
		HashSet<Solution> solutions = new HashSet<>();
		// Solves each factor as if it were equal to 0
		for(Expression current : factor()) {
			HashSet<Character> vars = current.getVars();
			// Removes i, the imaginary unit, from the variables.
			vars.remove(Term.interE);
			vars.remove(Term.interE);
			for(char cur : vars) {
				try {
					Set<Expression> s = current.solveFact(cur);
					// Attempts to add this to a existing solution for the current variable, else creates a new one.
					Iterator<Solution> iter = solutions.iterator();
                    Solution found = null;
					while(iter.hasNext() && found == null) {
                        Solution find = iter.next();
						if(find.letter == cur) {
							found = find;
                            iter.remove();
                            found.value.addAll(s);
						}
					}
					if(found == null)
						found = new Solution(cur, s);
                    solutions.add(found);
				} catch(NotAbleToSolve e) {

				}
			}
		}
		return solutions;
	}

	/**
	 * Solves the equation for the specified variable
	 * @param iso The variable to solve for.
	 * @return The solution for iso
	 * @throws NotEquation If this isn't an equation.
	 * @throws NotAbleToSolve If this cannot be solved.
	 */
	public Solution solveFor(char iso) throws NotEquation, NotAbleToSolve {
		if(!isEquation) {
			throw new NotEquation();
		}
		Solution s = new Solution(iso);
		// Solves each factor as if it were equal to zero for the requested variable
		for(Expression current : factor()) {
			try {
				// Solves the factor and adds it to solution if not null.
				Set<Expression> solution = current.solveFact(iso);
				if(solution != null) {
					s.value.addAll(solution);
				}
			} catch(NotAbleToSolve e) {
				s.allPossible = false;
			}
		}
		if(s.value.size() == 0) {
			throw new NotAbleToSolve("None of the factors of this could be solved.");
		}
		return s;
	}

	/**
	 * Solves a factor for a single variable. This method is only capable of solving Expressions where iso is raised to a
	 * single unique power.
	 * @param iso The variable to be solved for.
	 * @return What iso is equal to. Null if iso is not in the Expression.
	 * @throws NotAbleToSolve If this algorithm is not able to solve the equation.
	 */
	private HashSet<Expression> solveFact(char iso) throws NotAbleToSolve {
		// Checks to see how many unique powers of iso are in it.
		Constant firstPow = null, secondPow = null;
		boolean inUndistr = false;
		// Goes through each term to find its power of iso.
		for(Term current : terms) {
			Constant pow = current.vars.get(iso);
			if(pow == null) {
				pow = new Constant();
			}
			// Attempts to see if we have not found a first power yet or this is the same as it. If so
			// sets it as first, else tries the same with second, and if that fails it can't be solved with this.
			if(firstPow == null || pow.equals(firstPow)) {
				firstPow = pow;
			} else if(secondPow == null || pow.equals(secondPow)) {
				secondPow = pow;
			} else {
				throw new NotAbleToSolve("The equation is to the second degree or higher.");
			}
			// Goes through this terms undistr for anything this can't solve.
			for(Entry<Expression, Expression> exp : current.undistr.entrySet()) {
				if(exp.getKey().getVars().contains(iso)) {
					// Checks to see if this term has something like x(x+4)^z
					if(!pow.equals(new Constant())) {
						throw new NotAbleToSolve(iso
								+ " is multiplied by itself raised to variable or the root of the sum of itself and "
								+ "another expression");
					}
					// Checks to see if this equation is something like (x+4)^(1/2)+(x+5)^(1/2)
					if(inUndistr) {
						throw new NotAbleToSolve(iso + " is added to something, raised to a non-integer expression, and "
								+ "added to a similar term");
					}
					inUndistr = true;
				}
				if(exp.getValue().getVars().contains(iso)) {
					throw new NotAbleToSolve(iso + " is in an exponent. Logarithms will be added in future versions.");
				}
			}
		}
		// Returns null if iso isn't in this equation.
		if(firstPow == null && secondPow == null) {
			return null;
		}
		if(firstPow != null && secondPow != null) {
			// Checks to see if this is something like x^2+x=0
			if(!firstPow.equals(new Constant()) && !secondPow.equals(new Constant())) {
				throw new NotAbleToSolve("This equation requires factoring");
			}
			// Checks to see if this is something like x+(x+3)^z
			if((!firstPow.equals(new Constant()) || !secondPow.equals(new Constant())) && inUndistr) {
				throw new NotAbleToSolve("This equation has " + iso
						+ " added to the sum it and something else, raised to something.");
			}
		}
		// Attempts to solve for iso. Assumes that it is in one of two general patterns and solves accordingly:
		// ax^c+bx^c...+d+e...=0 solves to x=(-(d+e...)/(a+b...))^(1/c), where x is iso, c is any expression
		// without iso and there are any number of terms with x^c or no x.
		// i(ax^c+bx^c...+d+e...)^f+g+h...=0 solves to (((-(g+h...)/i)^(1/f)-(d+e...))/(a+b...))^(1/c), where
		// x is iso c and f are any expression without iso, there are any number of terms with out x where
		// g and h or d and e are, and there are any terms with x^c where ax^c+bx^c are.
		Expression hasVar = new Expression();
		Expression noVar = new Expression();
		Constant root = Constant.ONE;
		// Divides the terms according to which have iso in them and which don't
		for(Term currentTerm : terms) {
			if(currentTerm.getVars().contains(new Character(iso))) {
				hasVar.terms.add(currentTerm);
			} else {
				noVar.terms.add(currentTerm);
			}
		}
		noVar = noVar.negate();
		// Holds the a+b... of the first pattern or the i of the second.
		ArrayList<Term> divide = new ArrayList<>(hasVar.terms.size());
		// Used it this equation matches the second pattern, holds the (ax^c+bx^c...+d+e...)^f
		Entry<Expression, Expression> isoIn = null;
		for(Term now : hasVar.terms) {
			Term a = now.clone();
			Constant r = a.vars.remove(iso);
			if(r == null) {
				Iterator<Entry<Expression, Expression>> iter = a.undistr.entrySet().iterator();
				// If this has x but it isn't in vars, it must be in undistr, so this must follow the second pattern.
				// This goes through this terms undistr to find it.
				while(isoIn == null) {
					Entry<Expression, Expression> current = iter.next();
					if(current.getKey().getVars().contains(iso)) {
						isoIn = current;
						iter.remove();
					}
				}
				// Finds the c of the first pattern.
			} else {
				root = r;
			}
			divide.add(a);
		}
		Expression retrn;
		// Divides and and roots by c if necessary.
		if(root.equals(Constant.ONE)) {
			retrn = new Expression(divide).invert().multiply(noVar);
		} else {
			retrn = noVar.divide(new Expression(divide)).raise(new Expression(new Term(root)).invert());
		}
		// Checks to see if this pattern one or two, and returns accordingly
		if(isoIn == null) {
			return new HashSet<>(Collections.singleton(retrn));
		} else {
			// Roots both sides to get rid of the exponent of isoIn, then moves it back to the same side.
			retrn = retrn.raise(isoIn.getValue().invert()).multiply(NEGATIVE);
			// Puts retrn into the same Expression as isoIn's base, to attempt to solve it again.
			Expression toSolve = isoIn.getKey().clone().add(retrn);
			toSolve.isEquation = true;
			try {
				Solution solvedIsoIn = toSolve.solveFor(iso);
				return new HashSet<>(solvedIsoIn.value);
			} catch(NotAbleToSolve e) {
				throw new NotAbleToSolve("The expression " + iso + " is in was unable to be solved.", e);
			}
		}
	}

	/**
	 * Checks to see if this Expression is a single Term that is a constant.
	 * @return If this is a constant true, else false.
	 */
	public boolean isConstant() {
		return terms.size() == 1 && terms.get(0).isConstant();
	}

	/**
	 * Gets the variables in this.
	 * @return All variables in this.
	 */
	public HashSet<Character> getVars() {
		HashSet<Character> vars = new HashSet<>();
		for(Term current : terms) {
			vars.addAll(current.getVars());
		}
		return vars;
	}

	/**
	 * Gets the degree of the equation, or the highest power of a variable.
	 * @return the degree of the equation.
	 */
	public Constant getDegree() {
		Constant degree = new Constant();
		for(Term current : terms) {
			for(Constant pow : current.vars.values()) {
				if(pow.compareTo(degree) > 0) {
					degree = pow;
				}
			}
		}
		return degree;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Expression o) {
		return toString().compareTo(o.toString());
	}

	/**
	 * Checks if this is the same expression or equation as a.
	 * @param a Expression to check if this is the same as.
	 * @return If this is the same as a, true, else false.
	 */
	@Override
	public boolean equals(Object a) {
		//Sees if a is either an Expression, Term or Constant and checks equality with each.
		if(a instanceof Expression) {
			Expression b = (Expression)a;
			return terms.equals(b.terms) && isEquation == b.isEquation;
		} else if(a instanceof Term) {
			return terms.size() == 1 && terms.get(0).equals(a);
		} else if(a instanceof Constant) {
			return isConstant() && terms.get(0).coeff.equals(a);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return terms.hashCode() << 1 | (isEquation ? 1 : 0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(terms.isEmpty()) {
			return "0";
		}
		StringBuffer output = new StringBuffer();
		// Appends each Term to the String.
		terms.forEach(output::append);
		// Removes the plus sign from the first Term if present.
		if(output.charAt(0) == '+') {
			output.deleteCharAt(0);
		}
		if(isEquation) {
			output.append("=0");
		}
		return output.toString();
	}

	/**
	 * Replaces \u03c0 and e with their corresponding value and approximates all square roots.
	 * @return An approximation of this
	 */
	public Expression approx() {
		Expression retrn = new Expression();
		retrn.isEquation = isEquation;
		//Returns the approximation of each Term simplified.
		terms.forEach(t -> retrn.terms.add(t.approx()));
		retrn.simplifyTerms();
		return retrn;
	}

	/**
	 * @param places decimal places to round to.
	 * @return A String representation of this with decimals rounded to places rather than fractions.
	 */
	public String toStringDecimal(int places) {
		if(terms.isEmpty()) {
			return "0";
		}
		StringBuffer output = new StringBuffer();
		// Appends the approximation of each term to the String.
		terms.forEach(s -> output.append(s.toStringDecimal(places)));
		if(output.charAt(0) == '+') {
			output.deleteCharAt(0);
		}
		if(isEquation) {
			output.append("=0");
		}
		return output.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Expression clone() {
        Expression a = null;
        try {
            a = (Expression) super.clone();
            a.terms = (ArrayList<Term>) a.terms.clone();
            a.terms.replaceAll(Term::clone);
        }catch (CloneNotSupportedException e){
        }
		return a;
	}

	/**
	 * Class to sort Terms by the power of a certain variable.
	 *
	 * @author Luke Senseney
	 */
	public static final class SortByChar implements Comparator<Term> {
		char sortBy;

		/**
		 * Creates a new SortByChar
		 * @param sort The character whose power to sort by.
		 */
		SortByChar(char sort) {
			sortBy = sort;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Term o1, Term o2) {
			Constant c1 = o1.vars.get(sortBy);
			Constant c2 = o2.vars.get(sortBy);
			if(c1 == null) {
				if(c2 == null) {
					return 0;
				} else {
					return 1;
				}
			}
			return c1.compareTo(c2);
		}
	}
    public boolean isEquation() {
        return isEquation;
    }

    public void setEquation(boolean isEquation) {
        this.isEquation = isEquation;
        simplifyTerms();
    }
}
