package com.example.rojo.yambaismaelvictor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonTweet;
    Twitter twitter;

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
    }

    public void onClick(View v) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked");

        try {
            twitter.updateStatus(status);
            Log.d(TAG, "Enviado correctamente: ");
        } catch (Exception e) {
            Log.e(TAG, "Fallo en el envío");
            e.printStackTrace();
        }
    }
}
