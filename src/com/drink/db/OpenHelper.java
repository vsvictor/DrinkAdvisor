package com.drink.db;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.drink.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OpenHelper extends android.database.sqlite.SQLiteOpenHelper
{
	
	private static int dbVersion = 3;
	protected final Context context;

	public OpenHelper(Context context)//, String dbName)
	{
		super(context, "DrinkAdviser", null, dbVersion);
		this.context = context;
	}

	public void createFromDump(InputStream in, SQLiteDatabase db) throws IOException
	{
		String cmd = "";
		BufferedReader bReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = bReader.readLine();
		if(line != null)
		{
			db.beginTransaction();
			if(line.length() > 0 && line.charAt(0) == '\ufeff')
				line = line.substring(1);
			do
			{
				cmd += line.trim();
				if(cmd.length() > 0)
				{
					if(cmd.charAt(cmd.length() - 1) == ';')
					{
						try
						{
							db.execSQL(cmd);
						}
						catch(Exception e)
						{
							System.out.println(e.getMessage());
						}
						cmd = new String();
					}
					else
					{
						if(cmd.charAt(cmd.length() - 1) != ' ')
							cmd += ' ';
					}
				}
			}
			while((line = bReader.readLine()) != null);
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		else
			throw new IOException("data is empty");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		InputStream in = context.getResources().openRawResource(R.raw.struct);
		try
		{
			createFromDump(in, db);
			in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onCreate(db);
	}
}
