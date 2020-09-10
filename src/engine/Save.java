package engine;

import gui.EmptyFieldsDialog;
import gui.ErrorDialog;
import gui.NewDocumentDialog;
import gui.OverwriteDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import resources.ITextResources;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;

import core.Main;

import documentElements.Heading;
import documentElements.ListElement;
import documentElements.Page;
import documentElements.Question;
import documentElements.Answer;


public class Save
{
	public static boolean emptyExists()
	{
		boolean emptyElement = false;

		for (int i = Main.headings.size()-1; i > 0; i--)
		{
			Heading h = Main.headings.get(i);

			boolean type = h.type;
			int elementNumber = h.contentsH.size()-1;

			if (type)
			{
				for (int j = elementNumber; j > 0; j--)
				{
					Question q = (Question) h.contentsH.get(j);

					int answerNumber = q.contentsQ.size()-1;

					for (int k = answerNumber; k > 0; k--)
					{
						Answer a = (Answer) q.contentsQ.get(k);

						if (a.getText().equals(""))
						{
							q.answerArea.remove(k);
							q.contentsQ.remove(k);
							q.checkBoxes.remove(k);

							q.embraceQ.validate();
							q.embraceQ.repaint();

							for (int l = 1; l < q.contentsQ.size(); l++)
							{
								q.contentsQ.get(l).letter.setText((char)(l+96)+")  ");
							}
						}
					}

					answerNumber = q.contentsQ.size()-1;

					if (q.getText().equals(""))
					{
						if ((answerNumber == 0) && (h.contentsH.size() != 2))
						{
							((Page) q.embraceQ.getParent().getParent()).contentsP.remove(q);
							q.embraceQ.getParent().remove(q.embraceQ);
							h.contentsH.remove(j);

							Main.workspace.validate();
							Main.workspace.repaint();

							elementNumber--;

							for (int k = 1; k < elementNumber+1; k++)
							{
								((Question) h.contentsH.get(k)).number.setText(k+".  ");
							}
						}

						else
						{
							emptyElement = true;
							Main.focusOwner = q;
						}
					}
				}

				elementNumber = h.contentsH.size()-1;

				if (h.getText().equals(""))
				{
					if ((elementNumber == 0) && (Main.headings.size() != 2))
					{
						Main.workspace.remove((Page) h.embraceH.getParent().getParent());
						Main.headings.remove(h);

						Main.workspace.validate();
						Main.workspace.repaint();
					}

					else
					{
						emptyElement = true;
						Main.focusOwner = h;
					}
				}
			}

			else
			{
				for (int j = elementNumber; j > 0; j--)
				{
					ListElement l = (ListElement) h.contentsH.get(j);

					if (l.getText().equals(""))
					{
						((Page) l.embraceL.getParent().getParent()).contentsP.remove(l);
						l.embraceL.getParent().remove(l.embraceL);
						h.contentsH.remove(j);

						Main.workspace.validate();
						Main.workspace.repaint();
					}
				}
			}		
		}

		Main.workspace.validate();
		Main.workspace.repaint();

		return emptyElement;
	}

	public static String unicodize(String string)
	{
		string = string.replace("Æ", "\\u0106");
		string = string.replace("æ", "\\u0107");
		string = string.replace("È", "\\u010C");
		string = string.replace("è", "\\u010D");
		string = string.replace("Ð", "\\u0110");
		string = string.replace("ð", "\\u0111");
		string = string.replace("Š", "\\u0160");
		string = string.replace("š", "\\u0161");
		string = string.replace("Ž", "\\u017D");
		string = string.replace("ž", "\\u017E");
		return string;
	}

