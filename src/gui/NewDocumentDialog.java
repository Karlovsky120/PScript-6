package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import core.Main;
import documentElements.Heading;
import documentElements.ListElement;
import documentElements.Page;
import documentElements.Question;
import engine.Resetter;

public class NewDocumentDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private JDialog dialog = this;
	private JTextArea text;
	private JPanel radioButtonContainer, buttonContainer;
	private JRadioButton questions, list;
	private ButtonGroup group;
	private JButton confirm, cancel;

	private static ResourceBundle lang = Main.lang;

	public NewDocumentDialog()
	{
		//JDialog
		super(Main.masterWindow, lang.getString("newTitle"), true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				confirm.requestFocus();
			}
		});

		//text in the dialog
		text = new JTextArea();
		text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
		text.setText(lang.getString("newMessage"));
		text.setEditable(false);
		text.setOpaque(false);
		text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

		//JRadioButtons with their text
		questions = new JRadioButton(lang.getString("questionsOption"), true);
		list = new JRadioButton(lang.getString("listOption"), false);

		//radio button group
		group = new ButtonGroup();
		group.add(questions);
		group.add(list);

		//JPanel for JRadioButtons for vertical spacing
		radioButtonContainer = new JPanel();
		radioButtonContainer.setLayout(new BoxLayout(radioButtonContainer, BoxLayout.Y_AXIS));
		radioButtonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
		radioButtonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		radioButtonContainer.add(questions);
		radioButtonContainer.add(list);

		//JButton for confirming
		confirm = new JButton(lang.getString("ok"));
		confirm.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				Resetter.reset("untitled");

				//initialize Page object
				Page p = new Page(1);

				//create first element depending on heading type
				if (questions.isSelected())
				{				
					Heading h = new Heading(p, 1, true, "");
					new Question(p, h, 2, 1, "");
					h.requestFocus();
				}
				else
				{
					Heading h = new Heading(p, 1, false, "");
					new ListElement(p, h, 2, 1, "");
					h.requestFocus();
				}

				//validate and repaint visually changed component
				Main.workspace.validate();
				Main.workspace.repaint();

			}		
		});

		confirm.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					confirm.doClick();
				}
			}		
		});

		//JButton for canceling
		cancel = new JButton(lang.getString("cancel"));
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();
			}

		});

		//listener for enter press
		cancel.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					cancel.doClick();
				}
			}		
		});

		buttonContainer = new JPanel();
		buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		buttonContainer.add(confirm);
		buttonContainer.add(cancel);

		add(text);
		add(radioButtonContainer);
		add(buttonContainer);

		pack();
		setLocationRelativeTo(Main.masterWindow);
		setVisible(true);
	}
}
