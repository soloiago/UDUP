package com.iago.undiaunapalabra.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iago.undiaunapalabra.rssreader.RssReader;
import com.iago.undiaunapalabra.rssreader.Word;

public class DbHandler {
	public static final String tag = "UDUP";

	private static final String DATABASE_NAME = "UNDIAUNAPALABRABD";
	public static final String DATABASE_MAIN_TABLE = "UNDIAUNAPALABRATABLE";
	private static final String DATABASE_GAME_TABLE = "UNDIAUNAPALABRAGAMETABLE";
	private static final int DATABASE_VERSION = 2;

	public static final String KEY_WORD = "word";
	public static final String KEY_DEFINITION = "definition";
	public static final String KEY_DATE_MS = "date_ms";
	public static final String KEY_DATE_STRING = "date_string";
	public static final String KEY_RATING = "rating";
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_ROWID = "_id";

	public static final String KEY_TOTAL_GAMES = "game";
	public static final String KEY_TOTAL_WIN = "win";

	private static final String MAIN_DATABASE_CREATE =
		"create table " + DATABASE_MAIN_TABLE + " ("
		+ KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_WORD + " text not null, "
		+ KEY_DEFINITION + " text not null, "
		+ KEY_DATE_STRING + " text not null, "
		+ KEY_RATING + " long default 0, "
		+ KEY_COMMENTS + " integer default 0, "
		+ KEY_DATE_MS + " integer);";