	public static ArrayList<String> createReadableSave()
	{	
		ArrayList<String> saveFileData = new ArrayList<String>();

		saveFileData.add("");

		//add number of headings to saveData
		saveFileData.add(Integer.toString(Main.headings.size()-1));

		int numberOfPages = Main.pages.size()-1;

		for (int i = 1; i < Main.headings.size(); i++)
		{
			Heading h = Main.headings.get(i);
			Page thisHeadingPage = (Page) h.embraceH.getParent().getParent();
			int thisHeadingPageIndex = Main.pages.indexOf(thisHeadingPage);
			int currentPageIndex = thisHeadingPageIndex;				

			do
			{				
				if (numberOfPages == currentPageIndex)
				{
					break;
				}

				else if (Main.pages.get(currentPageIndex+1).hasHeading)
				{
					break;
				}

				currentPageIndex++;
			}
			while (true);

			int numberOfThisPages = currentPageIndex - thisHeadingPageIndex + 1;

			if (h.type)
			{
				saveFileData.add(numberOfThisPages+"/1/"+unicodize(h.getText()));
			}

			else
			{
				saveFileData.add(numberOfThisPages+"/0/"+unicodize(h.getText()));
			}

			for (int j = thisHeadingPageIndex; j < currentPageIndex+1; j++)
			{
				Page p = Main.pages.get(j);

				int startNumber = 1;

				int componentsOnPage = p.contentsP.size()-1;

				if (j == thisHeadingPageIndex)
				{
					startNumber = 2;
				}

				saveFileData.add(componentsOnPage+"");

				if (h.type)
				{
					for (int k = startNumber; k < componentsOnPage+1; k++)
					{
						Question q = (Question) p.contentsP.get(k);

						int answerNumber = q.contentsQ.size()-1;

						//add question to saveData
						saveFileData.add(answerNumber+"/"+unicodize(q.getText()));

						for (int l = 1; l < answerNumber+1; l++)
						{
							Answer a = (Answer) q.contentsQ.get(l);

							saveFileData.add(a.check.isSelected()+"/"+unicodize(a.getText()));
						}

					}
				}

				else
				{
					for (int k = startNumber; k < componentsOnPage+1; k++)
					{
						ListElement l = (ListElement) p.contentsP.get(k);

						//add listElement to saveData
						saveFileData.add(unicodize(l.getText()));
					}
				}
			}
		}

		return saveFileData;
	}

