package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class UserPhotoQuery extends WebQuery 
{
	public UserPhotoQuery(AppSettings appSettings, String barId, int offset, int limit) 
	{
		mURLBuilder.setPath("Checkins/List_foto_user");

		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());

		mURLBuilder.setParameter("bar_id", barId);
		mURLBuilder.setParameter(LIST_OFFSET, Integer.toString(offset));
		mURLBuilder.setParameter(LIST_LIMIT, Integer.toString(limit));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				if (mJSONResponse.opt("code") != null) 
				{
					data.error_code = 3;
					
					return data;
				}

				JSONArray array = mJSONResponse.getJSONArray("array_foto");
				
				for (int i = 0; i < array.length(); ++i) 
				{
					UserPhoto uPhoto = new UserPhoto();
					JSONObject object = array.getJSONObject(i);

					// photo id
					uPhoto.id = object.optInt("id");
					if ((object.optString("picture") != null) && !object.optString("picture").equals("") &&	!object.optString("picture").equals("null"))
					{
						uPhoto.picture = object.optString("picture");
					}
					
					// photo comment
					if ((object.optString("comment") != null) && !object.optString("comment").equals("") &&	!object.optString("comment").equals("null"))
					{
						uPhoto.comment = object.optString("comment");
					}
					
					// user id
					uPhoto.user_id = object.optInt("user_id");
					
					// user_name
					if ((object.optString("user") != null) && !object.optString("user").equals("") && !object.optString("user").equals("null"))
					{
						uPhoto.user_name = object.optString("user");
					}

					// user avatar
					if (object.optString("avatar") != null && !object.optString("avatar").equals("") && !object.optString("avatar").equals("null"))
					{
						uPhoto.avatar = object.optString("avatar");
					}

					data.photos.add(uPhoto);
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int error_code = -1;
		public ArrayList<UserPhoto> photos = new ArrayList<UserPhotoQuery.UserPhoto>();
	}

	public class UserPhoto 
	{
		public int 		id;
		public String 	picture;
		public String 	comment;
		public int 		user_id;		
		public String 	user_name;
		public String 	avatar;
	}
}
