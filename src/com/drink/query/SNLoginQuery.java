
package com.drink.query;

import com.drink.settings.AppSettings;

public class SNLoginQuery extends WebQuery 
{
	public SNLoginQuery(AppSettings appSettings, String type, String accessToken, String userId) 
	{
		mURLBuilder.setPath("Profile_social/Add_halogin");

		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());
		
		mURLBuilder.setParameter("reg_type", type);
		mURLBuilder.setParameter("social_access_token", accessToken);
		mURLBuilder.setParameter("social_identifier", userId);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult())
		{
			try 
			{
				data.success = ((mJSONResponse.opt("error") == null) && (mJSONResponse.opt("code") == null));
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public boolean success = false;
	}
}
