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

import sg.dhs.pic_che.db.PhraseDataSource;
import sg.dhs.pic_che.model.Phrase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add (R.id.container,new PlaceholderFragment()).commit();
		}
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
	 * Fragment of the ListView of phrases
	 */
	public static class PlaceholderFragment extends Fragment {

		List<String> P_ID = new ArrayList<String>();
		List<String> HOK = new ArrayList<String>();
		List<String> CAN = new ArrayList<String>();
		List<String> CHI = new ArrayList<String>();
		List<String> ENG = new ArrayList<String>();
		List<Phrase> PHRASES = new ArrayList<Phrase>();

		PhraseDataSource datasource;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			datasource = new PhraseDataSource(getActivity());

			datasource.open();

			String url = "http://www.awesome.jerome.yukazunori.com/PICCHE/";
			new ServerPhrases().execute(url);

			return rootView;
		}

		private class ServerPhrases extends AsyncTask<String, Void, String> {

			String LOGTAG = "ServerPhrases";

			@Override
			protected void onPreExecute() {
				Toast.makeText(getActivity(), "Downloading Phrases", Toast.LENGTH_SHORT).show();
			}

			@Override
			protected String doInBackground(String... urls) {
				/**
				 * Downloads JSON data into String from server 
				 * via a HTTPPost connection
				 * Returns String for PostExecute to process
				 * 
				 * FYI, urls[0] should be the url to the server that contains imgs in img folder,
				 * audio files in audio folder, and php scripts phrases.php that contains phrases in JSON format
				 * 
				 * Should only use HTTPPOST if wifi connection to internet exists
				 * else, just return null
				 * 
				 * After downloading JSON data, checks how many pictures and audio needs to be downloaded
				 * and downloads them
				 */

				if(isNetworkConnected()){

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
						HttpPost httppost = new HttpPost(urls[0]+"phrase.php");
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
					 * 3. Download missing phrases pictures
					 * 4. Download missing phrases audio files
					 */

					int phraseNum = 0;

					try {
						JSONArray jArr = new JSONArray(result);
						phraseNum = jArr.length(); //No. of phrases

					} catch (JSONException e) {
						Log.e(LOGTAG, "JSONException in Background Thread:"+e);
					}

					try {
						//Making directories to store audio and images
						File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/img");
						if(!ImageDirectory.exists()){
							ImageDirectory.mkdirs();
						}

						File AudioDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/audio");
						if(!AudioDirectory.exists()){
							AudioDirectory.mkdirs();
						}

						for(int i=1; i<=phraseNum; i++){
							File img = new File(ImageDirectory, i+".png");
							if(!img.exists()){//image file doesn't exist and must be downloaded

								img.createNewFile();
								URL url = new URL(urls[0]+"img/"+i+".png");
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

							File hokAudio = new File(AudioDirectory, i+"_hok.mp3");
							if(!hokAudio.exists()){//image file doesn't exist and must be downloaded
								hokAudio.createNewFile();
								URL url = new URL(urls[0]+"audio/"+i+"_hok.mp3");
								HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
								urlConnection.setRequestMethod("GET");
								urlConnection.setDoOutput(true);                   
								urlConnection.connect();
								FileOutputStream fileOutput = new FileOutputStream(hokAudio);
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

							File chiAudio = new File(AudioDirectory, i+"_chi.mp3");
							if(!chiAudio.exists()){//image file doesn't exist and must be downloaded
								chiAudio.createNewFile();
								URL url = new URL(urls[0]+"audio/"+i+"_chi.mp3");
								HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
								urlConnection.setRequestMethod("GET");
								urlConnection.setDoOutput(true);                   
								urlConnection.connect();
								FileOutputStream fileOutput = new FileOutputStream(chiAudio);
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

							File engAudio = new File(AudioDirectory, i+"_eng.mp3");
							if(!engAudio.exists()){//image file doesn't exist and must be downloaded
								engAudio.createNewFile();
								URL url = new URL(urls[0]+"audio/"+i+"_eng.mp3");
								HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
								urlConnection.setRequestMethod("GET");
								urlConnection.setDoOutput(true);                   
								urlConnection.connect();
								FileOutputStream fileOutput = new FileOutputStream(engAudio);
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
				else 
					return null;
			}

			@Override
			protected void onPostExecute(String result) {
				/**
				 * If connected to internet:
				 * 	Check local DB count with server DB count to check if updated
				 * 		If not updated, update
				 * 		If updated, leave be
				 * 
				 * Get results from Local DB
				 */
				if(result!=null){
					//Declaring JSON Array and name string and linear layout to add into activity
					JSONArray jArray = null;

					datasource = new PhraseDataSource(getActivity());
					datasource.open();

					try {
						jArray = new JSONArray(result);
						JSONObject json_data = null;

						int jArrayLength = jArray.length();
						
						Log.d(LOGTAG,"Downloaded JSON!");
						
						int localDBCount = datasource.getPhraseCount();
						if(localDBCount<jArrayLength){
							
							//Takes all the data and puts them in arrays
							for(int i=localDBCount;i<jArrayLength;i++){
								json_data = jArray.getJSONObject(i);
								Phrase phrase = new Phrase();
								phrase.setHokkien(json_data.getString("HOK"));
								phrase.setCantonese(json_data.getString("CAN"));
								phrase.setChinese(json_data.getString("CHI"));
								phrase.setEnglish(json_data.getString("ENG"));
								datasource.createPhrase(phrase);
								Log.d(LOGTAG,"Inserted phrase into DB");
							}

						}
					} catch (JSONException e) {
						Log.e(LOGTAG, "JSONException: "+e);
					}

				}
				else {
					Toast.makeText(getActivity(), "Not connected to internet!", Toast.LENGTH_LONG).show();
					Log.i("Data", "Connection status: "+isNetworkConnected());
				}
				
				List<Phrase> phrases = datasource.findAllPhrases();
				int localDBCount = datasource.getPhraseCount();
				
				for(int i=0; i<localDBCount; i++) {
					P_ID.add(Long.toString(phrases.get(i).getId()));
					HOK.add(phrases.get(i).getHokkien());
					CAN.add(phrases.get(i).getCantonese());
					CHI.add(phrases.get(i).getChinese());
					ENG.add(phrases.get(i).getEnglish());
				}
				
				//make arrays of phrases
				String[] hokkien = new String[localDBCount];
				HOK.toArray(hokkien);
				String[] cantonese = new String[localDBCount];
				CAN.toArray(cantonese);
				String[] chinese = new String[localDBCount];
				CHI.toArray(chinese);
				String[] english = new String[localDBCount];
				ENG.toArray(english);
				
				PhraseAdapter adapter = new PhraseAdapter(getActivity(), hokkien, cantonese, chinese, english);

				ListView listView = (ListView) getView().findViewById(R.id.listView);
				listView.setAdapter(adapter);
				
				//Define what happens when an item is clicked
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id){

						Intent intent = new Intent(getActivity(), PhraseActivity.class);
						intent.putExtra(EXTRA_HOKKIEN, HOK.get(position));
						intent.putExtra(EXTRA_CANTONESE, CAN.get(position));
						intent.putExtra(EXTRA_CHINESE, CHI.get(position));
						intent.putExtra(EXTRA_ENGLISH, ENG.get(position));
						intent.putExtra(EXTRA_ID, P_ID.get(position));
						getActivity().startActivity(intent);
					}
				});

			}

		}
		
		private class ServerCategories extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... urls) {
				if(isNetworkConnected()){
					return null;
				}
				else
					return null;
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

		private boolean isNetworkConnected() { //Checks if device is connected to the internet
			ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			return (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
		}

		@Override
		public void onPause() {
			super.onPause();
			datasource.close();
		}

		@Override
		public void onResume() {
			super.onResume();
			datasource.open();
		}
	}



}
