package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

    /**
     * Inicializa el fragment y extrae el nombre de la lista de dode venimos
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_item, container, false);
        boton = view.findViewById(R.id.delete_item_button);
        boton.setOnClickListener(this);
        items = view.findViewById(R.id.select_item);
        Bundle bundle = getArguments(); //Nombre de la lista de donde venimos
        if (bundle != null) {
            listaNombre = bundle.getString("LISTA_NOMBRE");
        }
        GetItems getItems = new GetItems(view, this.getActivity(), this); //Llamamos a una asyc task para obtener los items
        getItems.execute("");
        return view;
    }

    /**
     * Al hacer click se elimina la lisa
     * @param view
     */
    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameItem = items.getSelectedItem().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(nameItem);
    }

    /**
     * Guarda los elementos proporcionados en el AsyncTask en el spinner para poder seleccionarlos
     * @param items
     */
    public void onTaskFinished(ArrayList<String> items) {
        if (items != null) {
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

        public GetItems(View view, Context contexto, DeleteItemFragment fragment) {
            this.view = view;
            this.contexto = contexto;
            this.fragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Obtiene los elementos que se pueden borrar de una lista
         * @param params
         * @return String con un mensaje dependiendo de si se ha podido buscar o no
         */
        @Override
        protected String doInBackground(String... params) {
            String res;
            try {

                String where = ListaCompraContract.ColumnElemento.REMOVED + " = 0";
                Uri uri = Uri.parse(ListaCompraContract.CONTENT_URI_LISTA + "/" + listaNombre + "/Elementos");
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
            fragment.onTaskFinished(nombreItems);

        }
    }


    private class ConnectMySql extends AsyncTask<String, Void, String> {
        private Context contexto;
        private View view;
        private static final String url = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
        private static final String user = "root";
        private static final String pass = "";

        public ConnectMySql(View view, Context contexto) {
            this.view = view;
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, getResources().getString(R.string.saving_data), Toast.LENGTH_SHORT)
                    .show();

        }

        /**
         * Marca como borrado el elemento en la base de datos remosta y local
         * @param params
         * @return String dependiendo de si se ha podido borrar o no el elemento
         */
        @Override
        protected String doInBackground(String... params) {
            String res;
            Log.d(TAG, "Trying to insert " + params[0]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String itemName = params[0];

                Statement st = con.createStatement();
                Log.e(TAG, "update Elemento set eliminado = 1 where nombre = '" + itemName + "' and nombreLista = '" + listaNombre + "';");
                st.execute("update Elemento set eliminado = 1 where nombre = '" + itemName + "' and nombreLista = '" + listaNombre + "';");

                //Se actualiza en la bd local
                ContentValues values = new ContentValues();
                values.clear();
                values.put(ListaCompraContract.ColumnElemento.REMOVED, 1);

                String where = ListaCompraContract.ColumnElemento.ID + " = ? and " + ListaCompraContract.ColumnElemento.IDLISTA + " = ?";
                String[] args = {itemName, listaNombre};
                Uri uri = Uri.parse(ListaCompraContract.CONTENT_URI_LISTA + "/" + listaNombre + "/Elementos/" + itemName);
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
