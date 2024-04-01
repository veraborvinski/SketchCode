package template.tool;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import processing.app.Base;

public class TextBox {
	String text = "";
	ShapeBuilder bounds;
	
	Color stroke = new Color(0,0,0);
	int strokeSize = 1;
	
	TextBox(String initText, Point firstPoint, Point secondPoint){
		text = initText;
		bounds = new ShapeBuilder("rect",firstPoint,secondPoint);
	}
	
	String getProcessingLine() {
		return "fill(" + stroke.getRed() + ", " + stroke.getGreen() + ", " + stroke.getBlue() + ");\n\ttextSize(" + strokeSize*10 + ");\n\ttext(\"Hello World\", " + bounds.javaShape.getBounds().x + ", " + (bounds.javaShape.getBounds().y) +");";
	}
	
	String getProcessingText() {
		return "text(\"Hello World\", " + bounds.javaShape.getBounds().x + ", " + (bounds.javaShape.getBounds().y) +");";
	}
}
