package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import core.Main;
import engine.MailSender;

public class SendMailDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private JPanel namePanel, yearPanel, subjectPanel, buttons, buttonContainer;
	private JLabel name, year, subject;
	private JTextField nameInput;
	private JComboBox<String> yearInput, subjectInput;
	private JButton send, cancel, ok;
	private JDialog dialog = this;
	private boolean fastSwitch = true;

	public String[] years = {"Prva godina", "Druga godina", "Treæa godina", "Èetvrta godina", "Peta godina", "Šesta godina", "Drugo"};

	public String[] firstYear = {"Anatomija", "Fizika i biofizika", "Medicinska biologija", "Medicinski engleski I", "Medicinska kemija i biokemija I", "Uvod u medicinu i povijest medicine", "Socijalna medicina", "Psihološka medicina I", "Prva pomoæ", "Izborni predmet I", "Drugo"};
	public String[] secondYear = {"Histologija i embriologija", "Temelji neuroznanosti", "Medicinska kemija i biokemija II", "Medicinski engleski II", "Fiziologija", "Imunologija", "Uvod u znanstveni rad", "Medicinska statistika II", "Medicinska sociologija", "Izborni predmet II", "Drugo"};
	public String[] thirdYear = {"Patologija", "Patofiziologija", "Klinièka propedeutika", "Medcinski engleski III", "Medicinska mikrobiologija i parazitologija", "Farmakologija", "Psihološka medicina II", "Izborni predmet III", "Drugo"};
	public String[] fourthYear = {"Interna medicina", "Infektologija", "Klinièka mikrobiologija i parazitologija", "Neurologija", "Neurokirurgija", "Nuklearna medicina", "Klinièka biokemija", "Radiologija", "Psihijatrija", "Dermatovenerologija", "Onkologija", "Strulna praksa u zajednici", "Fizika medicinske dijagnostike", "medicinska statistika IV", "Izborni predmet IV", "Drugo"};
	public String[] fifthYear = {"Pedijatrija", "Fizikalna medicina", "Kirurgija", "Kirurgija djeèje dobi", "Anesteziologija i reumatologija", "Urologija", "Otorinolaringologija", "Ortopedija", "Maksilofacijalna kirurgija sa stomatologijom", "Ginekologija i opstetricija", "Izborni predmet V", "Drugo"};
	public String[] sixthYear = {"Sudska medicina", "Povijest medicine", "Obiteljska medicina i primarna zdravstvena zaštita", "Školska medicina", "Zdravstvena ekologija i medicina rada", "Oftamologija", "Medicinska informatika", "Epidemiologija", "Organizacija i upravljanje zdravstvenom zaštitom", "Modul I - Bolesti muskuloskeletnog sustava", "Modul II - Hitna stanja u medicini", "Modul III - Racionalna primjena lijekova", "Izorni predmet VI", "Drugo"};
	public String[] other = {"Drugo"};

	SendMailDialog()
	{
		super(Main.masterWindow, Main.lang.getString("sendTitle"), true);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);

		Color color = new Color(UIManager.getColor("control").getRGB());

		namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));

		name = new JLabel(Main.lang.getString("sendName"));
		name.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, color));
		name.setMinimumSize(new Dimension(60, 26));
		name.setPreferredSize(new Dimension(60, 26));
		name.setMaximumSize(new Dimension(60, 26));
		name.setSize(new Dimension(60, 26));

		nameInput = new JTextField();
		nameInput.setText(Main.fileName);
		nameInput.setMinimumSize(new Dimension(310, 26));
		nameInput.setPreferredSize(new Dimension(310, 26));
		nameInput.setMaximumSize(new Dimension(310, 26));
		nameInput.setSize(new Dimension(310, 26));

		yearPanel = new JPanel();
		yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.X_AXIS));

		year = new JLabel(Main.lang.getString("sendYear"));
		year.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, color));
		year.setMinimumSize(new Dimension(60, 20));
		year.setPreferredSize(new Dimension(60, 20));
		year.setMaximumSize(new Dimension(60, 20));
		year.setSize(new Dimension(60, 20));

		yearInput = new JComboBox<String>(years);
		yearInput.setSelectedIndex(0);
		yearInput.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (!fastSwitch)
				{
					switch ((String) e.getItem())
					{
						case "Prva godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(firstYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Druga godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(secondYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Treæa godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(thirdYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Èetvrta godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(fourthYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Peta godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(fifthYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Šesta godina":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(sixthYear);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}

						case "Drugo":
						{
							subjectPanel.remove(subjectInput);
							subjectInput = new JComboBox<String>(other);
							subjectInput.setEnabled(false);
							subjectPanel.add(subjectInput, 1);
							dialog.pack();
							dialog.setLocationRelativeTo(Main.masterWindow);
							break;
						}
					}

					fastSwitch = true;
				}

				else
				{
					fastSwitch = false;
				}
			}
		});

		subjectPanel = new JPanel();
		subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.X_AXIS));

		subject = new JLabel(Main.lang.getString("sendSubject"));
		subject.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, color));
		subject.setMinimumSize(new Dimension(60, 20));
		subject.setPreferredSize(new Dimension(60, 20));
		subject.setMaximumSize(new Dimension(60, 20));
		subject.setSize(new Dimension(60, 20));

		subjectInput = new JComboBox<String>(firstYear);
		subjectInput.setSelectedIndex(0);

		buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		send = new JButton(Main.lang.getString("send"));
		send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();

				//create one with the progress bar
				dialog = new JDialog(Main.masterWindow, Main.lang.getString("sendingDocumentTitle"), true);

				JProgressBar progress = new JProgressBar();
				progress.setString(Main.lang.getString("sendingDocumentMessage"));
				progress.setStringPainted(true);
				progress.setIndeterminate(true);

				dialog.getContentPane().add(progress);	
				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				dialog.setResizable(false);
				
				String name;
				
				if (nameInput.getText().endsWith("pdf"))
				{
					name = nameInput.getText().replace(".pdf", "");
				}
				
				else
				{
					name = nameInput.getText();
				}
				
				//SwingWorker
				MailSender sender = new MailSender(name, (String) subjectInput.getSelectedItem());

				sender.addPropertyChangeListener(new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent e1)
					{
						MailSender sender = (MailSender) e1.getSource();

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

				if (!sender.failed)
				{
					//new report sent dialog
					dialog = new JDialog(Main.masterWindow, Main.lang.getString("sentDocumentTitle"), true);

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
					JTextArea text = new JTextArea();
					text.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
					text.setText(Main.lang.getString("sentDocumentMessage"));
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
					dialog.setLocationRelativeTo(Main.masterWindow);
					dialog.setVisible(true);
				}
			}

		});

		cancel = new JButton(Main.lang.getString("cancel"));
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//dispose of the JDialog
				dialog.dispose();
			}

		});

		namePanel.add(name);
		namePanel.add(nameInput);
		yearPanel.add(year);
		yearPanel.add(yearInput);
		subjectPanel.add(subject);
		subjectPanel.add(subjectInput);
		buttons.add(send);
		buttons.add(cancel);
		add(namePanel);
		add(yearPanel);
		add(subjectPanel);
		add(buttons);

		pack();
		setLocationRelativeTo(Main.masterWindow);
		setVisible(true);
	}
}
