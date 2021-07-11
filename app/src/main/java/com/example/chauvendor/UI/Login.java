package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chauvendor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextView link_register;
    private EditText editText,editText1;
    private Button button;
    private ProgressBar progressBar;
    private  long  back_pressed;
    private  int  Time_lapsed=2000;
    private SharedPreferences sp;
    private boolean  status;
    private static final String TAG = "LoginActiviy";
    private LocationManager locationManager;




    private void start_pref() { sp = getApplicationContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE); }



    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().getStringExtra("check_view").equals("2")) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //Refer to Reg activity
            if (status)
                buildAlertMessageNoGps();
        }
    }


    //---------------------------------Location----------------------------------//


    //Step 4
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pls turn off GPS to resit location, do you want to turn off ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent1);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }







    //Step 5
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG," Disabled Location ");

    }





    @Override
    public void onBackPressed() {

        message("Press twice to exit");
        if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        back_pressed = System.currentTimeMillis();
        free_memory();

    }


    public   void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button =(Button) findViewById(R.id.email_sign_in_button);
        editText =(EditText) findViewById(R.id.email);
        editText1 =(EditText) findViewById(R.id.password);
        link_register = (TextView) findViewById(R.id.link_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);




        start_pref();

        link_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Reg.class));
            }});



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check())
                    signIn();

            }
        });
    }



    private void signIn(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText.getText().toString(), editText1.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            sp.edit().putString("user_email",editText.getText().toString()).apply();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                            message("Failed "+task.getException());
                    }}).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                message("Authentication Failed"+ e.getLocalizedMessage());
                hideDialog();
            }
        });
    }








    private boolean check() {
        if (editText.getText().toString().isEmpty()) {
            check_edit_text(editText, "Pls fill out field");
            return false;
        } else if (editText1.getText().toString().isEmpty()) {
            check_edit_text(editText1, "Pls fill out field");
            return false;
        } else
            return true;
    }


    public void check_edit_text(EditText edit, String string) {
        if (edit.getText().toString().isEmpty()) {
            edit.setError(string);
            edit.requestFocus();
        }
    }


    private  void  show_pro() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private  void  hideDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void message(String s) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_LONG).show();

    }
}