	public static File readableSaveToFile(String fileName)
	{
		ArrayList<String> readableSave = createReadableSave();
		File packet = new File(fileName+".pdfplab");

		try
		{
			FileWriter writer = new FileWriter(packet);

			for (int i = 1; i < readableSave.size(); i++)
			{
				writer.write(readableSave.get(i)+System.getProperty("line.separator"));
			}

			writer.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

		return packet;
	}


	public static JTextArea getHeading(Page p)
	{
		return ((Heading) p.contentsP.get(1));
	}

	public static JTextArea getQuestion(Page p, int index)
	{
		return ((Question) p.contentsP.get(index));
	}

	public static JTextArea getListElementText(Page p, int index)
	{
		return ((ListElement) p.contentsP.get(index));
	}

	public static ArrayList<Answer> getQuestionList(Page p, int index)
	{
		return ((Question) p.contentsP.get(index)).contentsQ;
	}

	public static JTextArea getAnswerText(ArrayList<Answer> contentsQ, int index)
	{
		return ((Answer) contentsQ.get(index));
	}

	public static boolean isAnswerCorrect(JTextArea q, int index)
	{
		if (((Question) q).checkBoxes.get(index).isSelected())
		{
			return true;
		}

		else
		{
			return false;
		}
	}

	public static PdfPTable quantizate(JTextArea textBox, Font font, float top, float bottom, float cellSpacingAfter, float tableSpacingAfter)
	{
		PdfPTable lines = new PdfPTable(1);

		String text = textBox.getText();
		int length = text.length();
		int offset = 0;
		int rowEnd;

		lines.setWidthPercentage(100);

		do
		{
			try
			{
				rowEnd = Utilities.getRowEnd(textBox, offset);
				PdfPCell cell = new PdfPCell(new Phrase(text.substring(offset, rowEnd), font));
				cell.setPaddingTop(top);
				cell.setPaddingBottom(bottom);
				lines.addCell(cell);
				offset = rowEnd+1;
				
				cell = new PdfPCell();
				cell.setFixedHeight(cellSpacingAfter);
				lines.addCell(cell);
			}
			catch (BadLocationException e) {/*never going to happen*/}
		}
		while (length > offset);
		
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(tableSpacingAfter);
		lines.addCell(cell);

		return lines;
	}

	public static void createPdfSave(String fileLocation, String filename)
	{
		Document document = new Document(PageSize.A4, 58, 58, 68, 68);

		try
		{
			Font calibriH = ITextResources.loadFont("calibri", 15);
			Font calibriQLA = ITextResources.loadFont("calibri", 12);
			Font calibriAB = ITextResources.loadFont("calibri_bold", 12);

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileLocation));
			writer.setPdfVersion(PdfWriter.PDF_VERSION_1_5);

			//metadata
			document.addTitle(filename);
			document.addHeader("Document version", "PLab PDF 1.0");
			document.addCreator("Perpetuum Lab Script Creator");
			document.addAuthor("Perpetuum Lab community");

			document.open();

			boolean type = true;
			int questionIndex = 1;
			
			//vertical spacing variables, DO NOT CHANGE!
			float cellPaddingTopHeading = 0f;
			float cellPaddingBottomHeading = 2f;
			float cellSpacingAfterHeading = 1.5f;
			float tableSpacingAfterHeading = 6f;
			
			float cellPaddingTopQuestion = 0f;
			float cellPaddingBottomQuestion = 2f;
			float cellSpacingAfterQuestion = 1.625f;
			float tableSpacingAfterQuestion = 3f;
			
			float cellPaddingTopAnswer =0f;
			float cellPaddingBottomAnswer = 2f;
			float cellSpacingAfterAnswer = 1.75f;
			float tableSpacingAfterAnswer = 1.75f;
			
			float spacingAfterQuestion = 3.25f;
			
			float cellPaddingTopListElement = 0f;
			float cellPaddingBottomListElement = 2f;
			float cellSpacingAfterListElement = 1.625f;
			float tableSpacingAfterListElement = 3f;
			

			for (int i = 1; i < Main.pages.size(); i++)
			{
				//process a page
				Page thisPage = Main.pages.get(i);
				int pageIndex = 0;	

				if (thisPage.hasHeading)
				{
					type = ((Heading) thisPage.contentsP.get(1)).type;
					pageIndex++;


					PdfPTable headingText = quantizate(getHeading(thisPage), calibriH, cellPaddingTopHeading, cellPaddingBottomHeading, cellSpacingAfterHeading, tableSpacingAfterHeading);
					
					document.add(headingText);

					if (type)
					{
						questionIndex = 1;
					}
				}

				if (type)
				{
					float[] widths = {32f, 15f, 432f};  //question number, answer letter, answer text

					for (int j = pageIndex+1; j < thisPage.contentsP.size(); j++)
					{	
						//process a question on a page
						PdfPTable questionTable = new PdfPTable(3);
						questionTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
						questionTable.setWidthPercentage(100);
						questionTable.setTotalWidth(widths);
						questionTable.setLockedWidth(true);

						Phrase questionNumber = new Phrase(Integer.toString(questionIndex+957)+".", calibriQLA);
						PdfPCell cell = new PdfPCell(questionNumber);
						cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

						questionTable.addCell(cell);

						PdfPTable questionText = quantizate(getQuestion(thisPage, j), calibriQLA, cellPaddingTopQuestion, cellPaddingBottomQuestion, cellSpacingAfterQuestion, tableSpacingAfterQuestion);
						cell = new PdfPCell(questionText);
						cell.setColspan(2);

						questionTable.addCell(cell);

						ArrayList<Answer> contentsQ = getQuestionList(thisPage, j);

						if (contentsQ.size() != 0)
						{							
							for (int k = 1; k < contentsQ.size(); k++)
							{
								//process an answer of a question
								questionTable.addCell(""); //this leaves sort of indent
								
								Phrase answerLetter;
								
								if (isAnswerCorrect(getQuestion(thisPage, j), k))
								{
									answerLetter = new Phrase(Character.toString((char)(k+96))+")", calibriAB);
								}

								else
								{
									answerLetter = new Phrase(Character.toString((char)(k+96))+")", calibriQLA);
								}
								
								
								cell = new PdfPCell(answerLetter);
								cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

								questionTable.addCell(cell);

								PdfPTable answerText = quantizate(getAnswerText(contentsQ, k), calibriQLA, cellPaddingTopAnswer, cellPaddingBottomAnswer, cellSpacingAfterAnswer, tableSpacingAfterAnswer);
								cell = new PdfPCell(answerText);

								questionTable.addCell(cell);
							}
							
							cell = new PdfPCell();
							cell.setFixedHeight(spacingAfterQuestion);
							cell.setColspan(3);
							questionTable.addCell(cell);		
						}
						
						document.add(questionTable);

						questionIndex++;
					}
				}

				else
				{
					PdfPTable listElementTable = new PdfPTable(2);
					listElementTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

					Phrase bullet = new Phrase("\u2022 ", calibriQLA);
					PdfPCell bulletCell = new PdfPCell(bullet);

					for (int j = pageIndex+1; j < thisPage.contentsP.size(); j++)
					{
						//process a listElement of a page
						listElementTable.addCell(bulletCell);

						PdfPTable listElementText = quantizate(getListElementText(thisPage, j), calibriQLA, cellPaddingTopListElement, cellPaddingBottomListElement, cellSpacingAfterListElement, tableSpacingAfterListElement);
						PdfPCell cell = new PdfPCell(listElementText);

						listElementTable.addCell(cell);
					}	
				}

				document.newPage();
			}

			//add readableSave to PDF
			PdfDictionary dictionary = new PdfDictionary();
			ArrayList<String> content = createReadableSave();

			PdfObject object;
			PdfName index;

			for (int i = 1; i < content.size(); i++)
			{
				object = new PdfString(content.get(i));
				index =  new PdfName(Integer.toString(i));

				dictionary.put(index, object);
			}

			writer.getExtraCatalog().putAll(dictionary);

			document.close();

			System.out.println("saved!"); //TODO
		}

