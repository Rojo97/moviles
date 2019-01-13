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

/**
 * @author ismpere
 * @@author vicrojo
 * Implementación del servicio para actualizar la base de datos local con los datos remotos
 */
public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    static final int DELAY = 20000; // medio minuto
    private boolean runFlag = false;

    public RefreshService() {
        super(TAG);
    }

    DbHelper dbHelper;
    SQLiteDatabase db;

    /**
     * Implementación del metodo onCreate del servicio
     */
    @Override
    public void onCreate() { //En creación
        super.onCreate();
        Log.d(TAG, "onCreated");

        dbHelper = new DbHelper(this);
    }

    /**
     * Implementacion del metodo onHandleIntent para iniciar el servicio cuando se llame
     * @param intent que lo activa
     */
    @Override
    protected void onHandleIntent(Intent intent) { //StartService
        Log.d(TAG, "onStated");

        this.runFlag = true;

        //Mientras el flag del servicio este activo se actualiza la base de datos local cada 20 segundos (delay)
        while (runFlag) {
            //Se cargan las preferencias de la aplicación para saber de que usuario obtener los datos
            Log.d(TAG, "Updater running Db");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String user = prefs.getString("user", "");
            String pass = prefs.getString("user_password", "");
            try {
                //Se actualiza la bd local y se duerme el hilo durante delay
                updateDB(user, pass);
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                runFlag = false;
            }
        }
    }

    /**
     * Implementacion del metodo onDestroy del servicio
     * Pone el flag de actualización a false
     */
    @Override
    public void onDestroy() { //StopService
        super.onDestroy();

        this.runFlag = false;

        Log.d(TAG, "onDestroyed");
    }

    /**
     * Implementacion del metodo para actualizar la base de datos local entera
     * Si el usuario y la contraseña no coinciden a ningun usuario se eliminan los datos
     * y no se actualiza la base de datos
     * @param user usuario
     * @param pass contraseña
     */
    private void updateDB(String user, String pass) {
        db = dbHelper.getWritableDatabase();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(ListaCompraContract.REMOTEURL, ListaCompraContract.REMOTEUSER, ListaCompraContract.REMOTEPASS);
            System.out.println("Database conection success");
            Log.d(TAG, "Database conection success Db");

            //Se limpian las tablas de la base de datos local
            db.execSQL("delete from " + ListaCompraContract.TABLEPARTICIPACION);
            db.execSQL("delete from " + ListaCompraContract.TABLELISTACOMPRA);
            db.execSQL("delete from " + ListaCompraContract.TABLEELEMENTO);

            //Se comprueba que el usuario existe en la base de datos remota
            Statement st = con.createStatement();
            String sql = "select * from Usuario where nick = '" + user + "' and contrasenia = '" + pass + "';";
            ResultSet rs = st.executeQuery(sql);

            //Si el usuario existe se actualiza la base de datos
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

    /**
     * Implementación del metodo para actualziar las tablas de la base de datos lcoal
     * @param con connection a la base de datos remota
     * @param db base de datos local
     * @param user usuario
     */
    private void updateTables(Connection con, SQLiteDatabase db, String user) {
        try {
            Statement st = con.createStatement();

            //Se exraen las listas de la compra de la base de datos remota
            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.USER, user) + " Db");
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.USER, user));

            ContentValues values = new ContentValues();

            //Se introducen todos los datos obtenidos en las listas de la compra de la base de datos lcoal
            while (rs.next()) {
                String nombreLista = rs.getString("nombreLista");

                // Insertar en la base de datos
                values.clear();
                values.put(ListaCompraContract.ColumnParticipacion.USER, user);
                values.put(ListaCompraContract.ColumnParticipacion.LISTA, nombreLista);
                db.insertWithOnConflict(ListaCompraContract.TABLEPARTICIPACION, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                //Se actualizan todos los datos correspondientes a esa lista
                updateLista(con, db, nombreLista, user);
            }
        } catch (SQLException e) {
            Log.d(TAG, "UPDATE Db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Implementacion del metodo para actualziar una lista de la base de datos local
     * @param con connection a  la base de datos remota
     * @param db base de datos local
     * @param idLista nombre de la lista
     * @param user usuario
     */
    private void updateLista(Connection con, SQLiteDatabase db, String idLista, String user) {
        try {

            Statement st = con.createStatement();

            //Se extrae la lista de la base de datos remota
            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLELISTACOMPRA, ListaCompraContract.ColumnListaCompra.ID, idLista) + " Db");
            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLELISTACOMPRA, ListaCompraContract.ColumnListaCompra.ID, idLista));

            //Se coge el primer elemento del ResultSet
            rs.next();
            String nickUsuario = rs.getString("nickUsuario");
            int estado = rs.getInt("estado");

            //Se extraen las listas con estado 1 o que que las ha creado el usuario
            if (estado == 1 || nickUsuario.equals(user)) {

                //Se instroducen los datos en la base de datos local
                values.clear();
                values.put(ListaCompraContract.ColumnListaCompra.ID, idLista);
                values.put(ListaCompraContract.ColumnListaCompra.USER, nickUsuario);
                values.put(ListaCompraContract.ColumnListaCompra.STATUS, estado);
                db.insertWithOnConflict(ListaCompraContract.TABLELISTACOMPRA, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                //Se actualizan las participaciones y elementos de esa lista
                updateParticipaciones(con, db, idLista, user);
                updateElementos(con, db, idLista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementacion del metodo de actualizacion de las participaciones de una lista
     * @param con connection a la base de datos remota
     * @param db base de datos local
     * @param idLista nombre de la lista
     * @param user usuario
     */
    private void updateParticipaciones(Connection con, SQLiteDatabase db, String idLista, String user) {
        try {

            Statement st = con.createStatement();

            //Se extraen las participaciones de la lista de la base de datos remota
            Log.d(TAG, String.format("select * from %s where %s = '%s' and %s <> '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.LISTA,
                    idLista, ListaCompraContract.ColumnParticipacion.USER, user) + " Db");
            ContentValues values = new ContentValues();

            //Saca los participantes de la lista distintos a user
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s' and %s <> '%s'", ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.ColumnParticipacion.LISTA,
                    idLista, ListaCompraContract.ColumnParticipacion.USER, user));

            //Se introducen las participaciones en la base de datos local
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

    /**
     * Implementacion del metodo de actualizacion de los elementos de la lista en la base de datos local
     * @param con connection a la base de datos remota
     * @param db base de datos local
     * @param idLista nombre de la lista
     */
    private void updateElementos(Connection con, SQLiteDatabase db, String idLista) {
        try {

            Statement st = con.createStatement();

            //Se extraen los elementos de la lista de la base de datos remota
            Log.d(TAG, String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEELEMENTO, ListaCompraContract.ColumnElemento.IDLISTA, idLista) + " Db");

            ContentValues values = new ContentValues();

            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", ListaCompraContract.TABLEELEMENTO, ListaCompraContract.ColumnElemento.IDLISTA, idLista));

            //Se introducen los elementos en la base de datos local
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