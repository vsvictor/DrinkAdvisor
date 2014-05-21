
package com.drink.activity;

import java.util.ArrayList;
import com.drink.R;
import com.drink.imageloader.WebImageView2;
import com.drink.query.InvitedQuery;
import com.drink.query.WebQuery;
import com.drink.types.Invited;
import com.drink.types.Meeting;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InvitedActivity extends BasicActivity 
{
	private ListView listView;
	private InvitedAdapter adapter;

	String ID;
	
	public InvitedActivity() 
	{
		super(R.layout.activity_invited);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		mCaption.setText(R.string.who_will_go);
		
		ID = getIntent().getStringExtra("meeting_id");
		
		listView = (ListView) findViewById(R.id.lv_invited);

		adapter = new InvitedAdapter(getApplicationContext());
		listView.setAdapter(adapter);

		new LoadTask().execute();
	}

	private class InvitedAdapter extends BaseAdapter 
	{
		private LayoutInflater inflater;
		private ArrayList<Invited> invited;

		public InvitedAdapter(Context context) 
		{
			this.inflater = LayoutInflater.from(context);
		}

		public void setData(ArrayList<Invited> invited) 
		{
			this.invited = invited;
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (this.invited == null) return 0;
			
			return invited.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder = null;
			View row = view;
			if (row == null) 
			{
				viewHolder = new ViewHolder();
				row = inflater.inflate(R.layout.listitem_invited, null);
				
				viewHolder.avatar = (WebImageView2) row.findViewById(R.id.wiv_avatar);
				viewHolder.avatar.setCircle(true);
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());
				
				viewHolder.name = (TextView) row.findViewById(R.id.friend_name);
				viewHolder.name.setTypeface(Typeface.createFromAsset(InvitedActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				
				viewHolder.tv_red = (TextView) row.findViewById(R.id.tv_red);
				viewHolder.tv_red.setTypeface(Typeface.createFromAsset(InvitedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				viewHolder.tv_green = (TextView) row.findViewById(R.id.tv_green);
				viewHolder.tv_green.setTypeface(Typeface.createFromAsset(InvitedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				viewHolder.tv_gray = (TextView) row.findViewById(R.id.tv_gray);
				viewHolder.tv_gray.setTypeface(Typeface.createFromAsset(InvitedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			Invited _invited = getItem(i);
			
			if ((_invited.avatar != null) && !_invited.avatar.equals("")) 
			{
				viewHolder.avatar.setImageURL(_invited.avatar, "123");
			}
			viewHolder.name.setText(_invited.name);

			switch (_invited.state)
			{
			case Meeting.GO:
				viewHolder.tv_red.setVisibility(View.GONE);
				viewHolder.tv_green.setVisibility(View.VISIBLE);
				viewHolder.tv_gray.setVisibility(View.GONE);
				break;
			case Meeting.DONT_GO:
				viewHolder.tv_red.setVisibility(View.VISIBLE);
				viewHolder.tv_green.setVisibility(View.GONE);
				viewHolder.tv_gray.setVisibility(View.GONE);
				break;
			case Meeting.DONT_KNOW:
				viewHolder.tv_red.setVisibility(View.GONE);
				viewHolder.tv_green.setVisibility(View.GONE);
				viewHolder.tv_gray.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			
			return row;
		}

		@Override
		public Invited getItem(int position) 
		{
			if (invited == null) return null;
			if (position < invited.size()) return invited.get(position);
			
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		
		private class ViewHolder
		{
			public WebImageView2 	avatar;
			public TextView 		name;
			public TextView			tv_red;
			public TextView			tv_green;
			public TextView			tv_gray;
		}
	}

	public class LoadTask extends QueryTask<Void, Void, InvitedQuery> 
	{
		@Override
		protected InvitedQuery doInBackground(Void... params) 
		{
			InvitedQuery query = new InvitedQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);
			
			return query;
		}

		@Override
		protected void onPostExecute(InvitedQuery query) 
		{
			if (checkResult(query))
			{
				InvitedQuery.Data data = query.getData();
				
				adapter.setData(data.invited);
			}
			
			super.onPostExecute(query);
		}
	}
}
