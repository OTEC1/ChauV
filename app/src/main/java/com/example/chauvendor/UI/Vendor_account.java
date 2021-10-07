package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.chauvendor.model.Vendor_uploads;
import com.example.chauvendor.util.Find;
import com.example.chauvendor.util.UserLocation;
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.chauvendor.constant.Constants.CHARGES;
import static com.example.chauvendor.constant.Constants.IMG_URL;
import static com.example.chauvendor.constant.Constants.PICK_IMAGE;

public class Vendor_account extends AppCompatActivity {


    private ProgressBar progressBar, progressBar1, progressBar_img, cat;
    private BottomNavigationView bottomNavigationView;
    private Button pic_select, upload1, view_review;
    private EditText foodprice, foodname;
    private CircleImageView vendor_img;
    private TextView shopname, progress, phone, business;
    private Bundle bundle = new Bundle();
    private ImageView image_view;
    private Spinner spinner;
    private Uri imgUri;


    private boolean confirm = false, started_payload = false;
    private String string = "Indicate", pic_key, TAG = "accountFragment";


    private FirebaseFirestore mfirebaseFirestore;
    private List arrays;
    private List<Map<String, Object>> obj;
    private ArrayAdapter adapter1;
    private UserLocation user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_account);
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        pic_select = (Button) findViewById(R.id.pic_select);
        upload1 = (Button) findViewById(R.id.upload);
        view_review = (Button) findViewById(R.id.view_review);
        foodprice = (EditText) findViewById(R.id.foodprice);
        foodname = (EditText) findViewById(R.id.foodname);
        image_view = (ImageView) findViewById(R.id.image_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar_img = (ProgressBar) findViewById(R.id.progressBar5);
        vendor_img = (CircleImageView) findViewById(R.id.vendor_img);
        spinner = (Spinner) findViewById(R.id.spinners);
        shopname = (TextView) findViewById(R.id.shop_name);
        phone = (TextView) findViewById(R.id.phone);
        business = (TextView) findViewById(R.id.business_details);
        progress = (TextView) findViewById(R.id.progress);
        cat = (ProgressBar) findViewById(R.id.progressBar6);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, bundle);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.account_top_section));
        }
        // View view =getSupportActionBar().getCustomView();

        if (FirebaseAuth.getInstance().getUid() != null)
            current_vendor();

        populate_drop_down();

        pic_select.setOnClickListener(this::file_picker);

        if (CHARGES == null)
            new utils().quick_commission_call(TAG);


        foodprice.setOnClickListener(u->{
            FOCUS(foodprice,foodname);
        });


        foodname.setOnClickListener(u->{
            FOCUS(foodprice,foodname);
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


        upload1.setOnClickListener(view -> {


            if (FirebaseAuth.getInstance().getUid() == null)
                message2("Pls Sign in.");
            else if (string.equals("Choose category"))
                message2("Pls indicate which category to upload");
            else if (CHARGES == null) {
                new utils().quick_commission_call(TAG);
                message2("Snap yr connection seems Poor !");
            } else if (foodprice.getText().toString().length() <= 0 | foodname.getText().toString().length() <= 0) {
                hide_progress();
                message2("Pls fill out both fields");
            } else if (!confirm) {
                hide_progress();
                message2("Pls select Food Picture");
            } else if (confirm && foodprice.getText().toString().length() > 0 && foodname.getText().toString().length() > 0) {
                pic_key = getFile_extension(imgUri);
                if (pic_key.equalsIgnoreCase("png") | pic_key.equalsIgnoreCase("jpg") | pic_key.equalsIgnoreCase("jpeg") | pic_key.equalsIgnoreCase("webp")) {
                    show_progress();
                    String in = generate_name().concat(".png");
                    send_data_to_firebase(foodprice.getText().toString(), foodname.getText().toString(), in);
                    credentials(in);
                } else {
                    message2("Pls select a valid Image file");
                    hide_progress();
                }
            }
        });


        view_review.setOnClickListener(h -> {
            new utils().openFragment(new home(), this, new Bundle());
        });

    }




    private void FOCUS(EditText foodprice, EditText foodname) {
        foodprice.setFocusableInTouchMode(true);
        foodprice.setFocusable(true);
        foodprice.requestFocus();
        foodname.setFocusableInTouchMode(true);
        foodname.setFocusable(true);
        foodname.requestFocus();
    }


    private void current_vendor() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            user = new utils().GET_VENDOR_CACHED(getApplicationContext(), getString(R.string.VENDOR));
        shopname.setText(" " + user.getUser().getName());
        phone.setText(" " + user.getUser().getPhone());
        business.setText(" " + user.getUser().getBusiness_details());
        new utils().img_load(getApplicationContext(), IMG_URL + user.getUser().getImg_url(), progressBar_img, vendor_img);
        progressBar1.setVisibility(View.GONE);
    }


    //Step 5
    private void credentials(final String m) {
        DocumentReference user = mfirebaseFirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        if (task.getResult().getString("p1").length() > 0 && task.getResult().getString("p2").length() > 0 && task.getResult().getString("p3").length() > 0)
                            send_data_to_s3(imgUri, m, task.getResult().getString("p1"), task.getResult().getString("p2"), task.getResult().getString("p3"));
                    } catch (URISyntaxException e) {
                        message2(e.toString());
                        hide_progress();
                    }

                }
            }
        });
    }


    //Firebase
    private void send_data_to_firebase(String price, String toString1, String pic_key) {
        started_payload = true;
        int res = Integer.parseInt(CHARGES.toString()) + Integer.parseInt(price);
        //message2("Final"+res+"  "+response);
        DocumentReference reference = mfirebaseFirestore.collection(getString(R.string.vendor_uploads)).document("room").collection(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).document();
        Vendor_uploads uploads = new Vendor_uploads(res, toString1, pic_key, string, FirebaseAuth.getInstance().getUid(), 0, 0, reference.getId(), 0);
        reference.set(uploads).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                message2("Uploaded  successfully.. Pls wait image uploading..");
            else
                message2("Error " + task.getException());
        });

    }


    //S3
    private void send_data_to_s3(Uri imgUri, String string, String p1, String p2, String p3) throws URISyntaxException {

        AWSCredentials credentials = new BasicAWSCredentials(p1, p2);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3.setRegion(Region.getRegion(Regions.EU_WEST_3));
        //s3.setObjectAcl("", ".png", CannedAccessControlList.PublicRead);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        String d = Find.get_file_selected_path(imgUri, getApplicationContext());
        TransferObserver trans = transferUtility.upload(p3, string, new File(d));
        trans.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDo = (int) percentDone;

                progress.setText("Uploading... " + percentDo);
                if (percentDo == 100) {
                    progress.setText("Uploaded");
                    hide_progress();
                    foodname.setText("");
                    foodprice.setText("");
                    image_view.setImageResource(R.drawable.plain);
                    started_payload = false;
                }


            }

            @Override
            public void onError(int id, Exception ex) {
                message2(ex.getLocalizedMessage());
                hide_progress();

            }

        });
    }


    //Media selector  Custom ui
    public void file_picker(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, PICK_IMAGE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            assert data != null;
            imgUri = data.getData();
            assert imgUri != null;
            if (imgUri.toString().contains("image")) {
                image_view.setImageURI(imgUri);
                confirm = true;
                progress.setText("");
            } else
                message2("Pls Select an Image.");
        }
    }


    private String getFile_extension(Uri uri) {
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(uri));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q));
    }


    private void populate_drop_down() {
        obj = new ArrayList<>();
        arrays = new ArrayList<>();
        Calls calls = Base_config.getConnection().create(Calls.class);
        Call<List<Map<String, Object>>> search = calls.getCat();
        search.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NotNull Call<List<Map<String, Object>>> call, @NotNull Response<List<Map<String, Object>>> response) {
                obj = response.body();
                assert obj != null;
                for (Map<String, Object> z : obj)
                    arrays.add(Objects.requireNonNull(z.get("category")).toString());
                if (arrays != null)
                    pop_out(arrays);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, @NotNull Throwable t) {
                message2(t.getLocalizedMessage());
            }
        });


    }


    private void pop_out(List<String> list) {
        Collections.reverse(list);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(R.layout.text_pad);
        adapter1.notifyDataSetChanged();
        spinner.setAdapter(adapter1);
        cat.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        if (started_payload)
            new utils().message2("Pls wait Upload in progress", this);
        else
            super.onBackPressed();
    }

    private void message2(String s) {
        new utils().message2(s, this);
    }

    private void show_progress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hide_progress() {
        progressBar.setVisibility(View.GONE);
    }

}