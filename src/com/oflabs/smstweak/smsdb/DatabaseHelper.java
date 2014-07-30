package com.oflabs.smstweak.smsdb;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "";

    private static final String TABLE_SMS = "table_sms";
    private static final String COL_ID = "_id";
    private static final String COL_CALLERID = "callerid";
    private static final String COL_BODY = "body";
    private static final String COL_DATETIME = "datetime";
    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_SMS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DATETIME + " DATE NOT NULL, "
            + COL_CALLERID + " TEXT NOT NULL, "
            + COL_BODY + " TEXT NOT NULL);";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS + ";");
        onCreate(db);
    }

}