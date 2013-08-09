package com.example.jsontest;

import com.example.jsontest.ItemListAdapter.ViewHolder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ItemsCursorAdapter extends CursorAdapter {
	
	 private final Context mCtx;
	 private final LayoutInflater mInflater;
	Cursor mCursor;
	static class ViewHolder {
		TextView name,id,price;
		TextView type,stock;
	}	
		
	public ItemsCursorAdapter(Context context, Cursor c, boolean autoRequery)
	{			
		super(context, c, autoRequery);
		this.mCursor = c;
		//this.mItems = objects;
		this.mCtx = context;
		//this.notifyDataSetChanged();
		mInflater = LayoutInflater.from(this.mCtx); //(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//	this.add(objects);
	}
	
	public void updateCursor(Cursor c)
	{
		this.mCursor = c;
		this.changeCursor(c);
		this.notifyDataSetChanged();
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v =  mInflater.inflate(R.layout.list_item, parent, false);    	
        return v;
    }
 
    @Override
    public void bindView(View v, Context context, Cursor c) {
 
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){		
		ViewHolder holder;
	
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.id = (TextView)convertView.findViewById(R.id.barcode);
			holder.price = (TextView)convertView.findViewById(R.id.price);
			holder.type = (TextView)convertView.findViewById(R.id.type);
			holder.stock = (TextView)convertView.findViewById(R.id.stock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
	    mCursor.moveToPosition(position);
		
		holder.name.setText((String) mCursor.getString( mCursor.getColumnIndexOrThrow("name")));
		holder.id.setText((String) mCursor.getString( mCursor.getColumnIndexOrThrow("barcode")));
		holder.price.setText((String) mCursor.getString( mCursor.getColumnIndexOrThrow("price")));	
		return convertView;
	}
	 
}

