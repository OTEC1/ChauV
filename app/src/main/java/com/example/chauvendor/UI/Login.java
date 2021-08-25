package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
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
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private TextView link_register;
    private EditText editText, editText1;
    private Button button;
    private ProgressBar progressBar;
    private SharedPreferences sp;
    private LocationManager locationManager;
    private FirebaseFirestore firebaseFirestore;


    private boolean status;
    private static final String TAG = "LoginActiviy";
    private long back_pressed;
    private int Time_lapsed = 2000;


    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getStringExtra("check_view").equals("2")) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //Refer to Reg activity
            if (status)
                buildAlertMessageNoGps();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = (Button) findViewById(R.id.email_sign_in_button);
        editText = (EditText) findViewById(R.id.email);
        editText1 = (EditText) findViewById(R.id.password);
        link_register = (TextView) findViewById(R.id.link_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        firebaseFirestore = FirebaseFirestore.getInstance();

        link_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Reg.class));
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check())
                    signIn();

            }
        });
    }


    private void signIn() {
        CACHE_FIRST_SIGNED_IN_USER();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText.getText().toString(), editText1.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        QUICK_DOC_REF();
                    else
                        message("Failed " + task.getException());
                }).addOnFailureListener(e -> {
            message("Authentication Failed" + e.getLocalizedMessage());
            hideDialog();
        });


    }

    private void CACHE_FIRST_SIGNED_IN_USER() {
        if (new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_USER), null) == null)
            new utils().instantiate_shared_preferences(sp, getApplicationContext()).edit().putString(getString(R.string.LAST_SIGN_IN_USER), editText.getText().toString()).apply();
        new utils().instantiate_shared_preferences(sp, getApplicationContext()).edit().putString(getString(R.string.VENDOR_NAME), null).apply();
    }


    private void message(String a) {
        new utils().message(a, this);

    }


    private void QUICK_DOC_REF() {
        Map<String, Object> i = new HashMap<>();
        i.put("token", new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), ""));
        DocumentReference documentReference = firebaseFirestore.collection(getString(R.string.Vendor_reg)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        documentReference.update(i).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FINAL_DOC_REF();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            } else
                new utils().message("Account not Registered", this);

        });

        System.out.println(new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_USER), ""));

    }


    private void FINAL_DOC_REF() {

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


    private void hideDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    //---------------------------------Location----------------------------------//
    //Step 1
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pls turn off GPS to reset location, do you want to turn off ?")
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


    //Step 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " Disabled Location ");

    }

    //------------------------------------------End Of Location--------------------------------//


    @Override
    public void onBackPressed() {

        new utils().message("Press twice to exit", this);
        if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        back_pressed = System.currentTimeMillis();
        free_memory();

    }


    public void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }


}