/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import android.database.sqlite.SQLiteDatabase;


public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = MainFragment.class.getSimpleName();
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ConnectMySql connectMySql = new ConnectMySql(view, this.getActivity(), this);
        connectMySql.execute("");

        if(isAdded()){
            dbHelper = new DbHelper(getActivity());
        }
        return view;

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        Log.d(TAG,"OnClicked " + view.getTag());
        Intent intentLista = new Intent(view.getContext(), ListaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("LISTA_NOMBRE", view.getTag().toString());
        intentLista.putExtras(bundle);
        startActivity(intentLista);
    }

    public void onTaskFinished(ArrayList<String> listas){
        LinearLayout layout = this.getView().findViewById(R.id.my_lists_buttons);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if(listas!=null){
            for (int i = 0; i < listas.size(); i++) {
                Button boton = new Button(this.getActivity());
                boton.setLayoutParams(layoutParams);
                boton.setText(listas.get(i));
                boton.setTag(listas.get(i));
                boton.setOnClickListener(this);
                layout.addView(boton);
            }
        }
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        Context contexto;
        View view;
        MainFragment main;
        ArrayList<String> listas;
        SharedPreferences preferencias;

        public ConnectMySql(View view, Context contexto, MainFragment main){
            this.view = view;
            this.contexto = contexto;
            this.main = main;
            preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);
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
                db = dbHelper.getReadableDatabase();

                String sql = "select * from " + StatusContract.TABLELISTACOMPRA;
                String[] args = {};
                Cursor c = db.rawQuery(sql,  args);

                String result = getResources().getString(R.string.data_charged);
                listas = new ArrayList<String>();

                while(c.moveToNext()){
                    listas.add(c.getString(0));
                }

                db.close();
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
            main.onTaskFinished(listas);
        }

    }

}