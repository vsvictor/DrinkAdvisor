package com.drink.types;

import java.util.ArrayList;

public class BaseObjectList  extends ArrayList<BaseObject>{
	public BaseObject find(int id){
		BaseObject result = null;
		for(BaseObject b : this){
			if(b.getID() == id) return b;
		}
		return result;
	}
	public BaseObject find(String name){
		BaseObject result = null;
		for(BaseObject b : this){
			if(b.getName().equals(name)) return b;
		}
		return result;
	}
}
