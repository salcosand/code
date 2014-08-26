package com.drawing.application;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingComponent;
import com.drawing.gestures.Point;

/**
 * 
 * @author Christian Brändel class for the asynchronous retrieval of drawn
 *         path objects
 */
public class PathRetrieverTask extends AsyncTask<CustomPath, Integer, Integer> 
{

	private GeneralView view;
	
	public PathRetrieverTask(GeneralView view)
	{
		this.view = view;
	}
	
	
	/**
	 * asynchronous retrieval of the drawn path that was selected by user
	 * input
	 * 
	 * @param paths
	 *            array of paths that are drawn on the canvas
	 */
	protected Integer doInBackground(CustomPath... paths) 
	{
		int minDistanceIndex = -1;
		// positive infinity
		float minDistance = (float) 1e9;
		List<Point> vertices = new ArrayList<Point>();
		Point vertex = new Point();
		RectF inputBounds = new RectF();
		float offset = 10;
		float distance;
		inputBounds.left = view.start.x - offset;
		inputBounds.right = view.start.x + offset;
		inputBounds.top = view.start.y - offset;
		inputBounds.bottom = view.start.y + offset;
		CustomPath tempPath = new CustomPath();

		if (paths != null) 
		{
			// debugList.clear();
			
			for (int i = 0; i < paths.length; i++) 
			{
				
				Log.d("Pathretreiver", "path.length " + paths.length);

				tempPath.set(paths[i]);
				
				DrawingComponent dc = view.getDrawingObjects().getObjectByPathId(paths[i].getUid());
				
				if (dc.getAlpha() > 0)
				{
					vertices = new ArrayList<Point>();
					vertices = paths[i].getVertices();

					// backup the transformation matrix, if no path is
					// highlighted in order to keep it up to date

					if (!view.getDrawingObjects().getPathList().ContainsHighlightedPath()) 
					{
						view.backupCurrentTransformationMatrix();
					}

					if (paths[i].isHighlighted()) 
					{
						vertices = CustomPath.transformVertices(vertices, view.matrix);
					} 
					else 
					{
						vertices = CustomPath.transformVertices(vertices, view.backupTransformationMatrix);
					}

					// debugList.add(vertices);

					for (int j = 0; j < vertices.size(); j++) 
					{
						vertex = vertices.get(j);

						inputBounds.contains(vertex.x, vertex.y);

						distance = Math.abs(vertex.x - inputBounds.centerX()) + Math.abs(vertex.y - inputBounds.centerY());
						
						if (distance < minDistance) 
						{
							minDistance = distance;
							minDistanceIndex = i;
						}
					}
				}

			}
		}

		if (minDistance > 40)
			minDistanceIndex = -1;

		return minDistanceIndex;
	}

	/**
	 * highlight the identified path
	 */
	protected void onPostExecute(Integer minDistanceIndex) 
	{
		Log.d("PathRetrieverTask","minDistanceIndex "+ minDistanceIndex);
		
		if (minDistanceIndex != -1)
		{
			CustomPath path = view.getDrawingObjects().getPathList().getPaths().get(minDistanceIndex);

			if(!path.isHighlighted())
			{
				// path match was found
				Log.d("PathRetrieverTask","updatePath "+ path);
				view.updatePath(path);
			}
			else
			{
				Log.d("PathRetrieverTask","removeHighlightFromPaths ");
				view.removeHighlightFromPaths();
			}
		}
		else 
		{
			if (view.getDrawingObjects().getHighlightedComponents().size() == 0) 
			{
				// no path match was found and there are no highlighted
				// paths
				// -> adjust view port so that it contains all drawn objects

				if((view instanceof DrawView))
				{
					Log.d("PathRetrieverTask","adjustViewPort ");
					view.adjustViewPort();
				}
				

			} else 
			{
				
				if((view instanceof DrawView))
				{
					Log.d("PathRetrieverTask","removeHighlightFromPaths ");
					view.removeHighlightFromPaths();
				}
				
			
			}
			
		}

		view.configureButtonActivation();

		view.invalidate();

	}

}
