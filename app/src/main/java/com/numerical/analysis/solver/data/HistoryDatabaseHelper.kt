package com.numerical.analysis.solver.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class HistoryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME   = "HistoryDatabase.db"
        const val TABLE_HISTORY   = "history"

        const val KEY_ID            = "id"
        const val KEY_TITLE         = "title"
        const val KEY_SUBTITLE      = "subtitle"
        const val KEY_RESULT        = "result"
        const val KEY_TIMESTAMP     = "timestamp"
        const val KEY_ACCENT        = "accent_color"
        // New columns added in version 2
        const val KEY_EQUATION      = "equation"
        const val KEY_DERIVATIVE    = "derivative"
        const val KEY_XL            = "x_low"
        const val KEY_XU            = "x_high"
        const val KEY_XI            = "x_initial"
        const val KEY_XMINUS1       = "x_minus1"
        const val KEY_EPS           = "tolerance"
        const val KEY_MAX_ITER      = "max_iterations"
        const val KEY_METHOD_TYPE   = "method_type"
        // New columns added in version 4 — Linear System matrix/vector storage
        const val KEY_MATRIX_DATA   = "matrix_data"
        const val KEY_VECTOR_DATA   = "vector_data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_HISTORY (" +
            "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$KEY_TITLE TEXT, " +
            "$KEY_SUBTITLE TEXT, " +
            "$KEY_RESULT TEXT, " +
            "$KEY_TIMESTAMP TEXT, " +
            "$KEY_ACCENT INTEGER, " +
            "$KEY_EQUATION TEXT DEFAULT '', " +
            "$KEY_DERIVATIVE TEXT DEFAULT '', " +
            "$KEY_XL TEXT DEFAULT '', " +
            "$KEY_XU TEXT DEFAULT '', " +
            "$KEY_XI TEXT DEFAULT '', " +
            "$KEY_XMINUS1 TEXT DEFAULT '', " +
            "$KEY_EPS TEXT DEFAULT '', " +
            "$KEY_MAX_ITER TEXT DEFAULT '100', " +
            "$KEY_METHOD_TYPE TEXT DEFAULT '', " +
            "$KEY_MATRIX_DATA TEXT DEFAULT '', " +
            "$KEY_VECTOR_DATA TEXT DEFAULT '')"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop and recreate — acceptable for a student project
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }
}

class HistoryRepository(context: Context) {
    private val dbHelper = HistoryDatabaseHelper(context)

    fun addEntry(entry: HistoryEntry) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(HistoryDatabaseHelper.KEY_TITLE,       entry.title)
            put(HistoryDatabaseHelper.KEY_SUBTITLE,    entry.subtitle)
            put(HistoryDatabaseHelper.KEY_RESULT,      entry.result)
            put(HistoryDatabaseHelper.KEY_TIMESTAMP,   entry.timestamp)
            put(HistoryDatabaseHelper.KEY_ACCENT,      entry.accentColor.toArgb())
            put(HistoryDatabaseHelper.KEY_EQUATION,    entry.equation)
            put(HistoryDatabaseHelper.KEY_DERIVATIVE,  entry.derivative)
            put(HistoryDatabaseHelper.KEY_XL,          entry.xl)
            put(HistoryDatabaseHelper.KEY_XU,          entry.xu)
            put(HistoryDatabaseHelper.KEY_XI,          entry.xi)
            put(HistoryDatabaseHelper.KEY_XMINUS1,     entry.xMinus1)
            put(HistoryDatabaseHelper.KEY_EPS,         entry.eps)
            put(HistoryDatabaseHelper.KEY_MAX_ITER,    entry.maxIterations)
            put(HistoryDatabaseHelper.KEY_METHOD_TYPE, entry.methodType)
            put(HistoryDatabaseHelper.KEY_MATRIX_DATA, entry.matrixData)
            put(HistoryDatabaseHelper.KEY_VECTOR_DATA, entry.vectorData)
        }
        db.insert(HistoryDatabaseHelper.TABLE_HISTORY, null, values)
        db.close()
    }

    fun getAllHistory(): List<HistoryEntry> {
        val list = mutableListOf<HistoryEntry>()
        val db   = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${HistoryDatabaseHelper.TABLE_HISTORY} ORDER BY ${HistoryDatabaseHelper.KEY_ID} DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    HistoryEntry(
                        id            = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_ID)),
                        title         = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_TITLE)),
                        subtitle      = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_SUBTITLE)),
                        result        = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_RESULT)),
                        timestamp     = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_TIMESTAMP)),
                        accentColor   = Color(cursor.getInt(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_ACCENT))),
                        equation      = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_EQUATION)) ?: "",
                        derivative    = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_DERIVATIVE)) ?: "",
                        xl            = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_XL))       ?: "",
                        xu            = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_XU))       ?: "",
                        xi            = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_XI))       ?: "",
                        xMinus1       = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_XMINUS1))  ?: "",
                        eps           = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_EPS))       ?: "",
                        maxIterations = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_MAX_ITER))  ?: "100",
                        methodType    = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_METHOD_TYPE)) ?: "",
                        matrixData    = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_MATRIX_DATA)) ?: "",
                        vectorData    = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDatabaseHelper.KEY_VECTOR_DATA)) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}

