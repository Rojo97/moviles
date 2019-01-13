package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ismpere
 * @author vicrojo
 * Implementación del activity para crear un elemento de la lista
 */
public class CreateItemActivity extends AppCompatActivity { //Activity de crear item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            CreateItemFragment fragment = new CreateItemFragment();
            fragment.setArguments(this.getIntent().getExtras()); //Le pasamos el nombre de la lista al fragment
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }

    }

}
