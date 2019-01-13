package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_item);
            // Comprobar si la actividad ya ha sido creada con anterioridad
            if (savedInstanceState == null) {
                // Crear un fragment
                EditItemFragment fragment = new EditItemFragment();
                fragment.setArguments(this.getIntent().getExtras());
                getFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                        .commit();
            }

        }

}
