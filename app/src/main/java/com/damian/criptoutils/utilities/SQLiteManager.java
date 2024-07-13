package com.damian.criptoutils.utilities;

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

//    public SQLiteManager(Context c, String Precio, String Nombre) {
//        context = c;
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(SQLiteGenerator.PRICE, Precio);
//        database.update(SQLiteGenerator.TABLE_NAME, contentValues, SQLiteGenerator.NAME + " = " + Nombre, null);
//        return i;
//    }

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
        contentValue.put(SQLiteGenerator.NAME, Nombre);
        contentValue.put(SQLiteGenerator.PRICE, Precio);
        contentValue.put(SQLiteGenerator.MARKETCAP, MarketCap);
        contentValue.put(SQLiteGenerator.DESCRIPTION, Descripcion);
        database.insert(SQLiteGenerator.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { SQLiteGenerator.ID, SQLiteGenerator.NAME, SQLiteGenerator.PRICE, SQLiteGenerator.MARKETCAP, SQLiteGenerator.DESCRIPTION };
        Cursor cursor = database.query(SQLiteGenerator.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchPrecio(String Nombre) {
        String[] columns = new String[] { SQLiteGenerator.PRICE };
        Cursor cursor = database.query(SQLiteGenerator.TABLE_NAME, columns, "Nombre = " + Nombre, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }








//    public String getTableAsString(String Nombre) {
////        Log.d(TAG, "getTableAsString called");
//        String cursorString = "";
//        String tablaString = String.format("Table %s:\n", SQLiteGenerator.TABLE_NAME);
////        Cursor allRows  = database.rawQuery("SELECT Precio FROM Criptomonedas WHERE Nombre = '" + Nombre + "'", null);
//        Cursor allRows  = database.rawQuery("SELECT Precio FROM Criptomonedas WHERE Nombre = '" + Nombre + "'", null);
////        Cursor allRows  = database.query(SQLiteGenerator.TABLE_NAME, columns, "Nombre = " + Nombre, null, null, null, null);
//        if (allRows.moveToFirst() ){
//            String[] columnNames = allRows.getColumnNames();
//            for (String name: columnNames)
//                cursorString += String.format("%s ][ ", name);
//            cursorString += "\n";
//            do {
//                for (String name: columnNames) {
//                    cursorString += String.format("%s ][ ",
//                            allRows.getString(allRows.getColumnIndex(name)));
//                }
//                cursorString += "\n";
//            } while (allRows.moveToNext());
//        }
//
//        return tablaString;
//    }

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
        contentValues.put(SQLiteGenerator.NAME, Nombre);
        contentValues.put(SQLiteGenerator.PRICE, Precio);
        contentValues.put(SQLiteGenerator.MARKETCAP, MarketCap);
        contentValues.put(SQLiteGenerator.DESCRIPTION, Descripcion);
        int i = database.update(SQLiteGenerator.TABLE_NAME, contentValues, SQLiteGenerator.NAME + " = " + Nombre, null);
        return i;
    }

    public int actualizarPrecio(String Nombre, String Precio) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteGenerator.PRICE, Precio);
        int i = database.update(SQLiteGenerator.TABLE_NAME, contentValues, (Nombre + " = " + SQLiteGenerator.NAME), null);
//        Log.e("SQLite", "Guardado " + Precio + " para " + Nombre + " en la BD SQLite de Criptos");
//        Log.e("LlamadaAPI", "Guardado: '" + SQLiteBD.selectPrecioBDD("Bitcoin") + "' en la Base de Datos");
        return i;
    }

    public void delete(String Nombre) {
        database.delete(SQLiteGenerator.TABLE_NAME, SQLiteGenerator.NAME + "=" + Nombre, null);
    }
}
