/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    static final int DELAY = 20000; // medio minuto
    private boolean runFlag = false;

    public RefreshService() {
        super(TAG);
    }

    DbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public void onCreate() { //En creación
        super.onCreate();
        Log.d(TAG, "onCreated");

        dbHelper = new DbHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) { //StartService
        Log.d(TAG, "onStated");

        this.runFlag = true;

        while (runFlag) {
            Log.d(TAG, "Updater running Db");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String user = prefs.getString("user", "");
            try {
                updateDB(user);
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                runFlag = false;
            }
        }
    }

    @Override
    public void onDestroy() { //StopService
        super.onDestroy();

        this.runFlag = false;

        Log.d(TAG, "onDestroyed");
    }

    private void updateDB(String user){
        // Iteramos sobre todos los componentes de timeline
        db = dbHelper.getWritableDatabase();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(CarroCompraContract.REMOTEURL, CarroCompraContract.REMOTEUSER, CarroCompraContract.REMOTEPASS);
            System.out.println("Database conection success");
            Log.d(TAG, "Database conection success Db");

            //Se actualiza la bd local
            updateTables(con, db, user);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        // Cerrar la base de datos
        db.close();
        Log.d(TAG, "Updater ran Db");
    }

    /*private void updateDB(Intent intent){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString("user", "");

        updateDB(user);
    }*/

    private void updateTables(Connection con, SQLiteDatabase db, String user){
        try {
            Statement st = con.createStatement();

            Log.d(TAG,String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLEPARTICIPACION, CarroCompraContract.ColumnParticipacion.USER, user)+" Db");
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLEPARTICIPACION, CarroCompraContract.ColumnParticipacion.USER, user));

            db.execSQL("delete from " + CarroCompraContract.TABLEPARTICIPACION);
            db.execSQL("delete from " + CarroCompraContract.TABLELISTACOMPRA);
            db.execSQL("delete from " + CarroCompraContract.TABLEELEMENTO);

            // Iteramos sobre todos los componentes de timeline
            ContentValues values = new ContentValues();

            while(rs.next()){
                String nombreLista = rs.getString("nombreLista");

                // Insertar en la base de datos
                values.clear();
                values.put(CarroCompraContract.ColumnParticipacion.USER, user);
                values.put(CarroCompraContract.ColumnParticipacion.LISTA, nombreLista);
                db.insertWithOnConflict(CarroCompraContract.TABLEPARTICIPACION, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                updateLista(con, db, nombreLista, user);
            }
        } catch (SQLException e) {
            Log.d(TAG, "UPDATE Db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLista(Connection con, SQLiteDatabase db, String idLista, String user){
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLELISTACOMPRA, CarroCompraContract.ColumnListaCompra.ID, idLista) + " Db");
            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLELISTACOMPRA, CarroCompraContract.ColumnListaCompra.ID, idLista));

            //Se coge el primer elemento del ResultSet
            rs.next();
            String nickUsuario = rs.getString("nickUsuario");
            int estado = rs.getInt("estado");

            if(estado==1 || nickUsuario.equals(user)){

                values.clear();
                values.put(CarroCompraContract.ColumnListaCompra.ID, idLista);
                values.put(CarroCompraContract.ColumnListaCompra.USER, nickUsuario);
                values.put(CarroCompraContract.ColumnListaCompra.STATUS, estado);
                db.insertWithOnConflict(CarroCompraContract.TABLELISTACOMPRA, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                updateParticipaciones(con, db, idLista, user);
                updateElementos(con, db, idLista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateParticipaciones(Connection con, SQLiteDatabase db, String idLista, String user){
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s' and %s <> '%s'", CarroCompraContract.TABLEPARTICIPACION, CarroCompraContract.ColumnParticipacion.LISTA,
                    idLista, CarroCompraContract.ColumnParticipacion.USER, user) + " Db");
            ContentValues values = new ContentValues();

            //Saca los participantes de la lista distintos a user
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s' and %s <> '%s'", CarroCompraContract.TABLEPARTICIPACION, CarroCompraContract.ColumnParticipacion.LISTA,
                    idLista, CarroCompraContract.ColumnParticipacion.USER, user));

            while(rs.next()){
                String nickUsuario = rs.getString("nickUsuario");

                // Imprimimos las actualizaciones en el log
                Log.d(TAG, String.format("AAAA %s: %s Db", nickUsuario, idLista));
                // Insertar en la base de datos

                values.clear();
                values.put(CarroCompraContract.ColumnParticipacion.USER, nickUsuario);
                values.put(CarroCompraContract.ColumnParticipacion.LISTA, idLista);
                db.insertWithOnConflict(CarroCompraContract.TABLEPARTICIPACION, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

        } catch (SQLException e) {
            Log.d(TAG, "Db " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateElementos(Connection con, SQLiteDatabase db, String idLista){
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLEELEMENTO, CarroCompraContract.ColumnElemento.IDLISTA, idLista) + " Db");

            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", CarroCompraContract.TABLEELEMENTO, CarroCompraContract.ColumnElemento.IDLISTA, idLista));

            while(rs.next()){
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                float precioUnidad = rs.getFloat("precioUnidad");
                int estado = rs.getInt("estado");



                values.clear();
                values.put(CarroCompraContract.ColumnElemento.ID, nombre);
                values.put(CarroCompraContract.ColumnElemento.QUANTITY, cantidad);
                values.put(CarroCompraContract.ColumnElemento.PRICE, precioUnidad);
                values.put(CarroCompraContract.ColumnElemento.IDLISTA, idLista);
                values.put(CarroCompraContract.ColumnElemento.STATUS, estado);
                db.insertWithOnConflict(CarroCompraContract.TABLEELEMENTO, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}