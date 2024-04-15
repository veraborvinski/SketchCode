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
	
	JPopupMenu setLink = new JPopupMenu("SetLink");
	JTextField link = new JTextField( 35 );
	JButton confirmLink = new JButton();
	
	ShapeBuilder shape = null;
	
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
		
		setLink.add(link);
		JLabel label3 = new JLabel("OK");
		confirmLink.add(label3);
		confirmLink.setActionCommand("confirmLink");
		confirmLink.addActionListener(this);
        setLink.add(confirmLink);
	}
	
	public void showButtonMenu(Component c, int x, int y) {
		buttonMenu.show(c, x, y);
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
			case "bLink":
				setLink.show(p, buttonMenu.getX(), buttonMenu.getY());
				break;
			case "confirmLink":
				p.currentAction = link.getText();
				
				if (shape != null) {
					p.addAction(shape);
				}
				break;
			case "bAnimation":
				p.animationAsAction = true;
				p.f.buttonMenus.get("bAnimate").showButtonMenu(p, buttonMenu.getX(), buttonMenu.getY());
				break;
			case "bUpAndDown":
			case "bBackAndForth":
			case "bExpandAndContract":
				if (p.animationAsAction) {
					p.currentAction =  e.getActionCommand();
					p.animationAsAction = false;
					
					if (shape != null) {
						p.addAction(shape);
					}
				} else {
					p.currentAnimation = e.getActionCommand();
					
					if (shape != null) {
						p.addAnimation(shape);
					}
				}
				break;
			default:
				if (p.currentEvent == e.getActionCommand()) {
					if(e.getActionCommand() == "bCircle") {
    					p.f.buttons.get("bEllipse").setOpaque(false);
        				p.f.buttons.get("bEllipse").setBackground(Color.LIGHT_GRAY);
        			} else if (e.getActionCommand() == "bSquare") {
    					p.f.buttons.get("bRect").setOpaque(false);
        				p.f.buttons.get("bRect").setBackground(Color.LIGHT_GRAY);
        			} else if (e.getActionCommand() == "bCurve" || e.getActionCommand() == "bBezier") {
    					p.f.buttons.get("bLine").setOpaque(false);
        				p.f.buttons.get("bLine").setBackground(Color.LIGHT_GRAY);
        			} else if (e.getActionCommand() == "bScalene" || e.getActionCommand() == "bIsosceles" || e.getActionCommand() == "bEquilateral") {
    					p.f.buttons.get("bTriangle").setOpaque(false);
        				p.f.buttons.get("bTriangle").setBackground(Color.LIGHT_GRAY);
        			} else if (e.getActionCommand() == "bChord" || e.getActionCommand() == "bOpen" || e.getActionCommand() == "bPie") {
    					p.f.buttons.get("bArc").setOpaque(false);
        				p.f.buttons.get("bArc").setBackground(Color.LIGHT_GRAY);
        			}
	        		
	        		p.currentEvent = "";
	        	} else if (p.currentEvent != e.getActionCommand()) {
	        		p.f.keyListeners.deselectAll();
	        		
	        		p.currentEvent = e.getActionCommand();
	        		
        			if(e.getActionCommand() == "bCircle") {
        				p.f.buttons.get("bEllipse").setBackground(Color.GRAY);
        			} else if (e.getActionCommand() == "bSquare") {
        				p.f.buttons.get("bRect").setBackground(Color.GRAY);
        			} else if (e.getActionCommand() == "bCurve" || e.getActionCommand() == "bBezier") {
        				p.f.buttons.get("bLine").setBackground(Color.GRAY);
        			} else if (e.getActionCommand() == "bScalene" || e.getActionCommand() == "bIsosceles" || e.getActionCommand() == "bEquilateral") {
        				p.f.buttons.get("bTriangle").setBackground(Color.GRAY);
        			} else if (e.getActionCommand() == "bChord" || e.getActionCommand() == "bOpen" || e.getActionCommand() == "bPie") {
        				p.f.buttons.get("bArc").setBackground(Color.GRAY);
        			}  
	        	}
		}
	}
}
