package com.drawing.gestures;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import android.util.FloatMath;

/**
 * The source of several methods was taken from an experimental JAVA
 * implementation of the $1 Recognizer:
 * http://depts.washington.edu/aimgroup/proj/dollar/
 * 
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * @author Norman Papernick
 * 
 */
public class Operations {

	private static float Phi = (float) (0.5 * (-1.0 + Math.sqrt(5.0))); // Golden
																		// Ratio
	private static float Infinity = (float) 1e9;
	
	private static Point tempPoint, centroid;

	/**
	 * Calculate the length of a given path
	 * 
	 * @param points
	 *            array of <b>Point</b> objects that represent a path
	 * @return length of the given path
	 */
	public static float PathLength(List<Point> points) {
		float d = 0.0f;

		Point pt1, pt2;

		for (int i = 1; i < points.size(); i++) {
			pt1 = points.get(i - 1);
			pt2 = points.get(i);

			d += FloatMath.sqrt((pt1.x - pt2.x) * (pt1.x - pt2.x)
					+ (pt1.y - pt2.y) * (pt1.y - pt2.y));
		}
		return d;
	}

	/**
	 * Calculate the distance between two paths that are represented through
	 * arrays of <b>Point</b> objects. The respective arrays have to be of the
	 * same length, because the points are compared pairwise and the resulting
	 * distance is added up to the final value
	 * 
	 * @param pts1
	 *            first <b>Point</b> array
	 * @param pts2
	 *            second <b>Point</b> array
	 * @return added up distance between the two paths that are represented by
	 *         the <b>Point</b> arrays
	 */
	public static float PathDistance(List<Point> pts1, List<Point> pts2) {
		int pts1Size = pts1.size();

		if (pts1Size != pts2.size()) {
			return Infinity;
		}
		float d = 0.0f;

		Point pt1, pt2;

		for (int i = 0; i < pts1Size; i++) {
			pt1 = pts1.get(i);
			pt2 = pts2.get(i);

			d += FloatMath.sqrt((pt1.x - pt2.x) * (pt1.x - pt2.x)
					+ (pt1.y - pt2.y) * (pt1.y - pt2.y));

		}
		return d / (float) pts1Size;
	}

