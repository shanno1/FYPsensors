package com.example.sensorsfyp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math.*;
import java.text.DecimalFormat;


public class Menu extends ActionBarActivity implements SensorEventListener{
	private TextView AccX,AccY,AccZ,OrX,OrY,OrZ,MagX,MagY,MagZ;
	 SensorManager mSensorManager;
	 Sensor mSensor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        AccX = (TextView)findViewById(R.id.AccX);
        AccY = (TextView)findViewById(R.id.AccY);
        AccZ = (TextView)findViewById(R.id.AccZ);
        
        OrX = (TextView)findViewById(R.id.OrX);
        OrY = (TextView)findViewById(R.id.OrY);
        OrZ = (TextView)findViewById(R.id.OrZ);
        
        MagX = (TextView)findViewById(R.id.MagX);
        MagY = (TextView)findViewById(R.id.MagY);
        MagZ = (TextView)findViewById(R.id.MagZ);
        init();
    }

    public void onSensorChanged(SensorEvent event){
    	float[] accelerometervalues;
		float[] orientationvalues;
		float[] geomagneticmatrix;
		switch(event.sensor.getType()){
	        case Sensor.TYPE_ACCELEROMETER:
	            accelerometervalues = addSamples(event.values.clone());
	            AccX.setText("X: "+accelerometervalues[0]);
	            AccY.setText("Y: "+accelerometervalues[1]);
	            AccZ.setText("Z: "+accelerometervalues[2]);
	            break;
	
	        case Sensor.TYPE_GYROSCOPE:
	        	onGyroscopeSensorChanged(event.values, event.timestamp);
	            break;
	    }
        
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	// Constants for the low-pass filters
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;
	 
	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();
	 
	// Gravity and linear accelerations components for the
	// Wikipedia low-pass filter
	private float[] gravity = new float[]
	{ 0, 0, 0 };
	 
	private float[] linearAcceleration = new float[]
	{ 0, 0, 0 };
	 
	// Raw accelerometer data
	private float[] input = new float[]
	{ 0, 0, 0 };
	 
	private int count = 0;
	 
	/**
	* Add a sample.
	* 
	* @param acceleration
	*            The acceleration data.
	* @return Returns the output of the filter.
	*/
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private long timestampOldCalibrated =0;
	private boolean hasInitialOrientation = false;
	private boolean stateInitializedCalibrated = false;
	private DecimalFormat df;
	public static final float EPSILON = 0.000000001f;
	private float[] initialRotationMatrix;
	private float[] currentRotationMatrixCalibrated;
	private float[] deltaRotationMatrixCalibrated;
	private float[] deltaRotationVectorCalibrated;
	private float[] gyroscopeOrientationCalibrated;
	
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) public void onGyroscopeSensorChanged(float[] gyroscope, long timestamp){
		// don't start until first accelerometer/magnetometer orientation has
		// been acquired

		if (!hasInitialOrientation){return;}
		// Initialization of the gyroscope based rotation matrix
		
		if (!stateInitializedCalibrated)
		{
			currentRotationMatrixCalibrated = matrixMultiplication(currentRotationMatrixCalibrated, initialRotationMatrix);
			stateInitializedCalibrated = true;
		}
		
		// This timestep's delta rotation to be multiplied by the current
		// rotation after computing it from the gyro sample data.
	
		if (timestampOldCalibrated != 0 && stateInitializedCalibrated)
		{
			final float dT = (timestamp - timestampOldCalibrated) * NS2S;
			// Axis of the rotation sample, not normalized yet.
			float axisX = gyroscope[0];
			float axisY = gyroscope[1];
			float axisZ = gyroscope[2];
			// Calculate the angular speed of the sample
			float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
			// Normalize the rotation vector if it's big enough to get the axis
			if (omegaMagnitude > EPSILON)
			{
				axisX /= omegaMagnitude;
				axisY /= omegaMagnitude;
				axisZ /= omegaMagnitude;
			}

			// Integrate around this axis with the angular speed by the timestep
			// in order to get a delta rotation from this sample over the
			// timestep. We will convert this axis-angle representation of the
			// delta rotation into a quaternion before turning it into the
			// rotation matrix.
			float thetaOverTwo = omegaMagnitude * dT / 2.0f;
			float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
			float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
			
			deltaRotationVectorCalibrated[0] = sinThetaOverTwo * axisX;
			deltaRotationVectorCalibrated[1] = sinThetaOverTwo * axisY;
			deltaRotationVectorCalibrated[2] = sinThetaOverTwo * axisZ;
			deltaRotationVectorCalibrated[3] = cosThetaOverTwo;
			
			SensorManager.getRotationMatrixFromVector(
					deltaRotationMatrixCalibrated,
					deltaRotationVectorCalibrated);

			currentRotationMatrixCalibrated = matrixMultiplication(
					currentRotationMatrixCalibrated,
					deltaRotationMatrixCalibrated);

			SensorManager.getOrientation(currentRotationMatrixCalibrated,
					gyroscopeOrientationCalibrated);
		
			
			
		}
		timestampOldCalibrated = timestamp;
		OrX.setText("Yaw: "+ df.format(gyroscopeOrientationCalibrated[0]));
        OrY.setText("Pitch: "+df.format(gyroscopeOrientationCalibrated[1]));
        OrZ.setText("Role: "+df.format(gyroscopeOrientationCalibrated[2]));
	}
	public float[] addSamples(float[] acceleration)
	{
		// Get a local copy of the sensor values
		System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);
		 
		timestamp = System.nanoTime();
		 
		// Find the sample period (between updates).
		// Convert from nanoseconds to seconds
		dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));
		 
		count++;
		         
		alpha = timeConstant / (timeConstant + dt);
		 
		gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];
		 
		linearAcceleration[0] = input[0] - gravity[0];
		linearAcceleration[1] = input[1] - gravity[1];
		linearAcceleration[2] = input[2] - gravity[2];
		 
		return linearAcceleration;
	}
	private float[] matrixMultiplication(float[] a, float[] b)
    {
        float[] result = new float[9];
 
        result[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
        result[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
        result[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];
 
        result[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
        result[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
        result[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];
 
        result[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
        result[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
        result[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];
 
        return result;
    }
	private void init(){
		initialRotationMatrix = new float[9];

		deltaRotationVectorCalibrated = new float[4];
		deltaRotationMatrixCalibrated = new float[9];
		currentRotationMatrixCalibrated = new float[9];
		gyroscopeOrientationCalibrated = new float[3];

		// Initialize the current rotation matrix as an identity matrix...
		currentRotationMatrixCalibrated[0] = 1.0f;
		currentRotationMatrixCalibrated[4] = 1.0f;
		currentRotationMatrixCalibrated[8] = 1.0f;
	}
	
}

