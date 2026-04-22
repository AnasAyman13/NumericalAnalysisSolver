package com.numerical.analysis.solver.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class HistoryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "HistoryDatabase.db"
        const val TABLE_HISTORY = "history"
        
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_SUBTITLE = "subtitle"
        const val KEY_RESULT = "result"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_ACCENT = "accent_color"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_SUBTITLE + " TEXT,"
                + KEY_RESULT + " TEXT,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_ACCENT + " INTEGER" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY)
        onCreate(db)
    }
}

class HistoryRepository(context: Context) {
    private val dbHelper = HistoryDatabaseHelper(context)

    fun addEntry(entry: HistoryEntry) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(HistoryDatabaseHelper.KEY_TITLE, entry.title)
            put(HistoryDatabaseHelper.KEY_SUBTITLE, entry.subtitle)
            put(HistoryDatabaseHelper.KEY_RESULT, entry.result)
            put(HistoryDatabaseHelper.KEY_TIMESTAMP, entry.timestamp)
            put(HistoryDatabaseHelper.KEY_ACCENT, entry.accentColor.toArgb())
        }
        db.insert(HistoryDatabaseHelper.TABLE_HISTORY, null, values)
        db.close()
    }

    fun getAllHistory(): List<HistoryEntry> {
        val historyList = mutableListOf<HistoryEntry>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${HistoryDatabaseHelper.TABLE_HISTORY} ORDER BY ${HistoryDatabaseHelper.KEY_ID} DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_TITLE))
                val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_SUBTITLE))
                val result = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_RESULT))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_TIMESTAMP))
                val colorInt = cursor.getInt(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_ACCENT))
                
                historyList.add(HistoryEntry(id, title, subtitle, result, timestamp, Color(colorInt)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }
}
