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

public class DeleteItemFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private Spinner items;
    String listaNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_delete_item, container, false);
        boton = view.findViewById(R.id.delete_item_button);
        boton.setOnClickListener(this);
        items = view.findViewById(R.id.select_item);
        Bundle bundle = getArguments();
        if(bundle != null) {
            listaNombre = bundle.getString("LISTA_NOMBRE");
        }
        GetItems getListas = new GetItems(view, this.getActivity(), this);
        getListas.execute("");
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameItem = items.getSelectedItem().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(nameItem);
    }

    public void onTaskFinished(ArrayList<String> items){
        if(items!=null){
            String[] nombreItems = new String[items.size()];
            nombreItems = items.toArray(nombreItems);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, nombreItems);
            this.items.setAdapter(adapter);
            }
        }

    private class GetItems extends AsyncTask<String, Void, String> {
        private Context contexto;
        private View view;
        ArrayList<String> nombreItems = new ArrayList<String>();
        DeleteItemFragment fragment;

        public GetItems(View view, Context contexto, DeleteItemFragment fragment){
            this.view = view;
            this.contexto = contexto;
            this.fragment = fragment;
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

                String where = CarroCompraContract.ColumnElemento.REMOVED +" = 0";
                Uri uri = Uri.parse(CarroCompraContract.CONTENT_URI_LISTA + "/" + listaNombre + "/Elementos");
                Cursor c = getActivity().getContentResolver().query(uri, null, where, null, null);

                String result = getResources().getString(R.string.data_charged);

                while (c.moveToNext()) {
                    nombreItems.add(c.getString(0));
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
            fragment.onTaskFinished(nombreItems);

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

                String itemName = params[0];

                Statement st = con.createStatement();
                Log.e(TAG, "update Elemento set eliminado = 1 where nombre = '"+itemName+"' and nombreLista = '"+listaNombre+"';");
                st.execute("update Elemento set eliminado = 1 where nombre = '"+itemName+"' and nombreLista = '"+listaNombre+"';");

                //Se actualiza en la bd local
                ContentValues values = new ContentValues();
                values.clear();
                values.put(CarroCompraContract.ColumnElemento.REMOVED, 1);

                String where = CarroCompraContract.ColumnElemento.ID + " = ? and " + CarroCompraContract.ColumnElemento.IDLISTA + " = ?";
                String[] args = {itemName, listaNombre};
                Uri uri = Uri.parse(CarroCompraContract.CONTENT_URI_LISTA + "/" + listaNombre + "/Elementos/" + itemName);
                getActivity().getContentResolver().update(uri, values, where, args);

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
