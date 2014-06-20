package sg.dhs.pic_che;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	public static String EXTRA_HOKKIEN = "sg.dhs.pic_che.HOKKIEN";
	public static String EXTRA_CANTONESE = "sg.dhs.pic_che.CANTONESE";
	public static String EXTRA_CHINESE = "sg.dhs.pic_che.CHINESE";
	public static String EXTRA_ENGLISH = "sg.dhs.pic_che.ENGLISH";
	public static String EXTRA_ID = "sg.dhs.pic_che.ID";
	protected List<String> P_ID = new ArrayList<String>();
	protected List<String> CAT = new ArrayList<String>();
	protected List<String> HOK = new ArrayList<String>();
	protected List<String> CAN = new ArrayList<String>();
	protected List<String> CHI = new ArrayList<String>();
	protected List<String> ENG = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		new ServerPhrases().execute("http://www.awesome.jerome.yukazunori.com/PICCHE/phrase.php");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	
	private class ServerPhrases extends AsyncTask<String, Void, String> {
		
		String LOGTAG = "ServerPhrases";
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(MainActivity.this, "Downloading Phrases", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		protected String doInBackground(String... urls) {
			/**
			 * Downloads JSON data into String from server 
			 * via a HTTPPost connection
			 * Returns String for PostExecute to process
			 * 
			 * FYI, urls[0] should be the url to the server with the phrases
			 * in JSON form
			 */
			
			//Array to be used to convert JSON into string
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			//result is the raw JSON data that will be collected by HTTPPost
			String result = null;
			
			//StringBuilder will help in turning the webdata into a string
			StringBuilder sb = null;

			//The InputStream that wil help parse the data
			InputStream is = null;
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urls[0]);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			    HttpResponse response = httpclient.execute(httppost);
			    HttpEntity entity = response.getEntity();
			    is = entity.getContent();
			}
			catch (Exception e){ //Error in connecting to HTTP
		         Log.e("log_tag", "Error in http connection: "+e.toString());
			}
			
			//Converting response to string
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				sb = new StringBuilder(); //Declare a new StringBuilder
			    sb.append(reader.readLine() + "\n"); //Appending response into sb
			    
			    String line="0";
			    line = reader.readLine();
			    while (line != null) { //Error may occur here. Scrutinize this
			    	line = reader.readLine();
			    	sb.append(line + "\n");
			    }
			    
			    is.close(); //close the input stream
			    result=sb.toString(); //convert result to string
				
			}
			catch (Exception e){ //Error converting result to String
	            Log.e("log_tag", "Error converting result: "+e.toString());
			}
			
			/**
			 * 1. Get number of phrases
			 * 2. Check how many no. of phrases is there locally
			 * 3. Download missing phrases
			 */
			
			int phraseNum = 0;
			
			try {
				JSONArray jArr = new JSONArray(result);
				phraseNum = jArr.length(); //No. of phrases
				
			} catch (JSONException e) {
				Log.e(LOGTAG, "JSONException in Background Thread:"+e);
			}
			
			try {
				File oldDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHEImg");
				if(oldDir.isDirectory()){
					String[] children = oldDir.list();
					for (int i = 0; i < children.length; i++) {
			            new File(oldDir, children[i]).delete();
			        }
					oldDir.delete();
				}
				
				File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/img");
				if(!ImageDirectory.exists()){
					ImageDirectory.mkdirs();
				}
				
				for(int i=1; i<=phraseNum; i++){
					File img = new File(ImageDirectory, i+".png");
					if(!img.exists()){//image file doesn't exist and must be downloaded
						
						img.createNewFile();
						URL url = new URL("http://www.awesome.jerome.yukazunori.com/PICCHE/img/"+i+".png");
						HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
						urlConnection.setRequestMethod("GET");
						urlConnection.setDoOutput(true);                   
						urlConnection.connect();
						FileOutputStream fileOutput = new FileOutputStream(img);
						InputStream inputStream = urlConnection.getInputStream();
						int totalSize = urlConnection.getContentLength();
						int downloadedSize = 0;   
						byte[] buffer = new byte[1024];
						int bufferLength = 0;
						while ( (bufferLength = inputStream.read(buffer)) > 0 ) 
						{                 
							fileOutput.write(buffer, 0, bufferLength);                  
							downloadedSize += bufferLength;                 
							Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
						}             
						fileOutput.close();
					}
				}
				
			} catch (MalformedURLException e) {
				Log.e(LOGTAG, "MalformedURLException: "+e);
			} catch (ProtocolException e) {
				Log.e(LOGTAG, "ProtocolException: "+e);
			} catch (IOException e) {
				Log.e(LOGTAG, "IOException: "+e);
			}
			
			return result;
	    }
		
		@Override
		protected void onPostExecute(String result) {
			//Declaring JSON Array and name string and linear layout to add into activity
			JSONArray jArray = null;
			
			try {
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				
				int jArrayLength = jArray.length();
				//Takes all the data and puts them in arrays
				for(int i=0;i<jArrayLength;i++){
					json_data = jArray.getJSONObject(i);
					P_ID.add(json_data.getString("_ID"));
					CAT.add(json_data.getString("NAME"));
					HOK.add(json_data.getString("HOK"));
					CAN.add(json_data.getString("CAN"));
					CHI.add(json_data.getString("CHI"));
					ENG.add(json_data.getString("ENG"));
				}
				
				//make arrays of phrases
				int phraseLength = HOK.size();
				String[] hokkien = new String[phraseLength];
				HOK.toArray(hokkien);
				String[] cantonese = new String[phraseLength];
				CAN.toArray(cantonese);
				String[] chinese = new String[phraseLength];
				CHI.toArray(chinese);
				String[] english = new String[phraseLength];
				ENG.toArray(english);
				
				PhraseAdapter adapter = new PhraseAdapter(getBaseContext(), hokkien, cantonese, chinese, english);
				
				ListView listView = (ListView) findViewById(R.id.listView);
				listView.setAdapter(adapter);
			} catch (JSONException e) {
				Log.e(LOGTAG, "JSONException: "+e);
			}
			
			ListView listView = (ListView) findViewById(R.id.listView);
			
			//Define what happens when an item is clicked
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					
					Intent intent = new Intent(MainActivity.this, PhraseActivity.class);
					intent.putExtra(EXTRA_HOKKIEN, HOK.get(position));
					intent.putExtra(EXTRA_CANTONESE, CAN.get(position));
					intent.putExtra(EXTRA_CHINESE, CHI.get(position));
					intent.putExtra(EXTRA_ENGLISH, ENG.get(position));
					intent.putExtra(EXTRA_ID, P_ID.get(position));
					MainActivity.this.startActivity(intent);
				}
			});
			
			
		}

	}
	
	class PhraseAdapter extends ArrayAdapter<String>{
	    
		Context context;
		String[] hokkienArray;
		String[] cantoneseArray;
		String[] chineseArray;
		String[] englishArray;
		
	    PhraseAdapter(Context c, String[] hokkien, String[] cantonese, String[] chinese, String[] english){
	    	super(c, R.layout.list_row, R.id.hokkien, hokkien);
	    	this.context = c;
	    	this.hokkienArray = hokkien;
	    	this.cantoneseArray = cantonese;
	    	this.chineseArray = chinese;
	    	this.englishArray = english;
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View row = convertView;
	    	RowHolder holder = null;
	    	
	    	if(row == null){ //1st time
		    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	row = inflater.inflate(R.layout.list_row, parent, false);
		    	holder = new RowHolder(row);
		    	row.setTag(holder);
	    	}
	    	else{
	    		holder = (RowHolder) row.getTag();
	    	}
	    	
	    	String fileName = (position+1)+".png";
	    	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/img/"+fileName);
	    	Bitmap bmp = BitmapFactory.decodeFile(dir.getAbsolutePath());
	    	holder.img.setImageBitmap(bmp);
	    	holder.hokkien.setText(hokkienArray[position]);
	    	holder.cantonese.setText(cantoneseArray[position]);
	    	holder.chinese.setText(chineseArray[position]);
	    	holder.english.setText(englishArray[position]);
	    	
	    	return row;
	    }
	    
	    class RowHolder {
	    	
	    	ImageView img;
	    	TextView hokkien;
	    	TextView cantonese;
	    	TextView chinese;
	    	TextView english;
	    	
	    	RowHolder(View v){
	    		
	    		img = (ImageView) v.findViewById(R.id.listImage);
	    		hokkien = (TextView) v.findViewById(R.id.hokkien);
	    		cantonese = (TextView) v.findViewById(R.id.cantonese);
	    		chinese = (TextView) v.findViewById(R.id.chinese);
	    		english = (TextView) v.findViewById(R.id.english);
	    	}
	    }
	}

}