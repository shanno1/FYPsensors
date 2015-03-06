package com.example.sensorsfyp;

public class CompareAlg {
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
}
