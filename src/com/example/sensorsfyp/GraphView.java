package com.example.sensorsfyp;

import android.support.v7.app.ActionBarActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GraphView extends ActionBarActivity {
	String name;    
	Bundle extras = getIntent().getExtras();
	private DBmanager db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph_view);
		db = new DBmanager(this);
		db.open();
		
		if (extras != null) {
		    //Here you get the id from the item
		    name= (String)extras.getString("exercise");
		}
		Cursor c = db.getAllExerciseValues(name);
		if (c.moveToFirst()) {

            while (c.isAfterLast() == false) {
             //   String ExName = c.getString();

                c.moveToNext();
            }
        }
		
	
	}

	
}
