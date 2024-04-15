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
		if(e.getKeyCode() == KeyEvent.VK_Z && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            callUndo();
        } else if(e.getKeyCode() == KeyEvent.VK_Z && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
            callUndo();
        } else if(e.getKeyCode() == KeyEvent.VK_Y && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            callRedo();
        } else if(e.getKeyCode() == KeyEvent.VK_Y && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
            callRedo();
		} else if(e.getKeyCode() == KeyEvent.VK_A && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            selectAll();
        } else if(e.getKeyCode() == KeyEvent.VK_A && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	selectAll();
		} else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE) {
            callDelete();
        } else if(e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
            
            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
            }
        	p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
            
            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
            }
        	p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	for (ShapeBuilder copiedShape: p.copiedShapes) {
				ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);
				shapeToPaste.moveShape(copiedShape.firstPoint);
				p.shapes.add(shapeToPaste);
				p.updateDraw(shapeToPaste.processingShape);
			}
            
            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
            }
        	p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	for (ShapeBuilder copiedShape: p.copiedShapes) {
				ShapeBuilder shapeToPaste = new ShapeBuilder(copiedShape.shapeType, copiedShape.firstPoint, copiedShape.secondPoint);
				shapeToPaste.moveShape(copiedShape.firstPoint);
				p.shapes.add(shapeToPaste);
				p.updateDraw(shapeToPaste.processingShape);
			}
            
            if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
            	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
            }
        	p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
			for (ShapeBuilder selectedShape: p.selectedShapes) {
				p.shapes.remove(selectedShape);
				p.removeProcessingLine("\t"+selectedShape.processingShape);
			}
			p.selectedShapes.clear();
			p.comboBox = null;
	        
	        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	        }
			p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.META_DOWN_MASK) {
        	p.copiedShapes.clear();
        	p.copiedShapes.addAll(p.selectedShapes);
			for (ShapeBuilder selectedShape: p.selectedShapes) {
				p.shapes.remove(selectedShape);
				p.removeProcessingLine("\t"+selectedShape.processingShape);
			}
			p.selectedShapes.clear();
			p.comboBox = null;
	        
	        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
	        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
	        }
			p.repaint();
        } else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        	deselectAll();
        } else if (p.comboBox != null) {
        	Point newPoint = new Point(p.comboBox.comboBox.x + p.comboBox.comboBox.width/2, p.comboBox.comboBox.y + p.comboBox.comboBox.height/2);

        	if(e.getKeyCode() == KeyEvent.VK_UP && p.comboBox != null) {
            	newPoint.y -= 10;
            	p.moveShapes(newPoint,p.selectedShapes,p.comboBox);
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN && p.comboBox != null) {
            	newPoint.y += 10;
            	p.moveShapes(newPoint,p.selectedShapes,p.comboBox);
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT && p.comboBox != null) {
            	newPoint.x += 10;
                p.moveShapes(newPoint,p.selectedShapes,p.comboBox);
            } else if(e.getKeyCode() == KeyEvent.VK_RIGHT && p.comboBox != null) {
            	newPoint.x -= 10;
                p.moveShapes(newPoint,p.selectedShapes,p.comboBox);
            } 
        }
    }
	
	@Override
	public void keyReleased(KeyEvent e) {
	
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	public void selectAll() {
		p.selectedShapes = p.shapes;
        p.createComboBoxFromSelectedShapes();
        p.repaint();
	}
	
	public void deselectAll() {
		f.buttons.get("bEllipse").setOpaque(false);
		f.buttons.get("bEllipse").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bRect").setOpaque(false);
		f.buttons.get("bRect").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bLine").setOpaque(false);
		f.buttons.get("bLine").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bTriangle").setOpaque(false);
		f.buttons.get("bTriangle").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bQuad").setOpaque(false);
		f.buttons.get("bQuad").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bPoint").setOpaque(false);
		f.buttons.get("bPoint").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bArc").setOpaque(false);
		f.buttons.get("bArc").setBackground(Color.LIGHT_GRAY);
		f.buttons.get("bText").setOpaque(false);
		f.buttons.get("bText").setBackground(Color.LIGHT_GRAY);
		
		p.currentEvent = "";
	}
	
	public void callUndo() {
		if (p.undoStack.size() > 0) {
			p.redoStack.add(p.undoStack.remove(p.undoStack.size()-1));
		}
		if (p.undoStack.size() > 0) {
			p.base.getActiveEditor().setText(p.undoStack.get(p.undoStack.size()-1).get(0));
			for (int i = 1; i < p.undoStack.get(p.undoStack.size()-1).size(); i++) {
				p.updateCode(p.undoStack.get(p.undoStack.size()-1).get(i));	    			
			}
			f.updateDrawingFromCode(p.undoStack.get(p.undoStack.size()-1));
	    	p.selectedShapes.clear();
			p.comboBox = null;
		}
	}
	
	public void callRedo() {
		if (p.redoStack.size() > 0) {
			p.undoStack.add(p.redoStack.remove(p.redoStack.size()-1));
		}
		if (p.redoStack.size() > 0) {
			p.base.getActiveEditor().setText(p.redoStack.get(p.redoStack.size()-1).get(0));
			for (int i = 1; i < p.redoStack.get(p.redoStack.size()-1).size(); i++) {
				p.updateCode(p.redoStack.get(p.redoStack.size()-1).get(i));	    			
			}
			f.updateDrawingFromCode(p.redoStack.get(p.redoStack.size()-1));
	    	p.selectedShapes.clear();
			p.comboBox = null;
		}
	}
	
	public void callDelete() {
		if (p.selectedShapes.size() != 0 && p.comboBox != null) {
			for (ShapeBuilder shape: p.selectedShapes) {
				for(TextBox textBox: p.textBoxes) {
					if (textBox.bounds == shape) {
						p.removeProcessingLine("\t"+textBox.getProcessingText());
						p.textBoxes.remove(textBox);
					}
				}
				
				if (p.findShapeGroup(shape) != null) {
					p.removeProcessingLine("\t\t"+shape.processingShape);
		        	p.removeProcessingLine("\t"+p.findShapeGroup(shape).classCall);
		        	p.removeClass(p.findShapeGroup(shape).name.toLowerCase());
		        	p.shapeGroups.remove(p.findShapeGroup(shape));
	        	}
				
				int buttonBoundsPosition = p.findProcessingLineNumber("\t"+shape.getButtonBounds());
				
				if (buttonBoundsPosition != -1) {
					while(!p.findProcessingLine(buttonBoundsPosition).contains("}")) {
						p.removeProcessingLineByNumber(buttonBoundsPosition);
					}
					p.removeProcessingLineByNumber(buttonBoundsPosition);
				}
				
	        	p.shapes.remove(shape);
	        	p.removeProcessingLine("\t"+shape.processingShape);
	        	p.removeProcessingLine("\t"+shape.getProcessingRotate());
	        	p.removeProcessingLine("\t"+shape.getReverseProcessingRotate());
			}
        	p.selectedShapes.clear();
			p.comboBox = null;
    	}
        
        if (!p.undoStack.contains(Arrays.asList(p.base.getActiveEditor().getText().split("\n")))) {
        	p.undoStack.add(new ArrayList<String>(Arrays.asList(p.base.getActiveEditor().getText().split("\n"))));
        }
        
		p.repaint();
	}
}
