package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

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
    MyPanel p = new MyPanel(canvasSize[0], canvasSize[1]);
	
	public void showGUI(Base base) { 
		this.base = base;
		p.base = base;
		
		JFrame f = new JFrame("SketchToCode");
		f.setLayout(new BorderLayout());
		
        JToolBar vtb = new JToolBar("Vertical tool bar", SwingConstants.VERTICAL);
        f.add(createToolBar(vtb, verticalButtons), BorderLayout.WEST);
        
		JToolBar htb = new JToolBar("Horizontal tool bar", SwingConstants.HORIZONTAL);
		f.add(createToolBar(htb, horizontalButtons), BorderLayout.NORTH);
		
		p.setBackground(Color.WHITE);
		f.add(p);
		
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
	        case "bUndo":
	        	if (p.shapes.size() != 0) {
		        	ArrayList<String> editorLines = new ArrayList<String>(Arrays.asList(base.getActiveEditor().getText().split("\n")));
		        	codeHistory.add(editorLines.get(editorLines.size()-1));
		        	editorLines.remove(editorLines.size()-1);
		        	base.getActiveEditor().setText("");
		        	for (int i = 0; i < editorLines.size(); i++) {
		        		if (editorLines.get(i) != "") {
		        			p.updateCode(editorLines.get(i));	
		        		}
		        	}
		        	p.shapeHistory.add(p.shapes.get(p.shapes.size()-1));
		        	p.shapes.remove(p.shapes.size()-1);
		        	p.repaint();
	        	}
	        	break;
	        
	        case "bRedo":
	        	if (p.shapeHistory.size() != 0) {
		        	p.updateCode(codeHistory.get(codeHistory.size()-1)); 
		        	codeHistory.remove(codeHistory.size()-1);
		        	p.shapes.add(p.shapeHistory.get(p.shapeHistory.size()-1));
		        	p.shapeHistory.remove(p.shapeHistory.size()-1);
		        	p.repaint();
	        	}
	        	break;
	        
	        default:
	            p.currentEvent = e.getActionCommand();
		}
	}
}

@SuppressWarnings("serial")
class MyPanel extends JPanel implements MouseListener{
	
	public ArrayList<Shape> shapes = new ArrayList<Shape>();
	public ArrayList<Shape> shapeHistory = new ArrayList<Shape>();
	String currentEvent = "";
	Point firstPoint = new Point(0,0);
	MyPanel panel;
	Base base;

    public MyPanel(int height, int width) {
    	setPreferredSize(new Dimension(height, width));
    	addMouseListener(this); 
    }
    
    public void paintComponent(Graphics g) {     
        super.paintComponent(g);       
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        for (Shape shape: shapes)
        {
        	g2.draw(shape);
        }
        g2.dispose();
    }  
    
    public void addRect(int xPos, int yPos, int width, int height) {
        shapes.add(new Rectangle(xPos,yPos,width,height));
        repaint();
    }
    
    public void addEllipse(int xPos, int yPos, int width, int height) {
        shapes.add(new Ellipse2D.Double(xPos,yPos,width,height));
        repaint();
    }
    
    public void addTriangle(int[] xPos, int[] yPos) {
        shapes.add(new Polygon(xPos,yPos,3));
        repaint();
    }
    
    public void addArc(int xPos, int yPos, int width, int height, int angle1, int angle2, int type) {
        shapes.add(new Arc2D.Double(xPos, yPos, width, height, angle1, angle2, type));
        repaint();
    }
    
    public void addQuad(int[] xPos, int[] yPos) {
        shapes.add(new Polygon(xPos,yPos,4));
        repaint();
    }
    
    public void addLine(int xPos1, int yPos1, int xPos2, int yPos2) {
        shapes.add(new Line2D.Double(xPos1,yPos1,xPos2,yPos2));
        repaint();
    }
    
    public void mousePressed(MouseEvent e) {
    	if (currentEvent != "") {
		    firstPoint = e.getPoint();
		}
    }
    
    public void mouseReleased(MouseEvent e) {
    	Point secondPoint = e.getPoint();
    	int x1 = firstPoint.x;
    	int x2 = secondPoint.x;
    	int y1 = firstPoint.y;
    	int y2 = secondPoint.y;

    	int smallX = ((x1>x2) ? x2 : x1);
    	int smallY = ((y1>y2) ? y2 : y1);
    	
    	int bigX = ((x2>x1) ? x2 : x1);
    	int bigY = ((y2>y1) ? y2 : y1);
		switch (currentEvent) {
	        case "bRect":
	        	updateCode("rect(" + x1 + ", " + y1 + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ");");
	        	addRect(x1,y1, Math.abs(x1-x2),Math.abs(y1-y2));
	            break;
	 
	        case "bEllipse":
	        	updateCode("ellipse(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ",  " + Math.abs(y1-y2) + ");");
	        	addEllipse(smallX,smallY, Math.abs(x1-x2),Math.abs(y1-y2));
	            break;
	 
	        case "bTriangle":
	        	updateCode("triangle(" + x1 + ", " + y1 + ", " + Math.abs((2*x1)-x2) + ", " + y2 + ", " + x2 + ", " + y2 + ");");
	        	int[] x = {x1,Math.abs((2*x1)-x2),x2};
	        	int[] y = {y1,y2,y2};
	        	addTriangle(x, y);
	            break;
	 
	        case "bArc":
	        	double startAngle = 0;
	        	double endAngle = Math.PI;
	        	updateCode("arc(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ", " + (startAngle+Math.PI) + ", " + (endAngle+Math.PI) + ", PIE);");
	        	addArc(smallX,smallY, Math.abs(x1-x2),Math.abs(y1-y2), (int)Math.toDegrees(startAngle), (int)Math.toDegrees(endAngle), Arc2D.PIE);
	        	break;
	 
	        case "bQuad":
	        	int[] xPos = {smallX,smallX+(int)(Math.abs(x1-x2)*0.75),bigX,smallX+(int)(Math.abs(x1-x2)*0.25)};
	        	int[] yPos =  {smallY,smallY,bigY,bigY};
	        	updateCode("quad(" + xPos[0] + ", " + yPos[0] + ", " + xPos[1] + ", " + yPos[1] + ", " + xPos[2] + ", " + yPos[2] + ", " + xPos[3] + ", " + yPos[3] + ");");
	        	addQuad(xPos, yPos);
	            break;
	 
	        case "bLine":
	        	updateCode("line(" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ");");
	        	addLine(x1, y1, x2, y2);
	            break;
	 
	        case  "bPoint":
	        	updateCode("point(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ");");
	        	addEllipse(smallX,smallY,1,1);
	            break;
		}
		currentEvent = "";
    }
    
    @Override
	public void mouseClicked(MouseEvent e) {
    }
	
	@Override
    public void mouseEntered(MouseEvent e) {
    }
	
	@Override
	public void mouseExited(MouseEvent e) {
    }
    
    public void updateCode(String newText) {
		base.getActiveEditor().setText(base.getActiveEditor().getText() + "\n" + newText);
	}
}

