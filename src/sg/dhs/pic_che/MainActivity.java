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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import sg.dhs.pic_che.adapters.PicCHEAdapter;
import sg.dhs.pic_che.db.PhraseDataSource;
import sg.dhs.pic_che.model.Category;
import sg.dhs.pic_che.model.Phrase;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ExpandableListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    PhraseDataSource datasource;

    public static String EXTRA_HOKKIEN = "sg.dhs.pic_che.HOKKIEN";
    public static String EXTRA_CANTONESE = "sg.dhs.pic_che.CANTONESE";
    public static String EXTRA_CHINESE = "sg.dhs.pic_che.CHINESE";
    public static String EXTRA_ENGLISH = "sg.dhs.pic_che.ENGLISH";
    public static String EXTRA_ID = "sg.dhs.pic_che.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new PhraseDataSource(getBaseContext());

        datasource.open();

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
        if (id == R.id.action_refresh) {


            if(isNetworkConnected()){

                String url = "http://www.awesome.jerome.yukazunori.com/PICCHE/";
                new ServerPhrases().execute(url);
                new ServerCategory().execute(url);

            }

        }
        if(id == R.id.action_add_phrase) {
            Intent intent = new Intent(getApplicationContext(), NewPhraseActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment of the ListView of phrases
     */
    public static class PlaceholderFragment extends Fragment {

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

            readDB(rootView);

            return rootView;
        }

        private void readDB(View v) {
            final String LOGTAG = "ReadDB";

            List<Phrase> phrases = datasource.findAllPhrases();
            List<Phrase> selfPhrases = datasource.findAllSelfPhrases();
            List<Phrase> tempPhrases;
            final List<Category> categories = datasource.findAllCategories();
            final Map<Category, List<Phrase>> picches;

            picches = new LinkedHashMap<>();

            for(Category category : categories){
                tempPhrases = new ArrayList<>();
                for(Phrase phrase : phrases) {
                    //If category of phrase matches current category, add phrase to temporary
                    if (category.getId() == phrase.getCatId()) {
                        tempPhrases.add(phrase);
                    }
                }
                if(category.getId() == 256){
                    for(Phrase selfPhrase : selfPhrases){
                        tempPhrases.add(selfPhrase);
                    }
                }
                picches.put(category, tempPhrases);
            }

            PicCHEAdapter adapter = new PicCHEAdapter(getActivity(), categories, picches);

            ExpandableListView expListView = (ExpandableListView) v.findViewById(R.id.expListView);
            expListView.setAdapter(adapter);

            //Set what happens when an item is clicked
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                    Log.i(LOGTAG, "Child Clicked");

                    Intent intent = new Intent(getActivity(), PhraseActivity.class);
                    Phrase phrase;
                    phrase = picches.get(categories.get(groupPosition)).get(childPosition);
                    intent.putExtra(EXTRA_HOKKIEN, phrase.getHokkien());
                    intent.putExtra(EXTRA_CANTONESE, phrase.getCantonese());
                    intent.putExtra(EXTRA_CHINESE, phrase.getChinese());
                    intent.putExtra(EXTRA_ENGLISH, phrase.getEnglish());
                    Category category;
                    category = categories.get(groupPosition);
                    if(category.getId() == 256)
                        intent.putExtra(EXTRA_ID, "self_" + Long.toString(phrase.getId()));
                    else
                        intent.putExtra(EXTRA_ID, Long.toString(phrase.getId()));

                    getActivity().startActivity(intent);

                    return true;

                }

            });
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



    //Checks if device is connected to the internet
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
    }

    private class ServerPhrases extends AsyncTask<String, Void, String> {

        String LOGTAG = "ServerPhrases";

        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext(), "Downloading...", Toast.LENGTH_SHORT).show();
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

            //Array to be used to convert JSON into string
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

            //result is the raw JSON data that will be collected by HTTPPost
            String result = null;

            //StringBuilder will help in turning the web data into a string
            StringBuilder sb;

            //The InputStream that will help parse the data
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
                sb.append(reader.readLine()).append("\n"); //Appending response into sb

                String line;
                line = reader.readLine();
                while (line != null) { //Error may occur here. Scrutinize this
                    line = reader.readLine();
                    sb.append(line).append("\n");
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
                File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");
                if(!ImageDirectory.exists()){
                    ImageDirectory.mkdirs();
                }

                File AudioDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Audio");
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
                        int bufferLength;
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
                        int bufferLength;
                        while ( (bufferLength = inputStream.read(buffer)) > 0 )
                        {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
                        }
                        fileOutput.close();
                    }

                    File canAudio = new File(AudioDirectory, i+"_can.mp3");
                    if(!canAudio.exists()){//audio file doesn't exist and must be downloaded
                        canAudio.createNewFile();
                        URL url = new URL(urls[0]+"audio/"+i+"_can.mp3");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        FileOutputStream fileOutput = new FileOutputStream(canAudio);
                        InputStream inputStream = urlConnection.getInputStream();
                        int totalSize = urlConnection.getContentLength();
                        int downloadedSize = 0;
                        byte[] buffer = new byte[1024];
                        int bufferLength;
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
                        int bufferLength;
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
                        int bufferLength;
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
            /**
             * If there is something
             * 	Replace Table Data w/ new data
             */
            //Declaring JSON Array and name string and linear layout to add into activity
            JSONArray jArray;

            datasource = new PhraseDataSource(getBaseContext());
            datasource.open();

            try {
                jArray = new JSONArray(result);
                JSONObject json_data;

                int jArrayLength = jArray.length();

                Log.d(LOGTAG,"Downloaded JSON!");
                datasource.deletePhrases();

                //Takes all the data and puts them in arrays
                for(int i=0;i<jArrayLength;i++){
                    json_data = jArray.getJSONObject(i);
                    Phrase phrase = new Phrase();
                    phrase.setId(Long.parseLong(json_data.getString("_ID")));
                    phrase.setHokkien(json_data.getString("HOK"));
                    phrase.setCantonese(json_data.getString("CAN"));
                    phrase.setChinese(json_data.getString("CHI"));
                    phrase.setEnglish(json_data.getString("ENG"));
                    phrase.setCatId(Integer.parseInt(json_data.getString("CAT_ID")));
                    datasource.createPhrase(phrase);
                    Log.i(LOGTAG,"Inserted phrase into DB");
                }

            } catch (JSONException e) {
                Log.e(LOGTAG, "JSONException: "+e);
            }



        }

    }

    private class ServerCategory extends AsyncTask<String, Void, String>{

        String LOGTAG = "ServerCategories";

        @Override
        protected String doInBackground(String... urls) {
            //Array to be used to convert JSON into string
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

            //result is the raw JSON data that will be collected by HTTPPost
            String result = null;

            //StringBuilder will help in turning the web data into a string
            StringBuilder sb;

            //The InputStream that will help parse the data
            InputStream is = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urls[0]+"category.php");
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
                sb.append(reader.readLine()).append("\n"); //Appending response into sb

                String line;
                line = reader.readLine();
                while (line != null) { //Error may occur here. Scrutinize this
                    line = reader.readLine();
                    sb.append(line).append("\n");
                }

                is.close(); //close the input stream
                result=sb.toString(); //convert result to string

            }
            catch (Exception e){ //Error converting result to String
                Log.e("log_tag", "Error converting result: "+e.toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i(LOGTAG,"Downloaded JSON!");

            //Declaring JSON Array and name string and linear layout to add into activity
            JSONArray jArray;

            datasource = new PhraseDataSource(getBaseContext());
            datasource.open();

            try {
                jArray = new JSONArray(result);
                JSONObject json_data;

                int jArrayLength = jArray.length();

                int DBCount = datasource.getServerCount();

                if(DBCount>0)
                    datasource.deleteCategories();

                //Takes all the data and puts them in arrays
                for(int i=0;i<jArrayLength;i++){
                    json_data = jArray.getJSONObject(i);
                    Category category = new Category();
                    category.setId(Long.parseLong(json_data.getString("CAT_ID")));
                    category.setHokkien(json_data.getString("HOK"));
                    category.setCantonese(json_data.getString("CAN"));
                    category.setChinese(json_data.getString("CHI"));
                    category.setEnglish(json_data.getString("ENG"));
                    datasource.createCategory(category);
                    Log.d(LOGTAG,"Inserted category into DB");
                }

            } catch (JSONException e) {
                Log.e(LOGTAG, "JSONException: "+e);
            }

            Toast.makeText(getBaseContext(), "Downloaded!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        datasource.close();
    }
}
