//package com.drawing.datastructure;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Path;
//import android.os.storage.OnObbStateChangeListener;
//import android.util.Log;
//
//import com.drawing.gestures.Point;
//
///**
// * 
// * @author Christian Br�ndel TU Dresden / SAP Research Dresden
// * @author Florian Schneider TU Dresden / SAP Next Business and Technology Dresden
// *
// */
//public class CustomPath extends Path implements Serializable
//{
//	
//	/**
//	 * The color assigned to this path object
//	 */
//	public int color;
//	
//	private boolean isVisible;
//
//	/**
//	 * The maximum x value of this path object
//	 */
//	public float maxX = -1;
//	/**
//	 * The maximum y value of this path object
//	 */	
//	public float maxY = -1;
//	/**
//	 * The minimum x value of this path object
//	 */	
//	public float minX = Float.POSITIVE_INFINITY;
//	/**
//	 * The minimum y value of this path object
//	 */	
//	public float minY = Float.POSITIVE_INFINITY; 
//	
//	/**
//	 * Serial version UID
//	 */
//	private static final long serialVersionUID = -1421442079328370267L;
//
//	/**
//	 * <b>GestureType</b> of the <b>CustomPath</b>
//	 */
//	private GestureTypes type;
//	
//	/**
//	 * Transformed vertex array 
//	 */
//	private List<Point> vertices = new ArrayList<Point>();
//	
//	/**
//	 * original vertex array
//	 */
//	private List<Point> originalVertices = new ArrayList<Point>();
//	
//	/**
//	 * get the unmodified vertex array that is the foundation of the respective path object
//	 * @return the unmodified vertex array
//	 */
//	public Point[] getOriginalVertices() 
//	{
//		Point[] array = new Point[originalVertices.size()];
//		return originalVertices.toArray(array);
//	}
//
//	/**
//	 * True if the path is highlighted
//	 */
//	private boolean highlighted = false;
//	
//	/**
//	 * The unique identifier of the <b>CustomPath</b> object
//	 */
//	private UUID uid;
//	
//	
//
//	/**
//	 * Getter for the <b>UUID</b> of the <b>CustomPath</b> object
//	 * @return The unique identifier of the <b>CustomPath</b> object
//	 */
//	public UUID getUid() 
//	{
//		return uid;
//	}
//	
//	/**
//	 * A Custom path object can be highlighted or selected
//	 * @return true if the respective path is highlighted or in other words selected
//	 */
//	public boolean isHighlighted() {
//		return highlighted;
//	}
//
//	/**
//	 * A Custom path object can be highlighted or selected
//	 * @param highlighted true if the respective path is highlighted or in other words selected
//	 */
//	public void setHighlighted(boolean highlighted) {
//		this.highlighted = highlighted;
//	}
//
//	
//	/**
//	 * Custom Path object that adds additional functionality and therefore enables geometrical transformations
//	 * for vertices and the availability of a vertex array
//	 */
//	public CustomPath()
//	{
//		super();
//	
//		uid = UUID.randomUUID();
//		
//		type = GestureTypes.NOGESTURE;
//		
//		highlighted = false;
//		
//		color = Color.RED;
//		
//		isVisible = true;
//
//	}
//
//	/**
//	 * Getter for the type attribute
//	 * @return <b>GestureType</b> of the <b>CustomPath</b>
//	 */
//		public GestureTypes getType() {
//			return type;
//		}
//
//		/**
//		 * Setter for the type attribute
//		 * @param type <b>GestureType</b> of the <b>CustomPath</b>
//		 */
//		public void setType(GestureTypes type) {
//			this.type = type;
//		}
//	
//
//
//	/**
//	 *  Attaches the vertex coordinates to the internal array of the <b>CustomPath</b> object
//	 * @see android.graphics.Path#moveTo(float, float)
//	 */
//	public void moveTo(float x, float y)
//	{
//		vertices.add(new Point(x,y));
//		originalVertices.add( new Point(x,y));
//		
//		updateExtrema(x, y);
//		
//		super.moveTo(x, y);
//	}
//	
//	/** 
//	 * Attaches the vertex coordinates to the internal array of the <b>CustomPath</b> object
//	 * @see android.graphics.Path#lineTo(float, float)
//	 */
//	public void lineTo(float x, float y)
//	{
//		vertices.add(new Point(x,y));
//		originalVertices.add( new Point(x,y));
//		
//		updateExtrema(x, y);
//		
//		super.lineTo(x, y);
//	}
//	
//	public void addCircle(float x, float y, float radius, Path.Direction dir)
//	{
//		vertices.add(new Point(x,y));
//		originalVertices.add( new Point(x,y));
//		
//		vertices.add(new Point(x-radius, y));
//		originalVertices.add( new Point(x-radius, y));
//		
//		vertices.add(new Point(x, y+radius));
//		originalVertices.add( new Point(x, y+radius));
//		
//		vertices.add(new Point(x, y+radius));
//		originalVertices.add( new Point(x, y+radius));
//		
//		vertices.add(new Point(x+radius, y));
//		originalVertices.add( new Point(x+radius, y));
//		
//		updateExtrema(x, y);
//		
//		updateExtrema(x-radius, y);
//		updateExtrema(x, y+radius);
//		updateExtrema(x+radius, y);
//		updateExtrema(x, y-radius);
//
//		super.addCircle(x, y, radius, dir);
//	}
//	
//	public void reset()
//	{
//		vertices.clear();
//		originalVertices.clear();
//		
//		resetExtrema();
//		
//		super.reset();
//	}
//
//	
//	private void updateExtrema(float x, float y)
//	{
//		if(x > maxX)
//			maxX = x;
//		
//		if(x < minX)
//			minX = x;
//		
//		if(y > maxY)
//			maxY = y;
//		
//		if(y < minY)
//			minY = y;
//	}
//	
//	private void resetExtrema()
//	{
//		maxX = -1;
//		maxY = -1;
//		minX = Float.POSITIVE_INFINITY;
//		minY = Float.POSITIVE_INFINITY; 
//	}
//
//	/**
//	 * get the underlying vertex array of the <b>CustomPath</b> object 
//	 * @return array of all vertex points that represent the path
//	 */
//	public List<Point> getVertices()
//	{
//		return vertices;		
//	}
//
//	
//	/**
//	 * set the underlying vertex array of the <b>CustomPath</b> object
//	 * @param vertices array of all vertex points that represent the path
//	 */
//	public void setVertices(List<Point> vertices)
//	{			
//		this.vertices = vertices;
//	}
//	
//	/**
//	 * transform an array of <b>Point</b> objects with an arbitrary transformation matrix
//	 * @param inputVertices array of <b>Point</b> that should be transformed
//	 * @param matrix <b>Matrix</b> object that includes the transformation information
//	 * @return the transformed array of <b>Point</b> objects
//	 * @see com.drawing.gesture.Point
//	 */
//	public static List<Point> transformVertices(List<Point> inputVertices, Matrix matrix)
//	{
//		int size = inputVertices.size();
//		Point[] transformedPoints = new Point[size];
//		float [] pointsToTransform = new float[size*2];
//		int counter = 0;
//		
//		for(int i = 0; i < size; i++)
//		{
//			pointsToTransform[counter]=inputVertices.get(i).x;
//			counter ++;
//			pointsToTransform[counter]=inputVertices.get(i).y;
//			counter ++;
//			
//		}
//		
//		matrix.mapPoints(pointsToTransform);
//				
//		counter = 0;
//		
//		for(int j = 0; j < inputVertices.size(); j++)
//		{
//			
//			transformedPoints[j] = new Point(pointsToTransform[counter],pointsToTransform[counter+1]);
//			counter+=2;
//			
//		}
//		
//		List<Point> transformedPointsList = new ArrayList<Point>();
//		
//		for(Point point : transformedPoints)
//		{
//			transformedPointsList.add(point);
//		}
//		
//		
//		return transformedPointsList;
//	}
//
//	/**
//	 * Apply the geometric transformation upon the highlighted <b>CustomPath</b> object
//	 * @param matrix The current Transformation matrix that should be applied upon the highlighted path
//	 * @param backupTransformationMatrix The stored matrix that is responsible for displaying all not selected paths
//	 */
//	public void applyTransformation(Matrix matrix, Matrix backupTransformationMatrix)
//	{			
//		Log.d("CustomPath", "applyTransformation");
//		
//		List<Point> tempVertices = new ArrayList<Point>();		
//		//apply matrices upon vertices
//		tempVertices = CustomPath.transformVertices(vertices, matrix);
//		
//		Matrix backupTransformationInverse = new Matrix();
//		backupTransformationMatrix.invert(backupTransformationInverse);
//		
//		tempVertices = CustomPath.transformVertices(tempVertices, backupTransformationInverse);
//				
//		vertices = new ArrayList<Point>();
//		
//		for(Point point : tempVertices)
//		{
//			vertices.add(point);
//		}
//		
//		//apply matrices upon the path itself
//		this.transform(matrix);
//		this.transform(backupTransformationInverse);
//		
//	}
//
//	/**
//	 * Overridden transformation matrix that ensures that the transformation matrix is also applied upon
//	 * the vertex array of this path
//	 */
//	public void transform(Matrix matrix)
//	{
//		//update the extrema of this path during every transformation
//		
//		float[] extrema = {minX, 0, maxX, 0, 0, minY, 0, maxY};
//
//		matrix.mapPoints(extrema);
//		
//		minX = extrema[0];
//		maxX = extrema[2];
//		minY = extrema[5];
//		maxY = extrema[7];
//		
//		super.transform(matrix);
//	}
//	
//	public void updateExtrema(Matrix matrix)
//	{
//		//update the extrema of this path during every transformation
//		
//		float[] extrema = {minX, 0, maxX, 0, 0, minY, 0, maxY};
//
//		matrix.mapPoints(extrema);
//		
//		minX = extrema[0];
//		maxX = extrema[2];
//		minY = extrema[5];
//		maxY = extrema[7];
//	}
//
//	public int getColor() 
//	{
//		return color;
//	}
//
//	public void setColor(int color)
//	{
//		this.color = color;
//	}
//
//	public boolean isVisible() {
//		return isVisible;
//	}
//
//	public void setVisible(boolean isVisible) {
//		this.isVisible = isVisible;
//	}
//
//}

