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
import java.net.URI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

import javax.imageio.*;

import java.awt.event.KeyAdapter;

import java.awt.Cursor;

import processing.app.Base;

/** 
* The GUIFrame class creates the main JFrame of the tool.
* This class was based on the GUIFrame class from https://github.com/joelmoniz/Shape-Sketch
* 
* @author Vera Borvinski
*/
@SuppressWarnings("serial")
public class GUIFrame extends JFrame implements ActionListener{
	Base base;
	
	//Specifications for toolbar buttons and their popups
	//all button icons are souced from: https://www.svgrepo.com/
	String[][] verticalButtons = {{"bRect", "Rectangle", "/data/rectangleoption.png"},
									{"bEllipse", "Ellipse", "/data/ellipseoptions.png"},
									{"bTriangle", "Triangle", "/data/triangleoptions.png"},
									{"bArc", "Arc", "/data/arcoptions.png"},
									{"bQuad", "Quad", "/data/parallelogram-figure-form-geometry-graphic-line-svgrepo-com.png"}, 
									{"bLine", "Line", "/data/lineoptions.png"},
									{"bPoint", "Point", "/data/dots-svgrepo-com.png"},
									{"bText", "Text", "/data/text-svgrepo-com.png"}};
	
	String[][] horizontalButtons = {{"bFill", "Fill", "/data/fill-svgrepo-com.png"},
									{"bColour", "Set colour", "/data/color-palette-svgrepo-com.png"},
									{"bStrokeColour", "Set stroke colour", "/data/pen-line-svgrepo-com.png"},
									{"bStrokeSize", "Set stroke size", "/data/stroke-width-svgrepo-com.png"},
									{"bUndo", "Undo", "/data/undo-left-svgrepo-com.png"},
									{"bRedo", "Redo", "/data/undo-right-svgrepo-com.png"},
									{"bDelete", "Delete shape", "/data/delete-svgrepo-com.png"},
									{"bClear", "Clear canvas", "/data/clear-svgrepo-com.png"},
									{"bUpdate", "Update drawing from code", "/data/update-svgrepo-com.png"},
									{"bSelect", "Select shape", "/data/cursor-alt-svgrepo-com.png"},
									{"bArray", "Group shapes", "/data/value-pointer-svgrepo-com.png"},
									{"bAnimate", "Animate shape", "/data/stars-svgrepo-com.png"},
									{"bButton", "Add action", "/data/button-arrow-right-svgrepo-com.png"},
									{"bReadMe", "Documentation", "/data/question-circle-svgrepo-com.png"}};
	
	String[][] rectButtons = {{"bRectangle", "Rectangle", "/data/rectangle-wide-svgrepo-com.png"},
								{"bSquare", "Square", "/data/square-svgrepo-com.png"}};
	
	String[][] ellipseButtons = {{"bEllipse", "Ellipse", "/data/ellipse-figure-form-geometry-graphic-line-svgrepo-com.png"},
								{"bCircle", "Circle", "/data/circle-svgrepo-com.png"}};
	
	String[][] lineButtons = {{"bLine", "Line", "/data/line-tool-svgrepo-com.png"}, 
								{"bCurve", "Curved line", "/data/vector-arc-svgrepo-com.png"}, 
								{"bBezier", "Bezier curve", "/data/spline-svgrepo-com.png"}};
	
	String[][] arcButtons = {{"bChord", "Chord arc", "/data/chordarc.png"},
							{"bOpen", "Open arc", "/data/openarc.png"},
							{"bPie", "Pie arc", "/data/circle-three-quarters-svgrepo-com.png"}};
	
	String[][] triangleButtons = {{"bEquilateral", "Equilateral triangle", "/data/shape-triangle-figure-form-geometry-graphic-svgrepo-com.png"},
									{"bIsosceles", "Isosceles triangle", "/data/triangle-svgrepo-com.png"}, 
									{"bScalene", "Scalene triangle", "/data/triangle-hand-drawn-shape-outline-svgrepo-com.png"}};
	
	String[][] animationButtons = {{"bUpAndDown", "Up and down", "/data/up-and-down-arrows-svgrepo-com.png"},
			{"bBackAndForth", "Back and forth", "/data/directional-arrows-left-and-right-svgrepo-com.png"},
			{"bExpandAndContract", "Expand and contract", "/data/expand-svgrepo-com.png"}};
	
	String[][] buttonButtons = {{"bLink", "Open website", "/data/link-svgrepo-com.png"},
			{"bAnimation", "Trigger animation", "/data/stars-svgrepo-com.png"}};
	
