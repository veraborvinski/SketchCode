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

@SuppressWarnings("serial")
class GUIPanel extends JPanel implements MouseListener, MouseMotionListener{
	
	public ArrayList<ShapeBuilder> shapes = new ArrayList<ShapeBuilder>();
	public ArrayList<ArrayList<String>> undoStack = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<String>> redoStack = new ArrayList<ArrayList<String>>();
	
	Map<String, int[]> textBoxes = new HashMap<String, int[]>();
	
	String currentEvent = "";
	Point firstPoint = new Point(0,0);
	Base base;
	
	Boolean isFlippedHorizontal = false;
	Boolean isFlippedVertical = false;
	int rotation = 0;
	int zoom = 0;
	
	ShapeBuilder currentShape;
	ArrayList<ShapeBuilder> copiedShapes = new ArrayList<ShapeBuilder>();
	ComboBox comboBox = null;
	ArrayList<ShapeBuilder> selectedShapes = new ArrayList<ShapeBuilder>();
	ArrayList<ShapeGroup> shapeGroups = new ArrayList<ShapeGroup>();
	
	Color fill;
	Color defaultColour = Color.WHITE;
	Color defaultStrokeColour = Color.BLACK;
	int defaultStrokeSize = 1;
	
	int cursorDirection;
	Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	PanelMenu panelMenu = new PanelMenu(this);
	ShapeMenu shapeMenu = new ShapeMenu(this);
	GUIFrame f;
	
	String currentClass = null;
	String currentAnimation = null;
	String currentAction = null;
	Boolean animationAsAction = false;

    public GUIPanel(int height, int width, GUIFrame initF) {
    	f = initF;
    	setPreferredSize(new Dimension(height, width));
    	addMouseListener(this); 
    	addMouseMotionListener(this);
    }
    
