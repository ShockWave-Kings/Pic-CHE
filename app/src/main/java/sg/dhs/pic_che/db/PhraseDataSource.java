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
	 * DB management methods:
	 * All Columns
	 * Create
	 * Find All
	 * Get Count
	 * 
	 * Methods should cover both Phrase table and Category table
	 */
	
	public static final String[] allColumnsPhrases = {
		PhraseOpenHelper.id,
		PhraseOpenHelper.category,
		PhraseOpenHelper.hokkien,
		PhraseOpenHelper.cantonese,
		PhraseOpenHelper.chinese,
		PhraseOpenHelper.english
	};
	
	public Phrase createPhrase(Phrase phrase){
		ContentValues values = new ContentValues();
        values.put(PhraseOpenHelper.id, phrase.getId());
        values.put(PhraseOpenHelper.catID, phrase.getCatId());
		values.put(PhraseOpenHelper.hokkien, phrase.getHokkien());
		values.put(PhraseOpenHelper.cantonese, phrase.getCantonese());
		values.put(PhraseOpenHelper.chinese, phrase.getChinese());
		values.put(PhraseOpenHelper.english, phrase.getEnglish());
		long insertid = database.insert(PhraseOpenHelper.TABLE_PHRASE, null, values);
		phrase.setId(insertid);
		Log.i(LOGTAG, "Phrase inserted into DB with ID: "+insertid+" and CatID: "+phrase.getCatId());
		
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
                phrase.setCatId(cursor.getInt(cursor.getColumnIndex(PhraseOpenHelper.catID)));
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
	
	public void deletePhrases() {
		String delete = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + PhraseOpenHelper.TABLE_PHRASE+"'";
		database.execSQL(delete);
		database.delete(PhraseOpenHelper.TABLE_PHRASE, null, null);
	}
	
	/**
	 * Category methods
	 */
	
	public static final String[] allColumnsCategories = {
		PhraseOpenHelper.catID,
		PhraseOpenHelper.catHokkien,
		PhraseOpenHelper.catCantonese,
		PhraseOpenHelper.catChinese,
		PhraseOpenHelper.catEnglish
	};
	
	public Category createCategory(Category category) {
		ContentValues values = new ContentValues();
        values.put(PhraseOpenHelper.catID, category.getId());
		values.put(PhraseOpenHelper.catHokkien, category.getHokkien());
		values.put(PhraseOpenHelper.catCantonese, category.getCantonese());
		values.put(PhraseOpenHelper.catChinese, category.getChinese());
		values.put(PhraseOpenHelper.catEnglish, category.getEnglish());
		long insertid = database.insert(PhraseOpenHelper.TABLE_CATEGORY, null, values);
		Log.i(LOGTAG, "Category inserted into DB with ID: "+category.getId());
		
		return category;
	}
	
	public List<Category> findAllCategories() {
		List<Category> categories = new ArrayList<Category>();
		Cursor cursor = database.query(PhraseOpenHelper.TABLE_CATEGORY, allColumnsCategories,
				null, null, null, null, null);
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				Category category = new Category();
				category.setId(cursor.getLong(cursor.getColumnIndex(PhraseOpenHelper.catID)));
				category.setHokkien(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.catHokkien)));
				category.setCantonese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.catCantonese)));
				category.setChinese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.catChinese)));
				category.setEnglish(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.catEnglish)));
				categories.add(category);
			}
		}
		
		
		return categories;
	}
	
	public int getServerCount(){
		Cursor cursor = database.query(PhraseOpenHelper.TABLE_CATEGORY, allColumnsCategories,
				null, null, null, null, null);
		return cursor.getCount();
	}
	
	public void deleteCategories(){
		String delete = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + PhraseOpenHelper.TABLE_CATEGORY+"'";
		database.execSQL(delete);
		database.delete(PhraseOpenHelper.TABLE_CATEGORY, null, null);
	}

    /**
     * Self Created Phrases
     */
    public static final String[] allColumnsSelf = {
            PhraseOpenHelper.selfID,
            PhraseOpenHelper.selfCatID,
            PhraseOpenHelper.selfHokkien,
            PhraseOpenHelper.selfCantonese,
            PhraseOpenHelper.selfChinese,
            PhraseOpenHelper.selfEnglish
    };

    public Phrase createSelfPhrase(Phrase phrase){
        ContentValues values = new ContentValues();
        values.put(PhraseOpenHelper.selfCatID, phrase.getCatId());
        values.put(PhraseOpenHelper.selfHokkien, phrase.getHokkien());
        values.put(PhraseOpenHelper.selfCantonese, phrase.getCantonese());
        values.put(PhraseOpenHelper.selfChinese, phrase.getChinese());
        values.put(PhraseOpenHelper.selfEnglish, phrase.getEnglish());
        long insertid = database.insert(PhraseOpenHelper.TABLE_SELF, null, values);
        phrase.setId(insertid);
        Log.i(LOGTAG, "Phrase inserted into DB with ID: "+insertid);

        return phrase;
    }

    public List<Phrase> findAllSelfPhrases() {
        List<Phrase> phrases = new ArrayList<Phrase>();
        Cursor cursor = database.query(PhraseOpenHelper.TABLE_SELF, allColumnsSelf,
                null, null, null, null, null);
        Log.i(LOGTAG, "Returned "+cursor.getCount()+" rows");

        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                Phrase phrase = new Phrase();
                phrase.setId(cursor.getLong(cursor.getColumnIndex(PhraseOpenHelper.selfID)));
                phrase.setCatId(cursor.getInt(cursor.getColumnIndex(PhraseOpenHelper.selfCatID)));
                phrase.setHokkien(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.selfHokkien)));
                phrase.setCantonese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.selfCantonese)));
                phrase.setChinese(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.selfChinese)));
                phrase.setEnglish(cursor.getString(cursor.getColumnIndex(PhraseOpenHelper.selfEnglish)));
                phrases.add(phrase);
            }
        }

        return phrases;
    }
	
}
