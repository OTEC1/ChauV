package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.FrameLayout;
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
import com.example.chauvendor.Retrofit_.Base_config;
import com.example.chauvendor.Retrofit_.Calls;
import com.example.chauvendor.util.User;
import com.example.chauvendor.util.UserLocation;
import com.example.chauvendor.util.Find;
import com.example.chauvendor.util.utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.chauvendor.constant.Constants.*;


public class Reg extends AppCompatActivity {


    private EditText editText1, editText2, editText3, editText4, editText5, editText6;
    private Button button_reg, button1;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinner;
    private ProgressBar mprogressBar, cat;
    private UserLocation muserLocation;
    private FirebaseFirestore mfirestore;
    private GeoPoint geoPoint;
    private Uri imgUri;
    private FrameLayout frameLayout;


    private List<String> list = new ArrayList();
    private List<Map<String, Object>> obj = null;


    private String string, p1 = "", p2 = "", p3 = "", img_url;
    private static final String TAG = "RegisterActivity";


    private boolean mLocationPermissionGranted = false;
    private boolean once = false, confirm = false;


    @Override
    protected void onResume() {
        super.onResume();
        mfirestore = FirebaseFirestore.getInstance();
        if (!mLocationPermissionGranted)
            checkMapServices();
        if (mLocationPermissionGranted) {
            getLast_know_Location(FirebaseAuth.getInstance().getUid(), 0);

        }

    }

    @Override
    public void onBackPressed() {
        if (once)
            message_reg("Pls wait Registration in progress...");
        else
            super.onBackPressed();
    }


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
        editText6 = (EditText) findViewById(R.id.business_details);
        button_reg = (Button) findViewById(R.id.btn_register);
        spinner = (Spinner) findViewById(R.id.vendor_category);
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        cat = (ProgressBar) findViewById(R.id.category_spinner);
        button1 = (Button) findViewById(R.id.pic_selector);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        mfirestore = FirebaseFirestore.getInstance();
        drop_down_populate(new ArrayList<>(), TAG, this);


        frameLayout.setOnClickListener(d -> {

        });


        button_reg.setOnClickListener(view -> {
            if (!new utils().init(getApplicationContext()).getBoolean(getString(R.string.DEVICE_REG_TOKEN), false))
                message_reg("Device not Registered Pls Relaunch or Reinstall App.");
            else {
                if (checkMapServices()) {
                    getLocationPermission();
                    if (mLocationPermissionGranted) {
                        if (new utils().verify(editText1, editText2, editText3, editText4, editText5, editText6, string, this) && confirm && TOKEN_OK()) {
                            String pic_key = getFile_extension(imgUri);
                            if (pic_key.equalsIgnoreCase("png") | pic_key.equalsIgnoreCase("jpg") | pic_key.equalsIgnoreCase("jpeg") | pic_key.equalsIgnoreCase("webp")) {
                                img_url = generate_name().concat(".png");
                                make_post_on_location(editText2.getText().toString(), editText4.getText().toString());
                            } else
                                message_reg("Invalid  File Selected");
                        } else if (!TOKEN_OK())
                            message_reg("Pls Reinstall App");
                        else if (!confirm)
                            message_reg("Pls select an image File");
                    } else
                        message_reg("Pls Reinstall and Grant all Permission");
                } else
                    message_reg("Google Map Can't work on this device");
            }

        });


        button1.setOnClickListener(this::file_picker);


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

