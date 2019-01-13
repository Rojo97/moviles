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
            String pass = prefs.getString("user_password", "");
            try {
                updateDB(user, pass);
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

    private void updateDB(String user, String pass) {
        // Iteramos sobre todos los componentes de timeline
        db = dbHelper.getWritableDatabase();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(ListaCompraContract.REMOTEURL, ListaCompraContract.REMOTEUSER, ListaCompraContract.REMOTEPASS);
            System.out.println("Database conection success");
            Log.d(TAG, "Database conection success Db");

            db.execSQL("delete from " + ListaCompraContract.TABLEPARTICIPACION);
            db.execSQL("delete from " + ListaCompraContract.TABLELISTACOMPRA);
            db.execSQL("delete from " + ListaCompraContract.TABLEELEMENTO);

            Statement st = con.createStatement();
            String sql = "select * from Usuario where nick = '" + user + "' and contrasenia = '" + pass + "';";
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                //Se actualiza la bd local
                updateTables(con, db, user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        // Cerrar la base de datos
        db.close();
        Log.d(TAG, "Updater ran Db");
    }

    private void updateTables(Connection con, SQLiteDatabase db, String user) {
        try {
            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.USER, user) + " Db");
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.USER, user));

            // Iteramos sobre todos los componentes de timeline
            ContentValues values = new ContentValues();

            while (rs.next()) {
                String nombreLista = rs.getString("nombreLista");

                // Insertar en la base de datos
                values.clear();
                values.put(ListaCompraContract.ColumnParticipacion.USER, user);
                values.put(ListaCompraContract.ColumnParticipacion.LISTA, nombreLista);
                db.insertWithOnConflict(ListaCompraContract.TABLEPARTICIPACION, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                updateLista(con, db, nombreLista, user);
            }
        } catch (SQLException e) {
            Log.d(TAG, "UPDATE Db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLista(Connection con, SQLiteDatabase db, String idLista, String user) {
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLELISTACOMPRA, ListaCompraContract.ColumnListaCompra.ID, idLista) + " Db");
            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLELISTACOMPRA, ListaCompraContract.ColumnListaCompra.ID, idLista));

            //Se coge el primer elemento del ResultSet
            rs.next();
            String nickUsuario = rs.getString("nickUsuario");
            int estado = rs.getInt("estado");

            if (estado == 1 || nickUsuario.equals(user)) {

                values.clear();
                values.put(ListaCompraContract.ColumnListaCompra.ID, idLista);
                values.put(ListaCompraContract.ColumnListaCompra.USER, nickUsuario);
                values.put(ListaCompraContract.ColumnListaCompra.STATUS, estado);
                db.insertWithOnConflict(ListaCompraContract.TABLELISTACOMPRA, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                updateParticipaciones(con, db, idLista, user);
                updateElementos(con, db, idLista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateParticipaciones(Connection con, SQLiteDatabase db, String idLista, String user) {
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s' and %s <> '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.LISTA,
                    idLista, ListaCompraContract.ColumnParticipacion.USER, user) + " Db");
            ContentValues values = new ContentValues();

            //Saca los participantes de la lista distintos a user
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s' and %s <> '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.LISTA,
                    idLista, ListaCompraContract.ColumnParticipacion.USER, user));

            while (rs.next()) {
                String nickUsuario = rs.getString("nickUsuario");

                // Imprimimos las actualizaciones en el log
                Log.d(TAG, String.format("AAAA %s: %s Db", nickUsuario, idLista));
                // Insertar en la base de datos

                values.clear();
                values.put(ListaCompraContract.ColumnParticipacion.USER, nickUsuario);
                values.put(ListaCompraContract.ColumnParticipacion.LISTA, idLista);
                db.insertWithOnConflict(ListaCompraContract.TABLEPARTICIPACION, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

        } catch (SQLException e) {
            Log.d(TAG, "Db " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateElementos(Connection con, SQLiteDatabase db, String idLista) {
        try {

            Statement st = con.createStatement();

            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEELEMENTO, ListaCompraContract.ColumnElemento.IDLISTA, idLista) + " Db");

            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEELEMENTO, ListaCompraContract.ColumnElemento.IDLISTA, idLista));

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                float precioUnidad = rs.getFloat("precioUnidad");
                int estado = rs.getInt("estado");
                int eliminado = rs.getInt("eliminado");


                values.clear();
                values.put(ListaCompraContract.ColumnElemento.ID, nombre);
                values.put(ListaCompraContract.ColumnElemento.QUANTITY, cantidad);
                values.put(ListaCompraContract.ColumnElemento.PRICE, precioUnidad);
                values.put(ListaCompraContract.ColumnElemento.IDLISTA, idLista);
                values.put(ListaCompraContract.ColumnElemento.STATUS, estado);
                values.put(ListaCompraContract.ColumnElemento.REMOVED, eliminado);
                db.insertWithOnConflict(ListaCompraContract.TABLEELEMENTO, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}