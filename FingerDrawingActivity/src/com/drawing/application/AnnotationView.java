/**
 * 
 */
package com.drawing.application;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.SumPathEffect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingComponent;
import com.drawing.datastructure.DrawingComposite;
import com.drawing.datastructure.DrawingCompositeWord;
import com.drawing.datastructure.DrawingGroup;
import com.drawing.datastructure.DrawingWordLetter;
import com.drawing.datastructure.ScaledPathArray;
import com.drawing.gestures.Point;
import com.drawing.test2.R;
import com.drawing.test2.R.color;

/**
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */
public class AnnotationView extends GeneralView implements OnTouchListener {

	/**
	 * true if a new path was created and no vertices were attached to it so far
	 */
	boolean firstPointOfPath = true;

	/**
	 * The center between two contact points in case of multi touch
	 */
	PointF mid = new PointF();

	/**
	 * helper distance for the calculation of zoom gestures
	 */
	float oldDist = 1f;
	/**
	 * The scale parameter that is applied upon the canvas' transformation
	 * <b>Matrix</b>
	 */

	/**
	 * The scale parameter that is applied upon the canvas' transformation
	 * <b>Matrix</b>
	 */
	float scale = 1f;

	/**
	 * Set to true if the pan mode was enabled by the System menu
	 */
	private boolean scaleView = false;

	private DrawingComponent objectToAnnotate = null;

	private Paint annotationPaint;

	public AnnotationView(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		// TODO Auto-generated constructor stub

		setFocusable(true);
		
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		fda = (FingerDrawingActivity) getContext();

		// initialize stroke paints
		paint.setColor(Color.DKGRAY);
		// paint.setAlpha(150);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);

		// tint path effect
		PathEffect effect = new CornerPathEffect(20);
		PathEffect randomLines = new DiscretePathEffect(5, 2);
		PathEffect dashEffect = new DashPathEffect(new float[] { 2, 1, 5, 0, 3,
				8, 7, 4, 2, 3 }, 5);
		PathEffect sumEffect = new SumPathEffect(effect, randomLines);

		@SuppressWarnings("unused")
		PathEffect composeEffect = new SumPathEffect(dashEffect, sumEffect);
		// paint.setPathEffect(composeEffect);

		highlightedPaint.setColor(Color.CYAN);
		highlightedPaint.setAntiAlias(true);
		highlightedPaint.setStyle(Paint.Style.STROKE);
		highlightedPaint.setStrokeWidth(3);

		highlightedPaint.setPathEffect(effect);
		paint.setPathEffect(effect);

		annotationPaint = new Paint();
		annotationPaint.setColor(Color.YELLOW);
		annotationPaint.setAntiAlias(true);
		annotationPaint.setStyle(Paint.Style.STROKE);
		annotationPaint.setStrokeWidth(3);
		annotationPaint.setPathEffect(effect);

		tempPath = new CustomPath();

		mode = DEFAULT;
		

	}

