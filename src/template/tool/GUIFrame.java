package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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

@SuppressWarnings("serial")
public class GUIFrame extends JFrame implements ActionListener{
	Base base;
	
	//all button icons are souced from: https://www.svgrepo.com/
	String[][] verticalButtons = {{"bRect", "Rectangle", "/data/rectangle-wide-svgrepo-com.png"}, 
									{"bEllipse", "Ellipse", "/data/ellipse-figure-form-geometry-graphic-line-svgrepo-com.png"}, 
									{"bTriangle", "Triangle", "/data/shape-triangle-figure-form-geometry-graphic-svgrepo-com.png"},
									{"bArc", "Arc", "/data/circle-three-quarters-svgrepo-com.png"},
									{"bQuad", "Quad", "/data/parallelogram-figure-form-geometry-graphic-line-svgrepo-com.png"},
									{"bLine", "Line", "/data/line-tool-svgrepo-com.png"},
									{"bPoint", "Point", "/data/dots-svgrepo-com.png"},
									{"bText", "Text", "/data/text-svgrepo-com.png"}};
	
	String[][] horizontalButtons = {{"bFill", "Fill", "/data/fill-svgrepo-com.png"},
									{"bColour", "Set colour", "/data/color-palette-svgrepo-com.png"},
									{"bStrokeColour", "Set stroke colour", "/data/pen-line-svgrepo-com.png"},
									{"bStrokeSize", "Set stroke size", "/data/stroke-width-svgrepo-com.png"},
									{"bUndo", "Undo", "/data/undo-left-svgrepo-com.png"},
									{"bRedo", "Redo", "/data/undo-right-svgrepo-com.png"},
									{"bDelete", "Delete shape", "/data/delete-svgrepo-com.png"},
									{"bUpdate", "Update drawing from code", "/data/update-svgrepo-com.png"},
									{"bSelect", "Select shape", "/data/cursor-alt-svgrepo-com.png"}};
	
	Map<String, JButton> buttons = new HashMap<String, JButton>();
	
    Map<Integer,String> codeHistory = new HashMap<Integer,String>();
    
    int[] canvasSize = {400,400};
    int backgroundColor = 255;
    int strokeColor = 0;
    int currentStrokeSize = 1;
    
    GUIPanel p = new GUIPanel(canvasSize[0], canvasSize[1],this);
    JPopupMenu strokeSelector = new JPopupMenu("StrokeSize");
    JSlider strokeSize = new JSlider(0, 50, 1);
    JFrame f = new JFrame("SketchToCode");
    JButton confirmStroke = new JButton();
    KeyListeners keyListeners = new KeyListeners(p,this);
	
	public void showGUI(Base base) { 
		this.base = base;
		p.base = base;
			
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
		
	    f.setSize(new Dimension(canvasSize[0] + 20, canvasSize[1] + 20));
	    
	    initEditor();
	    
	    f.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
	    p.addComponentListener(new ComponentAdapter() {
	    	    public void componentResized(ComponentEvent e){
	    	    	updateSize(e.getComponent().getWidth(), e.getComponent().getHeight());
	    		}
	    });
	    
	    f.pack();
	    f.setVisible(true); 
    }
	
	public void initEditor() {

		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		
		if (editorLines.size() == 0) {
	    	p.insertProcessingLine("background(" + backgroundColor + ");", 0);
	    	p.insertProcessingLine("fill(250,250,250);", 1);
	    	p.insertProcessingLine("stroke(" + strokeColor + ");", 2);
	    }
	    else if (editorLines.size() > 0) {
		    if (!editorLines.contains("background(" + backgroundColor + ");")) {
		    	p.insertProcessingLine("background(" + backgroundColor + ");", 1);
		    }
		    
		    if (!editorLines.contains("fill(250,250,250);")) {
		    	p.insertProcessingLine("fill(250,250,250);", 2);
		    }
		    
		    if (!editorLines.contains("stroke(" + strokeColor + ");")) {
		    	p.insertProcessingLine("stroke(" + strokeColor + ");", 3);
		    }
	    }
	    
	    updateDrawingFromCode(editorLines);
	}
	
