package documentElements;

import gui.NewSegmentDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class Answer extends JTextArea
{
	private static final long serialVersionUID = 1L;

	//GUI elements
	public JPanel embraceA, checkArea, letterArea;
	public JCheckBox check;
	public JLabel letter;

	//variable elements
	public Question thisQuestion;

	//document and filter
	private AbstractDocument document;
	private CustomDocumentFilter filter;

	//reference to itself for nested methods
	private Answer thisAnswer = this;

	public Answer(Question q, int indexQ, String text, boolean correct)
	{
		thisQuestion = q;

		//JPanel containing checkBox
		checkArea = new JPanel();
		checkArea.setLayout(new BorderLayout());
		checkArea.setBackground(Color.WHITE);

		check = new JCheckBox();
		check.setHorizontalAlignment(SwingConstants.RIGHT);
		check.setMinimumSize(new Dimension(35, 20));
		check.setPreferredSize(new Dimension(35, 20));
		check.setMaximumSize(new Dimension(35, 20));
		check.setVerticalAlignment(SwingConstants.TOP);
		check.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (check.isSelected())
				{

					int checkBoxesSize = thisQuestion.checkBoxes.size();
					JCheckBox currentCheckBox;

					for (int i = 1; i < checkBoxesSize; i++)
					{
						currentCheckBox = thisQuestion.checkBoxes.get(i);
						currentCheckBox.setSelected(false);
					}
					check.setSelected(true);
				}
			}

		});
		
		if (correct)
		{
			check.setSelected(true);
		}

		//JPanel containing letter
		letterArea = new JPanel();
		letterArea.setLayout(new BorderLayout());
		letterArea.setBackground(Color.WHITE);

		//answer letter
		letter = new JLabel((char)(indexQ+96)+")", SwingConstants.RIGHT);
		letter.setVerticalAlignment(SwingConstants.TOP);
		letter.setMinimumSize(new Dimension(25, 20));
		letter.setPreferredSize(new Dimension(25, 20));
		letter.setMaximumSize(new Dimension(25, 20));
		letter.setFont(Main.calibri);

		//JTextArea preferences
		setFont(Main.calibri);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(text);
		setMargin(new Insets(-3, 0, -6, 0));
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
		
		//answer primary action
		inputM.put(KeyStroke.getKeyStroke("ENTER"), "pressedEnter");
		actionM.put("pressedEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				if (!Main.keyPressed)
				{				
					int thisQuestionSize = thisQuestion.contentsQ.size()-1;

					//check if question has less than 5 answers
					if (thisQuestionSize < 5)
					{
						//add new answer to first index
						Answer a = new Answer(thisQuestion, thisQuestionSize+1, "", false);
						a.requestFocus();
						a.scrollRectToVisible(a.getBounds());
						Main.focusOwner = a;

						for (int i = 1; i < thisQuestionSize+2; i++)
						{
							thisQuestion.contentsQ.get(i).letter.setText((char)(i+96)+")  ");
						}
					}

					else
					{
						JPanel thisQuestionEmbrace = (JPanel) thisQuestion.getParent().getParent();
						Page thisPage = (Page) thisQuestionEmbrace.getParent().getParent();

						int indexOfOnThisPage = thisPage.contentsP.indexOf(thisQuestion);

						Heading thisHeading = thisQuestion.thisHeading;

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

		//answer secondary action
		inputM.put(KeyStroke.getKeyStroke("shift ENTER"), "pressedShiftEnter");
		actionM.put("pressedShiftEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					JPanel thisQuestionEmbrace = (JPanel) thisQuestion.getParent().getParent();
					Page thisPage = (Page) thisQuestionEmbrace.getParent().getParent();

					int indexOfOnThisPage = thisPage.contentsP.indexOf(thisQuestion);

					Heading thisHeading = thisQuestion.thisHeading;

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

					Main.keyPressed = true;
				}
			}
		});

		//answer tertiary action
		inputM.put(KeyStroke.getKeyStroke("control ENTER"), "pressedCtrlEnter");
		actionM.put("pressedCtrlEnter", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					JPanel thisQuestionEmbrace = (JPanel) thisQuestion.getParent().getParent();
					Page thisPage = (Page) thisQuestionEmbrace.getParent().getParent();
					int thisHeadingIndex = Main.headings.indexOf(thisQuestion.thisHeading);
					
					new NewSegmentDialog(thisPage, thisHeadingIndex+1);
					Main.keyPressed = true;	
				}
			}
		});

		//answer delete action
		inputM.put(KeyStroke.getKeyStroke("shift DELETE"), "pressedShiftDelete");
		actionM.put("pressedShiftDelete", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
			{
				if (!Main.keyPressed)
				{
					JPanel thisAnswerArea = (JPanel) embraceA.getParent();

					int previousAnswerIndex = thisQuestion.contentsQ.indexOf(thisAnswer)-1;

					thisAnswerArea.remove(embraceA);
					thisQuestion.contentsQ.remove(thisAnswer);

					int thisQuestionSize = thisQuestion.contentsQ.size()-1;

					for (int i = 1; i < thisQuestionSize+1; i++)
					{
						thisQuestion.contentsQ.get(i).letter.setText((char)(i+96)+")  ");
					}

					Main.masterWindow.validate();
					Main.masterWindow.repaint();

					if (previousAnswerIndex == 0)
					{
						thisQuestion.requestFocus();
						thisQuestion.scrollRectToVisible(thisQuestion.getBounds());
						Main.focusOwner = thisQuestion;
					}
					else
					{
						thisQuestion.contentsQ.get(previousAnswerIndex).requestFocus();
						thisQuestion.contentsQ.get(previousAnswerIndex).scrollRectToVisible(thisQuestion.contentsQ.get(previousAnswerIndex).getBounds());
						Main.focusOwner = thisQuestion.contentsQ.get(previousAnswerIndex);
					}
					Main.keyPressed = true;
				}
			}
		});

		addFocusListener(new FocusAdapter(){

			@Override
			public void focusGained(FocusEvent e)
			{
				Main.focusOwner = thisAnswer;
			}			
		});

		//add document filter to document
		filter = new CustomDocumentFilter();
		filter.setCharLimit(250);

		document = (AbstractDocument) this.getDocument();
		document.setDocumentFilter(filter);

		//setup enclosing JPanel
		embraceA = new JPanel();
		embraceA.setLayout(new BoxLayout(embraceA, BoxLayout.X_AXIS));
		embraceA.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));

		//create visual object from components
		checkArea.add(check);
		letterArea.add(letter, BorderLayout.EAST);
		embraceA.add(checkArea);
		embraceA.add(letterArea);
		embraceA.add(this);

		//add enclosing JPanel to page and to questions's content ArrayList
		q.answerArea.add(embraceA, indexQ);	
		q.contentsQ.add(indexQ, this);
		q.checkBoxes.add(check);
		
		Main.focusOwner = this;

		//validate and repaint visually changed component
		q.answerArea.validate();
		q.answerArea.repaint();
	}
}
