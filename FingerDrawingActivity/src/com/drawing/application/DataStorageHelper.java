package com.drawing.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.DrawingComposite;
import com.drawing.datastructure.TopicType;

/**
 * Helper class for saving and loading stored information
 * @author Christian Brändel TU Dresden / SAP Research Dresden
 *
 */
public class DataStorageHelper {
	
	static String FILENAME;
	
	static String MATRIXFILENAME;
	
	static String FILENAMESLIST = "filenames";
	
	static String TYPELIST = "sketchviz_topictypelist";
	
	/**
	 * Store the <b>DrawingComponent</b> object and all its children to internal storage
	 * @param context The current <b>Context</b> of the application
	 * @param drawView The instance of the current <b>DrawView</b> object that should be stored
	 * @param filename The name of the file that should be stored
	 * @param components The <b>DrawingComponent</b> that should be stored
	 * @return True if everything was stored successfully
	 */
	@SuppressWarnings("resource")
	public static boolean storeData(Context context, DrawView drawView, String filename)
	{
		FILENAME = filename;
		
		MATRIXFILENAME = FILENAME + "_matrices";
		
		
		
		FileOutputStream fos = null;
		FileOutputStream fosMatrix = null;
		ObjectOutputStream oos = null;
		ObjectOutputStream oosMatrix = null;
		
		DrawingComposite components = drawView.getDrawingObjects();
		
		List<CustomPath> highlightedPathList = components.getHighlightedComponents();

		for (int i = 0; i < highlightedPathList.size(); i++) {

			if (highlightedPathList.get(i).isHighlighted())
				drawView.updatePath(highlightedPathList.get(i));

		}
		
		float[][] matrices = drawView.getMatrices();
		
		try {
			

			
			fos = context.openFileOutput(FILENAME, Context.MODE_WORLD_READABLE);
			fosMatrix = context.openFileOutput(MATRIXFILENAME, Context.MODE_WORLD_READABLE);
			
			try {
				
			oos = new ObjectOutputStream(fos);

			oosMatrix = new ObjectOutputStream(fosMatrix);
			
			oos.writeObject(components);
			
			oosMatrix.writeObject(matrices);
			
			} catch (IOException e) {

				e.printStackTrace();
				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
			
			
		} catch (FileNotFoundException e) {
						
			e.printStackTrace();
//			Log.d("STORAGEEXCEPTION", e.toString());
			return false;
		}
		

		if(oos != null)
		{
			try {
				oos.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
		
		if(oosMatrix != null)
		{
			try {
				oosMatrix.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
		
		if(fos != null)
		{
			try {
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
		
		if(fosMatrix != null)
		{
			try {
				fosMatrix.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
		
		Toast toast = Toast.makeText(context, "Content was successfully stored", Toast.LENGTH_SHORT);
		
		toast.show();
		
		return true;
	}
	
	/**
	 * Delete the files associated with this filename from the file system 
	 * @param fileName file to delete
	 */
	public static boolean deleteData(Context context, String fileName) {

	FILENAME = fileName;
	
	MATRIXFILENAME = FILENAME + "_matrices";
	
	boolean deleted = false;
	
	try
	{
	
	File file = new File(context.getFilesDir().getPath() + "/" + FILENAME);
	
	deleted = file.delete();
	
	file = new File(context.getFilesDir().getPath() + "/" + MATRIXFILENAME);
	
	deleted = file.delete() && deleted;
	
	}catch(Exception e)
	{
		e.printStackTrace();
		return false;
	}
	
	return deleted;
	
	}
	
	/**
	 * Load the <b>DrawingComponent</b> object and all its children to internal storage
	 * @param context The current <b>Context</b> of the application
	 * @param filename The name of the file that should be restored
	 * @return The <b>DrawingComponent</b> that was stored before
	 */
	public static DrawingComposite loadData(Context context, String filename)
	{
		FILENAME = filename;
		
		MATRIXFILENAME = FILENAME + "_matrices";
		
		FileInputStream fis = null;
		
		ObjectInputStream ois = null;
		
		FileInputStream fisMatrix = null;
		
		ObjectInputStream oisMatrix = null;
		
		DrawingComposite component = null;
		
		float[][] matrices = new float[3][9];
		
		
		try {
			
			fis = new FileInputStream(context.getFilesDir().getPath() + "/" + FILENAME);
			
			fisMatrix = new FileInputStream(context.getFilesDir().getPath() + "/" + MATRIXFILENAME);
			
			try {
				
				ois = new ObjectInputStream(fis);
				
				oisMatrix = new ObjectInputStream(fisMatrix);
				
				try {
					component = (DrawingComposite) ois.readObject();
					
					matrices = (float[][]) oisMatrix.readObject();
					
					((FingerDrawingActivity)context).drawView.restoreMatrices(matrices);
					
				} catch (ClassNotFoundException e) {

					e.printStackTrace();
//					Log.d("STORAGEEXCEPTION", e.toString());
				}
				
				ois.close();
				oisMatrix.close();
				
			} catch (StreamCorruptedException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				
				
				
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
			}
			
		
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
//			Log.d("STORAGEEXCEPTION", e.toString());
		}
		
		
		
		return component;
	}

	/**
	 * Load the current state of the list of currently available topic types
	 * @param context The respective {@link Activity}
	 * @return The list of available {@link TopicType}
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<TopicType> loadTypeList(Context context) {

		FileInputStream fis = null;
		
		ObjectInputStream ois = null;
		
		ArrayList<TopicType> typeList = new ArrayList<TopicType>();
		
		
		try {
			
			fis = new FileInputStream(context.getFilesDir().getPath() + "/" + TYPELIST);
			
			try {
				
				ois = new ObjectInputStream(fis);
				
				try {
					typeList = (ArrayList<TopicType>) ois.readObject();
					
				} catch (ClassNotFoundException e) {

					e.printStackTrace();
				}
				
				ois.close();
				
			} catch (StreamCorruptedException e) {

				e.printStackTrace();
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		return typeList;
	}
	
	/**
	 * Save the current state of the list of currently available topic types
	 * @param context The respective {@link Activity}
	 * @param topicTypeList The list that should be stored
	 * @return True if the object was successfully saved
	 */
	public static boolean saveTypeList(Context context, ArrayList<TopicType> topicTypeList)
	{
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		
		try {
			
			fos = context.openFileOutput(TYPELIST, Context.MODE_WORLD_READABLE);
						
			try {
				
			oos = new ObjectOutputStream(fos);
						
			oos.writeObject(topicTypeList);
			
			} catch (IOException e) {

				e.printStackTrace();
				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
			
			
		} catch (FileNotFoundException e) {
						
			e.printStackTrace();
//			Log.d("STORAGEEXCEPTION", e.toString());
			return false;
		}
		

		if(oos != null)
		{
			try {
				oos.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
	
		
		if(fos != null)
		{
			try {
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
//				Log.d("STORAGEEXCEPTION", e.toString());
				return false;
			}
		}
	
		Toast toast = Toast.makeText(context, "Topic type list was successfully stored", Toast.LENGTH_SHORT);
		
		toast.show();
		
		return true;
		
	}



}
