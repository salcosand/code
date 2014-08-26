package com.drawing.datastructure;

import android.graphics.Matrix;

/**
 * Atomar letter element of a composite word Container
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class DrawingWordLetter extends DrawingLeaf
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -7650083221846791892L;

	/**
	 * Object that represents a drawn stroke within a recognized word
	 * @param path The respective path describing the visual representation
	 * @param pathMatrix The respective transformation matrix
	 */
	public DrawingWordLetter(CustomPath path, Matrix pathMatrix)
	{
		super(path, pathMatrix);
	}

	
	/**
	 * @see com.drawing.datastructure.DrawingComponent#setHighlighted(boolean)
	 */
	public void setHighlighted(boolean highlighted)
	{
		this.highlighted = highlighted;
		this.path.setHighlighted(highlighted);
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#updatePath(android.graphics.Matrix, android.graphics.Matrix)
	 */
	public void updatePath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight)
	{		
		parent.updatePath(matrix, backupTransformationMatrix, highlight);
	}

}
