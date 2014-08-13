package sg.dhs.pic_che;

import android.app.Activity;
import android.app.ActionBar;
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
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import sg.dhs.pic_che.R;

public class NewPhraseActivity extends Activity {

    private String LOGTAG = "NewPhraseActivity";

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
                    break;
                default:
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
