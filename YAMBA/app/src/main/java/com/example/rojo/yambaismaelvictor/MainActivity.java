/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.yambaismaelvictor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_tweet:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.itemServiceStart:
                startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.itemServiceStop:
                stopService(new Intent(this, RefreshService.class));
                return true;
            default:
                return false;
        }
    }
}
