package sg.dhs.pic_che;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "picchedatabase";
	private static final String TABLE_NAME = "PICCHEPHRASES";
	private static final String id = "_ID";
	private static final String category = "CAT_ID";
	private static final String hokkien = "HOK";
	private static final String cantonese = "CAN";
	private static final String chinese = "CHI";
	private static final String english = "ENG";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+category+" INTEGER, "+hokkien+" VARCHAR(255), "+cantonese+" VARCHAR(255), "+chinese+" VARCHAR(255), "+english+" VARCHAR(255));";
	private static final String DROP_TABLE = "DROP TABLE "+TABLE_NAME+" IF EXIST";
	private Context context;
	
	public PhraseHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = c;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try {
			db.execSQL(CREATE_TABLE);
		} catch (SQLException e) {
			Log.d("SQLException", "SQLException OnCreate: "+e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		} catch (SQLException e) {
			Log.d("SQLException", "SQLException OnDrop: "+e);
		}

	}

}
