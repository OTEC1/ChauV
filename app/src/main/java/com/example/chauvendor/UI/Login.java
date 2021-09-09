package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chauvendor.R;
import com.example.chauvendor.util.User;
import com.example.chauvendor.util.UserLocation;
import com.example.chauvendor.util.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.chauvendor.constant.Constants.READ_STORAGE_PERMISSION_REQUEST_CODE;

public class Login extends AppCompatActivity {

    private TextView link_register;
    private EditText editText, editText1;
    private Button button;
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout home_screen;

    private boolean status;
    private static final String TAG = "LoginActiviy";
    private long back_pressed;
    private int Time_lapsed = 2000;


    @Override
    protected void onResume() {
        super.onResume();
        STRICT_POLICY();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            CHECK_POLICY();


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
        home_screen = (RelativeLayout) findViewById(R.id.home_screen);
        firebaseFirestore = FirebaseFirestore.getInstance();

        bull_eye();

        home_screen.setOnClickListener(s -> {
        });

        link_register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Reg.class)));

        button.setOnClickListener(view -> {
            if (check())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    signIn();


        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signIn() {
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


    private void message(String a) {
        new utils().message(a, this);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void QUICK_DOC_REF() {
        Map<String, Object> i = new HashMap<>();
        i.put("token", new utils().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), ""));
        RE_USE(0, i, Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void FINAL_DOC_REF() {
        //Already installation
        if (new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null) != null) {
            //Another user sign in on device
            if (!Objects.equals(FirebaseAuth.getInstance().getUid(), new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null))) {
                Map<String, Object> i = new HashMap<>();
                i.put("token", "");
                RE_USE(1, i, new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null));
                //Same user sign in again
            } else
                NAVIGATE_USER();
            //First time installation
        } else
            NAVIGATE_USER();

    }


    private void NAVIGATE_USER() {
        new utils().VENDOR_LOCATION_QUERY(progressBar, this, editText);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void RE_USE(int i, Map<String, Object> s, String doc_id) {
        DocumentReference documentReference = firebaseFirestore.collection(getString(R.string.Vendor_reg)).document(doc_id);
        documentReference.update(s).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (i != 1)
                    FINAL_DOC_REF();
                NAVIGATE_USER();
            } else {
                new utils().message("Account not Registered", this);
                progressBar.setVisibility(View.GONE);
                System.out.println(task.getException());
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


    private void hideDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    //---------------------------------Location----------------------------------//
    //Step 1


    //Step 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " Disabled Location ");

    }
    //------------------------------------------End Of Location--------------------------------//


    @Override
    public void onBackPressed() {

        new utils().message1("Press twice to exit", this);
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


    //----------------------------------------------Permission for file sharing ---------------------------------------------//
    //Step 1
    public void STRICT_POLICY() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d(TAG, " Called !");
        }
    }


    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CHECK_POLICY() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else
            request_permission();
    }


    //Step 3
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed for file sharing")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        }


    }


    //Step 4
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                new utils().message2("Permission Granted", this);
            else
                new utils().message2("Permission Denied", this);

        }

    }
    //----------------------------------------------End of file sharing ---------------------------------------------//


    private void bull_eye() {
        if (getIntent().getStringExtra("check_view").equals("2")) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //Refer to Reg activity
            if (status)
                new utils().buildAlertMessageNoGps(this, 0, "Pls turn off GPRS to reset location, do you want to turn off?");

        }
    }

}