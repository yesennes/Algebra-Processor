package userIO;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
			public void run()
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
		Font font=new Font("Cambria Math",Font.PLAIN,12);
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
		add(output,new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
	}

	/**
	 * Enters a new equation.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Expression exp=new Expression(input.getText().replaceAll("\\s",""));
		output.setText(exp.toString());
		if(exp.isEquation)
			for(Solution current:exp.solve())
				output.append("\n"+current.toString());
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
