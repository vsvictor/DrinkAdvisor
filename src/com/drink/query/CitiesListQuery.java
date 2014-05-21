package com.drink.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class CitiesListQuery extends WebQuery 
{
	public CitiesListQuery() 
	{
		mURLBuilder.setPath("cities");
		mURLBuilder.setParameter("version", String.valueOf(System.currentTimeMillis()));
	}

	private class SortCitiesByName implements Comparator<CitiesListQuery.City>
	{
		@Override
		public int compare(CitiesListQuery.City o1, CitiesListQuery.City o2) 
		{
		    return o1.name.compareToIgnoreCase(o2.name);
		}
	}

	private class SortCountriesByCitiesAndName implements Comparator<String>
	{
		private HashMap<String, Integer> countries;
		
		public SortCountriesByCitiesAndName(HashMap<String, Integer> countries)
		{
			this.countries = countries;
		}
		
		@Override
		public int compare(String c1, String c2) 
		{
			if (countries.get(c1) < countries.get(c2)) return 1;
			if (countries.get(c1) > countries.get(c2)) return -1;
			
		    return c1.compareToIgnoreCase(c2);
		}
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult())
		{
			try 
			{
				JSONArray array = mJSONResponse.getJSONArray("array");
				
				ArrayList<City> cities =  new ArrayList<CitiesListQuery.City>();
				for (int i = 0; i < array.length(); ++i) 
				{
					JSONObject object = array.getJSONObject(i);
					City city = new City();
					city.id = object.optInt("id");
					city.name = object.optString("title");
					city.country = object.optString("country");
					city.lat = object.optDouble("map_lat");
					city.lng = object.optDouble("map_lng");
					
					cities.add(city);
				}
				
				Collections.sort(data.cities, new SortCitiesByName());

				HashMap<String, Integer> countries = new HashMap<String, Integer>();
				for (int i = 0; i < cities.size(); ++i)
				{
					String country = cities.get(i).country;
					if (country == null) continue;
					
					Integer val = countries.get(country);
					if (val == null)
					{
						countries.put(country, 1);
					}
					else
					{
						countries.put(country, val + 1);
					}
				}

				Set<String> keys = countries.keySet();
				// sort countries
				ArrayList<String> sorted = new ArrayList<String>();
				Iterator<String> it = keys.iterator();
				while (it.hasNext())
				{
					sorted.add(it.next());
				}
				
				Collections.sort(sorted, new SortCountriesByCitiesAndName(countries));
				
				for (int i = 0; i < sorted.size(); ++i)
				{
					String country = sorted.get(i);
					
					City city = new City();
					city.id = -1;
					city.name = country;

					data.cities.add(city);
					
					for (int j = 0; j < cities.size(); ++j)
					{
						City _city = cities.get(j);
						if (_city.country.equals(country))
						{
							data.cities.add(_city);
						}
					}
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<City> cities =  new ArrayList<CitiesListQuery.City>();
	}

	public class City 
	{
		public int id;
		public String name;
		public String country;
		public double lat = 0.0;
		public double lng = 0.0;
	}
}