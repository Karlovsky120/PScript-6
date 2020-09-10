package engine;

import java.util.ArrayList;

import javax.swing.JPanel;

import core.Main;
import documentElements.Heading;
import documentElements.Page;

public class Resetter
{
	public static void close()
	{
		Main.workspace.removeAll();

		Main.pages = null;
		Main.headings = null;

		Main.documentOpen = false;
		Main.wasPreviouslySaved = false;
		Main.currentSaveLocation = "";
		Main.fileName = "";
		
		Main.pages = null;
		Main.headings = null;

		Main.masterWindow.setTitle(Main.lang.getString("title"));

		Main.masterWindow.validate();
		Main.masterWindow.repaint();
	}
	
	public static void reset(String fileName)
	{
		//add document name to title
		Main.masterWindow.setTitle(Main.lang.getString("title")+ " - "+ fileName);

		//null component for index setting
		JPanel nill = new JPanel();
		nill.setVisible(false);

		//initialization of ArrayLists
		Main.pages = new ArrayList<Page>(30);
		Main.pages.add(null);

		Main.headings = new ArrayList<Heading>(5);
		Main.headings.add(null);

		//null element
		Main.workspace.add(nill);

		Main.documentOpen = true;

		Main.masterWindow.revalidate();
		Main.masterWindow.repaint();
	}

}
