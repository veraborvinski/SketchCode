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

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;

import java.awt.Cursor;

import processing.app.Base;

/** 
* The GUIPanel class creates the main JPanel of the tool.
* 
* @author Vera Borvinski
*/
@SuppressWarnings("serial")
class GUIPanel extends JPanel implements MouseListener, MouseMotionListener{
	
	//store the saves of the code history
	public ArrayList<ArrayList<String>> undoStack = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<String>> redoStack = new ArrayList<ArrayList<String>>();
	
	//drawing specifications of the canvas
	Color fill;
	Color defaultColour = Color.WHITE;
	Color defaultStrokeColour = Color.BLACK;
	int defaultStrokeSize = 1;
	
	Boolean isFlippedHorizontal = false;
	Boolean isFlippedVertical = false;
	int rotation = 0;
	float zoom = 0;
	
	//lists of objects currently on the canvas
	public ArrayList<ShapeBuilder> shapes = new ArrayList<ShapeBuilder>();
	ShapeBuilder currentShape;
	ArrayList<ShapeBuilder> copiedShapes = new ArrayList<ShapeBuilder>();
	ComboBox comboBox = null;
	ArrayList<ShapeBuilder> selectedShapes = new ArrayList<ShapeBuilder>();
	ArrayList<ShapeGroup> shapeGroups = new ArrayList<ShapeGroup>();
	ArrayList<TextBox> textBoxes = new ArrayList<TextBox>();
	
	//used to find how to edit a shape
	int cursorDirection;
	Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	//references to main frame and program
	PanelMenu panelMenu = new PanelMenu(this);
	ShapeMenu shapeMenu = new ShapeMenu(this);
	GUIFrame f;
	Base base;
	
	//attributes of potential shapes
	String currentClass = null;
	String currentAnimation = null;
	String currentAction = null;
	Boolean animationAsAction = false;
	String currentEvent = "";
	Point firstPoint = new Point(0,0);
	
	/** 
    * The constructor of the GUIPanel class, used to add mouse events. 
    * @param height The preferred height of the JPanel.
    * @param width The preferred width of the JPanel.
    * @param initF A reference to the tool. 
    */
    public GUIPanel(int height, int width, GUIFrame initF) {
    	f = initF;
    	setPreferredSize(new Dimension(height, width));
    	
    	//make it possible to interact with the panel
    	addMouseListener(this); 
    	addMouseMotionListener(this);
    }
    
    /** 
    * Paints the shapes that currently exist. 
    * @param g The Graphics object being drawn on.
    * @return void Nothing. 
    */
    public void paintComponent(Graphics g) {     
        super.paintComponent(g);   
        Graphics2D g2 = (Graphics2D) g;
        
        //transform the canvas
        if (zoom != 0) {
	 	    g2.scale(zoom/100, zoom/100);
	    }
	     
	    if (isFlippedHorizontal) {
	     	g2.scale(1, -1);
	     	g2.translate(0, -getHeight());
	    }
	     
	    if (isFlippedVertical) {
	     	g2.scale(-1, 1);
	     	g2.translate(-getWidth(), 0);
	    }
        
	    //draw textboxes
        for(TextBox textBox : textBoxes) {
        	AffineTransform old = g2.getTransform();
        	Rectangle textBoxBounds = textBox.bounds.javaShape.getBounds();
        	g2.rotate(Math.toRadians((textBox.bounds.rotation+rotation)%360), textBoxBounds.x + textBoxBounds.width/2, textBoxBounds.y + textBoxBounds.height/2);      	
        	g2.setColor(textBox.stroke);	
			g2.setStroke(new BasicStroke(textBox.strokeSize));
        	g2.drawString(textBox.text, textBox.bounds.javaShape.getBounds().x, textBox.bounds.javaShape.getBounds().y + 10);
        	g2.setTransform(old);
        }
        
        //draw shapes
        for (ShapeBuilder shape: shapes){
        	AffineTransform old = g2.getTransform();
        	Rectangle shapeBounds = shape.javaShape.getBounds();
        	g2.rotate(Math.toRadians((shape.rotation+rotation)%360), shapeBounds.x + shapeBounds.width/2, shapeBounds.y + shapeBounds.height/2);      	
        	g2.setColor(shape.fill);
        	g2.fill(shape.javaShape);
			g2.setColor(shape.stroke);	
			g2.setStroke(new BasicStroke(shape.strokeSize,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));			
        	g2.draw(shape.javaShape);
        	g2.setTransform(old);
        }
        
        //draw combobox
        if (comboBox != null) {
        	AffineTransform old = g2.getTransform();
        	//only rotate if combobox includes one shape
        	if (selectedShapes.size() == 1) {
        		g2.rotate(Math.toRadians((selectedShapes.get(0).rotation+rotation)%360), comboBox.comboBox.x + comboBox.comboBox.width/2, comboBox.comboBox.y + comboBox.comboBox.height/2);
        	} else if (rotation != 0){
        		g2.rotate(Math.toRadians(rotation), comboBox.comboBox.x + comboBox.comboBox.width/2, comboBox.comboBox.y + comboBox.comboBox.height/2);
        	}
        	//draw combobox
    		g2.setColor(Color.BLACK);
    		g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[] {1,2},0));		
    		g2.draw(comboBox.comboBox);
    		
