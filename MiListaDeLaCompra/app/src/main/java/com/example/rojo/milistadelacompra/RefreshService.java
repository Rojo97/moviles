/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    static final int DELAY = 30000; // medio minuto
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accesstoken = prefs.getString("accesstoken", "");
        String accesstokensecret = prefs.getString("accesstokensecret", "");
        while (runFlag) {
            Log.d(TAG, "Updater running");
            try {
                // Iteramos sobre todos los componentes de timeline
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(StatusContract.REMOTEURL, StatusContract.REMOTEUSER, StatusContract.REMOTEPASS);
                    System.out.println("Database conection success");

                    Statement st = con.createStatement();

                    //Se actualiza cada tabla de la bd
                    updateParticipaciones(st);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

                for (Status status : timeline) {
                    // Imprimimos las actualizaciones en el log
                    Log.d(TAG, String.format("%s: %s", status.getUser().getName(),
                            status.getText()));
                    // Insertar en la base de datos
                    values.clear();
                    values.put(StatusContract.Column.ID, status.getId());
                    values.put(StatusContract.Column.USER, status.getUser().getName());
                    values.put(StatusContract.Column.MESSAGE, status.getText());
                    values.put(StatusContract.Column.CREATED_AT,
                            status.getCreatedAt().getTime());
                    db.insertWithOnConflict(StatusContract.TABLE, null, values,
                            SQLiteDatabase.CONFLICT_IGNORE);
                }
                // Cerrar la base de datos
                db.close();
                Log.d(TAG, "Updater ran");
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

    private void updateParticipaciones(Statement st){
        try {
            ResultSet rs = st.executeQuery(String.format("select * from %s where %s = '%s'", StatusContract.TABLEPARTICIPACION, StatusContract.ColumnParticipacion.USER, "hola"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}