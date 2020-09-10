package documentElements;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;

import core.Main;
import gui.NewSegmentDialog;

public class Heading extends JTextArea
{
	private static final long serialVersionUID = 1L;

	//GUI elements
	public JPanel embraceH;

	//variable elements
	public boolean type;
	public ArrayList<JTextArea> contentsH = new ArrayList<JTextArea>(500);;

	//document and filter
	private AbstractDocument document;
	private CustomDocumentFilter filter;

	//reference to itself for nested methods
	private Heading thisHeading = this;

	public Heading(final Page p, int index, boolean segment, String text)
	{
		//set type of heading (true: questions, false: list)
		type = segment;

		//set JTextArea preferences
		setFont(Main.calibriBold);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(text);
		setMargin(new Insets(-2, 0, -6, 0));
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Main.keyPressed = false;
			}		
		});

		//get maps and add various keyboard actions
		InputMap inputM = this.getInputMap();
		ActionMap actionM = this.getActionMap();

		//heading primary action
		inputM.put(KeyStroke.getKeyStroke("ENTER"), "pressedEnter");
		actionM.put("pressedEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				if (!Main.keyPressed)
				{
					//add new heading element to first index
					if (type)
					{
						int thisHeadingSize = contentsH.size();

						Question q = new Question(p, thisHeading, 2, 1, "");
						q.requestFocus();
						q.scrollRectToVisible(q.getBounds());
						Main.focusOwner = q;
						
						Main.scroll.getViewport().scrollRectToVisible(q.getBounds());

						for (int i = 1; i < thisHeadingSize+1; i++)
						{
							((Question) contentsH.get(i)).number.setText(Integer.toString(i)+".  ");
						}
					}

					else
					{
						ListElement l = new ListElement(p, thisHeading, 2, 1, "");
						l.requestFocus();
						l.scrollRectToVisible(l.getBounds());
						Main.focusOwner = l;
					}
					Main.keyPressed = true;
				}
			}

		});

		//there is no heading secondary action, deal with it 

		//heading tertiary action
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


		//heading delete action
		inputM.put(KeyStroke.getKeyStroke("shift DELETE"), "pressedShiftDelete");
		actionM.put("pressedShiftDelete", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{	
					int headingNumber = Main.headings.size()-1;

					//check if this is not the last heading
					if (headingNumber > 1)
					{
						Page thisPage = p;

						ArrayList<Heading> headingList = Main.headings;

						JTextArea focus = null;

						int thisPageIndex = Main.pages.indexOf(thisPage);
						int numberOfPages = Main.pages.size()-1;

						int indexOfLastPageForDeleting = thisPageIndex;

						boolean reachedNextHeadingOrDocumentEnd = false;

						//check if this page is first
						if (thisPageIndex != 1)
						{
							int thisHeadingIndex = headingList.indexOf(thisHeading);

							Heading previousHeading = headingList.get(thisHeadingIndex - 1);	

							int previousHeadingLastElementIndex = previousHeading.contentsH.size()-1;

							JTextArea previousHeadingLastElement = (JTextArea) previousHeading.contentsH.get(previousHeadingLastElementIndex);

							//check if question
							if (previousHeading.type)
							{
								Question lastQuestion = (Question) previousHeadingLastElement;

								int lastQuestionSize = lastQuestion.contentsQ.size()-1;

								//check if question has no answers
								if (lastQuestionSize == 0)
								{
									focus = lastQuestion;
								}
								else

								{
									JPanel lastQuestionAnswerArea = lastQuestion.answerArea;
									Answer lastAnswer = (Answer) ((JPanel) lastQuestionAnswerArea.getComponent(lastQuestionSize)).getComponent(2);

									focus = lastAnswer;
								}
							}
							else

							{
								ListElement lastListElement = (ListElement) previousHeadingLastElement;
								focus = lastListElement;
							}
						}

						else
						{
							focus = headingList.get(1);
						}

						//see how many pages need to be removed
						do
						{
							//check if document end reached
							if (indexOfLastPageForDeleting == numberOfPages)
							{
								reachedNextHeadingOrDocumentEnd = true;
							}
							//check if next heading page reached
							else if (Main.pages.get(indexOfLastPageForDeleting+1).hasHeading)
							{
								reachedNextHeadingOrDocumentEnd = true;
							}

							indexOfLastPageForDeleting++;
						}
						while (!reachedNextHeadingOrDocumentEnd);

						//delete those pages
						for (int i = thisPageIndex; i < indexOfLastPageForDeleting; i++)
						{
							Main.pages.remove(thisPageIndex);
							Main.workspace.remove(thisPageIndex);
							Main.headings.remove(thisHeading);
						}
						Main.masterWindow.validate();
						Main.masterWindow.repaint();

						focus.requestFocus();
						focus.scrollRectToVisible(focus.getBounds());
						Main.focusOwner = focus;
					}
					Main.keyPressed = true;
				}
			}
		});
		
		addFocusListener(new FocusAdapter(){

			@Override
			public void focusGained(FocusEvent e)
			{
				Main.focusOwner = thisHeading;
			}			
		});

		//add document filter to document
		filter = new CustomDocumentFilter();
		filter.setCharLimit(100);

		document = (AbstractDocument) this.getDocument();
		document.setDocumentFilter(filter);

		//setup enclosing JPanel
		embraceH = new JPanel();
		embraceH.setLayout(new BoxLayout(embraceH, BoxLayout.Y_AXIS));	
		embraceH.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.WHITE));
		embraceH.add(this);

		//add null element to list to set first index to one

		contentsH.add(null);

		//set up a way to tell if page has heading
		p.hasHeading = true;

		//add enclosing JPanel to page
		p.center.add(embraceH, 1);
		p.contentsP.add(1, this);

		//add this to ArrayList used for transforming visual components to data that can be manipulated
		Main.headings.add(index, this);
		
		Main.focusOwner = this;

		//validate and repaint visually changed component
		p.center.validate();
		p.center.repaint();
	}
}