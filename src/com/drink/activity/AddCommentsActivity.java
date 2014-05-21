
package com.drink.activity;

import com.drink.R;
import com.drink.query.AddCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddCommentsActivity extends BasicActivity 
{
	String ID;
	EditText comment;
	String type;
	
	ImageButton		btnLike;
	ImageButton		btnDislike;
	
	Switch			swFacebook;
	Switch			swTwitter;
	
	boolean			force = false;
	
	public AddCommentsActivity() 
	{
		super(R.layout.activity_add_comments);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("id");
		type = bundle.getString("type");
		
		String name = bundle.getString("bar_name");
		if (name != null)
		{
			mCaption.setText(name);
			mCaption.setTextColor(Color.WHITE);
			mCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		}

		if (type == null || type.equals(CommentsQuery.BARS)) 
		{
			type = CommentsQuery.BARS;
			
			LinearLayout layout = (LinearLayout) findViewById(R.id.bars_panel);
			layout.setVisibility(View.VISIBLE);
		}
		
		comment = (EditText) findViewById(R.id.comment);
		
		Button btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setTypeface(Typeface.createFromAsset(AddCommentsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnSend.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if (!btnLike.isSelected() && !btnDislike.isSelected())
				{
					showErrorBox(getString(R.string.need_check_like), false);
					return;
				}
				
				if (!getIntent().hasExtra("like") && (comment.getText().toString().length() == 0))
				{
					showErrorBox(getString(R.string.enter_text), false);
					return;
				}
				
				new AddCommentTask().execute();
			}
		});

		btnLike = (ImageButton) findViewById(R.id.btnLike);
		btnLike.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					btnLike.setSelected(true);
					btnDislike.setSelected(false);
				}
			});
		
		btnDislike = (ImageButton) findViewById(R.id.btnDislike);
		btnDislike.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					btnDislike.setSelected(true);
					btnLike.setSelected(false);
				}
			});

		if (getIntent().hasExtra("like"))
		{
			boolean like = getIntent().getBooleanExtra("like", true);

			btnLike.setSelected(like);
			btnDislike.setSelected(!like);
			
			force = true;
		}
		
		TextView tvText =  (TextView) findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(AddCommentsActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));

		TextView tvSocialTitle =  (TextView) findViewById(R.id.tvTitle);
		tvSocialTitle.setTypeface(Typeface.createFromAsset(AddCommentsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		swFacebook = (Switch) findViewById(R.id.switch_facebook);
		swFacebook.setChecked(appSettings.getConnectFacebook() && appSettings.getPublishOnFacebook());
		swFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (!isChecked) return;
					
					checkConnectFacebook();
				}
			});

		swTwitter = (Switch) findViewById(R.id.switch_twitter);
		swTwitter.setChecked(appSettings.getConnectTwitter() && appSettings.getPublishOnTwitter());
		swTwitter.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (!isChecked) return;
					
					checkConnectTwitter();
				}
			});
	}
	
	private class AddCommentTask extends QueryTask<Void, Void, AddCommentQuery> 
	{
		@Override
		protected AddCommentQuery doInBackground(Void... params) 
		{
			AddCommentQuery addCommentQuery = new AddCommentQuery(AddCommentsActivity.this, type, ID);
			addCommentQuery.setText(comment.getText().toString());
			addCommentQuery.setBarRating(btnLike.isSelected() ? "1" : "0");
			if (swFacebook.isChecked())
			{
				addCommentQuery.setPublishOnFB();
			}
			if (swTwitter.isChecked())
			{
				addCommentQuery.setPublishOnTW();
			}
			addCommentQuery.getResponse(WebQuery.POST);

			return addCommentQuery;
		}

		@Override
		protected void onPostExecute(AddCommentQuery query) 
		{
			if (checkResult(query)) 
			{
				AddCommentQuery.Data data = query.getData();
				if (data.success)
				{
					setResult(RESULT_OK);
					finish();
				}
				else 
				{
					showErrorBox(data.message, true);
				}
			}

			super.onPostExecute(query);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch (requestCode) 
		{
		case CONNECT_FACEBOOK:
			if (!appSettings.getConnectFacebook())
			{
				swFacebook.setChecked(false);
			}
			break;
		case CONNECT_TWITTER:
			if (!appSettings.getConnectTwitter())
			{
				swTwitter.setChecked(false);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed()  
	{
		if (force)
		{
			new AddCommentTask().execute();
		}
		else
		{
			super.onBackPressed();
		}
	}
}

