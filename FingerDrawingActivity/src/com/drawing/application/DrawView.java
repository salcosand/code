package com.drawing.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SumPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.drawing.datastructure.CustomConstants;
import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingComponent;
import com.drawing.datastructure.DrawingComposite;
import com.drawing.datastructure.DrawingCompositeWord;
import com.drawing.datastructure.DrawingGroup;
import com.drawing.datastructure.DrawingLeaf;
import com.drawing.datastructure.DrawingWordLetter;
import com.drawing.datastructure.GestureTypes;
import com.drawing.datastructure.PersonObject;
import com.drawing.datastructure.ScaledPathArray;
import com.drawing.datastructure.TopicObject;
import com.drawing.datastructure.TopicType;
import com.drawing.datastructure.TopicTypes;
import com.drawing.gestures.Point;
import com.drawing.gestures.Recognizer;
import com.drawing.gestures.Result;
import com.drawing.test2.R;
import com.phatware.android.RecoInterface.WritePadAPI;

/**
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */

@TargetApi(13)
public class DrawView extends GeneralView implements OnTouchListener,
		FingerDrawingActivity.OnInkViewListener {

	static private LinearLayout alternativeWordListView;

	private static RelativeLayout topicTypeSelectionMenu;

	private float margin = new Float(50.0);
	
	/**
	 * minimal vertex count of a path that should be recognized
	 */
	static final int MINRECOGNITIONLENGTH = 10;

	/**
	 * Threshold for checking if a zoom event was detected
	 */
	static final float threshold = 50f;

	/**
	 * Set to true if the pan mode was enabled by the System menu
	 */
	private boolean scaleView = false;

	/**
	 * helper array for displaying and storing temporary paths
	 */
	List<CustomPath> newPaths = new ArrayList<CustomPath>();

	/**
	 * Annotation management object
	 */
	ScaledPathArray annotationArray = new ScaledPathArray();

	/**
	 * Helper object for immediate display of new paths
	 */
	CustomPath tempPath = new CustomPath();

	Paint defaultPaint = new Paint();

	/**
	 * The <b>Paint</b> for Text (handwriting recognition output)
	 */
	Paint textPaint = new Paint();

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
	float scale = 1f;

	/**
	 * The time span that a single touch interaction did last
	 */
	float downTime = -1;

	/**
	 * modified $1 Gesture Recognizer
	 */
	Recognizer recognizer;
	/**
	 * Result of the modified $1 Gesture Recognizer
	 */
	Result result;
	/**
	 * Input points for the modified $1 gesture recognizer
	 */
	List<Point> pointsForRecognizer;

	/**
	 * debug object
	 */
	Paint debugPaint = new Paint();
	Paint debugFilledPaint = new Paint();
	
	/**
	 * debug object
	 */
	List<Point[]> debugList = new ArrayList<Point[]>();

	/**
	 * Color of the canvas' background
	 */
	private int[] canvasColorARGB = { 255, 238, 221, 130 };

	/**
	 * true if the annotation mode is enabled
	 */
	private boolean ANNOTATIONMODE;

	private boolean changeAlternatives;

	private List<CustomPath> tempHandwritingPaths;

	private TopicObject selectedTopicObject = null;

	private boolean gestureRecognitionEnabled;

	private RectF gradientRect;
	private Paint gradientPaint;
	private RadialGradient gradient;
	private boolean drawGradient;
	
	private RectF tempBoundingFillBg = new RectF();
	
	private RectF tempBoundingNewPath = new RectF();
	
	private RectF tempBoundingDebug = new RectF();
	
	private boolean drawDebugRegions = false;

	private float[] m3;
	private float[] m2;
	
	/**
	 * Setter that validates to true if the gradient is drawn upon the canvas of
	 * the {@link DrawView}
	 * 
	 * @param drawGradient
	 *            true if the gradient is drawn
	 */
	public void setDrawGradient(boolean drawGradient) {
		this.drawGradient = drawGradient;
	}

	/**
	 * Getter that validates to true if the gradient is drawn upon the canvas of
	 * the {@link DrawView}
	 * 
	 * @return true if the gradient is drawn
	 * 
	 */
	public boolean isDrawGradient() {
		return drawGradient;
	}

	private float[] personObjectAnchor;

	/**
	 * Constructor of the DrawView
	 * 
	 * @param context
	 * @param attrSet
	 * @see android.widget.ImageView
	 */
	public DrawView(Context context, AttributeSet attrSet) {
		super(context, attrSet);

		ANNOTATIONMODE = false;
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		// initialize stroke paints
		paint.setColor(getResources().getColor(R.color.sketch));
		// paint.setAlpha(150);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);

		defaultPaint = paint;

		// tint path effect
		PathEffect effect = new CornerPathEffect(20);
		PathEffect randomLines = new DiscretePathEffect(5, 2);
		PathEffect dashEffect = new DashPathEffect(new float[] { 2, 1, 5, 0, 3,
				8, 7, 4, 2, 3 }, 5);
		PathEffect sumEffect = new SumPathEffect(effect, randomLines);

		@SuppressWarnings("unused")
		PathEffect composeEffect = new SumPathEffect(dashEffect, sumEffect);
		// paint.setPathEffect(composeEffect);

		highlightedPaint.setColor(getResources().getColor(R.color.highlight));
		highlightedPaint.setAntiAlias(true);
		highlightedPaint.setStyle(Paint.Style.STROKE);
		highlightedPaint.setStrokeWidth(3);

		highlightedPaint.setPathEffect(effect);
		paint.setPathEffect(effect);

		// setup recognizer
		recognizer = new Recognizer();
		result = null;
		pointsForRecognizer = new ArrayList<Point>();

		debugPaint.setColor(Color.RED);
		debugPaint.setAntiAlias(true);
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setStrokeWidth(1f);
		//debugPaint.setPathEffect(effect);

		debugFilledPaint.setColor(getResources().getColor(R.color.inactiveGray));
		debugFilledPaint.setAntiAlias(true);
		debugFilledPaint.setStyle(Paint.Style.FILL);
		debugFilledPaint.setStrokeWidth(1f);
		
		
		drawingObjects = new DrawingComposite(null, null);
		drawingObjects.setIsRoot(true);

		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(30);
		textPaint.setStrokeWidth(3);

		tempHandwritingPaths = new ArrayList<CustomPath>();
		changeAlternatives = false;



		gestureRecognitionEnabled = true;

		gradientRect = new RectF(-screenWidth, -screenHeight, 4 * screenWidth - screenWidth, 4
				* screenHeight - screenHeight);
		gradientPaint = new Paint();
		gradientPaint.setStyle(Paint.Style.FILL);
		gradientPaint.setShader(gradient);

		personObjectAnchor = new float[2];

		inverse = new Matrix();

		backupInverse = new Matrix();

		drawPath = new CustomPath();

		// Debug.startMethodTracing("sketch1");

	}

	private CustomPath drawPath = new CustomPath();

	

	/**
	 * onDraw method that is called after each invalidate() - call
	 */
	protected void onDraw(Canvas canvas) {
		
		Log.d("DrawView", "onDraw");

		float[] m = new float[9];

		matrix.getValues(m);
		
		// paint.setColor(Color.DKGRAY);

		// drawPath = new CustomPath();

		matrix.invert(inverse);

		backupTransformationMatrix.invert(backupInverse);

		// canvas.drawARGB(canvasColorARGB[0], canvasColorARGB[1],
		// canvasColorARGB[2], canvasColorARGB[3]);

//		canvas.clipRect(0, 0, screenWidth, screenHeight);

		// canvas.setMatrix(new Matrix());

//		canvas.save();

//		if (calculateCanvasSize) {
//			// canvasHeight = canvas.getClipBounds().height();
//
//			float[] scale = new float[] { canvas.getHeight(), 0 };
//
//			inverse.mapPoints(scale);
//
//			canvasHeight = (int) scale[0];
//
//			calculateCanvasSize = false;
//		}
		
		canvas.drawRect(tempBoundingFillBg, debugFilledPaint);
		
		canvas.drawRect(tempBoundingNewPath, debugPaint);
		canvas.drawRect(tempBoundingDebug, debugPaint);
		
		// apply transformations only to highlighted path objects
		if (drawingObjects.getPathList().ContainsHighlightedPath())
		{
			Log.d("DrawView", "drawing highlighted objects");
			
//			if (drawGradient) {
//				backupTransformationMatrix.mapRect(gradientRect);
//
//				gradient = new RadialGradient(gradientRect.centerX(),
//						gradientRect.centerY(), gradientRect.height() / 2,
//						 fda.getResources().getColor(android.R.color.background_dark), fda.getResources().getColor(android.R.color.background_light), Shader.TileMode.CLAMP);
//				gradientPaint.setShader(gradient);
//				
//				
//				canvas.drawRect(gradientRect, gradientPaint);
//				backupInverse.mapRect(gradientRect);
//			}
			
			Log.d("DrawView", "drawingObjects_size: " + drawingObjects.getPathList().getPaths().size());

			// draw highlighted paths
			for (int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++)
			{

				drawPath = drawingObjects.getPathList().getPath(i);

				// matchPaint(drawPath.getType());

				paint.setColor(drawPath.getColor());

				// apply the inverse matrix of the canvas upon the path

				if (drawPath.isHighlighted())
				{

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

//				if (paint.getColor() != Color.BLACK)
//					paint.setColor(Color.BLACK);
			}

			if (drawingObjects.ContainsContactObjects()) {

				drawingObjects.drawContactObjects(canvas, matrix,
						backupTransformationMatrix, true);
			}

		} else {

			Log.d("DrawView", "drawing stored objects");
			
//			if (drawGradient) {
//				matrix.mapRect(gradientRect);
//
//				gradient = new RadialGradient(gradientRect.centerX(),
//						gradientRect.centerY(), gradientRect.height() / 2,
//						fda.getResources().getColor(android.R.color.background_dark), fda.getResources().getColor(android.R.color.background_light), Shader.TileMode.CLAMP);
//				gradientPaint.setShader(gradient);
//
//				canvas.drawRect(gradientRect, gradientPaint);
//				inverse.mapRect(gradientRect);
//			}

			paint.setColor(getResources().getColor(R.color.highlight));
			
			if (!tempPath.isEmpty() && mode == DRAW) {
				// paint.setColor(Color.DKGRAY);
				canvas.drawPath(tempPath, paint);
			}

			// canvas.setMatrix(matrix);

//			canvas.restore();
			
			m2 = new float[9];
			matrix.getValues(m2);

			Log.d("DrawView", "drawingObjects_size: " + drawingObjects.getPathList().getPaths().size());
			
			// draw stored paths
			for (int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++)
			{
					drawPath = drawingObjects.getPathList().getPath(i);
					
					Log.d("DrawView", "dP: " + drawPath.toString());

					// matchPaint(drawPath.getType());

					paint.setColor(drawPath.getColor());

					// apply the inverse matrix of the canvas upon the path

					drawPath.transform(matrix);

					canvas.drawPath(drawPath, paint);

					// restore the initial path object with the original
					// transformation
					// Matrix

					drawPath.transform(inverse);
					
				}

//			canvas.restore();
			
			m3 = new float[9];
			matrix.getValues(m3);
			
			// attach new paths
			for (int j = 0; j < newPaths.size(); j++)
			{
				paint.setColor(getResources().getColor(R.color.activeSketch));
				
				canvas.drawPath(newPaths.get(j), paint);
				
			}
			

			if (drawingObjects.ContainsContactObjects()) {

				drawingObjects.drawContactObjects(canvas, matrix,
						backupTransformationMatrix, false);

			}

		}

		// DEBUG ///////////////////////

		
		// draw bounding boxes of paths for debugging purposes
					 
		if (drawDebugRegions)
		{
			RectF rect = new RectF();
			
			if(drawingObjects.getPathList().getPaths() != null)
			{

				//int i = drawingObjects.getPathList().getPaths().size()-1;
				
				//if (i >= 0)
				for(int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++)
				{

					drawingObjects.getPathList().getPath(i).computeBounds(rect, true);
					
					CustomPath cp = new CustomPath();

					cp.moveTo(rect.left, rect.top);
					cp.lineTo(rect.left, rect.bottom);
					cp.lineTo(rect.right, rect.top);
					cp.lineTo(rect.right, rect.bottom);
					cp.lineTo(rect.left, rect.top);
					
					cp.moveTo(rect.left, rect.top);
					cp.lineTo(rect.right, rect.top);
					cp.moveTo(rect.left, rect.bottom);
					cp.lineTo(rect.right, rect.bottom);

					cp.transform(matrix);
				
					canvas.drawPath(cp, debugPaint);
				}
			}
		}
		
		// DEBUG ///////////////////////
		
		float[] m4 = new float[9];
		matrix.getValues(m4);
		
		
		for (int i = 0; i<=8; i++)
		{
			Log.d("DrawView_draw", m[i] + "  " + m2[i] + " " + m3[i] + "  " + m4[i]);
		}

		//canvas.restore();

		super.onDraw(canvas);
	}


	/**
	 * Handler for all tracked touch events upon the <b>DrawView</b>
	 * 
	 * @param v
	 * @param event
	 * @return true if the method was successfully passed
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 *      android.view.MotionEvent)
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
//		Log.d("DrawView", "onTouch");

		try {

			// store historical motion event coordinates
			historicalX = x;
			historicalY = y;

			x = event.getX(0);
			y = event.getY(0);

			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			// invoked as soon as a touch input is registered
			case MotionEvent.ACTION_DOWN:
				
//				Log.d("DrawView", "MotionEvent.ACTION_DOWN");

				historicalX = -1;
				historicalY = -1;

				savedMatrix.set(matrix);

				start.set(x, y);

				mode = DRAW;

				// test if path objects are in the input area, if the pan mode
				// is
				// enabled and a double tap was performed

				float timedifference = -1;

				if (historicalEventTime > 0)
					timedifference = event.getEventTime() - historicalEventTime;

				if (timedifference < 300 && timedifference > -1) {
					doubleTap = true;
					matrix.set(savedMatrix);
					RetrievePossiblePathMatch(event,
							drawingObjects.getPathList());
					historicalEventTime = -1;
				}

				historicalEventTime = event.getEventTime();

				// //handwriting recognition
				// mCurrStroke = WritePadAPI.recoNewStroke( 3, 0xFFFF0000 );
				// if(mCurrStroke >= 0)
				// {
				// WritePadAPI.recoAddPixel(mCurrStroke, x, y);
				// }

				break;

			// invoked as soon as a second touch input is recognized
			// simultaneously
			// to the first one
			case MotionEvent.ACTION_POINTER_DOWN:
				
//				Log.d("DrawView", "MotionEvent.ACTION_POINTER_DOWN");

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
				
//				Log.d("DrawView", "MotionEvent.ACTION_POINTER_UP");

				scaleView = false;

				doubleTap = false;

				// stay in drag - mode if the flag is set
				if (mode == PANZOOM) {

					savedMatrix.set(matrix);

				}
				// switch back to standard drawing mode
				else
					mode = DRAW;

				pointsForRecognizer.clear();
				firstPointOfPath = true;
				tempPath = new CustomPath();

				calculateCanvasSize = true;
				
				break;

			// invoked in case the touch input is moving
			case MotionEvent.ACTION_MOVE:
				
				Log.d("DrawView", "MotionEvent.ACTION_MOVE");

				if (topicTypeSelectionMenu.getVisibility() != RelativeLayout.INVISIBLE) {
					topicTypeSelectionMenu
							.setVisibility(RelativeLayout.INVISIBLE);
					// selectedTopicObject = null;
				}

				doubleTap = false;

				// matrix.set(savedMatrix);

				if (mode == PANZOOM) {

					Log.d("DrawView", "mode == PANZOOM: MotionEvent.ACTION_MOVE");
					
					tempBoundingFillBg = new RectF();

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
						
						float[] m = new float[9];
						matrix.getValues(m);

						if (scaleView) {
							matrix.set(savedMatrix);
							// scale canvas according to detected scale
							// operation
							matrix.postScale(scale, scale, mid.x, mid.y);

							// translate canvas according to the detected pan
							// operation
							matrix.postTranslate(x - start.x, y - start.y);
							
							//Log.d("DrawView", (x - start.x) + " " + (y - start.y) + matrix);
							
						} else {
							matrix.set(savedMatrix);
							matrix.postTranslate(x - start.x, y - start.y);
							
							//Log.d("DrawView", (x - start.x) + " " + (y - start.y) + );
						}
						
						float[] m2 = new float[9];
						matrix.getValues(m2);
						
						for (int i = 0; i<=8; i++)
						{
							Log.d("DrawView_touch", m[i] + "  " + m2[i]);
						}

					}

				}

				break;

			case MotionEvent.ACTION_UP:
				
				RectF tempBoundingCheck = new RectF();
				tempBoundingFillBg = new RectF();
				
				Log.d("DrawView", "MotionEvent.ACTION_UP");
				
				if (!doubleTap && mode == DRAW)
				{	
					//check if new Word
					
//					Log.d("DrawView", "1_tempHandwritingPaths.size() "+ tempHandwritingPaths.size() + " " + margin);
					
					if (pointsForRecognizer.size() > MINSTROKELENGTH && tempHandwritingPaths.size() > 0)
					{
						tempBoundingCheck = new RectF();
						tempBoundingFillBg = new RectF();
						
						RectF r = new RectF();
						
						for (CustomPath cp : tempHandwritingPaths)
						{
							cp.computeBounds(r, true);
							
							tempBoundingFillBg.union(r);
						}
						
						CustomPath cpCalc = new CustomPath();

						cpCalc.moveTo(tempBoundingFillBg.left, tempBoundingFillBg.top);
						cpCalc.lineTo(tempBoundingFillBg.left, tempBoundingFillBg.bottom);
						cpCalc.lineTo(tempBoundingFillBg.right, tempBoundingFillBg.bottom);
						cpCalc.lineTo(tempBoundingFillBg.right, tempBoundingFillBg.top);
						cpCalc.lineTo(tempBoundingFillBg.left, tempBoundingFillBg.top);

						cpCalc.transform(matrix);
						
						cpCalc.computeBounds(tempBoundingCheck, true);
						
						tempBoundingCheck.left = tempBoundingCheck.left-margin;
						tempBoundingCheck.right = tempBoundingCheck.right+2*margin;
						tempBoundingCheck.top = tempBoundingCheck.top-margin;
						tempBoundingCheck.bottom = tempBoundingCheck.bottom+margin;

						boolean inRegion = false;
						
						for (Point p : pointsForRecognizer)
						{
							if (tempBoundingCheck.contains(p.x, p.y))
							{
								inRegion = true;
							}
						}
						
						Log.d("DrawView", "inRegion "+ inRegion);
						
						if (!inRegion) 
						{
							confirmPlaceholderWord();
							tempBoundingCheck = new RectF();
							tempBoundingFillBg = new RectF();
						}
						
					}

					CustomPath lastAddedPath =  new CustomPath();
					CustomPath path = new CustomPath();
					boolean first = true;

					// recognize gestures that consist of at least 10 Points
					if (pointsForRecognizer.size() > MINSTROKELENGTH)
					{

						for (Point point : pointsForRecognizer) {

							if (first) {
								path.moveTo(point.x, point.y);
								lastAddedPath.moveTo(point.x, point.y);
								first = false;
							}

							path.lineTo(point.x, point.y);
							lastAddedPath.lineTo(point.x, point.y);

						}
						
						calculateVisualBundingRect(lastAddedPath);

						newPaths.add(path);

						CustomPath[] pathArray = new CustomPath[] { path };

						asyncRecognizeGestues(pathArray);

						recognizeHandwriting(pointsForRecognizer, path);
						
					}

					tempPath = new CustomPath();

					firstPointOfPath = true;

					pointsForRecognizer.clear();

				}
				else
				{
					calculateVisualBundingRect(null);
				}


				break;

			}

			if (!doubleTap)
			{
				if (mode == DRAW
						&& !drawingObjects.getPathList()
								.ContainsHighlightedPath())
					// draw path but ensure that the user doesn't zoom
					if (event.getAction() == MotionEvent.ACTION_MOVE
							&& mode != PANZOOM)
					{
						
						Log.d("DrawView", "event.getAction() == MotionEvent.ACTION_MOVE&& mode != PANZOOM");
						
						paint.setColor(getResources().getColor(R.color.highlight));

						Point point = new Point();

						point.x = (int) x;

						point.y = (int) y;

						if (((int) historicalX) != point.x
								&& ((int) historicalY) != point.y) {

							pointsForRecognizer.add(point);

							if (firstPointOfPath) {
								tempPath.moveTo(point.x, point.y);
								firstPointOfPath = false;
							} else {
								tempPath.lineTo(point.x, point.y);
							}

						}

					}
			}
			
			// delete path if canvas was zoomed
			if (mode == PANZOOM) {
				pointsForRecognizer.clear();
				newPaths.clear();
				firstPointOfPath = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		invalidate();

		return true;

	}

	private void calculateVisualBundingRect(CustomPath lastAddedPath)
	{

		tempBoundingFillBg = new RectF();
		
		if (tempHandwritingPaths.size() == 0 && lastAddedPath == null)
		{
			return;
		}
		

		RectF r = new RectF();
		
		
		if (lastAddedPath != null)
		{
			lastAddedPath.computeBounds(r, true);

			CustomPath cpLastPath = new CustomPath();

			cpLastPath.moveTo(r.left, r.top);
			cpLastPath.lineTo(r.left, r.bottom);
			cpLastPath.lineTo(r.right, r.bottom);
			cpLastPath.lineTo(r.right, r.top);
			cpLastPath.lineTo(r.left, r.top);

			cpLastPath.transform(inverse);
			
			cpLastPath.computeBounds(r, true);
			
			tempBoundingFillBg.union(r);
		}
		
		for (CustomPath cp : tempHandwritingPaths)
		{
			cp.computeBounds(r, true);
			
			tempBoundingFillBg.union(r);
		}
		
		CustomPath tcp = new CustomPath();

//			tcp.moveTo(tempBoundingFillBg.left-margin, tempBoundingFillBg.top-margin);
//			tcp.lineTo(tempBoundingFillBg.left-margin, tempBoundingFillBg.bottom+margin);
//			tcp.lineTo(tempBoundingFillBg.right+2*margin, tempBoundingFillBg.bottom+margin);
//			tcp.lineTo(tempBoundingFillBg.right+2*margin, tempBoundingFillBg.top-margin);
//			tcp.lineTo(tempBoundingFillBg.left-margin, tempBoundingFillBg.top-margin);
		
		tcp.moveTo(tempBoundingFillBg.left, tempBoundingFillBg.top);
		tcp.lineTo(tempBoundingFillBg.left, tempBoundingFillBg.bottom);
		tcp.lineTo(tempBoundingFillBg.right, tempBoundingFillBg.bottom);
		tcp.lineTo(tempBoundingFillBg.right, tempBoundingFillBg.top);
		tcp.lineTo(tempBoundingFillBg.left, tempBoundingFillBg.top);

		tcp.transform(matrix);
		
		tcp.computeBounds(tempBoundingFillBg, true);
		
		tempBoundingFillBg.left = tempBoundingFillBg.left-margin;
		tempBoundingFillBg.right = tempBoundingFillBg.right+2*margin;
		tempBoundingFillBg.top = tempBoundingFillBg.top-margin;
		tempBoundingFillBg.bottom = tempBoundingFillBg.bottom+margin;
		
		
		//tcp.computeBounds(tempBoundingR2, true);

		
		Log.d("DrawView", "================================================");
		
		Log.d("DrawView", "Width R1 "+ tempBoundingFillBg.width());
		Log.d("DrawView", "Height R1 "+ tempBoundingFillBg.height());
		Log.d("DrawView", "left - right R1 "+ tempBoundingFillBg.left + " " + tempBoundingFillBg.right);
		Log.d("DrawView", "top - bottom R1 "+ tempBoundingFillBg.top + " " + tempBoundingFillBg.bottom);
		Log.d("DrawView", "================");
//			Log.d("DrawView", "Width R2 "+ tempBoundingR2.width());
//			Log.d("DrawView", "Height R2 "+ tempBoundingR2.height());
//			Log.d("DrawView", "left - right R2 "+ tempBoundingR2.left + " " + tempBoundingR2.right);
//			Log.d("DrawView", "top - bottom R2 "+ tempBoundingR2.top + " " + tempBoundingR2.bottom);
		
		Log.d("DrawView", "================================================");
		
	}
	
	/**
	 * Submit the point data to the RecognizerService in order to recognize the
	 * letters that were drawn out of the respective list of points
	 * 
	 * @param pointsForRecognizer2
	 *            The list of points that deliver the basis for the recognition
	 * @param path
	 *            The path object that was created out of the respective list of
	 *            points
	 */
	private void recognizeHandwriting(List<Point> pointsForRecognizer2,
			CustomPath path) {

		// handwriting recognition

		if (pointsForRecognizer2.size() > MINSTROKELENGTH) {

			mCurrStroke = WritePadAPI.recoNewStroke(3, 0xFFFF0000);

			for (Point point : pointsForRecognizer2) {
				if (mCurrStroke >= 0) {
					WritePadAPI.recoAddPixel(mCurrStroke, point.x, point.y);
				}
			}

			mCurrStroke = -1;
			nStrokeCnt = WritePadAPI.recoStrokeCount();

			tempHandwritingPaths.add(path);

			// notify recognizer thread about data availability
			fda.mBoundService.dataNotify(nStrokeCnt);
		}

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

		if (scaledPathArray2.getPaths().size() > 0)
		{
		PathRetrieverTask retriever = 	new PathRetrieverTask(this);
		retriever.execute(scaledPathArray2.getPathsArray());
		}

	}

	boolean firstBackup = true;



	/**
	 * Translate the transformations of highlighted path objects back to the
	 * canvas matrix
	 * 
	 * @param minDistanceIndex
	 *            Index of the detected selected path
	 */
	protected void updatePath(CustomPath path) {

		if (path != null) {

			DrawingComponent component = null;
			component = drawingObjects.getObjectByPathId(path.getUid());

			if(component != null)
			if (!path.isHighlighted()) {

				component.updatePath(matrix, backupTransformationMatrix, true);

			} else {

				// update object dependencies

				if (!component.isGrouped())

				{

					if (!(component instanceof DrawingWordLetter)) {

						drawingObjects.removeComponent(component);

						component.updatePath(matrix,
								backupTransformationMatrix, false);

						if (component.isComposite) {
							((DrawingComposite) component).deleteChildren();
						}

						drawingObjects.addComponent(component);

					} else {

						component = ((DrawingWordLetter) component).getParent();

						drawingObjects.removeCompositeComponent(component);

						component.updatePath(matrix,
								backupTransformationMatrix, false);

						drawingObjects.addComponent(component);
					}

				} else {

					if (component instanceof DrawingWordLetter) {
						component = component.getParent();
					}

					component = component.getParent();

					drawingObjects.removeCompositeComponent(component);

					component.updatePath(matrix, backupTransformationMatrix,
							false);

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

	/**
	 * Asynchronous gesture recognizer
	 * 
	 * @author Christian Brändel TU Dresden / SAP Research Dresden
	 * @see android.os.AsyncTask
	 * 
	 */
	// private class RecognizerTask extends AsyncTask<Point, Integer, Result> {
	private class RecognizerTask extends AsyncTask<CustomPath, Integer, Result> {

		boolean asyncGestureRecognition = gestureRecognitionEnabled;

		CustomPath examinedPath = new CustomPath();
		Matrix examinedInverseTransformationMatrix = new Matrix();
		
		int topicSizeThreshold = 120;

		protected void onPreExecute() {

			matrix.invert(examinedInverseTransformationMatrix);
		}

		protected Result doInBackground(CustomPath... paths) {

			examinedPath = paths[0];

			List<Point> params = examinedPath.getVertices();

			//TODO: Neukonfigurieren der Grenzen mit anshcließenden Tests
			
			// Decide whether to reset word recognition and enable gesture
			// recognition

			if (examinedPath.getVertices().size() > 25) {

				if (tempHandwritingPaths.size() > 1) {

					CustomPath referencePath = tempHandwritingPaths.get(0);

					float[] extrema = new float[] { referencePath.minX,
							referencePath.minY, referencePath.maxX,
							referencePath.maxY };

					matrix.mapPoints(extrema);

					float tolerance = (extrema[3] - extrema[1]) * 1.5f;
					if (tolerance > 80) {
						tolerance = 100;
					}

					if ((examinedPath.minY < extrema[1] - tolerance || (examinedPath.maxY > extrema[3]
							+ tolerance))
							&&
							// examined Path exceeds upper or lower tolerance
							// border
							// of previously drawn elements
							!((examinedPath.minY >= extrema[1]) && (examinedPath.maxY <= extrema[3])))
					// examined path is not located between the tolerance
					// borders

					{

						asyncGestureRecognition = true;

					} else {

						asyncGestureRecognition = false;

					}

				} else {

					asyncGestureRecognition = true;

				}

			} else {
				asyncGestureRecognition = false;
			}

			// only initiate recognition if no word is written at this very
			// moment
			if (params.size() > MINRECOGNITIONLENGTH && asyncGestureRecognition) {
				return recognizer.Recognize(params);
			} else
				return new Result("- none - ", 0.0f, 1.0f,
						GestureTypes.NOGESTURE);
		}

		protected void onPostExecute(Result result) {

			DrawingComponent object = null;

			switch (result.getType()) {
			case NOGESTURE:

				examinedPath.transform(examinedInverseTransformationMatrix);
				examinedPath.setColor(getResources().getColor(R.color.activeSketch));
				examinedPath.setType(GestureTypes.NOGESTURE);
				object = new DrawingLeaf(examinedPath,
						examinedInverseTransformationMatrix);

				break;
			case TOPIC:
				
				// NOTE: stroke can't be excluded from handwriting recognition,
				// because it could be
				// that one of the letters "o", "O" or "0" was written -> has to
				// be investigated
				// within the respective context
				// for this reason a minimum size for Topics is introduced
				
				int x,y = 0;
				
				x= (int) (examinedPath.maxX - examinedPath.minX);
				y = (int) (examinedPath.maxY - examinedPath.minY);

				if((x)>topicSizeThreshold && (y)>topicSizeThreshold)
				{
				
				examinedPath.transform(examinedInverseTransformationMatrix);
				examinedPath.setType(GestureTypes.TOPIC);
				object = new TopicObject(examinedPath,
						examinedInverseTransformationMatrix);

				displayTopicTypeOptions((TopicObject) object);
				
				}else
				{
					examinedPath.transform(examinedInverseTransformationMatrix);
					examinedPath.setType(GestureTypes.NOGESTURE);
					object = new DrawingLeaf(examinedPath,
							examinedInverseTransformationMatrix);
					result.setName("- none - ");
					result.setType(GestureTypes.NOGESTURE);
				}



				break;

			case PERSON:

				examinedPath.transform(examinedInverseTransformationMatrix);
				examinedPath.setType(GestureTypes.PERSON);

				personObjectAnchor[0] = examinedPath.minX;

				personObjectAnchor[1] = examinedPath.minY;

				matrix.mapPoints(personObjectAnchor);

				launchContactImport();

				break;

			case MATCHCONTACT:

				DrawingCompositeWord contact = null;

				RectF bounds = new RectF();

				examinedPath.computeBounds(bounds, false);

				examinedInverseTransformationMatrix.mapRect(bounds);

				contact = drawingObjects.retrieveWordMatch(bounds);

				if (contact != null)
					if (contact.getResult() != null) {

						examinedPath
								.transform(examinedInverseTransformationMatrix);

						personObjectAnchor[0] = examinedPath.minX;

						personObjectAnchor[1] = examinedPath.minY;

						matrix.mapPoints(personObjectAnchor);

						matchResultWithPhoneBook(contact.getResult(), contact);

					}

				break;

			}

			if (object != null)
				drawingObjects.addComponent(object);

			newPaths.remove(examinedPath);

			// clean View if the gesture Recognition is enabled, because word
			// suggestions would still be displayed
			if ((asyncGestureRecognition && result.getType() != GestureTypes.NOGESTURE)) {

				cleanView();

			} else {
				// if (((asyncGestureRecognition && tempHandwritingPaths.size()
				// >= 1) && result
				// .getType() == GestureTypes.NOGESTURE)) {

				// cleanView();
				//
				// recognizeHandwriting(examinedPath.getVertices(),
				// examinedPath);
				// } else {

				// manage undo button

				if (tempHandwritingPaths.size() > 0) {

					fda.findViewById(R.id.undoButton).setEnabled(true);

				} else {
					fda.findViewById(R.id.undoButton).setEnabled(false);
				}

			}

			invalidate();

			// if (!result.getName().contains("rectangle")) {
			if (result.getName().contains("rectangle")
					|| result.getName().contains("circle")
					|| result.getName().contains("triangle")) {
				Toast toast = Toast.makeText(getContext(),
						"Composite object recognized!", Toast.LENGTH_LONG);
				toast.show();
			}
		}

	}

	/**
	 * Start the asynchronous gesture recognition
	 * 
	 * @param pointsForRecognizer2
	 *            The <b>Point</b> array
	 */
	private void asyncRecognizeGestues(CustomPath[] paths) {

		new RecognizerTask().execute(paths);

	}


	/**
	 * clear the canvas or delete highlighted elements selectively
	 */
	public void clear() {

		// delete whole canvas
		// if (!drawingObjects.getPathList().ContainsHighlightedPath()) {
		// // this.scaledPathArray.clear();
		// drawingObjects.deleteChildren();
		//
		// cleanView();
		// }

		nStrokeCnt = WritePadAPI.recoStrokeCount();

		CustomPath path;

		DrawingComponent component;

		while (drawingObjects.getPathList().ContainsHighlightedPath()) {

			for (int i = 0; i < drawingObjects.getPathList().getPaths().size(); i++) {

				path = drawingObjects.getPathList().getPaths().get(i);

				if (path.isHighlighted()) {

					component = drawingObjects.getObjectByPathId(path.getUid());

					// delete object if it is part of a written but not
					// confirmed word or sketch

					if (nStrokeCnt > 0
							&& ((component instanceof DrawingLeaf) || (component instanceof TopicObject))) {

						// remove stroke from the set that was delivered for
						// handwriting recognition

						int index = WritePadAPI.recoGetStrokeByPoint(
								path.getOriginalVertices()[0].x,
								path.getOriginalVertices()[0].y);

						if (index > -1) {

							WritePadAPI.recoDeleteStrokeByIndex(index);

							tempHandwritingPaths.remove(index);

						}

					}

					if (component.isGrouped()) {
						component = component.getParent();
						drawingObjects.removeCompositeComponent(component);
					} else if (component instanceof DrawingWordLetter) {
						component = component.getParent();

						drawingObjects.removeCompositeComponent(component);
					} else {

						if (component instanceof PersonObject)
							((PersonObject) component).getContactPhoto()
									.recycle();

						drawingObjects.removeComponent(component);
					}

					// scaledPathArray.removePath(scaledPathArray.getPaths()
					// .get(i));
				}
			}
		}

		nStrokeCnt = WritePadAPI.recoStrokeCount();

		if (nStrokeCnt == 0)
			cleanView();
		else {
			// notify recognizer thread about data availability
			fda.mBoundService.dataNotify(nStrokeCnt);
		}

		restoreBackupTransformationMatrix();

		configureButtonActivation();

		invalidate();

	}

	/**
	 * Delete the last attached <b>CustomPath</b> object from the respective
	 * <b>ScaledPathArray</b> object
	 * 
	 * @see com.drawing.datastructure.ScaledPathArray
	 * @see com.drawing.datastructure.CustomPath
	 */
	public void undo() {

		// TODO: implement real history (maybe via an Array in
		// TODO: delete whole word if the last action was to create one
		// the DrawingComponent root)

		if (drawingObjects.getPathList().getPaths().size() > 0) {

			int lastPathIndex = -1;

			if (tempHandwritingPaths.size() > 0) {

				lastPathIndex = tempHandwritingPaths.size() - 1;

				CustomPath undoPath = tempHandwritingPaths.get(lastPathIndex);

				DrawingComponent component = drawingObjects
						.getObjectByPathId(undoPath.getUid());

				Point point = new Point(0.0f, 0.0f);

				if (component != null)
					point = component.getPath().getOriginalVertices()[0];

				int index = WritePadAPI.recoGetStrokeByPoint(point.x, point.y);

				if (index > -1) {

					WritePadAPI.recoDeleteStrokeByIndex(index);
					tempHandwritingPaths.remove(lastPathIndex);
					drawingObjects.removeComponent(component);

				}
			}

			nStrokeCnt = WritePadAPI.recoStrokeCount();
			// notify recognizer thread about data availability
			fda.mBoundService.dataNotify(nStrokeCnt);

			if (nStrokeCnt > 0) {
				configureButtonActivation();
				invalidate();
			} else
				cleanView();

		} else {

			nStrokeCnt = WritePadAPI.recoStrokeCount();
			// notify recognizer thread about data availability
			fda.mBoundService.dataNotify(nStrokeCnt);

			cleanView();

		}
	}

//	/**
//	 * backup the current transformation matrix in order to enable selective
//	 * manipulations of paths
//	 */
//	private void backupCurrentTransformationMatrix() {
//		backupTransformationMatrix.set(matrix);
//	}
//
//	/**
//	 * restore the formerly saved transformation matrix in order to conduct
//	 * arbitrary transformations to the whole canvas again
//	 */
//	private void restoreBackupTransformationMatrix() {
//		matrix.set(backupTransformationMatrix);
//	}

	/**
	 * Enable the modification mode and change the color of the canvas in order
	 * to visualize the mode change
	 * 
	 * @param enabled
	 *            true if the modification mode should be enabled
	 */
	public void toggleModificationMode(boolean enabled) {

		if (enabled) {
			paint.setColor(Color.WHITE);
			canvasColorARGB[1] = 255 - canvasColorARGB[1];
			canvasColorARGB[2] = 255 - canvasColorARGB[2];
			canvasColorARGB[3] = 255 - canvasColorARGB[3];
		} else {
			paint.setColor(Color.BLACK);
			canvasColorARGB[1] = 255 - canvasColorARGB[1];
			canvasColorARGB[2] = 255 - canvasColorARGB[2];
			canvasColorARGB[3] = 255 - canvasColorARGB[3];

		}

		invalidate();
	}

	/**
	 * Enable the annotation mode and change the visualization of the canvas in
	 * order to show the mode change
	 * 
	 * @param enabled
	 *            true if the annotation mode should be enabled
	 */
	public void toggleAnnotationMode(boolean enabled) {

		if (enabled) {
			ANNOTATIONMODE = true;
		} else {
			ANNOTATIONMODE = false;
		}

		invalidate();

	}

	private Set<String> mCurrMessage;

	// begin region PhatWare

	private int nStrokeCnt;

	private int mCurrStroke;

	/**
	 * Reset the handwriting recognizer and respective help structures
	 * 
	 * @see com.drawing.application.FingerDrawingActivity.OnInkViewListener#cleanView()
	 */
	public void cleanView() {
		
//		if (tempHandwritingPaths.size() > 0)
		{

//			ArrayList<DrawingComponent> wordChildren = new ArrayList<DrawingComponent>();

			// remove strokes from temporary drawing objects and attach them to
			// a composite word
			for (CustomPath path : tempHandwritingPaths) {
				
				path.setColor(getResources().getColor(R.color.sketch));
				
//				drawingObjects.removeComponent(drawingObjects
//						.getObjectByPathId(path.getUid()));
//				DrawingWordLetter wordLetter = new DrawingWordLetter(path,
//						new Matrix());
//				wordChildren.add(wordLetter);
			}

//			DrawingCompositeWord compositeWord = new DrawingCompositeWord(
//					wordChildren, "PLACEHOLDER");
//
//			drawingObjects.addComponent(compositeWord);
		}


		WritePadAPI.recoResetInk();
		tempHandwritingPaths.clear();
		mCurrMessage = null;
		mCurrStroke = -1;
		
		tempBoundingFillBg = new RectF();

		gestureRecognitionEnabled = true;

		hideSuggestionButtons();

		configureButtonActivation();

		invalidate();

	}

	/**
	 * Toggle button activation according to possible actions
	 */
	protected void configureButtonActivation() {

		int highlightedComponents = ((DrawingComposite) drawingObjects)
				.getHighlightedComponents().size();

		// manage group button

		// attach the respective functionality and the suiting icon to the
		// button
		// depending on whether the selected objects can be grouped or ungrouped

		List<CustomPath> highlightedPaths = drawingObjects
				.getHighlightedComponents();

		DrawingComponent component;

		boolean switchGroupButton = false;

		int groupCount = 0;

		for (CustomPath path : drawingObjects.getHighlightedComponents()) {

			component = drawingObjects.getObjectByPathId(path.getUid());

			if (component instanceof DrawingGroup) {

				groupCount++;

				for (DrawingComponent child : ((DrawingGroup) component).children) {

					if (!(child instanceof DrawingCompositeWord))
						highlightedPaths.remove(child.getPath());

					else
						try {
							highlightedPaths
									.remove(((DrawingCompositeWord) child).children
											.get(0).getPath());
						} catch (IndexOutOfBoundsException indEx) {
							indEx.printStackTrace();
						}

				}

				highlightedPaths.remove(component.getPath());

				if (highlightedPaths.size() == 0 && !(groupCount > 1)) {
					switchGroupButton = true;
					break;
				}
			}

		}

		if (switchGroupButton) {

			fda.findViewById(R.id.groupButton).setVisibility(Button.INVISIBLE);

			fda.findViewById(R.id.ungroupButton).setVisibility(Button.VISIBLE);

		} else

		{
			fda.findViewById(R.id.ungroupButton)
					.setVisibility(Button.INVISIBLE);

			fda.findViewById(R.id.groupButton).setVisibility(Button.VISIBLE);
		}

		if (highlightedComponents > 1) {
			fda.findViewById(R.id.groupButton).setEnabled(true);
		} else {
			fda.findViewById(R.id.groupButton).setEnabled(false);
		}

		// manage delete button
		if (highlightedComponents >= 1) {
			fda.findViewById(R.id.clearButton).setEnabled(true);
		} else {
			fda.findViewById(R.id.clearButton).setEnabled(false);
		}

		// manage undo button

		if (tempHandwritingPaths.size() > 0) {

			fda.findViewById(R.id.undoButton).setEnabled(true);

		} else {
			fda.findViewById(R.id.undoButton).setEnabled(false);
		}

	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	private final Handler mHandler = new Handler() {

		/**
		 * Handle the message including the recognition result from the
		 * <b>RecognizerService</b>
		 */
		public void handleMessage(Message msg) {

			if (!msg.getData().isEmpty()) {

				mCurrMessage = msg.getData().keySet();

				changeAlternatives = true;

			}

			if (mCurrMessage != null && changeAlternatives) {
				changeAlternatives = false;

				for (int i = 0; i < alternativeWordListView.getChildCount(); i++) {
					alternativeWordListView.getChildAt(i).setVisibility(
							Button.INVISIBLE);
				}

				Button btn = null;

				int counter = 0;

				for (String key : mCurrMessage) {

					if (key != "result") {

						switch (counter) {

						case 0:
							btn = (Button) alternativeWordListView
									.getChildAt(0);
							break;
						case 1:
							btn = (Button) alternativeWordListView
									.getChildAt(1);
							break;
						case 2:
							btn = (Button) alternativeWordListView
									.getChildAt(2);
							break;
						case 3:
							btn = (Button) alternativeWordListView
									.getChildAt(3);
							break;
						case 4:
							btn = (Button) alternativeWordListView
									.getChildAt(4);
							break;
						case 5:
							btn = (Button) alternativeWordListView
									.getChildAt(5);
							break;

						}

						counter++;

						if (btn != null) {
							btn.setText(key);
							btn.setVisibility(Button.VISIBLE);

							if (gestureRecognitionEnabled)
								if (key.length() > 1
										&& (!key.contains("O")
												&& !key.contains("o") && !key
													.contains("0"))) {

									gestureRecognitionEnabled = false;

								}

						}

					}

				}
			}

			invalidate();
		}

	};

	/**
	 * @see android.view.View#getHandler()
	 */
	public Handler getHandler() {
		return mHandler;
	}

	// end region PhatWare

	/**
	 * Set the panel for alternative words and initialize the respective button
	 * listeners for handling confirmation requests
	 * 
	 * @param alternativeWordListView
	 *            The LinearLayout object holding the buttons with alternative
	 *            word suggestions
	 */
	public void initializeAlternativeWordListView(
			LinearLayout alternativeWordListView) {

		DrawView.alternativeWordListView = alternativeWordListView;

		OnClickListener alternativeWordButtonListener = new View.OnClickListener() {

			public void onClick(View v) {

				Button btn = (Button) v;

				confirmWord(btn.getText().toString());

			}
		};

		for (int i = 0; i < alternativeWordListView.getChildCount(); i++) {

			alternativeWordListView.getChildAt(i).setOnClickListener(
					alternativeWordButtonListener);

		}

	}

	/**
	 * The type that was selected within the type selection dialog
	 */
	public TopicType selectedTopicType;

	private void assignSelectedTypeToCompositeObject(TopicType topicType) {

		selectedTopicObject.setType(topicType);

	}

	/**
	 * Set the panel for topic type selection and initialize the respective
	 * button listeners for handling confirmation requests
	 * 
	 * @param topicTypeSelectionMenu
	 *            The RelativeLayout object holding the buttons with topic type
	 *            suggestions
	 */
	public void initializeTypeSelectionMenu(
			RelativeLayout topicTypeSelectionMenu) {

		DrawView.topicTypeSelectionMenu = topicTypeSelectionMenu;

		OnClickListener topicTypeSelectionListener = new View.OnClickListener() {

			public void onClick(View v) {

				if (selectedTopicObject != null) {

					if (v instanceof Button) {

						Button btn = (Button) v;

						if (btn instanceof Button) {

							if (btn.getText() == fda
									.getText(R.string.OrganisationTypeLabel)) {
								selectedTopicObject.setType(fda.topicTypeList
										.get(1));
								selectedTopicObject.getPath().setType(
										GestureTypes.ORGANIZATIONTYPE);
							}

							if (btn.getText() == fda
									.getText(R.string.TopicTypeLabel)) {
								selectedTopicObject.setType(fda.topicTypeList
										.get(0));
								selectedTopicObject.getPath().setType(
										GestureTypes.TOPICTYPE);
							}
						}

					} else {

						ImageButton ibtn = (ImageButton) v;

						if (ibtn instanceof ImageButton) {

							if (ibtn.getId() == R.id.NewButton) {

								selectedTopicObject.setType(fda.topicTypeList
										.get(2));
								selectedTopicObject.getPath().setType(
										GestureTypes.NEWTYPE);
							}
							
							if(ibtn.getId() == R.id.TopicButton)
							{
								selectedTopicObject.setType(fda.topicTypeList
										.get(0));
								selectedTopicObject.getPath().setType(
										GestureTypes.TOPICTYPE);
							}
							
							if(ibtn.getId() == R.id.OrganisationButton)
							{
								selectedTopicObject.setType(fda.topicTypeList
										.get(1));
								selectedTopicObject.getPath().setType(
										GestureTypes.ORGANIZATIONTYPE);
							}

							if (ibtn.getId() == R.id.SelectionButton) {

								final Spinner listBox = new Spinner(fda);

								String[] listItems = new String[fda.topicTypeList
										.size()];

								int counter = 0;

								for (TopicType type : fda.topicTypeList) {
									listItems[counter] = type.getName();
									counter++;
								}

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										fda,
										android.R.layout.simple_spinner_item,
										listItems);

								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

								listBox.setAdapter(adapter);

								listBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int pos, long id) {

										Object item = parent
												.getItemAtPosition(pos);

										for (TopicType type : fda.topicTypeList) {
											if (type.getName().compareTo(
													item.toString()) == 0) {
												selectedTopicType = type;
											}
										}

										// name of the selected item
										item.toString();

									}

									public void onNothingSelected(
											AdapterView<?> parent) {
									}
								});

								AlertDialog.Builder assignTypeDialogBuilder = new AlertDialog.Builder(
										fda);

								assignTypeDialogBuilder
										.setTitle("Type Selection");

								assignTypeDialogBuilder
										.setMessage("Choose an existing type or create a new one!");

								assignTypeDialogBuilder.setView(listBox);

								assignTypeDialogBuilder.setPositiveButton(
										"Select",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												assignSelectedTypeToCompositeObject(selectedTopicType);

											}

										});

								assignTypeDialogBuilder.setNeutralButton(
										"New Type",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {

												AlertDialog.Builder createNewTypeDialogBuilder = new AlertDialog.Builder(
														fda);

												createNewTypeDialogBuilder
														.setTitle("Create new Type");

												createNewTypeDialogBuilder
														.setMessage("Specify the properties of the new type!");

												LayoutInflater factory = LayoutInflater
														.from(fda);

												final View createTypeView = factory
														.inflate(
																R.layout.new_type_creation_view,
																null);

												createTypeView
														.setMinimumHeight((int) (screenHeight * 0.75));

												createNewTypeDialogBuilder
														.setView(createTypeView);

												createNewTypeDialogBuilder
														.setPositiveButton(
																"Save",
																new DialogInterface.OnClickListener() {

																	public void onClick(
																			DialogInterface dialog,
																			int which) {

																		Button button = (Button) createTypeView
																				.findViewById(
																						R.id.type_selected_color_view);
																																				
																		
																		int color = ((ColorDrawable)button.getBackground()).getColor();

																		String name = ((EditText) createTypeView
																				.findViewById(R.id.type_name_text))
																				.getText()
																				.toString();

																		String shortcut = ((EditText) createTypeView
																				.findViewById(R.id.type_shortcut_text))
																				.getText()
																				.toString();

																		String description = ((EditText) createTypeView
																				.findViewById(R.id.type_description_text))
																				.getText()
																				.toString();

																		TopicType topicType = new TopicType(
																				name,
																				color,
																				description,
																				TopicType
																						.generateID(),
																				shortcut);

																		fda.topicTypeList
																				.add(topicType);

																		fda.saveTypeList();

																		assignSelectedTypeToCompositeObject(topicType);

																	}
																});

												createNewTypeDialogBuilder
														.setNegativeButton(
																"Abort",
																new DialogInterface.OnClickListener() {

																	public void onClick(
																			DialogInterface dialog,
																			int which) {


																		
																	}
																});

												// set listeners for the buttons
												// responsible for picking a
												// topic color

												OnClickListener colorPickerListener = new OnClickListener() {

													public void onClick(View v) {

														switch (v.getId()) {

														case R.id.button_type_blue:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.blue);

															break;

														case R.id.button_type_lightblue:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.lightblue);

															break;

														case R.id.button_type_green:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.green);

															break;

														case R.id.button_type_lightgreen:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.lightgreen);

															break;

														case R.id.button_type_orange:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.orange);

															break;

														case R.id.button_type_lightorange:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.lightorange);

															break;

														case R.id.button_type_pink:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.pink);

															break;

														case R.id.button_type_red:

															createTypeView
																	.findViewById(
																			R.id.type_selected_color_view)
																	.setBackgroundResource(
																			R.color.red);

															break;

														}

													}
												};

												AlertDialog newTypeDialog = createNewTypeDialogBuilder
														.create();

												newTypeDialog.show();

												// assign listener to all color
												// picker buttons

												LinearLayout colorPickerLayout = ((LinearLayout) createTypeView
														.findViewById(R.id.color_picker_layout));

												for (int i = 0; i < colorPickerLayout
														.getChildCount(); i++) {
													colorPickerLayout
															.getChildAt(i)
															.setOnClickListener(
																	colorPickerListener);
												}

											}
										});

								assignTypeDialogBuilder.setNegativeButton(
										"Abort",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												
												// deletion of topic types for debugging
//												fda.topicTypeList.remove(selectedTopicType);
//												fda.saveTypeList();

												return;
																								
											}
										});

								AlertDialog newTypeAssignDialog = assignTypeDialogBuilder
										.create();

								newTypeAssignDialog.show();

							}
						}
					}

				}

				DrawView.topicTypeSelectionMenu
						.setVisibility(RelativeLayout.INVISIBLE);
				// selectedTopicObject = null;

			}
		};

		for (int i = 0; i < DrawView.topicTypeSelectionMenu.getChildCount(); i++) {

			DrawView.topicTypeSelectionMenu.getChildAt(i).setOnClickListener(
					topicTypeSelectionListener);

		}
	}

	/**
	 * Display the context menu for topic tape selection This menu is only
	 * displayed for {@link DrawingComposite}s
	 * 
	 * @param object
	 */
	private void displayTopicTypeOptions(TopicObject object) {

		selectedTopicObject = object;

		float x = object.getPath().getVertices()
				.get(object.getPath().getVertices().size() - 1).x;
		float y = object.getPath().getVertices()
				.get(object.getPath().getVertices().size() - 1).y;

		float[] point = new float[] { x, y };

		matrix.mapPoints(point);

		float[] translation = new float[2];

		translation[0] = topicTypeSelectionMenu.getWidth() / 2;
		translation[1] = topicTypeSelectionMenu.getHeight() / 2;

		topicTypeSelectionMenu.setX(point[0] - translation[0]);
		topicTypeSelectionMenu.setY(point[1] - translation[1]);

		topicTypeSelectionMenu.setVisibility(RelativeLayout.VISIBLE);

	}

	/**
	 * Hide all buttons that display a suggestion for the handwriting
	 * recognition
	 */
	private void hideSuggestionButtons() {

		for (int i = 0; i < alternativeWordListView.getChildCount(); i++) {

			alternativeWordListView.getChildAt(i).setVisibility(
					Button.INVISIBLE);

		}

	}

	/**
	 * Add the recognized word to the data structure after it was confirmed by
	 * the user
	 * 
	 * @param word
	 *            The recognized String that will be attached to the respective
	 *            strokes
	 */
	public void confirmWord(String word) {

		if (tempHandwritingPaths.size() > 0) {

			ArrayList<DrawingComponent> wordChildren = new ArrayList<DrawingComponent>();

			// remove strokes from temporary drawing objects and attach them to
			// a composite word
			for (CustomPath path : tempHandwritingPaths) {
				
				path.setColor(getResources().getColor(R.color.sketch));
				
				drawingObjects.removeComponent(drawingObjects
						.getObjectByPathId(path.getUid()));
				DrawingWordLetter wordLetter = new DrawingWordLetter(path,
						new Matrix());
				wordChildren.add(wordLetter);
			}

			DrawingCompositeWord compositeWord = new DrawingCompositeWord(
					wordChildren, word);

			drawingObjects.addComponent(compositeWord);

			Toast toast = Toast.makeText(getContext(), "The word '" + word
					+ "' was recognized!", Toast.LENGTH_LONG);
			toast.show();

			tempBoundingFillBg = new RectF();
			
			cleanView();

		}

	}
	
	/**
	 * Add the recognized word to the data structure after it was confirmed by
	 * the user
	 * 
	 * @param word
	 *            The recognized String that will be attached to the respective
	 *            strokes
	 */
	public void confirmPlaceholderWord() {

		if (tempHandwritingPaths.size() > 0)
		{

			ArrayList<DrawingComponent> wordChildren = new ArrayList<DrawingComponent>();

			// remove strokes from temporary drawing objects and attach them to
			// a composite word
			for (CustomPath path : tempHandwritingPaths) {
				
				path.setColor(getResources().getColor(R.color.sketch));
				
				drawingObjects.removeComponent(drawingObjects
						.getObjectByPathId(path.getUid()));
				DrawingWordLetter wordLetter = new DrawingWordLetter(path,
						new Matrix());
				wordChildren.add(wordLetter);
			}

			DrawingCompositeWord compositeWord = new DrawingCompositeWord(
					wordChildren, "PLACEHOLDER");

			drawingObjects.addComponent(compositeWord);

//			Toast toast = Toast.makeText(getContext(), "The word '" + word
//					+ "' was recognized!", Toast.LENGTH_LONG);
//			toast.show();

			tempBoundingFillBg = new RectF();
			
			cleanView();

		}

	}

	/**
	 * Compare the recognized word with contact names in the address book and in
	 * case that a match is found, suggest to import this contact
	 * 
	 * @param word
	 *            The recognized word that was written by the user
	 */
	private void matchResultWithPhoneBook(String word,
			DrawingCompositeWord contactWord) {

		ContentResolver contentResolver = fda.getContentResolver();

		ArrayList<PersonObject> matchingContacts = new ArrayList<PersonObject>();

		Uri uri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(word));

		// define the columns I want the query to return
		String[] projection = new String[] {
				ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.PhoneLookup._ID,
				ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI };

		try {

			Cursor cursor = getContext().getContentResolver().query(uri,
					projection, null, null, null);

			PersonObject person;

			// test whether there is a contact whose name can be associated with
			// the written term
			if (cursor.moveToFirst()) {
				cursor.moveToPrevious();
				while (cursor.moveToNext()) {
					// if (cursor.moveToFirst()) {

					String contactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					String contactName = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					String phoneNumber = "";
					Uri photoThumbnailUri = null;
					String eMail = "";

					Cursor phoneCursor = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { contactId }, null);

					if (phoneCursor.moveToFirst()) {
						contactName = phoneCursor
								.getString(phoneCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						phoneNumber = phoneCursor
								.getString(phoneCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

						try {

							String uriString = phoneCursor
									.getString(phoneCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

							photoThumbnailUri = Uri.parse(uriString);

						} catch (NullPointerException npe) {
							npe.printStackTrace();
							photoThumbnailUri = null;
						}
					}

					Cursor eMailCursor = contentResolver.query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID
									+ " = ?", new String[] { contactId }, null);

					if (eMailCursor.moveToFirst()) {
						eMail = eMailCursor
								.getString(eMailCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
					}

					Bitmap bitmap = null;

					if (photoThumbnailUri != null)
						try {
							bitmap = MediaStore.Images.Media.getBitmap(
									contentResolver, photoThumbnailUri);
						} catch (FileNotFoundException e) {

							e.printStackTrace();
						} catch (IOException e) {

							e.printStackTrace();
						}

					person = new PersonObject(new CustomPath(), new Matrix(),
							contactName, phoneNumber, eMail, bitmap,
							photoThumbnailUri, 1);

					matchingContacts.add(person);

				}

				// while(cursor.moveToNext())
				// {
				//
				// contactId =
				// cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
				// name =
				// cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				//
				// }

				// prompt that shows available alternatives to the users that
				// match the search term

				// addContact(person);

				person = null;

				fda.showMatchingContactsSelection(matchingContacts, contactWord);

			} else {
				// a contact with the queried name could not be found in the
				// devices address book
				// the possibility to attach a new contact to the address book
				// has to be offered

				fda.createNewContact(word);

			}
		} catch (IllegalArgumentException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	/**
	 * This method adjusts the view port of the canvas so that every stroke that
	 * has been drawn is contained
	 */
	public void adjustViewPort() {

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

	private void launchContactImport() {

		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);

		fda.startActivityForResult(intent, CustomConstants.PICK_CONTACT);

		// Debug.stopMethodTracing();

	}

	/**
	 * Add an object of type <b>PersonObject</b> to the objects that are drawn
	 * within this view
	 * 
	 * @param personObject
	 *            an object containing contact information that was imported
	 *            from the Android system
	 */
	public void addContact(PersonObject personObject) {

		invalidate();

		CustomPath path = new CustomPath();

		try {

			if (personObject.getContactPhoto() == null) {

				BitmapFactory.Options bfOptions = new BitmapFactory.Options();

				bfOptions.inDither = false; // Disable Dithering mode

				bfOptions.inPurgeable = true; // Tell to gc that whether it
												// needs free memory, the Bitmap
												// can be cleared

				bfOptions.inInputShareable = true; // Which kind of reference
													// will be used to recover
													// the Bitmap data after
													// being clear, when it will
													// be used in the future

				bfOptions.inTempStorage = new byte[32 * 1024];

				Bitmap contactPhoto = BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_contact_picture5,
						bfOptions);

				contactPhoto.setDensity(DisplayMetrics.DENSITY_LOW);

				personObject.setContactPhoto(contactPhoto);

				personObject.setImageUri(null);

			}

			float photoScale = 1.0f;

			photoScale = (float) canvasHeight / screenHeight;

			PersonObject completePersonObject = new PersonObject(path, matrix,
					personObject.getContactName(),
					personObject.getPhoneNumber(), personObject.geteMail(),
					personObject.getContactPhoto(), personObject.getImageUri(),
					photoScale);

			completePersonObject.setPosition(personObjectAnchor[0],
					personObjectAnchor[1]);

			drawingObjects.addComponent(completePersonObject);

		} catch (OutOfMemoryError oome) {
			Toast toast = Toast.makeText(fda.getApplicationContext(),
					"Unfortunately the application ran out of memory",
					Toast.LENGTH_LONG);
			
			toast.show();

			oome.printStackTrace();
		}

	}

	/**
	 * Group highlighted objects together so that they can be treated uniformly
	 * regardless of their physical location
	 */
	public void groupHighlightedObjects() {

		ArrayList<DrawingComponent> componentsToAdd = new ArrayList<DrawingComponent>();

		if (drawingObjects.getHighlightedComponents().size() > 1) {

			ArrayList<DrawingComponent> componentList = new ArrayList<DrawingComponent>();

			DrawingComponent component;

			for (CustomPath path : drawingObjects.getHighlightedComponents()) {

				component = drawingObjects.getObjectByPathId(path.getUid());

				if (component != null) {

					if (component instanceof DrawingGroup) {

						componentList
								.addAll(((DrawingGroup) component).children);

						drawingObjects.removeCompositeComponent(component);
					} else

					if (component instanceof DrawingWordLetter) {

						component = ((DrawingWordLetter) component).getParent();

						component.setGrouped(true);

						if (!componentList.contains(component)) {

							componentList.add(component);

							drawingObjects.removeCompositeComponent(component);

						}

					} else {

						if (component instanceof DrawingComposite) {

							for (DrawingComponent child : ((DrawingComposite) component)
									.getLinearChildObjectList()) {
								if (!componentList.contains(child)
										&& child.isHighlighted()) {
									child.setGrouped(true);

									if ((child instanceof TopicObject)) {

										((TopicObject) child).children = new ArrayList<DrawingComponent>();

										componentList.add(child);

									} else {

										if (!(child instanceof DrawingGroup))
											componentList.add(child);

									}

									if (child instanceof DrawingCompositeWord) {

										drawingObjects
												.removeCompositeComponent(child);

									} else
										drawingObjects.removeComponent(child);

								} else {

									componentsToAdd.add(child);

								}
							}

						} else {

							component.setGrouped(true);

							if (!componentList.contains(component)) {

								componentList.add(component);

								drawingObjects.removeComponent(component);

							}
						}

					}

				}
			}

			for (DrawingComponent child : componentsToAdd) {
				drawingObjects.addComponent(child);
			}

			DrawingGroup group = new DrawingGroup(componentList,
					new CustomPath(), new Matrix());

			drawingObjects.addComponent(group);

			cleanView();

		}

	}

	/**
	 * Ungroup highlighted objects and reintegrate them into the existing data
	 * structure
	 */
	public void ungroupHighlightedObjects() {
		
		

		List<CustomPath> highlightedPaths = drawingObjects
				.getHighlightedComponents();

		DrawingComponent component;
				
		removeHighlightFromPaths();
		
		for (CustomPath path : highlightedPaths) {

			component = drawingObjects.getObjectByPathId(path.getUid());

			if (component instanceof DrawingGroup) {

				drawingObjects.removeCompositeComponent(component);

				for (DrawingComponent child : ((DrawingGroup) component).children) {

					child.setGrouped(false);
									
					child.setHighlighted(false);

					drawingObjects.addComponent(child);

				}

			}

		}

		configureButtonActivation();
		

		invalidate();

	}	
	
	/**
	 * Recalculate the screen size in order to handle orientation changes properly
	 */
	public void recalculateScreenSize() {

		Display display = fda.getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);

		screenHeight = size.y;
		screenWidth = size.x;
		canvasHeight = size.y;

	}

}














