/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    // Constructor
    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    // Llamado para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG,  " Db CREAAAAAAAA");

        try{
            db.execSQL("drop table if exists " + StatusContract.TABLEELEMENTO);
            db.execSQL("drop table if exists " + StatusContract.TABLEPARTICIPACION);
            db.execSQL("drop table if exists " + StatusContract.TABLELISTACOMPRA);

            //Consultas para crear las tablas de la bd que encesitamos de la bd remota
            String sqlListaCompra = String.format("create table %s (%s text primary key, %s text, %s int)",
                    StatusContract.TABLELISTACOMPRA,
                    StatusContract.ColumnListaCompra.ID,
                    StatusContract.ColumnListaCompra.USER,
                    StatusContract.ColumnListaCompra.STATUS);

            String sqlParticipacion = String.format("create table %s (%s text, %s text, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    StatusContract.TABLEPARTICIPACION,
                    StatusContract.ColumnParticipacion.USER,
                    StatusContract.ColumnParticipacion.LISTA,
                    StatusContract.ColumnParticipacion.LISTA,
                    StatusContract.TABLELISTACOMPRA,
                    StatusContract.ColumnListaCompra.ID,
                    StatusContract.ColumnParticipacion.USER,
                    StatusContract.ColumnParticipacion.LISTA);

            String sqlElemento = String.format("create table %s (%s text , %s int, %s float, %s text, %s int, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    StatusContract.TABLEELEMENTO,
                    StatusContract.ColumnElemento.ID,
                    StatusContract.ColumnElemento.QUANTITY,
                    StatusContract.ColumnElemento.PRICE,
                    StatusContract.ColumnElemento.IDLISTA,
                    StatusContract.ColumnElemento.STATUS,
                    StatusContract.ColumnElemento.IDLISTA,
                    StatusContract.TABLELISTACOMPRA,
                    StatusContract.ColumnListaCompra.ID,
                    StatusContract.ColumnElemento.ID,
                    StatusContract.ColumnElemento.IDLISTA);

            Log.d(TAG, "onCreate con SQL Db: " + sqlParticipacion);
            Log.d(TAG, "onCreate con SQL Db: " + sqlListaCompra);
            Log.d(TAG, "onCreate con SQL Db: " + sqlElemento);
            db.execSQL(sqlListaCompra);
            db.execSQL(sqlParticipacion);
            db.execSQL(sqlElemento);
        }catch(Exception e){
            Log.d(TAG, e.getMessage() + " Db");
        }
    }
    // Llamado siempre que tengamos una nueva version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

// Aqui irían las sentencias del tipo ALTER TABLE, de momento lo hacemosmas sencillo...
// Borramos la vieja base de datos
        db.execSQL("drop table if exists " + StatusContract.TABLEELEMENTO);
        db.execSQL("drop table if exists " + StatusContract.TABLEPARTICIPACION);
        db.execSQL("drop table if exists " + StatusContract.TABLELISTACOMPRA);
// Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG,
                "onUpgrade Db");
    }
}