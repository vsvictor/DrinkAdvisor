package com.drink.query;

import com.drink.query.FriendSubscribe.Data;
import com.drink.settings.AppSettings;

public class FriendEnableWatch extends WebQuery{
		public FriendEnableWatch(AppSettings appSettings, long id_user){
			mURLBuilder.setPath("follow/allow");
			mURLBuilder.setParameter("device_token", appSettings.getContentToken());
			mURLBuilder.setParameter("user_token", appSettings.getUserToken());
			mURLBuilder.setParameter("user_id_from",	appSettings.getUserId());
			mURLBuilder.setParameter("user_id", String.valueOf(id_user));
		}
/*		public void setOnlyReq() 
		{
			mURLBuilder.setParameter("only_requests", "1");
		}*/
		@Override
		public WebQueryData getData() {
			Data data = new Data();
			if (getResult()) {
				try {
					int i = 0;
					i++;
				} catch (Exception e) {
				}
			}
			return data;
		}
		public class Data extends WebQueryData 
		{
		}
}
