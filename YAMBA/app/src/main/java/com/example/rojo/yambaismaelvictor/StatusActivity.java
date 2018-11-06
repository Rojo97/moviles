package com.example.rojo.yambaismaelvictor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonTweet;
    Twitter twitter;
    TextView textCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        editStatus = (EditText) findViewById(R.id.editStatus);
        buttonTweet = (Button) findViewById(R.id.buttonTweet);
        buttonTweet.setOnClickListener(this);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey("bU3bzE1KJjjJhHa3kvlpIzWlw")
                .setOAuthConsumerSecret("P6XKEk1kJtzC3Vw48NExav3iQG7gjCkXh1otNhDn0pPNpMRE4Q")
                .setOAuthAccessToken("1059787428888801280-misjQLlJfqciJCke8E4ZPVBjQxLxKK")
                .setOAuthAccessTokenSecret("9GN4C3iCOA7vQtGj84Oddb6Xni4s5VmbW76tth0OIDSnD");
        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(280));
        textCount.setTextColor(Color.GREEN);
        editStatus.addTextChangedListener(this);
    }

    public void onClick(View v) {
        String status = editStatus.getText().toString();
        Log.d(TAG,"onClicked");
        new PostTask().execute(status);
    }

    @Override
    public void afterTextChanged(Editable statusText) {
        int count = 280 - statusText.length();
        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 10)
            textCount.setTextColor(Color.YELLOW);
        if (count < 0)
            textCount.setTextColor(Color.RED);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    private final class PostTask extends AsyncTask<String, Void, String> {
        // Llamada al empezar
        @Override
        protected String doInBackground(String... params) {
            try {
                twitter.updateStatus(params[0]);
                return "Tweet enviado correctamente";
            } catch (TwitterException e) {
                Log.e(TAG, "Fallo en el envío");
                e.printStackTrace();
                return "Fallo en el envío del tweet";
            }
        }
        // Llamada cuando la actividad en background ha terminado
        @Override
        protected void onPostExecute(String result) {
        // Acción al completar la actualización del estado
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, "Tweet enviado satisfactoriamente", Toast.LENGTH_LONG).show();
        }
    }

}

