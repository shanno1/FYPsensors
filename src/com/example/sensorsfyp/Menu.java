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
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.Math.*;
import java.text.DecimalFormat;


public class Menu extends ActionBarActivity{

	private Button Add_Exercise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Add_Exercise = (Button)findViewById(R.id.addexercise);
        
        Add_Exercise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               
               Intent intent = new Intent(Menu.this, Add_Exercise.class);
               startActivity(intent);
            }
        });
    }
}


/*public void startsensor(){
	 mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST); 
    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST); 
    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
}
public void stopsensor(){
	 mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)); 
   mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)); 
   mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
}
@SuppressLint("NewApi") public void onSensorChanged(SensorEvent event){
	float[] accelerometervalues;
	float[] orientationVals = new float[3];
	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
       accelerometervalues = addLinearAcc(event.values.clone());
       AccX.setText("X: "+df.format(accelerometervalues[0]));
       AccY.setText("Y: "+df.format(accelerometervalues[1]));
       AccZ.setText("Z: "+df.format(accelerometervalues[2]));
	}
	else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
      	OrX.setText("Yaw: " + df.format(event.values[0]));
      	OrY.setText("Pitch: " + df.format(event.values[1]));
      	OrZ.setText("Roll: " + df.format(event.values[2]));
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

		MagX.setText("Yaw: " + orientationVals[0]);
   	MagY.setText("Pitch: " + orientationVals[1]);
   	MagZ.setText("Roll: " + orientationVals[2]);
   	
	}*/


