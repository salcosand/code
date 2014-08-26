package com.drawing.datastructure;

import java.io.Serializable;

/**
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 * The object containing the type description that can be assigned to composite objects 
 */
public class TopicType implements Serializable{

	
	
	/**
	 * generated serial version uid
	 */
	private static final long serialVersionUID = 2340948567545823335L;

	private String name;
	
	private int color;
	
	private String description;
	
	private int topicID;
	
	private String shortcut;
	
	private static int counter = 0;
	
/**
 * The object containing the type description that can be assigned to composite objects 
 * @param name The name of the topic type
 * @param color	The Integer value of the assigned color of this topic type
 * @param description The description of this topic type
 * @param topicID The id of this topic type
 * @param shortcut The shortcut of this topic type
 */
	public TopicType(String name, int color, String description, int topicID, String shortcut)
	{
		this.name = name;
		
		this.color = color;
		
		this.description = description;
		
		this.topicID = topicID;
		
		this.shortcut = shortcut;
	}

	
	/**
	 * @return The name of this topic type
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name of this topic type
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return The color of this topic type
	 */
	public int getColor() {
		return color;
	}

	/**
	 * 
	 * @param color The color of this topic type
	 */
	public void setColor(int color) {
		this.color = color;
	}

	
	/**
	 * @return The description of this topic type
	 */
	public String getDescription() {
		return description;
	}

	
	/**
	 * @param description The description of this topic type
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	/**
	 * @return The id of this topic type
	 */
	public int getTopicID() {
		return topicID;
	}

	
	/**
	 * @param topicID The id of this topic type
	 */
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	/**
	 * @return The shortcut of this topic type
	 */
	public String getShortcut() {
		return shortcut;
	}

	/**
	 * @param shortcut The shortcut of this topic type
	 */
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	} 
	
	/**
	 * Method that generates an id for a topic type
	 */
	public static int generateID()
	{
	
		counter++;
		
		return counter-1;
		
	}
	
}
