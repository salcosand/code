package com.drawing.datastructure;

import android.graphics.Matrix;

/**
 * <b>Topic</b> object that can encapsulate primitive objects and other <b>Topic</b> objects
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class TopicObject extends DrawingComposite {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3414878749247305134L;
			
	private TopicType type;

	/**
	 * Constructor of the <b>Topic</b> object that can encapsulate primitive objects and other <b>Topic</b> objects
	 * @param path The path object that visually represents this component
	 * @param pathMatrix The transformation matrix that belongs to the respective path
	 * @see com.drawing.datastructure.DrawingComposite
	 */
	public TopicObject(CustomPath path, Matrix pathMatrix) {
		super(path, pathMatrix);
		
				
	}

	/**
	 * Get the type of the <b>TopicObject</b>
	 * @return The type of the <b>TopicObject</b>
	 */
	public TopicType getType() {
		return type;
	}

	/**
	 * Set the type of the <b>TopicObject</b>
	 * @param newtype The type of the <b>TopicObject</b>
	 */
	public void setType(TopicType newtype) {
		this.type = newtype;
		path.setColor(newtype.getColor());
	}

}
