package com.example.sensorsfyp;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SensorValueListView extends ActionBarActivity {
	String getName;    
	private DBmanager db;
	private ListView list;
	//private ArrayList<float[]> GyroArr = new ArrayList<float[]>();
	private ArrayList<float[]> RotArr = new ArrayList<float[]>();
	private ArrayList<float[]> AccelArr = new ArrayList<float[]>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensorlist_view);
		db = new DBmanager(this);
		db.open();
		Bundle extras = getIntent().getExtras();
		list = (ListView)findViewById(R.id.sensorvaluelist);
		if (extras != null) {
		    //Here you get the id from the item
		    getName= (String)extras.getString("name");
		}
		PopulateListview();
	}
	
	@SuppressLint("NewApi") 
	public void PopulateListview(){
		Cursor cursor = db.getAllExerciseValues(getName);
		String[] fromFieldNames = new String[] {db.EXER_VAL_COLUMN_SEQUENCE,
				db.EXER_VAL_COLUMN_ROT_X,db.EXER_VAL_COLUMN_ROT_Y,db.EXER_VAL_COLUMN_ROT_Z,
				db.EXER_VAL_COLUMN_LIN_X,db.EXER_VAL_COLUMN_LIN_Y,db.EXER_VAL_COLUMN_LIN_Z};
		
		int[] toViewIDs = new int[] {R.id.SeqText,
				R.id.RotXText,R.id.RotYText,R.id.RotZText,
				R.id.AccXText,R.id.AccYText,R.id.AccZText};
		SimpleCursorAdapter myCA;
		myCA = new SimpleCursorAdapter(getBaseContext(),R.layout.activity_sensorvalueitemview,cursor,fromFieldNames,toViewIDs,0);
		list.setAdapter(myCA);
	}
	
}
