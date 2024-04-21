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

import processing.app.Base;

/** 
* The TextBox class stores the data related to a Processing text() object/ Java DrawString.
* 
* @author Vera Borvinski
*/
public class TextBox {
	String text = "";
	ShapeBuilder bounds;
	
	Color stroke = new Color(0,0,0);
	int strokeSize = 1;
	
	/** 
     * The constructor of a TextBox, creates the shape that contains the text.
     * @param initText The text of the text box.
     * @param firstPoint The first point of the text box.
     * @param secondPoint The second point of the text box.
     */
	TextBox(String initText, Point firstPoint, Point secondPoint){
		text = initText;
		bounds = new ShapeBuilder("rect",firstPoint,secondPoint);
	}
	
	/** 
     * Finds the Processing call for the text box and its styling.
     * @return String The Processing text. 
     */
	public String getProcessingLine() {
		return "fill(" + stroke.getRed() + ", " + stroke.getGreen() + ", " + stroke.getBlue() + ");\n\ttextSize(" + strokeSize*10 + ");\n\ttext(\"" + text + "\", " + bounds.javaShape.getBounds().x + ", " + (bounds.javaShape.getBounds().y) +");";
	}
	
	/** 
     * Finds the Processing call for the text box without its styling.
     * @return String The Processing text. 
     */
	public String getProcessingText() {
		return "text(\"" + text + "\", " + bounds.javaShape.getBounds().x + ", " + (bounds.javaShape.getBounds().y) +");";
	}
}
