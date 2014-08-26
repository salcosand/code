package com.drawing.ontosketch.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drawing.ontosketch.R;

/**
 * abstract class for abstract UI Element ListItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public abstract class ListItem extends LinearLayout implements OnLongClickListener, OnClickListener, OnTouchListener
{	
	private String itemname;
	private String namespace;
	protected String uri;
	
	public enum ListItemState { NONE, ACTIVE, INACTIVE };
	
	private boolean allowDrag = false;
	
	protected LayoutInflater inflater;
	
	protected LinearLayout ll;
	
	protected TextView tv;
	
	protected TextView tv2;

	private ListItemState _currentState = ListItemState.NONE;
	
	protected boolean open = true;
	
	protected ImageButton collapseBtn;
	
	private ArrayList<ListItem> childs;
	
	public void addChildren(ListItem cll)
	{
		if (!childs.contains(cll))
		{
			childs.add(cll);
		}	
	}
	
	@SuppressLint("NewApi")
	public ListItem(Context context, String itemname)
	{
		super(context);
		
		this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
		
		childs = new ArrayList<ListItem>();
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setLayoutParams(params);
		
		this.itemname = itemname; 
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.setOnTouchListener(this);
	}
	
	private void drawInactive() 
	{
		this.setOnLongClickListener(null);
		
		tv.setTextColor(Color.LTGRAY);
		if(this instanceof IndividualListItem) tv2.setTextColor(Color.LTGRAY);
		
		this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
	}

	private void drawActive() 
	{
		this.setOnLongClickListener(this);

		tv.setTextColor(Color.BLACK);
		if(this instanceof IndividualListItem) tv2.setTextColor(getResources().getColor(R.color.ontoPanelBtn));
		
		this.setBackgroundColor(getResources().getColor(R.color.tabBackground));
	}

	@Override
	public boolean onLongClick(View v)
	{
		ClipData data = ClipData.newPlainText("uri", uri);
		
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

        v.startDrag(data, shadowBuilder, v, 0);
        
        return true;
	}


	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public void checkChildCount()
	{
		if (childs.size() > 0)
			collapseBtn.setVisibility(View.VISIBLE);
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
				collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_open2));
			}
			
			if (open && li.getChilds().size() > 0)
			{
				li.toogleCollapse(open);
			}
				
		}
		
		open = !open;
	}
	
	public void toogleCollapse(boolean b)
	{
		for (ListItem li : childs)
		{
			if (b) 
			{
				li.setVisibility(View.GONE);
				collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_close2));
			}
			else 
			{
				li.setVisibility(View.VISIBLE);
				collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_open2));
			}
			
			li.open = b;
			
			if (li.getChilds().size() > 0)
				li.toogleCollapse(b);
		}
	}
	
	public void setIconOpen()
	{
		if(collapseBtn != null) collapseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitem_open2));
		open = true;
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

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getUri()
	{
		return uri;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setCurrentState(ListItemState clis)
	{
		if (clis != _currentState)	
		{
			_currentState = clis;
			
			switch (clis) 
			{
			case ACTIVE:
				
				drawActive();
				break;
	
			case INACTIVE:
				
				drawInactive();
				break;
				
			default:
				break;
			}
			
		}
	
		return;
	}

	public ListItemState getCurrentState()
	{
		return _currentState;
	}

	public ArrayList<ListItem> getChilds()
	{
		return childs;
	}


	public String getItemname()
	{
		return itemname;
	}
}


	
