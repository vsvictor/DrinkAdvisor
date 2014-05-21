package com.drink.activity;

import com.drink.R;
import com.drink.query.AddCommentQuery;
import com.drink.query.WebQuery;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddTextCommentActivity extends BasicActivity 
{
	String ID;
	String type;
	EditText mCommentText;

	public AddTextCommentActivity() 
	{
		super(R.layout.activity_add_text_comment);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("ID");
		type = bundle.getString("type");
		mCommentText = (EditText) findViewById(R.id.comment);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.btnAddComment);
		TextView title = (TextView) relativeLayout.findViewById(R.id.text);
		title.setTypeface(Typeface.createFromAsset(AddTextCommentActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		ImageButton imageButton = (ImageButton) relativeLayout.findViewById(R.id.button);
		imageButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					new AddCommentTask().execute();
				}
			});
	}

	private class AddCommentTask extends QueryTask<Void, Void, AddCommentQuery> 
	{
		@Override
		protected AddCommentQuery doInBackground(Void... params) 
		{
			AddCommentQuery query = new AddCommentQuery(getApplicationContext(), type, ID);
			query.setText(mCommentText.getText().toString());
			query.getResponse(WebQuery.POST);

			return query;
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
}
