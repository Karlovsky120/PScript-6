package engine;

import gui.CloseDocumentDialog;
import gui.ErrorDialog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

import core.Main;
import documentElements.Answer;
import documentElements.ListElement;
import documentElements.Page;
import documentElements.Heading;
import documentElements.Question;

public class Open
{
	public static String deunicodize(String string)
	{
		string = string.replace("\\u0106", "Æ");
		string = string.replace("\\u0107", "æ");
		string = string.replace("\\u010C", "È");
		string = string.replace("\\u010D", "è");
		string = string.replace("\\u0110", "Ð");
		string = string.replace("\\u0111", "ð");
		string = string.replace("\\u0160", "Š");
		string = string.replace("\\u0161", "š");
		string = string.replace("\\u017D", "Ž");
		string = string.replace("\\u017E", "ž");
		return string;
	}

	public static void openFile(String fileLocation, String fileName)
	{
		try
		{	
			PdfReader reader = new PdfReader(fileLocation);

			PdfDictionary dictionary = reader.getCatalog();

			Resetter.reset(fileName);

			int lineCounter = 2;

			PdfName index = new PdfName("1");
			PdfObject line = new PdfString();
			String string;

			line = dictionary.getAsString(index);
			string = line.toString();

			int headingNumber = Integer.parseInt(string);

			int pageCounter = 1;

			//process headings
			for (int i = 1; i < headingNumber+1; i++)
			{
				index = new PdfName(Integer.toString(lineCounter));
				lineCounter++;
				line = dictionary.getAsString(index);
				string = line.toString();

				int breakSign = string.indexOf("/");

				int pageNumber = Integer.parseInt(string.substring(0, breakSign));
				boolean type = (Integer.parseInt(string.substring(breakSign+1, breakSign+2)) == 1);
				String text = string.substring(breakSign+3, string.length());

				Heading h = null;

				int componentCounter = 1;

				if (type)
				{
					for (int j = 1; j < pageNumber+1; j++)
					{
						Page p = new Page(pageCounter);
						pageCounter++;

						int startIndex = 1;
						if (j == 1)
						{
							h = new Heading(p, i, true, deunicodize(text));
							startIndex++;
						}

						index = new PdfName(Integer.toString(lineCounter));
						lineCounter++;
						line = dictionary.getAsString(index);
						string = line.toString();

						int componentNumber = Integer.parseInt(string);

						for (int k = startIndex; k < componentNumber+1; k++)
						{					
							index = new PdfName(Integer.toString(lineCounter));
							lineCounter++;
							line = dictionary.getAsString(index);
							string = line.toString();

							breakSign = string.indexOf("/");

							int answerNumber = Integer.parseInt(string.substring(0, breakSign));
							text = string.substring(breakSign+1, string.length());

							Question q = new Question(p, h, k, componentCounter, deunicodize(text));
							componentCounter++;

							for (int l = 1; l < answerNumber+1; l++)
							{
								index = new PdfName(Integer.toString(lineCounter));
								lineCounter++;
								line = dictionary.getAsString(index);
								string = line.toString();

								breakSign = string.indexOf("/");

								boolean state = Boolean.parseBoolean(string.substring(0, breakSign));
								text = string.substring(breakSign+1, string.length());

								new Answer(q, l, deunicodize(text), state);
							}
						}

					}
				}

				else
				{
					for (int j = 1; j < pageNumber+1; j++)
					{
						Page p = new Page(pageCounter);
						pageCounter++;

						int startIndex = 1;
						if (j == 1)
						{
							h = new Heading(p, headingNumber, false, deunicodize(text));
							startIndex++;
						}

						index = new PdfName(Integer.toString(lineCounter));
						lineCounter++;
						line = dictionary.getAsString(index);
						string = line.toString();

						int componentNumber = Integer.parseInt(string);

						for (int k = startIndex; k < componentNumber+1; k++)
						{
							index = new PdfName(Integer.toString(lineCounter));
							lineCounter++;
							line = dictionary.get(index);
							string = line.toString();

							new ListElement(p, h, k, componentCounter, deunicodize(string));
							componentCounter++;
						}
					}
				}
			}

			Main.workspace.requestFocus();
			Main.focusOwner = Main.workspace;

			System.out.println("opened!"); //TODO		
		}

		catch (IOException e)
		{
			new ErrorDialog(Main.masterWindow, Main.lang.getString("errorOpenDocumentTitle"), Main.lang.getString("errorOpenDocumentMessage"), e, false);
		}

		Main.headings.get(1);
	}

