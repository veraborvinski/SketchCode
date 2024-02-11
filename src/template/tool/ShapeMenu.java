package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

@SuppressWarnings("serial")
public class ShapeMenu implements ActionListener{
	final JPopupMenu shapeMenu = new JPopupMenu("ShapeMenu");
	JPopupMenu setLocation = new JPopupMenu("SetLocation");
	JPopupMenu setSize = new JPopupMenu("SetSize");
	JPopupMenu setComment = new JPopupMenu("SetComment");
	JPopupMenu setArc = new JPopupMenu("SetArc");
	JTextField startAngle = new JTextField( 3 );
	JTextField endAngle = new JTextField( 3 );
	JTextField xCoords = new JTextField( 4 );
	JTextField yCoords = new JTextField( 4 );
	JTextField height = new JTextField( 4 );
	JTextField width = new JTextField( 4 );
	JTextField comment = new JTextField( 35 );
	JButton confirmLocation = new JButton();
	JButton confirmSize = new JButton();
	JButton confirmComment = new JButton();
	JButton confirmArc = new JButton();
	Component currentComponent;
	int currentX;
	int currentY;
	GUIPanel p;
	ArrayList<ShapeBuilder> currentShapes;
	
	public ShapeMenu(GUIPanel initP){
		p = initP;
		
		shapeMenu.add("Copy").addActionListener( this );
		shapeMenu.add("Paste").addActionListener( this );
		shapeMenu.add("Cut").addActionListener( this );
		shapeMenu.add("Move").addActionListener( this );
		shapeMenu.add("Resize").addActionListener( this );
		shapeMenu.add("Bring to front").addActionListener( this );
		shapeMenu.add("Send to back").addActionListener( this );
		shapeMenu.add("Comment").addActionListener( this );
		shapeMenu.add("Group").addActionListener( this );
		
		setLocation.add(xCoords);
		setLocation.add(yCoords);
		JLabel label1 = new JLabel("OK");
		confirmLocation.add(label1);
		confirmLocation.setActionCommand("confirmLocation");
		confirmLocation.addActionListener(this);
		setLocation.add(confirmLocation);
		
		setSize.add(width);
		setSize.add(height);
		JLabel label2 = new JLabel("OK");
		confirmSize.add(label2);
		confirmSize.setActionCommand("confirmSize");
		confirmSize.addActionListener(this);
        setSize.add(confirmSize);
        
        setComment.add(comment);
		JLabel label3 = new JLabel("OK");
		confirmComment.add(label3);
		confirmComment.setActionCommand("confirmComment");
		confirmComment.addActionListener(this);
        setComment.add(confirmComment);
	}
	
	public void showShapeMenu(Component c, int x, int y, ArrayList<ShapeBuilder> shapes) {
		shapeMenu.show(c, x, y);
		currentComponent = c;
		currentX = x;
		currentY = y;
		currentShapes = shapes;
		if (shapes.size() == 1 && shapes.get(0).processingShape.contains("arc(")) {
			shapeMenu.add("Redraw arc").addActionListener( this );
			setArc.add(startAngle);
			setArc.add(endAngle);
			JLabel label4 = new JLabel("OK");
			confirmArc.add(label4);
			confirmArc.setActionCommand("confirmArc");
			confirmArc.addActionListener(this);
			setArc.add(confirmArc);
		}
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "Redraw arc":
				setArc.show(p, currentX, currentY);
				break;
			case "confirmArc":
				if (startAngle.getText() != "" && endAngle.getText() != "") {
					System.out.print(startAngle.getText() + endAngle.getText() );
				}
				break;
			case "Copy":
				p.copiedShapes.clear();
				p.copiedShapes.addAll(currentShapes);
				p.repaint();
				break;
			case "Paste":
				for (ShapeBuilder copiedShape: p.copiedShapes) {
					ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);;
					shapeToPaste.moveShape(new Point(currentX,currentY));
					p.shapes.add(shapeToPaste);
					p.updateDraw(shapeToPaste.processingShape);
				}
				p.repaint();
				break;
			case "Cut":
				p.copiedShapes.clear();
				p.copiedShapes.addAll(currentShapes);
				for (ShapeBuilder currentShape: currentShapes) {
					p.shapes.remove(currentShape);
					p.removeProcessingLine("\t"+currentShape.processingShape);
				}
				p.selectedShapes.clear();
    			p.comboBox = null;
				p.repaint();
				break;
			case "Send to back":
				for (ShapeBuilder currentShape: currentShapes) {
					p.shapes.remove(currentShape);
					p.shapes.add(0, currentShape);
				}
				break;
			case "Bring to front":
				for (ShapeBuilder currentShape: currentShapes) {
					p.shapes.remove(currentShape);
					p.shapes.add(currentShape);
				}
				break;
			case "Move":
				setLocation.show(p, currentX , currentY);
				break;
			case "Resize":
				setSize.show(p, currentX , currentY);
				break;
			case "confirmLocation":
				if (xCoords.getText() != "" && yCoords.getText() != "") {
					for (ShapeBuilder currentShape: currentShapes) {
						currentShape.moveShape(new Point(Integer.valueOf(xCoords.getText()), Integer.valueOf(yCoords.getText())));
					}
				}
				break;
			case "confirmSize":
				System.out.print("Resize to " + width.getText() + ", " + height.getText());
				break;
			case "Comment":
				setComment.show(p, currentX , currentY);
				break;
			case "confirmComment":
				for (ShapeBuilder currentShape: currentShapes) {
					p.insertProcessingLine("\t//" + comment.getText(), p.findProcessingShapeLine(currentShape)-1);
				}
				break;
			case "Group":
				p.shapesInArrays.add(currentShapes);
				break;
			default:
				System.out.print("Default");
		}
	}
}
