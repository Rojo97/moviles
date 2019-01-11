/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    // Constructor
    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    // Llamado para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Consultas para crear las tablas de la bd que encesitamos de la bd remota
        String sqlParticipacion = String.format("create table %s (%s int primary key, %s text, %s text, foreign key (%s) references %s (%s))",
                StatusContract.TABLEPARTICIPACION,
                StatusContract.ColumnParticipacion.ID,
                StatusContract.ColumnParticipacion.USER,
                StatusContract.ColumnParticipacion.LISTA,
                StatusContract.ColumnParticipacion.LISTA,
                StatusContract.TABLELISTACOMPRA,
                StatusContract.ColumnListaCompra.ID);

        String sqlListaCompra = String.format("create table %s (%s texto primary key, %s text, %s int)",
                StatusContract.TABLELISTACOMPRA,
                StatusContract.ColumnListaCompra.USER,
                StatusContract.ColumnListaCompra.STATUS);

        String sqlElemento = String.format("create table %s (%s text , %s int, %s float, %s text, %s int, foreign key (%s) references %s (%s), primary key (%s, %s))",
                StatusContract.TABLEELEMENTO,
                StatusContract.ColumnElemento.ID,
                StatusContract.ColumnElemento.QUANTITY,
                StatusContract.ColumnElemento.PRICE,
                StatusContract.ColumnElemento.IDLISTA,
                StatusContract.ColumnElemento.STATUS,
                StatusContract.ColumnParticipacion.LISTA,
                StatusContract.TABLELISTACOMPRA,
                StatusContract.ColumnListaCompra.ID,
                StatusContract.ColumnElemento.IDLISTA);

        Log.d(TAG, "onCreate con SQL: " + sqlParticipacion);
        Log.d(TAG, "onCreate con SQL: " + sqlListaCompra);
        Log.d(TAG, "onCreate con SQL: " + sqlElemento);
        db.execSQL(sqlParticipacion);
        db.execSQL(sqlListaCompra);
        db.execSQL(sqlElemento);
    }
    // Llamado siempre que tengamos una nueva version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

// Aqui irían las sentencias del tipo ALTER TABLE, de momento lo hacemosmas sencillo...
// Borramos la vieja base de datos
        db.execSQL("drop table if exists " + StatusContract.TABLEPARTICIPACION);
// Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG,
                "onUpgrade");
    }
}