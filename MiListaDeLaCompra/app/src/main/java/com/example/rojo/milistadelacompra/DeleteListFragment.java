package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class DeleteListFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private Spinner listas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_delete_list, container, false);
        boton = view.findViewById(R.id.delete_list_button);
        boton.setOnClickListener(this);
        listas = view.findViewById(R.id.select_listas);
        GetItems getListas = new GetItems(view, this.getActivity(), this);
        getListas.execute("");
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameList = listas.getSelectedItem().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(nameList);
    }
    public void onTaskFinished(ArrayList<String> listas){
        if(listas!=null){
            String[] nombrelistas = new String[listas.size()];
            nombrelistas = listas.toArray(nombrelistas);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, nombrelistas);
            this.listas.setAdapter(adapter);
            }
        }

    private class GetItems extends AsyncTask<String, Void, String> {
        private Context contexto;
        private View view;
        private static final String url = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
        private static final String user = "root";
        private static final String pass = "";
        SharedPreferences preferencias;
        ArrayList<String> nombreListas = new ArrayList<String>();
        DeleteListFragment fragment;

        public GetItems(View view, Context contexto, DeleteListFragment fragment){
            this.view = view;
            this.contexto = contexto;
            this.fragment = fragment;
            preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, getResources().getString(R.string.saving_data), Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String user = preferencias.getString("user", "");


                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from ListaCompra where nickUsuario = '"+user+"' and estado = '1';");
                ResultSetMetaData rsmd = rs.getMetaData();
                String result = getResources().getString(R.string.data_charged);

                while (rs.next()) {
                    nombreListas.add(rs.getString(1));
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
            fragment.onTaskFinished(nombreListas);

        }
    }


    private class ConnectMySql extends AsyncTask<String, Void, String> {
        private Context contexto;
        private View view;
        private static final String url = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
        private static final String user = "root";
        private static final String pass = "";

        public ConnectMySql(View view, Context contexto){
            this.view = view;
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, getResources().getString(R.string.saving_data), Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            Log.d(TAG, "Trying to insert "+ params[0]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String listName = params[0];
                SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
                String user = preferencias.getString("user", "");

                Statement st = con.createStatement();
                Log.e(TAG, "update ListaCompra set estado = 0 where nombre = '"+listName+"' and nickUsuario = '"+user+"';");
                st.execute("update ListaCompra set estado = 0 where nombre = '"+listName+"' and nickUsuario = '"+user+"';");
                String result = getResources().getString(R.string.data_saved);
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
        }
    }
}
