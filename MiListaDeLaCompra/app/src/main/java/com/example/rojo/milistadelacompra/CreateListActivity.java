package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

public class CreateListActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_list);
            // Comprobar si la actividad ya ha sido creada con anterioridad
            if (savedInstanceState == null) {
                // Crear un fragment
                CreateListFragment fragment = new CreateListFragment();
                getFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                        .commit();
            }

        }

}
