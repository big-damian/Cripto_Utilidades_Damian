package com.damian.criptoutils.utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.damian.criptoutils.miscriptorecyclersqlite.MisCriptomonedas;

import java.util.ArrayList;
import java.util.List;

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

    public void insert(String Nombre, String Precio, String MarketCap, String Descripcion, String iconoURL) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLiteGenerator.NOMBRE, Nombre);
        contentValue.put(SQLiteGenerator.PRECIO, Precio);
        contentValue.put(SQLiteGenerator.MARKETCAP, MarketCap);
        contentValue.put(SQLiteGenerator.DESCRIPCION, Descripcion);
        contentValue.put(SQLiteGenerator.ICONOURL, iconoURL);
        database.insert(SQLiteGenerator.NOMBRE_TABLA1, null, contentValue);
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

    public void actualizarPrecioCriptomoneda(String nombre, String precio) {
        ContentValues values = new ContentValues();
        values.put("Precio", precio);

        // Update the row in the Criptomonedas table where the name matches
        database.update("Criptomonedas", values, "Nombre = ?", new String[]{nombre});
    }

    public void actualizarCriptomoneda(String nombre, String precio, String marketCap, String descripcion, String iconoURL) {
        ContentValues values = new ContentValues();
        values.put("Precio", precio);
        values.put("Cap_Mercado", marketCap);
        values.put("Descripcion", descripcion);
        values.put("IconoURL", iconoURL);

        // Update the row in the Criptomonedas table where the name matches
        database.update("Criptomonedas", values, "Nombre = ?", new String[]{nombre});
    }

    public void actualizarCriptomoneda(String nombre, String precio, String marketCap, String descripcion) {
        ContentValues values = new ContentValues();
        values.put("Precio", precio);
        values.put("Cap_Mercado", marketCap);
        values.put("Descripcion", descripcion);

        // Update the row in the Criptomonedas table where the name matches
        database.update("Criptomonedas", values, "Nombre = ?", new String[]{nombre});
    }

    public void delete(String Nombre) {
        database.delete(SQLiteGenerator.NOMBRE_TABLA1, SQLiteGenerator.NOMBRE + "=" + Nombre, null);
    }

    // Metodos para tabla Mis_Criptomonedas

    public long guardarMiCriptomoneda(String Nombre, String Simbolo, String Cantidad) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Nombre", Nombre);
        contentValues.put("Simbolo", Simbolo);
        contentValues.put("Cantidad", Cantidad);
        long idColumna = database.insert(SQLiteGenerator.NOMBRE_TABLA2, null, contentValues);

//        Log.e("SQLite", "Guardado " + Precio + " para " + Nombre + " en la BD SQLite de Criptos");
//        Log.e("LlamadaAPI", "Guardado: '" + SQLiteBD.selectPrecioBDD("Bitcoin") + "' en la Base de Datos");
        return idColumna;
    }

    @SuppressLint("Range")
    public String selectMisCriptomonedasBDD(String Simbolo) {
        String tablaString = " ";
        Cursor cursor  = database.rawQuery("SELECT Simbolo FROM Mis_Criptomonedas WHERE Simbolo = '" + Simbolo + "'", null);

        if (cursor.moveToFirst()) {
            tablaString = cursor.getString(cursor.getColumnIndex("Simbolo"));
        }

        return tablaString;
    }

    public String selectIconoURL(String nombreCripto) {
        String valor = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400"; // Por defecto

        Cursor cursor = database.rawQuery("SELECT IconoURL FROM Criptomonedas WHERE Nombre = ?", new String[]{nombreCripto});

        if (cursor.moveToFirst()) {
            valor = cursor.getString(0);
        } cursor.close();

        return valor;
    }

    // Metodo para obtener todas las criptos para MisCriptos
    @SuppressLint("Range")
    public List<MisCriptomonedas> selectTodasMiscriptos() {
        List<MisCriptomonedas> cryptoList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Mis_Criptomonedas", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("Id"));
                String nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                String simbolo = cursor.getString(cursor.getColumnIndex("Simbolo"));
                String cantidad = cursor.getString(cursor.getColumnIndex("Cantidad"));
                cryptoList.add(new MisCriptomonedas(id, nombre, simbolo, cantidad));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cryptoList;
    }

    // Metodo borrar MisCriptomonedas por simbolo
    public void deleteMisCriptomoneda(String simbolo) {
        database.delete(SQLiteGenerator.NOMBRE_TABLA2, "Simbolo = ?", new String[]{simbolo});
    }

    // Recordar usuario
    public void recordarUsuarioBDD(String Email, String Contraseña) {
        ContentValues contentValue = new ContentValues();
        contentValue.put("Id", "1");
        contentValue.put("Email", Email);
        contentValue.put("Contraseña", Contraseña);
        contentValue.put("Switch_Activo", "true");
        database.insert(SQLiteGenerator.NOMBRE_TABLA3, null, contentValue);
    }

    // Olvidar usuario
    public void olvidarUsuarioBDD() {
//        database.delete(SQLiteGenerator.NOMBRE_TABLA3, "Email = ?", new String[]{"*"});
        try {
            // Borra todos los registros de la tabla
            database.delete(SQLiteGenerator.NOMBRE_TABLA3, null, null);
        } catch (Exception e) {
            Log.e("SQLite", "Error eliminando registros usuario recordadado: " + Log.getStackTraceString(e));
        }
    }

    // Comprobar si usuario guardado
    public String comprobarUsarioRecordado() {
        String valor = "false"; // Por defecto

        Cursor cursor = database.rawQuery("SELECT Switch_Activo FROM " + SQLiteGenerator.NOMBRE_TABLA3, null, null);

        if (cursor.moveToFirst()) {
            valor = cursor.getString(0);
        } cursor.close();

        return valor;
    }

    // Recuperar email usuario guardado
    public String recuperarEmailRecordado() {
        String valor = ""; // Por defecto

        Cursor cursor = database.rawQuery("SELECT Email FROM " + SQLiteGenerator.NOMBRE_TABLA3, null, null);

        if (cursor.moveToFirst()) {
            valor = cursor.getString(0);
        } cursor.close();

        return valor;
    }

    // Recuperar contraseña usuario guardado
    public String recuperarContraseñaRecordado() {
        String valor = ""; // Por defecto

        Cursor cursor = database.rawQuery("SELECT Contraseña FROM " + SQLiteGenerator.NOMBRE_TABLA3, null, null);

        if (cursor.moveToFirst()) {
            valor = cursor.getString(0);
        } cursor.close();

        return valor;
    }
}
