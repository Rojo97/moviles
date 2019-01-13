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
        super(context, CarroCompraContract.DB_NAME, null, CarroCompraContract.DB_VERSION);
    }

    // Llamado para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG,  " Crea la Db local");

        try{
            db.execSQL("drop table if exists " + CarroCompraContract.TABLEELEMENTO);
            db.execSQL("drop table if exists " + CarroCompraContract.TABLEPARTICIPACION);
            db.execSQL("drop table if exists " + CarroCompraContract.TABLELISTACOMPRA);

            //Consultas para crear las tablas de la bd que encesitamos de la bd remota
            String sqlListaCompra = String.format("create table %s (%s text primary key, %s text, %s int)",
                    CarroCompraContract.TABLELISTACOMPRA,
                    CarroCompraContract.ColumnListaCompra.ID,
                    CarroCompraContract.ColumnListaCompra.USER,
                    CarroCompraContract.ColumnListaCompra.STATUS);

            String sqlParticipacion = String.format("create table %s (%s text, %s text, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    CarroCompraContract.TABLEPARTICIPACION,
                    CarroCompraContract.ColumnParticipacion.USER,
                    CarroCompraContract.ColumnParticipacion.LISTA,
                    CarroCompraContract.ColumnParticipacion.LISTA,
                    CarroCompraContract.TABLELISTACOMPRA,
                    CarroCompraContract.ColumnListaCompra.ID,
                    CarroCompraContract.ColumnParticipacion.USER,
                    CarroCompraContract.ColumnParticipacion.LISTA);

            String sqlElemento = String.format("create table %s (%s text , %s int, %s float, %s text, %s int, %s int, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    CarroCompraContract.TABLEELEMENTO,
                    CarroCompraContract.ColumnElemento.ID,
                    CarroCompraContract.ColumnElemento.QUANTITY,
                    CarroCompraContract.ColumnElemento.PRICE,
                    CarroCompraContract.ColumnElemento.IDLISTA,
                    CarroCompraContract.ColumnElemento.STATUS,
                    CarroCompraContract.ColumnElemento.REMOVED,
                    CarroCompraContract.ColumnElemento.IDLISTA,
                    CarroCompraContract.TABLELISTACOMPRA,
                    CarroCompraContract.ColumnListaCompra.ID,
                    CarroCompraContract.ColumnElemento.ID,
                    CarroCompraContract.ColumnElemento.IDLISTA);

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
        db.execSQL("drop table if exists " + CarroCompraContract.TABLEELEMENTO);
        db.execSQL("drop table if exists " + CarroCompraContract.TABLEPARTICIPACION);
        db.execSQL("drop table if exists " + CarroCompraContract.TABLELISTACOMPRA);
// Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG,
                "onUpgrade Db");
    }
}