package userIO;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JDialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;







import lang.Expression;
import lang.MathFormatException;
import lang.OverflowException;
import lang.Solution;
import lang.Term;

/**
 * The default GUI for the AlgebraProcessor.
 * 
 * @author Luke Senseney
 */
public class UserIO extends JFrame implements ActionListener
{
	private static final long serialVersionUID=1L;
	/**
	 * User input field.
	 */
	private JTextField input=new JTextField();
	/**
	 * Displays solutions and standard form.
	 */
	private JTextArea output=new JTextArea();
	private JComboBox<String> approx;
	private PrecisionModel digits=new PrecisionModel();
	/**
	 * The font to be used for this.
	 */
	private final Font font=new Font("Cambria Math",Font.PLAIN,14);
	/**
	 * A String holding the imaginary unit, i.
	 */
	private static final String imagUnit=Term.IMAG_UNIT;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels())
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
		}catch(Exception e)
		{}
		EventQueue.invokeLater(new Runnable()
		{
			@Override public void run()
			{
				try
				{
					UserIO frame=new UserIO();
					frame.setVisible(true);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UserIO()
	{
		// Configures this window.
		setTitle("Algebra Processor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(450,275));
		setLayout(new GridBagLayout());
		GridBagConstraints g=new GridBagConstraints();
		// Sets up input.
		input.addActionListener(this);
		input.setFont(font);
		g.gridy=0;
		g.weightx=1;
		g.fill=GridBagConstraints.BOTH;
		// Places input on the top left, taking up all extra room horizontally
		add(input,g);
		// Configures the enter button.
		JButton enter=new JButton("Enter");
		enter.addActionListener(this);
		g.weightx=0;
		// Places input on the top right, not taking up extra room
		add(enter,g);
		// Makes a row of buttons, and adds the button for the imaginary number.
		JToolBar buttons=new JToolBar();
		buttons.setFloatable(false);
		// Sets up the button for the imaginary unit.
		JButton imag=new JButton(imagUnit);
		imag.addActionListener(new InsertString(imagUnit));
		imag.setFont(font);
		buttons.add(imag);
		buttons.addSeparator();
		//Sets up the button for \u03c0.
		JButton pi=new JButton(String.valueOf(Term.PI));
		pi.addActionListener(new InsertString(String.valueOf(Term.PI)));
		pi.setFont(font);
		buttons.add(pi);
		buttons.addSeparator();
		//Sets up the button for e.
		JButton e=new JButton(Term.E);
		e.addActionListener(new InsertString(Term.E));
		e.setFont(font);
		buttons.add(e);
		
		g.gridx=0;
		g.gridy=1;
		g.gridwidth=2;
		g.weightx=1;
		// Adds the toolbar going across the middle of the window.
		add(buttons,g);
		// Sets up the output box.
		output.setEditable(false);
		output.setFont(font);
		output.setMaximumSize(new Dimension(1000,1000));
		output.setText("Enter an equation in the box above, or click on the \"?\" in the bottom right for more help.");
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		// Creates the help button
		JButton help=new JButton("?");
		// Sets the help button to open up a pop up.
		help.addActionListener(l -> {
			JDialog helper=new JDialog(this,"Help Menu");
			JTextArea helpArea=new JTextArea(
					"\tWelcome to AlgebraProcessor Beta! Simply enter an expression or equation in the top box, press enter"
							+" or hit the enter key, and it will write it standard form, simplify it, factor it, and, if it is an equation, solve it. Use t"
							+"he \"^\" to enter exponents. To take the nth root of a number, raise it to 1/n. For instance, to take the square root of x, e"
							+"nter \"x^(1/2)\". To enter "+imagUnit+", the imaginary unit, press the buttion with "+imagUnit
							+" on it. If "+imagUnit+" is showing up as a "+new String(Character.toChars(0x1F700))+" to you, t"
							+"hen your computer doesn't have the font Cambria Math on it. Simply pretend squares are the imaginary unit. The same holds true for the mathmatical constant e. Later versions wil"
							+"l remove dependency on Cambria Math. It ignores whitespace, but will treat other, non-letter symbols like \"!\" as variables."
							+"\n\n\tCurrently it its capable of simplifying about anything, factoring out gcd and factoring quadratics. It can solve quadrat"
							+"ics and 2 step equations.\n\n\tGarbage in, Garbage out; currently, if you enter an"
							+"ything that doesn't make mathmatical sense, it may give an error or it may try to interpret you tried to enter. Later version may fix this. This product is still in beta, so it may give garbage out anyway."
							+"If you enter a proper expression or equation and it gives an error or wrong answer, please email what you entered to yesennes@gmail."
							+"com.\n\n\tUpdates will come periodically, to receive them send an email to yesennes@gmail.com requesting to be put on the ema"
							+"il list. Upcoming features include support for functions such as sine, better display with fractions actually stacked, bett"
							+"er input with superscript and \u221as, factoring and solving of more complex expression, constants like \u03c0 and "+new String(Character.toChars(0x1d4ee))
							+" and a button for approximate answers. If you would like to see any other features, send an email to yesennes@gmail.com."
							+ "\n\n\tP.S. I am still trying to think of a good name for this program. Any suggustions would be welcome.");
			helpArea.setLineWrap(true);
			output.setWrapStyleWord(true);
			helpArea.setFont(font);
			helper.add(helpArea);
			helper.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			helper.setMinimumSize(new Dimension(500,510));
			helper.setVisible(true);
		});
		//Sets up the dialog box for settings.
		JDialog set=new JDialog(this,"Settings");
		set.setMinimumSize(new Dimension(500,150));
		set.setLayout(new GridBagLayout());
		//Adds the mode label
		g.gridx=GridBagConstraints.RELATIVE;
		g.gridy=0;
		g.weightx=0;
		g.gridwidth=1;
		g.fill=GridBagConstraints.NONE;
		set.add(new JLabel("Mode:"),g);
		//Creates the label to explain the approximation mode.
		JLabel descrip=new JLabel("All non-rational numbers will be kept in as mathmatical constants and exponents.");
		//Creates the dropdown selection of mode.
		approx=new JComboBox<String>(new String[]{"Exact","Approximate"});
		//Sets up the selection box for rounding.
		JSpinner precision=new JSpinner(digits);
		precision.setEnabled(false);
		//When approx changes, notifies precision.
		approx.addActionListener(event->{
			if(approx.getSelectedItem().equals("Exact"))
			{
				digits.setValue(digits.getNumber());
				precision.setEnabled(false);
			}else
			{
				precision.setEnabled(true);
				digits.setValue(digits.getNumber());
			}
		});
		g.weightx=1;
		g.fill=GridBagConstraints.HORIZONTAL;
		set.add(approx,g);
		//Adds the label for precision.
		g.gridx=0;
		g.gridy=1;
		g.weightx=0;
		g.fill=GridBagConstraints.NONE;
		set.add(new JLabel("Precision:"),g);
		//When precision changes, corrects descrip.
		precision.addChangeListener(event->{
			if(approx.getSelectedItem().equals("Exact"))
				descrip.setText("All non-rational numbers will be kept in as mathmatical constants and exponents.");
			else if(digits.getNextValue().intValue()==0)
				descrip.setText("All non-rational numbers will be approximated to the nearest whole number");
			else
				descrip.setText("All non-rational numbers will be approximated to "+digits.getValue()+" places after the decimal.");
		});
		g.gridx=1;
		g.weightx=1;
		g.fill=GridBagConstraints.HORIZONTAL;
		set.add(precision,g);
		
		g.gridy=2;
		g.gridx=0;
		g.gridwidth=2;
		g.weighty=1;
		set.add(descrip,g);
		//Sets up the button for settings.
		JButton settings=new JButton("Settings"/*new ImageIcon("gear.png")*/);
		settings.addActionListener(event->set.setVisible(true));
		g.gridx=0;
		g.gridy=2;
		g.gridwidth=2;
		g.weightx=1;
		g.weighty=1;
		g.anchor=GridBagConstraints.SOUTHWEST;
		g.fill=GridBagConstraints.NONE;
		//Help, settings and output in the same place, with the help in the bottom right corner not resizing, and the output taking up all the room.
		add(settings,g);
		g.anchor=GridBagConstraints.SOUTHEAST;
		add(help,g);
		// Adds output last so help is on top.
		g.anchor=GridBagConstraints.CENTER;
		g.fill=GridBagConstraints.BOTH;
		add(output,g);
	}

	/**
	 * Enters a new equation.
	 */
	@Override public void actionPerformed(ActionEvent e)
	{
		output.setWrapStyleWord(false);
		try
		{
			// Creates an Expression and factors it, then if it is a equation, solves it.
			boolean appro=approx.getSelectedItem().equals("Exact");
			Expression exp=new Expression(input.getText());
			output.setText("Standard Form:"+(appro?exp:exp.approx().toStringDecimal(digits.getNumber()))+"\n");
			output.append("Factored:");
			ArrayList<Expression> facts=exp.factor();
			for(Expression fact:facts)
				output.append("("+(appro?fact:fact.approx().toStringDecimal(digits.getNumber()))+")");
			if(exp.isEquation)
				output.append("=0");
			output.append("\n");
			if(exp.isEquation)
			{
				output.append("Solutions:");
				HashSet<Solution> sols=exp.solve();
				if(sols.size()==0)
					output.append(" Was not able to solve");
				else
					for(Solution current:sols)
						output.append((appro?current:current.approx(digits.getNumber()))+"\n                    ");
			}
		}catch(MathFormatException format)
		{
			output.setWrapStyleWord(true);
			output.setText("There was an error in your formatting. "+format.getMessage()+" If you think what you entered was formatted correctly, please email what you entered to yesennes@gmail.com");
		}catch(OverflowException over)
		{
			output.setWrapStyleWord(true);
			output.setText("A number was to big. "+over.getMessage());
		}
		catch(Exception a)
		{
			output.setWrapStyleWord(true);
			output.setText("An error occured. Please email what you entered to yesennes@gmail.com");
			a.printStackTrace();
		}
		output.repaint();
	}

	/**
	 * @author Luke Senseney
	 * A model to show the precision, or Fractions when appropriate.
	 */
	public class PrecisionModel extends AbstractSpinnerModel
	{
		private static final long serialVersionUID=1L;
		Integer value=3;
		
		public PrecisionModel()
		{
		}
		
		@Override public Object getValue()
		{
			if(approx.getSelectedItem()=="Exact")
				return "Fractions";
			return value;
		}

		/* (non-Javadoc)
		 * @see javax.swing.SpinnerModel#setValue(java.lang.Object)
		 */
		@Override public void setValue(Object newValue)
		{
			try
			{
				Integer v=(Integer)newValue;
				if(v.intValue()<0)
					value=0;
				else
					value=v;
				fireStateChanged();
			}catch(ClassCastException e)
			{
				throw new IllegalArgumentException();
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.SpinnerModel#getNextValue()
		 */
		@Override public Integer getNextValue()
		{
			return value+1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.SpinnerModel#getPreviousValue()
		 */
		@Override public Object getPreviousValue()
		{
			return value.equals(0)?-1:value-1;
		}
		
		public Integer getNumber()
		{
			return value;
		}
	}

	/**
	 * Adds a String to the end of input when fired.
	 * @author Luke Senseney
	 *
	 */
	private class InsertString implements ActionListener
	{
		private String insert;

		public InsertString(String s)
		{
			insert=s;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override public void actionPerformed(ActionEvent e)
		{
			input.setText(input.getText()+insert);
		}
	}
}// Glory to God
