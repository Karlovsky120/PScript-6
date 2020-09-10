package documentElements;

import gui.ErrorDialog;
import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import core.Main;

public class CustomDocumentFilter extends DocumentFilter
{	
	private int charLimit = 100;

	//used to set charLimit for CustomDocumentFilter
	public void setCharLimit(int limit)
	{
		charLimit = limit;
	}
	
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) 
	{
		if ((fb.getDocument().getLength() + str.length()) <= charLimit)
		{
			str = str.replaceAll("\n", " ");
			str = str.replaceAll("\t", " ");
			try
			{
				fb.insertString(offs, str, a);
			}
			catch (BadLocationException e)
			{
				new ErrorDialog(Main.masterWindow, Main.lang.getString("errorFilterTitle"), Main.lang.getString("errorFilterMessage"), e, false);
			}
		}
		else
		{
			int spaceLeft = charLimit - fb.getDocument().getLength();
			if (spaceLeft <= 0)
			{
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			
			str = str.substring(0, spaceLeft);
			str = str.replaceAll("\n", " ");
			str = str.replaceAll("\t", " ");

			try
			{
				fb.insertString(offs, str, a);
			}
			catch (BadLocationException e)
			{
				new ErrorDialog(Main.masterWindow, Main.lang.getString("errorFilterTitle"), Main.lang.getString("errorFilterMessage"), e, false);
			}
		}
	}

	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
	{
		if (str.equals("\n") || str.equals("\t"))
		{ 
			str = "";
		}
		if ((fb.getDocument().getLength() + str.length() - length) <= charLimit)
		{
			str = str.replaceAll("\n", " ");
			str = str.replaceAll("\t", " ");
			try
			{
				fb.replace(offs, length, str, a);
			}
			catch (BadLocationException e)
			{
				new ErrorDialog(Main.masterWindow, Main.lang.getString("errorFilterTitle"), Main.lang.getString("errorFilterMessage"), e, false);
			}
		}
		else
		{
			int spaceLeft = charLimit - fb.getDocument().getLength() + length;
			if (spaceLeft <= 0)
			{
				Toolkit.getDefaultToolkit().beep();
				return;	
			}
			try
			{
				fb.replace(offs, length, str.substring(0,spaceLeft).replaceAll("\n", " "), a);
			}
			catch (BadLocationException e)
			{
				new ErrorDialog(Main.masterWindow, Main.lang.getString("errorFilterTitle"), Main.lang.getString("errorFilterMessage"), e, false);
			}
		}
	}
}



