//package com.drawing.datastructure;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import android.graphics.Matrix;
//
//import com.drawing.application.FingerDrawingActivity;
//import com.drawing.gestures.Point;
//
///**
// * abstract component class for the composite object structure
// * 
// * @author Christian Brändel TU Dresden / SAP Research Dresden
// * 
// */
//
//public abstract class DrawingComponent implements Serializable{
//
//	/**
//	 * Serial version UID
//	 */
//	private static final long serialVersionUID = -2462398927131280380L;
//	
//	/**
//	 * The object that is above this one in the hierarchy tree
//	 */
//	protected DrawingComposite parent = null;
//
//	/**
//	 * The component class of the composite object structure
//	 * 
//	 * @param path
//	 *            The respective path object that belongs to the component this
//	 *            should only be null if the component is the root element of
//	 *            the structure
//	 * @param pathMatrix
//	 *            The respective transformation matrix that belongs to the path
//	 *            object
//	 */
//	public DrawingComponent(CustomPath path, Matrix pathMatrix) {
//		if (path != null) {
//			List<Point> points = CustomPath.transformVertices(path.getVertices(),
//					pathMatrix);
//			path.setVertices(points);
//			
//			path.setHighlighted(false);
//		}
//
//		this.path = path;
//
//		if(pathMatrix == null)
//			pathMatrix = new Matrix();
//			
//		this.pathMatrixValues = new float[9];
//		pathMatrix.getValues(this.pathMatrixValues);
//		
//		
//		
//		highlighted = false;
//		
//		grouped = false;
//
//	}
//
//	protected CustomPath path;
//
//	protected DrawingComponent annotation;
//
//	protected float[] pathMatrixValues;
//
//	protected float[] annotationMatrixValues;
//
//	protected boolean highlighted;
//	
//	protected boolean grouped;
//	
//	/**
//	 * Getter for the composite property of the component
//	 */
//	public boolean isComposite;
//
//	/**
//	 * Getter that returns true if the component is highlighted
//	 * 
//	 * @return true if the component is highlighted
//	 */
//	public boolean isHighlighted() {
//		return highlighted;
//	}
//
//	protected boolean isRoot = false;
//	
//	/**
//	 * True if this component is the root node of the object tree
//	 * @param value true if this component is the root node
//	 */
//	public void setIsRoot(boolean value)
//	{
//		isRoot = value;
//	}
//	
//	/**
//	 * Setter for the highlighted property of the component
//	 * 
//	 * @param highlighted
//	 *            true if the component should be highlighted
//	 */
//	public void setHighlighted(boolean highlighted) {
//		if(!grouped)
//		{
//		this.highlighted = highlighted;
//		this.path.setHighlighted(highlighted);
//		}
//		else
//		{
//			if(parent != null)
//			{
//				this.highlighted = highlighted;
//				this.path.setHighlighted(highlighted);
//				
//				if(parent.isHighlighted() != highlighted)
//				parent.setHighlighted(highlighted);
//			}
//		}
//	}
//
//	public boolean isGrouped() {
//		return grouped;
//	}
//
//	public void setGrouped(boolean grouped) {
//		this.grouped = grouped;
//	}
//
//	/**
//	 * Getter for the count of objects included
//	 * 
//	 * @return The number of included objects
//	 */
//	public abstract int getComponentCount();
//
//	/**
//	 * Getter for the <b>CustomPath</b> object of the component
//	 * 
//	 * @return The <b>CustomPath</b> object of the component
//	 */
//	public CustomPath getPath() {
//		return path;
//	}
//
//	/**
//	 * Setter for the <b>CustomPath</b> object of the component
//	 * 
//	 * @param path
//	 *            The <b>CustomPath</b> object that should be attached to this
//	 *            component
//	 */
//	public void setPath(CustomPath path) {
//		this.path = path;
//		
//		if(path.isHighlighted())
//		{
//			setHighlighted(true);
//		}else
//		{
//			setHighlighted(false);
//		}
//	}
//
//	/**
//	 * Getter for the <b>CustomPath</b> annotation of the component
//	 * 
//	 * @return The <b>CustomPath</b> annotation of the component
//	 */
//	public DrawingComponent getAnnotation()
//	{
//		return this.annotation;
//	}
//	
//	/**
//	 * Setter for the <b>CustomPath</b> annotation of the component
//	 * 
//	 * @param annotation The <b>CustomPath</b> annotation of the component
//	 */
//	public void setAnnotation(DrawingComponent annotation)
//	{
//		this.annotation = annotation;
//	}
//
//	/**
//	 * Getter for the list of paths of this and the encapsulated components
//	 * 
//	 * @return The list of paths of this and the encapsulated components
//	 */
//	public abstract ScaledPathArray getPathList();
//
//	/**
//	 * Getter for the list of annotations of this and the encapsulated
//	 * components
//	 * 
//	 * @return The list of annotations of this and the encapsulated components
//	 */
//	public abstract ArrayList<DrawingComponent> getAnnotationList();
//
//	
//	/**
//	 * Getter for the list of annotation paths of this and the encapsulated components
//	 * @return The list of annotation paths of this and the encapsulated components
//	 */
//	public abstract ArrayList<CustomPath> getAnnotationPathList();
//	
//	/**
//	 * Retrieve a <b>DrawingComponent</b> by the respective <b>UUID</b>
//	 * 
//	 * @param uid
//	 *            Unique identifier of a <b>CustomPath</b> object
//	 * @return The <b>DrawingComponent</b> object that contains the path with
//	 *         the respective <b>UUID</b>
//	 */
//	public abstract DrawingComponent getObjectByPathId(UUID uid);
//
//	/**
//	 * Integrate a new component into the tree structure
//	 * 
//	 * @param component
//	 *            The <b>DrawingComponent</b> object that should be attached
//	 * @return true if this component is a <b>DrawingLeaf</b> and the respective
//	 *         <b>DrawingComponent</b> should be attached to the direct parent
//	 *         object
//	 */
//	public abstract void addComponent(DrawingComponent component);
//
//	/**
//	 * Remove the respective component from the data structure
//	 * @param component The component that shall be removed
//	 */
//	public void removeComponent(DrawingComponent component) {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	/**
//	 * Remove the respective composite component and its children from the data structure
//	 * @param component The composite component that shall be removed
//	 */
//	public void removeCompositeComponent(DrawingComponent component)
//	{
//		
//	}
//	
//	/**
//	 * Update the path according to current transformations and manage the highlighting
//	 * @param matrix The current transformation matrix of the canvas
//	 * @param backupTransformationMatrix The current backupMatrix of the canvas for temporary transformations
//	 * @param highlight true if the respective component should be highlighted
//	 */
//	public abstract void updatePath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight); 
//	
//
//	/**
//	 * Update a grouped path according to current transformations and manage the highlighting
//	 * @param matrix The current transformation matrix of the canvas
//	 * @param backupTransformationMatrix The current backupMatrix of the canvas for temporary transformations
//	 * @param highlight true if the respective component should be highlighted
//	 */
//	public abstract void updateGroupedPath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight);
//	
//
//	/**
//	 * 	All paths need to be redrawn due to an deserialization bug of the original Path object 
//	 *  of Android, because otherwise nothing would be drawn on the canvas 
//	 */
//	public abstract void redrawPathsafterDeserialization(FingerDrawingActivity fda);
//	
//
//	/**
//	 * Determine the maximum extension of the paths drawn in order to translate the view port of the canvas
//	 * in a way that every drawn path is included
//	 * @return The array containing <b>[minX, maxX, minY, maxY]</b>
//	 */
//	public float[] determineDrawnExtrema(float[] extrema)
//	{
//		
//		if(path != null)
//		{
//			
//			if(path.minX < extrema[0])
//				extrema[0]=path.minX;
//			
//			if(path.maxX > extrema[1])
//				extrema[1] = path.maxX;
//			
//			if(path.minY < extrema[2])
//				extrema[2]=path.minY;
//			
//			if(path.maxY > extrema[3])
//				extrema[3]=path.maxY;
//		
//			
//		}
//						
//		return extrema;
//		
//	}
//	
//	/**
//	 * Method that validates to true, if the component structure contains an element of type PersonObject
//	 * @return true if the component structure contains an element of type PersonObject
//	 */
//	public abstract boolean ContainsContactObjects();
//
//	/**
//	 * Getter for the parent property of this element
//	 * @return The parent object of this element or null if none has been defined
//	 */
//	public DrawingComposite getParent() {
//		return parent;
//	}
//
//	/**
//	 * Setter for the parent property of this element
//	 * @param parent The parent object of this element
//	 */
//	public void setParent(DrawingComposite parent) {
//		this.parent = parent;
//	}
//	
//}

