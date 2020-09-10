package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.SwingWorker;

import core.Main;
import engine.ErrorSender;

public class ErrorDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private JDialog dialog = this;
	private JTextArea text;
	private JPanel buttonContainer;
	private JButton sendErrorReportT, sendErrorReportF, ok;

	public ErrorDialog(final JFrame parent, String title, String message, final Exception error, final boolean exit)
	{
		super(parent, title, true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				sendErrorReportT.requestFocus();
			}
		});

		//text in the dialog
		text = new JTextArea();
		text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
		text.setText(message);
		text.setEditable(false);
		text.setOpaque(false);
		text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

		//JButton for sending error report
		sendErrorReportT = new JButton("Send Error Report");
		sendErrorReportT.setFocusPainted(false);
		sendErrorReportT.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the old JDialog
				dialog.dispose();

				//create one with the progress bar
				dialog = new JDialog(parent, Main.lang.getString("sendingErrorTitle"), true);

				JProgressBar progress = new JProgressBar();
				progress.setString(Main.lang.getString("sendingErrorMessage"));
				progress.setStringPainted(true);
				progress.setIndeterminate(true);

				dialog.getContentPane().add(progress);	
				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				dialog.setResizable(false);

				//SwingWorker
				ErrorSender sender = new ErrorSender(parent, error, exit);

				sender.addPropertyChangeListener(new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent e1)
					{
						ErrorSender sender = (ErrorSender) e1.getSource();

						// The state of the worker has changed...
						if (e1.getPropertyName().equalsIgnoreCase("state"))
						{

							if (sender.getState().equals(SwingWorker.StateValue.DONE))
							{
								dialog.dispose();
							}
						}	
					}		
				});

				sender.execute();

				//set progress dialog to visible, will be disposed of within SwingWorker
				dialog.setVisible(true);

				//new report sent dialog
				dialog = new JDialog(parent, Main.lang.getString("sentErrorTitle"), true);

				dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
				dialog.setResizable(false);
				dialog.addWindowListener(new WindowAdapter()
				{
					public void windowActivated(WindowEvent e)
					{
						ok.requestFocus();
					}
				});

				//text in the dialog
				text = new JTextArea();
				text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
				text.setText(Main.lang.getString("sentErrorMessage"));
				text.setEditable(false);
				text.setOpaque(false);
				text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

				//JButton for confirming
				ok = new JButton(Main.lang.getString("ok"));
				ok.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						//dispose of the JDialog
						dialog.dispose();
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

				dialog.add(text);
				dialog.add(buttonContainer);

				dialog.pack();
				dialog.setLocationRelativeTo(parent);
				dialog.setVisible(true);
			}		
		});

		//listener for enter press
		sendErrorReportT.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					sendErrorReportT.doClick();
				}
			}		
		});

		//JButton for not sending error report
		sendErrorReportF = new JButton("Don't send error report");
		sendErrorReportF.setFocusPainted(false);
		sendErrorReportF.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				if (exit)
				{
					System.exit(0);
				}
			}		
		});

		//listener for enter press
		sendErrorReportF.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					sendErrorReportF.doClick();
				}
			}		
		});

		buttonContainer = new JPanel();
		buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		buttonContainer.add(sendErrorReportT);
		buttonContainer.add(sendErrorReportF);

		add(text);
		add(buttonContainer);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public ErrorDialog(JFrame parent, String title, String message, final boolean exit)
	{
		super(parent, title, true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
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
		text.setText(message);
		text.setEditable(false);
		text.setOpaque(false);
		text.setBackground(new Color(UIManager.getColor("control").getRGB())); //same color as background color of JDialog in Nimbus L&F

		//JButton for confirming
		ok = new JButton("OK");
		ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				if (exit)
				{
					System.exit(0);
				}
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
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