	public static void openPFile(String fileLocation, String fileName)
	{
		try
		{
			File pdfplabFile = new File(fileLocation);

			FileInputStream fstream = new FileInputStream(pdfplabFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			Resetter.reset(fileName);			

			String line = br.readLine();

			int headingNumber = Integer.parseInt(line);

			int pageCounter = 1;

			//process headings
			for (int i = 1; i < headingNumber+1; i++)
			{
				line = br.readLine();

				int breakSign = line.indexOf("/");

				int pageNumber = Integer.parseInt(line.substring(0, breakSign));
				boolean type = (Integer.parseInt(line.substring(breakSign+1, breakSign+2)) == 1);
				String text = line.substring(breakSign+3, line.length());

				Heading h = null;

				int componentCounter = 1;

				if (type)
				{
					for (int j = 1; j < pageNumber+1; j++)
					{
						Page p = new Page(pageCounter);
						pageCounter++;

						int startIndex = 1;
						if (j == 1)
						{
							h = new Heading(p, i, true, deunicodize(text));
							startIndex++;
						}

						line = br.readLine();

						int componentNumber = Integer.parseInt(line);

						for (int k = startIndex; k < componentNumber+1; k++)
						{					
							line = br.readLine();

							breakSign = line.indexOf("/");

							int answerNumber = Integer.parseInt(line.substring(0, breakSign));
							text = line.substring(breakSign+1, line.length());

							Question q = new Question(p, h, k, componentCounter, deunicodize(text));
							componentCounter++;

							for (int l = 1; l < answerNumber+1; l++)
							{
								line = br.readLine();

								breakSign = line.indexOf("/");

								boolean state = Boolean.parseBoolean(line.substring(0, breakSign));
								text = line.substring(breakSign+1, line.length());

								new Answer(q, l, deunicodize(text), state);
							}
						}

					}
				}

				else
				{
					for (int j = 1; j < pageNumber+1; j++)
					{
						Page p = new Page(pageCounter);
						pageCounter++;

						int startIndex = 1;
						if (j == 1)
						{
							h = new Heading(p, headingNumber, false, deunicodize(text));
							startIndex++;
						}

						line = br.readLine();

						int componentNumber = Integer.parseInt(line);

						for (int k = startIndex; k < componentNumber+1; k++)
						{
							line = br.readLine();

							new ListElement(p, h, k, componentCounter, deunicodize(line));
							componentCounter++;
						}
					}
				}
			}				

			br.close();

			Main.workspace.requestFocus();
			Main.focusOwner = Main.workspace;

			System.out.println("opened!"); //TODO	
		}

		catch (IOException e)
		{
			new ErrorDialog(Main.masterWindow, Main.lang.getString("errorOpenDocumentTitle"), Main.lang.getString("errorOpenDocumentMessage"), e, false);
		}


	}

	public Open(boolean documentOpen)
	{
		if (documentOpen)
		{
			new CloseDocumentDialog(3);
		}

		else
		{
			//TODO translate file chooser

			JFileChooser opener;

			String defaultOpenLocation = Main.config.getProperty("defaultOpenLocation");

			if (defaultOpenLocation.equals(""))
			{
				opener = new JFileChooser();
			}

			else
			{
				opener = new JFileChooser(defaultOpenLocation);
			}

			opener.setDialogTitle(Main.lang.getString("open..."));
			opener.setFileHidingEnabled(false);
			opener.removeChoosableFileFilter(opener.getFileFilter()); 
			opener.setFileFilter(new FileNameExtensionFilter(".pdf", "pdf", "pdfplab"));

			int returnVal = opener.showDialog(Main.masterWindow, Main.lang.getString("open"));

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{	
				if (opener.getSelectedFile().exists())
				{			
					String fileName = null;

					Main.statusLocked = true;
					Main.statusBar.setText("File opened!"); //TODO

					if (opener.getSelectedFile().getName().endsWith(".pdf"))
					{
						fileName = opener.getSelectedFile().getName().replace(".pdf", "");
						fileName = fileName.replace(".PDF", "");
						openFile(opener.getCurrentDirectory().getPath()+"\\"+fileName+".pdf", fileName);
					}

					else if (opener.getSelectedFile().getName().endsWith(".pdfplab"))
					{
						fileName = opener.getSelectedFile().getName().replace(".pdfplab", "");
						fileName = fileName.replace(".PDFPLAB", "");
						openPFile(opener.getCurrentDirectory().getPath()+"\\"+fileName+".pdfplab", fileName);
					}		

					Main.currentSaveLocation = opener.getCurrentDirectory().getPath()+"\\"+fileName+".pdf";
					Main.wasPreviouslySaved = true;
					Main.fileName = fileName;

					Main.snap = true;	

					Main.statusLocked = false;
				}

				else
				{
					//TODO dialog (file in use??)
					new Open(false);
				}
			}
		}
	}

}
