
package com.drink.activity;

import com.drink.R;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SendWrongInfoActivity extends BasicActivity 
{
	String 		ID;
	EditText 	mText;

	public SendWrongInfoActivity() 
	{
		super(R.layout.activity_send_wrong_info);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("ID");
		mText = (EditText) findViewById(R.id.text);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.btnSend);
		TextView title = (TextView) relativeLayout.findViewById(R.id.text);
		title.setTypeface(Typeface.createFromAsset(SendWrongInfoActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		ImageButton imageButton = (ImageButton) relativeLayout.findViewById(R.id.button);
		imageButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					String text = mText.getText().toString();
					if ((text != null) && (text.length() > 0))
					{
						Intent intent = new Intent();
						intent.putExtra("text", text);
						setResult(RESULT_OK, intent);
					}
					
					finish();
				}
			});
	}
}
