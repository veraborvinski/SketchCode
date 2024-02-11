package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import processing.app.Base;

@SuppressWarnings("serial")
public class PanelMenu implements ActionListener{
	final JPopupMenu panelMenu = new JPopupMenu("panelMenu");
	
	JPopupMenu setSize = new JPopupMenu("SetSize");
	JTextField height = new JTextField( 4 );
	JTextField width = new JTextField( 4 );
	JButton confirmSize = new JButton();
	
	GUIPanel p;
	Component currentComponent;
	int currentX;
	int currentY;
	
	public PanelMenu(GUIPanel initP){
		p = initP;
		panelMenu.add("Change background").addActionListener( this );
		panelMenu.add("Paste").addActionListener( this );
		panelMenu.add("Rezise canvas").addActionListener( this );
		panelMenu.add("Rotate").addActionListener( this );
		panelMenu.add("Flip").addActionListener( this );
		panelMenu.add("Zoom").addActionListener( this );
		
		setSize.add(width);
		setSize.add(height);
		JLabel label2 = new JLabel("OK");
		confirmSize.add(label2);
		confirmSize.setActionCommand("confirmSize");
		confirmSize.addActionListener(this);
        setSize.add(confirmSize);
	}
	
	public void showPanelMenu(Component c, int x, int y) {
		panelMenu.show(c, x, y);
		currentComponent = c;
		currentX = x;
		currentY = y;
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "Paste":
				for (ShapeBuilder copiedShape: p.copiedShapes) {
					ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);;
					shapeToPaste.moveShape(new Point(currentX,currentY));
					p.shapes.add(shapeToPaste);
					p.updateDraw(shapeToPaste.processingShape);
				}
    			p.repaint();
				break;
			case "Change background":
				System.out.print("Change bg");
				break;
			case "Rezise canvas":
				setSize.show(p, currentX , currentY);
				break;
			case "confirmSize":
				if (width.getText() != "" && height.getText() != "") {
					p.setMinimumSize(new Dimension(Integer.valueOf(width.getText()),Integer.valueOf(height.getText())));
					p.f.setSize(new Dimension(Integer.valueOf(width.getText())+20,Integer.valueOf(height.getText())+20));
					p.revalidate();
				}
				break;
			case "Rotate":
				System.out.print("Rotate");
				break;
			case "Flip":
				System.out.print("Flip");
				break;
			case "Zoom":
				System.out.print("Zoom");
				break;
			default:
				System.out.print("Default");
		}
	}
}
