package sg.dhs.pic_che.db;

import java.util.ArrayList;
import java.util.List;

import sg.dhs.pic_che.model.Category;
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
	
	
	
	public PhraseDataSource(Context c){
		dbhelper = new PhraseOpenHelper(c);
	}
	
	public void open() {
		Log.i(LOGTAG, "Database opened");
		database = dbhelper.getWritableDatabase();
	}
	
	public void close() {
		Log.i(LOGTAG, "Database closed");
		dbhelper.close();
	}
	
	/**
	 * DB management functions:
	 * All Columns
	 * Create
	 * Find All
	 * Get Count
	 * 
	 * Functions should cover both Phrase table and Category table
	 */
	
	public static final String[] allColumnsPhrases = {
		PhraseOpenHelper.id,
		PhraseOpenHelper.cid,
		PhraseOpenHelper.hokkien,
		PhraseOpenHelper.cantonese,
		PhraseOpenHelper.chinese,
		PhraseOpenHelper.english
	};
	
	public Phrase createPhrase(Phrase phrase){
		ContentValues values = new ContentValues();
		values.put(PhraseOpenHelper.hokkien, phrase.getHokkien());
		values.put(PhraseOpenHelper.cantonese, phrase.getCantonese());
		values.put(PhraseOpenHelper.chinese, phrase.getChinese());
		values.put(PhraseOpenHelper.english, phrase.getEnglish());
		long insertid = database.insert(PhraseOpenHelper.TABLE_PHRASE, null, values);
		phrase.setId(insertid);
		Log.i(LOGTAG, "Phrase inserted into DB with ID: "+insertid);
		
		return phrase;
	}
	
	public List<Phrase> findAllPhrases() {
		List<Phrase> phrases = new ArrayList<Phrase>();
		Cursor cursor = database.query(PhraseOpenHelper.TABLE_PHRASE, allColumnsPhrases,
				null, null, null, null, null);
		Log.i(LOGTAG, "Returned "+cursor.getCount()+" rows");
		
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				Phrase phrase = new Phrase();
				phrase.setId(cursor.getLong(cursor.getColumnIndex(PhraseOpenHelper.id)));
				phrase.setHokkien(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.hokkien)));
				phrase.setCantonese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.cantonese)));
				phrase.setChinese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.chinese)));
				phrase.setEnglish(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.english)));
				phrases.add(phrase);
			}
		}
		
		return phrases;
	}
	
	public int getPhraseCount() {
		Cursor cursor = database.query(PhraseOpenHelper.TABLE_PHRASE, allColumnsPhrases,
				null, null, null, null, null);
		return cursor.getCount();
	}
	
	public static final String[] allColumnsCategories = {
		PhraseOpenHelper.cid,
		PhraseOpenHelper.cHOK,
		PhraseOpenHelper.cCAN,
		PhraseOpenHelper.cCHI,
		PhraseOpenHelper.cENG
	};
	
	public Category createCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(PhraseOpenHelper.hokkien, category.getHokkien());
		values.put(PhraseOpenHelper.cantonese, category.getCantonese());
		values.put(PhraseOpenHelper.chinese, category.getChinese());
		values.put(PhraseOpenHelper.english, category.getEnglish());
		long insertid = database.insert(PhraseOpenHelper.TABLE_CATEGORY, null, values);
		category.setId(insertid);
		Log.i(LOGTAG, "Category inserted into DB with ID: "+insertid);
		
		return category;
	}
	
	public List<Category> findAllCategories() {
		List<Category> categories = new ArrayList<Category>();
		Cursor cursor = database.query(PhraseOpenHelper.TABLE_CATEGORY, allColumnsCategories,
				null, null, null, null, null);
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				Category category = new Category();
				category.setId(cursor.getLong(cursor.getColumnIndex(PhraseOpenHelper.cid)));
				category.setHokkien(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.cHOK)));
				category.setCantonese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.cCAN)));
				category.setChinese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.cCHI)));
				category.setEnglish(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.cENG)));
				categories.add(category);
			}
		}
		
		
		return categories;
	}
	
}
