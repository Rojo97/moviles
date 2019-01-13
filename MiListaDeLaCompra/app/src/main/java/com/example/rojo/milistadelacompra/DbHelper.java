/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * @author ismpere
 * @author vicrojo
 * Implementación del SQLiteOpenHelper para la creación y actualización de la base de datos lcoal
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    /**
     * Constructor por defecto de la clase DBHelper
     * @param context contexto
     */
    public DbHelper(Context context) {
        super(context, ListaCompraContract.DB_NAME, null, ListaCompraContract.DB_VERSION);
    }

    /**
     * Implementación del método onCreate que elimina las tablas de la base de datos local y las crea de nuevo con los atributos necesarios
     * @param db base de datos de SQLite
     */
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

    /**
     * Implementación del método de actualización de la base de datos local
     * Elimina las tablas y vuelve a crear la base de datos
     */
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