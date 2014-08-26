package com.drawing.ontosketch.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.drawing.ontosketch.R;


/**
 * Round BackgroundIcon
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class PropertyIconBackground extends ImageView 
{
	private int color;
	
	public float alpha;

	private Paint paint;
	
	public PropertyIconBackground(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		alpha = 255;
		
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.concept));
		paint.setAntiAlias(true);
	}
	
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawCircle(20, 20, 20, paint);
		
		super.onDraw(canvas);
	}

	
	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		paint.setColor(color);
		this.color = color;
	}
	
	
	public float getAlpha()
	{
		return alpha;
	}

	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
}
