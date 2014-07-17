package sg.dhs.pic_che.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseOpenHelper extends SQLiteOpenHelper {
	
	private static final String LOGTAG = "PHRASEHELPER";
	
	private static final String DATABASE_NAME = "picchedatabase.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String TABLE_PHRASE = "PICCHEPHRASES";
	public static final String TABLE_CATEGORY = "PICCHECATEGORIES";
	public static final String catID = "CAT_ID";
	public static final String catHokkien = "HOK";
	public static final String catCantonese = "CAN";
	public static final String catChinese = "CHI";
	public static final String catEnglish = "ENG";
	public static final String id = "_ID";
	public static final String category = "CAT_ID";
	public static final String hokkien = "HOK";
	public static final String cantonese = "CAN";
	public static final String chinese = "CHI";
	public static final String english = "ENG";
	
	private static final String CREATE_TABLE = "CREATE TABLE "+
												TABLE_PHRASE+" ("+
												id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
												category+" INTEGER, "+
												hokkien+" VARCHAR(255), "+
												cantonese+" VARCHAR(255), "+
												chinese+" VARCHAR(255), "+
												english+" VARCHAR(255));";
	private static final String CREATE_TABLE2 = "CREATE TABLE "+TABLE_CATEGORY+" ("+
												catID+" INTEGER PRIMARY KEY AUTROINCREMENT, "+
												catHokkien+" VARCHAR(255), "+
												catCantonese+" VARCHAR(255), "+
												catChinese+" VARCHAR(255), "+
												catEnglish+" VARCHAR(255));";
	
	private static final String DROP_TABLE = "DROP TABLE IF EXIST "+TABLE_PHRASE+";";
	private static final String DROP_TABLE2 = "DROP TABLE IF EXIST "+TABLE_CATEGORY+";";
	
	public PhraseOpenHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try {
			db.execSQL(CREATE_TABLE);
			db.execSQL(CREATE_TABLE2);
		} catch (SQLException e) {
			Log.d(LOGTAG, "SQLException OnCreate: "+e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try {
			db.execSQL(DROP_TABLE);
			db.execSQL(DROP_TABLE2);
			onCreate(db);
		} catch (SQLException e) {
			Log.d(LOGTAG, "SQLException OnDrop: "+e);
		}

	}

}
