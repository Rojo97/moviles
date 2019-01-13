/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ListaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
        // Crear un fragment
            ListaFragment fragment = new ListaFragment();
            fragment.setArguments(this.getIntent().getExtras());
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }

    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_element:
                Intent intentAdd = new Intent(this, CreateItemActivity.class);
                intentAdd.putExtras(this.getIntent().getExtras());
                startActivity(intentAdd);
                return true;
            case R.id.recover_element:
                Intent intentRecover = new Intent(this, RecoverItemActivity.class);
                intentRecover.putExtras(this.getIntent().getExtras());
                startActivity(intentRecover);
                return true;
            case R.id.share_list:
                Intent intentShare = new Intent(this, ShareListActivity.class);
                intentShare.putExtras(this.getIntent().getExtras());
                startActivity(intentShare);
                return true;
            case R.id.delete_element:
                Intent intentDelete = new Intent(this, DeleteItemActivity.class);
                intentDelete.putExtras(this.getIntent().getExtras());
                startActivity(intentDelete);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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