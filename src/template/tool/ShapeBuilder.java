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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;

import processing.app.Base;

/** 
* The ShapeBuilder class stores the data related to a Processing/Java shape in one class.
* 
* @author Vera Borvinski
*/
public class ShapeBuilder {
	String shapeType = "";
	Point firstPoint;
	Point secondPoint;
	String processingShape = "";
	Shape javaShape;
	String[] shapes = {"rect", "ellipse", "arc", "line", "point", "triangle", "quad", "square", "circle", "scalene", "isosceles", "equilateral", "chord", "open", "pie", "curve", "bezier"};
	String[] processingConstructors = {"rect(", "ellipse(", "arc(", "line(", "point(", "triangle(", "quad(", "curve(", "bezier("};
	
	Color fill = new Color(255,255,255);
	Color stroke = new Color(0,0,0);
	int strokeSize = 1;
	int rotation = 0;
	
	/** 
     * The constructor of a ShapeBuilder which creates a Shape. 
     * @param initShapeType The type of shape that is to be created.
     * @param initFirstPoint The first point of the shape.
     * @param initSecondPoint The second point of the shape.
     */
	ShapeBuilder(String initShapeType, Point initFirstPoint, Point initSecondPoint){
		shapeType = determineShapeType(initShapeType);
		firstPoint = initFirstPoint;
		secondPoint = initSecondPoint;
		if(firstPoint != null & secondPoint != null) {
			createShape();
		} else if (Arrays.asList(processingConstructors).contains(shapeType)){
			createShapeFromCode(initShapeType);
		}
	}
	
	/** 
     * Finds which of the accepted types of shapes the ShapeBuilder belongs to. 
     * @param currentType The shape originally assigned to the ShapeBuilder.
     * @return String One of the permitted shapes. 
     */
	public String determineShapeType(String currentType) {
		for (String shape: shapes) {
			if (currentType.toLowerCase().contains(shape+"(")) {
				processingShape = currentType;
				return shape+"(";
			} else if (currentType.toLowerCase().contains(shape)) {
				return shape;
			}
		}
		return "";
	}
	
	/** 
     * Finds the Processing call for the current fill colour.
     * @return String The fill statement. 
     */
	public String getProcessingFill() {
		return "fill(" + fill.getRed() + ", " + fill.getGreen() + ", " + fill.getBlue() + ");";
	}
	
	/** 
     * Finds the Processing call for the current stroke colour.
     * @return String The stroke statement. 
     */
	public String getProcessingStroke() {
		return "stroke(" + stroke.getRed() + ", " + stroke.getGreen() + ", " + stroke.getBlue() + ");";
	}
	
	/** 
     * Finds the Processing call for the current stroke size.
     * @return String The stroke size statement. 
     */
	public String getProcessingStrokeSize() {
		return "strokeWeight(" + strokeSize + ");";
	}
	
	/** 
     * Finds the Processing call for the current rotation.
     * @return String The rotate statement. 
     */
	public String getProcessingRotate() {
		return  "rotate(" + Math.toRadians(rotation) + ");";
	}
	
	/** 
     * Finds the reverse Processing call for the current rotation.
     * @return String The reverse rotate statement. 
     */
	public String getReverseProcessingRotate() {
		return  "rotate(-" + Math.toRadians(rotation) + ");";
	}
	
	/** 
     * Finds the Processing call for the bounds of the shape.
     * @return String The counds condition. 
     */
	public String getButtonBounds() {
		return  "if (mouseX >= " + javaShape.getBounds().x + " && mouseX <= " + (javaShape.getBounds().x + javaShape.getBounds().width) + " && mouseY >= "+ javaShape.getBounds().y + " && mouseY <= " + (javaShape.getBounds().y + javaShape.getBounds().height) + ") {";
	}
	
