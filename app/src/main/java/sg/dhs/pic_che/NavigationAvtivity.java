package sg.dhs.pic_che;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sg.dhs.pic_che.adapters.PhraseAdapter;
import sg.dhs.pic_che.db.PhraseDataSource;
import sg.dhs.pic_che.model.Category;
import sg.dhs.pic_che.model.Phrase;


public class NavigationAvtivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static String EXTRA_HOKKIEN = "sg.dhs.pic_che.HOKKIEN";
    public static String EXTRA_CANTONESE = "sg.dhs.pic_che.CANTONESE";
    public static String EXTRA_CHINESE = "sg.dhs.pic_che.CHINESE";
    public static String EXTRA_ENGLISH = "sg.dhs.pic_che.ENGLISH";
    public static String EXTRA_ID = "sg.dhs.pic_che.ID";

    PhraseDataSource datasource;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_avtivity);

        datasource = new PhraseDataSource(getBaseContext());
        datasource.open();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        datasource = new PhraseDataSource(getBaseContext());
        datasource.open();
        List<Category> categories = datasource.findAllCategories();
        mTitle = categories.get(number-1).getEnglish(); //-1 to account for index starting at 1
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.navigation_avtivity, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
            else {
                Toast.makeText(getBaseContext(), "Not connected to internet!", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        PhraseDataSource dataSource;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation_avtivity, container, false);

            int index = getArguments().getInt(ARG_SECTION_NUMBER)-1;

            dataSource = new PhraseDataSource(getActivity());
            dataSource.open();

            List<Category> categories = dataSource.findAllCategories();
            long catID = categories.get(index).getId();

            final List<Phrase> phrases = dataSource.findPhraseByCategory(catID);
            ListView listView = (ListView) rootView.findViewById(R.id.phraseListView);
            PhraseAdapter adapter = new PhraseAdapter(getActivity(), phrases);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), PhraseActivity.class);
                    Phrase phrase;
                    phrase = phrases.get(position);
                    intent.putExtra(EXTRA_HOKKIEN, phrase.getHokkien());
                    intent.putExtra(EXTRA_CANTONESE, phrase.getCantonese());
                    intent.putExtra(EXTRA_CHINESE, phrase.getChinese());
                    intent.putExtra(EXTRA_ENGLISH, phrase.getEnglish());
                    if(phrase.getCatId() == 256)
                        intent.putExtra(EXTRA_ID, "self_" + Long.toString(phrase.getId()));
                    else
                        intent.putExtra(EXTRA_ID, Long.toString(phrase.getId()));

                    getActivity().startActivity(intent);
                }
            });

            dataSource.close();
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((NavigationAvtivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onPause() {
            super.onPause();
            dataSource.close();
        }

        @Override
        public void onResume() {
            super.onResume();
            dataSource.open();
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
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

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
                Log.e("log_tag", "Error in http connection: " + e.toString());
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
                phraseNum = jArrayLength;

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
                try {
                    File img = new File(ImageDirectory, i + ".png");
                    if (!img.exists()) {//image file doesn't exist and must be downloaded

                        img.createNewFile();
                        URL url = new URL(urls[0] + "img/" + i + ".png");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }

                try {
                    File hokAudio = new File(AudioDirectory, i + "_hok.mp3");
                    if (!hokAudio.exists()) {//hokkien audio file doesn't exist and must be downloaded
                        hokAudio.createNewFile();
                        URL url = new URL(urls[0] + "audio/" + i + "_hok.mp3");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                        Log.i(LOGTAG, "Downloaded Hokkien: " + i);
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }

                try {
                    File canAudio = new File(AudioDirectory, i + "_can.mp3");
                    if (!canAudio.exists()) {//cantonese audio file doesn't exist and must be downloaded
                        canAudio.createNewFile();
                        URL url = new URL(urls[0] + "audio/" + i + "_can.mp3");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                        Log.i(LOGTAG, "Downloaded Cantonese: " + i);
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }

                try {
                    File chiAudio = new File(AudioDirectory, i + "_chi.mp3");
                    if (!chiAudio.exists()) {//chinese audio file doesn't exist and must be downloaded
                        chiAudio.createNewFile();
                        URL url = new URL(urls[0] + "audio/" + i + "_chi.mp3");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                        Log.i(LOGTAG, "Downloaded Chinese: " + i);
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }

                try {
                    File engAudio = new File(AudioDirectory, i + "_eng.mp3");
                    if (!engAudio.exists()) {//English audio file doesn't exist and must be downloaded
                        engAudio.createNewFile();
                        URL url = new URL(urls[0] + "audio/" + i + "_eng.mp3");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                        Log.i(LOGTAG, "Downloaded English: " + i);
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }
            }

            datasource.close();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

        }

    }

    private class ServerCategory extends AsyncTask<String, Void, String>{

        String LOGTAG = "ServerCategories";

        @Override
        protected String doInBackground(String... urls) {
            //Array to be used to convert JSON into string
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

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

            Log.i(LOGTAG,"Downloaded JSON!");

            //Declaring JSON Array and name string and linear layout to add into activity
            JSONArray jArray;

            datasource = new PhraseDataSource(getBaseContext());
            datasource.open();

            List<Category> categories = new ArrayList<Category>();

            //Making directories to store audio and images
            File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");
            if(!ImageDirectory.exists()){
                ImageDirectory.mkdirs();
            }

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
                    categories.add(category);
                    datasource.createCategory(category);
                    Log.d(LOGTAG,"Inserted category into DB");
                }

            } catch (JSONException e) {
                Log.e(LOGTAG, "JSONException: "+e);
            }

            for(Category category : categories){
                try {
                    File img = new File(ImageDirectory, "cat_" + category.getId() + ".png");
                    if (!img.exists()) {//image file doesn't exist and must be downloaded

                        img.createNewFile();
                        URL url = new URL(urls[0] + "img/" + "cat_" + category.getId() + ".png");
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
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                        }
                        fileOutput.close();
                    }
                } catch (IOException e) {
                    Log.e(LOGTAG, "IOException: "+e);
                }
            }

            datasource.close();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), "Downloaded!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        datasource.open();
    }
}
