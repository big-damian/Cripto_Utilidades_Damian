package com.damian.criptoutils.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteGenerator extends SQLiteOpenHelper {

    // --- Variables SQLite ---

    // Nombre de las tablas
    public static final String NOMBRE_TABLA1 = "Criptomonedas";
    public static final String NOMBRE_TABLA2 = "Mis_Criptomonedas";
    public static final String NOMBRE_TABLA3 = "Recordar_Usuario";

    // Columnas de la tabla 1
    public static final String ID = "Id";
    public static final String NOMBRE = "Nombre";
    public static final String PRECIO = "Precio";
    public static final String MARKETCAP = "Cap_Mercado";
    public static final String DESCRIPCION = "Descripcion";
    public static final String ICONOURL = "IconoURL";

    // Nombre de la BD
    static final String BD_NOMBRE = "CriptoUtilsDamian";

    // Version de la base de datos
    static final int BD_VERSION = 1;

    // SQL para crear la tabla Criptomonedas
    private static final String CRATE_TABLE_1 = "CREATE TABLE " + NOMBRE_TABLA1 + " ("
            + ID + " INTEGER, "
            + NOMBRE + " TEXT UNIQUE,"
            + PRECIO + " TEXT,"
            + MARKETCAP + " TEXT,"
            + DESCRIPCION + " TEXT,"
            + ICONOURL + " TEXT)";

    // SQL para crear la tabla Mis_criptomonedas
    private static final String CRATE_TABLE_2 =
            "CREATE TABLE " + NOMBRE_TABLA2 + " ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Nombre TEXT UNIQUE,"
                    + "Simbolo TEXT UNIQUE,"
                    + "Cantidad TEXT,"
                    + "Euros TEXT,"
                    + "Descripcion TEXT)";

    // SQL para crear la tabla Recordar_Usuario
    private static final String CRATE_TABLE_3 =
            "CREATE TABLE " + NOMBRE_TABLA3 + " ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Email TEXT UNIQUE,"
                    + "Contraseña TEXT UNIQUE,"
                    + "Switch_Activo TEXT UNIQUE)";

    // --- Metodos SQLite ---

    public SQLiteGenerator(Context context) {
        super(context, BD_NOMBRE, null, BD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRATE_TABLE_1);
        db.execSQL(CRATE_TABLE_2);
        db.execSQL(CRATE_TABLE_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA1);
        db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA2);
        db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA3);
        onCreate(db);
    }

}
