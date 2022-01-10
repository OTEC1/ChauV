package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.chauvendor.R;
import com.example.chauvendor.util.utils;

public class Forgot_Pass extends AppCompatActivity {

    private Button button2;
    private EditText email;
    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        button2= findViewById(R.id.email_sign_in_button);
        email= findViewById(R.id.email);
        container= findViewById(R.id.container);

        button2.setOnClickListener(view ->{
            if(email.getText().toString().trim().isEmpty())
                new utils().message("Pls fill out the email field", this);
            else {
                FIREBASE_CALL();
                startActivity(new Intent(getApplicationContext(), Login.class));
                new utils().long_message("A Password Reset Link would be Sent to your email", getApplicationContext());

            }
        } );

        container.setOnClickListener(d->{

        });
    }

    private void FIREBASE_CALL() {
        //first check if email is valid
    }

}