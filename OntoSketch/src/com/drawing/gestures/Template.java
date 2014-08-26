package com.drawing.gestures;

import java.util.List;

import com.drawing.datastructure.GestureTypes;

/**
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class Template {
	  String Name;
	  List<Point> Points;
	  
		int NumPoints = 64;
		float SquareSize = 250.0f;
		
		public GestureTypes Type;
	  
	  /**
	   * The template for gesture recognition
	 * @param name The name of the template
	 * @param points The array of <b>Point</b> objects belonging to the defined template
	 */
	public Template( String name, List<Point> points, GestureTypes type)
	  {
	    Name = name;
	    Points = Operations.Resample( points, NumPoints);
	    Points = Operations.RotateToZero( Points );
	    Points = Operations.ScaleToSquare( Points, SquareSize);
	    Points = Operations.TranslateToOrigin( Points );
	    Type = type;
	  }
	  
	  
	  
	}
