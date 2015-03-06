package com.example.sensorsfyp;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewExerciseList extends ActionBarActivity {
	public DBmanager db;
	ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_exercise_list);
		db = new DBmanager(this);
		list = (ListView)findViewById(R.id.ExerciseList);
		db.open();
		PopulateListview();
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(ViewExerciseList.this, ViewExercise.class);
				i.putExtra("id",position);
				startActivity(i);	
			}
		});
	}

	@SuppressLint("NewApi") 
	public void PopulateListview(){
		Cursor cursor = db.getAllExercises();
		String[] fromFieldNames = new String[] {db.EXER_COLUMN_NAME,db.EXER_COLUMN_DESCRIPTION};
		int[] toViewIDs = new int[] {R.id.SeqText,R.id.textDesc};
		SimpleCursorAdapter myCA;
		myCA = new SimpleCursorAdapter(getBaseContext(),R.layout.activity_itemactivity,cursor,fromFieldNames,toViewIDs,0);
		list.setAdapter(myCA);
	}
	
}
