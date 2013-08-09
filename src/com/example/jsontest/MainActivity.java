package com.example.jsontest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.jsontest.JSONparser;
import com.example.jsontest.MainActivity;
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ItemsDbAdapter dbHelper;
	//private SimpleCursorAdapter dataAdapter;
	
	ItemListAdapter mItemListAdapter;
	ItemsCursorAdapter mItemCursorAdapter;
	ProgressBar mProgressBar;
	 
	int mCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showToast("v1.00 - Refreshing data");
		mCount = 0;
		
		mProgressBar= (ProgressBar) findViewById( R.id.progressBar);
		mProgressBar.setMax(29);
		 
		dbHelper = new ItemsDbAdapter(this);
		dbHelper.open();
		dbHelper.deleteAllItems();
		//dbHelper.insertSomeData();
		initListView();
		RefreshList();
	}

	public void initListView()
	{	
		 Cursor cursor = dbHelper.fetchAllItems();
		 
		/*  // The desired columns to be bound
		  String[] columns = new String[] {
		    ItemsDbAdapter.KEY_CODE,
		    ItemsDbAdapter.KEY_NAME,
		    ItemsDbAdapter.KEY_PRICE,		    
		  };
		 
		  // the XML defined views which the data will be bound to
		  int[] to = new int[] { 
		    R.id.barcode,
		    R.id.name,
		    R.id.price,
		  };
		 
		  // create the adapter using the cursor pointing to the desired data 
		  //as well as the layout information
		  dataAdapter = new SimpleCursorAdapter(
		    this, R.layout.list_item, 
		    cursor, 
		    columns, 
		    to,
		    0);
		    */
		  
		  mItemCursorAdapter = new ItemsCursorAdapter(this,cursor,true);
		  
		  ListView listView = (ListView) findViewById(R.id.listView1);
		  // Assign adapter to ListView
		  
		  listView.setAdapter(mItemCursorAdapter);
		  this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		  
		  EditText myFilter = (EditText) findViewById(R.id.editFilterText);
		  
		  myFilter.addTextChangedListener(new TextWatcher(){
			  public void afterTextChanged(Editable s) { }
			 
			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }
			  public  void onTextChanged(CharSequence s, int start, int before, int count){
				  mItemCursorAdapter.getFilter().filter(s.toString());
			  }
		  });
		  
		 
		  mItemCursorAdapter.setFilterQueryProvider(new FilterQueryProvider(){
			  public Cursor runQuery(CharSequence constraint) {
				  mItemCursorAdapter.mCursor = dbHelper.fetchItemsByBarcode(constraint.toString()); 
				 return mItemCursorAdapter.mCursor;
			  }
		  });
		 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.action_refresh:
	    		RefreshList();
        	 //	mItemCursorAdapter.notifyDataSetChanged();
	    		return true;
	        case R.id.action_settings:
	        	
	            return true;	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void RefreshList() {
		LongOperation lo = new LongOperation(){
	         @Override
	         protected void onPostExecute(String result) {
	  
	        	 	showToast("Result: "+result);
	        	/* 	if(MainActivity.this.getListAdapter()==null){
	        	 		mItemListAdapter = new ItemListAdapter(MainActivity.this, mItems);
	        	 		MainActivity.this.setListAdapter(mItemListAdapter);
	        	 	}else{
	        	 		mItemListAdapter.UpdateData(mItems);
	        
	        	 	}
	        	 	*/

	        	 	//mItemCursorAdapter.notifyDataSetChanged();
	        	 /*	int count = mItemListAdapter.getCount();
	        	 */
		        	Cursor cursor = dbHelper.fetchAllItems();
		        	
	    	 		mItemCursorAdapter.updateCursor(cursor);
		        	MainActivity.this.setTitle("# items:" + String.valueOf(dbHelper.getCount()) + ","+result );
	         }

	         @Override
	         protected void onPreExecute() {
	         }

	         @Override
	         protected void onProgressUpdate(Integer... values) {	   
	        //	MainActivity.this.mProgressBar= (ProgressBar) findViewById( R.id.progressBar);
	        	MainActivity.this.mProgressBar.setProgress( (Integer)values[0]);
	         }
		};
		lo.execute("");
		
	}

	 private class LongOperation extends AsyncTask<String, Integer, String> {
		 List<HashMap<String, String>> mItems = new ArrayList<HashMap<String, String>>();
		 
		 @Override
         protected String doInBackground(String... params) {

			 	//	if(isOnline()==false)
			 		//	return "no internet";
			 		
			 long startTime;
			 long endTime;
			 long totalParse = 0;
			 long totalDl = 0;
			 String parseTime;
			 String dlTime ="";
			 
       		JSONparser jp = new JSONparser();
    		JSONObject jobj = null;
    	
    		//jp.getJSONfileCompressedFromURL("http://www.mina-viner.se/json-samples/sample300.php");
    		mCount = 0;
    		while(mCount<30) {            		
            		 startTime = SystemClock.elapsedRealtime();  
            		jobj = jp.jObjDownloadAndDecompress("http://www.mina-viner.se/json-samples/sample1000.php");
            		//jobj = jp.getJSONfileFromURL("http://www.mina-viner.se/json-samples/sample1000.json");
            		endTime = SystemClock.elapsedRealtime();
            		
            		totalDl += (endTime - startTime);
            		dlTime = " Time to dl: "+ String.valueOf(endTime - startTime) + "ms";
            		
            		JSONArray jarray = null;
            		
            		if(jobj == null)
            			return "jobj == null";
            		
            	 //   Gson gson = new Gson();
            		int nJSONObjects = 0;
            		startTime = SystemClock.elapsedRealtime();
            		try{
            			jarray = jobj.getJSONArray("items");
            			
            			nJSONObjects = jarray.length();
            			MainActivity.this.dbHelper.beginTransaction();
            			try {
	            			for(int i = 0; i<nJSONObjects;i++) {
	            				JSONObject oj = jarray.getJSONObject(i);
	            		        // creating new HashMap
	                            //HashMap<String, String> map = new HashMap<String, String>();
	                            //parse(oj,map);	                     
                            	MainActivity.this.dbHelper.createItem(String.valueOf(mCount*100)+oj.getString("Id"), oj.getString("Name"), String.valueOf(oj.getDouble("PriceOut")));
	                            //	Log.i("parser", ); 
                            	//  gson.toJson(map); //where map is your map object                            
	                           	// mItems.add(map);
	            			}
	            			MainActivity.this.dbHelper.commitTransaction();
	            		} finally {
            				MainActivity.this.dbHelper.endTransaction();
            			}
            			
            		}catch(JSONException je) {
            			je.printStackTrace();
            		}
            		endTime = SystemClock.elapsedRealtime();
            		totalParse += (endTime - startTime);
            		parseTime = " this batch / Total time to parse: "+String.valueOf(endTime - startTime)+" / " +String.valueOf(totalParse) + "ms " +" JSON obj: "+String.valueOf(nJSONObjects);  		
            		Log.e(">>>",dlTime);
            		Log.e(">>>",parseTime);
	        	 
            		publishProgress( new Integer(mCount).intValue());

	        	 		mCount++;
	        	 	}
	        	//Cursor cursor = dbHelper.fetchAllItems();
	
    			dlTime = "Dl: "+ String.valueOf(totalDl) + "ms,";
	        	parseTime = "Parse: "+ String.valueOf(totalParse) + "ms ";

               return dlTime+parseTime;
         }    
	
         public Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException{
        	    Iterator<String> keys = json.keys();
        	    while(keys.hasNext()){
        	        String key = keys.next();
        	        String val = null;
        	        try{
        	             JSONObject value = json.getJSONObject(key);
        	             parse(value,out);
        	        }catch(Exception e){
        	            val = json.getString(key);
        	        }

        	        if(val != null){
        	            out.put(key,val);
        	        }
        	    }
        	    return out;
         }	
	 }
