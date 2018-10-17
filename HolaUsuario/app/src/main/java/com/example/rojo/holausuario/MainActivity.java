package com.example.rojo.holausuario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText txtNombre;
    private Button btnSaludo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNombre = (EditText)findViewById(R.id.Nombretxt);
        btnSaludo = (Button)findViewById(R.id.BotonSaludo);
    }

    public void clickBotonSaludo(View v){
        Intent intent = new Intent(MainActivity.this, SaludoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("NOMBRE", txtNombre.getText().toString());

        intent.putExtras(bundle);
        startActivity(intent);
    }

}