	public void updateSize(int width, int height) {
		ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
    	base.getActiveEditor().setText("size(" + width + ", " + height + ");");
        for (int i = 1; i < editorLines.size(); i++) {
    		p.updateCode(editorLines.get(i));
    	}
	}
	
	public JToolBar createToolBar(JToolBar tb, String[][] buttonNames) { 
        for (int i = 0; i < buttonNames.length; i++) {
        	  buttons.put(buttonNames[i][0], new JButton());
        	  try {
	        	  Image icon = ImageIO.read(getClass().getResource(buttonNames[i][2]));
	        	  buttons.get(buttonNames[i][0]).setIcon(new ImageIcon(icon.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH)));
        	  } catch (Exception e) {
        		    System.out.println(e);
    		  }
        	  buttons.get(buttonNames[i][0]).setActionCommand(buttonNames[i][0]);
        	  buttons.get(buttonNames[i][0]).addActionListener(this);
        	  buttons.get(buttonNames[i][0]).setToolTipText(buttonNames[i][1]);
        	  buttons.get(buttonNames[i][0]).setPreferredSize(new Dimension(30, 30));
        	  buttons.get(buttonNames[i][0]).setBackground(Color.LIGHT_GRAY);
        	  buttons.get(buttonNames[i][0]).setOpaque(true);
        	  tb.add(buttons.get(buttonNames[i][0]));
        	  tb.setBackground(Color.LIGHT_GRAY);
        	  tb.setOpaque(true);
        }
        