	/** 
     * Creates a Shape from a line in Processing.
     * @param s The ShapeBuilder to find the shape for.
     * @return void Nothing. 
     */
	public void createShapeFromCode(String s) {
		String[] values = s.replace(" ", "").split("\\(", 2)[1].split("\\)",2)[0].split(",");
		switch (shapeType) {
	        case "ellipse(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[0])+Integer.valueOf(values[2]),Integer.valueOf(values[1])+Integer.valueOf(values[3]));
				javaShape = new Ellipse2D.Double(Integer.valueOf(values[0])-Integer.valueOf(values[2])/2,Integer.valueOf(values[1])-Integer.valueOf(values[3])/2,Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				shapeType = "ellipse";
				break;
	        case "rect(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[0])+Integer.valueOf(values[2]),Integer.valueOf(values[1])+Integer.valueOf(values[3]));
				javaShape = new Rectangle(Integer.valueOf(values[0]),Integer.valueOf(values[1]),Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				shapeType = "rect";
				break;
	        case "triangle(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[4]),Integer.valueOf(values[5]));
				int[] x = {Integer.valueOf(values[0]),Integer.valueOf(values[2]),Integer.valueOf(values[4])};
				int[] y = {Integer.valueOf(values[1]),Integer.valueOf(values[3]),Integer.valueOf(values[5])};
				javaShape = new Polygon(x,y,3);
				shapeType = "triangle";
				break;
	        case "arc(":
				int smallX = Integer.valueOf(values[0])-(Integer.valueOf(values[2])/2);
				int smallY = Integer.valueOf(values[1])-(Integer.valueOf(values[3])/2);
				firstPoint = new Point(smallX,smallY);
				secondPoint = new Point(Integer.valueOf(values[0])+Integer.valueOf(values[2]),Integer.valueOf(values[1])+Integer.valueOf(values[3]));
				javaShape = new Arc2D.Double(smallX,smallY,Integer.valueOf(values[2]),Integer.valueOf(values[3]),(int)Math.toDegrees(Float.parseFloat(values[4])-Math.PI), (int)Math.toDegrees(Float.parseFloat(values[5])-Math.PI),Arc2D.PIE);
				shapeType = "arc";
				break;
	        case "quad(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[4]),Integer.valueOf(values[5]));
				int[] xPos = {Integer.valueOf(values[0]),Integer.valueOf(values[2]),Integer.valueOf(values[4]),Integer.valueOf(values[6])};
				int[] yPos = {Integer.valueOf(values[1]),Integer.valueOf(values[3]),Integer.valueOf(values[5]),Integer.valueOf(values[7])};
				javaShape = new Polygon(xPos,yPos,4);
				shapeType = "quad";
				break;
	        case "line(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				javaShape = new Line2D.Double(Integer.valueOf(values[0]),Integer.valueOf(values[1]),Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				shapeType = "line";
				break;
	        case "curve(":
				firstPoint = new Point(Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				secondPoint = new Point(Integer.valueOf(values[4]),Integer.valueOf(values[5]));
				javaShape = new QuadCurve2D.Double(Integer.valueOf(values[2]),Integer.valueOf(values[3]),Integer.valueOf(values[0])-350,Integer.valueOf(values[1])-350,Integer.valueOf(values[4]),Integer.valueOf(values[5]));
				shapeType = "curve";
				break;
	        case "bezier(":
				firstPoint = new Point(Integer.valueOf(values[2]),Integer.valueOf(values[3]));
				secondPoint = new Point(Integer.valueOf(values[6]),Integer.valueOf(values[7]));
				javaShape = new CubicCurve2D.Double(Integer.valueOf(values[2]),Integer.valueOf(values[3]),Integer.valueOf(values[0])-100,Integer.valueOf(values[1])-100,Integer.valueOf(values[4]),Integer.valueOf(values[5]),Integer.valueOf(values[6]),Integer.valueOf(values[7]));
				shapeType = "bezier";
				break;
	        case "point(":
				firstPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				secondPoint = new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1]));
				javaShape = new Rectangle(Integer.valueOf(values[0]),Integer.valueOf(values[1]),1,1);
				shapeType = "point";
				break;
		}
	}
	