package com.drawing.datastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.graphics.Matrix;

import com.drawing.application.FingerDrawingActivity;
import com.drawing.gestures.Point;

/**
 * abstract component class for the composite object structure
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */

public abstract class DrawingComponent implements Serializable{

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -2462398927131280380L;
	
	/**
	 * The object that is above this one in the hierarchy tree
	 */
	protected DrawingComposite parent = null;

	/**
	 * The component class of the composite object structure
	 * 
	 * @param path
	 *            The respective path object that belongs to the component this
	 *            should only be null if the component is the root element of
	 *            the structure
	 * @param pathMatrix
	 *            The respective transformation matrix that belongs to the path
	 *            object
	 */
	public DrawingComponent(CustomPath path, Matrix pathMatrix) {
		if (path != null) {
			List<Point> points = CustomPath.transformVertices(path.getVertices(),
					pathMatrix);
			path.setVertices(points);
			
			path.setHighlighted(false);
		}

		this.path = path;

		if(pathMatrix == null)
			pathMatrix = new Matrix();
			
		this.pathMatrixValues = new float[9];
		pathMatrix.getValues(this.pathMatrixValues);
		
		
		
		highlighted = false;
		
		grouped = false;

	}

	protected CustomPath path;

	protected DrawingComponent annotation;

