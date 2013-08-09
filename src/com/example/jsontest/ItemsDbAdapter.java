package com.example.jsontest;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;

import android.widget.TextView;


public class ItemsDbAdapter  {

	 public static final String KEY_ROWID = "_id";
	 public static final String KEY_CODE = "barcode";
	 public static final String KEY_NAME = "name";
	 public static final String KEY_PRICE = "price";

	 private static final String DATABASE_NAME = "Products";
	 private static final String SQLITE_TABLE = "Items";
	 private static final int DATABASE_VERSION = 1;

	 private static final String TAG = "ItemDbAdapter";
	 private DatabaseHelper mDbHelper;
	 private SQLiteDatabase mDb;
	 private final Context mCtx;
	 
	 private int mCount;

	 
	 private static final String _DATABASE_CREATE =
			  "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
			  KEY_ROWID + " integer PRIMARY KEY autoincrement," +
			  KEY_CODE + "," +
			  KEY_NAME + "," +
			  KEY_PRICE + "," +
			  " UNIQUE (" + KEY_CODE +"));";
	 private static final String DATABASE_CREATE =
			  "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
			  KEY_ROWID + " integer PRIMARY KEY autoincrement," +
			  KEY_CODE + "," +
			  KEY_NAME + "," +
			  KEY_PRICE + "," +
			  " UNIQUE (" + KEY_ROWID +"));";

	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		 
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);		
		}
		
		@Override		
		public void onCreate(SQLiteDatabase db) {			
			Log.w(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		   Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				     + newVersion + ", which will destroy all old data");
		   db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
		   onCreate(db);
		}

	}
	public ItemsDbAdapter(Context ctx) {
		this.mCtx = ctx;		
	}
	
	public ItemsDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;		
	}
	public void close() {
		if(mDbHelper != null) {
			mDbHelper.close();
		}		
	}
	public long createItem(String code, String name, String price) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CODE, code);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PRICE, price);
		long catID = (int) mDb.insertWithOnConflict(SQLITE_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
		//catID = mDb.insert(SQLITE_TABLE, null, initialValues);
		return catID; 
	}
	
	public boolean deleteAllItems() {
		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		Log.w(TAG,"Deleted items #" + Integer.toString(doneDelete));
		return  doneDelete > 0;				
	}
	 
	public Cursor fetchItemsByBarcode(String inputText) throws SQLException {
		  Log.w(TAG, inputText);
		  Cursor mCursor = null;
		  if (inputText == null  ||  inputText.length () == 0)  {
		   mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
		     KEY_CODE, KEY_NAME, KEY_PRICE}, 
		     null, null, null, null, null);
		 
		  }
		  else {
		   mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
		     KEY_CODE, KEY_NAME, KEY_PRICE}, 
		     KEY_CODE + " like '%" + inputText + "%'", null,
		     null, null, null, null);
		  }
		  if (mCursor != null) {
		   mCursor.moveToFirst();
		  }
		  return mCursor;		 
	}
	public int getCount()
	{
		String query = "SELECT COUNT(*) FROM "+SQLITE_TABLE;
		Cursor c = mDb.rawQuery(query,null);
		c.moveToFirst();
		mCount = c.getInt(0);
		return mCount;
	}
	public Cursor fetchAllItems() {
			
		Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID, KEY_CODE, KEY_NAME, KEY_PRICE}, null, null ,null, null, null,"69, 10");
		
		if (mCursor != null) {
			mCursor.moveToFirst();
			mCount = mCursor.getCount();
		}
		
		return mCursor;		
	}
	
	public void beginTransaction(){
		mDb.beginTransaction();
	}
	public void commitTransaction() {
		mDb.setTransactionSuccessful();// marks a commit	
	}
	public void endTransaction() {
		mDb.endTransaction();
	}
	
	//Benchmark
	//31491 ms (1000 items) = no transactions
	//450 ms (1000 times) = with transactions.	
	public void insertSomeData() {
		Random r = new Random(SystemClock.elapsedRealtime());
  		long startTime = SystemClock.elapsedRealtime();            		

		mDb.beginTransaction();
		try{
			for(int i=0;i<1000;i++){
				String code = "C" + i;
				String name = "name [" + i + "]";
				String price = (r.nextFloat()*100)+" Kr";
				createItem(code,name,price);
			}
			mDb.setTransactionSuccessful();// marks a commit
		}finally {
			mDb.endTransaction();
		}
		long endTime = SystemClock.elapsedRealtime();
				
		String msg = "Time to insert data to db: "+ String.valueOf(endTime - startTime) + "ms";
		Log.i(TAG,msg);
	}
}
