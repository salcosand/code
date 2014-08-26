package com.drawing.datastructure;

import java.util.ArrayList;

import android.graphics.Matrix;

/**
 * Composite container class without an optical representation that encapsulates
 * <b>DrawingCompositeLetter</b> objects
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */
public class DrawingCompositeWord extends DrawingComposite {

	/**
	 * Serial version UID
	 */
	 private static final long serialVersionUID = -5696530248227643663L;
	 
	static CustomPath path = new CustomPath();
	// static float[] pathMatrixValues = new float[9];

	private String result;

	/**
	 * Getter for the encapsulated word of this {@link DrawingCompositeWord}
	 * @return The word that is represented by this object
	 */
	public String getResult() {
		return result;
	}

	private static Matrix matrix = new Matrix();

	/**
	 * Constructor of <b>DrawingCompositeWord</b>
	 * 
	 * @param children
	 *            A list of <b>DrawingComponent</b> child elements that will be
	 *            attached to this component
	 * @param result
	 *            True if the object was created successfully
	 */
	public DrawingCompositeWord(ArrayList<DrawingComponent> children,
			String result) {

		super(path, matrix);

		this.children = children;
		this.result = result;

		try {

			for (DrawingComponent child : children) {
				((DrawingWordLetter) child).setParent(this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#updatePath(android.graphics.Matrix,
	 *      android.graphics.Matrix)
	 */
	public void updatePath(Matrix matrix, Matrix backupTransformationMatrix,
			boolean highlight) {

		if(!grouped)
		{
		
		applyTransformationAndHighlight(matrix, backupTransformationMatrix, highlight);
		
		}
		else
		{
			if(parent != null)
			{
				parent.updatePath(matrix, backupTransformationMatrix, highlight);
			}
		}
		
	}
	
	/**
	 * @see com.drawing.datastructure.DrawingComposite#updateGroupedPath(android.graphics.Matrix, android.graphics.Matrix, boolean)
	 */
	public void updateGroupedPath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight)
	{
		
		applyTransformationAndHighlight(matrix, backupTransformationMatrix, highlight);
		
	}
	
	 
	/**
	 * Method that actually applies transformation and highlight upon the {@link DrawingCompositeWord} object and its children
	 * @param matrix The current transformation matrix of the canvas
	 * @param backupTransformationMatrix The current backupMatrix of the canvas for temporary transformations

	 * @param highlight True if the respective component should be highlighted

	 */
	public void applyTransformationAndHighlight(Matrix matrix, Matrix backupTransformationMatrix,
			boolean highlight)
	{
		
		if (this.highlighted != highlight) {

			changed = true;

				if (highlight) {
					CustomPath tempPath;


					
					for (DrawingComponent child : children) {
						tempPath = child.getPath();

						tempPath.applyTransformation(
								backupTransformationMatrix, matrix);

						child.setPath(tempPath);

						child.setHighlighted(highlight);

					}

					this.highlighted = highlight;

				}

				else

				{

					CustomPath tempPath = new CustomPath();

					if(annotation != null)
					{
						for(CustomPath annotationPath : getAnnotationPathList())
						{
							annotationPath.applyTransformation(matrix, backupTransformationMatrix);
						}
					}
					
					for (DrawingComponent child : children) {
						// Caution: the retransformation of a highlighted object
						// differs from the previously applied transformation

						tempPath = child.getPath();

						tempPath.applyTransformation(matrix,
								backupTransformationMatrix);

						child.setPath(tempPath);

						child.setHighlighted(highlight);
					}

					this.highlighted = highlight;

				}

			
		}
		
	}
	
	/**
	 * @see com.drawing.datastructure.DrawingComponent#setGrouped(boolean)
	 */
	public void setGrouped(boolean grouped) {

		this.grouped = grouped;

		for (DrawingComponent child : children) {
			child.setGrouped(grouped);
		}

	}

}
