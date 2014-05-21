package com.drink.query;

import com.drink.query.FriendUnsubsrcibe.Data;
import com.drink.settings.AppSettings;

public class FriendSubscribe extends WebQuery{
	public FriendSubscribe(AppSettings appSettings, long id_user){
		mURLBuilder.setPath("follow/beginFollow");
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id_from",	appSettings.getUserId());
		mURLBuilder.setParameter("user_id", String.valueOf(id_user));
	}
	public void setOnlyReq() 
	{
		mURLBuilder.setParameter("only_requests", "1");
	}

	@Override
	public WebQueryData getData() {
		Data data = new Data();
		if (getResult()) {
			try {
				data.setError(mJSONResponse.getString("success").equals("true"));
				data.setAllow(mJSONResponse.getString("allow").equals("true"));
			} catch (Exception e) {
			}
		}
		return data;
	}
	public class Data extends WebQueryData 
	{
		private boolean error;
		private boolean allow;
		public Data(){
			error = false;
			allow = true;
		}
		public void setError(boolean error){
			this.error = error;
		}
		public boolean getError(){
			return error;
		}
		public void setAllow(boolean allow){
			this.allow = allow;
		}
		public boolean getAllow(){
			return this.allow;
		}
	}
}
