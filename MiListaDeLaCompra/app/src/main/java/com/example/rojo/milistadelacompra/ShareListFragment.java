package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShareListFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private EditText userToShare;
    private String lista = "";
    private TextView titulo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_list, container, false);
        boton = view.findViewById(R.id.share_list_button);
        boton.setOnClickListener(this);
        userToShare = view.findViewById(R.id.share_list_user);
        titulo = view.findViewById(R.id.text_share_list);
        String title = titulo.getText().toString();
        String user = userToShare.getText().toString();
        Bundle bundle = getArguments();
        if (bundle != null) {
            lista = bundle.getString("LISTA_NOMBRE");
            title = title + " " + lista;
            //titulo.setText(title);
            //view.refreshDrawableState();
        }
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String user = userToShare.getText().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(user, lista);
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
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            Log.d(TAG, "Trying to insert " + params[0] + " " + params[1]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String user = params[0];
                String lista = params[1];

                String result = "";

                if (!user.equals("")) {

                    user = user.trim();

                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("select * from Usuario where nick= '" + user + "';");

                    if (rs.next()) {
                        String userFound = rs.getString(1);
                        Log.e(TAG, "found " + userFound);
                        Log.e(TAG, "insert into Participacion (nickUsuario, nombreLista) values ('" + user + "', '" + lista + "');");
                        st.execute("insert into Participacion (nickUsuario, nombreLista) values ('" + user + "', '" + lista + "');");

                        //Guardo el elemento en la bd local
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(ListaCompraContract.ColumnParticipacion.USER, user);
                        values.put(ListaCompraContract.ColumnParticipacion.LISTA, lista);
                        Uri uri = Uri.parse(ListaCompraContract.CONTENT_URI_LISTA + "/" + lista + "/Participantes");
                        getActivity().getContentResolver().insert(uri, values);

                        result = getResources().getString(R.string.share_ok);
                    } else {
                        result = getResources().getString(R.string.user_not_found);
                    }
                } else {
                    result = getResources().getString(R.string.insert_user_name);
                }

                res = result;

            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                res = getResources().getString(R.string.db_duplicate_participacion);
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
