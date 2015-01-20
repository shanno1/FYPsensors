package com.example.sensorsfyp;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Add_Exercise extends ActionBarActivity implements SensorEventListener {
	private SensorManager mSensorManager;
	private ProgressBar timerBar;
	private Sensor mSensor;
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;
	Button SaveBtn, CancelBtn, Record;
	long startTime;
	long elapsedTime = System.currentTimeMillis() - startTime;
	long elapsedSeconds = elapsedTime / 1000;
	private Boolean recording=false;
	 
	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();
	
	private DecimalFormat df = new DecimalFormat("#.###");
	// Gravity and linear accelerations components for the Wikipedia version of the low-pass filter 
	private float[] gravity = new float[]
	{ 0, 0, 0 };
	private float[] linearAcceleration = new float[]
	{ 0, 0, 0 };
	// Raw accelerometer data
	private float[] input = new float[]{ 0, 0, 0 };
	private int count = 0;

	
	private float[] currentRotationMatrixCalibrated= new float[9];
	private float[] deltaRotationMatrixCalibrated= new float[9];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		
		SaveBtn = (Button)findViewById(R.id.buttonSave);
		CancelBtn = (Button)findViewById(R.id.buttonCancel);
		Record = (Button)findViewById(R.id.StartRecording);
		
		timerBar = (ProgressBar)findViewById(R.id.progressBarTimer);
		timerBar.setMax(5);
		//GETTING LATE CURRENTLY REVISE OVER LOGIC!
		Record.setOnClickListener(new OnClickListener(){
			 @Override
	            public void onClick(View v) {
	               if(!recording){
	            	   startsensor();
	            	   Record.setText("Stop Recording Exercise");
	            	   recording=true;
	            	   startTime = System.currentTimeMillis();
	               }
	               else{
	            	   stopsensor();
	            	   Record.setText("Start Recording Exercise");
	            	   recording=false;
	            	   timerBar.setProgress(0);
	               }
	            	
	            }
		});
		elapsedTime = System.currentTimeMillis() - startTime;
  	   	elapsedSeconds = elapsedTime / 1000;
  	   	timerBar.setProgress((int)elapsedSeconds);
  	   	if(elapsedSeconds==5){
  	   		stopsensor();
  	   		timerBar.setProgress((int)elapsedSeconds);
  	   }
	}
	
	   public void startsensor(){
	    	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 200000); 
	        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 200000); 
	        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 200000);
	    }
	    public void stopsensor(){
	   	 	mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)); 
	        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)); 
	        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
	   }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add__exercise, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi") public void onSensorChanged(SensorEvent event){
    	float[] accelerometervalues;
		float[] orientationVals = new float[3];
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometervalues = addLinearAcc(event.values.clone());
            //df.format(accelerometervalues[0]));
            //df.format(accelerometervalues[1]));
            //df.format(accelerometervalues[2]));
		}
		else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
	       	//df.format(event.values[0];
	       	//df.format(event.values[1];
			//df.format(event.values[2];
	    }
		else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
			// Convert the rotation-vector to a 4x4 matrix.
	        SensorManager.getRotationMatrixFromVector(currentRotationMatrixCalibrated, event.values);
	        SensorManager.remapCoordinateSystem(currentRotationMatrixCalibrated,
	                    SensorManager.AXIS_X, SensorManager.AXIS_Z,
	                    deltaRotationMatrixCalibrated);
	        SensorManager.getOrientation(deltaRotationMatrixCalibrated, orientationVals);

	        // Optionally convert the result from radians to degrees
	        orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
	        orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
	        orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);
        	
		}
        
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public float[] addLinearAcc(float[] acceleration)
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
}
