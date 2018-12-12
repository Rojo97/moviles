/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.yambaismaelvictor;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey("bU3bzE1KJjjJhHa3kvlpIzWlw")
                        .setOAuthConsumerSecret("P6XKEk1kJtzC3Vw48NExav3iQG7gjCkXh1otNhDn0pPNpMRE4Q")
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesstokensecret);
                TwitterFactory factory = new TwitterFactory(builder.build());
                Twitter twitter = factory.getInstance();
                try {
                    List<Status> timeline = twitter.getHomeTimeline();
                    // Iteramos sobre todos los componentes de timeline
                    ContentValues values = new ContentValues();
                // Imprimimos las actualizaciones en el log
                    for (Status status : timeline) {
                        Log.d(TAG, String.format("%s: %s", status.getUser().getName(),
                                status.getText()));
                        // Insertar en la base de datos
                        values.clear();
                        values.put(StatusContract.Column.ID, status.getId());
                        values.put(StatusContract.Column.USER, status.getUser().getName());
                        values.put(StatusContract.Column.MESSAGE, status.getText());
                        values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
                        Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                    }
                }
                catch (TwitterException e) {
                    Log.e(TAG, "Failed to fetch the timeline", e);
                }
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