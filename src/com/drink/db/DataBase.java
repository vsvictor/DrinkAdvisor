package com.drink.db;

import java.util.ArrayList;

import org.json.JSONObject;

import com.drink.query.FavBarsQuery.Data;
import com.drink.types.Bar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBase {
	final protected SQLiteDatabase db;
	final private OpenHelper openHelper;

	public DataBase(Context ctx) {
		openHelper = new OpenHelper(ctx);
		db = openHelper.getWritableDatabase();
	}

	public void close() {
		openHelper.close();
	}


	public void addNewBar(Bar bar) {
		if (!barExistInDB(bar.id)) {

			//String replace = replaceString.replace("'","\'");
			String sql = "Insert into bars (id, title , rating , dist, picture ,picture_name, latitude , longitude , name , common_info , city_id , city_name , address , phone) "
					+ " VALUES ('"
					+ bar.id
					+ "','"
					+ bar.title.replace("'", "`")
					+ "','"
					+ bar.rating
					+ "','"
					+ bar.dist
					+ "','"
					+ bar.picture
					+ "','"
					+ bar.picture_addr
					+ "','"
					+ bar.lat
					+ "','"
					+ bar.lon
					+ "','"
					+ bar.name.replace("'", "`")
					+ "','"
					+ bar.info.replace("'", "`")
					+ "','"
					+ bar.city_id
					+ "','"
					+ bar.city.replace("'", "`")
					+ "','"
					+ bar.address.replace("'", "`")
					+ "','" + bar.phone + "')";
			db.execSQL(sql);
		} else {

		}

	}

	private boolean barExistInDB(int id) {
		boolean retVal = false;
		String sql = "Select * from bars where id = '" + id + "'";
		Cursor curVal = db.rawQuery(sql, null);
		if (curVal.moveToFirst()) {
			retVal = true;
		}

		return retVal;

	}

	public ArrayList<Bar> getAllBars() {
		
		ArrayList<Bar> bars = new ArrayList<Bar>();

		Bar bar;
		String sql = "Select id, title , rating , dist, picture , latitude , longitude , name , common_info , city_id , city_name , address , phone  from bars ";
		Cursor curVal = db.rawQuery(sql, null);
		for (curVal.moveToFirst(); !curVal.isAfterLast(); curVal.moveToNext()) {
			bar = new Bar();
			bar.id = curVal.getInt(curVal.getColumnIndex("id"));
			bar.title = curVal.getString(curVal.getColumnIndex("title"));
			bar.rating = curVal.getInt(curVal.getColumnIndex("rating"));
			bar.dist = curVal.getInt(curVal.getColumnIndex("dist"));
			bar.picture = curVal.getString(curVal.getColumnIndex("picture"));
			bar.lat = curVal.getString(curVal.getColumnIndex("latitude"));
			bar.lon = curVal.getString(curVal.getColumnIndex("longitude"));
			bar.info = curVal.getString(curVal.getColumnIndex("common_info"));
			bar.city_id = curVal.getInt(curVal.getColumnIndex("city_id"));
			bar.city = curVal.getString(curVal.getColumnIndex("city_name"));
			bar.address = curVal.getString(curVal.getColumnIndex("address"));
			bar.phone = curVal.getString(curVal.getColumnIndex("phone"));
			
			bars.add(bar);

		}
		curVal.close();

		return bars;
	}

	
	public Bar getBarById(String iD) {
		Bar bar = new Bar(); 
		
		String sql = "Select id, title , rating , dist, picture , latitude , longitude , name , common_info , city_id , city_name , address , phone  from bars ";
		Cursor curVal = db.rawQuery(sql, null);
		if(curVal.moveToFirst())
		{
			bar.id = curVal.getInt(curVal.getColumnIndex("id"));
			bar.title = curVal.getString(curVal.getColumnIndex("title"));
			bar.rating = curVal.getInt(curVal.getColumnIndex("rating"));
			bar.dist = curVal.getInt(curVal.getColumnIndex("dist"));
			bar.picture = curVal.getString(curVal.getColumnIndex("picture"));
			bar.lat = curVal.getString(curVal.getColumnIndex("latitude"));
			bar.lon = curVal.getString(curVal.getColumnIndex("longitude"));
			bar.info = curVal.getString(curVal.getColumnIndex("common_info"));
			bar.city_id = curVal.getInt(curVal.getColumnIndex("city_id"));
			bar.city = curVal.getString(curVal.getColumnIndex("city_name"));
			bar.address = curVal.getString(curVal.getColumnIndex("address"));
			bar.phone = curVal.getString(curVal.getColumnIndex("phone"));
		}
		
		
		return bar;
	}
	
	

}
