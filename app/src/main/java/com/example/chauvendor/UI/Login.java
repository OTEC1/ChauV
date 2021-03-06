package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chauvendor.R;
import com.example.chauvendor.Running_Service.Keep_alive;
import com.example.chauvendor.Running_Service.RegisterUser;
import com.example.chauvendor.util.User;
import com.example.chauvendor.util.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.pushy.sdk.Pushy;

import static com.example.chauvendor.constant.Constants.READ_STORAGE_PERMISSION_REQUEST_CODE;

public class Login extends AppCompatActivity {

    private TextView link_register, missue_report,forgot_pass;
    private EditText editText, editText1;
    private Button button;
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout home_screen;


    private boolean status;
    private static final String TAG = "LoginActivity";
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
        missue_report = (TextView) findViewById(R.id.issue_report);
        forgot_pass = (TextView) findViewById(R.id.forgot_pass);
        firebaseFirestore = FirebaseFirestore.getInstance();

        new MainActivity().NOTIFICATION_LISTER(new Keep_alive(), new Intent(), this);
        bull_eye();

        home_screen.setOnClickListener(s -> {
        });

        forgot_pass.setOnClickListener(o->{
            startActivity(new Intent(getApplicationContext(),Forgot_Pass.class));
        });

        link_register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Reg.class)));

        button.setOnClickListener(view -> {
            if (check())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    signIn();
        });


        missue_report.setOnClickListener(s -> {
            startActivity(new Intent(getApplicationContext(), Issues_submit.class));
        });
    }


    //Step 1
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText.getText().toString(), editText1.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        QUICK_UPDATE();
                    else
                        message("Failed " + task.getException());
                }).addOnFailureListener(e -> {
            message("Authentication Failed" + e.getLocalizedMessage());
            hideDialog();
        });


    }


    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Map<String, Object> QUICK_UPDATE() {

        Map<String, Object> i = new HashMap<>();
        i.put("token", new utils().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), ""));
        RE_USE(0, i, Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        return i;

    }


    //Step 3
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void FINAL_DOC_REF() {
        //Already installation
        if (new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null) != null) {
            //Another user sign in on device
            if (!Objects.equals(FirebaseAuth.getInstance().getUid(), new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null)))
                UPDATE_REGISTRATION_SECTION();
            else
                //Another user sign in
                NAVIGATE_USER();
        } else
            //First time installation
            NAVIGATE_USER();

    }


    //Step 4
    private void UPDATE_REGISTRATION_SECTION() {
        FirebaseFirestore.getInstance().collection(getString(R.string.Vendor_reg)).document(new utils().init(getApplicationContext())
                .getString(getString(R.string.LAST_SIGN_IN_VENDOR), null))
                .update(MAP()).addOnCompleteListener(u -> {
            if (u.isSuccessful())
                NAVIGATE_USER();
            else {
                new utils().message("Registration section  not Updated " + u.getException(), this);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    //Step 5
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void UPDATE_LOCATION_SECTION(User user) {
        if (new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null) != null)
            if (!Objects.equals(FirebaseAuth.getInstance().getUid(), new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null))) {
                FirebaseFirestore.getInstance().collection(getString(R.string.Vendor_loc)).document(new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null))
                        .update("user", MAP_USER(user, 0)).addOnCompleteListener(u -> {
                    if (u.isSuccessful())
                        RE_USE(1, MAP(), new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null));
                    else {
                        new utils().message("Error occurred "+u.getException(), this);
                        progressBar.setVisibility(View.GONE);
                    }

                });
            } else
                NAVIGATE_USER();
        else NAVIGATE_USER();
    }


    //Step 6
    private Map<String, Object> MAP() {
        Map<String, Object> i = new HashMap<>();
        i.put("token", "");
        return i;
    }


    //Step 6b
    private Object MAP_USER(User user, int y) {
        Map<String, Object> i = new HashMap<>();
        i.put("app_user", user.getApp_user());
        i.put("bad", user.getBad());
        i.put("business_details", user.getBusiness_details());
        i.put("email", user.getEmail());
        i.put("fair", user.getFair());
        i.put("good", user.getGood());
        i.put("img_url", user.getImg_url());
        i.put("member_T", user.getMember_T());
        i.put("name", user.getName());
        i.put("phone", user.getPhone());

        if (y == 0)
            i.put("token", "");
        else
            i.put("token", user.getToken());

        i.put("user_id", user.getUser_id());
        i.put("username", user.getUsername());
        return i;
    }


    //Step 7
    private void NAVIGATE_USER() {

        firebaseFirestore.collection(getString(R.string.Vendor_reg)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(u -> {
            if (u.isSuccessful()) {
                User user = u.getResult().toObject(User.class);
                UPDATE_VENDOR_LOCATIONS(user);
            } else {
                new utils().message("Error getting  user details" + u.getException(), this);
                progressBar.setVisibility(View.GONE);
            }

        });


    }


    //Step 8
    private void UPDATE_VENDOR_LOCATIONS(User user) {
        FirebaseFirestore.getInstance().collection(getString(R.string.Vendor_loc)).document(FirebaseAuth.getInstance().getUid())
                .update("user", MAP_USER(user, 1)).addOnCompleteListener(u -> {
            if (u.isSuccessful())
                new utils().VENDOR_LOCATION_QUERY(progressBar, this, editText);
            else {
                new utils().message("Error updating  user location", this);
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "RE_USE: " + u.getException());
            }
        });

    }


    //Step final
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void RE_USE(int i, Map<String, Object> s, String doc_id) {
        firebaseFirestore.collection(getString(R.string.Vendor_reg)).document(doc_id).update(s).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (i != 1) {
                    if (new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null) != null)
                        firebaseFirestore.collection(getString(R.string.Vendor_reg)).document(new utils().init(getApplicationContext()).getString(getString(R.string.LAST_SIGN_IN_VENDOR), null)).get()
                                .addOnCompleteListener(d -> {
                                    if (d.isSuccessful()) {
                                        User user = d.getResult().toObject(User.class);
                                        UPDATE_LOCATION_SECTION(user);
                                    }
                                });
                    else
                        FINAL_DOC_REF();
                } else
                    FINAL_DOC_REF();
            } else {
                new utils().message("Account not Registered" + task.getException(), this);
                progressBar.setVisibility(View.GONE);
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


    //Step 2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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




    private boolean isServicerunning(Class<? extends Keep_alive> aClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, " Service Already Running");
                return true;
            }
            Log.d(TAG, " Service Not Running");
        }
        return false;
    }


    private void message(String a) {
        new utils().message(a, this);

    }
}