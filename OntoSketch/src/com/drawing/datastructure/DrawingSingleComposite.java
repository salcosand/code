package com.drawing.datastructure;

import com.drawing.application.MainActivity;
import com.hp.hpl.jena.ontology.OntResource;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * The single composite drawing object class which accepts only one DrawingCompositeWord as child
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 * 
 */
public abstract class DrawingSingleComposite extends DrawingComposite 
{

	private static final long serialVersionUID = 4141437007000296040L;
	
	protected DrawingCompositeWord componentChild;
	
	transient protected Paint paint;
	transient protected Paint backgroundPaint;
	
	transient protected Paint buttonPaint;
	
	public DrawingCompositeWord getComponentChild() 
	{
		return componentChild;
	}

	protected String name;

	public DrawingSingleComposite(CustomPath path, Matrix pathMatrix)
	{
		super(path, pathMatrix);
		
		leafState = true;
		
		paint = new Paint();
		paint.setAntiAlias(true);
		
		buttonPaint = new Paint();
		buttonPaint.setAntiAlias(true);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		
	}

	/**
	 * @see com.drawing.datastructure.DrawingLeaf#redrawPathsafterDeserialization()
	 */
	public void redrawPathsafterDeserialization(MainActivity ma)
	{
		paint = new Paint();
		paint.setAntiAlias(true);
		
		buttonPaint = new Paint();
		buttonPaint.setAntiAlias(true);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);

		super.redrawPathsafterDeserialization(ma);
		
		ontResource = MainActivity.getOntologyResource(uri);
	}
}
