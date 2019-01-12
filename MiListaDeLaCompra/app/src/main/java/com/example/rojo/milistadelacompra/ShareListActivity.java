package com.example.rojo.milistadelacompra;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ShareListActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_share_list);
            // Comprobar si la actividad ya ha sido creada con anterioridad
            if (savedInstanceState == null) {
                // Crear un fragment
                ShareListFragment fragment = new ShareListFragment();
                fragment.setArguments(this.getIntent().getExtras());
                getFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                        .commit();
            }

        }

}
