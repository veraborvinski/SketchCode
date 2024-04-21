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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

/** 
* The ShapeGroup class stores grouped shapes and the Processing call and body for their shared class.
* 
* @author Vera Borvinski
*/
public class ShapeGroup {
	String classCall;
	String classBody;
	String name;
	ArrayList<ShapeBuilder> shapes = new ArrayList<ShapeBuilder>();
	
	/** 
     * The constructor of a ShapeGroup, it creates the class call, and class body based on the submitted shapes.
     * @param initShapes The shapes to add to the class.
     * @param className The name of the class 
     */
	ShapeGroup(ArrayList<ShapeBuilder> initShapes, String className){
		shapes.addAll(initShapes);
		classCall = className + " " + className.toLowerCase() + " = new " + className + "();";
		name = className;
		classBody = "class " + className + "{\n" + "\t" + className + "(){\n";
		
		for (int i = 0; i < shapes.size(); i++) {
			if (i == 0) {
				classBody = classBody + "\t\t" + shapes.get(i).getProcessingFill() + "\n";
				classBody = classBody + "\t\t" + shapes.get(i).getProcessingStroke() + "\n";
				classBody = classBody + "\t\t" + shapes.get(i).getProcessingStrokeSize() + "\n";
			} else {
				if (shapes.get(i).getProcessingFill() != shapes.get(i-1).getProcessingFill()) {
					classBody = classBody + "\t\t" + shapes.get(i).getProcessingFill() + "\n";
				}
				
				if (shapes.get(i).getProcessingStroke() != shapes.get(i-1).getProcessingStroke()) {
					classBody = classBody + "\t\t" + shapes.get(i).getProcessingStroke() + "\n";
				}
				
				if (shapes.get(i).getProcessingStrokeSize() != shapes.get(i-1).getProcessingStrokeSize()) {
					classBody = classBody + "\t\t" + shapes.get(i).getProcessingStrokeSize() + "\n";
				}
			}
			
			if (shapes.get(i).rotation != 0) {
				//change to get from panel later
				classBody = classBody + "\t\ttranslate(200,200);\n";
				classBody = classBody + "\t\t" + shapes.get(i).getProcessingRotate() + "\n";
				classBody = classBody + "\t\t" + shapes.get(i).processingShape + "\n";
				classBody = classBody + "\t\t" + shapes.get(i).getReverseProcessingRotate() + "\n";
				classBody = classBody + "\t\ttranslate(-200,-200);\n";
			} else {
				classBody = classBody + "\t\t" + shapes.get(i).processingShape + "\n";
			}
		}
		
		classBody = classBody + "\t}\n" + "}";
	}
}
