package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import processing.app.Base;

@SuppressWarnings("serial")
public class GUIFrame extends JFrame implements ActionListener{
	Base base;
	String[] verticalButtons = {"bRect", "bEllipse", "bTriangle", "bArc", "bQuad", "bLine", "bPoint"};
	String[] horizontalButtons = {"bSelect", "bFill", "bColour", "bUndo", "bRedo"};
    ArrayList<String> codeHistory = new ArrayList<String>();
    int[] canvasSize = {400,400};
    int backgroundColor = 255;
    int strokeColor = 0;
    Canvas c = new Canvas();
    ArrayList<Shape> shapes = new ArrayList<Shape>();
	
	public void showGUI(Base base) { 
		this.base = base;
		
		JFrame f = new JFrame("SketchToCode");
		f.setLayout(new BorderLayout());
		
        JToolBar vtb = new JToolBar("Vertical tool bar", SwingConstants.VERTICAL);
        f.add(createToolBar(vtb, verticalButtons), BorderLayout.WEST);
        
		JToolBar htb = new JToolBar("Horizontal tool bar", SwingConstants.HORIZONTAL);
		f.add(createToolBar(htb, horizontalButtons), BorderLayout.NORTH);
		
		c.setSize(400, 400);
		c.setBackground(Color.WHITE);
		f.add(c, BorderLayout.CENTER);
		
	    f.setSize(canvasSize[0] + 20, canvasSize[1] + 20); 
	    base.getActiveEditor().setText("size(" + canvasSize[0]+ ", " + canvasSize[1] + ");\n"
	    	+ "background(" + backgroundColor + ");\n"
	    	+ "noFill();\n"
	    	+ "stroke(" + strokeColor + ");"
	    );
	    
	    f.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
	    
	    f.pack();
	    f.setVisible(true); 
    }
	
	public JToolBar createToolBar(JToolBar tb, String[] buttonNames) { 
        Map<String, JButton> buttons = new HashMap<String, JButton>();
        
        for (int i = 0; i < buttonNames.length; i++) {
        	  buttons.put(buttonNames[i], new JButton());
      		  JLabel label = new JLabel(buttonNames[i]);
      		  buttons.get(buttonNames[i]).add(label);
        	  buttons.get(buttonNames[i]).setActionCommand(buttonNames[i]);
        	  buttons.get(buttonNames[i]).addActionListener(this);
        	  tb.add(buttons.get(buttonNames[i]));
        }
        
        return tb;
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
	        case "bRect":
	        	updateCode("rect(0, 0, 10, 10);");
	        	addRect(60,60,70,70);
	            break;
	 
	        case "bEllipse":
	        	updateCode("ellipse(0, 0, 10, 10);");
	            break;
	 
	        case "bTriangle":
	        	updateCode("triangle(0, 0, 10, 10, 20, 30);");
	            break;
	 
	        case "bArc":
	        	updateCode("arc(50, 55, 50, 50, 0, HALF_PI);");
	            break;
	 
	        case "bQuad":
	        	updateCode("quad(15, 12, 34, 8, 26, 22, 12, 30);");
	            break;
	 
	        case "bLine":
	        	updateCode("line(10, 10, 15, 15);");
	            break;
	 
	        case  "bPoint":
	        	updateCode("point(10, 10);");
	            break;
	        case "bUndo":
	        	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
	        	codeHistory.add(editorLines.get(editorLines.size()-1));
	        	editorLines.remove(editorLines.size()-1);
	        	base.getActiveEditor().setText("");
	        	for (int i = 0; i < editorLines.size(); i++) {
	        		updateCode(editorLines.get(i));
	        	}
	        	break;
		}
	}
	
	public void updateCode(String newText) {
		base.getActiveEditor().setText(base.getActiveEditor().getText() + "\n" + newText);
	}

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawRect(100, 150, 60, 200);
        for(Shape s : shapes){
            g2.draw(s);
        }
    }

    public void addRect(int xPos, int yPos, int width, int height) {
        shapes.add(new Rectangle(xPos,yPos,width,height));
        repaint();
    }
}