	Map<String, JButton> buttons = new HashMap<String, JButton>();	
	Map<String, ButtonMenu> buttonMenus = new HashMap<String, ButtonMenu>();
	
	//Popup used to create textboxes
	JPopupMenu setText = new JPopupMenu("SetText");
	JTextField text = new JTextField( 35 );
	JButton confirmText = new JButton();
    
	//The inital values of the drawing
    int[] canvasSize = {400,400};
    int backgroundColor = 255;
    int strokeColor = 0;
    int currentStrokeSize = 1;
    
    //creating the main components of the tool
    GUIPanel p = new GUIPanel(canvasSize[0], canvasSize[1],this);
    JPopupMenu strokeSelector = new JPopupMenu("StrokeSize");
    JSlider strokeSize = new JSlider(0, 50, 1);
    JFrame f = new JFrame("SketchToCode");
    JButton confirmStroke = new JButton();
    KeyListeners keyListeners = new KeyListeners(p,this);
	
    /** 
     * This method is used show the main JFrame and create everything on screen. 
     * @param base This is the processing application. 
     * @return void Nothing. 
     */
	public void showGUI(Base base) { 
		this.base = base;
		p.base = base;

		f.setMaximumSize(new Dimension(1000000,1000000));
		
		buttonMenus.put("bRect", new ButtonMenu(p, rectButtons));
		buttonMenus.put("bEllipse", new ButtonMenu(p, ellipseButtons));
		buttonMenus.put("bArc", new ButtonMenu(p, arcButtons));
		buttonMenus.put("bLine", new ButtonMenu(p, lineButtons));
		buttonMenus.put("bTriangle", new ButtonMenu(p, triangleButtons));
		buttonMenus.put("bAnimate", new ButtonMenu(p, animationButtons));
		buttonMenus.put("bButton", new ButtonMenu(p, buttonButtons));
			
		f.setLayout(new BorderLayout());
		
        JToolBar vtb = new JToolBar("Vertical toolbar", SwingConstants.VERTICAL);
        f.add(createToolBar(vtb, verticalButtons), BorderLayout.WEST);
        
		JToolBar htb = new JToolBar("Horizontal toolbar", SwingConstants.HORIZONTAL);
		f.add(createToolBar(htb, horizontalButtons), BorderLayout.NORTH);
		

	    f.addKeyListener(keyListeners);
		
		p.setBackground(Color.WHITE);
		f.add(p);
		

	    strokeSize.setMajorTickSpacing(10);
	    strokeSize.setMinorTickSpacing(5);
	    strokeSize.setPaintLabels(true);
        strokeSelector.add(strokeSize);
        JLabel label = new JLabel("OK");
        confirmStroke.add(label);
        confirmStroke.setActionCommand("confirmStroke");
        confirmStroke.addActionListener(this);
        strokeSelector.add(confirmStroke);
        
        setText.add(text);
		JLabel label3 = new JLabel("OK");
		confirmText.add(label3);
		confirmText.setActionCommand("confirmText");
		confirmText.addActionListener(this);
        setText.add(confirmText);
	    
	    initEditor();
	    
	    f.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
	    
	    //update the Processing sketch's size when the window is resized
	    p.addComponentListener(new ComponentAdapter() {
	    	    public void componentResized(ComponentEvent e){
	    	    	updateSize(e.getComponent().getWidth(), e.getComponent().getHeight());
	    		}
	    }); 
	    
	    f.pack();
	    f.setVisible(true); 
    }
	