        return tb;
    }
	
	public void updateDrawingFromCode(ArrayList<String> editorLines) {
		ArrayList<String> codeList = new ArrayList<String>();
		for (ShapeBuilder shape: p.shapes) {
			codeList.add(shape.processingShape);
		}

		for (String line: editorLines) {
			if (!codeList.contains(line) || codeList.size() == 0) {
				Color nextFill = Color.WHITE;
				Color nextStrokeColour = Color.BLACK;
				int nextStrokeSize = 1;
				ShapeBuilder shapeToAdd = new ShapeBuilder(line, null, null);
				if (shapeToAdd.javaShape != null) {
					shapeToAdd.fill = nextFill;
					shapeToAdd.stroke = nextStrokeColour;
					shapeToAdd.strokeSize = nextStrokeSize;
					p.shapes.add(shapeToAdd);
				}
				else if (line.contains("fill(")) {
					String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
					nextFill = new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2]));
				}
				else if (line.contains("stroke(")) {
					String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
					if (RGBValues.length != 3) {
						nextStrokeColour = new Color(Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]));
					} else {
						nextStrokeColour = new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2]));
					}
				}
				else if (line.contains("strokeWeight(")) {
					nextStrokeSize = Integer.valueOf(line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0]);
				}
				else if (line.contains("background(")) {
					String[] RGBValues = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",3);
					if (RGBValues.length != 3) {
						p.setBackground(new Color(Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0]),Integer.valueOf(RGBValues[0])));
					} else {
						p.setBackground(new Color(Integer.valueOf(RGBValues[0]), Integer.valueOf(RGBValues[1]), Integer.valueOf(RGBValues[2])));
					}
				}
				else if (line.contains("size(")) {
					String[] size = line.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",",2);
					p.setMinimumSize(new Dimension(Integer.valueOf(size[0]),Integer.valueOf(size[1])));
					f.setSize(new Dimension(Integer.valueOf(size[0])+20,Integer.valueOf(size[1])+20));
					p.revalidate();
				}	
			}
		}
		p.repaint();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) { 
			case "bUpdate":
				ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
				updateDrawingFromCode(editorLines);
				break;
	        case "bUndo":
	        	keyListeners.callUndo();
	        	break;
	        case "bRedo":
	        	keyListeners.callRedo();
	        	break;
	        case "bFill":
	        	p.fill = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (p.selectedShapes.size() != 0) {
		        	p.changeFill(p.fill);
		        	p.repaint();
			        p.selectedShape = null;
			        p.fill = null;
	        	}
	        	break;
	        case "bColour":
	        	Color color = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (color != null) {
		        	int[] newColor = {color.getRed(),color.getGreen(),color.getBlue()};	        	
			        p.changeFill(color);        	
			        p.repaint();
			        p.selectedShape = null;
	        	}
	        	break;
	        case "bStrokeColour":
	        	Color strokeColor = JColorChooser.showDialog(this,"Select a color", Color.WHITE);
	        	if (strokeColor != null) {
		        	        	
		    	    if (p.selectedShapes.size() != 0) {
		    	    	int position = p.findProcessingShapeLine(p.selectedShape)-1;
		    	    	p.selectedShape.stroke = strokeColor;
		    	    	p.insertProcessingLine(p.selectedShape.getProcessingStroke(), position);
		    	    	
		    	    	if (p.shapes.indexOf(p.selectedShape)+1 < p.shapes.size()) {
		    	    		ShapeBuilder nextShape = p.shapes.get(p.shapes.indexOf(p.selectedShape)+1);
		    		    	int nextPosition = p.findProcessingShapeLine(nextShape)-1;
		    		    	p.insertProcessingLine(nextShape.getProcessingStroke(), nextPosition);	    	    		
		    	    	}
		        	}
		    	    else {
		    	    	if (p.shapes.size() != 0) {
		    	    		p.shapes.get(p.shapes.size()-1).stroke = strokeColor;
		    	    	}
	    	    		p.defaultStrokeColour = strokeColor;
		    	    	p.updateCode("stroke(" + strokeColor.getRed() + ", " + strokeColor.getGreen() + ", " + strokeColor.getBlue() + ");");
		    	    }
		        	p.selectedShape = null;
		        	p.repaint();
	        	}
	        	break;
	        case "confirmStroke":
	        	int newSize = strokeSize.getValue();
	        	
	        	if (p.selectedShape != null) {
	    	    	int position = p.findProcessingShapeLine(p.selectedShape)-1;
	    	    	p.selectedShape.strokeSize = newSize;
	    	    	p.insertProcessingLine(p.selectedShape.getProcessingStrokeSize(), position);
	    	    	if (p.shapes.indexOf(p.selectedShape)+1 < p.shapes.size()) {
	    	    		ShapeBuilder nextShape = p.shapes.get(p.shapes.indexOf(p.selectedShape)+1);
	    		    	int nextPosition = p.findProcessingShapeLine(nextShape)-1;
	    		    	p.insertProcessingLine(nextShape.getProcessingStrokeSize(), nextPosition);
	    	    	}
	        	}
	    	    else {
	    	    	if (p.shapes.size() != 0) {
	    	    		p.shapes.get(p.shapes.size()-1).strokeSize = newSize;
	    	    	}
	    	    	p.defaultStrokeSize = newSize;
	    	    	p.updateCode("strokeWeight(" + newSize + ");");
	    	    }
	        	
	        	p.selectedShape = null;
	        	p.repaint();
	        	break;
	        case "bStrokeSize":
	        	strokeSelector.show(f,200,100);
	        	break;
	        case "bDelete":
	        	keyListeners.callDelete();
	        	break;
	        default:
	        	if (p.currentEvent != e.getActionCommand()) {
		        	if (buttons.keySet().contains(p.currentEvent)) {
		        		buttons.get(p.currentEvent).setOpaque(false);
		        		buttons.get(p.currentEvent).setBackground(Color.LIGHT_GRAY);
	        		}	
	        		p.cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	        		p.currentEvent = e.getActionCommand();
	        		buttons.get(e.getActionCommand()).setBackground(Color.GRAY);
	        	}
	        	else {
	        		p.cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	        		buttons.get(p.currentEvent).setOpaque(false);
	        		buttons.get(p.currentEvent).setBackground(Color.LIGHT_GRAY);
	        		p.currentEvent = "";
	        	}
	        	p.comboBox = null;
	        	p.repaint();
		}
	}
	
	
}



