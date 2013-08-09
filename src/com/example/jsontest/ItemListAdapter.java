package com.example.jsontest;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


//ArrayList<HashMap<String, String>>
public class ItemListAdapter extends ArrayAdapter<List<HashMap<String, String>>> {
	
	private final Context mContext;
	private List<HashMap<String, String>> mItems;
	private final LayoutInflater mInflater;

	static class ViewHolder {
		TextView name,id,price;
		TextView type,stock;
	}	
	
	public ItemListAdapter(Context context, List<HashMap<String, String>> objects)
	{			
		super(context, R.layout.list_item, (List)objects);
		this.mItems = objects;
		this.mContext = context;
		this.notifyDataSetChanged();
		mInflater = LayoutInflater.from(this.mContext); //(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//	this.add(objects);
	}
	public void UpdateData(List<HashMap<String, String>> objects){
		this.mItems.addAll(objects);
		
 		this.notifyDataSetChanged();
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
		
		holder.name.setText((String) mItems.get(position).get("Name"));
		holder.id.setText((String) mItems.get(position).get("Id"));
		holder.price.setText((String) mItems.get(position).get("PriceOut"));
		//holder.type.setText((String) mItems.get(position).get("type"));
	//	holder.country.setText((String) mItems.get(position).get("country"));
		
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		return convertView;
	}

}