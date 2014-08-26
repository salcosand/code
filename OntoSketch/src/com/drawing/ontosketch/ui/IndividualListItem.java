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
 * CodeBehide class for UI Element IndividualListItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class IndividualListItem extends ListItem
{ 
	private String classname;
	
	private ImageButton childToggleBtn;

	@SuppressLint("NewApi")
	public IndividualListItem(Context context, String itemname, final String classname)
	{
		super(context, itemname);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.individual_list_item, null);
		
		this.addView(ll);
		
		tv = (TextView) ll.findViewById(R.id.primaryText);
		
		tv.setText(itemname);
		
		tv2 = (TextView) ll.findViewById(R.id.secondaryText);
		
		tv2.setText(getResources().getString(R.string.individual_class_of) + " " + classname);
		
		this.classname = classname;
		
		setCurrentState(ListItemState.ACTIVE);
		
		childToggleBtn = (ImageButton) ll.findViewById(R.id.itemButton);
		
		childToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) v.getContext()).selectiveUpdateOntoPanel(OntologyObjectTypes.FORMALIZEDINDIVIDUAL, OntologyObjectTypes.FORMALIZEDCONCEPT, getUri(), classname);
			}
		});
		
	}

	public IndividualListItem(final IndividualListItem li)
	{
		super(li.getContext(), li.getItemname());
		
		inflater = (LayoutInflater) li.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.individual_list_item, null);
		
		this.addView(ll);
		
		tv = (TextView) ll.findViewById(R.id.primaryText);
		
		tv.setText(li.getItemname());
		
		tv2 = (TextView) ll.findViewById(R.id.secondaryText);
		
		tv2.setText(getResources().getString(R.string.individual_class_of) + " " + li.getClassName());
		
		setCurrentState(li.getCurrentState());
		
		uri = li.getUri();
		
		childToggleBtn = (ImageButton) ll.findViewById(R.id.itemButton);
		
		childToggleBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) v.getContext()).selectiveUpdateOntoPanel(OntologyObjectTypes.FORMALIZEDINDIVIDUAL, OntologyObjectTypes.FORMALIZEDCONCEPT, getUri(), li.getClassName());
			}
		});
	}
	
	public String getClassName() 
	{
		return classname;
	}
	
}


	