    public void paintComponent(Graphics g) {     
        super.paintComponent(g);   
        Graphics2D g2 = (Graphics2D) g;
        
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
        
        for (ShapeBuilder shape: shapes){
        	AffineTransform old = g2.getTransform();
        	Rectangle shapeBounds = shape.javaShape.getBounds();
        	g2.rotate(Math.toRadians((shape.rotation+rotation)%360), shapeBounds.x + shapeBounds.width/2, shapeBounds.y + shapeBounds.height/2);      	
        	g2.setColor(shape.fill);
        	g2.fill(shape.javaShape);
			g2.setColor(shape.stroke);	
			g2.setStroke(new BasicStroke(shape.strokeSize));			
        	g2.draw(shape.javaShape);
        	g2.setTransform(old);
        }
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
        
        for(Map.Entry<String, int[]> entry : textBoxes.entrySet()) {
        	g2.drawString(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
        
        g2.dispose();
        
        if (!undoStack.contains(Arrays.asList(base.getActiveEditor().getText().split("\n")))) {
        	undoStack.add(new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n"))));
        }
    } 
    
    public void callMenu(MouseEvent e) {
    	currentEvent = "";
		Boolean isShapeMenu = false;
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
		if (!isShapeMenu) {
			panelMenu.showPanelMenu(e.getComponent(), e.getX(), e.getY());
		}
    }
    
    public void mousePressed(MouseEvent e) {
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		firstPoint = e.getPoint();
    		if (currentEvent == "bText") {
            	int[] coords = {firstPoint.x, firstPoint.y};
            	textBoxes.put("Hello World", coords);
            	updateDraw("text(\"Hello World\", " + firstPoint.x + ", " + firstPoint.y +");");
    		}
    		if (currentEvent != "" && currentEvent != "bText" && currentEvent != "bSelect" && currentClass == null) {
    			currentShape = new ShapeBuilder(currentEvent, firstPoint, firstPoint);
    			currentShape.fill = defaultColour;
    			currentShape.stroke = defaultStrokeColour;
    			currentShape.strokeSize = defaultStrokeSize;
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
    
    public void mouseReleased(MouseEvent e) {
    	Point secondPoint = e.getPoint();
    	
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		if (currentEvent != "" && currentEvent != "bText" && currentEvent != "bSelect") {
    			if (currentClass == null) {
    				updateDraw(currentShape.processingShape);
    			} else if (currentAnimation == null) {
    				addAnimation(currentShape); 
    			} else {
    				updateClass(currentClass, currentShape.processingShape);
    				currentClass = null;
    			}
    			currentShape = null;
    		}
    		
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
				    		insertProcessingLine("\ttranslate("+getWidth()/2+","+getHeight()/2+");", firstLine-1);
				    		insertProcessingLine("\t"+selectedShapes.get(0).getProcessingRotate(), firstLine);
				    	}
						
						if (findProcessingLine(secondLine+1).contains("rotate(-")) {
	    		    		replaceProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+1);
				    	} else {
				    		insertProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+2);
				    		insertProcessingLine("\ttranslate(-"+getWidth()/2+",-"+getHeight()/2+");", secondLine+3);
				    	}
					}
					findSelectedShapes();
				}
    		}
    	}
    	repaint();
    }
    
    public void groupShapes() {
    	findSelectedShapes();
    	ShapeGroup shapeGroup = new ShapeGroup(selectedShapes, "Group" + String.valueOf(shapeGroups.size()+1));
    	shapeGroups.add(shapeGroup);
    	for (ShapeBuilder s: selectedShapes) {
    		removeProcessingLine("\t" + s.processingShape); 
    	}
    	selectedShapes.clear();
    	updateDraw(shapeGroup.classCall);
    	updateCode(shapeGroup.classBody);
    }
    
    public ShapeGroup findShapeGroup(ShapeBuilder s) {
    	for (ShapeGroup shapeGroup: shapeGroups) {
    		if (shapeGroup.shapes.contains(s)){
    			return shapeGroup;
    		}
    	}
    	return null;
    }
    
    public void moveShapes(Point secondPoint, ArrayList<ShapeBuilder> shapesToMove, ComboBox boxToMove) {
    	Point previousPoint = new Point(boxToMove.comboBox.getBounds().x, boxToMove.comboBox.getBounds().y);
    	boxToMove.moveComboBox(secondPoint);
    	Point newPoint = new Point(boxToMove.comboBox.getBounds().x, boxToMove.comboBox.getBounds().y);
    	Point difference = new Point(newPoint.x-previousPoint.x, newPoint.y-previousPoint.y);
    	for (ShapeBuilder shape: shapesToMove) {
    		Point pointToMoveTo = new Point((shape.firstPoint.x+shape.secondPoint.x)/2+difference.x, (shape.firstPoint.y+shape.secondPoint.y)/2+difference.y);
			int lineToUpdate = findProcessingShapeLine(shape);
			String potentialButtonBounds = "\t" + shape.getButtonBounds();
			
			shape.moveShape(pointToMoveTo);
			
			if (findShapeGroup(shape) != null) {
				replaceProcessingLine("\t\t"+shape.processingShape, lineToUpdate);
			} else {
				replaceProcessingLine("\t"+shape.processingShape, lineToUpdate);
			}
			
			if (findProcessingLineNumber(potentialButtonBounds) != -1) {
				replaceProcessingLine("\t"+shape.getButtonBounds(), findProcessingLineNumber(potentialButtonBounds));
			}
		}
    }
    
    public void findSelectedShapes() {
    	selectedShapes.clear();
    	for(ShapeBuilder shape: shapes) {
    		Rectangle shapeBounds = shape.javaShape.getBounds();
    		if (comboBox.comboBox.contains(shapeBounds.x,shapeBounds.y,shapeBounds.width,shapeBounds.height)) {
    			selectedShapes.add(shape);
    		}
    	}
    	if (selectedShapes.size()==1) {
    		comboBox = new ComboBox(selectedShapes.get(0).javaShape.getBounds());
    	}
    }
    
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
    
    @Override
	public void mouseClicked(MouseEvent e) {
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
	    	if (comboBox!=null) {
	    		selectedShapes.clear();
	    		comboBox=null;
	    		repaint();
	    	}
			else {
				for (ShapeBuilder shape: shapes) {
	    			if (shape.javaShape.contains(e.getPoint())) {
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
				if (selectedShapes.size() == 0 && fill != null) {
					setBackground(fill);
					updateBackground(fill);
				}
	        }
    	}
    }
    
    public void addAction(ShapeBuilder shape) {
    	Boolean isFunctionClosed = true;
		if (findProcessingLineNumber("void mousePressed() {") == -1) {
			updateCode("void mousePressed() {");
			isFunctionClosed = false;
		}
		
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
    			insertProcessingLine("\t" + shape.getButtonBounds() + "\n\t\tlink(\""+currentAction+"\");\n\t}", findProcessingLineNumber("void mousePressed() {"));	 
    	}
    	
    	if (!isFunctionClosed){
			updateCode("}");
		}
    	
    	currentAction = null;
    }
    
    public void addAnimation(ShapeBuilder shape) {
    	int lineNumber = findProcessingLineNumber("\t"+shape.processingShape)-1;
		removeProcessingLine("\t"+shape.processingShape);
		String line;
		
		if (currentAnimation == "bBackAndForth") {
			line = shape.processingShape.split("\\(")[0] + "(x," + shape.processingShape.split(",",2)[1];
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint x = 0;\nint xCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif (x == 400 || x == -1){\n\t\txCoefficient = -xCoefficient;\n\t}\n\tx += xCoefficient;");
			}
		} else if (currentAnimation == "bUpAndDown") {
			line = shape.processingShape.split(",")[0] + ", y, " + shape.processingShape.split(",", 3)[2];
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint y = 0;\nint yCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif (y == 400 || y == -1){\n\t\tyCoefficient = -yCoefficient;\n\t}\n\ty += yCoefficient;");
			}
		} else {
			line = shape.processingShape.split(",")[0] + "," + shape.processingShape.split(",")[1] + ", w, h);";
			
			insertProcessingLine("\t"+line, lineNumber);
			if (!doesAnimationExist(currentAnimation)) {
				insertProcessingLine("\nint w = 0;\nint h = 0;\nint whCoefficient = 1;\n",findProcessingLineNumber("void draw() {")-1);
				insertProcessingLine("\tbackground(255);\n",findProcessingLineNumber("void draw() {"));
				updateDraw("\n\tif ((h == 200 || h == -200) || h == 0){\n\t\twhCoefficient = -whCoefficient;\n\t}\n\th += whCoefficient;\n\tw += whCoefficient;");
			}
		}
		
		currentAnimation = null;
    }
    
    public void updateBackground(Color c) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	base.getActiveEditor().setText(editorLines.get(0));
    	for (int i = 1; i < editorLines.size(); i++) {
    		if (editorLines.get(i).contains("background(")) {
    			updateDraw("background(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");");
    		}
    		else {
    			updateDraw(editorLines.get(i));	
    		}
    	}
    }
    
    public void changeFill(Color c) {
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
    	}
	    else {
	    	if (shapes.size() != 0) {
	    		shapes.get(shapes.size()-1).fill = c;
	    	}
	    	defaultColour = c;
	    	updateDraw("\tfill(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");");
	    }
	    
    }
	
	@Override
    public void mouseEntered(MouseEvent e) {
    }
	
	@Override
	public void mouseExited(MouseEvent e) {
    }
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point secondPoint = e.getPoint();
		
		//calculate angle from two points: https://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
		float angle = (float) Math.toDegrees(Math.atan2(secondPoint.y - firstPoint.y, secondPoint.x - firstPoint.x));

	    if(angle < 0){
	        angle += 360;
	    }
	    
		if (currentEvent != "" && currentEvent != "bText" && currentEvent != "bSelect") {
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
					Point previousDimensions = new Point(comboBox.comboBox.getBounds().width, comboBox.comboBox.getBounds().height);
					Point previousPoint = new Point(comboBox.comboBox.getBounds().x, comboBox.comboBox.getBounds().y);
					comboBox.stretchComboBox(secondPoint,cursorDirection);
					Point newDimensions = new Point(comboBox.comboBox.getBounds().width, comboBox.comboBox.getBounds().height);
					Point newPoint = new Point(comboBox.comboBox.getBounds().x, comboBox.comboBox.getBounds().y);
					Point difference = new Point(newDimensions.x-previousDimensions.x, newDimensions.y-previousDimensions.y);
					
					for (ShapeBuilder shape: selectedShapes) {
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
		repaint();
    }
	
	
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
    
    public void updateCode(String newText) {
		base.getActiveEditor().setText(base.getActiveEditor().getText() + "\n" + newText);
	}
    
    public void updateDraw(String newText) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	
    	base.getActiveEditor().setText(editorLines.get(0));
		
		boolean isInDraw = false;

		for (int i = 1; i < editorLines.size()-1; i++) {
			updateCode(editorLines.get(i));
			
			if (editorLines.get(i).toLowerCase().contains("void draw() {")){
				isInDraw = true;
			}
			
			if (editorLines.get(i+1).contains("}") && isInDraw){
				updateCode("\t" + newText);
				isInDraw = false;
			}
		}
		
		updateCode(editorLines.get(editorLines.size()-1));
	}
    
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
 
    public int findProcessingShapeLine(ShapeBuilder s) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	if (editorLines.contains("\t\t"+s.processingShape)) {
    		return editorLines.indexOf("\t\t"+s.processingShape);
    	}
    	return editorLines.indexOf("\t"+s.processingShape);
	}
    
    public String findProcessingLine(int n) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.get(n);
	}
    
    public int findProcessingLineNumber(String line) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.indexOf(line);
	}
    
    public Boolean doesProcessingLineExists(String line) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.contains(line);
	}
	
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
	
	public void removeProcessingLine(String lineToRemove) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		editorLines.remove(lineToRemove);
		
		base.getActiveEditor().setText(editorLines.get(0));

		for (int i = 1; i < editorLines.size(); i++) {
			updateCode(editorLines.get(i));
		}
	}
	
	public void removeProcessingLineByNumber(int lineToRemove) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		editorLines.remove(lineToRemove);
		
		base.getActiveEditor().setText(editorLines.get(0));

		for (int i = 1; i < editorLines.size(); i++) {
			updateCode(editorLines.get(i));
		}
	}
	
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
