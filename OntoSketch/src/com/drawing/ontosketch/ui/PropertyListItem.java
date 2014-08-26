package com.drawing.ontosketch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.drawing.application.MainActivity;
import com.drawing.ontosketch.R;

/**
 * CodeBehide class for UI Element PropertyListItem
 * 
 * @author Florian Schneider TU Dresden / SAP NEXT Dresden
 *
 */
public class PropertyListItem extends ListItem implements OnCheckedChangeListener
{
	private String itemname;
	private String namespace;
	private String uri;
	
	private int color;
	
	private PropertyIconBackground iconBackground;

	private boolean allowDrag = false;

	protected LayoutInflater inflater;

	protected LinearLayout ll;

	protected CheckBox cb;
	
	private MainActivity ma;
	
	@SuppressLint("NewApi")
	public PropertyListItem(Context context, String itemname, int color)
	{
		super(context, itemname);
		
		ma = (MainActivity) getContext();

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setLayoutParams(params);

		this.itemname = itemname;
		
		this.color = color;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.property_list_item, null);

		this.addView(ll);

		cb = (CheckBox) ll.findViewById(R.id.primaryText);

		cb.setText(itemname);
		
		iconBackground = (PropertyIconBackground) ll.findViewById(R.id.imageDraw);
		
		iconBackground.setColor(color);

		setCurrentState(ListItemState.INACTIVE);
		
		this.setOnTouchListener(null);

	}

	public PropertyListItem(PropertyListItem li)
	{
		super(li.getContext(), li.getItemname());
		
		ma = (MainActivity) getContext();

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setLayoutParams(params);

		this.itemname = li.getItemname();
		
		this.color = li.getColor();

		inflater = (LayoutInflater) li.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ll = (LinearLayout) inflater.inflate(R.layout.property_list_item, null);

		this.addView(ll);

		cb = (CheckBox) ll.findViewById(R.id.primaryText);

		cb.setText(itemname);
		
		iconBackground = (PropertyIconBackground) ll.findViewById(R.id.imageDraw);
		
		iconBackground.setColor(color);

		setCurrentState(li.getCurrentState());
		
		this.setOnTouchListener(null);
		
		uri = li.getUri();
	}

	private ListItemState _currentState = ListItemState.NONE;

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
	
	private void drawInactive()
	{
		iconBackground.setColor(getResources().getColor(R.color.tabBtnBackground));
		
		cb.setOnCheckedChangeListener(null);
		
		cb.setChecked(false);
		cb.setEnabled(false);
		
		ma.setOntopanelPropertyListItemActive(uri, false);

	}

	private void drawActive()
	{
		iconBackground.setColor(color);
		
		cb.setChecked(true);
		cb.setEnabled(true);
		
		ma.setOntopanelPropertyListItemActive(uri, true);
		
		cb.setOnCheckedChangeListener(this);

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


	public void setUri(String uri)
	{
		this.uri = uri;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if(isChecked)
		{
			ma.showPropertyRelation(this.uri, this.color);
		}
		else 
		{
			ma.hidePropertyRelation(this.uri);
		}
		
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
