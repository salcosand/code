package com.drawing.datastructure;

import java.util.ArrayList;
import java.util.UUID;

import android.graphics.Matrix;

import com.drawing.application.FingerDrawingActivity;
import com.drawing.gestures.Point;

/**
 * Leaf component of the composite object structure
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */
public class DrawingLeaf extends DrawingComponent {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3959048327911389993L;

	/**
	 * Constructor of the leaf component of the composite object structure
	 * 
	 * @param path
	 *            The path object that visually represents this component
	 * @param pathMatrix
	 *            The transformation matrix that belongs to the respective path
	 */
	public DrawingLeaf(CustomPath path, Matrix pathMatrix) {

		super(path, pathMatrix);

		isComposite = false;

	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getComponentCount()
	 */
	public int getComponentCount() {

		return 1;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getPath()
	 */
	public CustomPath getPath() {

		return path;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getAnnotation()
	 */
	public DrawingComponent getAnnotation() {

		return annotation;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getPathList()
	 */
	public ScaledPathArray getPathList() {

		ScaledPathArray pathArray = new ScaledPathArray();
		pathArray.getPaths().add(path);
		Matrix matrix = new Matrix();
		matrix.setValues(pathMatrixValues);
		pathArray.getScales().add(matrix);

		return pathArray;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getAnnotationList()
	 */
	public ArrayList<DrawingComponent> getAnnotationList() {

		ArrayList<DrawingComponent> annotationList = new ArrayList<DrawingComponent>();

		if(annotation != null)
		{
			annotationList.addAll(((DrawingCompositeWord)annotation).children);
		}
		
		return annotationList;

	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getObjectByPathId(java.util.UUID)
	 */
	public DrawingComponent getObjectByPathId(UUID uid) {

		if (path.getUid().compareTo(uid) == 0) {
			return this;
		} else
			return null;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#addComponent(com.drawing.datastructure.DrawingComponent)
	 */
	public void addComponent(DrawingComponent component) {
		return;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#updatePath(android.graphics.Matrix,
	 *      android.graphics.Matrix)
	 */
	public void updatePath(Matrix matrix, Matrix backupTransformationMatrix,
			boolean highlight) {

		if (grouped) {

			if (parent != null) {
				parent.updatePath(matrix, backupTransformationMatrix, highlight);
			}

		} else {

			if (path != null) {
				if (!path.isHighlighted()) {

					path.setHighlighted(true);

					path.applyTransformation(backupTransformationMatrix, matrix);

					this.setHighlighted(true);

				} else {

					path.setHighlighted(false);

					if(annotation != null)
					{
						for(CustomPath annotationPath : getAnnotationPathList())
						{
							annotationPath.applyTransformation(matrix, backupTransformationMatrix);
						}
					}
					
					path.applyTransformation(matrix, backupTransformationMatrix);

					this.setHighlighted(false);

				}
			}
		}

	}
	
	/**
	 * @see com.drawing.datastructure.DrawingComponent#updateGroupedPath(android.graphics.Matrix, android.graphics.Matrix, boolean)
	 */
	public void updateGroupedPath(Matrix matrix,
			Matrix backupTransformationMatrix, boolean highlight) {

		if (path != null && grouped) {
			
			if (!path.isHighlighted()) {

				path.setHighlighted(true);

				path.applyTransformation(backupTransformationMatrix, matrix);

				this.setHighlighted(true);

			} else {

				path.setHighlighted(false);

				path.applyTransformation(matrix, backupTransformationMatrix);

				this.setHighlighted(false);

			}
		}
		
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#redrawPathsafterDeserialization()
	 */
	public void redrawPathsafterDeserialization(FingerDrawingActivity fda) {

		// TODO: figure out why general composite objects are not drawn after
		// deserialization

		boolean first = true;

		CustomPath tempPath = new CustomPath();
		CustomPath tempAnnotationPath;

		for (Point point : path.getVertices()) {

			if (first) {
				tempPath.moveTo(point.x, point.y);
				first = false;
			}

			tempPath.lineTo(point.x, point.y);

		}
		
		//redraw annotations
		if(annotation != null)
		for(DrawingComponent annotationComponent : ((DrawingComposite)annotation).children)
		{
			
			tempAnnotationPath = new CustomPath();
			first = true;
			
		for(Point point : annotationComponent.path.getVertices())
		{
			if (first) {
				tempAnnotationPath.moveTo(point.x, point.y);
				first = false;
			}

			tempAnnotationPath.lineTo(point.x, point.y);
		}
		
		annotationComponent.path.set(tempAnnotationPath);
			
		}

		tempPath.setHighlighted(path.isHighlighted());
		tempPath.setType(path.getType());

		path.set(tempPath);

	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#ContainsContactObjects()
	 */
	public boolean ContainsContactObjects() {

		if (this instanceof PersonObject)
			return true;

		return false;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#getAnnotationPathList()
	 */
	public ArrayList<CustomPath> getAnnotationPathList() {
		
		ArrayList<CustomPath> annotationPaths = new ArrayList<CustomPath>();
	

		if(annotation != null)
		{
		//add all letters of the annotation of this object to the list
		for(DrawingComponent child : ((DrawingCompositeWord)annotation).children )
		{
			annotationPaths.add(child.path);
		}
		}
		
		return annotationPaths;
	}



}
