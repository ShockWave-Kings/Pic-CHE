package sg.dhs.pic_che.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseHelper extends SQLiteOpenHelper {
	
	private static final String LOGTAG = "PHRASEHELPER";
	
	private static final String DATABASE_NAME = "picchedatabase.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "PICCHEPHRASES";
	private static final String TABLE2_NAME = "PICCHECATEGORIES";
	private static final String id = "_ID";
	private static final String cid = "CAT_ID";
	private static final String cName = "NAME";
	private static final String category = "CAT_ID";
	private static final String hokkien = "HOK";
	private static final String cantonese = "CAN";
	private static final String chinese = "CHI";
	private static final String english = "ENG";
	
	private static final String CREATE_TABLE = "CREATE TABLE "+
												TABLE_NAME+" ("+
												id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
												category+" INTEGER, "+
												hokkien+" VARCHAR(255), "+
												cantonese+" VARCHAR(255), "+
												chinese+" VARCHAR(255), "+
												english+" VARCHAR(255));";
	private static final String CREATE_TABLE2 = "CREATE TABLE "+TABLE2_NAME+" ("+
												cid+" INTEGER PRIMARY KEY AUTROINCREMENT, "+
												cName+" INTEGER);";
	
	private static final String DROP_TABLE = "DROP TABLE IF EXIST "+TABLE_NAME+";";
	private static final String DROP_TABLE2 = "DROP TABLE IF EXIST "+TABLE2_NAME+";";
	
	public PhraseHelper(Context c) {
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
