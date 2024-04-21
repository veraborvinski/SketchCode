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

import java.awt.Cursor;

import processing.app.Base;

/** 
* The ComboBox class stores the components of the hit box surrounding one or more selected shapes.
* 
* @author Vera Borvinski
*/
public class ComboBox {
	Rectangle comboBox;
	Ellipse2D rotationPoint;
	
	/** 
     * The constructor of a ComboBox, adds a rectangle for the combo box, and an ellipse for the rotation point.
     * @param bounds The dimensions of the combo box. 
     */
	ComboBox(Rectangle bounds){
		comboBox = bounds;
		rotationPoint = new Ellipse2D.Double(comboBox.x+comboBox.width/2,comboBox.y - 10,5,5); 
	}
	
	/** 
     * Moves the ComboBox to a new point.
     * @param newPoint The point to move the box to.
     * @return void Nothing. 
     */
	public void moveComboBox(Point newPoint) {
		int x1 = newPoint.x - (comboBox.width/2);
		int y1 = newPoint.y - (comboBox.height/2);
		int width = comboBox.width;
		int height = comboBox.height;
		comboBox = new Rectangle(x1,y1,width,height);
		rotationPoint = new Ellipse2D.Double(comboBox.x+comboBox.width/2,comboBox.y - 10,5,5);
	}
	
	/** 
     * Stretches a side of the ComboBox to a new point.
     * @param newPoint The point to stretch the box to.
     * @return void Nothing. 
     */
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
