package documentElements;

import gui.NewSegmentDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

import core.Main;

public class ListElement extends JTextArea
{
	private static final long serialVersionUID = 1L;

	//GUI elements
	public JPanel embraceL, bulletArea;
	public JLabel bullet;

	//variable elements
	public Heading thisHeading;

	//document and filter
	private AbstractDocument document;
	private CustomDocumentFilter filter;

	//reference to itself for nested methods
	private ListElement thisListElement = this;

	public ListElement(final Page p, final Heading h, int indexP, int indexH, String text)
	{		
		thisHeading = h;

		//JPanel containing bullet
		bulletArea = new JPanel();
		bulletArea.setLayout(new BorderLayout());
		bulletArea.setBackground(Color.WHITE);

		//bullet
		bullet = new JLabel("\u2022 ", SwingConstants.RIGHT); //UNICODE symbol!
		bullet.setMinimumSize(new Dimension(80, 20));
		bullet.setPreferredSize(new Dimension(80, 20));
		bullet.setMaximumSize(new Dimension(80, 20));
		bullet.setFont(new Font("Arial", Font.PLAIN, 30));

		setFont(Main.calibri);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(text);
		setMargin(new Insets(-3, 0, -5, 0));
		addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				Main.keyPressed = false;
			}

			@Override
			public void keyTyped(KeyEvent e) {}		
		});

		//get maps and add various keyboard actions
		InputMap inputM = this.getInputMap();
		ActionMap actionM = this.getActionMap();

		//listElement primary action
		inputM.put(KeyStroke.getKeyStroke("ENTER"), "pressedEnter");
		actionM.put("pressedEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!Main.keyPressed)
				{	

					Page thisPage = (Page) embraceL.getParent().getParent();

					int indexOfOnThisPage = thisPage.contentsP.indexOf(thisListElement);
					int indexOfOnThisHeading = thisHeading.contentsH.indexOf(thisListElement);
					int locationOnPage = indexOfOnThisPage+1;
					int locationOnHeading = indexOfOnThisHeading+1;

					//add new listElement
					ListElement l = new ListElement(thisPage, thisHeading, locationOnPage, locationOnHeading, "");
					l.requestFocus();
					l.scrollRectToVisible(l.getBounds());
					Main.focusOwner = l;

					Main.keyPressed = true;
				}
			}
		});

		//there is no listElement secondary action, deal with it

		//listElement tertiary action
		inputM.put(KeyStroke.getKeyStroke("control ENTER"), "pressedCtrlEnter");
		actionM.put("pressedCtrlEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					int thisHeadingIndex = Main.headings.indexOf(thisHeading);
					new NewSegmentDialog(p, thisHeadingIndex+1);

					Main.keyPressed = true;
				}
			}
		});

		//listElement delete action
		inputM.put(KeyStroke.getKeyStroke("shift DELETE"), "pressedShiftDelete");
		actionM.put("pressedShiftDelete", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					Page thisPage = (Page) embraceL.getParent().getParent();

					Heading thisHeading = h; 

					if (thisHeading.contentsH.size()-1 != 1)
					{
						int previousListElementIndex = thisHeading.contentsH.indexOf(thisListElement)-1;

						thisPage.center.remove(embraceL);
						thisPage.contentsP.remove(thisListElement);
						thisHeading.contentsH.remove(thisListElement);

						Main.masterWindow.validate();
						Main.masterWindow.repaint();

						if (previousListElementIndex == 0)
						{
							thisHeading.requestFocus();
							thisHeading.scrollRectToVisible(thisHeading.getBounds());
							Main.focusOwner = thisHeading;
						}

						else
						{
							ListElement previousListElement = (ListElement) thisHeading.contentsH.get(previousListElementIndex);

							previousListElement.requestFocus();
							previousListElement.scrollRectToVisible(previousListElement.getBounds());
							Main.focusOwner = previousListElement;
						}
					}
					Main.keyPressed = true;
				}
			}
		});

		addFocusListener(new FocusAdapter(){

			@Override
			public void focusGained(FocusEvent e)
			{
				Main.focusOwner = thisListElement;
			}			
		});

		//add document filter to document
		filter = new CustomDocumentFilter();
		filter.setCharLimit(250);

		document = (AbstractDocument) this.getDocument();
		document.setDocumentFilter(filter);

		//setup enclosing JPanel
		embraceL = new JPanel();
		embraceL.setLayout(new BoxLayout(embraceL, BoxLayout.X_AXIS));
		embraceL.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

		//create visual object from components
		bulletArea.add(bullet, BorderLayout.NORTH);
		embraceL.add(bulletArea);
		embraceL.add(this);

		//add enclosing JPanel to page and to heading's content ArrayList
		p.center.add(embraceL, indexP);
		p.contentsP.add(indexP, this);
		h.contentsH.add(indexH, this);
		
		Main.focusOwner = this;

		//validate and repaint visually changed component
		p.center.validate();
		p.center.repaint();
	}
}