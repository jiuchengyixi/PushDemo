package com.common.android.utils.screen;

import android.content.Context;
import android.content.SharedPreferences;

/**
* This class is used to store preferences and acts as shared memory
* between the activity and the service
*/
public class SharedMemory {
	private SharedPreferences prefs;
	
	public SharedMemory(Context ctx){
		prefs=ctx.getSharedPreferences("SCREEN_SETTINGS", Context.MODE_PRIVATE);
	}
	
	public void setAlpha(int value){
		prefs.edit().putInt("alpha", value).commit();
	}
	
	public void setRed(int value){
		prefs.edit().putInt("red", value).commit();
	}
	
	public void setGreen(int value){
		prefs.edit().putInt("green", value).commit();
	}
	
	public void setBlue(int value){
		prefs.edit().putInt("blue", value).commit();
	}
	
	public int getBlue(){
		return prefs.getInt("blue", 0x00);
	}
	
	public int getGreen(){
		return prefs.getInt("green", 0x00);
	}
	
	public int getRed(){
		return prefs.getInt("red", 0x00);
	}
	
	public int getAlpha(){
		return prefs.getInt("alpha", 0x00);
	}
	
	public static int getColor(int alpha, int red, int green, int blue){
		String hex = String.format("%02x%02x%02x%02x", alpha, red, green, blue);
		int color=(int)Long.parseLong(hex,16);
		return color;
	}
	
	public int getColor(){
		String hex = String.format("%02x%02x%02x%02x", getAlpha(), getRed(), getGreen(), getBlue());
		int color=(int)Long.parseLong(hex,16);
		return color;
	}
}
