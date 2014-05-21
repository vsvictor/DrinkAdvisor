package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;
import com.drink.types.Block;
import android.content.Context;

public class ReviewOneQuery extends WebQuery {
	public ReviewOneQuery(Context context, String id) {

		mURLBuilder.setPath("blog/getV2");
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("postId", id);
		
		getResponse(WebQuery.GET);
	}


	@Override
	public Data getData() {
		Data data = new Data();

		if (getResult()) {
			try {
				data.id = mJSONResponse.optString("id");
				data.date = mJSONResponse.optString("date");
				data.title = mJSONResponse.optString("title");
				data.picture = mJSONResponse.optString("picture");
				JSONObject user = mJSONResponse.getJSONObject("author");
				data.authId = user.optString("id");
				data.authName = user.optString("name");
				data.authPic = user.optString("picture");
				JSONArray array = mJSONResponse.optJSONArray("blocks");
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						Block block = new Block();

						JSONObject object = array.getJSONObject(i);
						block.id = object.optString("id");
						block.type = object.optString("type");
						block.text = object.optString("text");
						block.goal_type = object.optString("goal_type");
						block.goal_id = object.optString("goal_id");

						JSONArray childrens = object.getJSONArray("childrens");
						if (childrens.length() > 0) {
							for (int j = 0; j < childrens.length(); j++) {
								Block block_child = new Block();
								block_child.id = object.optString("id");
								block_child.type = object.optString("type");
								block_child.text = object.optString("text");
								block_child.goal_type = object
										.optString("goal_type");
								block_child.goal_id = object
										.optString("goal_id");
								block_child.picture = object
										.optString("picture");
								block_child.number = object.optString("number");
								block.child_blocks.add(block_child);
							}
						}

						data.blocks.add(block);
					}
				}
			} catch (Exception e) {
			}
		}

		return data;
	}

	public class Data extends WebQueryData {
		public String date;
		public String title;
		public String id;
		public String picture;
		public String authId;
		public String authName;
		public String authPic;
		public ArrayList<Block> blocks = new ArrayList<Block>();
	}
}
