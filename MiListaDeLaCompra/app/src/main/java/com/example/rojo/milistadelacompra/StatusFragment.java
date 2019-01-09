/*Victor Rojo Alvarez
* Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusFragment extends Fragment implements View.OnClickListener, TextWatcher {
    SharedPreferences prefs;

    private static final String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonTweet;
    TextView textCount;
    ProgressBar progressBar;
    final String totalChar = "/" + Integer.toString(280);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_status,
                container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editStatus = (EditText) view.findViewById(R.id.editStatus);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setMax(2);
        buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
        buttonTweet.setOnClickListener(this);

        textCount = (TextView) view.findViewById(R.id.textCount);
        textCount.setText( Integer.toString(0) + totalChar);
        textCount.setTextColor(Color.GREEN);
        editStatus.addTextChangedListener(this);

        return view;
    }

    public void onClick(View v) {
        String status = editStatus.getText().toString();
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG,"onClicked");
        new PostTask(v, this.getActivity()).execute(status);
    }

    @Override
    public void afterTextChanged(Editable statusText) {
        int count = statusText.length();
        textCount.setText(count + totalChar);
        if (count < 240 ){
            textCount.setTextColor(Color.GREEN);
        }else if (count > 280){
            textCount.setTextColor(Color.RED);
        } else {
            textCount.setTextColor(Color.YELLOW);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    private final class PostTask extends AsyncTask<String, Integer, String> {

        Context contexto;
        View view;
        public PostTask(View view, Context contexto){
            this.view = view;
            this.contexto = contexto;
        }
        // Llamada al empezar
        @Override
        protected String doInBackground(String... params) {

            String accesstoken = prefs.getString("accesstoken", "");
            String accesstokensecret = prefs.getString("accesstokensecret", "");
            // Comprobar si el nombre de usuario o el password están vacíos.
            // Si lo están, indicarlo mediante un Toast y redirigir al usuario a Settings
            if (TextUtils.isEmpty(accesstoken) || TextUtils.isEmpty(accesstokensecret)) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return "Por favor, actualiza tu nombre de usuario y tu contraseña";
            }
            return "Lo siento, twitter ya no está";
        }
        // Llamada cuando la actividad en background ha terminado
        @Override
        protected void onPostExecute(String result) {
            // Acción al completar la actualización del estado
            super.onPostExecute(result);
            publishProgress(2);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Snackbar.make(view, result, Snackbar.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            progressBar.setProgress(values[0]);
        }


        @Override
        protected void onPreExecute(){
        }
    }
}