	protected float[] pathMatrixValues;

	protected float[] annotationMatrixValues;

	protected boolean highlighted;
	
	protected boolean grouped;
	
	/**
	 * Getter for the composite property of the component
	 */
	public boolean isComposite;

	/**
	 * Getter that returns true if the component is highlighted
	 * 
	 * @return true if the component is highlighted
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	protected boolean isRoot = false;
	
	/**
	 * True if this component is the root node of the object tree
	 * @param value true if this component is the root node
	 */
	public void setIsRoot(boolean value)
	{
		isRoot = value;
	}
	
	/**
	 * Setter for the highlighted property of the component
	 * 
	 * @param highlighted
	 *            true if the component should be highlighted
	 */
	public void setHighlighted(boolean highlighted) {
		if(!grouped)
		{
		this.highlighted = highlighted;
		this.path.setHighlighted(highlighted);
		}
		else
		{
			if(parent != null)
			{
				this.highlighted = highlighted;
				this.path.setHighlighted(highlighted);
				
				if(parent.isHighlighted() != highlighted)
				parent.setHighlighted(highlighted);
			}
		}
	}

	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	/**
	 * Getter for the count of objects included
	 * 
	 * @return The number of included objects
	 */
	public abstract int getComponentCount();

	/**
	 * Getter for the <b>CustomPath</b> object of the component
	 * 
	 * @return The <b>CustomPath</b> object of the component
	 */
	public CustomPath getPath() {
		return path;
	}

	/**
	 * Setter for the <b>CustomPath</b> object of the component
	 * 
	 * @param path
	 *            The <b>CustomPath</b> object that should be attached to this
	 *            component
	 */
	public void setPath(CustomPath path) {
		this.path = path;
		
		if(path.isHighlighted())
		{
			setHighlighted(true);
		}else
		{
			setHighlighted(false);
		}
	}

	/**
	 * Getter for the <b>CustomPath</b> annotation of the component
	 * 
	 * @return The <b>CustomPath</b> annotation of the component
	 */
	public DrawingComponent getAnnotation()
	{
		return this.annotation;
	}
	
