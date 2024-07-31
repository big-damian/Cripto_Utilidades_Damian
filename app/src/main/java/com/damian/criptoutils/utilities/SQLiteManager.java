package com.damian.criptoutils.utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteManager {

    private SQLiteGenerator SQLiteGenerator;

    private Context context;

    private SQLiteDatabase database;

    public SQLiteManager(Context c) {
        context = c;
    }
    

    public SQLiteManager open() throws SQLException {
        SQLiteGenerator = new SQLiteGenerator(context);
        database = SQLiteGenerator.getWritableDatabase();
        return this;
    }

    public void close() {
        SQLiteGenerator.close();
    }

    public void insert(String Nombre, String Precio, String MarketCap, String Descripcion) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLiteGenerator.NOMBRE, Nombre);
        contentValue.put(SQLiteGenerator.PRECIO, Precio);
        contentValue.put(SQLiteGenerator.MARKETCAP, MarketCap);
        contentValue.put(SQLiteGenerator.DESCRIPCION, Descripcion);
        database.insert(SQLiteGenerator.NOMBRE_TABLA1, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { SQLiteGenerator.ID, SQLiteGenerator.NOMBRE, SQLiteGenerator.PRECIO, SQLiteGenerator.MARKETCAP, SQLiteGenerator.DESCRIPCION };
        Cursor cursor = database.query(SQLiteGenerator.NOMBRE_TABLA1, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchPrecio(String Nombre) {
        String[] columns = new String[] { SQLiteGenerator.PRECIO };
        Cursor cursor = database.query(SQLiteGenerator.NOMBRE_TABLA1, columns, "Nombre = " + Nombre, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    @SuppressLint("Range")
    public String selectPrecioBDD(String Nombre) {
//        Log.d(TAG, "getTableAsString called");
        String tablaString = " ";
        Cursor cursor  = database.rawQuery("SELECT Precio FROM Criptomonedas WHERE Nombre = '" + Nombre + "'", null);

        if (cursor.moveToFirst()) {
            tablaString = cursor.getString(cursor.getColumnIndex("Precio"));
        }

        return tablaString;
    }

    public int update(long Id, String Nombre, String Precio, String MarketCap, String Descripcion) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteGenerator.NOMBRE, Nombre);
        contentValues.put(SQLiteGenerator.PRECIO, Precio);
        contentValues.put(SQLiteGenerator.MARKETCAP, MarketCap);
        contentValues.put(SQLiteGenerator.DESCRIPCION, Descripcion);
        int i = database.update(SQLiteGenerator.NOMBRE_TABLA1, contentValues, SQLiteGenerator.NOMBRE + " = " + Nombre, null);
        return i;
    }

    public int actualizarPrecio(String Nombre, String Precio) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteGenerator.PRECIO, Precio);
        int i = database.update(SQLiteGenerator.NOMBRE_TABLA1, contentValues, (Nombre + " = " + SQLiteGenerator.NOMBRE), null);
//        Log.e("SQLite", "Guardado " + Precio + " para " + Nombre + " en la BD SQLite de Criptos");
//        Log.e("LlamadaAPI", "Guardado: '" + SQLiteBD.selectPrecioBDD("Bitcoin") + "' en la Base de Datos");
        return i;
    }

    public void delete(String Nombre) {
        database.delete(SQLiteGenerator.NOMBRE_TABLA1, SQLiteGenerator.NOMBRE + "=" + Nombre, null);
    }
}
