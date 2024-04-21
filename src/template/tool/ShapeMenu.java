/**
 * The SketchCode tool is used to generate a Processing sketch from a GUI.
 * 
 * Author: Vera Borvinski
 * Matriculation number: 2421818
 * 
 * This tool uses the Processing tool template from https://github.com/processing/processing-tool-template
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author   Vera Borvinski
 * @modified 20/4-24
 * @version  1.0
 */

package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

/** 
* The ShapeMenu class is used to call a JPopupMenu from one or more shapes.
* 
* @author Vera Borvinski
*/
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
	boolean isAnimationOptionShowing = false;
	
	ShapeGroup currentClass = null;
	
	/** 
     * The constructor of a ShapeMenu, adds all the default menu items and submenus.
     * @param initP A reference to the drawing. 
     */
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
	
	/** 
     * Shows the menu at the specified location and adds relevant menu items.
     * @param c The compenent to add the menu to.
     * @param x The x position to add the menu to.
     * @param y The y position to add the menu to.
     * @param shapes The shapes the menu is being called from.
     * @return void Nothing. 
     */
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
			if (!isAnimationOptionShowing) {
				shapeMenu.add("Animate").addActionListener( this );
				shapeMenu.add("Add action").addActionListener( this );
				isAnimationOptionShowing = true;
			}
			if (p.findShapeGroup(shapes.get(0)) != null && !isClassOptionShowing) {
				currentClass = p.findShapeGroup(shapes.get(0));
				shapeMenu.add("Remove from class").addActionListener( this );
				shapeMenu.add("Edit class").addActionListener( this );
				isClassOptionShowing = true;
				isToClassOptionShowing = true;
			} else if (p.findShapeGroup(shapes.get(0)) == null && isClassOptionShowing) {
				shapeMenu.remove(12);
				shapeMenu.remove(11);
				isClassOptionShowing = false;
				isToClassOptionShowing = false;
			} 
			if (!isToClassOptionShowing && p.findShapeGroup(shapes.get(0)) == null) {
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
	
	/** 
     * Calls the functionality of the menu item that was clicked.
     * @param e The ActionEvent that triggered the function.
     * @return void Nothing. 
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "Animate":
				p.f.buttonMenus.get("bAnimate").shape = currentShapes.get(0);
				p.f.buttonMenus.get("bAnimate").showButtonMenu(p, currentX, currentY);
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Add action":
				p.f.buttonMenus.get("bButton").shape = currentShapes.get(0);
				p.f.buttonMenus.get("bButton").showButtonMenu(p, currentX, currentY);
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Edit class":
				if (currentClass != null) {
					setOrderOfItems = null;
					setOrderOfItems = new JPopupMenu("SetOrderOfItems");
					
					for (ShapeBuilder s: currentClass.shapes) {
						setOrderOfItems.add(s.processingShape).addActionListener( this );
					}
					
					setOrderOfItems.add("Add shape").addActionListener( this );
					setOrderOfItems.show(p, currentX, currentY);
				}
				break;
			case "Remove from class":
				p.findShapeGroup(currentShapes.get(0)).shapes.remove(currentShapes.get(0));
				p.removeProcessingLine("\t\t"+currentShapes.get(0).processingShape);
				p.updateDraw(currentShapes.get(0).processingShape);
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Create class":
				setNumberOfItems.show(p, currentX, currentY);
				break;
			case "confirmNumberOfItems":
				if (numberOfItems.getText() != "") {
					p.selectedShapes.clear();
					p.shapes.add(currentShapes.get(0));
					p.selectedShapes.add(currentShapes.get(0));
					for (int i = 1; i < Integer.valueOf(numberOfItems.getText()); i++) {
						ShapeBuilder clonedShape = new ShapeBuilder(currentShapes.get(0).shapeType, new Point(currentShapes.get(0).firstPoint.x+10*i,currentShapes.get(0).firstPoint.y+10*i), new Point(currentShapes.get(0).secondPoint.x+10*i,currentShapes.get(0).secondPoint.y+10*i));
						p.shapes.add(clonedShape);
						p.selectedShapes.add(clonedShape);
					}
					p.createComboBoxFromSelectedShapes();
					p.groupShapes();
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
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
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Copy":
				p.copiedShapes.clear();
				p.copiedShapes.addAll(currentShapes);
				p.repaint();
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Paste":
				for (ShapeBuilder copiedShape: p.copiedShapes) {
					ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);;
					shapeToPaste.moveShape(new Point(currentX,currentY));
					p.shapes.add(shapeToPaste);
					p.updateDraw(shapeToPaste.processingShape);
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
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
    	        
    	        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
    	        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
    	        }
				p.repaint();
				break;
			case "Send to back":
				for (ShapeBuilder currentShape: currentShapes) {
					p.shapes.remove(currentShape);
					p.shapes.add(0, currentShape);
					p.removeProcessingLine("\t"+currentShape.processingShape);
					p.insertProcessingLine("\t"+currentShape.processingShape, p.findProcessingLineNumber("void draw() {"));
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
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
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
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
						String oldLine = currentShape.processingShape;
						currentShape.moveShape(new Point(Integer.valueOf(xCoords.getText())+currentShape.javaShape.getBounds().width/2, Integer.valueOf(yCoords.getText())+currentShape.javaShape.getBounds().height/2));
						p.replaceProcessingLine("\t"+currentShape.processingShape, p.findProcessingLineNumber("\t"+oldLine));
					}
				}
				p.comboBox = null;
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				p.repaint();
				break;
			case "confirmSize":
				if (width.getText() != "" && height.getText() != "") {
					for (ShapeBuilder currentShape: currentShapes) {
						String oldLine = currentShape.processingShape;
						currentShape.stretchShape(new Point(currentShape.firstPoint.x+Integer.valueOf(width.getText()), currentShape.firstPoint.y+Integer.valueOf(height.getText())),Cursor.SE_RESIZE_CURSOR);
						p.replaceProcessingLine("\t"+currentShape.processingShape, p.findProcessingLineNumber("\t"+oldLine));
					}
				}
				p.comboBox = null;
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				p.repaint();
				break;
			case "Comment":
				setComment.show(p, currentX , currentY);
				break;
			case "confirmComment":
				for (ShapeBuilder currentShape: currentShapes) {
					p.insertProcessingLine("\t//" + comment.getText(), p.findProcessingShapeLine(currentShape)-1);
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Group":
				p.groupShapes();
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
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
