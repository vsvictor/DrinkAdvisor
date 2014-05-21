package com.drink.types;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public abstract class FilteredAdapter extends BaseAdapter implements Filterable{
	
	protected ValueFilter filter;
	protected BaseObjectList data;
	protected BaseObjectList list;
	protected Context context;
	protected LayoutInflater inflater;
	protected int count;
	BaseObjectList filterList;
	public FilteredAdapter(BaseObjectList list) {
		count = list.size();
		this.data = list;
		this.list = list;
		context = null;
		inflater = null;
	}	
	public FilteredAdapter(Context context, BaseObjectList list) {
		count = list.size();
		this.data = list;
		this.list = list;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}	
	public LayoutInflater getInflater(){
		return inflater;
	}
	public BaseObjectList getData(){
		return data;
	}
	@Override
 	public int getCount() {
		if(filterList == null) return getData().size();
		else return filterList.size();
	}
	@Override
	public Object getItem(int position) {return data.get(position);}
	@Override
	public long getItemId(int position) {return data.get(position).getID();}
/*	@Override
	public View getView(int position, View convertView, ViewGroup parent) {return null;}
*/
	@Override
	public Filter getFilter() {
		if(filter == null){
			filter = new ValueFilter();
		}
		return filter;
	}
	public void resetFilter(){
		if(filter != null) filter.reset();
	}
	private class ValueFilter extends Filter{
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults result = new FilterResults();
			if(constraint!=null && constraint.length()>0){
				filterList = new BaseObjectList();
	            for(int i=0;i<data.size();i++){
	                if((data.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
	                    filterList.add(data.get(i));
	                }
	            }
	            result.count=filterList.size();
	            result.values=filterList;
	        }else{
	            result.count=data.size();
	            result.values=data;
	        }
			return result;
		}
		@Override
		protected void publishResults(CharSequence constraint,FilterResults results) {
			data= (BaseObjectList) results.values;
	        notifyDataSetChanged();
		}
		public void reset(){
			data = list;
			notifyDataSetChanged();
		}
	}
}