		catch (FileNotFoundException e)
		{
			//TODO file open dialog!!!
			new Save(true, 0);
		}

		catch (DocumentException e)
		{
			new ErrorDialog(Main.masterWindow, Main.lang.getString("errorSavingDocumentTitle"), Main.lang.getString("errorSavingDocumentMessage"), e, false);
		}
	}

	public Save(boolean saveAs, int afterOperation)
	{
		//afterOperation values:
		//1: new
		//2: open
		//3: close
		//4: exit

		if (emptyExists())
		{
			new EmptyFieldsDialog();
		}

		else
		{

			if (saveAs)
			{
				//TODO translate file chooser

				JFileChooser saver;

				String defaultSaveLocation = Main.config.getProperty("defaultSaveLocation");

				if (defaultSaveLocation.equals(""))
				{
					saver = new JFileChooser();
				}

				else
				{
					saver = new JFileChooser(defaultSaveLocation);
				}

				//set JFileChooser properties
				saver.setDialogTitle(Main.lang.getString("save"));
				saver.setFileHidingEnabled(false);
				saver.setSelectedFile(new File(Main.fileName));
				saver.removeChoosableFileFilter(saver.getFileFilter());
				saver.setFileFilter(new FileNameExtensionFilter(".pdf", "pdf"));

				int returnVal = saver.showDialog(Main.masterWindow, Main.lang.getString("save"));

				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					//get file name and path
					String currentDirectoryPath = saver.getCurrentDirectory().getPath();
					String fileName = saver.getSelectedFile().getName();

					fileName = fileName.replace(".pdf", "");
					fileName = fileName.replace(".PDF", "");

					//create new file 
					File file = new File(currentDirectoryPath+"\\"+fileName+".pdf");

					//check if exists
					if (file.exists())
					{
						new OverwriteDialog(currentDirectoryPath, fileName, afterOperation);
					}

					else
					{
						Main.statusLocked = true;
						Main.statusBar.setText("Saving file...");
						
						createPdfSave(currentDirectoryPath+"\\"+fileName+".pdf", fileName);
						Main.currentSaveLocation = currentDirectoryPath+"\\"+fileName+".pdf";

						Main.masterWindow.setTitle(Main.lang.getString("title")+" - "+fileName+".pdf");
						Main.wasPreviouslySaved = true;
						Main.fileName = fileName;
						
						Main.statusBar.setText("File saved!");
						Main.statusLocked = false;

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
					}
				}
			}

			else
			{
				Main.statusLocked = true;
				Main.statusBar.setText("File saved!");
				
				createPdfSave(Main.currentSaveLocation, Main.fileName);
				
				Main.statusLocked = false;

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
			}
		}
	}
}