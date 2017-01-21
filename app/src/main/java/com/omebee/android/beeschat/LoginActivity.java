package com.omebee.android.beeschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.parse.LogInCallback;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 * Created by phannguyen on 9/12/16.
 */
public class LoginActivity extends Activity {

    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(getApplicationContext(), MainActivity.class);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField = (EditText) findViewById(R.id.loginPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Wrong username/password",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        long time = System.currentTimeMillis();
        long time1 =  Calendar.getInstance().getTimeInMillis();
        Log.i("TIME","Time "+time);
        Log.i("TIME","Time1 "+time1);

        Calendar rightNow = Calendar.getInstance();
        // offset to add since we're not UTC
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);

        long time2 = (rightNow.getTimeInMillis() + offset);
        Log.i("TIME","Time2 "+time2);

        if(FirebaseHelper.Instance().getCurrentUserId() !=null){
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
