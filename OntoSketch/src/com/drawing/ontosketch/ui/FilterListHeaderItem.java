package com.drawing.ontosketch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drawing.application.MainActivity;
import com.drawing.ontosketch.R;

/**
 * CodeBehide class for UI Element FilterListHeaderItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class FilterListHeaderItem extends RelativeLayout implements OnTouchListener, OnClickListener
{
	protected LayoutInflater inflater;

	protected RelativeLayout ll;

	protected TextView tv;
	protected TextView label;
	
	final ImageButton closeBtn;
	
	public FilterListHeaderItem(final Context context, AttributeSet attrs) 
	{
		super(context, attrs);

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (RelativeLayout) inflater.inflate(R.layout.filter_list_item, null);

		this.addView(ll);

		tv = (TextView) ll.findViewById(R.id.searchText);
		label = (TextView) ll.findViewById(R.id.label);

		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		
		closeBtn = (ImageButton) ll.findViewById(R.id.closeButton); 
		
		closeBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) context).closeFilter();
			}
		});
	}
	
	public void updateText(String label, String searchText)
	{
		tv.setText(searchText);
		this.label.setText(label + ":");
	}

	@Override
	public void onClick(View v)
	{

	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return true;
	}

}
