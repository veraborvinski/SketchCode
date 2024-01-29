package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import processing.app.Base;

public class KeyListeners implements KeyListener{
	GUIPanel p;
	GUIFrame f;
	
	public KeyListeners(GUIPanel initPanel, GUIFrame initFrame) {
    	p = initPanel;
    	f = initFrame;
    }
	
	@Override
	public void keyPressed(KeyEvent e) {
		Point newPoint = new Point(p.comboBox.comboBox.x + p.comboBox.comboBox.width/2, p.comboBox.comboBox.y + p.comboBox.comboBox.height/2);

        if(e.getKeyCode() == KeyEvent.VK_Z && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            callUndo();
        } else if(e.getKeyCode() == KeyEvent.VK_Z && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
            callUndo();
        } else if(e.getKeyCode() == KeyEvent.VK_Y && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            callRedo();
        } else if(e.getKeyCode() == KeyEvent.VK_Y && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
            callRedo();
		} else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE) {
            callDelete();
        } else if(e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
        } else if(e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
        } else if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	for (ShapeBuilder copiedShape: p.copiedShapes) {
				ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);
				shapeToPaste.moveShape(copiedShape.firstPoint);
				p.shapes.add(shapeToPaste);
				p.updateDraw(shapeToPaste.processingShape);
			}
        } else if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	for (ShapeBuilder copiedShape: p.copiedShapes) {
				ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);
				shapeToPaste.moveShape(copiedShape.firstPoint);
				p.shapes.add(shapeToPaste);
				p.updateDraw(shapeToPaste.processingShape);
			}
        } else if(e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
			for (ShapeBuilder selectedShape: p.selectedShapes) {
				p.shapes.remove(selectedShape);
				p.removeProcessingLine("\t"+selectedShape.processingShape);
			}
        } else if(e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
			for (ShapeBuilder selectedShape: p.selectedShapes) {
				p.shapes.remove(selectedShape);
				p.removeProcessingLine("\t"+selectedShape.processingShape);
			}
        } else if(e.getKeyCode() == KeyEvent.VK_UP && p.comboBox != null) {
        	newPoint.y -= 10;
        	p.moveShape(newPoint,p.selectedShapes,p.comboBox);
        } else if(e.getKeyCode() == KeyEvent.VK_DOWN && p.comboBox != null) {
        	newPoint.y += 10;
        	p.moveShape(newPoint,p.selectedShapes,p.comboBox);
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT && p.comboBox != null) {
        	newPoint.x += 10;
            p.moveShape(newPoint,p.selectedShapes,p.comboBox);
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT && p.comboBox != null) {
        	newPoint.x -= 10;
            p.moveShape(newPoint,p.selectedShapes,p.comboBox);
        }
    }
	
	@Override
	public void keyReleased(KeyEvent e) {
	
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	public void callUndo() {
		if (p.shapes.size() != 0) {
			ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(f.base.getActiveEditor().getText().split("\n")));
    		ShapeBuilder shapeToRemove = p.shapes.get(p.shapes.size()-1);
    		String lineToRemove = shapeToRemove.processingShape;
        	f.codeHistory.put(p.findProcessingShapeLine(shapeToRemove),lineToRemove);
        	editorLines.remove("\t"+lineToRemove);
        	f.base.getActiveEditor().setText(editorLines.get(0));
        	for (int i = 1; i < editorLines.size(); i++) {
        		p.updateCode(editorLines.get(i));
        	}
        	p.shapeHistory.add(shapeToRemove);
        	p.shapes.remove(shapeToRemove);
        	p.repaint();
    	}
	}
	
	public void callRedo() {
		if (p.shapeHistory.size() != 0) {
			ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(f.base.getActiveEditor().getText().split("\n")));
    		ShapeBuilder shapeToAdd = p.shapeHistory.get(p.shapeHistory.size()-1);
    		int lineNumber = Collections.max(f.codeHistory.keySet());
    		String lineToAdd = f.codeHistory.get(lineNumber);
        	p.insertProcessingLine("\t"+lineToAdd,lineNumber); 
        	f.codeHistory.remove(lineNumber);
        	p.shapes.add(shapeToAdd);
        	p.shapeHistory.remove(shapeToAdd);
        	p.repaint();
    	}
	}
	
	public void callDelete() {
		if (p.selectedShapes.size() != 0 && p.comboBox != null) {
			for (ShapeBuilder shape: p.selectedShapes) {
	        	p.shapeHistory.add(shape);
	        	p.shapes.remove(shape);
	        	p.removeProcessingLine("\t"+shape.processingShape);
			}
        	p.selectedShapes.clear();
			p.comboBox = null;
    	}
		p.repaint();
	}
}
