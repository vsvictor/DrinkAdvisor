package com.drink.activity;

import com.drink.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class FriendsPhoneActivity extends Activity{
	static private final int PICK_CONTACT = 2;
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
		intent.setType(Phone.CONTENT_TYPE);
		startActivityForResult(intent, PICK_CONTACT);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK)
		{
			if(requestCode == PICK_CONTACT){
			     Uri contactData = data.getData();
			     Cursor c =  managedQuery(contactData, null, null, null, null);
			     if (c.moveToFirst()) 
			     {
		            Uri contactUri = data.getData();
		            String[] projection = {Phone.NUMBER};
		            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
		            cursor.moveToFirst();
		            int column = cursor.getColumnIndex(Phone.NUMBER);
		            String number = cursor.getString(column);
		            Uri uri = Uri.parse("smsto:" + number); 
		            Intent it = new Intent(Intent.ACTION_SENDTO, uri); 
		            it.putExtra("sms_body", getString(R.string.sms_text)); 
		            startActivity(it); 
			     }
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
}

