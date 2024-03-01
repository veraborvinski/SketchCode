package template.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import processing.app.Base;

public class ShapeGroup {
	String classCall;
	String classBody;
	ArrayList<ShapeBuilder> shapes;
	
	ShapeGroup(ArrayList<ShapeBuilder> initShapes, String className){
		shapes = initShapes;
		classCall = className + " " + className.toLowerCase() + " = new " + className + "();";
		
		classBody = "class " + className + "{\n" + "\t" + className + "(){\n";
		
		for (ShapeBuilder s: shapes) {
			classBody = classBody + "\t\t" + s.processingShape + "\n";
		}
		
		classBody = classBody + "\t}\n" + "}";
	}
}
