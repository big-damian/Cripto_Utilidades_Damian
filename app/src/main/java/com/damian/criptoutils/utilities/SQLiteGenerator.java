package com.damian.criptoutils.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteGenerator extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "Criptomonedas";

    // Table columns
    public static final String ID = "Id";
    public static final String NAME = "Nombre";
    public static final String PRICE = "Precio";
    public static final String MARKETCAP = "Cap_Mercado";
    public static final String DESCRIPTION = "Descripcion";

    // Database Information
    static final String DB_NAME = "CriptoUtilsDamian";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT UNIQUE,"
            + PRICE + " TEXT,"
            + MARKETCAP + " TEXT,"
            + DESCRIPTION + " TEXT)";

    public SQLiteGenerator(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