	/** 
     * Creates a Shape from the points of the ShapeBUilder object.
     * @return void Nothing. 
     */
	public void createShape() {
		int x1 = firstPoint.x;
    	int x2 = secondPoint.x;
    	int y1 = firstPoint.y;
    	int y2 = secondPoint.y;

    	int smallX = ((x1>x2) ? x2 : x1);
    	int smallY = ((y1>y2) ? y2 : y1);
    	
    	int bigX = ((x2>x1) ? x2 : x1);
    	int bigY = ((y2>y1) ? y2 : y1);
    	
    	double startAngle = 0;
    	double endAngle = Math.PI;
    	
		switch (shapeType) {
	        case "rect":
	        	processingShape = shapeType + "(" + x1 + ", " + y1 + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ");";
	        	javaShape = new Rectangle(x1,y1, Math.abs(x1-x2),Math.abs(y1-y2));
	            break;
	        
	        case "square":
	        	processingShape = "rect(" + x1 + ", " + y1 + ", " + Math.abs(x1-x2) + ", " + Math.abs(x1-x2) + ");";
	        	javaShape = new Rectangle(x1,y1, Math.abs(x1-x2),Math.abs(x1-x2));
	            break;
	 
	        case "ellipse":
	        	processingShape = shapeType + "(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ");";
	        	javaShape = new Ellipse2D.Double(smallX,smallY, Math.abs(x1-x2),Math.abs(y1-y2));
	            break;
	            
	        case "circle":
	        	processingShape = "ellipse(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(x1-x2) + ");";
	        	javaShape = new Ellipse2D.Double(smallX,smallY, Math.abs(x1-x2),Math.abs(x1-x2));
	            break;
	 
	        case "triangle":
	        case "scalene":
	        	processingShape = "triangle(" + x1 + ", " + y2 + ", " + Math.abs((2*x1)-x2) + ", " + y1 + ", " + x2 + ", " + y2 + ");";
	        	int[] x = {x1,Math.abs((2*x1)-x2),x2};
	        	int[] y = {y2,y1,y2};
	        	javaShape = new Polygon(x,y,3);
	        	break;
	        
	        case "isosceles":
	        	processingShape = "triangle(" + x1 + ", " + y2 + ", " + (x1+x2)/2 + ", " + y1 + ", " + x2 + ", " + y2 + ");";
	        	int[] xi = {x1,(x1+x2)/2,x2};
	        	int[] yi = {y2,y1,y2};
	        	javaShape = new Polygon(xi,yi,3);
	        	break;
	        
	        case "equilateral":
	        	processingShape = "triangle(" + x1 + ", " + y2 + ", " + (x1+x2)/2 + ", " + (y2-(int)(0.86*(x2-x1))) + ", " + x2 + ", " + y2 + ");";
	        	int[] xe = {x1,(x1+x2)/2,x2};
	        	int[] ye = {y2,y2-(int)(0.86*(x2-x1)),y2};
	        	javaShape = new Polygon(xe,ye,3);
	        	break;
	 
