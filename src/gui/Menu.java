package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import core.Main;
import engine.Open;
import engine.Save;


public class Menu extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	private JMenu menuFile, menuEdit;
	private JMenuItem itmNew, itmClose, itmOpen, itmSave, itmSaveAs, itmSend, itmExit, itmCut, itmCopy, itmPaste, itmProperties;
	private ResourceBundle lang = Main.lang;

	public Menu()
	{
		menuFile = new JMenu(lang.getString("file"));	
		menuEdit = new JMenu(lang.getString("edit"));

		itmNew = new JMenuItem(lang.getString("new..."));
		itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		itmNew.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					new CloseDocumentDialog(1);
				}

				else
				{
					new NewDocumentDialog();
				}
			}	
		});

		itmClose = new JMenuItem(lang.getString("close"));
		itmClose.setActionCommand("Close");
		itmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		itmClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					new CloseDocumentDialog(3);
				}
			}
		});

		itmOpen = new JMenuItem(lang.getString("open..."));
		itmOpen.setActionCommand("Open");
		itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		itmOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new Open(Main.documentOpen);

				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						if (Main.snap)
						{
							Main.scroll.getVerticalScrollBar().setValue(0);
							Main.snap = false;
						}
					}
				});

				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						Main.workspace.validate();
						Main.workspace.repaint();
					}
				});

			}
		});

		itmSave = new JMenuItem(lang.getString("save"));
		itmSave.setActionCommand("Save");
		itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itmSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					boolean showFileChooser = !Main.wasPreviouslySaved;
					new Save(showFileChooser, 0);
				}
			}
		});

		itmSaveAs = new JMenuItem(lang.getString("saveAs..."));
		itmSaveAs.setActionCommand("SaveAs");
		itmSaveAs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					new Save(true, 0);
				}
			}
		});

		itmSend = new JMenuItem(lang.getString("send..."));
		itmSend.setActionCommand("Send");
		itmSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					if(Save.emptyExists())
					{
						new EmptyFieldsDialog();
					}

					else
					{
						new SendMailDialog();
					}
				}
			}
		});

		itmExit = new JMenuItem(lang.getString("exit..."));
		itmExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (Main.documentOpen)
				{
					new CloseDocumentDialog(4);
				}

				else
				{
					System.exit(0);
				}
			}
		});


		itmCut = new JMenuItem(lang.getString("cut"));
		itmCut.setActionCommand("Cut");
		itmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		itmCut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Main.scroll.getVerticalScrollBar().setValue(0);
			}
		});


		itmCopy = new JMenuItem(lang.getString("copy"));
		itmCopy.setActionCommand("Copy");
		itmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

		itmPaste = new JMenuItem(lang.getString("paste"));
		itmPaste.setActionCommand("Paste");
		itmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

		itmProperties = new JMenuItem(lang.getString("properties..."));
		itmProperties.setActionCommand("properties");
		itmProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		itmProperties.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//TODO temporary
				Object[] options = {"English/Engleski", "Croatian/Hrvatski"};

				JTextArea message = new JTextArea("Choose language(restart required)/Odaberi jezik(zahtjeva ponovno pokretanje):");
				JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_OPTION, null, options);
				JDialog dialog = pane.createDialog(Main.masterWindow, "Choose language/Odaberi jezik");
				message.setOpaque(false);
				message.setEditable(false);
				message.requestFocus();
				dialog.setVisible(true);	    
				Object selection = pane.getValue();

				if (selection.equals("English/Engleski"))
				{
					Main.config.setProperty("language", "en");
					Main.config.setProperty("country", "US");	
					try
					{
						Main.config.store(new FileOutputStream(new File("src\\config.properties")), "");
					}
					catch (IOException e1) {}

				}
				else if (selection.equals("Croatian/Hrvatski"))
				{
					Main.config.setProperty("language", "hr");
					Main.config.setProperty("country", "HR");
					try
					{
						Main.config.store(new FileOutputStream(new File("src\\config.properties")), "");
					}
					catch (IOException e1) {}
				} 

				Main.masterWindow.repaint();
				Main.masterWindow.validate();
			}
		});

		menuFile.add(itmNew);
		menuFile.add(itmClose);
		menuFile.addSeparator();
		menuFile.add(itmOpen);
		menuFile.addSeparator();
		menuFile.add(itmSave);
		menuFile.add(itmSaveAs);
		menuFile.addSeparator();
		menuFile.add(itmSend);
		menuFile.addSeparator();
		menuFile.add(itmExit);

		menuEdit.add(itmCut);
		menuEdit.add(itmCopy);
		menuEdit.add(itmPaste);
		menuEdit.addSeparator();
		menuEdit.add(itmProperties);


		add(menuFile);
		add(menuEdit);

		//TODO create actionListeners for all the menus
	}

	/*
	 //TODO deal with this 
		menuFile.setText(lang.getString("file"));
		menuEdit.setText(lang.getString("edit"));
		itmNew.setText(lang.getString("new"));
		itmClose.setText(lang.getString("close"));
		itmOpen.setText(lang.getString("open"));
		itmSave.setText(lang.getString("save"));
		itmSaveAs.setText(lang.getString("saveAs"));
		itmSend.setText(lang.getString("send"));
		itmExit.setText(lang.getString("exit"));
		itmCut.setText(lang.getString("cut"));
		itmCopy.setText(lang.getString("copy"));
		itmPaste.setText(lang.getString("paste"));
		itmProperties.setText(lang.getString("properties"));
	 */

}
