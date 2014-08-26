package com.drawing.datastructure;

import android.graphics.Matrix;

/**
 * Formalized Component for a InstantiationRelation
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */
public class InstatiationRelation extends CustomObjectRelation
{

	private static final long serialVersionUID = 7284888900894995962L;

	public InstatiationRelation(CustomPath path, Matrix pathMatrix, DrawingComponent startElement, DrawingComponent endElement)
	{
		super(path, pathMatrix, startElement, endElement);
		
	}

	public void updateHelpText()
	{
		if (endElement instanceof DrawingIndividual)
		{
			if (!startElement.getItemText().equalsIgnoreCase("")) endElement.setHelpText("of type " + startElement.getItemText());
			else endElement.setHelpText("of type EMPTY CONCEPT");
		}
		else
		{
			if (!endElement.getItemText().equalsIgnoreCase("")) startElement.setHelpText("of type " + endElement.getItemText());
			else startElement.setHelpText("of type EMPTY CONCEPT");
		}
	}

	public void resetHelpText()
	{
		endElement.setHelpText("");
	}

}