	public void initializeButtonListeners() {
		
		((ImageButton) fda.findViewById(R.id.annotate_annotateButton))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						fda.toggleAnnotateMode();

					}
				});
		
		((ImageButton)fda.findViewById(R.id.annotate_modifyButton)).setOnClickListener(
				new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		((ImageButton)fda.findViewById(R.id.annotate_clearButton)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				if(lastHighlightedComponent != null)
				{
					lastHighlightedComponent.setAnnotation(null);
				}
				
			newPaths.clear();
			invalidate();
				
			}
		});
		
		((ImageButton)fda.findViewById(R.id.annotate_clearButton)).setEnabled(false);
		
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		matrix.invert(inverse);

		backupTransformationMatrix.invert(backupInverse);

		// canvas.drawARGB(canvasColorARGB[0], canvasColorARGB[1],
		// canvasColorARGB[2], canvasColorARGB[3]);

		canvas.clipRect(0, 0, screenWidth, screenHeight);

		// canvas.setMatrix(new Matrix());

		canvas.save();

		if (calculateCanvasSize) {
			// canvasHeight = canvas.getClipBounds().height();

			float[] scale = new float[] { canvas.getHeight(), 0 };

			inverse.mapPoints(scale);

			canvasHeight = (int) scale[0];

			calculateCanvasSize = false;
		}

		// apply transformations only to highlighted path objects
		if (drawingObjects.getPathList().ContainsHighlightedPath()) {

			// draw highlighted paths

			for (int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++) {

				drawPath = drawingObjects.getPathList().getPath(i);

				// matchPaint(drawPath.getType());

				paint.setColor(drawPath.getColor());

				if (paint.getColor() == Color.BLACK)
					paint.setColor(Color.DKGRAY);

				// apply the inverse matrix of the canvas upon the path

				if (drawPath.isHighlighted()) {

					drawPath.transform(matrix);

					// draw the path
					canvas.drawPath(drawPath, highlightedPaint);

					// restore the initial path object with the original
					// transformation
					// Matrix

					drawPath.transform(inverse);

				} else {

					drawPath.transform(backupTransformationMatrix);

					canvas.drawPath(drawPath, paint);

					drawPath.transform(backupInverse);

				}

				if (paint.getColor() != Color.DKGRAY)
					paint.setColor(Color.DKGRAY);
			}

			if (drawingObjects.ContainsContactObjects()) {

				drawingObjects.drawContactObjects(canvas, matrix,
						backupTransformationMatrix, true);

			}

			// draw tempPath
			if (!tempPath.isEmpty() && mode == DRAW) {
				// paint.setColor(Color.DKGRAY);
				canvas.drawPath(tempPath, annotationPaint);
			}

			// attach new paths
			for (int j = 0; j < newPaths.size(); j++) {
				canvas.drawPath(newPaths.get(j), annotationPaint);
			}

		} else {

			if (!tempPath.isEmpty() && mode == DRAW) {
				// paint.setColor(Color.DKGRAY);
				canvas.drawPath(tempPath, paint);
			}

			// canvas.setMatrix(matrix);

			canvas.restore();

			// draw stored paths
			for (int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++) {

				drawPath = drawingObjects.getPathList().getPath(i);

				// matchPaint(drawPath.getType());

				paint.setColor(drawPath.getColor());

				if (paint.getColor() == Color.BLACK)
					paint.setColor(Color.DKGRAY);

				// apply the inverse matrix of the canvas upon the path

				drawPath.transform(matrix);

				canvas.drawPath(drawPath, paint);

				// restore the initial path object with the original
				// transformation
				// Matrix

				drawPath.transform(inverse);

				if (paint.getColor() != Color.DKGRAY)
					paint.setColor(Color.DKGRAY);

			}

			// attach new paths
			for (int j = 0; j < newPaths.size(); j++) {
				
				canvas.drawPath(newPaths.get(j), paint);
			}

			if (drawingObjects.ContainsContactObjects()) {

				drawingObjects.drawContactObjects(canvas, matrix,

				backupTransformationMatrix, false);

			}

		}

		paint.setColor(Color.LTGRAY);
		
		for (CustomPath path : drawingObjects.getAnnotationPathList()) {
			path.transform(matrix);

			canvas.drawPath(path, paint);

			path.transform(inverse);
		}

		//draw Annotation with special paint
		if (lastHighlightedComponent != null) {
			for (CustomPath path : lastHighlightedComponent
					.getAnnotationPathList())
			{

				path.transform(matrix);

			canvas.drawPath(path, annotationPaint);

			path.transform(inverse);
			
			}
		}

		canvas.restore();

		super.onDraw(canvas);
	}

	/**
	 * Getter for the <b>DrawingComponent</b> of the <b>View</b>
	 * 
	 * @return The components that represent the created data structure
	 */
	public DrawingComposite getDrawingObjects() {
		return drawingObjects;
	}

	/**
	 * Getter for the <b>DrawingComponent</b> of the <b>View</b>
	 * 
	 * @param drawingObjects
	 *            The components that represent the data structure that should
	 *            be attached
	 */
	public void setDrawingObjects(DrawingComposite drawingObjects) {

		// All paths need to be redrawn due to an deserialization bug of the
		// original Path object
		// of Android, because otherwise nothing would be drawn on the canvas

		drawingObjects.redrawPathsafterDeserialization(fda);

		this.drawingObjects = drawingObjects;

		invalidate();
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if (lastHighlightedComponent != null)
			mode = DRAW;

		x = event.getX(0);
		y = event.getY(0);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:

			historicalX = -1;
			historicalY = -1;

			savedMatrix.set(matrix);

			start.set(x, y);

			// test if path objects are in the input area, if the pan mode
			// is
			// enabled and a double tap was performed

			float timedifference = -1;

			if (historicalEventTime > 0)
				timedifference = event.getEventTime() - historicalEventTime;

			if (timedifference < 300 && timedifference > -1) {
				doubleTap = true;
				matrix.set(savedMatrix);

				RetrievePossiblePathMatch(event, drawingObjects.getPathList());

				if (lastHighlightedComponent != null) {
					if (lastHighlightedComponent.isHighlighted())
						mode = DRAW;
					else
						mode = DEFAULT;
				}

				historicalEventTime = -1;
			}

			historicalEventTime = event.getEventTime();

			break;

		// invoked as soon as a second touch input is recognized
		// simultaneously
		// to the first one
		case MotionEvent.ACTION_POINTER_DOWN:

			doubleTap = false;

			oldDist = spacing(event);

			if (oldDist > 0f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = PANZOOM;
			}
			break;

		// invoked as soon as the second touch input disappears
		case MotionEvent.ACTION_POINTER_UP:

			scaleView = false;

			doubleTap = false;

			// stay in drag - mode if the flag is set
			if (mode == PANZOOM) {

				savedMatrix.set(matrix);

			}
			// switch back to standard drawing mode
			else
				mode = DEFAULT;

			firstPointOfPath = true;
			tempPath = new CustomPath();

			calculateCanvasSize = true;

			break;

		// invoked in case the touch input is moving
		case MotionEvent.ACTION_MOVE:

			doubleTap = false;

			// matrix.set(savedMatrix);

			if (mode == PANZOOM) {

				float newDist = spacing(event);

				if (newDist > 0f) {

					matrix.set(savedMatrix);

					// apply a threshold that enables a distinction between
					// zoom and pan
					if (!scaleView && Math.abs(newDist - oldDist) > 75) {
						scaleView = true;
						oldDist = newDist;
					}

					scale = Math.abs(newDist / oldDist);

					if (scaleView) {
						matrix.set(savedMatrix);
						// scale canvas according to detected scale
						// operation
						matrix.postScale(scale, scale, mid.x, mid.y);

						// translate canvas according to the detected pan
						// operation
						matrix.postTranslate(x - start.x, y - start.y);
					} else {
						matrix.set(savedMatrix);
						matrix.postTranslate(x - start.x, y - start.y);
					}

				}

			}

			break;

		case MotionEvent.ACTION_UP:

			if (!doubleTap && mode == DRAW) {

				// recognize gestures that consist of at least 10 Points
				if (tempPath.getVertices().size() > MINSTROKELENGTH) {

					newPaths.add(tempPath);

				}

				tempPath = new CustomPath();

				firstPointOfPath = true;

			}

			break;

		}

		if (!doubleTap)
			if (mode == DRAW && lastHighlightedComponent != null)

				// draw path but ensure that the user doesn't zoom
				if (event.getAction() == MotionEvent.ACTION_MOVE
						&& mode != PANZOOM) {

					Point point = new Point();

					point.x = (int) x;

					point.y = (int) y;

					if (((int) historicalX) != point.x
							&& ((int) historicalY) != point.y) {

						if (firstPointOfPath) {
							tempPath.moveTo(point.x, point.y);
							firstPointOfPath = false;
						} else {
							tempPath.lineTo(point.x, point.y);
						}

					}

				}

		// delete path if canvas was zoomed
		if (mode == PANZOOM) {

			newPaths.clear();
			firstPointOfPath = true;
		}

		invalidate();

		return true;

	}

	/**
	 * Asynchronously identify selected path objects
	 * 
	 * @param event
	 *            touch input event
	 * @param scaledPathArray2
	 *            ScaledPathArray object that contains all paths that are drawn
	 *            on the canvas
	 */
	private void RetrievePossiblePathMatch(MotionEvent event,
			ScaledPathArray scaledPathArray2) {

		if (scaledPathArray2.getPaths().size() > 0) {
			
			PathRetrieverTask retriever = new PathRetrieverTask(this);
			retriever.execute(scaledPathArray2.getPathsArray());
		}

	}

	@Override
	protected void configureButtonActivation() {
		

		if (lastHighlightedComponent != null) {
			((ImageButton)fda.findViewById(R.id.annotate_clearButton)).setEnabled(true);
		} else {
			((ImageButton)fda.findViewById(R.id.annotate_clearButton)).setEnabled(false);
		}
		

	}

	protected void assignAnnotationToSelectedObject() {
		DrawingCompositeWord annotation;

		ArrayList<DrawingComponent> annotations = new ArrayList<DrawingComponent>();
		
		if(lastHighlightedComponent != null && newPaths.size() > 0)
		{

		if (lastHighlightedComponent.getAnnotation() != null) {
			annotations = lastHighlightedComponent.getAnnotationList();
		}

		for (CustomPath path : newPaths) {
			path.transform(inverse);

			annotations.add(new DrawingWordLetter(path, inverse));

		}

		newPaths.clear();

		annotation = new DrawingCompositeWord(annotations, "annotation");

		drawingObjects.removeComponent(lastHighlightedComponent);

		lastHighlightedComponent.setAnnotation(annotation);

		drawingObjects.addComponent(lastHighlightedComponent);
		}

	}
	
	

}
