package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

@SuppressWarnings("serial")
public class ButtonMenu implements ActionListener{
	final JPopupMenu buttonMenu = new JPopupMenu("ButtonMenu");
	GUIPanel p;
	
	public ButtonMenu(GUIPanel initP, String[] buttons){
		p = initP;
		
		for (String buttonName: buttons) {
			buttonMenu.add(buttonName).addActionListener( this );
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
