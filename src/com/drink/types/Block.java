package com.drink.types;

import java.util.ArrayList;

public class Block 
{
	public String id;
	public String type;
	public String text;
	public String goal_type;
	public String goal_id;
	public String picture;
	public String number;
	public int arr_item;
	public ArrayList<Block> child_blocks = new ArrayList<Block>();
	public ArrayList<Comment> comments = new ArrayList<Comment>();
	public void setArrPos(int pos)
	{
		this.arr_item = pos;
	}

}

