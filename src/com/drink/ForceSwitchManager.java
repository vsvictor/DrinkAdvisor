package com.drink;

import java.util.HashMap;

import android.net.Uri;

public class ForceSwitchManager 
{
	public static ForceSwitchManager sharedInstance()
	{
		if (sm_instance == null)
		{
			sm_instance = new ForceSwitchManager();
		}
		
		return sm_instance;
	}
	
	private ForceSwitchManager()
	{
	}
	
	public boolean isNeedForceSwitch()
	{
		return (m_parameters.size() > 0);
	}
	
	public void resetForceSwitch()
	{
		m_parameters.clear();
	}
	
	public boolean parseURI(Uri uri)
	{
		resetForceSwitch();
		
		String model = uri.getQueryParameter("model");
		assert(model != null);
		m_parameters.put("model", model);
		
		String id = uri.getQueryParameter("id");
		assert(id != null);
		m_parameters.put("id", id);
		
		if (model.compareTo("rate_bar") == 0)
		{
			String rate = uri.getQueryParameter("rate");
			if (rate != null)
			{
				m_parameters.put("rate", rate);
			}
		}
		else if (model.compareTo("user") == 0)
		{
			String suggest = uri.getQueryParameter("sugest");
			if (suggest != null)
			{
				m_parameters.put("suggest", suggest);
			}
			else
			{
				String isfriend = uri.getQueryParameter("isfriend");
				if (isfriend != null)
				{
					m_parameters.put("isfriend", isfriend);
				}
			}
		}

		return true;
	}
	
	public String getParameter(String parameter)
	{
		return m_parameters.get(parameter);
	}
	
	private static ForceSwitchManager sm_instance;
	
	private HashMap<String, String> m_parameters = new HashMap<String, String>();
}
