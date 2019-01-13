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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class EditItemFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private Spinner items;
    String listaNombre;
    private EditText newQuantity;
    private EditText newPrize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);
        boton = view.findViewById(R.id.edit_item_button);
        boton.setOnClickListener(this);
        items = view.findViewById(R.id.select_item_edit);
        newPrize = view.findViewById(R.id.item_prize);
        newQuantity = view.findViewById(R.id.item_quantity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            listaNombre = bundle.getString("LISTA_NOMBRE");
        }
        GetItems getItems = new GetItems(view, this.getActivity(), this);
        getItems.execute("");
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameItem = items.getSelectedItem().toString();
        String quantity = newQuantity.getText().toString();
        String prize = newPrize.getText().toString();

        new ConnectMySql(this.getView(), this.getActivity()).execute(nameItem, quantity, prize);
    }

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
        EditItemFragment fragment;

        public GetItems(View view, Context contexto, EditItemFragment fragment) {
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

                String where = CarroCompraContract.ColumnElemento.REMOVED + " = 0";
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

        @Override
        protected String doInBackground(String... params) {
            String res;
            Log.d(TAG, "Trying to insert " + params[0] + " " + params[1] + " " + params[2]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String itemName = params[0];
                String quantity = params[1];
                String prize = params[2];


                Statement st = con.createStatement();
                ContentValues values = new ContentValues();
                values.clear();

                String updateCantidad = CarroCompraContract.ColumnElemento.QUANTITY + " = " + quantity;
                String updatePrecio = CarroCompraContract.ColumnElemento.PRICE + " = " + prize;
                String sql = "update Elemento set ";

                if (quantity.equals("") && prize.equals("")) {
                    res = getResources().getString(R.string.insert_something);
                } else if (quantity.equals("") || prize.equals("")) {
                    if (quantity.equals("")) {
                        sql = sql + updatePrecio;
                        values.put(CarroCompraContract.ColumnElemento.PRICE, prize);
                    } else {
                        sql = sql + updateCantidad;
                        values.put(CarroCompraContract.ColumnElemento.QUANTITY, quantity);
                    }
                } else {
                    sql = sql + updateCantidad + " and " + updatePrecio;
                    values.put(CarroCompraContract.ColumnElemento.PRICE, prize);
                    values.put(CarroCompraContract.ColumnElemento.QUANTITY, quantity);
                }


                //TODO Update en local y en remoto
                String w = " where " + CarroCompraContract.ColumnElemento.IDLISTA + " = '"+ listaNombre +
                        "' and " + CarroCompraContract.ColumnElemento.ID + " = '" + itemName + "' ";

                Log.e(TAG, sql + w + ";");
                st.execute(sql + w + ";");

                //Se actualiza en la bd local
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