    private boolean TOKEN_OK() {
        return new utils().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), "").trim().length() > 0;
    }


    private void make_post_on_location(String email, String pass) {
        show_progress();
        once = true;
        button_reg.setEnabled(false);
        message_reg("Registering member");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    String email1 = editText2.getText().toString();
                    if (task.isSuccessful()) {
                        User user = new User();
                        user.setName(editText1.getText().toString().toLowerCase());
                        user.setEmail(email1);
                        user.setUsername(email1.substring(0, email1.indexOf("@")));
                        user.setPhone(editText3.getText().toString());
                        user.setUser_id(FirebaseAuth.getInstance().getUid());
                        user.setMember_T("I agree to Terms and Condition");
                        user.setApp_user(string);
                        user.setImg_url(img_url);
                        user.setToken(new utils().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), ""));
                        user.setGood(0);
                        user.setBad(0);
                        user.setFair(0);
                        user.setBusiness_details(editText6.getText().toString());

                        DocumentReference new_member = mfirestore.collection(getString(R.string.Vendor_reg)).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        new_member.set(user).addOnCompleteListener(task1 -> {


                            if (task1.isSuccessful())
                                getUserDetails(FirebaseAuth.getInstance().getUid());
                            else {
                                message_reg("Something went wrong.");
                                hide_bar();
                                button_reg.setEnabled(true);
                            }
                        });

                    } else {
                        message(task.getException());
                        hide_bar();
                        button_reg.setEnabled(true);
                    }
                });

    }


    //---------------------------------Location----------------------------------//

    //Step 1
    private boolean checkMapServices() {
        if (isServicesOK())
            if (isMapsEnabled())
                return true;
        return false;
    }


    //Step 2
    public boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Reg.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            new utils().message("an error occurred but we can fix it pls follow instruction", this);
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Reg.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    //Step 3
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        } else {
            Log.d(TAG, " Added Location Permission");
            return true;
        }
    }


    //Step 4
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Pls turn on your GPRS and internet too reset location, do you want to enable it ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted)
                    Log.d(TAG, " Added Location Permission");
                else
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
                new utils().message2("No Image Selected.", this);
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
    private void getUserDetails(final String user_id) {

        muserLocation = new UserLocation();
        DocumentReference userref = mfirestore.collection(getString(R.string.Vendor_reg)).document(user_id);
        userref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                message_reg("Member Registered");
                User user = task.getResult().toObject(User.class);
                if (user == null)
                    return;

                muserLocation.setUser(user);
                muserLocation.setTimestamp(null);
                getLast_know_Location(user_id, 1);
            }
        });

    }


    private void message_reg(String s) {
        new utils().message2(s, this);
    }


    //Step 3
    private void getLast_know_Location(final String vs, int i) {
        if (i != 0)
            new utils().message2("Requesting for Vendor location", this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;


        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        if (muserLocation != null)
                            muserLocation.setGeo_point(geoPoint);

                        if (C == 0 && i != 0) {
                            saveUserLocation(vs);
                            C++;
                        }

                    }
                }
            }
        };

        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);


    }


    //Step 4
    private void saveUserLocation(String x) {

        if (muserLocation != null) {
            DocumentReference locationref = mfirestore.collection(getString(R.string.Vendor_loc)).document(x);
            locationref.set(muserLocation).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    credentials();
                else {
                    hide_progress();
                    message(Objects.requireNonNull(task.getException()));
                    button_reg.setEnabled(false);
                }
            });
        }
    }

    //-----------------------------------------------End of Location Query------------------------------------//


    //-----------------------------------------------S3 Query------------------------------------//
    private void credentials() {

        DocumentReference user = mfirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                p1 = task.getResult().getString("p1");
                p2 = task.getResult().getString("p2");
                p3 = task.getResult().getString("p3");
                try {
                    if (p1.length() > 0 && p2.length() > 0) {
                        assert p3 != null;
                        if (p3.length() > 0) send_data_to_s3(p1, p2, p3, img_url);
                    }
                } catch (Exception e) {
                    message(e);
                    hide_progress();
                }
            }
        });
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


    private void send_data_to_s3(String p1, String p2, String p3, String url) throws URISyntaxException {


        new utils().message2("Uploading Image...", this);
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


                if (percentDo == 100)
                    NEXT_PAGE();


            }

            @Override
            public void onError(int id, Exception ex) {
                message(ex);
                hide_progress();
                button_reg.setEnabled(true);


            }

        });


    }

    private void NEXT_PAGE() {
        new utils().openFragment(new Business_details(), this, new Bundle());
        button_reg.setEnabled(true);
    }

    //-----------------------------------------------End S3 Query------------------------------------//


    private void message(Exception ex) {
        new utils().message2(ex.getLocalizedMessage(), this);
    }


    private void message2(String localizedMessage) {
        new utils().message2(localizedMessage, this);
    }


    private void hide_bar() {
        mprogressBar.setVisibility(View.INVISIBLE);
    }


    private void show_progress() {
        mprogressBar.setVisibility(View.VISIBLE);
    }

    private void hide_progress() {
        mprogressBar.setVisibility(View.INVISIBLE);
    }

    public void space(View view) {
    }


    public void drop_down_populate(ArrayList<String> list, String TAG, AppCompatActivity applicationContext) {
        obj = new ArrayList<>();
        Calls calls = Base_config.getConnection().create(Calls.class);
        Call<List<Map<String, Object>>> search = calls.getCat();
        search.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                obj = response.body();
                assert obj != null;
                for (Map<String, Object> z : obj)
                    list.add(Objects.requireNonNull(z.get("category")).toString());
                if (list != null)
                    pop_out(list);
            }

            @Override
            public void onFailure(@NotNull Call<List<Map<String, Object>>> call, Throwable t) {
                message2("Error occurred "+t.getLocalizedMessage());
            }
        });


    }


    private void pop_out(List<String> list) {
        Collections.reverse(list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.text_pad);
        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);
        cat.setVisibility(View.GONE);
    }


}