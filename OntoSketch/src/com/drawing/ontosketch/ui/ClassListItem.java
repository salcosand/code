package com.drawing.ontosketch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drawing.application.MainActivity;
import com.drawing.datastructure.OntologyObjectTypes;
import com.drawing.ontosketch.R;

/**
 * CodeBehide class for UI Element ClassListItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class ClassListItem extends ListItem
{ 

	private ImageButton childToggleBtn;
	
	private boolean hasIndividuals = false;
	
	private int hLevel = 0;
	
	@SuppressLint("NewApi")
	public ClassListItem(final Context context, final String itemname, int layer)
	{
		super(context, itemname);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.class_list_item, null);
		
		this.addView(ll);
		
		hLevel = layer;
		
		tv = (TextView) ll.findViewById(R.id.primaryText);
		
		collapseBtn = (ImageButton) ll.findViewById(R.id.collapseButton);
		
		childToggleBtn = (ImageButton) ll.findViewById(R.id.itemButton);
		
		childToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) context).selectiveUpdateOntoPanel(OntologyObjectTypes.FORMALIZEDCONCEPT, OntologyObjectTypes.FORMALIZEDINDIVIDUAL, getUri(), itemname);
			}
		});

		LayoutParams params = (LayoutParams) collapseBtn.getLayoutParams();
		params.setMargins(5+(25*layer), 10, 5, 0); //left, top, right, bottom
		collapseBtn.setLayoutParams(params);
		
		collapseBtn.setVisibility(View.INVISIBLE);
		
		childToggleBtn.setVisibility(View.GONE);
		
		tv.setText(itemname);
		
		setCurrentState(ListItemState.ACTIVE);
		
		this.setOnClickListener(this);
		
	}
	
	public ClassListItem(final ClassListItem li)
	{
		super(li.getContext(), li.getItemname());
		
		inflater = (LayoutInflater) li.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.class_list_item, null);
		
		this.addView(ll);
		
		tv = (TextView) ll.findViewById(R.id.primaryText);
		
		collapseBtn = (ImageButton) ll.findViewById(R.id.collapseButton);

		childToggleBtn = (ImageButton) ll.findViewById(R.id.itemButton);
		
		childToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) v.getContext()).selectiveUpdateOntoPanel(OntologyObjectTypes.FORMALIZEDCONCEPT, OntologyObjectTypes.FORMALIZEDINDIVIDUAL, getUri(), li.getItemname());
			}
		});
		
		childToggleBtn.setVisibility(View.GONE);
		
		setHasIndividuals(li.hasIndividuals);

		hLevel = li.gethLevel();
		
		LayoutParams params = (LayoutParams) collapseBtn.getLayoutParams();
		params.setMargins(5+(25*hLevel), 10, 5, 0); //left, top, right, bottom
		collapseBtn.setLayoutParams(params);
		
		collapseBtn.setVisibility(View.GONE);
		
		tv.setText(li.getItemname());
		
		setCurrentState(li.getCurrentState());
		
		this.setOnClickListener(this);
		
		uri = li.getUri();

	}

	public void setHasIndividuals(boolean hasIndividuals)
	{
		if (hasIndividuals)
			childToggleBtn.setVisibility(View.VISIBLE);
		else
			childToggleBtn.setVisibility(View.GONE);
		
		this.hasIndividuals = hasIndividuals;
	}

	public int gethLevel()
	{
		return hLevel;
	}
	
}


	
