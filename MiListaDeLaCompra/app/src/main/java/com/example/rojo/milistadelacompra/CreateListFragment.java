package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

/**
 * @author ismpere
 * @author vicrojo
 * Fragment de la vista crear lista
 */
public class CreateListFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private EditText newList;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Inicializa el fragment al crear la vista
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_list, container, false);
        boton = view.findViewById(R.id.new_list_button);
        boton.setOnClickListener(this);
        newList = view.findViewById(R.id.new_list_name);

        if (isAdded()) {
            dbHelper = new DbHelper(getActivity());
        }

        return view;
    }

    /**
     * Llama a ala async task para poder crear la lista
     * @param view
     */
    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String nameList = newList.getText().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(nameList);
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
         * Introduce en la base de datos local y remota la nueva lista
         * @param params
         * @return String con un mensaje dependeiendo si se ha podido introducir o no
         */
        @Override
        protected String doInBackground(String... params) {
            String res;
            Log.d(TAG, "Trying to insert " + params[0]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String listName = params[0];

                if (!listName.equals("")) {
                    SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String user = preferencias.getString("user", "");

                    listName = listName.trim();

                    Statement st = con.createStatement();
                    Log.e(TAG, "insert into ListaCompra values ('" + listName + "', '" + user + "', '" + 1 + ");");
                    st.executeUpdate("insert into ListaCompra values ('" + listName + "', '" + user + "', " + 1 + ");");
                    st.executeUpdate("INSERT INTO Participacion (nickUsuario, nombreLista) VALUES ('" + user + "', '" + listName + "');");


                    //Guardo los datos en la bd local
                    ContentValues values = new ContentValues();
                    values.clear();
                    values.put(ListaCompraContract.ColumnListaCompra.ID, listName);
                    values.put(ListaCompraContract.ColumnListaCompra.USER, user);
                    values.put(ListaCompraContract.ColumnListaCompra.STATUS, 1);
                    getActivity().getContentResolver().insert(ListaCompraContract.CONTENT_URI_LISTA, values);

                    values.clear();
                    values.put(ListaCompraContract.ColumnParticipacion.USER, user);
                    values.put(ListaCompraContract.ColumnParticipacion.LISTA, listName);
                    Uri uri = Uri.parse(ListaCompraContract.CONTENT_URI_LISTA + "/" + listName + "/Participantes");
                    getActivity().getContentResolver().insert(uri, values);

                    String result = getResources().getString(R.string.data_saved);
                    res = result;
                } else {
                    res = getResources().getString(R.string.insert_list_name);
                }


            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                res = getResources().getString(R.string.db_duplicate_list);
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
