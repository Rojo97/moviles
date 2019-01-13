/*Victor Rojo Alvarez
* Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class ListaFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ListaFragment.class.getSimpleName();
    private TextView nombreTextview;
    private String listaNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        nombreTextview = view.findViewById(R.id.nombre_lista);
        Bundle bundle = getArguments();
        ConnectMySql conection = new ConnectMySql(view, this.getActivity(), this);
        conection.execute();
        if(bundle != null) {
            listaNombre = bundle.getString("LISTA_NOMBRE");
            nombreTextview.setText(listaNombre);
        }

        return view;
    }

    public void onTaskFinished(ArrayList<String> productos){
        LinearLayout layout = this.getView().findViewById(R.id.my_products);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if(productos!=null){
            for (int i = 0; i < productos.size(); i++) {
                CheckBox item = new CheckBox(this.getActivity());
                item.setLayoutParams(layoutParams);
                item.setText(productos.get(i));
                item.setTag(productos.get(i));
                item.setOnClickListener(this);
                item.setTextSize(25);
                layout.addView(item);
                this.getView().destroyDrawingCache();
                this.getView().refreshDrawableState();
            }
        }
    }

    @Override
    public void onClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String tag = view.getTag().toString();
        String estado;
        if(checked == true){
            estado = "1";
        }else{
            estado = "0";
        }
        new UpdateElement(this.getView(),this.getActivity(), this).execute(tag, estado);
    }

    private class UpdateElement extends AsyncTask<String, Void, String> {
        String res = "";

        Context contexto;
        View view;
        ListaFragment fragment;
        ArrayList<String> productos;
        private static final String url = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
        private static final String user = "root";
        private static final String pass = "";

        public UpdateElement(View view, Context contexto, ListaFragment fragment){
            this.view = view;
            this.contexto = contexto;
            this.fragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(contexto, getResources().getString(R.string.loading_data), Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String nombre = params[0];
                String estado = params[1];

                Statement st = con.createStatement();
                Log.e(TAG,"update Elemento set estado = "+ estado +" where nombre = '" +nombre+"' and  nombreLista = '"+listaNombre+"';");
                st.execute("update Elemento set estado = "+ estado +" where nombre = '" +nombre+"' and  nombreLista = '"+listaNombre+"';");
                String result = getResources().getString(R.string.data_charged);

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
            fragment.onTaskFinished(productos);
        }
    }


    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        Context contexto;
        View view;
        ListaFragment fragment;
        ArrayList<String> productos;

        public ConnectMySql(View view, Context contexto, ListaFragment fragment){
            this.view = view;
            this.contexto = contexto;
            this.fragment = fragment;
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

                String where = CarroCompraContract.ColumnElemento.REMOVED +" = 0";
                Uri uri = Uri.parse(CarroCompraContract.CONTENT_URI_LISTA + "/" + listaNombre + "/Elementos");
                Cursor c = getActivity().getContentResolver().query(uri, null, where, null, null);

                String result = getResources().getString(R.string.data_charged);
                productos = new ArrayList<>();

                while(c.moveToNext()){
                    productos.add(c.getString(0));
                }

                res = result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString() + " Db " + e.getMessage());
                res = getResources().getString(R.string.db_error);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, result);
            Toast.makeText(contexto, result, Toast.LENGTH_SHORT)
                    .show();
            fragment.onTaskFinished(productos);
        }
    }

}
