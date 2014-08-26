package com.drawing.application;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingComponent;
import com.drawing.datastructure.DrawingComposite;
import com.drawing.datastructure.DrawingWordLetter;
import com.drawing.gestures.Point;

public abstract class GeneralView extends ImageView {


	/**
	 * minimal stroke length that is processed
	 */
	static final int MINSTROKELENGTH = 2;
	
	/**
	 * no mode has been set
	 */
	protected static final int DEFAULT = -1;

	/**
	 * Drawing mode is activated
	 */
	protected static final int DRAW = 0;

	/**
	 * Zoom mode is activated
	 */
	protected static final int PANZOOM = 2;

	protected MainActivity ma;

	protected static DrawingComposite drawingObjects;
		
	/**
	 * The transformation <b>Matrix</b> of the canvas
	 */
	protected Matrix matrix = new Matrix();
	/**
	 * The backup transformation <b>Matrix</b> of the canvas
	 */
	protected Matrix savedMatrix = new Matrix();
	/**
	 * The backup transformation <b>Matrix</b> for highlighted paths
	 */
	protected Matrix backupTransformationMatrix = new Matrix();
	

	protected Matrix inverse, backupInverse = new Matrix();
	
	protected CustomPath drawPath, tempPath;

	protected int screenWidth, screenHeight, canvasHeight;
	
	/**
	 * Coordinates from <b>MotionEvents</b>
	 */
	protected float historicalX, historicalY, x, y = -1;

	/**
	 * Variable for keeping track of the time when the previous
	 * <b>ACTION_DOWN</b> event was performed
	 */
	protected float historicalEventTime = -1;
	
	protected boolean calculateCanvasSize;
	
	/**
	 * true if a double tap interaction was detected
	 */
	protected boolean doubleTap = false;
	
	/**
	 * True is the current transformation matrix hasn't been backuped before 
	 */
	protected boolean firstBackup = true;
	
	/**
	 * The start point of a input gesture
	 */
	protected PointF start = new PointF();

	/**
	 * Detection mode for the {@link onTouchListener}
	 */
	protected int mode = DRAW;

	public int getMode()
	{
		return mode;
	}
	
	public void resetMode()
	{
		mode = DEFAULT;
	}

	/**
	 * The <b>Paint</b> object for standard paths
	 */
	protected Paint paint = new Paint();

	/**
	 * The <b>Paint</b> object for highlighted paths
	 */
	protected Paint highlightedPaint = new Paint();
	
	protected DrawingComponent lastHighlightedComponent = null;

	/**
	 * helper array for displaying and storing temporary paths
	 */
	protected List<CustomPath> newPaths = new ArrayList<CustomPath>();

	@SuppressLint("NewApi")
	public GeneralView(Context context, AttributeSet attrs) 
	{
		
		super(context, attrs);

		matrix = new Matrix();
		inverse = new Matrix();
		backupInverse = new Matrix();
		
		backupTransformationMatrix = new Matrix();
	
		ma = (MainActivity) getContext();

		Display display = ma.getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);

