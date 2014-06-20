package sg.dhs.pic_che;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhraseActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phrase);

		getActionBar().setDisplayHomeAsUpEnabled(true);

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

		if (id == android.R.id.home){
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

		String LOGTAG = "Phrase Activity";

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
			final String ID = args.getString(MainActivity.EXTRA_ID);

			Button hok = (Button) rootView.findViewById(R.id.phraseHokButton); //NullPointerException: cannot findViewByID
			hok.setText(hokkien);
			hok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					playAudio("hok", ID);
				}
			});
			
			playAll(ID);
			
			Button can = (Button) rootView.findViewById(R.id.phraseCanButton);
			can.setText(cantonese);

			Button chi = (Button) rootView.findViewById(R.id.phraseChiButton);
			chi.setText(chinese);
			chi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					playAudio("chi", ID);
				}
			});

			Button eng = (Button) rootView.findViewById(R.id.phraseEngButton);
			eng.setText(english);
			eng.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					playAudio("eng", ID);
				}
			});

			ImageView img = (ImageView) rootView.findViewById(R.id.phraseImageView);
			int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			img.getLayoutParams().height = width;


			String fileName = ID+".png";
			File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/img/"+fileName);
			Bitmap bmp = BitmapFactory.decodeFile(dir.getAbsolutePath());
			img.setImageBitmap(bmp);

			return rootView;
		}

		void playAudio(String language, String ID){
			try {
				File audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/audio/"+ID+"_"+language+".mp3");
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(audio.getAbsolutePath());
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
					public void onCompletion(MediaPlayer player) {
						Log.d(LOGTAG, "Completed");
						player.stop();
						player.release();
					}
				});
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.e(LOGTAG, "IllegalArgumentException "+e);
			} catch (SecurityException e) {
				Log.e(LOGTAG, "SecurityException "+e);
			} catch (IllegalStateException e) {
				Log.e(LOGTAG, "IllegalStateException "+e);
			} catch (IOException e) {
				Log.e(LOGTAG, "IOException "+e);
			}
		}

		void playAll(String _ID) {
			try {
				final String ID = _ID;
				File audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/audio/"+ID+"_hok.mp3");
				MediaPlayer mp = new MediaPlayer();
				mp.setDataSource(audio.getAbsolutePath());
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
					public void onCompletion(MediaPlayer player) {
						player.stop();
						player.release();

						try {
							File audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/audio/"+ID+"_chi.mp3");
							MediaPlayer mp = new MediaPlayer();
							mp.setDataSource(audio.getAbsolutePath());

							mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
								public void onCompletion(MediaPlayer player) {
									player.stop();
									player.release();

									try {
										File audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/audio/"+ID+"_eng.mp3");
										MediaPlayer mp = new MediaPlayer();
										mp.setDataSource(audio.getAbsolutePath());

										mp.prepare();
										mp.start();
									} catch (IllegalArgumentException e) {
										Log.e(LOGTAG, "IllegalArgumentException "+e);
									} catch (SecurityException e) {
										Log.e(LOGTAG, "SecurityException "+e);
									} catch (IllegalStateException e) {
										Log.e(LOGTAG, "IllegalStateException "+e);
									} catch (IOException e) {
										Log.e(LOGTAG, "IOException "+e);
									}
								}
							});

							mp.prepare();
							mp.start();
						} catch (IllegalArgumentException e) {
							Log.e(LOGTAG, "IllegalArgumentException "+e);
						} catch (SecurityException e) {
							Log.e(LOGTAG, "SecurityException "+e);
						} catch (IllegalStateException e) {
							Log.e(LOGTAG, "IllegalStateException "+e);
						} catch (IOException e) {
							Log.e(LOGTAG, "IOException "+e);
						}
					}
				});
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.e(LOGTAG, "IllegalArgumentException "+e);
			} catch (SecurityException e) {
				Log.e(LOGTAG, "SecurityException "+e);
			} catch (IllegalStateException e) {
				Log.e(LOGTAG, "IllegalStateException "+e);
			} catch (IOException e) {
				Log.e(LOGTAG, "IOException "+e);
			}
		}
	}
}