    		//draw rotation point
    		g2.setStroke(new BasicStroke(1));
    		g2.draw(comboBox.rotationPoint);
    		
    		if (selectedShapes.size() == 1) {
    			g2.setTransform(old);
    		}
    	}
        
        g2.dispose();
    } 
    
    /** 
     * Opens either a ShapeMenu, or PanelMenu based on the position of the mouse click. 
     * @param e The MouseEvent that triggered the call for a menu.
     * @return void Nothing. 
     */
    public void callMenu(MouseEvent e) {
    	currentEvent = "";
		Boolean isShapeMenu = false;
		
		//check if the point is in shape(s)
		if (comboBox != null) {
			if (comboBox.comboBox.contains(e.getPoint())) {
				shapeMenu.showShapeMenu(e.getComponent(), e.getX(), e.getY(), selectedShapes);
	            isShapeMenu = true;
			}
		}
		
		for (ShapeBuilder shape : shapes) {
			if (shape.javaShape.contains(e.getPoint())) {
				selectedShapes.clear();
				comboBox = null;
				selectedShapes.add(shape);
                shapeMenu.showShapeMenu(e.getComponent(), e.getX(), e.getY(), selectedShapes);
                isShapeMenu = true;
        	}
        }
		
		//otherwise call the panel's menu
		if (!isShapeMenu) {
			panelMenu.showPanelMenu(e.getComponent(), e.getX(), e.getY());
		}
    }
    
    /** 
     * Tracks the first position of the mouse press. 
     * @param e The MouseEvent that called this function.
     * @return void Nothing. 
     */
    public void mousePressed(MouseEvent e) {
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		firstPoint = e.getPoint();
    		
    		//draw a text box using the only the initial point
    		if (currentEvent == "confirmText") {
            	TextBox textBox = new TextBox(f.text.getText(), new Point(firstPoint.x,firstPoint.y), new Point(firstPoint.x+60,firstPoint.y+20));
            	textBoxes.add(textBox);
            	updateDraw(textBox.getProcessingLine());
            	textBox.bounds.stroke = new Color(0,0,0,0);
            	textBox.bounds.fill = new Color(0,0,0,0);
            	shapes.add(textBox.bounds);
    		}
    		
    		//set the first point of a shape and its type
    		if (currentEvent != "" && currentEvent != "confirmText" && currentEvent != "bSelect" ){
    			currentShape = new ShapeBuilder(currentEvent, firstPoint, firstPoint);
    			currentShape.fill = defaultColour;
    			currentShape.stroke = defaultStrokeColour;
    			currentShape.strokeSize = defaultStrokeSize;
    			
    			//seperate different types of shapes with a blank line
    			if (shapes.size() > 0) {
	    			if (currentShape.shapeType != shapes.get(shapes.size()-1).shapeType) {
	    				updateDraw("");
	    			}
    			}
    			
    			shapes.add(currentShape);
    			comboBox = new ComboBox(currentShape.javaShape.getBounds());
    			findSelectedShapes();
    		}
    	}
    }
    
    /** 
     * Tracks the ending position of a mouse drag and adds new shapes to the Processing sketch. 
     * @param e The MouseEvent that called this function.
     * @return void Nothing. 
     */
    public void mouseReleased(MouseEvent e) {
    	Point secondPoint = e.getPoint();
    	
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		//add the shape to the sketch after it is finished
    		if (currentEvent != "" && currentEvent != "confirmText" && currentEvent != "bSelect") {
    			if (currentShape.javaShape.getBounds().width < 5 && currentShape.javaShape.getBounds().height < 5 && !currentShape.processingShape.contains("point")){
    				shapes.remove(currentShape);
    			} else if (currentClass == null && currentAnimation == null) {
    				//add new shape and it's specifications
    				if (shapes.size() > 1) {
	    				if (currentShape.fill != shapes.get(shapes.indexOf(currentShape)-1).fill) {
	        				updateDraw(currentShape.getProcessingFill());
	    				}
	    				if (currentShape.stroke != shapes.get(shapes.indexOf(currentShape)-1).stroke) {
	        				updateDraw(currentShape.getProcessingStroke());
	    				}
	    				if (currentShape.strokeSize != shapes.get(shapes.indexOf(currentShape)-1).strokeSize) {
	        				updateDraw(currentShape.getProcessingStrokeSize());
	    				}
    				}
    				updateDraw(currentShape.processingShape);
    			} else if (currentAnimation == null) {
    				updateClass(currentClass, currentShape.processingShape);
    				currentClass = null;
    			} else {
    				addAnimation(currentShape); 
    				currentAnimation = null;
    			}
    			currentShape = null;
    		}
    		
    		//add final transformation of shape to the sketch
    		if (currentClass == null && currentAnimation == null) { 
	    		if (comboBox == null) {
		    		comboBox = new ComboBox(new Rectangle(Math.min(firstPoint.x,secondPoint.x),Math.min(firstPoint.y,secondPoint.y),Math.abs(firstPoint.x-secondPoint.x),Math.abs(firstPoint.y-secondPoint.y)));
					findSelectedShapes();
					if (selectedShapes.size()==0) {
						comboBox=null;
					}
	    		} else {
					if (comboBox.rotationPoint.contains(firstPoint)) {
						int firstLine = findProcessingShapeLine(selectedShapes.get(0));
						int secondLine = findProcessingShapeLine(selectedShapes.get(selectedShapes.size()-1));
						if (findProcessingLine(firstLine-1).contains("rotate(") && !findProcessingLine(firstLine-1).contains("rotate(-")) {
	    		    		replaceProcessingLine("\t"+selectedShapes.get(0).getProcessingRotate(), firstLine-1);
				    	} else {
				    		insertProcessingLine("", firstLine-1);
				    		insertProcessingLine("\ttranslate("+getWidth()/2+","+getHeight()/2+");", firstLine);
				    		insertProcessingLine("\t"+selectedShapes.get(0).getProcessingRotate(), firstLine+1);
				    	}
						
						if (findProcessingLine(secondLine+1).contains("rotate(-")) {
	    		    		replaceProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+1);
				    	} else {
				    		insertProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+3);
				    		insertProcessingLine("\ttranslate(-"+getWidth()/2+",-"+getHeight()/2+");", secondLine+4);
				    	}
					}
					findSelectedShapes();
				}
    		}
            
            if (!undoStack.contains(Arrays.asList(base.getActiveEditor().getText().split("\n")))) {
            	undoStack.add(new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n"))));
            }
    	}
    	repaint();
    }
    
    /** 
     * Creates a ShapeGroup with the currently selected shapes.
     * @return void Nothing. 
     */
    public void groupShapes() {
    	if (selectedShapes.size() != 0) {
	    	ShapeGroup shapeGroup = new ShapeGroup(selectedShapes, "Group" + String.valueOf(shapeGroups.size()+1));
	    	shapeGroups.add(shapeGroup);
	    	
	    	//replace the Processing shapes with the class call and body
	    	for (ShapeBuilder s: selectedShapes) {
	    		removeProcessingLine("\t" + s.processingShape); 
	    	}
	    	
	    	selectedShapes.clear();
	    	updateDraw(shapeGroup.classCall);
	    	updateCode(shapeGroup.classBody);
    	}
    }
    
    /** 
     * Fins the ShapeGroup a SHapeBuilder object belongs to.. 
     * @param s The ShapeBuilder which might be in a group.
     * @return ShapeGroup The ShapeGroup of the ShapeBuilder if it exists. 
     */
    public ShapeGroup findShapeGroup(ShapeBuilder s) {
    	for (ShapeGroup shapeGroup: shapeGroups) {
    		if (shapeGroup.shapes.contains(s)){
    			return shapeGroup;
    		}
    	}
    	
    	return null;
    }
    
    /** 
     * Moves the given shapes and their combo box together.
     * @param secondPoint The point to move the shapes to.
     * @param shapesToMove The shapes that are to be moved.
     * @param boxToMove The combo box of the shapes that are to be moved.
     * @return void Nothing. 
     */
    public void moveShapes(Point secondPoint, ArrayList<ShapeBuilder> shapesToMove, ComboBox boxToMove) {
    	Point previousPoint = new Point(boxToMove.comboBox.getBounds().x, boxToMove.comboBox.getBounds().y);
    	boxToMove.moveComboBox(secondPoint);
    	Point newPoint = new Point(boxToMove.comboBox.getBounds().x, boxToMove.comboBox.getBounds().y);
    	Point difference = new Point(newPoint.x-previousPoint.x, newPoint.y-previousPoint.y);
    	
    	for (ShapeBuilder shape: shapesToMove) {
    		Point pointToMoveTo = new Point((shape.firstPoint.x+shape.secondPoint.x)/2+difference.x, (shape.firstPoint.y+shape.secondPoint.y)/2+difference.y);
			int lineToUpdate = findProcessingShapeLine(shape);
			String potentialButtonBounds = "\t" + shape.getButtonBounds();
			String textBoxLine = "";
			TextBox movedTextBox = null;
			
			//move textboxes when their connected shapes are being moved
			for(TextBox textBox: textBoxes) {
				if (textBox.bounds == shape) {
					textBoxLine = textBox.getProcessingText();
					movedTextBox = textBox;
				}
			}
			
			shape.moveShape(pointToMoveTo);
			
			//edit lines connected to shape
			if (findShapeGroup(shape) != null) {
				replaceProcessingLine("\t\t"+shape.processingShape, lineToUpdate);
			} else {
				replaceProcessingLine("\t"+shape.processingShape, lineToUpdate);
			}
			
			if (findProcessingLineNumber(potentialButtonBounds) != -1) {
				replaceProcessingLine("\t"+shape.getButtonBounds(), findProcessingLineNumber(potentialButtonBounds));
			}
			
			if (textBoxLine != "") {
				replaceProcessingLine("\t"+movedTextBox.getProcessingText(), findProcessingLineNumber("\t" + textBoxLine));
			}
		}
    }
    
    /** 
     * Find shapes that are selected on screen by checking which are in a combo box
     * @return void Nothing. 
     */
    public void findSelectedShapes() {
    	selectedShapes.clear();
    	
    	for(ShapeBuilder shape: shapes) {
    		Rectangle shapeBounds = shape.javaShape.getBounds();
    		if (comboBox.comboBox.contains(shapeBounds.x,shapeBounds.y,shapeBounds.width,shapeBounds.height)) {
    			selectedShapes.add(shape);
    		}
    	}
    	
    	//redraw the combo box to fit exactly arounf the shape
    	if (selectedShapes.size()==1) {
    		comboBox = new ComboBox(selectedShapes.get(0).javaShape.getBounds());
    	}
    }
    
    /** 
     * Takes the currently selected shapes and creates a combo box around them.
     * @return void Nothing. 
     */
    public void createComboBoxFromSelectedShapes() {
    	int x1 = Integer.MAX_VALUE;
    	int x2 = Integer.MIN_VALUE;
    	int y1 = Integer.MAX_VALUE; 
    	int y2 = Integer.MIN_VALUE;
    	
    	for(ShapeBuilder shape: selectedShapes) {
    		if (shape.javaShape.getBounds().x < x1) {
    			x1 = shape.javaShape.getBounds().x;
    		}
    		
    		if (shape.javaShape.getBounds().y < y1) {
    			y1 = shape.javaShape.getBounds().y;
    		}
    		
    		if (shape.javaShape.getBounds().x > x2) {
    			x2 = shape.javaShape.getBounds().x + shape.javaShape.getBounds().width;
    		}
    		
    		if (shape.javaShape.getBounds().y > y2) {
    			y2 = shape.javaShape.getBounds().y + shape.javaShape.getBounds().height;
    		}
    	}
    	
    	comboBox = new ComboBox(new Rectangle(x1, y1, x2-x1, y2-y1));
    }
    
    /** 
     * Ckecks whether a type of animation currently exists in the Processing sketch. 
     * @param animation The animation type.
     * @return boolean Whether the animation already exists in the editor or not. 
     */
    public boolean doesAnimationExist(String animation) {
    	switch(animation) {
	    	case "bUpAndDown":
	    		if (findProcessingLineNumber("int y = 0;") != -1) {
					return true;
				}
			case "bBackAndForth":
				if (findProcessingLineNumber("int x = 0;") != -1) {
					return true;
				}
			case "bExpandAndContract":
				if (findProcessingLineNumber("int h = 0;") != -1) {
					return true;
				}
				break;
    		default:
    			return false;
    	}
    	
    	return false;
    }
    
    /** 
     * Handles what is supposed to happen when the mouse is clicked in one spot. 
     * @param e The MouseEvent that called this function.
     * @return void Nothing. 
     */
    @Override
	public void mouseClicked(MouseEvent e) {
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		//assume the user wants to select a shape
	    	if (comboBox!=null) {
	    		selectedShapes.clear();
	    		comboBox=null;
	    		repaint();
	    	}
			else {
				for (ShapeBuilder shape: shapes) {
	    			if (shape.containsPoint(e.getPoint())) {
	    				if (currentClass == null && currentAnimation == null && currentAction == null) {
		    				if (findShapeGroup(shape) != null) {
		    					selectedShapes.addAll(findShapeGroup(shape).shapes);
		    					createComboBoxFromSelectedShapes();
		    				} else {
				                comboBox = new ComboBox(shape.javaShape.getBounds());
				                findSelectedShapes();
		    				}
			                repaint();
			                if (fill != null) {
			                	changeFill(fill);
			    	        	selectedShapes.clear();
			    	        	comboBox=null;
			                	fill = null;
			                	repaint();
			                }
	    				} else if (currentAnimation != null){
    						addAnimation(shape);
	    				} else if (currentAction != null){
    						addAction(shape);
	    				} else {
	    					removeProcessingLine("\t"+shape.processingShape);
	    					updateClass(currentClass, shape.processingShape);
	    					currentClass = null;
	    				}
	            	}
	            }
				
				//if the user is not interacting with a shape they might be trying to fill the background
				if (selectedShapes.size() == 0 && fill != null) {
					setBackground(fill);
					updateBackground(fill);
				}
		        
		        if (!undoStack.contains(Arrays.asList(base.getActiveEditor().getText().split("\n")))) {
		        	undoStack.add(new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n"))));
		        }
	        }
    	}
    }
    
    /** 
     * Adds the bounds of a ShapeBuilder to the code to add a button. 
     * @param shape The ShapeBuilder to add an action to.
     * @return void Nothing. 
     */
    public void addAction(ShapeBuilder shape) {
    	Boolean isFunctionClosed = true;
    	
    	//add the mousePressed function to Processing sketch if it's not already there
		if (findProcessingLineNumber("void mousePressed() {") == -1) {
			updateCode("void mousePressed() {");
			isFunctionClosed = false;
		}
		
		//add animation to the shape and make animations stand still until shape is clicked
    	switch(currentAction) {
    		case "bBackAndForth":
    			currentAnimation = currentAction;
    			addAnimation(shape);
    			insertProcessingLine("\t" + shape.getButtonBounds() + "\n\t\txCoefficient = 1;\n\t}", findProcessingLineNumber("void mousePressed() {"));	 
    			replaceProcessingLine("int x = "+ shape.javaShape.getBounds().x +";", findProcessingLineNumber("int x = 0;"));
    			replaceProcessingLine("int xCoefficient = 0;", findProcessingLineNumber("int xCoefficient = 1;"));
    			break;
    		case "bUpAndDown":
    			currentAnimation = currentAction;
    			addAnimation(shape);
    			insertProcessingLine("\t" + shape.getButtonBounds() + "\n\t\tyCoefficient = 1;\n\t}", findProcessingLineNumber("void mousePressed() {"));	 
    			replaceProcessingLine("int y = "+ shape.javaShape.getBounds().y +";", findProcessingLineNumber("int y = 0;"));
    			replaceProcessingLine("int yCoefficient = 0;", findProcessingLineNumber("int yCoefficient = 1;"));
    			break;
    		case "bExpandAndContract":
    			currentAnimation = currentAction;
    			addAnimation(shape);
    			insertProcessingLine("\t" + shape.getButtonBounds() + "\n\t\twhCoefficient = 1;\n\t}", findProcessingLineNumber("void mousePressed() {"));	 
    			replaceProcessingLine("int w = "+ shape.javaShape.getBounds().width +";", findProcessingLineNumber("int w = 0;"));
    			replaceProcessingLine("int h = "+ shape.javaShape.getBounds().height +";", findProcessingLineNumber("int h = 0;"));
    			replaceProcessingLine("int whCoefficient = 0;", findProcessingLineNumber("int whCoefficient = 1;"));
    			break;
    		default:
    			//if the action is not used to trigger an animation, assume it's a link
    			insertProcessingLine("\t" + shape.getButtonBounds() + "\n\t\tlink(\""+currentAction+"\");\n\t}", findProcessingLineNumber("void mousePressed() {"));	 
    	}
    	
    	if (!isFunctionClosed){
			updateCode("}");
		}
    	
    	currentAction = null;
    }
    
    /** 
     * Adds variables, and constants to the code to add an animation. 
     * @param shape The ShapeBuilder to add an animation to.
     * @return void Nothing. 
     */
    public void addAnimation(ShapeBuilder shape) {
    	int lineNumber = findProcessingLineNumber("\t"+shape.processingShape)-1;
    	
    	//remove the old Processing shape
    	if (lineNumber < 0 ) {
    		lineNumber = findProcessingLineNumber("\t\t"+shape.processingShape)-1;
    		removeProcessingLine("\t\t"+shape.processingShape);
    	} else {
    		removeProcessingLine("\t"+shape.processingShape);
    	}
    	
    	//and initialse the new one
		String line = "";
		
		//and its animation
		if (currentAnimation == "bBackAndForth") {
			line = shape.processingShape.split("\\(")[0] + "(x," + shape.processingShape.split(",",2)[1];
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint x = 0;\nint xCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif (x == 400 || x == -1){\n\t\txCoefficient = -xCoefficient;\n\t}\n\tx += xCoefficient;");
			}
		} else if (currentAnimation == "bUpAndDown") {
			if (shape.shapeType != "point") {
				line = shape.processingShape.split(",")[0] + ", y, " + shape.processingShape.split(",", 3)[2];
			} else {
				line = shape.processingShape.split(",")[0] + ", y);";
			}
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint y = 0;\nint yCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif (y == 400 || y == -1){\n\t\tyCoefficient = -yCoefficient;\n\t}\n\ty += yCoefficient;");
			}
		} else {
			if (shape.shapeType != "point" && shape.shapeType != "triangle" && shape.shapeType != "quad" && shape.shapeType != "arc") {
				line = shape.processingShape.split(",")[0] + "," + shape.processingShape.split(",")[1] + ", w, h);";
			} else if (shape.shapeType != "point") {
				line = shape.processingShape.split(",")[0] + "," + shape.processingShape.split(",")[1] + ", w, h," + shape.processingShape.split(",",5)[4];
			}
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint w = 0;\nint h = 0;\nint whCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif ((h == 200 || h == -200) || h == 0){\n\t\twhCoefficient = -whCoefficient;\n\t}\n\th += whCoefficient;\n\tw += whCoefficient;");
			}
		}
		shape.processingShape = line;
		
		currentAnimation = null;
    }
    
    /** 
     * Update the Processing sketch to have a new background colour. 
     * @param c The Color to paint the background.
     * @return void Nothing. 
     */
    public void updateBackground(Color c) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	base.getActiveEditor().setText(editorLines.get(0));
    	
    	for (int i = 1; i < editorLines.size(); i++) {
    		if (editorLines.get(i).contains("background(")) {
    			updateCode("\tbackground(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");");
    		} else {
    			updateCode(editorLines.get(i));	
    		}
    	}
    }
    
    /** 
     * Changes the current default fill to a new colour. 
     * @param c The Color to change the fill to.
     * @return void Nothing. 
     */
    public void changeFill(Color c) {
    	//add the new colour to the selected shape(s)
	    if (selectedShapes.size() != 0) {
	    	for (ShapeBuilder selectedShape: selectedShapes) {
		    	int position = findProcessingShapeLine(selectedShape)-1;
		    	selectedShape.fill = c;
		    	if (findProcessingLine(position).contains("fill(")) {
		    		replaceProcessingLine("\t"+selectedShape.getProcessingFill(), position);
		    	} else {
		    		insertProcessingLine("\t"+selectedShape.getProcessingFill(), position);
		    	}
		    	
		    	if (shapes.indexOf(selectedShape)+1 < shapes.size()) {
			    	ShapeBuilder nextShape = shapes.get(shapes.indexOf(selectedShape)+1);
			    	int nextPosition = findProcessingShapeLine(nextShape)-1;
			    	if (findProcessingLine(nextPosition).contains("fill(")) {
			    		replaceProcessingLine("\t"+nextShape.getProcessingFill(), nextPosition);
			    	} else {
			    		insertProcessingLine("\t"+nextShape.getProcessingFill(), nextPosition);
			    	}
		    	}
	    	}
    	} else {
    		//change the default colour if no shapes are selected
	    	if (shapes.size() != 0) {
	    		shapes.get(shapes.size()-1).fill = c;
	    	}
	    	defaultColour = c;
	    	updateDraw("fill(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");");
	    }
	    
    }
	
    /** 
     * Function of the MouseEvent library. 
     * @param e The MouseEvent calling the function.
     * @return void Nothing. 
     */
	@Override
    public void mouseEntered(MouseEvent e) {
    }
	
	/** 
     * Function of the MouseEvent library. 
     * @param e The MouseEvent calling the function.
     * @return void Nothing. 
     */
	@Override
	public void mouseExited(MouseEvent e) {
    }
	
	/** 
     * Animates the creation and editing of shapes. 
     * @param e The MouseEvent calling the function.
     * @return void Nothing. 
     */
	@Override
	public void mouseDragged(MouseEvent e) {
		Point secondPoint = e.getPoint();
		
		//calculate angle from two points: https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
		float angle = (float) Math.toDegrees(Math.atan2(secondPoint.y - firstPoint.y, secondPoint.x - firstPoint.x));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    //calculate the mouse cursor based on the angle between the first and second point 
	    //this animates the creation of a shape in the correct direction
		if (currentEvent != "" && currentEvent != "confirmText" && currentEvent != "bSelect") {
			if ((angle > 0 && angle < 22.5) || (angle<360 && angle>337.5)) {
				currentShape.stretchShape(secondPoint, Cursor.N_RESIZE_CURSOR);
			} else if (angle > 22.5 && angle < 67.5) {
				currentShape.stretchShape(secondPoint, Cursor.NE_RESIZE_CURSOR);
			} else if (angle > 67.5 && angle < 112.5) {
				currentShape.stretchShape(secondPoint, Cursor.E_RESIZE_CURSOR);
			} else if (angle > 112.5 && angle < 157.5) {
				currentShape.stretchShape(secondPoint, Cursor.SE_RESIZE_CURSOR);
			} else if (angle > 157.5 && angle < 202.5) {
				currentShape.stretchShape(secondPoint, Cursor.S_RESIZE_CURSOR);
			} else if (angle > 202.5 && angle < 247.5) {
				currentShape.stretchShape(secondPoint, Cursor.SW_RESIZE_CURSOR);
			} else if (angle > 247.5 && angle < 292.5) {
				currentShape.stretchShape(secondPoint, Cursor.W_RESIZE_CURSOR);
			} else if (angle > 292.5 && angle < 337.5) {
				currentShape.stretchShape(secondPoint, Cursor.NW_RESIZE_CURSOR);
			}
			comboBox = new ComboBox(currentShape.javaShape.getBounds());
		}
		
		if(currentEvent == "" || currentEvent == "bSelect") {
			if (comboBox != null) {
				if (comboBox.rotationPoint.contains(firstPoint)) {
					for (ShapeBuilder shape: selectedShapes) {
						shape.rotation = (int)angle;
					}
				} else if (cursorDirection == 0 && comboBox.comboBox.contains(firstPoint)) {
				 	moveShapes(secondPoint,selectedShapes,comboBox);
				} else if (cursorDirection != 0){
					//stretches selected shapes
					Point previousDimensions = new Point(comboBox.comboBox.getBounds().width, comboBox.comboBox.getBounds().height);
					Point previousPoint = new Point(comboBox.comboBox.getBounds().x, comboBox.comboBox.getBounds().y);
					comboBox.stretchComboBox(secondPoint,cursorDirection);
					Point newDimensions = new Point(comboBox.comboBox.getBounds().width, comboBox.comboBox.getBounds().height);
					Point newPoint = new Point(comboBox.comboBox.getBounds().x, comboBox.comboBox.getBounds().y);
					Point difference = new Point(newDimensions.x-previousDimensions.x, newDimensions.y-previousDimensions.y);
					
					for (ShapeBuilder shape: selectedShapes) {
						if (shape.fill != new Color(0,0,0,0)) {
							int lineToUpdate = findProcessingShapeLine(shape);
							String potentialButtonBounds = "\t" + shape.getButtonBounds();
							
							if (selectedShapes.size()>1) {
								int stretchX;
								int stretchY;
								if (previousPoint.x != newPoint.x) {
									stretchX = shape.firstPoint.x + difference.x;
								} else {
									stretchX = shape.secondPoint.x + difference.x;
								}
								
								if (previousPoint.y != newPoint.y) {
									stretchY = shape.firstPoint.y - difference.y;
								} else {
									stretchY = shape.secondPoint.y - difference.y;
								}
								shape.stretchShape(new Point(stretchX, stretchY), cursorDirection);
							} else {
								shape.stretchShape(secondPoint,cursorDirection);
							}
							replaceProcessingLine("\t"+shape.processingShape, lineToUpdate);
							
							if (findProcessingLineNumber(potentialButtonBounds) != -1) {
								replaceProcessingLine("\t"+shape.getButtonBounds(), findProcessingLineNumber(potentialButtonBounds));
							}
						}
					}
				} 
			} 
		}
		repaint();
    }
	
	/** 
     * Calculates the mouse cursor based on the mouse position relative to combo boxes. 
     * @param e The MouseEvent calling the function.
     * @return void Nothing. 
     */
	@Override
	public void mouseMoved(MouseEvent e) {
        if (comboBox != null) {
        	if (comboBox.comboBox.contains(e.getPoint())) {
        		setCursor(new Cursor(Cursor.MOVE_CURSOR));
        		cursorDirection = 0;
        	} else {
	        	
	        	int sensitivity = 5;
	        	
	        	double x1 = comboBox.comboBox.getX();
	        	double y1 = comboBox.comboBox.getY();
	        	double x2 = comboBox.comboBox.getX() + comboBox.comboBox.getWidth();
	        	double y2 = comboBox.comboBox.getY() + comboBox.comboBox.getHeight();
	        	
	        	if (Math.abs(e.getPoint().x - x1) <= sensitivity) {
	        		if (Math.abs(e.getPoint().y - y1) <= sensitivity) {
	        			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
	            	} else if (Math.abs(e.getPoint().y - y2) <= sensitivity) {
	            		setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
	            	} else {
	            		setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
	            	}
	        		cursorDirection = getCursor().getType();
	        	} else if (Math.abs(e.getPoint().x - x2) <= sensitivity) {
	        		if (Math.abs(e.getPoint().y - y1) <= sensitivity) {
	        			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
	            	} else if (Math.abs(e.getPoint().y - y2) <= sensitivity) {
	            		setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
	            	} else {
	            		setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
	            	}
	        		cursorDirection = getCursor().getType();
	        	} else if (e.getPoint().y == y1) {
	        		setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
	        		cursorDirection = getCursor().getType();
	        	} else if (e.getPoint().y == y2) {
	        		setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
	        		cursorDirection = getCursor().getType();
	        	} else {
	        		setCursor(cursor);
	        	}
        			
        	}
        }
    }
	
	//processing helper functions
    
	/** 
     * Adds a new line to the Processing editor. 
     * @param newText The text to add.
     * @return void Nothing. 
     */
    public void updateCode(String newText) {
    	//set the text of the editor to the current text plus a new line
		base.getActiveEditor().setText(base.getActiveEditor().getText() + "\n" + newText);
	}
    
    /** 
     * Adds a new line to the draw function. 
     * @param newText The text to add.
     * @return void Nothing. 
     */
    public void updateDraw(String newText) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	
    	base.getActiveEditor().setText(editorLines.get(0));
		
		boolean isInDraw = false;

		for (int i = 1; i < editorLines.size()-1; i++) {
			updateCode(editorLines.get(i));
			
			if (editorLines.get(i).toLowerCase().contains("void draw() {")){
				isInDraw = true;
			}
			
			if ((editorLines.get(i+1).contains("}") || editorLines.get(i+1).contains("if")) && isInDraw){
				updateCode("\t" + newText);
				isInDraw = false;
			}
		}
		
		updateCode(editorLines.get(editorLines.size()-1));
	}
    
    /** 
     * Adds the lines for rotating a shape by an angle in Processing. 
     * @param angleInDegrees The angle in degerees.
     * @return void Nothing. 
     */
    public void rotateCanvas(int angleInDegrees) {
		int position = findProcessingLineNumber("void draw() {");
		if (findProcessingLine(position+1).contains("translate("+getWidth()/2+","+getHeight()/2+");")){
    		replaceProcessingLine("\trotate("+Math.toRadians(angleInDegrees)+");", position+2);
    	} else {
    		insertProcessingLine("\ttranslate("+getWidth()/2+","+getHeight()/2+");", position);
    		insertProcessingLine("\trotate("+Math.toRadians(angleInDegrees)+");", position+1);
    		insertProcessingLine("\ttranslate(-"+getWidth()/2+",-"+getHeight()/2+");", position+2);
    	}
	}
    
    /** 
     * Checks what line number in the Processing editor a shape has. 
     * @param s The ShapeBuilder which line is being searched for.
     * @return int The line number. 
     */
    public int findProcessingShapeLine(ShapeBuilder s) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	if (editorLines.contains("\t\t"+s.processingShape)) {
    		return editorLines.indexOf("\t\t"+s.processingShape);
    	}
    	return editorLines.indexOf("\t"+s.processingShape);
	}
    
    /** 
     * Finds the line on the position n in the Processing editor.
     * @param n The line number being searched for.
     * @return String The line. 
     */
    public String findProcessingLine(int n) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.get(n);
	}
    
    /** 
     * Finds the line number of a line in the Processing editor. 
     * @param newText The line being searched for.
     * @return int THe line number. 
     */
    public int findProcessingLineNumber(String line) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.indexOf(line);
	}
    
    /** 
     * Checks whether a line exists in the Processing editor. 
     * @param newText The liner.
     * @return Boolean Whether the line exists or not. 
     */
    public Boolean doesProcessingLineExists(String line) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.contains(line);
	}
	
    /** 
     * Inserts a line in the Processing editor at the line number. 
     * @param newLine The text to be inserted.
     * @param position The position to insert the line on.
     * @return void Nothing. 
     */
	public void insertProcessingLine(String newLine, int position) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		base.getActiveEditor().setText(editorLines.get(0));
		if (position < editorLines.size()) {
			for (int i = 1; i <= position; i++) {
				updateCode(editorLines.get(i));
			}
			updateCode(newLine);
			for (int j = position + 1; j < editorLines.size(); j++) {
				updateCode(editorLines.get(j));
			}
		}
		else {
			for (int i = 1; i <= editorLines.size()-1; i++) {
				updateCode(editorLines.get(i));
			}
			updateCode(newLine);
		}
	}
	
	/** 
     * Replaces the line at the position in the Processing editor with a new one. 
     * @param newLine The line to replace the previous line with.
     * @param The position of the old line.
     * @return void Nothing. 
     */
	public void replaceProcessingLine(String newLine, int position) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		if (position != 0) {
			base.getActiveEditor().setText(editorLines.get(0));
		} else {
			base.getActiveEditor().setText(newLine);
		}
		

		for (int i = 1; i < editorLines.size(); i++) {
			if (position == i) {
				updateCode(newLine);
			} else {
				updateCode(editorLines.get(i));
			}
		}
	}
	
	/** 
     * Removes a line in the Processing editor. 
     * @param lineToRemove The line to be removed.
     * @return void Nothing. 
     */
	public void removeProcessingLine(String lineToRemove) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		editorLines.remove(lineToRemove);
		
		base.getActiveEditor().setText(editorLines.get(0));

		for (int i = 1; i < editorLines.size(); i++) {
			updateCode(editorLines.get(i));
		}
	}
	
	/** 
     * Removes a line of the Processing editor based on its line number.
     * @param lineToRemove The number of the line to be removed.
     * @return void Nothing. 
     */
	public void removeProcessingLineByNumber(int lineToRemove) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		editorLines.remove(lineToRemove);
		
		base.getActiveEditor().setText(editorLines.get(0));

		for (int i = 1; i < editorLines.size(); i++) {
			updateCode(editorLines.get(i));
		}
	}
	
	/** 
     * Removed the lines asscoiated with a certain class from the Processing editor. 
     * @param className The name of the class.
     * @return void Nothing. 
     */
	public void removeClass(String className) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		base.getActiveEditor().setText(editorLines.get(0));
		
		boolean isInClass = false;

		for (int i = 1; i < editorLines.size(); i++) {
			if (editorLines.get(i).toLowerCase().contains(className)){
				isInClass = true;
			}
			
			if (editorLines.get(i) == "}"){
				isInClass = false;
			}
			
			if (!isInClass) {
				updateCode(editorLines.get(i));
			} 
		}
	}
	
	/** 
     * Adds a new line to a class in the Processing editor. 
     * @param className The name of the class to add the line to.
     * @param newLine The new line.
     * @return void Nothing. 
     */
	public void updateClass(String className, String newLine) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		base.getActiveEditor().setText(editorLines.get(0));
		
		boolean isInClass = false;

		for (int i = 1; i < editorLines.size(); i++) {
			if (editorLines.get(i).contains(className+"{")){
				isInClass = true;
			}
			
			if (isInClass){
				if (editorLines.get(i+1).contains("}")) {
					updateCode("\t\t"+newLine);
					isInClass = false;
				}
			}
			
			updateCode(editorLines.get(i));
		}
	}
}