	        case "arc":
	        case "pie":
	        	processingShape = "arc(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ", " + (startAngle+Math.PI) + ", " + (endAngle+Math.PI) + ", PIE);";
	        	javaShape = new Arc2D.Double(smallX,smallY, Math.abs(x1-x2), Math.abs(y1-y2), (int)Math.toDegrees(startAngle), (int)Math.toDegrees(endAngle), Arc2D.PIE);
	        	break;
	        	
	        case "chord":
	        	processingShape = "arc(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ", " + (startAngle+Math.PI) + ", " + (endAngle+Math.PI) + ", CHORD);";
	        	javaShape = new Arc2D.Double(smallX,smallY, Math.abs(x1-x2), Math.abs(y1-y2), (int)Math.toDegrees(startAngle), (int)Math.toDegrees(endAngle), Arc2D.CHORD);
	        	break;
	        	
	        case "open":
	        	processingShape = "arc(" + (int)((x1+x2)/2) + ", " + (int)((y1+y2)/2) + ", " + Math.abs(x1-x2) + ", " + Math.abs(y1-y2) + ", " + (startAngle+Math.PI) + ", " + (endAngle+Math.PI) + ", OPEN);";
	        	javaShape = new Arc2D.Double(smallX,smallY, Math.abs(x1-x2), Math.abs(y1-y2), (int)Math.toDegrees(startAngle), (int)Math.toDegrees(endAngle), Arc2D.OPEN);
	        	break;
	 
	        case "quad":
	        	int[] xPos = {smallX,smallX+(int)(Math.abs(x1-x2)*0.75),bigX,smallX+(int)(Math.abs(x1-x2)*0.25)};
	        	int[] yPos =  {smallY,smallY,bigY,bigY};
	        	processingShape = shapeType + "(" + xPos[0] + ", " + yPos[0] + ", " + xPos[1] + ", " + yPos[1] + ", " + xPos[2] + ", " + yPos[2] + ", " + xPos[3] + ", " + yPos[3] + ");";
	        	javaShape = new Polygon(xPos,yPos,4);
	            break;
	 
	        case "line":
	        	processingShape = shapeType + "(" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ");";
	        	javaShape = new Line2D.Double(x1, y1, x2, y2);
	            break;
	        
	        case "curve":
	        	processingShape = shapeType + "(" + (x1+300) + ", " + (y1+300) + ", " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + x2 + ", " + y2  +");";
	        	javaShape = new QuadCurve2D.Double(x1,y1,x1-50,y1-50,x2,y2);
	            break;
	        
	        case "bezier":
	        	processingShape = shapeType + "(" + (x1+50) + ", " + (y1+50) + ", " + x1 + ", " + y1 + ", " + (x2+50) + ", " + (y2+50) + ", " + x2 + ", " + y2 + ");";
	        	javaShape = new CubicCurve2D.Double(x1,y1,x1-50,y1-50,x2+50,y2+50,x2,y2);
	            break;
	 
	        case "point":
	        	processingShape = shapeType + "(" + x1 + ", " + y1 + ");";
	        	javaShape = new Rectangle(x1,y1,strokeSize,strokeSize);
	        	this.fill = new Color(0,0,0);
	            break;
		}
	}
	
	/** 
     * Calaculates the new position of the ShapeBuilder..
     * @param newPoint The point to move the shape to.
     * @return void Nothing. 
     */
	public void moveShape(Point newPoint) {
		int x1 = newPoint.x - Math.abs(firstPoint.x-secondPoint.x)/2;
		int y1 = newPoint.y - Math.abs(firstPoint.y-secondPoint.y)/2;
		int x2 = newPoint.x + Math.abs(firstPoint.x-secondPoint.x)/2;
		int y2 = newPoint.y + Math.abs(firstPoint.y-secondPoint.y)/2;
		firstPoint = new Point(x1,y1);
		secondPoint = new Point(x2,y2);
		createShape();
	}
	
	/** 
     * Stretches on edge of a Shape to a new point.
     * @param newPoint The point to stretch the shape's edge to.
     * @param cursor The direction of the stretch.
     * @return void Nothing. 
     */
	public void stretchShape(Point newPoint, int cursor) {
		switch (cursor) {
			case Cursor.NW_RESIZE_CURSOR:
				firstPoint = newPoint;
				break;
			case Cursor.SW_RESIZE_CURSOR:
				firstPoint = new Point(newPoint.x,firstPoint.y);
				secondPoint = new Point(secondPoint.x,newPoint.y);
				break;
			case Cursor.W_RESIZE_CURSOR:
				firstPoint = new Point(newPoint.x,firstPoint.y);
				break;
			case Cursor.NE_RESIZE_CURSOR:
				firstPoint = new Point(firstPoint.x,newPoint.y);
				secondPoint = new Point(newPoint.x,secondPoint.y);
				break;
			case Cursor.SE_RESIZE_CURSOR:
				secondPoint = newPoint;
				break;
			case Cursor.E_RESIZE_CURSOR:
				secondPoint = new Point(newPoint.x,secondPoint.y);
				break;
			case Cursor.N_RESIZE_CURSOR:
				firstPoint = new Point(firstPoint.x,newPoint.y);
				break;
			case Cursor.S_RESIZE_CURSOR:
				secondPoint = new Point(secondPoint.x,newPoint.y);
				break;
		}
		createShape();
	}
	
	/** 
     * Checks whether a point exists within the shape's bounds.
     * @param point The point.
     * @return Boolean Whether the shape contains the point or not. 
     */
	public Boolean containsPoint(Point point) {
		Rectangle pointBounds = new Rectangle(firstPoint.x-2,firstPoint.y-2,4,4);
		
		if (shapeType != "point" && shapeType != "line") {
			return javaShape.contains(point);
		} else if (shapeType == "point") {
			return pointBounds.contains(point);
		} else {
			return javaShape.intersects(pointBounds);
		}
	}
}