	/**
	 * Calculate a bounding box that fits around a path
	 * 
	 * @param points
	 *            array of <b>Point</b> objects that represents a path
	 * @return the bounding box belonging to the array of <b>Point</b> objects
	 *         in form of a <b>Rectangle</b>
	 * @see com.drawing.gestures.Rectangle
	 */
	public static Rectangle BoundingBox(List<Point> points) {
		float minX = Infinity;
		float maxX = -Infinity;
		float minY = Infinity;
		float maxY = -Infinity;
		
		Point pt = new Point();

		for (int i = 1; i < points.size(); i++) {
			
			pt = points.get(i);
			
			minX = Math.min(pt.x, minX);
			maxX = Math.max(pt.x, maxX);
			minY = Math.min(pt.y, minY);
			maxY = Math.max(pt.y, maxY);
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Calculate the centroid of a given <b>Point</b> array
	 * 
	 * @param points
	 *            array of <b>Points</b> that's centroid should be calculated
	 * @return the centroid for the respective <b>Point</b> array
	 */
	public static Point Centroid(List<Point> points) {
		centroid = new Point(0.0f, 0.0f);

//		Point tempPoint;
		
		int size = points.size();

		for (int i = 1; i < size; i++) {
			tempPoint = points.get(i);
			centroid.x += tempPoint.x;
			centroid.y += tempPoint.y;
		}
		centroid.x /= size;
		centroid.y /= size;
		return centroid;
	}
	
	/**
	 * Rotate an array of <b>Point</b> objects by a given angle
	 * 
	 * @param points
	 *            The array of <b>Point</b> objects that should be rotated
	 * @param theta
	 *            The angle by which the points are going to be rotated
	 * @return The rotated array of <b>Point</b> objects
	 */
	public static List<Point> RotateBy(List<Point> points, float theta) {
		centroid = Centroid(points);
		float Cos = FloatMath.cos(theta);
		float Sin = FloatMath.sin(theta);
		int size = points.size();

		List<Point> newpoints = new ArrayList<Point>(size + 1);

		float qx, qy;
		

		for (int i = 0; i < size; i++) {
			tempPoint = points.get(i);
			qx = (tempPoint.x - centroid.x) * Cos - (tempPoint.y - centroid.y) * Sin + centroid.x;
			qy = (tempPoint.x - centroid.x) * Sin + (tempPoint.y - centroid.y) * Cos + centroid.y;
			newpoints.add(new Point(qx, qy));
		}
		return newpoints;
	}

	/**
	 * Rotating a array of points so that its "indicative angle" (angle between
	 * its first point and its centroid) is at 0°
	 * 
	 * @param points
	 *            The array of <b>Point</b> objects that should be rotated
	 * @return The array of rotated points
	 */
	public static List<Point> RotateToZero(List<Point> points) {
		centroid = Centroid(points);
//		Point
		tempPoint = points.get(0);
		float theta = (float) Math.atan2(centroid.y - tempPoint.y, centroid.x - tempPoint.x);
		return RotateBy(points, -theta);
	}

	/**
	 * Resample an array of <b>Point</b> objects in order to get an array
	 * consisting of n equidistant <b>Point</b> objects
	 * 
	 * @param points
	 *            The array that should be resampled
	 * @param n
	 *            The number of points that should result
	 * @return The respective array that contains <b>n</b> equidistant points
	 *         that describe the same path as the original array of points
	 */
	public static List<Point> Resample(List<Point> points, int n) {
		int size = points.size();
		float I = (float) (PathLength(points) / ((float) n - 1.0));
		float D = 0.0f;
		List<Point> newpoints = new ArrayList<Point>(size + 1);
		Stack<Point> stack = new Stack<Point>();
		for (int i = 0; i < size; i++) {
			stack.push(points.get(size - 1 - i));
		}

		Point pt1, pt2, q;
		
		while (!stack.empty()) {
			pt1 = (Point) stack.pop();

			if (stack.empty()) {
				newpoints.add(pt1);
				continue;
			}
			pt2 = (Point) stack.peek();
			float d = FloatMath.sqrt((pt1.x - pt2.x) * (pt1.x - pt2.x)
					+ (pt1.y - pt2.y) * (pt1.y - pt2.y));
			if ((D + d) >= I) {
				float qx = pt1.x + ((I - D) / d) * (pt2.x - pt1.x);
				float qy = pt1.y + ((I - D) / d) * (pt2.y - pt1.y);
				q = new Point(qx, qy);
				newpoints.add(q);
				stack.push(q);
				D = 0.0f;
			} else {
				D += d;
			}
		}

		if (newpoints.size() == (n - 1)) {
			newpoints.add(points.get(size - 1));
		}
		return newpoints;

	}

	/**
	 * Scale an array of Points in order to fit them into a predefined reference
	 * square
	 * 
	 * @param points
	 *            The array of points that should be fitted
	 * @param sz
	 *            size of the reference square
	 * @return The fitted array of <b>Point</b> objects
	 */
	public static List<Point> ScaleToSquare(List<Point> points, float sz) {
		Rectangle B = BoundingBox(points);
		List<Point> newpoints = new ArrayList<Point>(points.size() + 1);
//		Point tempPoint;

		for (int i = 0; i < points.size(); i++) {
			tempPoint = points.get(i);

			float qx = tempPoint.x * (sz / B.Width);
			float qy = tempPoint.y * (sz / B.Height);
			newpoints.add(new Point(qx, qy));
		}
		return newpoints;
	}

	/**
	 * Iteratively calculate the minimum distance between a path and a
	 * predefined <b>Template</b> object by using GSS - Golden Section Search
	 * 
	 * @param points
	 *            The array of points that is analyzed
	 * @param T
	 *            The predefined gesture template
	 * @param a
	 *            minimum angle boundary (-45° for GSS)
	 * @param b
	 *            maximum angle boundary (45° for GSS)
	 * @param threshold
	 *            threshold that determines angle precision
	 * @return The minimum distance between the respective path and a compared
	 *         predefined <b>Template</b>
	 */
	public static float DistanceAtBestAngle(List<Point> points, Template T,
			float a, float b, float threshold) {
		float x1 = (float) (Phi * a + (1.0 - Phi) * b);
		float f1 = DistanceAtAngle(points, T, x1);
		float x2 = (float) ((1.0 - Phi) * a + Phi * b);
		float f2 = DistanceAtAngle(points, T, x2);
		while (Math.abs(b - a) > threshold) {
			if (f1 < f2) {
				b = x2;
				x2 = x1;
				f2 = f1;
				x1 = (float) (Phi * a + (1.0 - Phi) * b);
				f1 = DistanceAtAngle(points, T, x1);
			} else {
				a = x1;
				x1 = x2;
				f1 = f2;
				x2 = (float) ((1.0 - Phi) * a + Phi * b);
				f2 = DistanceAtAngle(points, T, x2);
			}
		}
		return Math.min(f1, f2);
	}

	/**
	 * Calculate the distance between a path and a <b>Template</b> at a given
	 * angle
	 * 
	 * @param points
	 *            array of <b>Point</b> objects
	 * @param T
	 *            <b>Template</b> predefined gesture template
	 * @param theta
	 *            given rotation angle
	 * @return The calculated distance
	 */
	public static float DistanceAtAngle(List<Point> points, Template T,
			float theta) {
		List<Point> newpoints = RotateBy(points, theta);
		return PathDistance(newpoints, T.Points);
	}

	/**
	 * Translate the whole path, so that its centroid is moved to (0,0)
	 * 
	 * @param points
	 *            The path that should be translated
	 * @return The translated path
	 */
	public static List<Point> TranslateToOrigin(List<Point> points) {
		centroid = Centroid(points);
		List<Point> newpoints = new ArrayList<Point>(points.size() + 1);

//		Point tempPoint;

		for (int i = -0; i < points.size(); i++) {

			tempPoint = points.get(i);

			float qx = tempPoint.x - centroid.x;
			float qy = tempPoint.y - centroid.y;
			newpoints.add(new Point(qx, qy));
		}
		return newpoints;
	}

	/**
	 * Append a <b>Template</b> object to an existing array of <b>Template</b>
	 * objects
	 * 
	 * @param templates
	 *            The array of <b>Template</b> objects
	 * @param template
	 *            The <b>Template</b> object that should be attached
	 * @return The given array with the given <b>Template</b> object attached at
	 *         it's end
	 * @see com.drawing.gestures.Template
	 */
	// public static List<Template> append(List<Template> templates, Template
	// template) {
	//
	//
	// if(templates != null)
	// {
	//
	// Log.d("APPEND", "Append Template");
	//
	// templates.add(template);
	//
	// return templates;
	//
	// }
	//
	// return null;
	//
	//
	// }

	/**
	 * Append a <b>Point</b> object to an existing array of <b>Template</b>
	 * objects
	 * 
	 * @param points
	 *            The array of <b>Point</b> objects
	 * @param point
	 *            The <b>Point</b> object that should be attached
	 * @return The given array with the given <b>Point</b> object attached at
	 *         it's end
	 * @see com.drawing.gestures.Point
	 */
	// public static Point[] append(Point[] points, Point point)
	// {
	//
	// try
	// {
	//
	// Point[] array = new Point[points.length + 1];
	//
	// for(int i=0; i<points.length; i++)
	// {
	// array[i]=points[i];
	// }
	//
	// array[points.length] = point;
	//
	// return array;
	//
	// }catch(Exception e)
	// {
	// Point[] array = new Point[1];
	//
	// array[0] = point;
	//
	// return array;
	// }
	//
	//
	//
	//
	// }

	/**
	 * Select a subset out of a given array of <b>Template</b> objects
	 * 
	 * @param templates
	 *            The array of <b>Template</b> objects
	 * @param offset
	 *            The index where the selection should start
	 * @param length
	 *            The length of the subset
	 * @return The selected subset of <b>Template</b> objects from the
	 *         respective array
	 */
	public static List<Template> subset(List<Template> templates, int offset,
			int length) {

		List<Template> subsetList = new ArrayList<Template>(length);

		for (int i = offset; i < offset + length; i++) {

			subsetList.add(templates.get(i));

		}

		return subsetList;
	}

}
