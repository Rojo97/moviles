package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DeleteItemActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_delete_item);
            // Comprobar si la actividad ya ha sido creada con anterioridad
            if (savedInstanceState == null) {
                // Crear un fragment
                DeleteItemFragment fragment = new DeleteItemFragment();
                fragment.setArguments(this.getIntent().getExtras());
                getFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                        .commit();
            }

        }

}
