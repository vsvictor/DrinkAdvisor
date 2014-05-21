package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class BrandsQuery extends WebQuery {

	public BrandsQuery(String drink_id) {

		mURLBuilder.setPath("brands/listV2");

		setDrinkID(drink_id);
		setLimit("25");
		setOffset("0");

	}

	public void setDeviceToken(String string) {
		mURLBuilder.setParameter(DEVICE_TOKEN, string);
	}

	public void setUserToken(String string) {
		mURLBuilder.setParameter(USER_TOKEN, string);
	}

	public void setLimit(String string) {
		mURLBuilder.setParameter(LIST_LIMIT, string);
	}

	public void setOffset(String string) {
		mURLBuilder.setParameter(LIST_OFFSET, string);
	}

	public void setDrinkID(String string) {
		mURLBuilder.setParameter("drink_id", string);
	}

	@Override
	public Data getData() {

		Data data = new Data();
		data.brands = new ArrayList<BrandsQuery.Brand>();

		if (getResult()) {
			try {
				JSONArray arrayBrands = mJSONResponse.getJSONArray("Brands");

				data.name = mJSONResponse.optString("name");
				
				for (int i = 0; i < arrayBrands.length(); i++) {
					JSONObject obj = (JSONObject) arrayBrands.get(i);
					Brand brand = new Brand();

					brand.id = obj.optInt("id");
					brand.name = obj.optString("Name");
					brand.pic = obj.optString("picture");
					data.brands.add(brand);

				}

			} catch (Exception e) {

			}
		}
		return data;
	}

	public class Data extends WebQueryData {

		public String name;
		public ArrayList<Brand> brands;
	}

	public class Brand {

		public int id;
		public String name;
		public String pic;
	}

}
