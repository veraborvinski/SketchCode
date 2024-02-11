package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.imageio.*;

import processing.app.Base;

@SuppressWarnings("serial")
public class ButtonMenu implements ActionListener{
	final JPopupMenu buttonMenu = new JPopupMenu("ButtonMenu");
	GUIPanel p;
	
	public ButtonMenu(GUIPanel initP, String[][] buttons){
		p = initP;
		
		for (String[] button: buttons) {
			JMenuItem menuButton;
			try {
	        	  Image icon = ImageIO.read(getClass().getResource(button[2]));
	        	  menuButton = new JMenuItem(new ImageIcon(icon.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH)));
	      	} catch (Exception e) {
	      			menuButton = new JMenuItem(button[1]);
	      		    System.out.println(e);
	  		}
			menuButton.setToolTipText(button[1]);
			menuButton.setActionCommand(button[0]);
			buttonMenu.add(menuButton).addActionListener( this );
		}
	}
	
	public void showShapeMenu(Component c, int x, int y) {
		buttonMenu.show(c, x, y);
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		p.currentEvent = e.getActionCommand();
	}
}
