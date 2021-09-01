package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.example.chauvendor.model.Vendor_uploads;
import com.example.chauvendor.util.Find;
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.chauvendor.constant.Constants.CHARGES;
import static com.example.chauvendor.constant.Constants.IMG_URL;
import static com.example.chauvendor.constant.Constants.PICK_IMAGE;

public class Vendor_account extends AppCompatActivity {


    private ProgressBar progressBar, progressBar1, progressBar_img;
    private BottomNavigationView bottomNavigationView;
    private Button pic_select, upload1, view_review;
    private EditText foodprice, foodname;
    private CircleImageView vendor_img;
    private TextView shopname, progress;
    private Bundle bundle = new Bundle();
    private ImageView image_view;
    private Spinner spinner;
    private Uri imgUri;


    private boolean confirm = false;
    private String string = "Indicate", p1, p2, p3, pic_key, TAG = "accountFragment";


    private FirebaseFirestore mfirebaseFirestore;
    private SharedPreferences sp;
    private ArrayList arrayList;
    private ArrayAdapter adapter1;


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
        progress = (TextView) findViewById(R.id.progress);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, bundle);


        if (FirebaseAuth.getInstance().getUid() != null)
            current_vendor();

        populate_drop_down();


        pic_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_picker(view);
            }
        });


        if (CHARGES == null)
            new utils().quick_commission_call(mfirebaseFirestore, TAG);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                string = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (FirebaseAuth.getInstance().getUid() == null)
                    message2("Pls Sign in.");
                else if (string.equals("Indicate"))
                    message2("Pls indicate section to upload");
                else if (CHARGES == null) {
                    new utils().quick_commission_call(mfirebaseFirestore, TAG);
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
            }
        });

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

    private void current_vendor() {

        if (new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.VENDOR_NAME), null) != null) {
            shopname.setText(new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.VENDOR_NAME), ""));
            new utils().img_load(getApplicationContext(), IMG_URL + new utils().instantiate_shared_preferences(sp, getApplicationContext()).getString(getString(R.string.VENDOR_IMG_URL), ""), progressBar_img, vendor_img);
            progressBar1.setVisibility(View.GONE);
        } else {
            DocumentReference user = mfirebaseFirestore.collection(getString(R.string.Vendor_reg)).document(FirebaseAuth.getInstance().getUid());
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        shopname.setText(String.valueOf(task.getResult().get("name")));
                        progressBar1.setVisibility(View.GONE);
                        new utils().img_load(getApplicationContext(), IMG_URL + task.getResult().get("img_url"), progressBar_img, vendor_img);
                        I(getString(R.string.VENDOR_NAME), task.getResult().get("name").toString());
                        I(getString(R.string.VENDOR_IMG_URL), task.getResult().get("img_url").toString());
                    }
                }
            });
        }
    }


    private void I(String a, String s) {
        new utils().instantiate_shared_preferences(sp, getApplicationContext()).edit().putString(a, s).apply();
    }


    //Step 5
    private void credentials(final String m) {

        DocumentReference user = mfirebaseFirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    p1 = task.getResult().getString("p1");
                    p2 = task.getResult().getString("p2");
                    p3 = task.getResult().getString("p3");
                    //System.out.println(id + " " + p1 + "  " + p2 + "  " + p3);

                    try {
                        if (p1.length() > 0 && p2.length() > 0 && p3.length() > 0)
                            send_data_to_s3(imgUri, m, p1, p2, p3);
                    } catch (URISyntaxException e) {
                        message2(e.toString());
                        Log.d(TAG, e.toString());
                        hide_progress();
                    }

                }
            }
        });
    }


    private void send_data_to_firebase(String price, String toString1, String pic_key) {

        int res = Integer.parseInt(CHARGES.toString()) + Integer.parseInt(price);
        //message2("Final"+res+"  "+response);
        DocumentReference reference = mfirebaseFirestore.collection(getString(R.string.vendor_uploads)).document("room").collection(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).document();
        Vendor_uploads uploads = new Vendor_uploads(res, toString1, pic_key, string, FirebaseAuth.getInstance().getUid(), 0, 0, reference.getId(), 0);
        reference.set(uploads).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message2("Uploaded  successfully.. Pls wait image uploading..");
                } else
                    message2("Error " + task.getException());
            }
        });

    }


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
                }


            }

            @Override
            public void onError(int id, Exception ex) {
                message2(ex.getLocalizedMessage());
                Log.d(TAG, ex.getLocalizedMessage());
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
        arrayList = new ArrayList<>();
        arrayList.add("Indicate");
        arrayList.add("Yam");
        arrayList.add("Rice");
        arrayList.add("Pap");
        arrayList.add("Ewa");
        arrayList.add("Ewka");
        arrayList.add("Abacha");
        arrayList.add("Noodle");
        arrayList.add("Swallow");
        arrayList.add("Tea and beard");
        arrayList.add("Noodle and egg");

        adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        adapter1.setDropDownViewResource(R.layout.text_pad);
        spinner.setAdapter(adapter1);

    }


}