		screenHeight = size.y;
		screenWidth = size.x;
		canvasHeight = size.y;
		
	}

	/**
	 * backup the current transformation matrix in order to enable selective
	 * manipulations of paths
	 */
	protected void backupCurrentTransformationMatrix() 
	{
		backupTransformationMatrix.set(matrix);
	}

	/**
	 * restore the formerly saved transformation matrix in order to conduct
	 * arbitrary transformations to the whole canvas again
	 */
	protected void restoreBackupTransformationMatrix() 
	{
		matrix.set(backupTransformationMatrix);
	}
	
	/**
	 * Translate the transformations of highlighted path objects back to the
	 * canvas matrix
	 * 
	 * @param minDistanceIndex
	 *            Index of the detected selected path
	 */
	protected void updatePath(CustomPath path) 
	{
		Log.d("GeneralView","!!!!!!!! updatePath");

		if (path != null) {

			DrawingComponent component = null;
			component = drawingObjects.getObjectByPathId(path.getUid());

			if (!path.isHighlighted()) {
				
				component.updatePath(matrix, backupTransformationMatrix, true);

			} else {

				// update object dependencies

				if (!component.isGrouped())

				{

					if (!(component instanceof DrawingWordLetter)) {

						drawingObjects.removeComponent(component);

						component.updatePath(matrix, backupTransformationMatrix, false);

						if (component.isComposite) 
						{
							((DrawingComposite) component).deleteChildren();
						}

						drawingObjects.addComponent(component);

					} else {

						component = ((DrawingWordLetter) component).getParent();

						drawingObjects.removeCompositeComponent(component);

						component.updatePath(matrix, backupTransformationMatrix, false);

						drawingObjects.addComponent(component);
					}

				} else {

					if (component instanceof DrawingWordLetter)
					{
						component = component.getParent();
					}

					component = component.getParent();

					drawingObjects.removeCompositeComponent(component);

					component.updatePath(matrix, backupTransformationMatrix, false);

					drawingObjects.addComponent(component);

				}

			}

		}

		if (firstBackup)
			if (drawingObjects.getPathList().ContainsHighlightedPath()) {
				backupCurrentTransformationMatrix();
				firstBackup = false;
			}

		if (!firstBackup
				&& !drawingObjects.getPathList().ContainsHighlightedPath()) {
			restoreBackupTransformationMatrix();
			firstBackup = true;
		}

	}

	protected abstract void configureButtonActivation();
	
	/**
	 * This method adjusts the view port of the canvas so that every stroke that
	 * has been drawn is contained
	 */
	public void adjustViewPort() {

		Log.d("GeneralView","adjustViewPort ");
		
		if (drawingObjects.getChild().size() > 0) {

			float[] extrema = { Float.POSITIVE_INFINITY, -1,
					Float.POSITIVE_INFINITY, -1 };

			Matrix adjustmentMatrix = new Matrix();

			extrema = drawingObjects.determineDrawnExtrema(extrema);

			Point viewCenter = new Point(getWidth() / 2, getHeight() / 2);

			float heightScale = 1.0f;

			float widthScale = 1.0f;

			float appliedScale = 1.0f;

			int offset = 100;

			float extremaWidth = (float) Math.abs(extrema[1] - extrema[0]);

			float extremaHeight = (float) Math.abs(extrema[3] - extrema[2]);

			Point extremaCenter = new Point((extrema[0] + extrema[1]) / 2,
					(extrema[2] + extrema[3]) / 2);

			widthScale = ((getWidth() - offset) / extremaWidth);

			heightScale = ((getHeight() - offset) / extremaHeight);

			if (widthScale > 1.0) {

				if (heightScale > 1.0) {
					if (widthScale > heightScale) {
						appliedScale = heightScale;
					} else {
						appliedScale = widthScale;
					}
				} else {
					appliedScale = heightScale;
				}

			} else {
				if (heightScale > 1.0) {
					appliedScale = widthScale;
				} else {
					if (widthScale < heightScale) {
						appliedScale = widthScale;
					} else {
						appliedScale = heightScale;
					}
				}

			}

			adjustmentMatrix.postScale(appliedScale, appliedScale);
			extremaCenter.x = extremaCenter.x * appliedScale;
			extremaCenter.y = extremaCenter.y * appliedScale;

			adjustmentMatrix.postTranslate(viewCenter.x - extremaCenter.x,
					viewCenter.y - extremaCenter.y);

			savedMatrix.set(matrix);

			matrix.set(adjustmentMatrix);

		} else {
			matrix.set(new Matrix());
		}

	}
	
	/**
	 * Getter for the <b>DrawingComponent</b> of the <b>View</b>
	 * 
	 * @return The components that represent the created data structure
	 */
	public DrawingComposite getDrawingObjects() 
	{
		return drawingObjects;
	}

	/**
	 * Getter for the <b>DrawingComponent</b> of the <b>View</b>
	 * 
	 * @param drawingObjects
	 *            The components that represent the data structure that should
	 *            be attached
	 */
	public void setDrawingObjects(DrawingComposite drawingObjects) 
	{

		// All paths need to be redrawn due to an deserialization bug of the
		// original Path object
		// of Android, because otherwise nothing would be drawn on the canvas

		drawingObjects.redrawPathsafterDeserialization(ma);

		this.drawingObjects = drawingObjects;

		invalidate();
	}
	
	/**
	 * Calculate the mid of two points and set the resulting values to the first
	 * one
	 * 
	 * @param point
	 *            The first point
	 * @param event
	 *            The event containing touch information for the second point
	 */
	protected void midPoint(PointF point, MotionEvent event) 
	{

		float x = event.getX(0) + event.getX(1);

		float y = event.getY(0) + event.getY(1);

		point.set(x / 2, y / 2);

	}
	
	/**
	 * Compute the distance between the former and the current tracked touch
	 * event
	 * 
	 * @param event
	 *            The tracked touch input event
	 * @return The distance between the current and the former event
	 */
	protected float spacing(MotionEvent event) 
	{
		if (event.getPointerCount() >= 2) 
		{

			float x = (event.getX(0) - event.getX(1));

			float y = (event.getY(0) - event.getY(1));

			return FloatMath.sqrt((x * x + y * y));
		} else
			return 0.0f;
	}
	
	/**
	 * This method takes the current transformation matrices and converts them
	 * to <b>float []</b>
	 * 
	 * @return An array of <b>float[]</b> that represents the transformation
	 *         matrices of the <b>DrawView</b>
	 */
	protected float[][] getMatrices() 
	{
		float[][] matrices = new float[3][9];

		matrix.getValues(matrices[0]);

		savedMatrix.getValues(matrices[1]);

		backupTransformationMatrix.getValues(matrices[2]);

		return matrices;
	}

	/**
	 * This method restores the current transformation matrices from <b>float
	 * []</b>
	 * 
	 * @param matrices
	 *            An array of <b>float[]</b> that represents the transformation
	 *            matrices of the <b>DrawView</b>
	 */
	protected void restoreMatrices(float[][] matrices) 
	{
		matrix.setValues(matrices[0]);

		savedMatrix.setValues(matrices[1]);

		backupTransformationMatrix.setValues(matrices[2]);

	}
	
	protected void removeHighlightFromPaths()
	{
		
		// no path match was found but there are highlighted paths

		List<CustomPath> highlightedPathList = drawingObjects.getHighlightedComponents();

		for (int i = 0; i < highlightedPathList.size(); i++) 
		{
			if (highlightedPathList.get(i).isHighlighted())
				updatePath(highlightedPathList.get(i));

		}
		
		invalidate();
		
	}

}
