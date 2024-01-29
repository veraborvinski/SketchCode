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
	public ArrayList<ShapeBuilder> shapeHistory = new ArrayList<ShapeBuilder>();
	
	Map<String, int[]> textBoxes = new HashMap<String, int[]>();
	
	String currentEvent = "";
	Point firstPoint = new Point(0,0);
	Base base;
	
	ShapeBuilder currentShape;
	ArrayList<ShapeBuilder> copiedShapes = new ArrayList<ShapeBuilder>();
	ComboBox comboBox = null;
	ArrayList<ShapeBuilder> selectedShapes = new ArrayList<ShapeBuilder>();
	
	Color fill;
	Color defaultColour = Color.WHITE;
	Color defaultStrokeColour = Color.BLACK;
	int defaultStrokeSize = 1;
	
	int cursorDirection;
	Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	PanelMenu panelMenu = new PanelMenu(this);
	ShapeMenu shapeMenu = new ShapeMenu(this);
	GUIFrame f;

    public GUIPanel(int height, int width, GUIFrame initF) {
    	f = initF;
    	setPreferredSize(new Dimension(height, width));
    	addMouseListener(this); 
    	addMouseMotionListener(this);
    }
    
    public void paintComponent(Graphics g) {     
        super.paintComponent(g);       
        Graphics2D g2 = (Graphics2D) g;
        
        for (ShapeBuilder shape: shapes){
        	AffineTransform old = g2.getTransform();
        	Rectangle shapeBounds = shape.javaShape.getBounds();
        	g2.rotate(Math.toRadians(shape.rotation), shapeBounds.x + shapeBounds.width/2, shapeBounds.y + shapeBounds.height/2);
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
        		g2.rotate(Math.toRadians(selectedShapes.get(0).rotation), comboBox.comboBox.x + comboBox.comboBox.width/2, comboBox.comboBox.y + comboBox.comboBox.height/2);
        	}
        	//draw comboobox
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
            	updateDraw("text(Hello World, " + firstPoint.x + ", " + firstPoint.y +");");
    		}
    		if (currentEvent != "" && currentEvent != "bText") {
    			currentShape = new ShapeBuilder(currentEvent, firstPoint, firstPoint);
    			currentShape.fill = defaultColour;
    			currentShape.stroke = defaultStrokeColour;
    			currentShape.strokeSize = defaultStrokeSize;
    			shapes.add(currentShape);
    			comboBox = new ComboBox(currentShape.javaShape.getBounds());
    			findSelectedShapes();
    		}
    		if (comboBox == null && currentEvent=="") {
				for (ShapeBuilder shape: shapes) {
					if (shape.javaShape.contains(e.getPoint())) {
		                comboBox = new ComboBox(shape.javaShape.getBounds());
		                findSelectedShapes();
		                repaint();
		        	}
		        }
			} 
    	}
    }
    
    public void mouseReleased(MouseEvent e) {
    	Point secondPoint = e.getPoint();
    	
    	if (e.isPopupTrigger()) {
    		callMenu(e);
    	} else {
    		if (currentEvent != "" && currentEvent != "bText") {
    			updateDraw(currentShape.processingShape);
    			currentShape = null;
    		} 
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
			    		insertProcessingLine("\t"+selectedShapes.get(0).getProcessingRotate(), firstLine-1);
			    	}
					if (findProcessingLine(secondLine+1).contains("rotate(-")) {
    		    		replaceProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+1);
			    	} else {
			    		insertProcessingLine("\t"+selectedShapes.get(selectedShapes.size()-1).getReverseProcessingRotate(), secondLine+1);
			    	}
				}
			}
    	}
    }
    
    public void moveShape(Point secondPoint, ArrayList<ShapeBuilder> shapesToMove, ComboBox boxToMove) {
    	for (ShapeBuilder shape: shapesToMove) {
			int lineToUpdate = findProcessingShapeLine(shape);
			shape.moveShape(secondPoint);
			replaceProcessingLine("\t"+shape.processingShape, lineToUpdate);
		}
    	boxToMove.moveComboBox(secondPoint);
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
		                selectedShapes.add(shape);
		                comboBox = new ComboBox(shape.javaShape.getBounds());
		                repaint();
		                if (fill != null) {
		                	changeFill(fill);
		    	        	selectedShapes.clear();
		    	        	comboBox=null;
		                	fill = null;
		                	repaint();
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
			    	if (findProcessingLine(position).contains("fill(")) {
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
	    
		if (currentEvent != "" && currentEvent != "bText") {
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
			findSelectedShapes();
		}
		
		if(currentEvent == "") {
			if (comboBox != null) {
				if (comboBox.rotationPoint.contains(firstPoint)) {
					for (ShapeBuilder shape: selectedShapes) {
						shape.rotation = (int)angle;
					}
				} else if (cursorDirection == 0 && comboBox.comboBox.contains(firstPoint)) {
				 	moveShape(secondPoint,selectedShapes,comboBox);
				} else if (comboBox.comboBox.contains(firstPoint)){
					comboBox.stretchComboBox(secondPoint,cursorDirection);
					for (ShapeBuilder shape: selectedShapes) {
						int lineToUpdate = findProcessingShapeLine(shape);
						shape.stretchShape(secondPoint,cursorDirection);
						replaceProcessingLine("\t"+shape.processingShape, lineToUpdate);
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
    
    public void updateCode(String newText) {
		base.getActiveEditor().setText(base.getActiveEditor().getText() + "\n" + newText);
	}
    
    public void updateDraw(String newText) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	insertProcessingLine("\t"+newText, editorLines.size()-2);
	}
    
    public int findProcessingShapeLine(ShapeBuilder s) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.indexOf("\t"+s.processingShape);
	}
    
    public String findProcessingLine(int n) {
    	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	return editorLines.get(n);
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
}