package com.drawing.datastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;

import com.drawing.gestures.Operations;
import com.drawing.gestures.Point;

/**
 * 
 * @author Christian Br�ndel TU Dresden / SAP Research Dresden
 *
 */
public class CustomPath extends Path implements Serializable{
	
	/**
	 * The color assigned to this path object
	 */
	public int color;
	
	/**
	 * The maximum x value of this path object
	 */
	public float maxX = -1;
	/**
	 * The maximum y value of this path object
	 */	
	public float maxY = -1;
	/**
	 * The minimum x value of this path object
	 */	
	public float minX = Float.POSITIVE_INFINITY;
	/**
	 * The minimum y value of this path object
	 */	
	public float minY = Float.POSITIVE_INFINITY; 
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -1421442079328370267L;

	/**
	 * <b>GestureType</b> of the <b>CustomPath</b>
	 */
	private GestureTypes type;
	
	/**
	 * Transformed vertex array 
	 */
	private List<Point> vertices = new ArrayList<Point>();
	
	/**
	 * original vertex array
	 */
	private List<Point> originalVertices = new ArrayList<Point>();
	
	/**
	 * get the unmodified vertex array that is the foundation of the respective path object
	 * @return the unmodified vertex array
	 */
	public Point[] getOriginalVertices() {
		Point[] array = new Point[originalVertices.size()];
		return originalVertices.toArray(array);
	}

