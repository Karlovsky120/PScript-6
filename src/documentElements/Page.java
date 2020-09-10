package documentElements;

import gui.ErrorDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import core.Main;

public class Page extends JPanel
{
	private static final long serialVersionUID = 1L;

	//GUI elements
	public JPanel header, center, nill, footer;

	public JLabel headerLabel, footerLabel; 
	public Filler filler;

	//variable elements
	public boolean hasHeading = false;
	boolean thisPageExists = true;
	public int oldFillerHeight = 10000;

	public ArrayList<JTextArea> contentsP = new ArrayList<JTextArea>(35);

	//reference to itself for nested methods
	private Page thisPage= this;

	//all added to center must also be added to ArrayList<Question> Heading.questions OR ArrayList<List> Heading.list
	public Page(int index)
	{	
		//headerLabel (currently just for GUI stability)
		headerLabel = new JLabel("  ");
		headerLabel.setHorizontalAlignment(JLabel.CENTER);

		//header
		header = new JPanel();
		header.setLayout(new BorderLayout());
		header.setBorder(BorderFactory.createMatteBorder(42, 0, 0, 0, Main.background));
		header.setBackground(Color.WHITE);
		header.add(headerLabel, BorderLayout.CENTER);

		//center of page containing all of the components
		center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setMaximumSize(new Dimension(726, 1072));
		center.setPreferredSize(new Dimension(726, 1072));
		center.setBackground(Color.WHITE);
		center.setAlignmentX(Component.CENTER_ALIGNMENT);

		//Box.Filler that fills unoccupied area of the center
		filler = new Filler(new Dimension(0, 0), new Dimension(0, 1072), new Dimension(0, 1072));
		filler.addComponentListener(new ComponentAdapter()
		{		
			public void componentResized(ComponentEvent e)
			{
				System.out.println("1  "+filler.getHeight());	
				
				if (!Main.calculating && thisPageExists)
				{
					Main.calculating = true;

					System.out.println("11  "+filler.getHeight());
					
					Page nextPage = null;
					Page previousPage;

					JPanel migratingJPanel = null;
					JTextArea migratingJTextArea = null;

					Filler previousPageFiller;

					int newFillerHeight = filler.getHeight();
					int numberOfPages = Main.pages.size()-1;

					System.out.println("1b  "+newFillerHeight);
					
					int thisPageIndex = Main.pages.indexOf(thisPage);
					int thisPageElementNumber = center.getComponentCount()-2;

					int previousPageIndex;
					int previousPageElementNumber;
					int previousPageFillerHeight;

					int nextPageIndex;
					int nextPageElementNumber;

					int migratingJPanelHeight;

					boolean thisPageIsFirst;
					boolean thisPageIsLast;
					boolean nextPageHasHeading;
					boolean removeMe = false;
					boolean migrated;

					Main.workspace.validate();
					Main.workspace.repaint();
					
					//detect when element was removed (when filler height increases)
					if ((newFillerHeight > oldFillerHeight) && (oldFillerHeight != 0))
					{	
						System.out.println("2  "+filler.getHeight());
						thisPageIsFirst = (thisPageIndex == 1);
						thisPageIsLast = (thisPageIndex == numberOfPages);

						//check if page is first or if it has heading
						if (!(thisPageIsFirst && thisPage.hasHeading))
						{
							previousPageIndex = Main.pages.indexOf(thisPage)-1;
							previousPage = Main.pages.get(previousPageIndex);

							//migrate elements from this page to previous, if possible
							do
							{
								migrated = false;

								thisPageElementNumber = center.getComponentCount()-2;

								if (thisPageElementNumber == 0)
								{
									Main.pages.remove(thisPage);
									Main.workspace.remove(thisPage);

									Main.workspace.validate();
									Main.workspace.repaint();
								}

								else
								{
									previousPageElementNumber = previousPage.center.getComponentCount()-2;
									previousPageFiller = (Filler) previousPage.center.getComponent(previousPageElementNumber+1);
									previousPageFillerHeight = previousPageFiller.getHeight();

									migratingJPanel = (JPanel) center.getComponent(1);
									migratingJPanelHeight = migratingJPanel.getHeight();

									migratingJTextArea = contentsP.get(1);

									if (previousPageFillerHeight > migratingJPanelHeight)
									{
										migrated = true;

										previousPage.center.add(migratingJPanel, previousPageElementNumber+1);
										previousPage.contentsP.add(previousPageElementNumber+1, migratingJTextArea);
										center.remove(migratingJPanel);
										contentsP.remove(migratingJTextArea);

										if (thisPageElementNumber == 0)
										{
											Main.workspace.remove(thisPage);
											Main.pages.remove(thisPage);

											thisPageExists = false;	
											migrated = false;
										}

										Main.workspace.validate();
										Main.workspace.repaint();
									}
								}

							} while (migrated);	
						}

						//check if this page is last or if it was deleted in previous loop
						if (!thisPageIsLast && thisPageExists)
						{
							nextPageIndex = Main.pages.indexOf(thisPage)+1;
							nextPage = Main.pages.get(nextPageIndex);
							nextPageElementNumber = nextPage.center.getComponentCount()-2;

							migratingJPanel = (JPanel) nextPage.center.getComponent(1);
							migratingJPanelHeight = migratingJPanel.getHeight();

							migratingJTextArea = nextPage.contentsP.get(1);

							//check for room on the next page and if it has heading
							if ((newFillerHeight > migratingJPanelHeight) && !nextPage.hasHeading)
							{
								center.add(migratingJPanel, thisPageElementNumber+1);
								contentsP.add(thisPageElementNumber+1, migratingJTextArea);
								nextPage.contentsP.remove(migratingJTextArea);

								if (nextPageElementNumber == 1)
								{
									Main.pages.remove(nextPage);
									Main.workspace.remove(nextPage);

									thisPageExists = false;	
								}

								else
								{
									removeMe = true;
								}
							}
						}
					}
					
					//detect when element was added and overflowed the thisPage (fillerHeight gets reduced to zero)
					else if (newFillerHeight == 0)
					{
						System.out.println("2  "+filler.getHeight());

						//detect if thisPage is last
						if (numberOfPages == thisPageIndex)
						{
							System.out.println("thus");
							//create new page at the end
							nextPage = new Page(numberOfPages+1);
						}

						else
						{
							nextPageHasHeading = Main.pages.get(thisPageIndex+1).hasHeading;

							//detect if nextPage has heading
							if (nextPageHasHeading)
							{
								//create new page after thisPage
								nextPage = new Page(thisPageIndex+1);
							}

							else
							{
								//fetch the nextPage
								nextPage = Main.pages.get(thisPageIndex+1);
							}
						}

						do
						{							
							thisPageElementNumber = center.getComponentCount()-2;

							//get the element that caused the overflow
							migratingJPanel = (JPanel) center.getComponent(thisPageElementNumber);
							migratingJTextArea = contentsP.get(thisPageElementNumber);

							//migrate the migratingJPanel
							nextPage.contentsP.add(1, migratingJTextArea);
							nextPage.center.add(migratingJPanel, 1);
							contentsP.remove(migratingJTextArea);
							center.remove(migratingJPanel);

							nextPage.validate();
							nextPage.repaint();

							thisPage.validate();
							thisPage.repaint();

							newFillerHeight = filler.getHeight();

						}
						while (newFillerHeight == 0);
					}

					//update the filler height
					oldFillerHeight = filler.getHeight();

					Main.calculating = false;

					if (removeMe)
					{
						nextPage.center.remove(migratingJPanel);
					}

					Main.workspace.validate();
					Main.workspace.repaint();

					//return focus to previous focusOwner
					Main.focusOwner.requestFocus();
					Main.focusOwner.scrollRectToVisible(Main.focusOwner.getBounds());
				}
				System.out.println("9  "+filler.getHeight());
			}
		});

		nill = new JPanel();
		nill.setVisible(false);

		//add null element and filler
		center.add(nill);
		center.add(filler);

		//footerLabel (link)
		//TODO finish this
		footerLabel = new JLabel("<html><u>www.perpetuum-lab.com.hr</u></html>");
		footerLabel.setFont(new Font("Consolas", Font.HANGING_BASELINE, 16));
		footerLabel.setForeground(new Color(102, 89, 255));
		footerLabel.setHorizontalAlignment(JLabel.CENTER);
		footerLabel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					java.awt.Desktop.getDesktop().browse(new java.net.URI("www.perpetuum-lab.com.hr"));
				}

				catch (IOException | URISyntaxException e1)
				{
					new ErrorDialog(Main.masterWindow, Main.lang.getString("errorBrowserTitle"), Main.lang.getString("errorBrowserMessage"), e1, false);
				}		
			}

			public void mouseEntered(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				footerLabel.setForeground(new Color(102, 190, 255));
			}

			public void mouseExited(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				footerLabel.setForeground(new Color(102, 89, 255));
			}
		});

		//footer
		footer = new JPanel();
		footer.setLayout(new BorderLayout());
		footer.setBorder(BorderFactory.createMatteBorder(0, 0, 42, 0, Main.background));
		footer.setBackground(Color.WHITE);
		footer.add(footerLabel, BorderLayout.CENTER);

		//set preferences of class
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMaximumSize(new Dimension(989, 1372));
		setPreferredSize(new Dimension(989, 1372));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setBorder(BorderFactory.createMatteBorder(0, 40, 0, 40, Main.background));
		setBackground(Color.BLUE);

		//add null element for contents of Page
		contentsP.add(null);

		//add all components	
		add(header); 
		add(center);
		add(footer);

		//add class to workspace (GUI), and ArrayList for further reference
		Main.workspace.add(this, index);
		Main.pages.add(index, this);
	}
}