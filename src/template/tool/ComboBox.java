package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

import java.awt.Cursor;

import processing.app.Base;

public class ComboBox {
	Rectangle comboBox;
	Ellipse2D rotationPoint;
	
	ComboBox(Rectangle bounds){
		comboBox = bounds;
		rotationPoint = new Ellipse2D.Double(comboBox.x+comboBox.width/2,comboBox.y - 10,4,4); 
	}
	
	public void moveComboBox(Point newPoint) {
		int x1 = newPoint.x - (comboBox.width/2);
		int y1 = newPoint.y - (comboBox.height/2);
		int width = comboBox.width;
		int height = comboBox.height;
		comboBox = new Rectangle(x1,y1,width,height);
		rotationPoint = new Ellipse2D.Double(comboBox.x+comboBox.width/2,comboBox.y - 10,4,4);
	}
	
	public void stretchComboBox(Point newPoint, int cursor) {
		Point firstPoint = new Point(comboBox.x,comboBox.y);
		Point secondPoint = new Point(comboBox.x+comboBox.width,comboBox.y+comboBox.height);
		
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
		comboBox = new Rectangle(firstPoint.x,firstPoint.y,Math.abs(firstPoint.x-secondPoint.x),Math.abs(firstPoint.y-secondPoint.y));
		rotationPoint = new Ellipse2D.Double(comboBox.x+comboBox.width/2,comboBox.y - 10,4,4);
	}
}
