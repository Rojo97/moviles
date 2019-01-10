/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = MainFragment.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT};
    private static final int LOADER_ID = 42;
    private static final String url = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
    private static final String user = "root";
    private static final String pass = "";
    String res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int botones = 5;
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayout layout = view.findViewById(R.id.my_lists_buttons);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < botones; i++){
            Button boton = new Button(this.getActivity());
            boton.setLayoutParams(layoutParams);
            boton.setText("Aqui va la lista " + i);
            boton.setId(i);
            boton.setOnClickListener(this);
            layout.addView(boton);

        }
        return view;

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        ConnectMySql connectMySql = new ConnectMySql(this.getView(), this.getActivity());
        connectMySql.execute("");
        Log.d(TAG,"OnClicked " + i);
        Intent intentLista = new Intent(view.getContext(), ListaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("PULSADO", i);
        intentLista.putExtras(bundle);
        startActivity(intentLista);
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        Context contexto;
        View view;
        public ConnectMySql(View view, Context contexto){
            this.view = view;
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, "Please wait...", Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databaseection success");

                String result = "Database Connection Successful\n";
                Statement st = con.createStatement();
                st.execute("INSERT INTO ListaCompra VALUES ('listaDeLaApp', 'victor');");
                //ResultSet rs = st.executeQuery("select * from Usuario");

                //ResultSetMetaData rsmd = rs.getMetaData();

                //while (rs.next()) {
                //    result += rs.getString(1) + "\n";
                //}
                //res = result;
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, result);
        }
    }

}