package sg.dhs.shockwave_kings.pic_che;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sg.dhs.shockwave_kings.pic_che.R;
import sg.dhs.shockwave_kings.pic_che.db.PhraseDataSource;
import sg.dhs.shockwave_kings.pic_che.model.Phrase;
import sg.dhs.shockwave_kings.pic_che.technology.ImageLoader;

public class NewPhraseActivity extends Activity {

    private String LOGTAG = "NewPhraseActivity";

    PhraseDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_phrase);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_phrase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
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
            String LOGTAG = "onCreateView";

            View rootView = inflater.inflate(R.layout.fragment_new_phrase, container, false);

            ImageView img = (ImageView) rootView.findViewById(R.id.selfAddPhoto);
            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth(); //Set width to be same as height
            img.getLayoutParams().height = width;

            File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");

            if(!ImageDirectory.exists()){
                ImageDirectory.mkdirs();
            }

            File selfTemp = new File(ImageDirectory, "tempSelf.png");

            if(selfTemp.exists()){
                Bitmap bm = BitmapFactory.decodeFile(selfTemp.getAbsolutePath());
                img.setImageBitmap(bm);
            }

            return rootView;
        }
    }

    public void selfPhoto(View v){
        Log.i(LOGTAG, "Photo clicked");
        File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");
        if(!ImageDirectory.exists()){
            ImageDirectory.mkdirs();
        }

        File selfTemp = new File(ImageDirectory, "tempSelf.png");

        if(!selfTemp.exists()){
            try {
                selfTemp.createNewFile();
            } catch (IOException e) {
                Log.e(LOGTAG,e.toString());
            }
        }
        else{
            selfTemp.delete();
            try {
                selfTemp.createNewFile();
            }
            catch (IOException e) {
                Log.e(LOGTAG, e.toString());
            }
        }
        Uri capturedImageUri = Uri.fromFile(selfTemp);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");
                    File selfTemp = new File(ImageDirectory, "tempSelf.png");
                    selfTemp.delete();
                    break;
                default:
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public void selfAdd(View v){

        Log.i(LOGTAG, "Add Phrase clicked!");

        File ImageDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images");
        if(!ImageDirectory.exists()){
            ImageDirectory.mkdirs();
        }

        File selfTemp = new File(ImageDirectory, "tempSelf.png");

        if(selfTemp.exists()){

            datasource = new PhraseDataSource(getBaseContext());
            datasource.open();

            EditText hokkienText = (EditText) findViewById(R.id.selfHokkien);
            String hokkien = hokkienText.getText().toString();

            EditText cantoneseText = (EditText) findViewById(R.id.selfCantonese);
            String cantonese = cantoneseText.getText().toString();

            EditText chineseText = (EditText) findViewById(R.id.selfChinese);
            String chinese = chineseText.getText().toString();

            EditText englishText = (EditText) findViewById(R.id.selfEnglish);
            String english = englishText.getText().toString();

            Phrase phrase = new Phrase();
            phrase.setCatId(256);
            phrase.setHokkien(hokkien);
            phrase.setCantonese(cantonese);
            phrase.setChinese(chinese);
            phrase.setEnglish(english);

            phrase = datasource.createSelfPhrase(phrase);

            String id = Long.toString(phrase.getId());

            Bitmap decoded = ImageLoader.decodeFile(selfTemp);
            FileOutputStream outputStream = null;

            File selfImg = new File(ImageDirectory, "self_"+id+".png");

            try{
                outputStream = new FileOutputStream(selfImg);
                decoded.compress(Bitmap.CompressFormat.PNG, 8, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e){
                Log.e(LOGTAG, "Compression Error: "+e.toString());
            }

            selfTemp.delete();
            finish();
        }
        else{
            Toast.makeText(getBaseContext(), "No Image Taken!", Toast.LENGTH_SHORT).show();
        }
    }
}
