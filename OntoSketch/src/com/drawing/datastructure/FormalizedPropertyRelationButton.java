package com.drawing.datastructure;

import android.graphics.Matrix;

/**
 * Formalized Component for a FormalizedPropertyRelationButton
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */
public class FormalizedPropertyRelationButton extends PropertyRelation 
{

	private static final long serialVersionUID = 1666730344048278030L;
	
	private DrawingPropertyRelation parentRelation;

	public FormalizedPropertyRelationButton(CustomPath path, Matrix pathMatrix, DrawingComponent startElement, DrawingComponent endElement)
	{
		super(path, pathMatrix, startElement, endElement);
		
		isOpen = true;
	}

	@Override
	public void setOpen(boolean b)
	{
		isOpen = true;
	}

	public DrawingPropertyRelation getParentRelation()
	{
		return parentRelation;
	}

	public void setParentRelation(DrawingPropertyRelation parentRelation)
	{
		this.parentRelation = parentRelation;
	}

}