/*	
	function fillDatabase(String stringWithJson){

		SQLiteDatabase databaseInstance = getDatabase();
		MyWrapperObject mwo = new Gson().fromJson(stringWithJson, MyWrapperObject.class);

		InsertHelper helper1 = new InsertHelper(databaseInstance, "firstTableName");
		InsertHelper helper2 = new InsertHelper(databaseInstance, "otherTableName");

		// here bind all column names to it's index
		// showing only example:
		final int SURNAME_COLUMN = helper1.bind("surnameColumnName");
		// etc...

		databaseInstance.beginTransaction();

		try{

		    // walk through array and use both InsertHelper instances
		    // example
		    Iterator<MyType> iterator = mwo.items.iterator();
		    while(iterator.hasNext()){
		        helper1.prepareForInsert();
		        helper1.bind(SURNAME_COLUMN, "surname column value");
		        helper1.execute();

		        helper2.prepareForInsert();
		        helper2.bind(ATNOHER_COLUMN, "another column value");
		        helper2.execute();
		    }
		    // if everything went OK, we will confirm, whole transaction is successful
		    databaseInstance.setTransactionSuccessful();
		} finally {
		    // execute or screw all inserted transactions
		    databaseInstance.endTransaction();
		}

	}	
	*/
         public void showToast(final String toast)
     	{
     	    runOnUiThread(new Runnable() {
     	        public void run()
     	        {
     	            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
     	        }
     	    });
     	}
     	         
}
