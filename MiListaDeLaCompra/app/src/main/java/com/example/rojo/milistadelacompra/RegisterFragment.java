package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private Button boton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private EditText newUser;
    private EditText newPassword;
    private EditText newName;
    private EditText newSurname1;
    private EditText newSurname2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        boton = view.findViewById(R.id.register_button);
        boton.setOnClickListener(this);
        newUser = view.findViewById(R.id.new_user);
        newPassword = view.findViewById(R.id.new_password);
        newName = view.findViewById(R.id.new_name);
        newSurname1 = view.findViewById(R.id.new_surname1);
        newSurname2 = view.findViewById(R.id.new_surname2);
        return view;
    }


    @Override
    public void onClick(View view) {
        ConnectMySql conexion = new ConnectMySql(this.getView(), this.getActivity());
        String userNickName = newUser.getText().toString();
        String userPassword = newPassword.getText().toString();
        String userName = newName.getText().toString();
        String userSurname1 = newSurname1.getText().toString();
        String userSurname2 = newSurname2.getText().toString();
        new ConnectMySql(this.getView(), this.getActivity()).execute(userNickName, userPassword, userName, userSurname1, userSurname2);
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
            Log.d(TAG, "Trying to insert " + params[0] + " " + params[1] + " " + params[2] + " " + params[3] + " " + params[4]);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database conection success");

                String userNickName = params[0];
                String userPass = params[1];
                String userName = params[2];
                String userSurname1 = params[3];
                String userSurname2 = params[4];

                String result = "";

                if (!userNickName.equals("")) {
                    if (!userPass.equals("")) {
                        if (!userName.equals("")) {
                            if (!userSurname1.equals("") && !userSurname2.equals("")) {
                                userNickName = userNickName.trim();
                                userName = userName.trim();
                                userSurname1 = userSurname1.trim();
                                userSurname2 = userSurname2.trim();

                                Statement st = con.createStatement();
                                Log.e(TAG, "insert into Usuario values ('" + userNickName + "', '" + userPass + "', '" + userName + "', '" + userSurname1 + "', '" + userSurname2 + "');");
                                st.execute("insert into Usuario values ('" + userNickName + "', '" + userPass + "', '" + userName + "', '" + userSurname1 + "', '" + userSurname2 + "');");
                                result = getResources().getString(R.string.data_charged);
                            } else {
                                result = getResources().getString(R.string.insert_user_surname);
                            }
                        } else {
                            result = getResources().getString(R.string.insert_user_name);
                        }
                    } else {
                        result = getResources().getString(R.string.insert_user_pass);
                    }

                } else {
                    result = getResources().getString(R.string.insert_user_nick);
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
        }
    }
}
