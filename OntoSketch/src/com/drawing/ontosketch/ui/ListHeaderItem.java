package com.drawing.ontosketch.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drawing.application.MainActivity;
import com.drawing.ontosketch.R;

/**
 * CodeBehide class for UI Element ListHeaderItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class ListHeaderItem extends LinearLayout implements OnClickListener, OnTouchListener
{
	private String itemname;

	protected LayoutInflater inflater;

	protected RelativeLayout ll;

	protected TextView tv;
	
	private boolean open = true;
	
	final ImageButton collapseBtn;
	final ImageButton deleteBtn;
	
	private ArrayList<ListItem> childs;

	@SuppressLint("NewApi")
	public ListHeaderItem(final Context context, final String itemname)
	{
		super(context);
		
		childs = new ArrayList<ListItem>();

		this.itemname = itemname;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (RelativeLayout) inflater.inflate(R.layout.listheader_item, null);

		this.addView(ll);

		tv = (TextView) ll.findViewById(R.id.primaryText);

		tv.setText(itemname);
		
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		
		collapseBtn = (ImageButton) ll.findViewById(R.id.collapseButton); 
		deleteBtn = (ImageButton) ll.findViewById(R.id.deleteButton); 
		
		collapseBtn.setOnClickListener(this);
		
		deleteBtn.setOnClickListener(new OnClickListener() 
		{
			@SuppressLint("NewApi")
			public void onClick(View v) 
			{
				((MainActivity) context).showRemoveOntologyDialog(itemname);
			}
		});
	}

	public ArrayList<ListItem> getChilds()
	{
		return childs;
	}

	public void addChildren(ListItem cll)
	{
		if (!childs.contains(cll))
		{
			childs.add(cll);
		}
		
	}

	@Override
	public void onClick(View v)
	{
		for (ListItem li : childs)
		{
			if (open) 
			{

				li.setVisibility(View.GONE);
				collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_close2));
				
			}
			else 
			{
				li.setVisibility(View.VISIBLE);
				li.setIconOpen();
				
				collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_open2));
			}
		}
		
		open = !open;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				this.setBackgroundColor(getResources().getColor(R.color.tabBtnBackground));
				break;
			case MotionEvent.ACTION_UP:
				this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
				break;
			case MotionEvent.ACTION_CANCEL:
				this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
				break;
			case MotionEvent.ACTION_MOVE:
				this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
				break;
		}
		
		return false;
	}


}
