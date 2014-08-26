package com.drawing.gestures;

import java.io.Serializable;
import android.util.FloatMath;

/**
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * 
 */
public class Point implements Serializable{

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3967609775364774967L;

	/**
	 * x coordinate of the <b>Point</b> object
	 */
	public float x;
	
	/**
	 * y coordinate of the <b>Point</b> object
	 */
	public float y;

	/**
	 * constructor of the <b>Point</b> object
	 * @param x x coordinate of the <b>Point</b> object
	 * @param y y coordinate of the <b>Point</b> object
	 */
	public Point(float x, float y) 
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * constructor of the <b>Point</b> object that initializes an empty <b>Point</b> object
	 * x and y coordinates are set to 0
	 */
	public Point() 
	{
		x = 0;
		y = 0;
	}

	/**
	 * calculate the distance between two point objects
	 * @param other instance of the <b>Point</b> object that's distance should be calculated
	 * @return distance between the respective <b>Point</b> object and this one
	 */
	public float distance(Point other)
	{
		return FloatMath.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));		
	}


}
