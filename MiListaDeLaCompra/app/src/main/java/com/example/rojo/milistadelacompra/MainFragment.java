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
import java.util.ArrayList;


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

        ConnectMySql connectMySql = new ConnectMySql(view, this.getActivity(), this);
        connectMySql.execute("");
        //LinearLayout layout = view.findViewById(R.id.my_lists_buttons);

        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                //LinearLayout.LayoutParams.WRAP_CONTENT);
        /*for (int i = 0; i < botones; i++){
            Button boton = new Button(this.getActivity());
            boton.setLayoutParams(layoutParams);
            boton.setText("Aqui va la lista " + i);
            boton.setId(i);
            boton.setOnClickListener(this);
            layout.addView(boton);

        }*/
        return view;

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        Log.d(TAG,"OnClicked " + view.getTag());
        Intent intentLista = new Intent(view.getContext(), ListaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("LISTA_NOMBRE", view.getTag().toString());
        intentLista.putExtras(bundle);
        startActivity(intentLista);
    }

    public void onTaskFinished(ArrayList<String> listas){
        LinearLayout layout = this.getView().findViewById(R.id.my_lists_buttons);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < listas.size(); i++) {
            Button boton = new Button(this.getActivity());
            boton.setLayoutParams(layoutParams);
            boton.setText(listas.get(i));
            boton.setTag(listas.get(i));
            boton.setOnClickListener(this);
            layout.addView(boton);
        }
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        Context contexto;
        View view;
        MainFragment main;
        ArrayList<String> listas;
        public ConnectMySql(View view, Context contexto, MainFragment main){
            this.view = view;
            this.contexto = contexto;
            this.main = main;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, getResources().getString(R.string.loading_data), Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");


                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from ListaCompra where estado = '1';");
                ResultSetMetaData rsmd = rs.getMetaData();
                String result = getResources().getString(R.string.data_charged);
                listas = new ArrayList<String>();

                while (rs.next()) {
                    listas.add(rs.getString(1));

                }
                res = result;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                res = getResources().getString(R.string.db_error);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, result);
            Toast.makeText(contexto, result, Toast.LENGTH_SHORT)
                    .show();
            main.onTaskFinished(listas);
        }

    }

}