	/**
	 * True if the path is highlighted
	 */
	private boolean highlighted = false;
	
	/**
	 * The unique identifier of the <b>CustomPath</b> object
	 */
	private UUID uid;

	
	public void setUid(UUID uid) {
		this.uid = uid;
	}

	/**
	 * Getter for the <b>UUID</b> of the <b>CustomPath</b> object
	 * @return The unique identifier of the <b>CustomPath</b> object
	 */
	public UUID getUid() {
		return uid;
	}
	
	/**
	 * A Custom path object can be highlighted or selected
	 * @return true if the respective path is highlighted or in other words selected
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * A Custom path object can be highlighted or selected
	 * @param highlighted true if the respective path is highlighted or in other words selected
	 */
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	
	/**
	 * Custom Path object that adds additional functionality and therefore enables geometrical transformations
	 * for vertices and the availability of a vertex array
	 */
	public CustomPath()
	{
		
		super();
	
		uid = UUID.randomUUID();
		
		type = GestureTypes.NOGESTURE;
		
		highlighted = false;
		
		color = Color.BLACK;
	}

/**
 * Getter for the type attribute
 * @return <b>GestureType</b> of the <b>CustomPath</b>
 */
	public GestureTypes getType() {
		return type;
	}

	/**
	 * Setter for the type attribute
	 * @param type <b>GestureType</b> of the <b>CustomPath</b>
	 */
	public void setType(GestureTypes type) {
		this.type = type;
	}

