package com.example.sensorsfyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBmanager {

	//Sensor data table 
	public static final String DATABASE_NAME = "Exercises";
	public static final String EXER_VAL_TABLE_NAME = "exercisesamples";
	public static final String EXER_VAL_COLUMN_ID = "_id";
	public static final String EXER_VAL_COLUMN_NAME = "name";
	public static final String EXER_VAL_COLUMN_SEQUENCE = "sequence";
	public static final String EXER_VAL_COLUMN_ROT_X = "rot_x";
	public static final String EXER_VAL_COLUMN_ROT_Y = "rot_y";
	public static final String EXER_VAL_COLUMN_ROT_Z = "rot_z";
	public static final String EXER_VAL_COLUMN_LIN_X = "lin_x";
	public static final String EXER_VAL_COLUMN_LIN_Y = "lin_y";
	public static final String EXER_VAL_COLUMN_LIN_Z = "lin_z";
	public static final String EXER_VAL_COLUMN_GYRO_X = "gyro_x";
	public static final String EXER_VAL_COLUMN_GYRO_Y = "gyro_y";
	public static final String EXER_VAL_COLUMN_GYRO_Z = "gyro_z";
	public static final int DATABASE_VERSION = 1;
	
	//List of exercises table
	public static final String EXER_TABLE_NAME = "exercisedesc";
   	public static final String EXER_COLUMN_ID = "_id";
   	public static final String EXER_COLUMN_NAME = "name";
   	public static final String EXER_COLUMN_DESCRIPTION = "description";
   	
   	//create tables
   	public static final String DB_EXERCISE_SAMPLES = 
		"CREATE TABLE " + EXER_VAL_TABLE_NAME +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
	    EXER_VAL_COLUMN_NAME +" TEXT NOT NULL, "+
	    EXER_VAL_COLUMN_SEQUENCE+" INTEGER NOT NULL, "+
	    EXER_VAL_COLUMN_ROT_X+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_ROT_Y+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_ROT_Z+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_LIN_X+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_LIN_Y+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_LIN_Z+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_GYRO_X+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_GYRO_Y+" REAL NOT NULL, "+
	    EXER_VAL_COLUMN_GYRO_Z+" REAL NOT NULL);";

	public static final String DB_EXERCISE =
    	"create table " + EXER_TABLE_NAME +" ("
    	+EXER_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
    	EXER_COLUMN_NAME +" TEXT NOT NULL, "+
    	EXER_COLUMN_DESCRIPTION+" TEXT NOT NULL);";
    // other attributes
    private final Context  context; 
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // 
    public DBmanager(Context ctx) 
    {
    	this.context = ctx;
        
    }//End constructor
  
	static class MyDatabaseHelper extends SQLiteOpenHelper {
    	MyDatabaseHelper(Context context){
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    		
    	}//End MyDatabaseHelper
	    
    	@Override
 	    public void onCreate(SQLiteDatabase db) {
	 	      // TODO Auto-generated method stub
    		
	 	      db.execSQL(DB_EXERCISE_SAMPLES);
	 	      db.execSQL(DB_EXERCISE);
	 	      
	    }
	    	     
	       @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    // TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + EXER_VAL_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + EXER_TABLE_NAME);
			onCreate(db);
		}
	
	}//End inner class MyDatabaseHelper
	
	public DBmanager open() {
		DBHelper = new MyDatabaseHelper(context);
    	db = DBHelper.getWritableDatabase();
    	return this;
    }//End SampleDBManager open()
    
    public void close(){
    	DBHelper.close();
    }//End close DBHelper method
    
	    
	   
   public boolean insertToExercise(String name, String description)
   {
      ContentValues contentValues = new ContentValues();

      contentValues.put("name", name);
      contentValues.put("description", description);	

      db.insert(EXER_VAL_TABLE_NAME, null, contentValues);
      db.close();
      return true;
   }
   public boolean insertToExerciseSample(String name, int seq, float rot_x,float rot_y, 
		   float rot_z,float lin_x,float lin_y,float lin_z,float gyro_x,float gyro_y,float gyro_z)
   {
      
      ContentValues contentValues = new ContentValues();
      contentValues.put(EXER_VAL_COLUMN_NAME, name);
      contentValues.put(EXER_VAL_COLUMN_SEQUENCE, seq);
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
      db.close(); 
      return true;
   }
   public Cursor customQuery(String selectsomething,String fromtable,String wheresomething,int equalsomethingelse){
      
      Cursor res =  db.rawQuery( "select "+selectsomething+" from "+fromtable+" where "+wheresomething+"="+equalsomethingelse, null );
      return res;
   }
   
   public boolean updateExerciseSamples(int id, String name, int seq, float rot_x,float rot_y, 
		   float rot_z,float lin_x,float lin_y,float lin_z,float gyro_x,float gyro_y,float gyro_z)
   {
	      
      ContentValues contentValues = new ContentValues();
      contentValues.put(EXER_VAL_COLUMN_NAME, name);
      contentValues.put(EXER_VAL_COLUMN_SEQUENCE, seq);
      contentValues.put(EXER_VAL_COLUMN_ROT_X, rot_x);
      contentValues.put(EXER_VAL_COLUMN_ROT_Y, rot_y);
      contentValues.put(EXER_VAL_COLUMN_ROT_Z, rot_z);
      contentValues.put(EXER_VAL_COLUMN_LIN_X, lin_x);
      contentValues.put(EXER_VAL_COLUMN_LIN_Y, lin_y);
      contentValues.put(EXER_VAL_COLUMN_LIN_Z, lin_z);
      contentValues.put(EXER_VAL_COLUMN_GYRO_X, gyro_x);
      contentValues.put(EXER_VAL_COLUMN_GYRO_Y, gyro_y);
      contentValues.put(EXER_VAL_COLUMN_GYRO_Z, gyro_z);
      db.update(EXER_TABLE_NAME, contentValues, "id = ?", new String[] { Integer.toString(id) } );
      return true;
   }

   public Integer deleteTableRow(String table,Integer id)
   {
     
      return db.delete(table, "id = ? ", new String[] { Integer.toString(id) });
   }



}