	/**
	 * Setter for the <b>CustomPath</b> annotation of the component
	 * 
	 * @param annotation The <b>CustomPath</b> annotation of the component
	 */
	public void setAnnotation(DrawingComponent annotation)
	{
		this.annotation = annotation;
	}

	/**
	 * Getter for the list of paths of this and the encapsulated components
	 * 
	 * @return The list of paths of this and the encapsulated components
	 */
	public abstract ScaledPathArray getPathList();

	/**
	 * Getter for the list of annotations of this and the encapsulated
	 * components
	 * 
	 * @return The list of annotations of this and the encapsulated components
	 */
	public abstract ArrayList<DrawingComponent> getAnnotationList();

	
	/**
	 * Getter for the list of annotation paths of this and the encapsulated components
	 * @return The list of annotation paths of this and the encapsulated components
	 */
	public abstract ArrayList<CustomPath> getAnnotationPathList();
	
	/**
	 * Retrieve a <b>DrawingComponent</b> by the respective <b>UUID</b>
	 * 
	 * @param uid
	 *            Unique identifier of a <b>CustomPath</b> object
	 * @return The <b>DrawingComponent</b> object that contains the path with
	 *         the respective <b>UUID</b>
	 */
	public abstract DrawingComponent getObjectByPathId(UUID uid);

	/**
	 * Integrate a new component into the tree structure
	 * 
	 * @param component
	 *            The <b>DrawingComponent</b> object that should be attached
	 * @return true if this component is a <b>DrawingLeaf</b> and the respective
	 *         <b>DrawingComponent</b> should be attached to the direct parent
	 *         object
	 */
	public abstract void addComponent(DrawingComponent component);

	/**
	 * Remove the respective component from the data structure
	 * @param component The component that shall be removed
	 */
	public void removeComponent(DrawingComponent component) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Remove the respective composite component and its children from the data structure
	 * @param component The composite component that shall be removed
	 */
	public void removeCompositeComponent(DrawingComponent component)
	{
		
	}
	
	/**
	 * Update the path according to current transformations and manage the highlighting
	 * @param matrix The current transformation matrix of the canvas
	 * @param backupTransformationMatrix The current backupMatrix of the canvas for temporary transformations
	 * @param highlight true if the respective component should be highlighted
	 */
	public abstract void updatePath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight); 
	

	/**
	 * Update a grouped path according to current transformations and manage the highlighting
	 * @param matrix The current transformation matrix of the canvas
	 * @param backupTransformationMatrix The current backupMatrix of the canvas for temporary transformations
	 * @param highlight true if the respective component should be highlighted
	 */
	public abstract void updateGroupedPath(Matrix matrix, Matrix backupTransformationMatrix, boolean highlight);
	

	/**
	 * 	All paths need to be redrawn due to an deserialization bug of the original Path object 
	 *  of Android, because otherwise nothing would be drawn on the canvas 
	 */
	public abstract void redrawPathsafterDeserialization(FingerDrawingActivity fda);
	

	/**
	 * Determine the maximum extension of the paths drawn in order to translate the view port of the canvas
	 * in a way that every drawn path is included
	 * @return The array containing <b>[minX, maxX, minY, maxY]</b>
	 */
	public float[] determineDrawnExtrema(float[] extrema)
	{
		
		if(path != null)
		{
			
			if(path.minX < extrema[0])
				extrema[0]=path.minX;
			
			if(path.maxX > extrema[1])
				extrema[1] = path.maxX;
			
			if(path.minY < extrema[2])
				extrema[2]=path.minY;
			
			if(path.maxY > extrema[3])
				extrema[3]=path.maxY;
		
			
		}
						
		return extrema;
		
	}
	
	/**
	 * Method that validates to true, if the component structure contains an element of type PersonObject
	 * @return true if the component structure contains an element of type PersonObject
	 */
	public abstract boolean ContainsContactObjects();

	/**
	 * Getter for the parent property of this element
	 * @return The parent object of this element or null if none has been defined
	 */
	public DrawingComposite getParent() {
		return parent;
	}

	/**
	 * Setter for the parent property of this element
	 * @param parent The parent object of this element
	 */
	public void setParent(DrawingComposite parent) {
		this.parent = parent;
	}
	
}
