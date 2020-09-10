package core;

import gui.ErrorDialog;
import gui.Menu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import resources.JavaResources;

import documentElements.Heading;
import documentElements.Page;


public class Main
{
	//configuration and language files
	public static Properties config;
	public static Locale currentLocale;
	public static ResourceBundle lang;

	//visual components
	public static JFrame masterWindow;
	public static JPanel workspace;
	public static JScrollPane scroll;
	public static JDialog errorDialog;
	public static Menu menuBar;
	public static JLabel statusBar;

	//background color
	public static Color background;

	//ArrayList used to make all visual and variable data accessible
	public static ArrayList<Page> pages;
	public static ArrayList<Heading> headings;

	//booleans to keep track of key pressing and focus owners
	public static boolean keyPressed = false;
	public static JComponent focusOwner = null;
	public static JTextArea border;

	//booleans and strings used when opening/closing/saving files
	public static boolean documentOpen = false;
	public static boolean wasPreviouslySaved = false;
	
	public static String currentSaveLocation = "";
	public static String fileName = "";
	
	public static boolean calculating = false;
	public static boolean snap = false;
	public static boolean statusLocked = false;

	//fonts
	public static Font calibri;
	public static Font calibriBold;

	public static JFrame modal;

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				//JFrame dummy for JDialog errors
				modal = new JFrame();
				modal.setLocationRelativeTo(null);

				//check Java version
				float javaMinorVersion;
				int javaMajorVersion;

				try
				{
					String javaVersionS = System.getProperty("java.version");
					javaMinorVersion = Float.parseFloat(javaVersionS.substring(0, 3));
					javaMajorVersion = Integer.parseInt(javaVersionS.substring(2, 3));
				}

				catch (NumberFormatException e)
				{
					String javaVersionS = System.getProperty("java.runtime.version");
					javaMinorVersion = Float.parseFloat(javaVersionS.substring(0, 3));
					javaMajorVersion = Integer.parseInt(javaVersionS.substring(2, 3));
				}

				if (javaMajorVersion <= 6)
				{
					String errorJavaVersionTitle = "Fatal error: Wrong Java version!";
					String errorJavaVersionMessage = "In order to run this program you need to have at least Java Runtime Enviroment 6 (1.6) installed. Go to \"http://java.com/en/download/index.jsp\" to download latest Java. Your current Java version is " + javaMajorVersion + " (" + javaMinorVersion + ")!";

					new ErrorDialog(Main.modal, errorJavaVersionTitle, errorJavaVersionMessage, true);
				}

				//load properties file
				config = new Properties();

				try
				{
					config.load(new FileInputStream("config.properties"));

					//load language
					currentLocale = new Locale(config.getProperty("language"), config.getProperty("country"));
					lang = ResourceBundle.getBundle("language", currentLocale);
				}

				catch (IOException e)
				{
					//TODO add new string when time is right

					config.setProperty("country", "US");
					config.setProperty("language", "en");
					config.setProperty("defaultSaveLocation", "");
					config.setProperty("defaultOpenLocation", "");

					//load language
					currentLocale = new Locale("US", "en");
					lang = ResourceBundle.getBundle("language", currentLocale);

					new ErrorDialog(Main.modal, lang.getString("errorConfigMissingTitle"), lang.getString("errorConfigMissingMessage"), e, false);

					FileOutputStream configSaver = null;	
					try
					{
						configSaver = new FileOutputStream(new File("config.properties"));
						config.store(configSaver, "config");
					}
					catch (IOException e1)
					{
						new ErrorDialog(modal, lang.getString("errorCreatingConfigTitle"), lang.getString("errorCreatingConfigMessage"), e1, false);
					}
				}

				//set LAF
				boolean LAFExists = false;

				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				{
					if ("Nimbus".equals(info.getName()))
					{
						LAFExists = true;

						try
						{
							UIManager.setLookAndFeel(info.getClassName());
						}

						catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
						{
							new ErrorDialog(Main.modal, lang.getString("errorLAFUnexpectedTitle"), lang.getString("errorLAFUnexpectedMessage"), e, true);
						}
						break;
					}
				}

				if (!LAFExists)
				{			
					new ErrorDialog(Main.modal, lang.getString("errorLAFMissingTitle"), lang.getString("errorLAFMissingMessage"), true);
				}

				modal.dispose();
				
				border = new JTextArea();

				//load fonts
				calibri = JavaResources.loadFont("calibri", 19);
				calibriBold = JavaResources.loadFont("calibri_bold", 22);

				//create main window
				masterWindow = new JFrame(lang.getString("title"));
				masterWindow.setSize(1100, 800);
				masterWindow.setMinimumSize(new Dimension(800, 800));
				masterWindow.setLocationRelativeTo(null);
				masterWindow.setLayout(new BorderLayout());
				masterWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				masterWindow.setVisible(true);

				//create and add menu bar
				menuBar = new Menu();
				masterWindow.add(menuBar, BorderLayout.NORTH);

				//create and add status bar
				statusBar = new JLabel("Welcome to \"program name\" press File>new to create a new file or go to File>Open to open an existing");
				statusBar.setBorder(BorderFactory.createLoweredSoftBevelBorder());
				masterWindow.add(statusBar, BorderLayout.SOUTH);
				
				//create workspace
				workspace = new JPanel();
				workspace.setLayout(new BoxLayout(workspace, BoxLayout.Y_AXIS));

				background = Color.BLACK; //TODO
				workspace.setBackground(background);

				//create scrollPane and add workspace to it
				scroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scroll.getVerticalScrollBar().setUnitIncrement(20);
				scroll.setViewportView(workspace);

				masterWindow.add(scroll, BorderLayout.CENTER);

				masterWindow.repaint();
				masterWindow.validate();
			}
		});
	}
}
