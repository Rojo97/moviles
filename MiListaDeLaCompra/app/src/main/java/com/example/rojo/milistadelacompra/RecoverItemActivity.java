package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity de la vista recuperar elemento
 */
public class RecoverItemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_item);
        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            RecoverItemFragment fragment = new RecoverItemFragment();
            fragment.setArguments(this.getIntent().getExtras()); //Le pasamos el nombre de la lista
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }

    }

}
