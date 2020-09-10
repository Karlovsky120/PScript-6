package resources;

import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

public class ITextResources
{
	public static Font loadFont(String fontName, int size)
	{
		try
		{
			BaseFont base = BaseFont.createFont(JavaResources.class.getResource(fontName+".ttf").toString(), BaseFont.IDENTITY_H, false);
			Font font = new Font(base, size, Font.NORMAL);

			return font;
		}

		catch (DocumentException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}	
}
