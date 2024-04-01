package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

public class ShapeGroup {
	String classCall;
	String classBody;
	String name;
	ArrayList<ShapeBuilder> shapes = new ArrayList<ShapeBuilder>();
	
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
