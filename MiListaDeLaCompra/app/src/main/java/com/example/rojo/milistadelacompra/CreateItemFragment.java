package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateItemFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private EditText newItem;
    private EditText newQuantity;
    private EditText newPrize;
    private String lista = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_item, container, false);
        boton = view.findViewById(R.id.new_item_button);
        boton.setOnClickListener(this);
        newItem = view.findViewById(R.id.new_item_name);
        newQuantity = view.findViewById(R.id.new_item_quantity);
        newPrize = view.findViewById(R.id.new_item_prize);
        Bundle bundle = getArguments();
        if(bundle != null) {
            lista = bundle.getString("LISTA_NOMBRE");
        }
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameItem = newItem.getText().toString();
        String cantidad = newQuantity.getText().toString();
        String prize = newPrize.getText().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(nameItem, cantidad, prize, lista);
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
            Log.d(TAG, "Trying to insert "+ params[0] +" "+ params[1]+" " + params[2]+" " + params [3]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String name = params[0];
                String cantidad = params[1];
                String prize = params[2];
                String lista = params[3];

                SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
                String user = preferencias.getString("user", "");

                Statement st = con.createStatement();
                Log.e(TAG, "insert into Elemento values ('"+name+"', "+ cantidad + ", "+prize+", '"+lista+"', "+0+", "+0+");");
                st.execute("insert into Elemento values ('"+name+"', "+ cantidad + ", "+prize+", '"+lista+"', "+0+", "+0+");");

                //Guardo el elemento en la bd local
                ContentValues values = new ContentValues();
                values.clear();
                values.put(CarroCompraContract.ColumnElemento.ID, name);
                values.put(CarroCompraContract.ColumnElemento.QUANTITY, cantidad);
                values.put(CarroCompraContract.ColumnElemento.PRICE, prize);
                values.put(CarroCompraContract.ColumnElemento.IDLISTA, lista);
                values.put(CarroCompraContract.ColumnElemento.STATUS, 0);
                values.put(CarroCompraContract.ColumnElemento.REMOVED, 0);
                Uri uri = Uri.parse(CarroCompraContract.CONTENT_URI_LISTA + "/" + lista + "/Elementos");
                getActivity().getContentResolver().insert(uri, values);

                String result = getResources().getString(R.string.data_saved);
                res = result;

            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString() + " Db " + e.getMessage() +" - " +e.getStackTrace());
                res = getResources().getString(R.string.db_duplicate_element);
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
