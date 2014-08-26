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
 * abstract Component class for the PropertyRelation
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */
public abstract class PropertyRelation extends CustomObjectRelation 
{
	private static final long serialVersionUID = 6454438369422880498L;

	transient private Paint arrowPaint;
	transient private Path arrow;

	private int backgroundColor;
	
	transient protected Paint paint;
	transient protected Paint backgroundPaint;
	
	transient private RectF strokeWidthRect;

	public PropertyRelation(CustomPath path, Matrix pathMatrix, DrawingComponent startElement, DrawingComponent endElement) 
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
	
	public void updateArrow(Canvas canvas, Matrix matrix)
	{
		strokeWidthRect = new RectF(0,0, 4f, 4f);
		
		RectF tempRect = new RectF();
		
		path.computeBounds(tempRect, true);

		if (!path.isHighlighted()) matrix.mapRect(tempRect);
		matrix.mapRect(strokeWidthRect);

		Point circleCenterPoint = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		
		Matrix rotateMatrix = new Matrix();
		
		Point start = new Point((float)(tempRect.left+(0.25*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point end = new Point((float)(tempRect.left+(0.75*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));
		Point top = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.25*tempRect.height())));
		Point bottom = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.75*tempRect.height())));
		
		arrow = new Path();
		arrow.moveTo(start.x, start.y);
		arrow.lineTo(end.x, end.y);
		arrow.lineTo(top.x, top.y);
		arrow.lineTo(end.x, end.y);
		arrow.lineTo(bottom.x, bottom.y);
		arrow.lineTo(end.x, end.y);
		arrow.lineTo(start.x, start.y);
		
		rotateMatrix.setRotate((float)(-90+this.angle),circleCenterPoint.x, circleCenterPoint.y);
		
		arrow.transform(rotateMatrix);
		
		arrowPaint.setStrokeWidth(strokeWidthRect.width());
		arrowPaint.setAlpha(alpha);
		if (path.isVisible()) canvas.drawPath(arrow, arrowPaint);
	}
	
	public void drawPropertyRelation(Canvas canvas, Matrix matrix)
	{
		if (!helpText.equalsIgnoreCase(""))
		{
			if (isOpen && path.isVisible())
			{
				int stringCount = helpText.length();
				
				RectF tempRect = new RectF();

				path.computeBounds(tempRect, true);

				matrix.mapRect(tempRect);
				
				Point circleCenterPoint = new Point((float)(tempRect.left+(0.5*tempRect.width())), (float)(tempRect.top+(0.5*tempRect.height())));

				
				float rTop = (float) (circleCenterPoint.y-tempRect.height()/2);
				float rLeft = circleCenterPoint.x;
				float rRight = (float) (circleCenterPoint.x + tempRect.height() + (stringCount*(0.45 * tempRect.height()/2)));
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

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor)
	{
		backgroundPaint.setColor(backgroundColor);
		this.backgroundColor = backgroundColor;
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
}
