package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chauvendor.R;
import com.example.chauvendor.model.UserLocation;
import com.example.chauvendor.widget.ViewHeightAnimation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.example.chauvendor.constant.Constants.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private  String TAG ="MainActivity";
    private boolean mLocationPermissionGranted = false,decision=false;
    private BottomNavigationView bottomNav;
    private static long back_pressed;
    private static int Time_lapsed = 2000;
    private boolean decide = false;
    private FirebaseFirestore mfirestore;
    private UserLocation muserLocation;
    private  RelativeLayout fragmentlayout,framelayout;




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  void  start_pref(){ sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE); }
    private void Get_instance() {
        mfirestore = FirebaseFirestore.getInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        decide =true;
    }

    //----------------------------------------------Permission for   file sharing ---------------------------------------------//
    //Step 1
    public  void policy(){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d (TAG," Called !");
        }
    }



    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void check() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

        }else
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
                message2("Permission Granted");
            else
                message2("Permission Denied");

        }

    }





    //----------------------------------------------End of file sharing ---------------------------------------------//




















    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav =(BottomNavigationView) findViewById(R.id.bottomNav);
        fragmentlayout = (RelativeLayout) findViewById(R.id.header);
        framelayout = (RelativeLayout) findViewById(R.id.sections);
        openFragment(new home());
        Get_instance();
        policy();
        start_pref();
        check();
        bottom_nav();

    }





    protected void bottom_nav() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homes:
                        expandMapAnimation();

                        decide = true;
                        openFragment(new home());
                        return true;
                    case R.id.carts:
                        if(!decision)
                            contractMapAnimation();

                        decide = false;
                        openFragment(new notification());
                        return true;
                    case R.id.notification:
                        if(!decision)
                            contractMapAnimation();

                        decide = false;
                        openFragment(new account());
                        return true;
                }
                return false;
            }
        });
    }


    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    @Override
    public void onBackPressed() {
        if (decide) {
            message1("Press twice to exit");
            if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            back_pressed = System.currentTimeMillis();
            free_memory();
        } else
            super.onBackPressed();
    }


    public void clears() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("gui");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public   void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }







    private  void message1(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    private void message2(String s) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_SHORT).show();

    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.signuser, popup.getMenu());
        popup.show();
    }



    void contractMapAnimation(){
        ViewHeightAnimation mapAnimationWrapper = new ViewHeightAnimation(fragmentlayout);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                22,
                12);
        mapAnimation.setDuration(800);

        ViewHeightAnimation recyclerAnimationWrapper = new ViewHeightAnimation(framelayout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                78,
                88);
        recyclerAnimation.setDuration(10);
        recyclerAnimation.start();
        mapAnimation.start();
        decision =true;
    }



    void  expandMapAnimation(){

        ViewHeightAnimation mapAnimationWrapper = new ViewHeightAnimation(fragmentlayout);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                12,
                20);
        mapAnimation.setDuration(0);

        ViewHeightAnimation recyclerAnimationWrapper = new ViewHeightAnimation(framelayout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                88,
                80);
        recyclerAnimation.setDuration(0);
        recyclerAnimation.start();
        mapAnimation.start();
        decision = false;
    }

    public void signin(MenuItem item) { startActivity(new Intent(this, Login.class).putExtra("check_view",String.valueOf(2))); }

    public void signout(MenuItem item) { sp.edit().putString("KOS",null).apply(); Log.d(TAG,"Sign out");}

    public void search(MenuItem item) {
        decide = false;
        openFragment(new Search());
        if(!decision)
            contractMapAnimation();
    }

    public void space(View view) {
    }
}