	private static final String GAME_DATABASE_CREATE =
		"create table " + DATABASE_GAME_TABLE + " ("
		+ KEY_ROWID + " integer primary key autoincrement, "
		+ KEY_TOTAL_GAMES + " integer default 0, "
		+ KEY_TOTAL_WIN + " integer default 0); ";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(MAIN_DATABASE_CREATE);
				db.execSQL(GAME_DATABASE_CREATE);
				insertValue(db);
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

		}

		private void insertValue(SQLiteDatabase db) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_TOTAL_GAMES, 0);
			contentValues.put(KEY_TOTAL_WIN, 0);
			if(db.insert(DATABASE_GAME_TABLE, null, contentValues) != -1) {
				Log.i(tag, "Palabra insertada en la BD correctamente");
			} else {
				Log.e(tag, "Problema al escribir en la BD");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
				Log.w(tag, "Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MAIN_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GAME_TABLE);
				onCreate(db);
			} catch (Exception e) {
				Log.w(tag, e);
			}
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public DbHandler(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DbHandler open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
		mDb.close();
	}


	public Cursor getElementById(long id) {
		String[] campos = new String[] {KEY_WORD, KEY_DEFINITION};
		String[] args = new String[] {Long.toString(id)};

		return mDb.query(DATABASE_MAIN_TABLE, campos, "_id=?", args, null, null, null);
	}

	public Cursor fetchAllWords(String order) {
		String[] campos = new String[] {KEY_WORD, KEY_DATE_STRING, KEY_RATING, KEY_COMMENTS, KEY_ROWID};

		return mDb.query(DATABASE_MAIN_TABLE, campos, null, null, null, null, order);
	}	

	public void updateElement(String name, Long rating) {
		String[] args = new String[] {name};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_RATING, rating);
		mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_WORD + "=?", args);
	}

	public Cursor getWord(long ms) {
		String[] campos = new String[] {KEY_WORD, KEY_DEFINITION, KEY_DATE_STRING};
		String[] args = new String[] {Long.valueOf(ms).toString()};

		return mDb.query(DATABASE_MAIN_TABLE, campos, KEY_DATE_MS +"=?", args, null, null, null);
	}

	public String getTodayWord() {
		String out = "";

		Calendar calendar = Calendar.getInstance();
		Long ms = calendar.getTimeInMillis() - 
		calendar.get(Calendar.MILLISECOND) - 
		calendar.get(Calendar.SECOND)*1000 - 
		calendar.get(Calendar.MINUTE)*1000*60 - 
		calendar.get(Calendar.HOUR_OF_DAY)*1000*60*60;

		String[] campos = new String[] {KEY_WORD};
		String[] args = new String[] {Long.valueOf(ms).toString()};

		Cursor result = mDb.query(DATABASE_MAIN_TABLE, campos, KEY_DATE_MS +"=?", args, null, null, null);

		if(result.getCount() != 0) {
			result.moveToFirst();
			out = result.getString(0);
		}

		return out;
	}

	public String getLastWord() {
		String out = "No disponible<br>Click para actualizar";

		String[] campos = new String[] {KEY_WORD, KEY_DATE_STRING};

		Cursor result = mDb.query(DATABASE_MAIN_TABLE, campos, null, null, null, null, KEY_DATE_MS + " DESC");

		if(result.getCount() != 0) {
			result.moveToFirst();
			out = "<b><h1>" + result.getString(0) + "</h1></b>\n" + result.getString(1);
		}

		return out;
	}

	public boolean updateWithNewRssFeed() {
		boolean update = false;

		List<Word> words = RssReader.getLatestWordList();
		for(Word word: words) {
			if (!isWord(word)) {
				update = true;
				createWord(word);
			} else {
				updateDateWord(word);
			}
		}

		return update;
	}

	private boolean isWord(Word word) {
		boolean out = false;

		String[] args = new String[] {word.getName()};

		Cursor result = mDb.query(DATABASE_MAIN_TABLE, null, KEY_WORD +"=?", args, null, null, null);

		if(result.getCount() != 0) {
			out = true;
		}

		return out;
	}

	public void createWord(Word word) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_WORD, word.getName());
		contentValues.put(KEY_DEFINITION, word.getDefinition());
		contentValues.put(KEY_DATE_MS, word.getDateMs());
		contentValues.put(KEY_DATE_STRING, word.getDateString());
		if(mDb.insert(DATABASE_MAIN_TABLE, null, contentValues) != -1) {
			Log.i(tag, "Palabra insertada en la BD correctamente");
		} else {
			Log.e(tag, "Problema al escribir en la BD");
		}
	}
	
	public void updateDateWord(Word word) {
		String[] args = new String[] {word.getName()};
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DATE_MS, word.getDateMs());
		contentValues.put(KEY_DATE_STRING, word.getDateString());
		mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_WORD + "=?", args);
	}

	public void updateElement(long rowId, float rating) {
		String[] args = new String[] {Long.valueOf(rowId).toString()};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_RATING, rating);
		mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_ROWID + "=?", args);
	}

	public void updateElement(String name, float rating) {
		String[] args = new String[] {name};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_RATING, rating);
		mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_WORD+"=?", args);
	}

	public Cursor getElementByName(String name) {
		String[] campos = new String[] {KEY_WORD, KEY_DEFINITION, KEY_RATING, KEY_DATE_STRING};
		String[] args = new String[] {name};

		return mDb.query(DATABASE_MAIN_TABLE, campos, KEY_WORD+"=?", args, null, null, null);
	}

	public List<WordAndDefinition> getWordAndDefinitionList() {
		List<WordAndDefinition> wordAndDefinitionList = new ArrayList<WordAndDefinition>();
		String[] campos = new String[] {KEY_WORD, KEY_DEFINITION};

		Cursor cursor = mDb.query(DATABASE_MAIN_TABLE, campos, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				WordAndDefinition wordAndDefinition = new WordAndDefinition();
				wordAndDefinition.setWord(cursor.getString(0));
				wordAndDefinition.setDefinition(cursor.getString(1));
				wordAndDefinitionList.add(wordAndDefinition);
			} while (cursor.moveToNext());
		}

		return wordAndDefinitionList;
	}

	public int getNumberOfWords() {
		Cursor cursor = mDb.rawQuery("SELECT COUNT(*) FROM " + DATABASE_MAIN_TABLE, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public void saveHits(int hits) {
		String[] campos = new String[] {KEY_TOTAL_WIN};

		Cursor cursor = mDb.query(DATABASE_GAME_TABLE, campos, null, null, null, null, null);
		cursor.moveToFirst();

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TOTAL_WIN, hits + cursor.getInt(0));

		mDb.update(DATABASE_GAME_TABLE, contentValues, null, null);
	}

	public int getHits() {
		String[] campos = new String[] {KEY_TOTAL_WIN};
		Cursor cursor = mDb.query(DATABASE_GAME_TABLE, campos, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public void saveTotal(int total) {
		String[] campos = new String[] {KEY_TOTAL_GAMES};

		Cursor cursor = mDb.query(DATABASE_GAME_TABLE, campos, null, null, null, null, null);
		cursor.moveToFirst();

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TOTAL_GAMES, total + cursor.getInt(0));

		mDb.update(DATABASE_GAME_TABLE, contentValues, null, null);
	}

	public int getTotal() {
		String[] campos = new String[] {KEY_TOTAL_GAMES};
		Cursor cursor = mDb.query(DATABASE_GAME_TABLE, campos, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public void resetStats() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TOTAL_WIN, 0);
		contentValues.put(KEY_TOTAL_GAMES, 0);

		mDb.update(DATABASE_GAME_TABLE, contentValues, null, null);
	}

	public DbHelperHistoricalWords migrate() {
		DbHelperHistoricalWords dbHelper = null;;
		
		dbHelper = getDbHelper();
		
		if (dbHelper == null) {
			Log.w(tag, "Error al obtener dbHelper");
		}
		
		return dbHelper;
		
	}

	private DbHelperHistoricalWords getDbHelper() throws Error {
		DbHelperHistoricalWords dbHelper = new DbHelperHistoricalWords(mCtx);

		try {
			dbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		return dbHelper;
	}

	public long insert(String databaseMainTable, ContentValues contentValues) {
		return mDb.insert(DATABASE_MAIN_TABLE, null, contentValues);
	}

	public Cursor query(String databaseMainTable, String[] campos,
			String string, String[] args) {
		return mDb.query(DbHandler.DATABASE_MAIN_TABLE, campos, DbHandler.KEY_WORD+"=?", args, null, null, null);
	}

	public int update(String databaseMainTable, ContentValues contentValues, String[] args) {
		return mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_WORD + "=?", args);
	}

	public void updateNumberOfComments(String palabra, int numberOfComments) {
		String[] args = new String[] {palabra};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_COMMENTS, numberOfComments);
		mDb.update(DATABASE_MAIN_TABLE, contentValues, KEY_WORD + "=?", args);
	}
}
