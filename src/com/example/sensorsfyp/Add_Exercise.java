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
import android.os.CountDownTimer;
import android.os.Handler;
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
	private TextView elapsedtext;
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;
	Button SaveBtn, CancelBtn, Record;
	long startTime;
	long elapsedTime = System.currentTimeMillis() - startTime;
	long elapsedSeconds = elapsedTime / 1000;
	private Boolean recording=false;
	TimerCountdown countdown;
	
	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();
	Handler handler=new Handler();
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
		
		elapsedtext = (TextView)findViewById(R.id.elapsedtime);
		SaveBtn = (Button)findViewById(R.id.buttonSave);
		CancelBtn = (Button)findViewById(R.id.buttonCancel);
		Record = (Button)findViewById(R.id.StartRecording);
		
		timerBar = (ProgressBar)findViewById(R.id.progressBarTimer);
		//GETTING LATE CURRENTLY REVISE OVER LOGIC!
		Record.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v) {
				 if(!recording){
					 startsensor();
	            	 Record.setText("Stop Recording Exercise");
	            	 recording=true;
	            	 timerBar.setProgress(100);
	            	 countdown =  new TimerCountdown(500, 100);
	            	 countdown.start();
	             }
	             else{
	            	 stopsensor();
	            	 countdown.onFinish();
	            	 Record.setText("Start Recording Exercise");
	            	 recording=false;
	            	
	             }
			 }
		});
	}
	
	public void startsensor(){
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 100000); 
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100000); 
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 100000);
	}
	public void stopsensor(){
		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)); 
	    mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)); 
	    mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
	}
	
	public class TimerCountdown extends CountDownTimer {
   	
		 public TimerCountdown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		 }
		 
		  @Override
		  public void onTick(long millisUntilFinished) {
			  int progress = (int) (millisUntilFinished/100);
			  timerBar.setProgress(progress);
		  }
		 
		  @Override
		  public void onFinish() {
			  elapsedtext.setText("Finished");
			  timerBar.setProgress(0);
		  }
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
