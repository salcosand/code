package com.drawing.datastructure;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import com.drawing.application.MainActivity;
import com.drawing.gestures.Point;

/**
 * abstract component class for the SubClassRelation
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */

public class SubClassRelation extends CustomObjectRelation
{
	transient private Paint arrowPaint;
	transient private Path arrow;

	private int backgroundColor;
	
	transient protected Paint paint;
	transient protected Paint backgroundPaint;
	
	transient private RectF strokeWidthRect;

	private static final long serialVersionUID = 6359953629961716313L;

	public SubClassRelation(CustomPath path, Matrix pathMatrix, DrawingComponent startElement, DrawingComponent endElement)
	{
		super(path, pathMatrix, startElement, endElement);
	
		paint = new Paint();
		paint.setAntiAlias(true);

		arrowPaint = new Paint();
		arrowPaint.setAntiAlias(true);
		arrowPaint.setColor(Color.WHITE);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setStrokeJoin(Join.ROUND);
		arrowPaint.setStrokeWidth(4f);
		arrowPaint.setAlpha(alpha);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);

	}
	
	public void updateArrow(Canvas canvas, Matrix matrix, Point center, Point endArrow)
	{
		RectF tempRect = new RectF();

		path.computeBounds(tempRect, true);

		if (!path.isHighlighted()) 
			matrix.mapRect(tempRect);
		
		RectF r = new RectF(center.x, center.y, endArrow.x, endArrow.y);
		Point arrowCenterPoint = new Point(r.centerX(), r.centerY());
		
		Matrix rotateMatrix = new Matrix();
		
		tempRect = new RectF((float)(arrowCenterPoint.x-(0.5*tempRect.width())), (float)(arrowCenterPoint.y-(0.5*tempRect.height())), (float)(arrowCenterPoint.x+(0.5*tempRect.width())), (float)(arrowCenterPoint.y+(0.5*tempRect.height())));
		
		Point start = new Point((float)(tempRect.left+(0.25*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point end = new Point((float)(tempRect.left+(0.75*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point top = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.25*tempRect.height())));
		Point bottom = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.75*tempRect.height())));
		
		arrow = new Path();
		arrow.moveTo(end.x, end.y);
		arrow.lineTo(top.x, top.y);
		arrow.lineTo(end.x, end.y);
		arrow.lineTo(bottom.x, bottom.y);
		arrow.lineTo(end.x, end.y);

		rotateMatrix.setRotate((float)(-90+this.angle),arrowCenterPoint.x, arrowCenterPoint.y);
		
		arrow.transform(rotateMatrix);
		
		arrowPaint.setStrokeWidth(3f);
		arrowPaint.setColor(Color.BLACK);
		arrowPaint.setAlpha(alpha);
		canvas.drawPath(arrow, arrowPaint);
	}
	
	public void updateArrow(Canvas canvas, Matrix matrix)
	{
		RectF tempRect = new RectF();

		path.computeBounds(tempRect, true);

		//if (!path.isHighlighted()) 
			matrix.mapRect(tempRect);
		
		Point center = this.getCenterPoint();
		Point endArrow = this.getEndElementCenterPoint();
		
		float[] points = {center.x, center.y, endArrow.x, endArrow.y};
		matrix.mapPoints(points);
		
		RectF r = new RectF(points[0], points[1], points[2], points[3]);

		Point arrowCenterPoint = new Point(r.centerX(), r.centerY());

		Matrix rotateMatrix = new Matrix();
		
		tempRect = new RectF((float)(arrowCenterPoint.x-(0.5*tempRect.width())), (float)(arrowCenterPoint.y-(0.5*tempRect.height())), (float)(arrowCenterPoint.x+(0.5*tempRect.width())), (float)(arrowCenterPoint.y+(0.5*tempRect.height())));
		
		Point start = new Point((float)(tempRect.left+(0.25*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point end = new Point((float)(tempRect.left+(0.75*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point top = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.25*tempRect.height())));
		Point bottom = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.75*tempRect.height())));

		arrow = new Path();
		arrow.moveTo(end.x, end.y);
		arrow.lineTo(top.x, top.y);
		arrow.lineTo(end.x, end.y);
		arrow.lineTo(bottom.x, bottom.y);
		arrow.lineTo(end.x, end.y);
		
		rotateMatrix.setRotate((float)(-90+this.angle),arrowCenterPoint.x, arrowCenterPoint.y);
		
		arrow.transform(rotateMatrix);
		
		arrowPaint.setStrokeWidth(3f);
		arrowPaint.setColor(Color.BLACK);
		arrowPaint.setAlpha(alpha);
		canvas.drawPath(arrow, arrowPaint);
	}
	
	public void updateHelpText()
	{
		String endString = "EMPTY CONCEPT";
		String startString = "EMPTY CONCEPT";
		
		if (!endElement.getItemText().equalsIgnoreCase("")) startString = endElement.getItemText();
		if (!startElement.getItemText().equalsIgnoreCase("")) endString = startElement.getItemText();
		
		helpText = startString + " " + "is sub concept of" + " " + endString;
		
		endElement.setHelpText("is sub concept of " + endString);

	}
	
	public void drawRelationIcon(Canvas canvas, Matrix matrix)
	{
		
		strokeWidthRect = new RectF(0,0, 4f, 4f);
		
		RectF tempRect = new RectF();

		path.computeBounds(tempRect, true);

		if (!path.isHighlighted()) matrix.mapRect(tempRect);
		matrix.mapRect(strokeWidthRect);

		float tempTextSize = (float) (tempRect.height() * 0.8);
		
		Point textPos = new Point((float)(tempRect.left+(0.225*tempRect.width())), (float) (tempRect.top+(0.775*tempRect.height())));
		
		paint.setTextSize(tempTextSize);  

		if (isOpen)
			paint.setColor(Color.GRAY);
		else 
			paint.setColor(Color.WHITE);

		paint.setAlpha(alpha);
		canvas.drawText("C", textPos.x, textPos.y, paint);
		
	}
	

	public void drawPropertyRelationHelpText(Canvas canvas, Matrix matrix)
	{
		if (!helpText.equalsIgnoreCase(""))
		{
			if (isOpen)
			{
				int stringCount = helpText.length();
				
				RectF tempRect = new RectF();

				path.computeBounds(tempRect, true);

				matrix.mapRect(tempRect);
				
				Point circleCenterPoint = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));

				float rTop = (float) (circleCenterPoint.y-tempRect.height()/2);
				float rLeft = circleCenterPoint.x;
				float rRight = (float) (circleCenterPoint.x + tempRect.height() +(stringCount*(0.425 * tempRect.height()/2)));
				float rBottom = (float) (circleCenterPoint.y+tempRect.height()/2);

				RectF helpBackground = new RectF(rLeft, rTop,rRight, rBottom);

				backgroundPaint.setStyle(Style.FILL);
				backgroundPaint.setColor(backgroundColor);
				backgroundPaint.setAlpha(alpha);
				
				canvas.drawRect(helpBackground, backgroundPaint);
				
				backgroundPaint.setStyle(Style.STROKE);
				backgroundPaint.setStrokeWidth(2f);
				backgroundPaint.setColor(path.getColor());
				backgroundPaint.setAlpha(alpha);
				
				canvas.drawRect(helpBackground, backgroundPaint);
				
				float tempTextSize = (float) (tempRect.height()*0.40);
				
				Point textPos = new Point((float)(circleCenterPoint.x+ 0.75*tempRect.height()), (float) (circleCenterPoint.y+(tempRect.height()*0.15)));
				
				paint.setTextSize(tempTextSize);  

				paint.setColor(Color.BLACK);

				paint.setAlpha(alpha);
				canvas.drawText(helpText, textPos.x, textPos.y, paint);
			}	
		}
		else 
		{
			isOpen = false;
		}
	}
	
	/**
	 * @see com.drawing.datastructure.DrawingLeaf#redrawPathsafterDeserialization()
	 */
	public void redrawPathsafterDeserialization(MainActivity ma)
	{
		paint = new Paint();
		paint.setAntiAlias(true);

		arrowPaint = new Paint();
		arrowPaint.setAntiAlias(true);
		arrowPaint.setColor(Color.WHITE);
		arrowPaint.setStyle(Style.STROKE);
		arrowPaint.setStrokeJoin(Join.ROUND);
		arrowPaint.setStrokeWidth(4f);
		arrowPaint.setAlpha(alpha);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		
		super.redrawPathsafterDeserialization(ma);
	}
	
	public void resetHelpText()
	{
		endElement.setHelpText("");
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor)
	{
		backgroundPaint.setColor(backgroundColor);
		this.backgroundColor = backgroundColor;
	}
}
