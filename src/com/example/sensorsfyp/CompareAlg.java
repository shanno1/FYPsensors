package com.example.sensorsfyp;

import java.util.ArrayList;


public class CompareAlg {
	public final static float ACCEL_MIN_BUFFER = 0.0f;
	public final static float ACCEL_MAX_BUFFER = 0.0f;
	public final static float ROT_MIN_BUFFER = 0.0f;
	public final static float ROT_MAX_BUFFER = 0.0f;
	
	public boolean betweenInt(int var, int high, int low){
		if(var <= high && var >= low)
			return true;
		else
			return false;
	}
	
	public boolean betweenfloat(float var, float high, float low){
		if(var <= high && var >= low)
			return true;
		else
			return false;
	}
	
	public boolean bufferzone(float var, float high, float low){
		if(var <= high && var >= low)
			return true;
		else
			return false;
	}
	/*length is size of sample exercise array
	*Checks if length is greater than user array size
	*/
	public void ArrayAdd(ArrayList<float[]> userMove, int length, float[] newValue){
		if(userMove.size() <= length){
			userMove.add(newValue);
		}
		else{
			userMove.remove(0);
			userMove.add(newValue);
		}
	}
	
}
