package documentElements;

import gui.NewSegmentDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

import core.Main;

public class Question extends JTextArea
{
	private static final long serialVersionUID = 1L;

	//GUI elements
	public JPanel embraceQ, questionArea, numberArea, answerArea, nill;
	public JLabel number;

	//variable elements
	public ArrayList<Answer> contentsQ = new ArrayList<Answer>(6);
	public ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>(6);
	public Heading thisHeading;

	//document and filter
	private AbstractDocument document;
	private CustomDocumentFilter filter;

	//reference to itself for nested methods
	private Question thisQuestion = this;

	//All answers are added to JPanel Question.answerArea AND ArrayList<JPanel> answers

	public Question(final Page p, final Heading h, final int indexP, final int indexH, String text)
	{	
		thisHeading = h;
		
		//set JPanel contain question number and question text
		questionArea = new JPanel();
		questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.X_AXIS));
		questionArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));	//third argument sets vertical spacing below question

		//set JPanel containing question number
		numberArea = new JPanel();
		numberArea.setLayout(new BorderLayout());
		numberArea.setBackground(Color.WHITE);

		//set question number JLabel
		number = new JLabel((indexH+9)+".", SwingConstants.RIGHT);
		number.setMinimumSize(new Dimension(35, 20));
		number.setAlignmentY(RIGHT_ALIGNMENT);
		number.setPreferredSize(new Dimension(35, 20));
		number.setMaximumSize(new Dimension(35, 20));
		number.setFont(Main.calibri);

		//set JTextArea preferences
		setFont(Main.calibri);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(text);
		setMargin(new Insets(-3, 0, -5, 0));
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

		//question primary action
		inputM.put(KeyStroke.getKeyStroke("ENTER"), "pressedEnter");
		actionM.put("pressedEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!Main.keyPressed)
				{	
					int thisQuestionSize = contentsQ.size()-1;

					//check if question has less than 5 answers
					if (thisQuestionSize < 5)
					{
						//add new answer to first index
						Answer a = new Answer(thisQuestion, 1, "", false);
						a.requestFocus();
						a.scrollRectToVisible(a.getBounds());
						Main.focusOwner = a;

						for (int i = 1; i < thisQuestionSize+2; i++)
						{
							((Answer) ((JPanel) answerArea.getComponent(i)).getComponent(2)).letter.setText((char)(i+96)+")  ");
						}
					}
					
					else
					{
						Page thisPage = (Page) embraceQ.getParent().getParent();

						int indexOfOnThisPage = thisPage.contentsP.indexOf(thisQuestion);
						int thisHeadingSize = thisHeading.contentsH.size()-1;
						int indexOfOnThisHeading = thisHeading.contentsH.indexOf(thisQuestion);
						int locationOnPage = indexOfOnThisPage+1;
						int locationOnHeading = indexOfOnThisHeading+1;

						//add new question
						Question q = new Question(thisPage, thisHeading, locationOnPage, locationOnHeading, "");
						q.requestFocus();
						q.scrollRectToVisible(q.getBounds());
						Main.focusOwner = q;

						for (int i = 1; i < thisHeadingSize+2; i++)
						{
							((Question) thisHeading.contentsH.get(i)).number.setText(Integer.toString(i)+".  ");
						}
					}
					Main.keyPressed = true;
				}
			}
		});

		//question secondary action
		inputM.put(KeyStroke.getKeyStroke("shift ENTER"), "pressedShiftEnter");
		actionM.put("pressedShiftEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Page thisPage = (Page) embraceQ.getParent().getParent();

				int indexOfOnThisPage = thisPage.contentsP.indexOf(thisQuestion);
				int thisHeadingSize = thisHeading.contentsH.size()-1;
				int indexOfOnThisHeading = thisHeading.contentsH.indexOf(thisQuestion);
				int locationOnPage = indexOfOnThisPage+1;
				int locationOnHeading = indexOfOnThisHeading+1;

				//add new question
				Question q = new Question(thisPage, thisHeading, locationOnPage, locationOnHeading, "");
				q.requestFocus();
				q.scrollRectToVisible(q.getBounds());
				Main.focusOwner = q;

				for (int i = 1; i < thisHeadingSize+2; i++)
				{
					((Question) thisHeading.contentsH.get(i)).number.setText(Integer.toString(i)+".  ");
				}
			}
		});
		
		//question tertiary action
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
		
		//question delete action
		inputM.put(KeyStroke.getKeyStroke("shift DELETE"), "pressedShiftDelete");
		actionM.put("pressedShiftDelete", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					Page thisPage = (Page) embraceQ.getParent().getParent();
					
					Heading thisHeading = h; 
					
					if (thisHeading.contentsH.size()-1 != 1)
					{
						int previousQuestionIndex = thisHeading.contentsH.indexOf(thisQuestion)-1;
						int thisHeadingSize = thisHeading.contentsH.size()-1;
						
						thisPage.center.remove(embraceQ);
						thisPage.contentsP.remove(thisQuestion);
						thisHeading.contentsH.remove(thisQuestion);

						for (int i = 1; i < thisHeadingSize; i++)
						{
							((Question) thisHeading.contentsH.get(i)).number.setText(i+".  ");
						}

						Main.masterWindow.validate();
						Main.masterWindow.repaint();

						if (previousQuestionIndex == 0)
						{
							thisHeading.requestFocus();
							thisHeading.scrollRectToVisible(thisHeading.getBounds());
							Main.focusOwner = thisHeading;
						}
						
						else
						{
							Question previousQuestion = (Question) thisHeading.contentsH.get(previousQuestionIndex);
							if (previousQuestion.contentsQ.size() == 1)
							{	
								previousQuestion.requestFocus();
								previousQuestion.scrollRectToVisible(previousQuestion.getBounds());
								Main.focusOwner = previousQuestion;
							}
							
							else
							{
								int previousQuestionSize = previousQuestion.answerArea.getComponentCount()-1;
								Answer lastAnswer = (Answer) ((JPanel) previousQuestion.answerArea.getComponent(previousQuestionSize)).getComponent(2);
								
								lastAnswer.requestFocus();
								lastAnswer.scrollRectToVisible(lastAnswer.getBounds());
								Main.focusOwner = lastAnswer;
							}
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
				Main.focusOwner = thisQuestion;
			}			
		});
		
		//add document filter to document
		filter = new CustomDocumentFilter();
		filter.setCharLimit(450);

		document = (AbstractDocument) this.getDocument();
		document.setDocumentFilter(filter);

		//JPanel containing all the answers
		answerArea = new JPanel();
		answerArea.setLayout(new BoxLayout(answerArea, BoxLayout.Y_AXIS));

		nill = new JPanel();
		nill.setVisible(false);

		//setup enclosing JPanel
		embraceQ = new JPanel();
		embraceQ.setLayout(new BoxLayout(embraceQ, BoxLayout.Y_AXIS));
		embraceQ.setBackground(Color.WHITE);
		embraceQ.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.WHITE));	//third argument sets vertical spacing below question set

		//create visual object from components
		numberArea.add(number, BorderLayout.NORTH);
		questionArea.add(numberArea);
		questionArea.add(this);
		answerArea.add(nill);	
		embraceQ.add(questionArea);
		embraceQ.add(answerArea);

		//add null element to list to set first index to one
		contentsQ.add(null);
		checkBoxes.add(null);

		//add enclosing JPanel to page and to heading's content ArrayList
		p.center.add(embraceQ, indexP);
		p.contentsP.add(indexP, this);
		h.contentsH.add(indexH, this);

		Main.focusOwner = this;
		
		//validate and repaint visually changed component
		p.center.validate();
		p.center.repaint();
	}
}