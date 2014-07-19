package userIO;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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
	private final Font font=new Font("Cambria Math",Font.PLAIN,14);
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}catch(Exception e)
		{
		}
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
		setTitle("Algebra Processor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(450,275));
		setLayout(new GridBagLayout());
		input.addActionListener(this);
		input.setFont(font);
		add(input,new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		JButton enter=new JButton("Enter");
		enter.addActionListener(this);
		JToolBar buttons=new JToolBar();
		add(enter,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		buttons.setFloatable(false);
		JButton imag=new JButton(new String(Character.toChars(120050)));
		imag.addActionListener(new Imaginary());
		imag.setFont(font);
		buttons.add(imag);
		buttons.addSeparator();
		add(buttons,new GridBagConstraints(0,1,2,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		output.setEditable(false);
		output.setFont(font);
		output.setMaximumSize(new Dimension(1000,1000));
		JLayeredPane pane=new JLayeredPane();
		pane.setLayout(new GridBagLayout());
		pane.add(output,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		JButton help=new JButton("?");
		help.addActionListener(l->{
			JDialog helper=new JDialog(this,"Help Menu");
			JTextArea helpArea=new JTextArea("\tWelcome to AlgebraProcessor Beta! Simply enter an expression or equation in the top box, press enter"
					+ " or hit the enter key, and it will put it standard form, simplify it, factor it, and, if it is an equation, solve it. Use the"
					+ " \"^\" to enter exponents. If enter a root, raise it to 1/x, for instance, to take the square root of x, enter \"x^(1/2)\". T"
					+ "o enter "+new String(Character.toChars(120050))+", the imginary unit, press the buttion with "
					+new String(Character.toChars(120050))+" on it. If "+new String(Character.toChars(120050))+" is showing up as a square to you, t"
					+ "hen your computer doesn't have the font Cambria Math on it. Simply pretend squares are the imaginary unit. Later versions wil"
					+ "l remove dependency on Cambria Math.It ignores whitespace, but will treat other, non-letter symbols like \"!\" as variables."
					+ "\n\n\tCurrently it its capable of simplifying about anyting, factoring out gcd and factoring quadratics. It can solve quadrat"
					+ "ics and 2 step equations./n/n/tRemeber that the program is still in beta. Garbage in, Garbage out; Currently, if you enter an"
					+ "ything that doesn't make mathmatical sense, it may give an error or it may make a guess at what you tried to enter. If you e"
					+ "nter a proper expression or equation and it gives an error or wrong answer, please email what you entered to yesennes@gmail."
					+ "com.\n\n\tUpdates will come periodically, to receive them send an email to yesennes@gmail.com requesting to be put on the ema"
					+ "il list. Upcoming features include support for functions such as sine, better display with fractions actually stacked, bett"
					+ "er input with superscript and \u221as, factoring and solving of more complex expression, and a button for approximate answer"
					+ "s. If you would like to see any other features, send an email to yesennes@gmail.com");
			helpArea.setLineWrap(true);
			helpArea.setFont(font);
			helper.add(helpArea);
			helper.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			helper.setMinimumSize(new Dimension(500,500));
			helper.setVisible(true);
		});
		pane.add(help,new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.SOUTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		pane.setLayer(help,1);
		add(pane,new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
	}

	/**
	 * Enters a new equation.
	 */
	@Override public void actionPerformed(ActionEvent e)
	{
		Expression exp=new Expression(input.getText().replaceAll("\\s",""));
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
					output.append(current.toString()+"\n\t  ");
		}
		output.repaint();
	}
	
	//Adds a the imaginary unit to the end of input when fired. 
	private class Imaginary implements ActionListener
	{
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override public void actionPerformed(ActionEvent e)
		{
			input.setText(input.getText()+new String(Character.toChars(120050)));
		}
	}
}
