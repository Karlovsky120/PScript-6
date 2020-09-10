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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import core.Main;

public class EmptyFieldsDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private JDialog dialog = this;
	private JTextArea text;
	private JPanel buttonContainer;
	private JButton ok;

	private static ResourceBundle lang = Main.lang;
	
	public EmptyFieldsDialog()
	{
		super(Main.masterWindow, lang.getString("emptyTitle"), true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				ok.requestFocus();
			}
		});

		//text in the dialog
		text = new JTextArea();
		text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
		text.setText(lang.getString("emptyMessage"));
		text.setEditable(false);
		text.setOpaque(false);
		text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

		//JButton for confirming
		ok = new JButton(lang.getString("ok"));
		ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				Main.focusOwner.requestFocus();
				Main.focusOwner.scrollRectToVisible(Main.focusOwner.getBounds());
			}		
		});

		//listener for enter press
		ok.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					ok.doClick();
				}
			}		
		});

		buttonContainer = new JPanel();
		buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		buttonContainer.add(ok);

		add(text);
		add(buttonContainer);

		pack();
		setLocationRelativeTo(Main.masterWindow);
		setVisible(true);
	}
}
