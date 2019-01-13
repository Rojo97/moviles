/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity de la vista de una lista
 */
public class ListaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            ListaFragment fragment = new ListaFragment();
            fragment.setArguments(this.getIntent().getExtras()); //Le pasamos el nombre de la lista
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }

    }

    /**
     * Al volver de otra vista refresca la vista
     */
    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /**
     * Crea el menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    /**
     * Controla las distintas opciones del menú
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload: //Recargar la vista
                finish();
                startActivity(getIntent());
                return true;
            case R.id.add_element:
                Intent intentAdd = new Intent(this, CreateItemActivity.class);
                intentAdd.putExtras(this.getIntent().getExtras()); //Pasamos el nombre de la lista
                startActivity(intentAdd);
                return true;
            case R.id.recover_element:
                Intent intentRecover = new Intent(this, RecoverItemActivity.class);
                intentRecover.putExtras(this.getIntent().getExtras()); //Pasamos el nombre de la lista
                startActivity(intentRecover);
                return true;
            case R.id.share_list:
                Intent intentShare = new Intent(this, ShareListActivity.class);
                intentShare.putExtras(this.getIntent().getExtras()); //Pasamos el nombre de la lista
                startActivity(intentShare);
                return true;
            case R.id.delete_element:
                Intent intentDelete = new Intent(this, DeleteItemActivity.class);
                intentDelete.putExtras(this.getIntent().getExtras()); //Pasamos el nombre de la lista
                startActivity(intentDelete);
                return true;
            case R.id.edit_element:
                Intent intentEdit = new Intent(this, EditItemActivity.class);
                intentEdit.putExtras(this.getIntent().getExtras()); //Pasamos el nombre de la lista
                startActivity(intentEdit);
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