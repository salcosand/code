package com.drawing.application;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

import com.drawing.datastructure.CustomPath;
import com.drawing.datastructure.FormalizedConcept;
import com.drawing.datastructure.FormalizedIndividual;
import com.drawing.datastructure.GestureTypes;
import com.drawing.ontosketch.ui.ClassListItem;
import com.drawing.ontosketch.ui.IndividualClassListItem;
import com.drawing.ontosketch.ui.IndividualListItem;
import com.hp.hpl.jena.ontology.OntResource;

/**
 * DragListener for formalized obejects from the ontoPanel
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class SketchBoardDragListener implements OnDragListener 
{
	    @Override
	    public boolean onDrag(View v, DragEvent event) {
	    	
	    	DrawView sketchBoard = (DrawView) v;
	    	
	    	switch (event.getAction()) {

		      case DragEvent.ACTION_DRAG_STARTED:
		    	  
		    	  sketchBoard.ma.getSearchInput().setCursorVisible(false);
		    	  
		    	  Log.d("DRAG", "ACTION_DRAG_STARTED");
		 
		        break;
		      case DragEvent.ACTION_DRAG_ENTERED:
		    	  
		    	  Log.d("DRAG", "ACTION_DRAG_ENTERED");

		        break;
		      case DragEvent.ACTION_DRAG_EXITED:
		    	  
		    	  Log.d("DRAG", "ACTION_DRAG_EXITED");

		        break;
		      case DragEvent.ACTION_DROP:
		    	  
		    	  Log.d("DRAG", "ACTION_DROP");
		    	  
			        View view = (View) event.getLocalState();
	
			        String uri = event.getClipData().getItemAt(0).getText().toString();
			        
			        OntResource ontresource = MainActivity.getOntologyResource(uri);
			        
			        if (view.getClass().equals(ClassListItem.class))
			        {
			        	CustomPath cp = new CustomPath();
			        	
			        	cp.setHighlighted(false);
			        	cp.setType(GestureTypes.CONCEPT);
			        	
			        	cp.setColor(Color.TRANSPARENT);
			        	
			        	sketchBoard.ma.setOntopanelConceptListItemInactive(uri);
	
			        	FormalizedConcept fc = new FormalizedConcept(cp, sketchBoard.matrix, ontresource.getLocalName(), "");
	
			        	sketchBoard.addFormalizedObject(fc, new Point((int)event.getX(), (int)event.getY()), ontresource);
	
			        }
			        else if(view.getClass().equals(IndividualListItem.class) || view.getClass().equals(IndividualClassListItem.class))
			        {
			        	CustomPath cp = new CustomPath();
			        	
			        	cp.setHighlighted(false);
			        	cp.setType(GestureTypes.INDIVIDUAL);
			        	
			        	cp.setColor(Color.TRANSPARENT);
			        	
			        	sketchBoard.ma.setOntopanelIndividualListItemInactive(uri);
			        	
			        	FormalizedIndividual fi = new FormalizedIndividual(cp, sketchBoard.matrix, ontresource.getLocalName(),"");
	
		        		sketchBoard.addFormalizedObject(fi, new Point((int)event.getX(), (int)event.getY()), ontresource);
			        }
	
			        
			        sketchBoard.invalidate();

	        break;
	      case DragEvent.ACTION_DRAG_ENDED:
	      default:
	        break;
	      }
	      return true;
	    }
	  }