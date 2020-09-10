package resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class JavaResources
{
	public static BufferedImage loadImage(String imageFileName)
	{
		URL url = JavaResources.class.getResource(imageFileName);
		if(url == null) return null;

		try
		{
			return ImageIO.read(url);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static ImageIcon loadIcon(String imageFileName)
	{
		BufferedImage i = loadImage(imageFileName);
		if(i == null) return null;
		return new ImageIcon(i);
	}
	
	public static Font loadFont(String fontName, float size)
	{
		try
		{
			Font font = Font.createFont(Font.TRUETYPE_FONT, JavaResources.class.getResourceAsStream(fontName+".ttf"));
			font = font.deriveFont(size);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			return font;
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}