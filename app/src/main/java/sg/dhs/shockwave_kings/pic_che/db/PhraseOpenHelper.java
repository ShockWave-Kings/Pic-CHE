package sg.dhs.shockwave_kings.pic_che.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseOpenHelper extends SQLiteOpenHelper {
	
	private static final String LOGTAG = "PHRASEHELPER";
	
	private static final String DATABASE_NAME = "picchedatabase.db";
	private static final int DATABASE_VERSION = 15;
	
	public static final String TABLE_PHRASE = "PICCHEPHRASES";
	public static final String TABLE_CATEGORY = "PICCHECATEGORIES";
    public static final String TABLE_SELF = "PICCHESELF";
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
    public static final String selfID = "_ID";
    public static final String selfCatID = "CAT_ID";
    public static final String selfHokkien = "HOK";
    public static final String selfCantonese = "CAN";
    public static final String selfChinese = "CHI";
    public static final String selfEnglish = "ENG";

	private static final String CREATE_TABLE = "CREATE TABLE "+
												TABLE_PHRASE+" ("+
												id+" INTEGER, "+
												category+" INTEGER, "+
												hokkien+" TEXT, "+
												cantonese+" TEXT, "+
												chinese+" TEXT, "+
												english+" TEXT )";

	private static final String CREATE_TABLE2 = "CREATE TABLE "+
												TABLE_CATEGORY+" ("+
												catID+" INTEGER, "+
												catHokkien+" TEXT, "+
												catCantonese+" TEXT, "+
												catChinese+" TEXT, "+
												catEnglish+" TEXT)";

    private static final String CREATE_TABLE_SELF = "CREATE TABLE "+
                                                    TABLE_SELF+" ("+
                                                    selfID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                    selfCatID+" INTEGER, "+
                                                    selfHokkien+" TEXT, "+
                                                    selfCantonese+" TEXT, "+
                                                    selfChinese+" TEXT, "+
                                                    selfEnglish+" TEXT)";

	private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_PHRASE;
    private static final String DROP_TABLE2 = "DROP TABLE IF EXISTS "+TABLE_CATEGORY;
    private static final String DROP_TABLE_SELF = "DROP TABLE IF EXISTS "+TABLE_SELF;

	public PhraseOpenHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try {
			db.execSQL(CREATE_TABLE2);
			db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE_SELF);
		} catch (SQLException e) {
			Log.d(LOGTAG, "SQLException OnCreate: "+e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try {
			db.execSQL(DROP_TABLE);
			db.execSQL(DROP_TABLE2);
            db.execSQL(DROP_TABLE_SELF);
			onCreate(db);
		} catch (SQLException e) {
			Log.d(LOGTAG, "SQLException OnDrop: "+e);
		}

	}

}
