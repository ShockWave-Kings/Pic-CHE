package sg.dhs.pic_che.db;

import java.util.ArrayList;
import java.util.List;

import sg.dhs.pic_che.model.Phrase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseDataSource {
	
	public static final String LOGTAG="PICCHEDataSource";
	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	
	public static final String[] allColumnsPhrases = {
		PhraseHelper.id,
		PhraseHelper.cid,
		PhraseHelper.hokkien,
		PhraseHelper.cantonese,
		PhraseHelper.chinese,
		PhraseHelper.english
	};
	
	public PhraseDataSource(Context c){
		dbhelper = new PhraseHelper(c);
	}
	
	public void open() {
		Log.i(LOGTAG, "Database opened");
		database = dbhelper.getWritableDatabase();
	}
	
	public void close() {
		Log.i(LOGTAG, "Database closed");
		dbhelper.close();
	}
	
	public Phrase createPhrase(Phrase phrase){
		ContentValues values = new ContentValues();
		values.put(PhraseHelper.hokkien, phrase.getHokkien());
		values.put(PhraseHelper.cantonese, phrase.getCantonese());
		values.put(PhraseHelper.chinese, phrase.getChinese());
		values.put(PhraseHelper.english, phrase.getEnglish());
		long insertid = database.insert(PhraseHelper.TABLE_PHRASE, null, values);
		phrase.setId(insertid);
		Log.i(LOGTAG, "Phrase inserted into DB with ID: "+insertid);
		
		return phrase;
	}
	
	public List<Phrase> findAllPhrases() {
		List<Phrase> phrases = new ArrayList<Phrase>();
		Cursor cursor = database.query(PhraseHelper.TABLE_PHRASE, allColumnsPhrases,
				null, null, null, null, null);
		Log.i(LOGTAG, "Returned "+cursor.getCount()+" rows");
		
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				Phrase phrase = new Phrase();
				phrase.setId(cursor.getLong(cursor.getColumnIndex(PhraseHelper.id)));
				phrase.setHokkien(cursor.getString(cursor.getColumnIndex(PhraseHelper.hokkien)));
				phrase.setCantonese(cursor.getString(cursor.getColumnIndex(PhraseHelper.cantonese)));
				phrase.setChinese(cursor.getString(cursor.getColumnIndex(PhraseHelper.chinese)));
				phrase.setEnglish(cursor.getString(cursor.getColumnIndex(PhraseHelper.english)));
				phrases.add(phrase);
			}
		}
		
		return phrases;
	}
	
	public int getPhraseCount() {
		Cursor cursor = database.query(PhraseHelper.TABLE_PHRASE, allColumnsPhrases,
				null, null, null, null, null);
		return cursor.getCount();
	}
	
}
