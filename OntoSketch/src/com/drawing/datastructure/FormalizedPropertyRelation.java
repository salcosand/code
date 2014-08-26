package com.drawing.datastructure;

import android.graphics.Matrix;

/**
 * Formalized Component for a FormalizedPropertyRelation
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */
public class FormalizedPropertyRelation extends PropertyRelation
{
	private static final long serialVersionUID = -5815212642098766909L;

	public FormalizedPropertyRelation(CustomPath path, Matrix pathMatrix, DrawingComponent startElement, DrawingComponent endElement) 
	{
		super(path, pathMatrix, startElement, endElement);
	}

}
