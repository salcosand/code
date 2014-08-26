package com.drawing.datastructure;

import java.util.UUID;

import android.graphics.Matrix;
import android.graphics.Path;

import com.drawing.application.MainActivity;
import com.drawing.gestures.Point;
import com.drawing.ontosketch.R;

/**
 * Leaf component of the composite object structure
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * @author Christian Br�ndel TU Dresden / SAP Research Dresden
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
	public void updatePath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight) {

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
	public void redrawPathsafterDeserialization(MainActivity ma)
	{

		boolean first = true;

		CustomPath tempPath = new CustomPath();

		if (path != null) 
		{
			if ((path.getOntoType() == OntologyObjectTypes.FORMALIZEDCONCEPT) ||
					(path.getOntoType() == OntologyObjectTypes.FORMALIZEDINDIVIDUAL) ||
					(path.getOntoType() == OntologyObjectTypes.DRAWNINDIVIDUAL) ||
					(path.getOntoType() == OntologyObjectTypes.DRAWNCONCEPT))
			{

				for (Point point : path.getVertices()) 
				{
					if (first) 
					{
						tempPath.moveTo(point.x, point.y);
						first = false;
					}
	
					tempPath.lineTo(point.x, point.y);
				}
			}
			else if ((path.getOntoType() == OntologyObjectTypes.DRAWNPROPERTYRELATION))
			{
				Point p = path.getVertices().get(0);
				
				float radius = (float) ma.getResources().getIntArray(R.array.sizes)[0];
				
				tempPath.moveTo(p.x, p.y);
				tempPath.addCircle(p.x, p.y, (float) radius, Path.Direction.CW);
				
				Point pathPoint = new Point(p.x, p.y-radius); 

				tempPath.moveTo((float)(pathPoint.x-(0.2*radius)), pathPoint.y);
				
				tempPath.lineTo(pathPoint.x+1*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+2*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+3*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+4*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+5*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+6*radius, pathPoint.y);
				tempPath.lineTo(pathPoint.x+7*radius, pathPoint.y);
				
				tempPath.lineTo(pathPoint.x+7*radius, pathPoint.y+2*radius);
				
				tempPath.lineTo(pathPoint.x+6*radius, pathPoint.y+2*radius);
				tempPath.lineTo(pathPoint.x+5*radius, pathPoint.y+2*radius);
				tempPath.lineTo(pathPoint.x+4*radius, pathPoint.y+2*radius);
				tempPath.lineTo(pathPoint.x+3*radius, pathPoint.y+2*radius);
				tempPath.lineTo(pathPoint.x+2*radius, pathPoint.y+2*radius);
				tempPath.lineTo(pathPoint.x+1*radius, pathPoint.y+2*radius);
				tempPath.lineTo((float)(pathPoint.x-(0.2*radius)), pathPoint.y+2*radius);

				tempPath.close();
			}
			else if ((path.getOntoType() == OntologyObjectTypes.FORMALIZEDPROPERTYRELATION)||
					(path.getOntoType() == OntologyObjectTypes.FORMALIZEDINSTANTIATION) ||
					(path.getOntoType() == OntologyObjectTypes.FORMALIZEDSUBCONCEPTRELATION)||
					(path.getOntoType() == OntologyObjectTypes.DRAWNINSTANTIATION) ||
					(path.getOntoType() == OntologyObjectTypes.DRAWNSUBCONCEPTRELATION))
			{		
				if (path.getVertices().size() > 0)
				{
					Point p = path.getVertices().get(0);
					
					tempPath.moveTo(p.x, p.y);
					tempPath.addCircle(p.x, p.y, (float) ma.getResources().getIntArray(R.array.sizes)[0], Path.Direction.CW);
				}

			}
			else
			{

				for (Point point : path.getVertices())
				{

					if (first)
					{
						tempPath.moveTo(point.x, point.y);
						first = false;
					}
					else
					{
						tempPath.lineTo(point.x, point.y);
					}
				}
				
			}
				
			
			tempPath.setOntoType(path.getOntoType());
			tempPath.setHighlighted(path.isHighlighted());
			tempPath.setType(path.getGestureType());
			tempPath.setColor(path.color);
			tempPath.setVisible(path.isVisible());

			path.set(tempPath);

		}
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#ContainsFormalizedConceptObjects()
	 */
	public boolean ContainsFormalizedConceptObjects() {

		if (this instanceof FormalizedConcept)
			return true;

		return false;
	}

	/**
	 * @see com.drawing.datastructure.DrawingComponent#ContainsFormalizedIndividualObjects()
	 */
	public boolean ContainsFormalizedIndividualObjects() {

		if (this instanceof FormalizedIndividual)
			return true;

		return false;
	}

}
