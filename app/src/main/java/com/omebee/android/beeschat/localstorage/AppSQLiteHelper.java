package com.omebee.android.beeschat.localstorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.omebee.android.global.OmebeeApplication;
import com.omebee.android.layers.localstorage.sqlitedb.tables.TableBrand;
import com.omebee.android.layers.localstorage.sqlitedb.tables.TableCategory;
import com.omebee.android.layers.localstorage.sqlitedb.tables.TableMasterTag;
import com.omebee.android.utils.AppLog;
import com.omebee.android.utils.SQLiteDBConstant;

/**
 * Created by phannguyen on 11/15/15.
 */
public class AppSQLiteHelper extends SQLiteOpenHelper {
    private static Object lock = new Object();
    private static AppSQLiteHelper sInstance;
    private SQLiteDatabase database;
    private int mDbOpeningCount = 0;//this variable will help access db from multi threads

    public static synchronized AppSQLiteHelper getInstance() {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new AppSQLiteHelper(OmebeeApplication.getAppContext());
        }
        return sInstance;
    }

    public AppSQLiteHelper(Context context) {
        super(context, SQLiteDBConstant.APP_DATABASE_NAME, null, SQLiteDBConstant.APP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TableCategory.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(TableBrand.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(TableMasterTag.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TableBrand.TABLE_NAME);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TableCategory.TABLE_NAME);
//        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase openDB() {
        synchronized(lock) {
            if(database==null || (database!=null && !database.isOpen()))
                database = getWritableDatabase();
            mDbOpeningCount++;
            AppLog.log(Log.INFO, "OPEN APP DB", mDbOpeningCount + "");
            return database;
        }

    }

    public void closeDB() {
        synchronized(lock) {
            mDbOpeningCount--;
            AppLog.log(Log.INFO, "CLOSE APP DB", mDbOpeningCount+"");
            if(mDbOpeningCount<=0)//there no connection to db now, ok close db connection.
            {
                mDbOpeningCount = 0;
                AppLog.log(Log.INFO, "CLOSE APP DB", "No connection left, DB CLosed");
                close();
            }
        }

    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
