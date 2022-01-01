package my.app.androidapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DBHelper extends SQLiteAssetHelper {


    public DBHelper(Context context) {
        super(context, "database.db", null, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        super.onUpgrade(db, oldVersion, newVersion);

    }


}