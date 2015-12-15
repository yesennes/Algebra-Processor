package lang;

import java.util.ArrayList;

/**
 * Unused
 * @author Luke Senseney
 */
public class SystemEquations { // Start class
	private ArrayList<Expression> equations = new ArrayList<>();
	
	@SuppressWarnings("unused") 
	private ArrayList<Solution> solutions = new ArrayList<>();

	public SystemEquations() {
	}

	public SystemEquations(String firstEquation) {
		this.addEquation(firstEquation);
	}

	public void addEquation(String newEquation) { // Start addEquation
		equations.add(new Expression(newEquation));
	}
}
