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
import engine.Open;
import engine.Resetter;
import engine.Save;

public class OverwriteDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private JDialog dialog = this;
	private JTextArea text;
	private JPanel buttonContainer;
	private JButton yes, no, cancel;

	private static ResourceBundle lang = Main.lang;

	public OverwriteDialog(final String currentDirectoryPath, final String nameOfFile, final int afterOperation)
	{
		super(Main.masterWindow, lang.getString("overwriteTitle"), true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				no.requestFocus();
			}
		});

		//text in the dialog
		text = new JTextArea();
		text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
		text.setText(lang.getString("overwriteMessage")+" \""+nameOfFile+".pdf\"?");
		text.setEditable(false);
		text.setOpaque(false);
		text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

		//JButton for yes
		yes = new JButton(lang.getString("yes"));
		yes.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String fileName = nameOfFile;
				
				if (fileName.toLowerCase().endsWith(".pdf"))
				{
					fileName = fileName.replace(".pdf", "");
					fileName = fileName.replace(".PDF", "");
				}

				Save.createPdfSave(currentDirectoryPath+"\\"+fileName+".pdf", fileName);
				Main.currentSaveLocation = currentDirectoryPath+"\\"+fileName+".pdf";

				Main.wasPreviouslySaved = true;

				if (afterOperation == 1)
				{
					new NewDocumentDialog();
				}
				
				else if (afterOperation == 2)
				{
					Resetter.close();
					new Open(false);
				}
				
				else if (afterOperation == 3)
				{
					Resetter.close();
				}

				else if (afterOperation == 4)
				{
					System.exit(0);
				}

				//dispose of the JDialog
				dialog.dispose();
			}		
		});

		yes.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					yes.doClick();
				}
			}		
		});

		//JButton for no
		no = new JButton(lang.getString("no"));
		no.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				//open save dialog again
				new Save(true, afterOperation);
			}

		});

		//listener for enter press
		no.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					no.doClick();
				}
			}		
		});

		//JButton for cancel
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
		buttonContainer.add(yes);
		buttonContainer.add(no);
		buttonContainer.add(cancel);

		add(text);
		add(buttonContainer);

		pack();
		setLocationRelativeTo(Main.masterWindow);
		setVisible(true);
	}

}
