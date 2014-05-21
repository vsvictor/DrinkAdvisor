package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;
import com.drink.types.Block;
import com.drink.types.Comment;

import android.content.Context;
import android.util.Log;

public class BlogPostQueryAdapted extends WebQuery {
	private int type;

	public BlogPostQueryAdapted(Context context, String id, int type) {
		this.type = type;
		if (type == 2) {
			mURLBuilder.setPath("blog/" + id);
			mURLBuilder.setParameter("device_token",
					new AppSettings(context).getContentToken());
			mURLBuilder.setParameter("user_token",
					new AppSettings(context).getUserToken());
			getResponse(WebQuery.GET);
		} else {
			mURLBuilder.setPath("blog/getV2");
			mURLBuilder.setParameter("device_token",
					new AppSettings(context).getContentToken());
			mURLBuilder.setParameter("user_token",
					new AppSettings(context).getUserToken());
			mURLBuilder.setParameter("postId", id);

			getResponse(WebQuery.GET);
		}
	}

	@Override
	public Data getData() {
		Data data = new Data();

		if (getResult()) {
			switch (type) {
			case 2:
				try {

					data.type = 2;
					data.date = mJSONResponse.optString("date");
					data.title = mJSONResponse.optString("title");
					data.text = mJSONResponse.optString("text");
					data.picture = mJSONResponse.optString("picture");
					JSONArray array = mJSONResponse.optJSONArray("comments");
					for (int i = 0; i < array.length(); i++) {
						Block block = new Block();
						block.type = "100";
						Comment comment = new Comment();
						JSONObject object = array.getJSONObject(i);

						comment.id = object.optString("id");
						comment.text = object.optString("text");
						comment.created_at = object.optString("created_at");
						comment.user_id = object.optString("user_id");
						comment.user_name = object.optString("user_name");
						comment.user_picture = object.optString("user_picture");
						comment.bar_rating = object.optString("bar_rating");
						comment.last_checkin_at = object
								.optString("last_checkin_at");
						comment.text_lang = object.optString("text_lang");
						block.comments.add(comment);

						data.blocks.add(block);
					}

				} catch (Exception e) {
				}
				break;
			case 1:
				try {
					data.type = 1;
					data.id = mJSONResponse.optString("id");
					data.date = mJSONResponse.optString("date");
					data.title = mJSONResponse.optString("title");
					data.picture = mJSONResponse.optString("picture");
					JSONObject user = mJSONResponse.getJSONObject("author");
					data.authId = user.optString("id");
					data.authName = user.optString("name");
					data.authPic = user.optString("picture");
					data.shText= user.optString("shText");
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

							if (object.has("childrens")) {
								JSONArray childrens = object
										.getJSONArray("childrens");
								if (childrens.length() > 0) {
									block.arr_item = 0;

									for (int j = 0; j < childrens.length(); j++) {
										Block block_child = new Block();
										JSONObject object1 = childrens
												.getJSONObject(j);
										if (object1.has("id")) {
											block_child.id = object1
													.optString("id");
										}
										block_child.type = object1
												.optString("type");

										block_child.text = object1
												.optString("text");
										block_child.goal_type = object1
												.optString("goal_type");
										block_child.goal_id = object1
												.optString("goal_id");
										block_child.picture = object1
												.optString("picture");

										block_child.arr_item = j;
										if (object1.has("number")) {
											block_child.number = object1
													.optString("number");
										} else
											block_child.number = null;

										block.child_blocks.add(block_child);
									}
								}
							}
							data.blocks.add(block);
						}

					}
					Block block = new Block();
					block.type = "110";
					data.blocks.add(block);
				} catch (Exception e) {
					Log.e("error parsing", e.toString());
				}

				break;
			}

		}

		return data;
	}

	public class Data extends WebQueryData {

		public int type;
		public String date;
		public String title;
		public String text;
		public String picture;
		public String shText;
		// part of comments
		public String id;
		public String authId;
		public String authName;
		public String authPic;
		public ArrayList<Block> blocks = new ArrayList<Block>();

	}
}
