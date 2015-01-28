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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Add_Exercise extends ActionBarActivity implements SensorEventListener {
	
	//UI implementation
	private ProgressBar timerBar;
	private Sensor mSensor;
	private TextView elapsedtext;
	Button SaveBtn, CancelBtn, Record;
	private Boolean recording=false;
	TimerCountdown countdown;
	private EditText name,description;
	
	//Read in sensor values temp storage
	private int counter=0,count = 0;
	private float[] Xaccel, Yaccel, Zaccel = new float[60]; 
	private float[] Xgyro, Ygyro, Zgyro = new float[60];
	private float[] Xrot, Yrot, Zrot = new float[60];
	
	//SENSOR ACTIVATION and EDITING value
	private SensorManager mSensorManager;
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;
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
	private float[] currentRotationMatrixCalibrated= new float[9];
	private float[] deltaRotationMatrixCalibrated= new float[9];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		name = (EditText)findViewById(R.id.ExerciseName);
		description = (EditText)findViewById(R.id.ExerciseDescription);
		elapsedtext = (TextView)findViewById(R.id.elapsedtime);
		SaveBtn = (Button)findViewById(R.id.buttonSave);
		CancelBtn = (Button)findViewById(R.id.buttonCancel);
		Record = (Button)findViewById(R.id.StartRecording);
		elapsedtext.setText(String.valueOf(5));
		timerBar = (ProgressBar)findViewById(R.id.progressBarTimer);
		timerBar.setMax(5);
		timerBar.setIndeterminate(false);
		
		
		
		//GETTING LATE CURRENTLY REVISE OVER LOGIC!
		Record.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v) {
				 if(!recording){
					 startsensor();
					 elapsedtext.setText(String.valueOf(5));
	            	 Record.setEnabled(false);
	            	 recording=true;
	            	 timerBar.setProgress(5);
	            	 countdown =  new TimerCountdown(5000, 1000);
	            	 countdown.start();
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
			  int progress = (int) (millisUntilFinished/1000);
			  elapsedtext.setText(String.valueOf(progress));
			  timerBar.setProgress(progress);
		  }
		 
		  @Override
		  public void onFinish() {
			  elapsedtext.setText("Finished");
			  timerBar.setProgress(0);
			  stopsensor();
			  recording=false;
			  WritetoDatabase();
			  Record.setEnabled(false);
			  Record.setText("Start Recording Exercise");
		  }
	 }  
		 
	//SENSORS UPDATE METHOD 
	@SuppressLint("NewApi") 
	public void onSensorChanged(SensorEvent event){
    	float[] accelerometervalues;
		float[] orientationVals = new float[3];
		DBmanager db;
		db = new DBmanager(this);
		//LINEAR ACCELEROMETER
		if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            accelerometervalues = addLinearAcc(event.values.clone());
            
            Xaccel[counter] = accelerometervalues[0];
            Yaccel[counter] = accelerometervalues[1];
            Zaccel[counter] = accelerometervalues[2];
		}
		//GYROSCOPE
		else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
	       	Xgyro[counter] = event.values[0];
	    	Ygyro[counter] = event.values[1];
	    	Zgyro[counter] = event.values[2];
	    }
		//ROTATION VECTOR
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
	        
	        Xrot[counter] = orientationVals[0];
	        Yrot[counter] = orientationVals[1];
	        Zrot[counter] = orientationVals[2];
		}
		counter++;
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	//LINEAR ACCELERATOR CODE
	//CHANGED FROM THE NORMAL ACCELEROMETER
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
	
	/*This method is used to write from the array into the database
	There are a few ways I want to experiment with on how to write the data to the db,
	need to make it the data conform to a general standard such as taking null values away to avoid margins of error
	so if this works then it means anything under a certain value will mark as 0 and all 0 values will not be stored.
	this leaves pure movement.
	Another way is to have a start and end identifier which could also be a 0.
	Reason for choosing 0 is because means no movement and the sensor will never have a straight 0 value, will always have 0.0001 of something.
	*/
	
	public void WritetoDatabase(){
		//initialise the database
		DBmanager db;
		db = new DBmanager(this);
		//Version 1 - try with no null values - test to see values in action
		for(int i = 0 ; i <= 60;i++){
			db.insertToExerciseSample(String.valueOf(name.getText()), i, Xrot[i], Yrot[i], Zrot[i], Xaccel[i], Yaccel[i], Zaccel[i], Xgyro[i], Ygyro[i], Zgyro[i]);
		}
		if(name.getText()!=null && description.getText()!=null){
			db.insertToExercise(String.valueOf(name.getText()), String.valueOf(description.getText()));	//Version 2 - Test with different body types - check values
		}
		
		//Version 3 - Take null values away if still feasible
		
		
		//Final Version
		
		
		//closing database
		db.close();
	}
}
