package com.drawing.ontosketch.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.drawing.ontosketch.R;

/**
 * CodeBehide class for UI Element IndividualClassListItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class IndividualClassListItem extends ListItem 
{

	public IndividualClassListItem(Context context, String itemname, int layer)
	{
		super(context, itemname);
	
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.class_individual_list_item, null);
		
		this.addView(ll);
		
		tv = (TextView) ll.findViewById(R.id.primaryText);
		
		tv.setText(itemname);
		
		LayoutParams params = (LayoutParams) tv.getLayoutParams();
		params.setMargins(5+(25*layer), 0, 5, 0); //left, top, right, bottom
		tv.setLayoutParams(params);

		setCurrentState(ListItemState.ACTIVE);
		
		this.setBackgroundColor(getResources().getColor(R.color.tabBtnBackground));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
				break;
			case MotionEvent.ACTION_UP:
				this.setBackgroundColor(getResources().getColor(R.color.tabBtnBackground));
				break;
			case MotionEvent.ACTION_CANCEL:
				this.setBackgroundColor(getResources().getColor(R.color.tabBtnBackground));
				break;
			case MotionEvent.ACTION_MOVE:
				this.setBackgroundColor(getResources().getColor(R.color.tabBtnBackground));
				break;
		}
		
		return false;
	}
	
}
