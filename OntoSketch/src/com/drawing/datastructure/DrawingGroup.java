package com.drawing.datastructure;

import java.util.ArrayList;

import android.graphics.Matrix;

public class DrawingGroup extends DrawingComposite {

//	private static CustomPath path = new CustomPath();
//
//	private static Matrix matrix = new Matrix();

	public DrawingGroup(ArrayList<DrawingComponent> children, CustomPath path, Matrix matrix) {
		
		super(path, matrix);

		this.children = children;

		try {

			for (DrawingComponent child : children) {

				child.setParent(this);

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		highlighted = true;
		
		leafState = true;

	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#updatePath(android.graphics.Matrix,
	 *      android.graphics.Matrix)
	 */
	public void updatePath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight) {

		changed = true;

		if (this.highlighted != highlight)
		{
			if (!this.highlighted)
			{
				this.highlighted = true;

			}
			else
			{
				this.highlighted = false;
			}

			for (DrawingComponent child : children)
			{
				if (child.isGrouped())
				{
					child.updateGroupedPath(matrix, backupTransformationMatrix, highlight);

				}

			}
		}

	}

	/**
	 * @see com.drawing.datastructure.DrawingComposite#setHighlighted(boolean)
	 */
	public void setHighlighted(boolean highlighted)
	{
		this.highlighted = highlighted;

		for (DrawingComponent child : children)
		{
			child.setHighlighted(highlighted);
		}

	}

}
