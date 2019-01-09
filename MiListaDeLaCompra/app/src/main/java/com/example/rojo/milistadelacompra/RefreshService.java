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
    protected void onHandleIntent(Intent intent){ //StartService
        Log.d(TAG,"onStated");

        this.runFlag = true;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String accesstoken = prefs.getString("accesstoken", "");
        String accesstokensecret = prefs.getString("accesstokensecret", "");
        while (runFlag) {
            Log.d(TAG, "Updater running");
            try {
                Log.d(TAG, "Updater ran");
                Thread.sleep(DELAY);
            }
            catch (InterruptedException e) {
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
}