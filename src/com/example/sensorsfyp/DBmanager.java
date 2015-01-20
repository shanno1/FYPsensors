package com.example.sensorsfyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBmanager extends SQLiteOpenHelper {

	//Sensor data table 
	public static final String DATABASE_NAME = "Exercise.db";
	public static final String EXER_VAL_TABLE_NAME = "exercise_samples";
	public static final String EXER_VAL_COLUMN_ID = "id";
	public static final String EXER_VAL_COLUMN_NAME = "exercise_name";
	public static final String EXER_VAL_COLUMN_SEQUENCE = "sequence";
	public static final String EXER_VAL_COLUMN_ROT_X = "rotation_vec_x";
	public static final String EXER_VAL_COLUMN_ROT_Y = "rotation_vec_y";
	public static final String EXER_VAL_COLUMN_ROT_Z = "rotation_vec_z";
	public static final String EXER_VAL_COLUMN_LIN_X = "linear_vec_x";
	public static final String EXER_VAL_COLUMN_LIN_Y = "linear_vec_y";
	public static final String EXER_VAL_COLUMN_LIN_Z = "linear_vec_z";
	public static final String EXER_VAL_COLUMN_GYRO_X = "gyroscope_x";
	public static final String EXER_VAL_COLUMN_GYRO_Y = "gyroscope_y";
	public static final String EXER_VAL_COLUMN_GYRO_Z = "gyroscope_z";
	
	//List of exercises table
	public static final String EXER_TABLE_NAME = "exercises";
   	public static final String EXER_COLUMN_ID = "id";
   	public static final String EXER_COLUMN_NAME = "name";
   	public static final String EXER_COLUMN_DESCRIPTION = "description";
   	
   	

   	public DBmanager(Context context)
   	{
      super(context, DATABASE_NAME , null, 1);
   	}
	
	   	@Override
	   public void onCreate(SQLiteDatabase db) {
	      // TODO Auto-generated method stub
	      db.execSQL(
		      "create table " + EXER_VAL_TABLE_NAME +
		      "(id integer primary key,"+
		      EXER_VAL_COLUMN_NAME +" text,"+
		      EXER_VAL_COLUMN_SEQUENCE+" integer,"+
		      EXER_VAL_COLUMN_ROT_X+" real,"+
		      EXER_VAL_COLUMN_ROT_Y+" real,"+
		      EXER_VAL_COLUMN_ROT_Z+" real,"+
		      EXER_VAL_COLUMN_LIN_X+" real,"+
		      EXER_VAL_COLUMN_LIN_Y+" real,"+
		      EXER_VAL_COLUMN_LIN_Z+" real,"+
		      EXER_VAL_COLUMN_GYRO_X+" real,"+
		      EXER_VAL_COLUMN_GYRO_Y+" real,"+
		      EXER_VAL_COLUMN_GYRO_Z+" real,"
		      +")"
	      );
	      
	      db.execSQL(
		      "create table " + EXER_TABLE_NAME +
		      "("+EXER_COLUMN_ID+" integer primary key,"+
		      EXER_COLUMN_NAME +" text,"+
		      EXER_COLUMN_DESCRIPTION+" text,"
		      +")"
	      );
	      
	   }
	
	   @Override
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      // TODO Auto-generated method stub
	      db.execSQL("DROP TABLE IF EXISTS exercises");
	      onCreate(db);
	   }
	
	   public boolean insertToExercise(String name, String description)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	
	      contentValues.put("name", name);
	      contentValues.put("description", description);	
	
	      db.insert(EXER_VAL_TABLE_NAME, null, contentValues);
	      return true;
	   }
	   public boolean insertToExerciseSample(String name, String description, float rot_x,float rot_y, 
			   float rot_z,float lin_x,float lin_y,float lin_z,float gyro_x,float gyro_y,float gyro_z)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      
	      contentValues.put(EXER_VAL_COLUMN_NAME, name);
	      contentValues.put(EXER_VAL_COLUMN_SEQUENCE, description);
	      contentValues.put(EXER_VAL_COLUMN_ROT_X, rot_x);
	      contentValues.put(EXER_VAL_COLUMN_ROT_Y, rot_y);
	      contentValues.put(EXER_VAL_COLUMN_ROT_Z, rot_z);
	      contentValues.put(EXER_VAL_COLUMN_LIN_X, lin_x);
	      contentValues.put(EXER_VAL_COLUMN_LIN_Y, lin_y);
	      contentValues.put(EXER_VAL_COLUMN_LIN_Z, lin_z);
	      contentValues.put(EXER_VAL_COLUMN_GYRO_X, gyro_x);
	      contentValues.put(EXER_VAL_COLUMN_GYRO_Y, gyro_y);
	      contentValues.put(EXER_VAL_COLUMN_GYRO_Z, gyro_z);
	      
	
	      db.insert(EXER_TABLE_NAME, null, contentValues);
	      return true;
	   }
	   public Cursor customQuery(String selectsomething,String fromtable,String wheresomething,int equalsomethingelse){
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select "+selectsomething+" from "+fromtable+" where "+wheresomething+"="+equalsomethingelse, null );
	      return res;
	   }
	   
	   public boolean updateTable(String tablename,Integer id, String name, String phone, String email, String street,String place)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      contentValues.put("name", name);
	      contentValues.put("phone", phone);
	      contentValues.put("email", email);
	      contentValues.put("street", street);
	      contentValues.put("place", place);
	      db.update(tablename, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
	      return true;
	   }
	
	   public Integer deleteTableRow(String table,Integer id)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      return db.delete(table, "id = ? ", new String[] { Integer.toString(id) });
	   }
	
	}