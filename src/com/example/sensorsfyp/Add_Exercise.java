package com.example.sensorsfyp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteException;
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
import android.widget.Toast;

public class Add_Exercise extends ActionBarActivity implements SensorEventListener {
	
	//UI implementation
	private ProgressBar timerBar;
	private Sensor mSensor;
	private TextView elapsedtext;
	private Button SaveBtn, CancelBtn, Record;
	private Boolean recording=false;
	TimerCountdown countdown;
	private EditText name,description;
	
	//Read in sensor values temp storage
	private int countacc=0,countgyro=0,countrot=0,count = 0;
	private ArrayList<float[]> GyroArr = new ArrayList<float[]>();
	private ArrayList<float[]> RotArr = new ArrayList<float[]>();
	private ArrayList<float[]> AccelArr = new ArrayList<float[]>();
	//SENSOR ACTIVATION and EDITING value
	private SensorManager mSensorManager;
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;
	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();
	private DecimalFormat df = new DecimalFormat("#.######");
	// Gravity and linear accelerations components for the Wikipedia version of the low-pass filter 
	private float[] gravity = new float[]
	{ 0, 0, 0 };
	private float[] linearAcceleration = new float[]
	{ 0, 0, 0 };
	
	// Raw accelerometer data
	private float[] input = new float[]{ 0, 0, 0 };
	private float[] currentRotationMatrixCalibrated= new float[9];
	private float[] deltaRotationMatrixCalibrated= new float[9];
	private DBmanager db = new DBmanager(this);
	private static final int SENSOR_RATE = 500000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		
		//-------------------------------------------------------
		//UI Initialisation
		//-------------------------------------------------------
		name = (EditText)findViewById(R.id.ExerciseName);
		description = (EditText)findViewById(R.id.ExerciseDescription);
		elapsedtext = (TextView)findViewById(R.id.elapsedtime);
		SaveBtn = (Button)findViewById(R.id.buttonSave);
		CancelBtn = (Button)findViewById(R.id.buttonCancel);
		Record = (Button)findViewById(R.id.StartRecording);

		SaveBtn.setEnabled(false);
		timerBar = (ProgressBar)findViewById(R.id.progressBarTimer);
		timerBar.setMax(5);
		timerBar.setIndeterminate(false);
		elapsedtext.setText(String.valueOf(5));
		
		
		
		
		//-------------------------------------------------------
		//Button Click Listeners
		//-------------------------------------------------------
		Record.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v) {
				 if(!recording){
					 recording = true;
					 timerBar.setProgress(5);
					 countdown =  new TimerCountdown(5000, 1000);
					 countdown.start();
					 startsensor();
				 }
			 }
		});
		SaveBtn.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v) {
				if(name.getText().toString().trim().length() > 0 && description.getText().toString().trim().length() > 0){
					WritetoDatabase();
		            finish();
			 	} 
			 	else{
			 		Toast.makeText(getApplicationContext(), "Please fill the name and description boxes!", 1).show();
			 	}
		 
			 }
		});
		CancelBtn.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v) {
	             finish();
			 }
		});
		
	}
	
	
	//-------------------------------------------------------
	//Sensor Manager Registration
	//-------------------------------------------------------
	@SuppressLint("InlinedApi") public void startsensor(){
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SENSOR_RATE); 
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_RATE); 
	    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SENSOR_RATE);
	}
	@SuppressLint("InlinedApi") public void stopsensor(){
		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)); 
	    mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)); 
	    mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
	}
	//-------------------------------------------------------
	
	
	public void AddtoArray(String type, float[] array){
		float[] tempArr = new float[8];
		if (type=="gyro"){
			//System.out.println("Gyro X: "+ array[0] + "\nY: " +array[1] + "\n Z: " + array[2]);
			GyroArr.add(array);
		}
		else if (type=="rot"){
			//System.out.println("Rot X: "+ array[0] + "\nY: " +array[1] + "\n Z: " + array[2]);
			RotArr.add(array);
		}
		else if (type=="accel"){
			//System.out.println("Accel X: "+ array[0] + "\nY: " +array[1] + "\n Z: " + array[2]);
			AccelArr.add(array);
			
		}
	}
	
	//-------------------------------------------------------
	//5 second clock timer for exercise recording 
	//-------------------------------------------------------
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
			  //WritetoDatabase();
			  Record.setEnabled(true);
			  countrot = 0;
			  countacc = 0;
			  countgyro = 0;
			  Record.setText("Start Recording Exercise");
			  SaveBtn.setEnabled(true);
		  }
	 }  
		 
	//SENSORS UPDATE METHOD 
	@SuppressLint("NewApi") 
	public void onSensorChanged(SensorEvent event){
    	float[] accelerometervalues;
		float[] orientationVals = new float[3];
		
		//LINEAR ACCELEROMETER
		if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            accelerometervalues = addLinearAcc(event.values.clone());
            
            AddtoArray("accel",accelerometervalues);
            countacc++;
            System.out.println("Accel sensor count: "+ countacc);
		}
		//GYROSCOPE
		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			AddtoArray("gyro",event.values);
	    	countgyro++;
	    	System.out.println("Gyro sensor count: "+ countgyro);
	    }
		//ROTATION VECTOR
		if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
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
	        
	        AddtoArray("rot",orientationVals);
	        countrot++;
	        System.out.println("Rot sensor count: "+ countrot);
		}
		
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
	private float[] gyro = new float[2];
	private float[] rot = new float[2];
	private float[] acc = new float[2];
	
	public void WritetoDatabase(){
		//initialise the database
		db.open();
		int i =0;
		
		//Version 1 - try with no null values - test to see values in action
		try{
			Iterator<float[]> aIt = AccelArr.iterator();
			Iterator<float[]> bIt = GyroArr.iterator();
			Iterator<float[]> cIt = RotArr.iterator();
			
			
			// assumes all the lists have the same size
			while(aIt.hasNext() && bIt.hasNext() && cIt.hasNext())
			{
				rot =  RotArr.get(i);
				gyro = GyroArr.get(i);
				acc = AccelArr.get(i);
				
				db.insertToExerciseSample(name.getText().toString(), i, rot[0], rot[1], rot[2], acc[0], acc[1], acc[2], gyro[0], gyro[1], gyro[2]);
				i++;
			}
			System.out.println(name.getText().toString());
			System.out.println(description.getText().toString());
			db.insertToExercise(name.getText().toString(), description.getText().toString());	
			
		}
		
		 catch (SQLiteException e) {
	        Toast.makeText(this, "insert isn't working", 1).show();
	    }
		
		//Version 2 - Test with different body types - check values
		
		//Version 3 - Take null values away if still feasible
		
		
		//Final Version
		
		
		i=0;
		//closing database
		db.close();
	}
}