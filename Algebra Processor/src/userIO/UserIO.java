package userIO;

import javax.swing.JFrame;
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
import lang.Solution;

/**
 * The default GUI for the AlgebraProcessor.
 * 
 * @author Luke Senseney
 *
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
	/**
	 * The font to be used for this.
	 */
	private final Font font=new Font("Cambria Math",Font.PLAIN,14);
	/**
	 * A String holding the imaginary unit, i.
	 */
	private final String imagUnit=new String(Character.toChars(120050));

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
		// Sets up input.
		input.addActionListener(this);
		input.setFont(font);
		// Places input on the top left, taking up all extra room horizontally
		add(input,new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		// Configures the enter button.
		JButton enter=new JButton("Enter");
		enter.addActionListener(this);
		// Places input on the top right, not taking up extra room
		add(enter,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		// Makes a row of buttons, and adds the button for the imaginary number.
		JToolBar buttons=new JToolBar();
		buttons.setFloatable(false);
		// Sets up the button for the imaginary unit.
		JButton imag=new JButton(imagUnit);
		imag.addActionListener(new InsertString(imagUnit));
		imag.setFont(font);
		buttons.add(imag);
		buttons.addSeparator();
		// Adds the toolbar going across the middle of the window.
		add(buttons,new GridBagConstraints(0,1,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
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
							+"hen your computer doesn't have the font Cambria Math on it. Simply pretend squares are the imaginary unit. Later versions wil"
							+"l remove dependency on Cambria Math. It ignores whitespace, but will treat other, non-letter symbols like \"!\" as variables."
							+"\n\n\tCurrently it its capable of simplifying about anything, factoring out gcd and factoring quadratics. It can solve quadrat"
							+"ics and 2 step equations.\n\n\tGarbage in, Garbage out; currently, if you enter an"
							+"ything that doesn't make mathmatical sense, it may give an error or it may try to interpret you tried to enter. Later version may fix this. This product is still in beta, so it may give garbage out anyway."
							+"If you enter a proper expression or equation and it gives an error or wrong answer, please email what you entered to yesennes@gmail."
							+"com.\n\n\tUpdates will come periodically, to receive them send an email to yesennes@gmail.com requesting to be put on the ema"
							+"il list. Upcoming features include support for functions such as sine, better display with fractions actually stacked, bett"
							+"er input with superscript and \u221as, factoring and solving of more complex expression, constants like \u03c0 and "+new String(Character.toChars(0x1d4ee))
							+" and a button for approximate answers. If you would like to see any other features,"+" send an email to yesennes@gmail.com.");
			helpArea.setLineWrap(true);
			output.setWrapStyleWord(true);
			helpArea.setFont(font);
			helper.add(helpArea);
			helper.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			helper.setMinimumSize(new Dimension(500,510));
			helper.setVisible(true);
		});
		// Both help and output in the same place, with the help in the bottom right corner not resizing, and the output taking up all the room.
		// Adds output last so help is on top.
		add(help,new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.SOUTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		add(output,new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
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
			Expression exp=new Expression(input.getText());
			output.setText("Standand Form:"+exp.toString()+"\n");
			output.append("Factored:");
			ArrayList<Expression> facts=exp.factor();
			for(Expression fact:facts)
				output.append("("+fact+")");
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
						output.append(current.toString()+"\n                    ");
			}
		}catch(Exception a)
		{
			output.append(" An error occured. Please email what you entered to yesennes@gmail.com");
			a.printStackTrace();
		}
		output.repaint();
	}

	// Adds a String to the end of input when fired.
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