	/** 
	 * Attaches the vertex coordinates to the internal array of the <b>CustomPath</b> object
	 * @see android.graphics.Path#lineTo(float, float)
	 */
	public void lineTo(float x, float y) {
		
		vertices.add(new Point(x,y));
		originalVertices.add( new Point(x,y));
		
		if(x > maxX)
			maxX = x;
		
		if(x < minX)
			minX = x;
		
		if(y > maxY)
			maxY = y;
		
		if(y < minY)
			minY = y;
		
		super.lineTo(x, y);
	}

	
	/**
	 *  Attaches the vertex coordinates to the internal array of the <b>CustomPath</b> object
	 * @see android.graphics.Path#moveTo(float, float)
	 */
	public void moveTo(float x, float y) {
		
		vertices.add(new Point(x,y));
		originalVertices.add( new Point(x,y));
		
		if(x > maxX)
			maxX = x;
		
		if(x < minX)
			minX = x;
		
		if(y > maxY)
			maxY = y;
		
		if(y < minY)
			minY = y;
		
		super.moveTo(x, y);
	}

	/**
	 * get the underlying vertex array of the <b>CustomPath</b> object 
	 * @return array of all vertex points that represent the path
	 */
	public List<Point> getVertices() {
		
		return vertices;		
	}

	
	/**
	 * set the underlying vertex array of the <b>CustomPath</b> object
	 * @param vertices array of all vertex points that represent the path
	 */
	public void setVertices(List<Point> vertices) {
				
		this.vertices = vertices;
	}
	
	/**
	 * transform an array of <b>Point</b> objects with an arbitrary transformation matrix
	 * @param inputVertices array of <b>Point</b> that should be transformed
	 * @param matrix <b>Matrix</b> object that includes the transformation information
	 * @return the transformed array of <b>Point</b> objects
	 * @see com.drawing.gesture.Point
	 */
	public static List<Point> transformVertices(List<Point> inputVertices, Matrix matrix)
	{
		
		int size = inputVertices.size();
		Point[] transformedPoints = new Point[size];
		float [] pointsToTransform = new float[size*2];
		int counter = 0;
		
		for(int i = 0; i < size; i++)
		{
			pointsToTransform[counter]=inputVertices.get(i).x;
			counter ++;
			pointsToTransform[counter]=inputVertices.get(i).y;
			counter ++;
			
		}
		
		matrix.mapPoints(pointsToTransform);
				
		counter = 0;
		
		for(int j = 0; j < inputVertices.size(); j++)
		{
			
			transformedPoints[j] = new Point(pointsToTransform[counter],pointsToTransform[counter+1]);
			counter+=2;
			
		}
		
		List<Point> transformedPointsList = new ArrayList<Point>();
		for(Point point : transformedPoints)
		{
			transformedPointsList.add(point);
		}
		
		
		return transformedPointsList;
	}

	/**
	 * Apply the geometric transformation upon the highlighted <b>CustomPath</b> object
	 * @param matrix The current Transformation matrix that should be applied upon the highlighted path
	 * @param backupTransformationMatrix The stored matrix that is responsible for displaying all not selected paths
	 */
	public void applyTransformation(Matrix matrix,
			Matrix backupTransformationMatrix) {
				
		List<Point> tempVertices = new ArrayList<Point>();		
		//apply matrices upon vertices
		tempVertices = CustomPath.transformVertices(vertices, matrix);
		
		Matrix backupTransformationInverse = new Matrix();
		backupTransformationMatrix.invert(backupTransformationInverse);
		
		tempVertices = CustomPath.transformVertices(tempVertices, backupTransformationInverse);
				
		vertices = new ArrayList<Point>();
		for(Point point : tempVertices)
		{
			vertices.add(point);
		}
		
		//apply matrices upon the path itself
		this.transform(matrix);
		this.transform(backupTransformationInverse);
		
	}

	/**
	 * Overridden transformation matrix that ensures that the transformation matrix is also applied upon
	 * the vertex array of this path
	 */
	public void transform(Matrix matrix) {

		//update the extrema of this path during every transformation
		
		float[] extrema = {minX,0 ,maxX,0 ,0,minY ,0,maxY};

		matrix.mapPoints(extrema);
		
		minX = extrema[0];
		maxX = extrema[2];
		minY = extrema[5];
		maxY = extrema[7];
		
		super.transform(matrix);
	}

	public int getColor() {
		// TODO Auto-generated method stub
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	
}

