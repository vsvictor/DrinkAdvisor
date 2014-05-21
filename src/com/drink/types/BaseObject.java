package com.drink.types;

public class BaseObject {
	protected long id;
	protected String name;
	
	public BaseObject(){
		id = 0;
		name = "";
	}
	public BaseObject(int id, String name){
		this.id = id;
		this.name = name;
	}
	public BaseObject(long id, String name){
		this.id = id;
		this.name = name;
	}
	public void setID(int id){
		this.id = id;
	}
	public long getID(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
