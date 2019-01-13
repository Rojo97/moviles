package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class RecoverListFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private Spinner listas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recover_list, container, false);
        boton = view.findViewById(R.id.recover_list_button);
        boton.setOnClickListener(this);
        listas = view.findViewById(R.id.select_listas_recover);
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
        SharedPreferences preferencias;
        ArrayList<String> nombreListas = new ArrayList<>();
        RecoverListFragment fragment;

        public GetItems(View view, Context contexto, RecoverListFragment fragment){
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
                String user = preferencias.getString("user", "");

                String subSql = "select * from " + CarroCompraContract.TABLEPARTICIPACION + " where " + CarroCompraContract.ColumnParticipacion.LISTA + " = "
                        + CarroCompraContract.ColumnListaCompra.ID + " and " + CarroCompraContract.ColumnParticipacion.USER + " = '"+user+"'";

                String sql = CarroCompraContract.ColumnListaCompra.STATUS + " = 0"+
                        " and exists ( " + subSql +" )" ;

                Cursor c = getActivity().getContentResolver().query(CarroCompraContract.CONTENT_URI_LISTA, null, sql, null, null);

                String result = getResources().getString(R.string.data_charged);

                while(c.moveToNext()){
                    nombreListas.add(c.getString(0));
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
                Log.e(TAG, "update ListaCompra set estado = 1 where nombre = '"+listName+"' ;");
                st.execute("update ListaCompra set estado = 1 where nombre = '"+listName+"' ;");

                //Se actualiza en la bd local
                ContentValues values = new ContentValues();
                values.clear();
                values.put(CarroCompraContract.ColumnListaCompra.STATUS, 1);

                Uri uri = Uri.parse(CarroCompraContract.CONTENT_URI_LISTA + "/" + listName );
                getActivity().getContentResolver().update(uri, values, null, null);

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