	/** 
	* This adds the default lines to the processing editor if they are not already there.
	* @return void Nothing. 
	*/
	public void initEditor() {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		Boolean isBackgroundSet = false;
		Boolean isFillSet = false;
		Boolean isStrokeSet = false;
		Boolean isSetupSet = false;
		Boolean isDrawSet = false;
		
		//ckeck whether setup and draw functions are initialised correctly
		if (editorLines.size() > 0) {
	    	for (String line: editorLines) {
			    if (line.contains("background(")) {
			    	isBackgroundSet = true;
			    }
			    
			    if (line.contains("fill(")) {
			    	isFillSet = true;
			    }
			    
			    if (line.contains("stroke(")) {
			    	isStrokeSet = true;
			    }
			    
			    if (line.contains("void setup() {")) {
			    	isSetupSet = true;
			    }
			    
			    if (line.contains("void draw() {")) {
			    	isDrawSet = true;
			    }
	    	}
	    }
		
		updateSize(canvasSize[0], canvasSize[1]);
		
		//Fill in missing values
		if (!isBackgroundSet) {
	    	p.insertProcessingLine("\tbackground(" + backgroundColor + ");", 2);
	    }
	    
	    if (!isFillSet) {
	    	p.insertProcessingLine("\tfill(250,250,250);", 3);
	    }
	    
	    if (!isStrokeSet) {
	    	p.insertProcessingLine("\tstroke(" + strokeColor + ");", 4);
	    }
	    
	    if (!isSetupSet) {
	    	p.insertProcessingLine("}", 5);
	    }
	    
	    if (!isDrawSet) {
	    	p.insertProcessingLine("void draw() {", 6);
	    	p.updateCode("}");
	    }
	    
	    //make canvas match the Processing sketch setup
	    updateDrawingFromCode(editorLines);
	    
	    //save a reference of the blank drawing
	    if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
        }
	}
	
	/** 
    * Updates the size of the Processing sketch. 
    * @param width The width to set the Processing sketch to.
    * @param height The height to set the Processing sketch to
    * @return void Nothing. 
    */
	public void updateSize(int width, int height) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	base.getActiveEditor().setText("void setup() {\n\tsize(" + width + ", " + height + ");");
        for (int i = 2; i < editorLines.size(); i++) {
    		p.updateCode(editorLines.get(i));
    	}
	}
	
	/** 
    * This method is used to create the upper and left toolbars of the JFrame. 
    * @param tb The toolbar to add buttons to.
    * @param buttonNames A 2D array containing the names and specification of the button to add
    * @return JToolBar The toolbar with the buttons added. 
    */
	public JToolBar createToolBar(JToolBar tb, String[][] buttonNames) { 
        for (int i = 0; i < buttonNames.length; i++) {
        	  buttons.put(buttonNames[i][0], new JButton());
        	  
        	  //try to add the button's icon to it
        	  try {
	        	  Image icon = ImageIO.read(getClass().getResource(buttonNames[i][2]));
	        	  buttons.get(buttonNames[i][0]).setIcon(new ImageIcon(icon.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH)));
        	  } catch (Exception e) {
        		    System.out.println(e);
    		  }
        	  
        	  //style button and add tooltipse
        	  buttons.get(buttonNames[i][0]).setActionCommand(buttonNames[i][0]);
        	  buttons.get(buttonNames[i][0]).addActionListener(this);
        	  buttons.get(buttonNames[i][0]).setToolTipText(buttonNames[i][1]);
        	  buttons.get(buttonNames[i][0]).setPreferredSize(new Dimension(30, 30));
        	  buttons.get(buttonNames[i][0]).setBackground(Color.LIGHT_GRAY);
        	  buttons.get(buttonNames[i][0]).setOpaque(true);
        	  
        	  //Ass button to toolbar
        	  tb.add(buttons.get(buttonNames[i][0]));
        	  tb.setBackground(Color.LIGHT_GRAY);
        	  tb.setOpaque(true);
        	  
        	  //add the buttons popup
        	  if (buttonMenus.containsKey(buttonNames[i][0])) {
        		  buttons.get(buttonNames[i][0]).setComponentPopupMenu(buttonMenus.get(buttonNames[i][0]).buttonMenu);
        	  }
        }
        
        return tb;
    }
	
	/** 
    * This method is used to update the current drawing based on the lines given. 
    * @param editorLines The current Processing code. 
    * @return void Nothing. 
    */
	public void updateDrawingFromCode(ArrayList<String> editorLines) {
		//start with a blank canvas
		p.shapes.clear();
		p.textBoxes.clear();
		p.shapeGroups.clear();
		
		//set styling to default values
		Color nextFill = Color.WHITE;
		Color nextStrokeColour = Color.BLACK;
		int nextStrokeSize = 1;
		String currentClass = null;
		int rotation = 0;
		int count = 0;
		p.isFlippedHorizontal = false;
		p.isFlippedVertical = false;
		p.rotation = 0;
		p.zoom = 0;
		
		for (String line: editorLines) {
			//assume the current line is a shape, if it is not possible to build a shape from the line check if it's something else
			ShapeBuilder shapeToAdd = new ShapeBuilder(line, null, null);
			if (shapeToAdd.javaShape != null) {
				shapeToAdd.fill = nextFill;
				shapeToAdd.stroke = nextStrokeColour;
				shapeToAdd.strokeSize = nextStrokeSize;
				shapeToAdd.processingShape = line.replace("\t", "");
				shapeToAdd.rotation = rotation;
				p.shapes.add(shapeToAdd);
				if (currentClass != null) {
					p.shapeGroups.get(p.shapeGroups.size()-1).shapes.add(shapeToAdd);
				}
			} else if (line.contains("fill(")) {
				String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
				if (RGBValues.length != 3) {
					nextFill = new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[0]));
				} else {
					nextFill = new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2]));
				}
			} else if (line.contains("stroke(")) {
				String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
				if (RGBValues.length != 3) {
					nextStrokeColour = new Color(Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]));
				} else {
					nextStrokeColour = new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2]));
				}
			} else if (line.contains("strokeWeight(")) {
				nextStrokeSize = Integer.valueOf(line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0]);
			} else if (line.contains("text(")) {
				String[] textValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
				TextBox textBox = new TextBox(textValues[1].replace("\"",""), new Point(Integer.valueOf(textValues[1]),Integer.valueOf(textValues[2])), new Point(Integer.valueOf(textValues[1])+60,Integer.valueOf(textValues[2])+20));
            	p.textBoxes.add(textBox);
            	textBox.bounds.stroke = nextStrokeColour;
            	textBox.bounds.fill = nextStrokeColour;
            	p.shapes.add(textBox.bounds);
			} else if (line.contains("background(")) {
				String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
				if (RGBValues.length != 3) {
					p.setBackground(new Color(Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0])));
				} else {
					p.setBackground(new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2])));
				}
			} else if (line.contains("size(")) {
				String[] size = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",2);
				p.resize(Integer.valueOf(size[0]),Integer.valueOf(size[1]));
				f.setSize(new Dimension(Integer.valueOf(size[0])+34,Integer.valueOf(size[1])+62));
			} else if (line.contains("class ")) {
				String className = line.split(" ")[1].replace("{", "");
				currentClass = className;
				p.shapeGroups.add(new ShapeGroup(new ArrayList<ShapeBuilder>(), className));
			} else if (line.contains("rotate(")) {
				if (line.contains("rotate(-")) {
					rotation = 0;
				} else if (editorLines.get(count+1).contains("translate(")) {
					p.rotation = (int)Math.toDegrees(java.lang.Double.parseDouble(line.split("\\(")[1].split("\\)")[0]));
				} else {
					rotation = (int)Math.toDegrees(java.lang.Double.parseDouble(line.split("\\(")[1].split("\\)")[0]));
				}
			} else if (line.contains("scale(1,-1);")){
				p.isFlippedHorizontal = true;
			} else if (line.contains("scale(-1,1);")) {
				p.isFlippedVertical = true;
			} else if (line.contains("scale(")) {
				p.zoom = Float.parseFloat(line.split("\\(")[1].split(",")[0])*100;
			}
			
			count++;
		}
		p.repaint();
	}
	
	/** 
    * Determines the naxt action after a button of the toolbar has been pressed. 
    * @param e This current event. 
    * @return void Nothing. 
    */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "bUpdate":
				ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
				updateDrawingFromCode(editorLines);
				break;
			case "bAnimate":
			case "bButton":
				JButton button = buttons.get(e.getActionCommand());
				buttonMenus.get(e.getActionCommand()).showButtonMenu(p, button.getX(), button.getY());
				break;
	        case "bUndo":
	        	keyListeners.callUndo();
	        	break;
	        case "bRedo":
	        	keyListeners.callRedo();
	        	break;
	        case "bClear":
	        	base.getActiveEditor().setText(null);
	        	initEditor();
	        	ArrayList<String> initLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
				updateDrawingFromCode(initLines);
				p.shapes.clear();
				if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	            }
	        	break;
	        case "bReadMe":
	        	try {
	        		java.awt.Desktop.getDesktop().browse(URI.create("https://github.com/veraborvinski/SketchCode/blob/distribution/README.md"));
				} catch (Exception err) {
					System.out.println(err);
				}
	        	break;
	        case "bArray":
	        	p.groupShapes();
	        	break;
	        case "bFill":
	        	keyListeners.deselectAll();
	        	p.fill = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (p.selectedShapes.size() != 0) {
		        	p.changeFill(p.fill);
		        	p.repaint();
			        p.fill = null;
	        	}
	        	break;
	        case "bColour":
	        	Color color = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (color != null) {
		        	int[] newColor = {color.getRed(),color.getGreen(),color.getBlue()};	        	
			        p.changeFill(color);    
			        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		            }
			        p.repaint();
	        	}
	        	break;
	        case "bStrokeColour":
	        	Color strokeColor = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (strokeColor != null) {
		    	    if (p.selectedShapes.size() != 0) {
		    	    	for (ShapeBuilder selectedShape: p.selectedShapes) {
			    	    	int position = p.findProcessingShapeLine(selectedShape)-1;
			    	    	selectedShape.stroke = strokeColor;
			    	    	if (p.findProcessingLine(position).contains("stroke(")) {
			    	    		p.replaceProcessingLine("\t"+selectedShape.getProcessingStroke(), position);
					    	} else {
					    		p.insertProcessingLine("\t"+selectedShape.getProcessingStroke(), position);
					    	}
			    	    	if (p.shapes.indexOf(selectedShape)+1 < p.shapes.size()) {
			    	    		ShapeBuilder nextShape = p.shapes.get(p.shapes.indexOf(selectedShape)+1);
			    		    	int nextPosition = p.findProcessingShapeLine(nextShape)-1;
			    		    	if (p.findProcessingLine(nextPosition).contains("stroke(")) {
			    		    		p.replaceProcessingLine("\t"+nextShape.getProcessingStroke(), nextPosition);
						    	} else {
						    		p.insertProcessingLine("\t"+nextShape.getProcessingStroke(), nextPosition);
						    	}	    	    		
			    	    	}
		    	    	}
		        	}
		    	    else {
		    	    	if (p.shapes.size() != 0) {
		    	    		p.shapes.get(p.shapes.size()-1).stroke = strokeColor;
		    	    	}
	    	    		p.defaultStrokeColour = strokeColor;
		    	    	p.updateDraw("stroke(" + strokeColor.getRed() + ", " + strokeColor.getGreen() + ", " + strokeColor.getBlue() + ");");
		    	    }
		    	    if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		            }
		        	p.repaint();
	        	}
	        	break;
	        case "confirmStroke":
	        	int newSize = strokeSize.getValue();
	        	if (p.selectedShapes.size() != 0) {
	     	    	for (ShapeBuilder selectedShape: p.selectedShapes) {
	     	    		int position = p.findProcessingShapeLine(selectedShape)-1;
		    	    	selectedShape.strokeSize = newSize;
		    	    	if (p.findProcessingLine(position).contains("strokeWeight(")) {
		    	    		p.replaceProcessingLine("\t"+selectedShape.getProcessingStrokeSize(), position);
				    	} else {
				    		p.insertProcessingLine("\t"+selectedShape.getProcessingStrokeSize(), position);
				    	}
		    	    	if (p.shapes.indexOf(selectedShape)+1 < p.shapes.size()) {
		    	    		ShapeBuilder nextShape = p.shapes.get(p.shapes.indexOf(selectedShape)+1);
		    		    	int nextPosition = p.findProcessingShapeLine(nextShape)-1;
		    		    	if (p.findProcessingLine(nextPosition).contains("strokeWeight(")) {
		    		    		p.replaceProcessingLine("\t"+nextShape.getProcessingStrokeSize(), nextPosition);
					    	} else {
					    		p.insertProcessingLine("\t"+nextShape.getProcessingStrokeSize(), nextPosition);
					    	}
		    	    	}
	     	    	}
	         	} else {
	    	    	if (p.shapes.size() != 0) {
	    	    		p.shapes.get(p.shapes.size()-1).strokeSize = newSize;
	    	    	}
	    	    	p.defaultStrokeSize = newSize;
	    	    	p.updateDraw("strokeWeight(" + newSize + ");");
	    	    }
	        	if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	            }
	        	p.repaint();
	        	break;
	        case "bStrokeSize":
	        	strokeSelector.show(f,200,100);
	        	break;
	        case "bDelete":
	        	keyListeners.callDelete();
	        	break;
	        case "bText":
	        	if (p.currentEvent == "confirmText") {
	        		buttons.get("bText").setOpaque(false);
	        		buttons.get("bText").setBackground(Color.LIGHT_GRAY);
	        		p.currentEvent = "";
	        	} else {
	        		buttons.get("bText").setBackground(Color.GRAY);
	        		setText.show(p, buttons.get("bText").getX(), buttons.get("bText").getY());
	        	}
	        	break;
	        default:
	        	//mark shape button as selected
		        if (p.currentEvent == e.getActionCommand()) {
	        		p.cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	        		
	        		if (buttons.keySet().contains(p.currentEvent)) {
    					buttons.get(p.currentEvent).setOpaque(false);
        				buttons.get(e.getActionCommand()).setBackground(Color.LIGHT_GRAY);
        			}
	        		
	        		p.currentEvent = "";
	        	} else if (p.currentEvent != e.getActionCommand()) {
	        		keyListeners.deselectAll();
	        		p.cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	        		p.currentEvent = e.getActionCommand();
	        		
        			if (buttons.keySet().contains(p.currentEvent)){
        				buttons.get(e.getActionCommand()).setBackground(Color.GRAY);
        			} 
	        	}
	        	p.comboBox = null;
	        	p.repaint();	
		}
	}	
}



