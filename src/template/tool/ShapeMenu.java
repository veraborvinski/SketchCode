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
	JPopupMenu setNumberOfItems = new JPopupMenu("SetNumberOfItems");
	JPopupMenu setOrderOfItems = new JPopupMenu("SetOrderOfItems");
	
	JTextField startAngle = new JTextField( 3 );
	JTextField endAngle = new JTextField( 3 );
	JTextField xCoords = new JTextField( 4 );
	JTextField yCoords = new JTextField( 4 );
	JTextField height = new JTextField( 4 );
	JTextField width = new JTextField( 4 );
	JTextField comment = new JTextField( 35 );
	JTextField numberOfItems = new JTextField( 2 );
	
	JButton confirmLocation = new JButton();
	JButton confirmSize = new JButton();
	JButton confirmComment = new JButton();
	JButton confirmArc = new JButton();
	JButton confirmNumberOfItems = new JButton();
	JButton confirmOrderOfItems = new JButton();
	
	Component currentComponent;
	int currentX;
	int currentY;
	GUIPanel p;
	ArrayList<ShapeBuilder> currentShapes = new ArrayList<ShapeBuilder>();
	
	boolean isArcOptionShowing = false;
	boolean isClassOptionShowing = false;
	boolean isToClassOptionShowing = false;
	
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
		currentShapes.addAll(shapes);
		
		if (shapes.size() == 1 && shapes.get(0).processingShape.contains("arc(") && !isArcOptionShowing) {
			shapeMenu.add("Redraw arc").addActionListener( this );
			setArc.add(startAngle);
			setArc.add(endAngle);
			JLabel label4 = new JLabel("OK");
			confirmArc.add(label4);
			confirmArc.setActionCommand("confirmArc");
			confirmArc.addActionListener(this);
			setArc.add(confirmArc);
			isArcOptionShowing = true;
		} else if (shapes.size() == 1) {
			shapeMenu.add("Animate").addActionListener( this );
			shapeMenu.add("Add action").addActionListener( this );
			if (p.findShapeGroup(shapes.get(0)) != null && !isClassOptionShowing) {
				shapeMenu.add("Select class").addActionListener( this );
				shapeMenu.add("Remove from class").addActionListener( this );
				shapeMenu.add("Edit class").addActionListener( this );
				
				for (ShapeBuilder s: p.findShapeGroup(shapes.get(0)).shapes) {
					setOrderOfItems.add(s.processingShape).addActionListener( this );
				}
				
				setOrderOfItems.add("Add shape").addActionListener( this );
				isClassOptionShowing = true;
			} else if (!isToClassOptionShowing) {
				shapeMenu.add("Create class").addActionListener( this );
				
				JLabel label6 = new JLabel("Number of shapes: ");
				setNumberOfItems.add(label6);
				setNumberOfItems.add(numberOfItems);
				JLabel label5 = new JLabel("OK");
				confirmNumberOfItems.add(label5);
				confirmNumberOfItems.setActionCommand("confirmNumberOfItems");
				confirmNumberOfItems.addActionListener(this);
				setNumberOfItems.add(confirmNumberOfItems);
				
				isToClassOptionShowing = true;
			}
		}
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "Animate":
				p.f.buttonMenus.get("bAnimate").shape = currentShapes.get(0);
				p.f.buttonMenus.get("bAnimate").showButtonMenu(p, currentX, currentY);
				break;
			case "Add action":
				p.f.buttonMenus.get("bButton").shape = currentShapes.get(0);
				p.f.buttonMenus.get("bButton").showButtonMenu(p, currentX, currentY);
				break;
			case "Edit class":
				setOrderOfItems.show(p, currentX, currentY);
				break;
			case "Select class":
				p.selectedShapes.clear();
				p.selectedShapes.addAll(p.findShapeGroup(currentShapes.get(0)).shapes);
				p.createComboBoxFromSelectedShapes();
				p.repaint();
				break;
			case "Remove from class":
				p.findShapeGroup(currentShapes.get(0)).shapes.remove(currentShapes.get(0));
				p.removeProcessingLine("\t\t"+currentShapes.get(0).processingShape);
				p.updateDraw("\t"+currentShapes.get(0).processingShape);
				break;
			case "Create class":
				setNumberOfItems.show(p, currentX, currentY);
				break;
			case "confirmNumberOfItems":
				if (numberOfItems.getText() != "") {
					ArrayList<ShapeBuilder> clonedShapes = new ArrayList<>(Collections.nCopies(Integer.valueOf(numberOfItems.getText()), currentShapes.get(0)));
					p.shapes.addAll(clonedShapes);
					p.selectedShapes.clear();
					p.selectedShapes.addAll(clonedShapes);
					p.groupShapes();
				}
				p.repaint();
				break;
			case "Redraw arc":
				setArc.show(p, currentX, currentY);
				break;
			case "confirmArc":
				if (startAngle.getText() != "" && endAngle.getText() != "") {
					String[] splitArc = currentShapes.get(0).processingShape.split(",");
					p.replaceProcessingLine("\t" + splitArc[0] + "," + splitArc[1] + "," + splitArc[2] + "," + splitArc[3] + ", " + String.valueOf(Math.toRadians(Integer.valueOf(startAngle.getText()))) + ", " + String.valueOf(Math.toRadians(Integer.valueOf(endAngle.getText()))) + "," + splitArc[6], p.findProcessingShapeLine(currentShapes.get(0)));
					p.f.updateDrawingFromCode(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
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
					p.removeProcessingLine("\t"+currentShape.processingShape);
					p.insertProcessingLine("\t"+currentShape.processingShape, p.findProcessingLineNumber("void draw() {"));
				}
				p.repaint();
				break;
			case "Bring to front":
				for (ShapeBuilder currentShape: currentShapes) {
					p.shapes.remove(currentShape);
					p.shapes.add(currentShape);
					p.removeProcessingLine("\t"+currentShape.processingShape);
					p.updateDraw(currentShape.processingShape);
				}
				p.repaint();
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
				p.comboBox = null;
				p.repaint();
				break;
			case "confirmSize":
				if (width.getText() != "" && height.getText() != "") {
					for (ShapeBuilder currentShape: currentShapes) {
						currentShape.stretchShape(new Point(currentShape.secondPoint.x+Integer.valueOf(width.getText()), currentShape.secondPoint.y+Integer.valueOf(height.getText())),1);
					}
				}
				p.comboBox = null;
				p.repaint();
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
				p.groupShapes();
				break;
			case "Add shape":
				p.currentClass = p.findShapeGroup(currentShapes.get(0)).name;
			default:
				for (ShapeBuilder s: p.shapes) {
					if (e.getActionCommand() == s.processingShape) {
						currentShapes.clear();
						currentShapes.add(s);
						showShapeMenu(p, currentX, currentY, currentShapes);
					}
				}
				break;
		}
	}
}
