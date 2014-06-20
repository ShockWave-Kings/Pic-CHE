package sg.dhs.pic_che;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhraseActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phrase);
		
		/*Button hok = (Button) findViewById(R.id.phraseHokButton); //NullPointerException: cannot findViewByID
		hok.setText(hokkien);
		Button can = (Button) findViewById(R.id.phraseCanButton);
		can.setText(cantonese);
		Button chi = (Button) findViewById(R.id.phraseChiButton);
		chi.setText(chinese);
		Button eng = (Button) findViewById(R.id.phraseEngButton);
		eng.setText(english);*/
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phrase, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_phrase,
					container, false);
			
			Bundle args = getActivity().getIntent().getExtras();
			String hokkien = args.getString(MainActivity.EXTRA_HOKKIEN);
			String cantonese = args.getString(MainActivity.EXTRA_CANTONESE);
			String chinese = args.getString(MainActivity.EXTRA_CHINESE);
			String english = args.getString(MainActivity.EXTRA_ENGLISH);
			String pictureID = args.getString(MainActivity.EXTRA_ID);
			
			Button hok = (Button) rootView.findViewById(R.id.phraseHokButton); //NullPointerException: cannot findViewByID
			hok.setText(hokkien);
			Button can = (Button) rootView.findViewById(R.id.phraseCanButton);
			can.setText(cantonese);
			Button chi = (Button) rootView.findViewById(R.id.phraseChiButton);
			chi.setText(chinese);
			Button eng = (Button) rootView.findViewById(R.id.phraseEngButton);
			eng.setText(english);
			
			ImageView img = (ImageView) rootView.findViewById(R.id.phraseImageView);
			int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			img.getLayoutParams().height = width;
			
			
			String fileName = pictureID+".png";
			File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/img/"+fileName);
			Bitmap bmp = BitmapFactory.decodeFile(dir.getAbsolutePath());
			img.setImageBitmap(bmp);
			
			return rootView;
		}
	}

}
