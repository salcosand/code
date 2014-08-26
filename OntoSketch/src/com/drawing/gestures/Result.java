package com.drawing.gestures;

import com.drawing.datastructure.GestureTypes;

/**
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class Result {
	String Name;
	
	GestureTypes type = GestureTypes.NOGESTURE;

	float Score;


	float Ratio;

	/**
	 * The result object that describes the recognition of a gesture
	 * 
	 * @param name Name of the gesture
	 * @param score Probability that the recognized gesture is accurate
	 * @param ratio Template containing the reference points that were used for recognition
	 * @param type <b>GestureType</b> object describing the type of the respective gesture
	 */
	public Result(String name, float score, float ratio, GestureTypes type) {
		Name = name;
		Score = score;
		Ratio = ratio;
		this.type = type;
	}

	/**
	 * Figure out the name of the respective recognized gesture
	 * @return The name of the recognized gesture
	 */
	public String getName() {
		return Name;
	}
	
	/**
	 * Change the name of the respective recognized gesture
	 * @param name The name that is manually assigned to the result
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Getter for the type attribute of the detected gesture
	 * @return The type attribute of the detected gesture
	 */
	public GestureTypes getType() {
		return type;
	}

	/**
	 * Setter for the type attribute of the detected gesture
	 * @param nogesture The manually assigned gesture type
	 */
	public void setType(GestureTypes nogesture) {
		
		type = nogesture;
		
	}
}
