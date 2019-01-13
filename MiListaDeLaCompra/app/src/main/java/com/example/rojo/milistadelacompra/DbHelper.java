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
        super(context, ListaCompraContract.DB_NAME, null, ListaCompraContract.DB_VERSION);
    }

    // Llamado para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, " Crea la Db local");

        try {
            db.execSQL("drop table if exists " + ListaCompraContract.TABLEELEMENTO);
            db.execSQL("drop table if exists " + ListaCompraContract.TABLEPARTICIPACION);
            db.execSQL("drop table if exists " + ListaCompraContract.TABLELISTACOMPRA);

            //Consultas para crear las tablas de la bd que encesitamos de la bd remota
            String sqlListaCompra = String.format("create table %s (%s text primary key, %s text, %s int)",
                    ListaCompraContract.TABLELISTACOMPRA,
                    ListaCompraContract.ColumnListaCompra.ID,
                    ListaCompraContract.ColumnListaCompra.USER,
                    ListaCompraContract.ColumnListaCompra.STATUS);

            String sqlParticipacion = String.format("create table %s (%s text, %s text, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    ListaCompraContract.TABLEPARTICIPACION,
                    ListaCompraContract.ColumnParticipacion.USER,
                    ListaCompraContract.ColumnParticipacion.LISTA,
                    ListaCompraContract.ColumnParticipacion.LISTA,
                    ListaCompraContract.TABLELISTACOMPRA,
                    ListaCompraContract.ColumnListaCompra.ID,
                    ListaCompraContract.ColumnParticipacion.USER,
                    ListaCompraContract.ColumnParticipacion.LISTA);

            String sqlElemento = String.format("create table %s (%s text , %s int, %s float, %s text, %s int, %s int, foreign key (%s) references %s (%s), primary key (%s, %s))",
                    ListaCompraContract.TABLEELEMENTO,
                    ListaCompraContract.ColumnElemento.ID,
                    ListaCompraContract.ColumnElemento.QUANTITY,
                    ListaCompraContract.ColumnElemento.PRICE,
                    ListaCompraContract.ColumnElemento.IDLISTA,
                    ListaCompraContract.ColumnElemento.STATUS,
                    ListaCompraContract.ColumnElemento.REMOVED,
                    ListaCompraContract.ColumnElemento.IDLISTA,
                    ListaCompraContract.TABLELISTACOMPRA,
                    ListaCompraContract.ColumnListaCompra.ID,
                    ListaCompraContract.ColumnElemento.ID,
                    ListaCompraContract.ColumnElemento.IDLISTA);

            Log.d(TAG, "onCreate con SQL Db: " + sqlParticipacion);
            Log.d(TAG, "onCreate con SQL Db: " + sqlListaCompra);
            Log.d(TAG, "onCreate con SQL Db: " + sqlElemento);
            db.execSQL(sqlListaCompra);
            db.execSQL(sqlParticipacion);
            db.execSQL(sqlElemento);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage() + " Db");
        }
    }

    // Llamado siempre que tengamos una nueva version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

// Aqui irían las sentencias del tipo ALTER TABLE, de momento lo hacemosmas sencillo...
// Borramos la vieja base de datos
        db.execSQL("drop table if exists " + ListaCompraContract.TABLEELEMENTO);
        db.execSQL("drop table if exists " + ListaCompraContract.TABLEPARTICIPACION);
        db.execSQL("drop table if exists " + ListaCompraContract.TABLELISTACOMPRA);
// Creamos una base de datos nueva
        onCreate(db);
        Log.d(TAG,
                "onUpgrade Db");
    }
}