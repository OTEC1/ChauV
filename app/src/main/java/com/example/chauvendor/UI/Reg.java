package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.chauvendor.R;
import com.example.chauvendor.model.User;
import com.example.chauvendor.model.UserLocation;
import com.example.chauvendor.util.Find;
import com.example.chauvendor.util.UserClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.chauvendor.constant.Constants.*;


public class Reg extends AppCompatActivity {


    private EditText editText1,editText2,editText3,editText4,editText5;
    private Button button_reg,button1;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list;
    private Spinner spinner;
    private String string,p1,p2,p3,in;
    private ProgressBar mprogressBar;
    private SharedPreferences sp;
    private static final String TAG = "RegisterActiviy";
    private UserLocation muserLocation;
    private boolean mLocationPermissionGranted = false, once = false, confirm = false;
    private FirebaseFirestore mfirestore;
    private Uri imgUri;
    private  int z;


    @Override
    protected void onResume() {
        super.onResume();
        Get_instance();
        start_pref();
        if (!mLocationPermissionGranted)
            checkMapServices();

    }

    @Override
    public void onBackPressed() {
        if(once)
            message2("Pls wait Registration in progress...");
        else
            super.onBackPressed();
    }





    private void Get_instance() {
        mfirestore = FirebaseFirestore.getInstance();
    }
    private  void  start_pref(){ sp = (getApplicationContext()).getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE); }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        editText1 = (EditText) findViewById(R.id.name);
        editText2 = (EditText) findViewById(R.id.email);
        editText3 = (EditText) findViewById(R.id.phone);
        editText4 = (EditText) findViewById(R.id.pass);
        editText5 = (EditText) findViewById(R.id.input_confirm_password);
        button_reg = (Button) findViewById(R.id.btn_register);
        spinner = (Spinner) findViewById(R.id.vendor_category);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        button1 = (Button)  findViewById(R.id.pic_selector);

        Get_instance();
        start_pref();
        drop_down_populate();






        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  z=0;
                if(verify() && confirm) {
                    String pic_key = getFile_extension(imgUri);
                    if (pic_key.equalsIgnoreCase("png") | pic_key.equalsIgnoreCase("jpg") | pic_key.equalsIgnoreCase("jpeg") | pic_key.equalsIgnoreCase("webp")) {
                        in = generate_name().concat(".png");
                        make_post_on_location( editText2.getText().toString(), editText4.getText().toString());
                    }
                    else
                        message2("Invalid  File Selected");
                }
                else
                if(!confirm && z!=1)
                    message2("Pls select an image File");

            }
        });



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                file_picker(view);
            }
        });




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                string = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }






    private void make_post_on_location( String email, String pass) {
        show_progress();
        once = true;
        button_reg.setEnabled(false);
        Log.d(TAG," Register member");
        message2("Registering member");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String email = editText2.getText().toString();
                        if(task.isSuccessful()) {
                            User user = new User();
                            user.setName(editText1.getText().toString());
                            user.setEmail(email);
                            user.setUsername(email.substring(0, email.indexOf("@")));
                            user.setPhone(editText3.getText().toString());
                            user.setUser_id(FirebaseAuth.getInstance().getUid());
                            user.setMember_T("I agree to Terms and Condition");
                            user.setApp_user(string);
                            user.setImg_url(in);
                            user.setGood(0);
                            user.setBad(0);
                            user.setFair(0);


                            DocumentReference new_member = mfirestore.collection(getString(R.string.Vendor_reg)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

                            new_member.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if(task.isSuccessful()) {

                                        if (checkMapServices()) {
                                            if (mLocationPermissionGranted) {
                                                getUserDetails(FirebaseAuth.getInstance().getUid());
                                                getLast_know_Location(FirebaseAuth.getInstance().getUid());
                                            } else
                                                getLocationPermission();
                                        }



                                    }else {
                                        message2("Something went wrong.");
                                        hide_bar();
                                        button_reg.setEnabled(true);
                                    }
                                }
                            });

                        }
                        else {
                            message2("Error "+task.getException());
                            hide_bar();
                            button_reg.setEnabled(true);
                        }
                    }
                });

    }



















    //---------------------------------Location----------------------------------//

    //Step 1
    private boolean checkMapServices(){
        if(isServicesOK())
            if(isMapsEnabled())
                return true;
        return false;
    }


    //Step 2
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Reg.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else
        if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Reg.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    //Step 3
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }else {
            //sp.edit().putString("states","Ok").apply();
            Log.d(TAG," Added Location Permission");
            return true;
        }
    }

    //Step 4
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pls turn on your GPRS , do you want to enable it ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }




    //Media selector  Custom ui
    public void file_picker(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, PICK_IMAGE);

    }



    //Step 5
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted) {
                    //sp.edit().putString("states","Ok").apply();
                    Log.d(TAG," Added Location Permission");
                }else
                    getLocationPermission();

            }
        }


        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            assert data != null;
            imgUri = data.getData();
            assert imgUri != null;
            if (imgUri.toString().contains("image")) {
                button1.setText("Image Added Proceed to Registration");
                confirm = true;
            } else
                message2("No Image Selected.");
        }

    }





    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }





    //Step 2
    private  void  getUserDetails(final String sp){
        if(muserLocation == null) {
            muserLocation = new UserLocation();
            DocumentReference userref = mfirestore.collection(getString(R.string.Vendor_reg))
                    .document(sp);
            userref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, " onComplete successfully got details");
                        message2("Member Registered");
                        // System.out.println(task.getResult().getData());
                        User user = task.getResult().toObject(User.class);
                        if (user == null)
                            return;

                        muserLocation.setUser(user);
                        muserLocation.setTimestamp(null);
                        ((UserClient) getApplicationContext()).setUser(user);
                        getLast_know_Location(sp);
                    }
                }
            });
        }else
            getLast_know_Location(sp);
    }


    //Step 3
    private void getLast_know_Location(final String vs) {
        Log.d(TAG," requesting for last known location");
        message2("Requesting for user location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;


        LocationRequest mLocationRequest= LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback =new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if(locationResult == null)
                    return;

                for(Location location: locationResult.getLocations()){
                    if(location != null){
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "on complete Lat" + geoPoint.getLatitude());
                        Log.d(TAG, "on complete Lat" + geoPoint.getLongitude());
                        muserLocation.setGeo_point(geoPoint);

                        if(STOP_SERVICE == 0)
                            saveUserLocation(vs);
                        STOP_SERVICE = 2;

                    }
                }}};

        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }





    //Step 4
    private  void saveUserLocation(String x){

        if(muserLocation != null) {
            DocumentReference locationref = mfirestore.collection(getString(R.string.Vendor_loc))
                    .document(x);
            locationref.set(muserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sp.edit().putString("KOS",null).apply();
                        Log.d(TAG, "Lat"+muserLocation.getGeo_point().getLatitude());
                        Log.d(TAG, "long"+muserLocation.getGeo_point().getLongitude());
                        credentials();
                    }else {
                        hide_progress();
                        message2("Failed " + task.getException());
                        button_reg.setEnabled(false);
                    }
                }
            });
        }}






    private void credentials() {

        DocumentReference user =mfirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    p1 = task.getResult().getString("p1");
                    p2 = task.getResult().getString("p2");
                    p3 = task.getResult().getString("p3");
                    try {
                        if(p1.length()>0 && p2.length()>0 && p3.length()>0)
                            send_data_to_s3(p1,p2,p3,in);
                    } catch (Exception e) {
                        message2(e.toString());
                        Log.d(TAG,e.toString());
                        hide_progress();
                    }}}});
    }





    private String getFile_extension(Uri uri) {
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q));
    }



    private  void send_data_to_s3(String p1,String p2, String p3,String url) throws URISyntaxException {


        message2("Uploading Image...");
        AWSCredentials credentials = new BasicAWSCredentials(p1, p2);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3.setRegion(Region.getRegion(Regions.EU_WEST_3));
        //s3.setObjectAcl("", ".png", CannedAccessControlList.PublicRead);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        String d = Find.get_file_selected_path(imgUri, getApplicationContext());
        TransferObserver trans = transferUtility.upload(p3, url, new File(d));
        trans.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDo = (int) percentDone;


                if(percentDo == 100) {
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtra("check_view",String.valueOf(1)));
                    button_reg.setEnabled(true);
                }



            }

            @Override
            public void onError(int id, Exception ex) {
                message2(ex.getLocalizedMessage());
                Log.d(TAG,ex.getLocalizedMessage());
                hide_progress();

            }

        });



    }

    //-----------------------------------------------End of Location Query------------------------------------//









    private void drop_down_populate() {
        list =new ArrayList<>();
        list.add("Vendor Category");
        list.add("Rice (Jellof)");
        list.add("Rice (Stew)");
        list.add("Swallow");
        list.add("Noodles");
        list.add("Pap");
        list.add("All");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.text_pad);
        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);
    }



    private boolean verify() {
        if (editText1.getText().toString().isEmpty()) {
            check_edit_text(editText1, "Pls fill out field");
            return false;
        }
        else
        if (editText2.getText().toString().isEmpty()) {
            check_edit_text(editText2, "Pls fill out field");
            return false;
        }
        else
        if (editText3.getText().toString().isEmpty()) {
            check_edit_text(editText3, "Pls fill out field");
            return false;
        }
        else
        if (editText4.getText().toString().isEmpty()) {
            check_edit_text(editText4, "Pls fill out field");
            return false;
        }
        else
        if (editText5.getText().toString().isEmpty()) {
            check_edit_text(editText5, "Pls fill out field");
            return false;
        }
        else
        if(!doStringsMatch(editText4.getText().toString(),editText5.getText().toString())) {
            message2("Password does not match");
            return false;
        }
        else
        if(string.equals("Vendor Category")) {
            message2("Pls Indicate vendor type.");
            z=1;
            return  false;
        }
        else
            return true;
    }






    private  void hide_bar(){
        mprogressBar.setVisibility(View.INVISIBLE);
    }

    public static boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }




    private void message2(String s) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_SHORT).show();

    }


    public void check_edit_text(EditText edit, String string) {
        if (edit.getText().toString().isEmpty()) {
            edit.setError(string);
            edit.requestFocus();
        }
    }


    private void show_progress() {
        mprogressBar.setVisibility(View.VISIBLE);
    }

    private void hide_progress() {
        mprogressBar.setVisibility(View.INVISIBLE);
    }

    public void space(View view) { }
}