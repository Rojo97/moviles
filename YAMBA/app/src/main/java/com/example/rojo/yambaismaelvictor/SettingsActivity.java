package com.example.rojo.yambaismaelvictor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Comprobar que no se ha creado la actividad antes
        if (savedInstanceState == null) {
// Crear el fragment
            SettingsFragment fragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment,
                            fragment.getClass().getSimpleName())
                    .commit();
        }
    }
}