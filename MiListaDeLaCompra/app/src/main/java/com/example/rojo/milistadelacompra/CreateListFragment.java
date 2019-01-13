package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

public class CreateListFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private EditText newList;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_create_list, container, false);
        boton = view.findViewById(R.id.new_list_button);
        boton.setOnClickListener(this);
        newList = view.findViewById(R.id.new_list_name);

        if(isAdded()){
            dbHelper = new DbHelper(getActivity());
        }

        return view;
    }


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

                db = dbHelper.getWritableDatabase();

                String listName = params[0];
                SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
                String user = preferencias.getString("user", "");

                Statement st = con.createStatement();
                Log.e(TAG, "insert into ListaCompra values ('"+listName+"', '"+ user + "', '"+1+");");
                st.executeUpdate("insert into ListaCompra values ('"+listName+"', '"+ user + "', "+1+");");
                st.executeUpdate("INSERT INTO Participacion (nickUsuario, nombreLista) VALUES ('"+user+"', '"+listName+"');");

                //Se añade la lista en la bd local
                String sql = String.format("insert into %s (%s, %s, %s) values ('%s', '%s', %d)", CarroCompraContract.TABLELISTACOMPRA,
                        CarroCompraContract.ColumnListaCompra.ID, CarroCompraContract.ColumnListaCompra.USER, CarroCompraContract.ColumnListaCompra.STATUS,
                        listName, user, 1);
                db.execSQL(sql);

                String result = getResources().getString(R.string.data_saved);
                res = result;

            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                res = getResources().getString(R.string.db_duplicate_list);
            }catch (Exception e) {
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
