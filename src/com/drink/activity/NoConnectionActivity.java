package com.drink.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;

public class NoConnectionActivity extends BasicActivity implements
		OnClickListener {

	public NoConnectionActivity() {
		super(R.layout.activity_no_connection);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView tv_text = (TextView) findViewById(R.id.tv_noconn_announce);
		tv_text.setTypeface(Typeface.createFromAsset(
				NoConnectionActivity.this.getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		TextView tv_off = (TextView) findViewById(R.id.tv_noconn_off);
		tv_off.setTypeface(Typeface.createFromAsset(
				NoConnectionActivity.this.getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		TextView tv_on = (TextView) findViewById(R.id.tv_noconn_on);
		tv_on.setTypeface(Typeface.createFromAsset(
				NoConnectionActivity.this.getAssets(),
				"fonts/ProximaNova-Reg.otf"));

		tv_off.setOnClickListener(this);
		tv_on.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_noconn_on:

			if (CommonHelper.checkInternetConnection()) {

				NoConnectionActivity.this.finish();
			} else {
				showErrorBox(Defines.ERROR_CONNECTION, false);

			}

			break;
		case R.id.tv_noconn_off:

			break;

		}

	}

	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);

	}

}
