package template.tool;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import processing.app.Base;

@SuppressWarnings("serial")
public class PanelMenu implements ActionListener{
	final JPopupMenu panelMenu = new JPopupMenu("panelMenu");
	
	JPopupMenu setSize = new JPopupMenu("SetSize");
	JTextField height = new JTextField( 4 );
	JTextField width = new JTextField( 4 );
	JButton confirmSize = new JButton();
	
	JPopupMenu rotationSelector = new JPopupMenu("Rotation");
	JPopupMenu zoomSelector = new JPopupMenu("Zoom");
	JSlider rotation = new JSlider(0, 360, 0);
	JSlider zoom = new JSlider(50, 200, 100);
	JButton confirmRotation = new JButton();
	JButton confirmZoom = new JButton();
	
	GUIPanel p;
	Component currentComponent;
	int currentX;
	int currentY;
	
	public PanelMenu(GUIPanel initP){
		p = initP;
		panelMenu.add("Change background").addActionListener( this );
		panelMenu.add("Paste").addActionListener( this );
		panelMenu.add("Select all").addActionListener( this );
		panelMenu.add("Rezise canvas").addActionListener( this );
		panelMenu.add("Rotate").addActionListener( this );
		panelMenu.add("Flip horizontally").addActionListener( this );
		panelMenu.add("Flip vertically").addActionListener( this );
		panelMenu.add("Zoom").addActionListener( this );
		
		setSize.add(width);
		setSize.add(height);
		JLabel label1 = new JLabel("OK");
		confirmSize.add(label1);
		confirmSize.setActionCommand("confirmSize");
		confirmSize.addActionListener(this);
        setSize.add(confirmSize);
        
        rotation.setMajorTickSpacing(90);
        rotation.setPaintLabels(true);
        rotationSelector.add(rotation);
        JLabel label2 = new JLabel("OK");
		confirmRotation.add(label2);
		confirmRotation.setActionCommand("confirmRotation");
		confirmRotation.addActionListener(this);
		rotationSelector.add(confirmRotation);
        
        zoom.setMajorTickSpacing(25);
        zoom.setPaintLabels(true);
        zoomSelector.add(zoom);
        JLabel label3 = new JLabel("OK");
		confirmZoom.add(label3);
		confirmZoom.setActionCommand("confirmZoom");
		confirmZoom.addActionListener(this);
		zoomSelector.add(confirmZoom);
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
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
    			p.repaint();
				break;
			case "Select all":
				p.f.keyListeners.selectAll();
				break;
			case "Change background":
				p.fill = JColorChooser.showDialog(currentComponent,"Select a color", Color.WHITE);
	        	if (p.selectedShapes.size() != 0) {
		        	p.changeFill(p.fill);
		        	p.repaint();
			        p.fill = null;
	        	}
	            
	            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	            }
	        	break;
			case "Rezise canvas":
				setSize.show(p, currentX , currentY);
				break;
			case "confirmSize":
				if (width.getText() != "" && height.getText() != "") {
					p.f.updateSize(Integer.valueOf(width.getText()),Integer.valueOf(height.getText()));
					p.f.updateDrawingFromCode(new ArrayList<String>(Arrays.asList(p.f.base.getActiveEditor().getText().split("\n"))));
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Rotate":
				rotationSelector.show(p, currentX , currentY);
				break;
			case "confirmRotation":
				p.rotateCanvas(rotation.getValue());
				p.rotation = rotation.getValue();
				p.repaint();
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Flip horizontally":
				p.isFlippedHorizontal = !p.isFlippedHorizontal;
				p.repaint();
				if(p.isFlippedHorizontal) {
					int position = p.findProcessingLineNumber("void draw() {");
		    		p.insertProcessingLine("\tscale(1,-1);", position);
		    		p.insertProcessingLine("\ttranslate(0,-"+p.getHeight()+");", position+1);
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Flip vertically":
				p.isFlippedVertical = !p.isFlippedVertical;
				p.repaint();
				if(p.isFlippedVertical) {
					int position = p.findProcessingLineNumber("void draw() {");
		    		p.insertProcessingLine("\tscale(-1,1);", position);
		    		p.insertProcessingLine("\ttranslate(-"+p.getWidth()+",0);", position+1);
				}
		        
		        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
		        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
		        }
				break;
			case "Zoom":
				zoomSelector.show(p, currentX , currentY);
				break;
			case "confirmZoom":
				p.zoom = zoom.getValue();
				p.repaint();
	    		p.insertProcessingLine("\tscale("+100/p.zoom+","+100/p.zoom+");", p.findProcessingLineNumber("void draw() {"));
	            
	            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	            }
				break;
			default:
				break;
		}
	}
}
