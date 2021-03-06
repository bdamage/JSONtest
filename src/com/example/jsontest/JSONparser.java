package com.example.jsontest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.BufferOverflowException;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import android.util.Base64;
import android.util.Log;

public class JSONparser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	
	public JSONparser(){
		
	}
	
	String downloadAndDecompress(String sUrl) 
	{
		StringBuilder string = new StringBuilder();
		try {
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
				InputStream stream = connection.getInputStream();
				stream = new GZIPInputStream(stream);
				
				byte[] data = new byte[16024];
				int bytesRead;
				while ((bytesRead = stream.read(data)) != -1) {
				        string.append(new String(data, 0, bytesRead));
				}
							
			} catch (BufferOverflowException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("JSON",e.toString());				
			return null;
			}
		 return string.toString();
	}
	
	public JSONObject jObjDownloadAndDecompress(String sUrl) 
	{
		StringBuilder string = new StringBuilder();
		try {
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
				InputStream stream = connection.getInputStream();
				stream = new GZIPInputStream(stream);
				
				byte[] data = new byte[16024];
				int bytesRead;
				while ((bytesRead = stream.read(data)) != -1) {
				        string.append(new String(data, 0, bytesRead));
				}
				
			} catch (BufferOverflowException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("JSON",e.toString());				
			return null;
			}
		   // try parse the string to a JSON object
        try {
            jObj = new JSONObject(string.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", ">> Error parsing data " + e.toString());
        }
 
		 return jObj;
	}

	
	public JSONObject getJSONfileFromURL(String url)
	{
		   try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(url);
	 
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            	           
	            json = EntityUtils.toString(httpEntity);	            
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		   
		   // try parse the string to a JSON object
	        try {
	            jObj = new JSONObject(json);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", ">> Error parsing data " + e.toString());
	        }
	 	            
		return jObj;
		
	}
	private static JSONObject getJsonObjectFromMap(Map<String,String> params) throws JSONException {

	    //all the passed parameters from the post request
	    //iterator used to loop through all the parameters
	    //passed in the post request
	    Iterator iter = params.entrySet().iterator();

	    //Stores JSON
	    JSONObject holder = new JSONObject();

	    //using the earlier example your first entry would get email
	    //and the inner while would get the value which would be 'foo@bar.com' 
	    //{ fan: { email : 'foo@bar.com' } }

	    //While there is another entry
	    while (iter.hasNext()) {
	        //gets an entry in the params
	        Map.Entry pairs = (Map.Entry)iter.next();

	        //creates a key for Map
	        String key = (String)pairs.getKey();

	        //Create a new map
	        Map m = (Map)pairs.getValue();   

	        //object for storing Json
	        JSONObject data = new JSONObject();

	        //gets the value
	        Iterator iter2 = m.entrySet().iterator();
	        while (iter2.hasNext())  {
	            Map.Entry pairs2 = (Map.Entry)iter2.next();
	            data.put((String)pairs2.getKey(), (String)pairs2.getValue());
	        }

	        //puts email and 'foo@bar.com'  together in map
	        holder.put(key, data);
	    }
	    return holder;
	}
	
	public static HttpResponse makeRequest(String path, Map<String,String> params) throws Exception 
	{
	    //instantiates httpclient to make request
	    DefaultHttpClient httpclient = new DefaultHttpClient();

	    //url with the post data
	    HttpPost httpost = new HttpPost(path);

	    //convert parameters into JSON object
	    JSONObject holder = getJsonObjectFromMap(params);

	    //passes the results to a string builder/entity
	    StringEntity se = new StringEntity(holder.toString());

	    //sets the post request as the resulting string
	    httpost.setEntity(se);
	    //sets a request header so the page receving the request
	    //will know what to do with it
	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");

	    //Handles what is returned from the page 
	    ResponseHandler responseHandler = new BasicResponseHandler();
	    return httpclient.execute(httpost, responseHandler);
	}
}
