package com.example.sensorsfyp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ViewExercise extends ActionBarActivity {
	int pos;    
	private DBmanager db;
	TextView nameView,descView;
	Button Graph;
	String name,desc;
	//---------------------------------------
	//nickf's iterative search algorithm
	//Research it and see if any help
	//
	//Value distance algorithm
	//
	//
	//---------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_exercise);
		db = new DBmanager(this);
		db.open();
		
		nameView = (TextView)findViewById(R.id.exercisename);
		descView = (TextView)findViewById(R.id.exercisedescription);
		Graph = (Button)findViewById(R.id.btnGraph);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		  pos = extras.getInt("id");
		//Here you use the id to get the object from your database or whatever
		Cursor c = db.getExercise(pos+1);
		for(c.moveToFirst();!c.isAfterLast(); c.moveToNext()){
			name = c.getString(c.getColumnIndex("name"));
			desc = c.getString(c.getColumnIndex("description"));
		}
		
		nameView.setText(name);
		descView.setText(desc);
		Graph.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent(ViewExercise.this, SensorValueListView.class);
				i.putExtra("name",name);
				startActivity(i);
			}
		});
		
		//THE FOLLOWING SECTION DEALS WITH THE DEMOING OF THE EXERCISES
		//-------------------------------------------------------------
		
		
		
	}


}
