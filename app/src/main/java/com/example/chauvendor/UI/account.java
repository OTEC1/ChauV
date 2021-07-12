 package com.example.chauvendor.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.chauvendor.R;
import com.example.chauvendor.constant.Constants;
import com.example.chauvendor.model.Vendor_uploads;
import com.example.chauvendor.util.Find;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.chauvendor.constant.Constants.PICK_IMAGE;


 public class account extends Fragment {

    private Button pic_select, upload1, view_review;
    private EditText foodprice, foodname;
    private Uri imgUri;
    private ImageView image_view;
    private CircleImageView vendor_img;
    private TextView shopname,progress;
    private Spinner spinner;
    private ProgressBar progressBar,progressBar1,progressBar_img;

     private boolean confirm = false, verified = false;
     private  String string="Indicate", p1,p2,p3,TAG ="accountFragment";
     private  static  String responsed="";


    private FirebaseFirestore mfirebaseFirestore;
    private SharedPreferences sp;
    private ArrayList arrayList;
    private ArrayAdapter adapter1;


    private void start_pref() { sp = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE); }


    @Override
    public void onResume() {
        super.onResume();
        mfirebaseFirestore =FirebaseFirestore.getInstance();
        if(responsed.trim().length()<=0)
            responsed = quick_commission_call();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        pic_select = (Button) view.findViewById(R.id.pic_select);
        upload1 = (Button) view.findViewById(R.id.upload);
        view_review = (Button) view.findViewById(R.id.view_review);
        foodprice = (EditText) view.findViewById(R.id.foodprice);
        foodname = (EditText) view.findViewById(R.id.foodname);
        image_view = (ImageView) view.findViewById(R.id.image_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar3);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar4);
        progressBar_img = (ProgressBar) view.findViewById(R.id.progressBar5);
        vendor_img = (CircleImageView) view.findViewById(R.id.vendor_img);
        spinner = (Spinner) view.findViewById(R.id.spinners);
        shopname = (TextView) view.findViewById(R.id.shop_name);
        progress = (TextView) view.findViewById(R.id.progress);



        if(FirebaseAuth.getInstance().getUid() != null)
            current_vendor();

        start_pref();
        populate_drop_down();


        pic_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_picker(view);
            }
        });




        if(responsed.trim().length()<=0)
            responsed = quick_commission_call();



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


                Objects.requireNonNull(sp.getString("user_email", "")).trim();
                if (sp.getString("user_email", "").trim().length() <= 0)
                    message2("Pls Sign in.");

                else
                if(string.equals("Indicate"))
                    message2("Pls indicate section to upload");
                else
                if(responsed.trim().length()<=0)
                    responsed = quick_commission_call();
                else
                {
                    if (confirm && foodprice.getText().toString().length()>0 && foodname.getText().toString().length()>0 && verified && responsed!=null) {
                        show_progress();
                        String pic_key = getFile_extension(imgUri);
                        if (pic_key.equalsIgnoreCase("png") | pic_key.equalsIgnoreCase("jpg") | pic_key.equalsIgnoreCase("jpeg") | pic_key.equalsIgnoreCase("webp") ) {
                            String in = generate_name().concat(".png");

                            send_data_to_firebase(foodprice.getText().toString(), foodname.getText().toString(), in,responsed);
                            credentials(in);

                        }
                        else
                        if(!verified)
                            message2("Pls Reload Page ! ");
                        else {
                            message2("Pls select an image");
                            hide_progress();
                        }
                    }
                    else
                    if(foodprice.getText().toString().length()<=0 | foodname.getText().toString().length()<=0) {
                        hide_progress();
                        message2("Pls fill out both fields");
                    }
                    else
                    if(!confirm) {
                        hide_progress();
                        message2("Pls select Food Picture");
                    }
                }
            }
        });

        return view;
    }



    private void current_vendor() {
        DocumentReference user =mfirebaseFirestore.collection("Vendor Registration").document(FirebaseAuth.getInstance().getUid());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    shopname.setText(String.valueOf(task.getResult().get("name")));
                    progressBar1.setVisibility(View.GONE);
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);

                    img_load(requestOptions,task);
                }
            }});


    }






     private void img_load(RequestOptions requestOptions, Task<DocumentSnapshot> task) {
         Glide.with(getContext())
                 .load(Constants.IMG_URL.concat(String.valueOf(task.getResult().get("img_url"))))
                 .listener(new RequestListener<Drawable>() {
                     @Override
                     public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                         progressBar_img.setVisibility(View.GONE);
                         return false;
                     }

                     @Override
                     public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                         progressBar_img.setVisibility(View.GONE);
                         return false;
                     }
                 })
                 .apply(requestOptions)
                 .into(vendor_img);

     }




     private String quick_commission_call() {

        DocumentReference user =mfirebaseFirestore.collection("west").document("token");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    responsed=String.valueOf(task.getResult().get("tokens"));
                    verified = true;
                }
                else
                    Log.d(TAG, String.valueOf(task.getException()));
            }});

        return responsed;

    }



    //Step 5
    private void credentials(final String m) {

        DocumentReference user =mfirebaseFirestore.collection("east").document("lab");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    p1 = task.getResult().getString("p1");
                    p2 = task.getResult().getString("p2");
                    p3 = task.getResult().getString("p3");
                    //System.out.println(id + " " + p1 + "  " + p2 + "  " + p3);

                    try {
                        if(p1.length()>0 && p2.length()>0 && p3.length()>0)
                            send_data_to_s3(imgUri,m,p1,p2,p3);
                    } catch (URISyntaxException e) {
                        message2(e.toString());
                        Log.d(TAG,e.toString());
                        hide_progress();
                    }

                }
            }
        });
    }




    private void send_data_to_firebase(String price, String toString1, String pic_key,String response) {

        int  res= Integer.parseInt(response)+Integer.parseInt(price);
        //message2("Final"+res+"  "+response);
        DocumentReference reference = mfirebaseFirestore.collection(getString(R.string.vendor_uploads)).document("room").collection(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).document();
        Vendor_uploads uploads = new Vendor_uploads(res, toString1, pic_key,string,FirebaseAuth.getInstance().getUid(),0,0, reference.getId());
        reference.set(uploads).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    message2("Uploaded  successfully.. Pls wait image uploading..");
                    verified= false;
                    responsed = "";
                }
                else
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
        TransferUtility transferUtility = new TransferUtility(s3, getActivity());
        String d = Find.get_file_selected_path(imgUri, getContext());
        TransferObserver trans = transferUtility.upload(p3, string, new File(d));
        trans.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDo = (int) percentDone;

                progress.setText("Uploading... "+percentDo);
                if(percentDo == 100) {
                    progress.setText("Uploaded");
                    hide_progress();
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
        return mine.getExtensionFromMimeType(getActivity().getContentResolver().getType(uri));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q));
    }


    private void populate_drop_down(){
        arrayList =new ArrayList<>();
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

        adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
        adapter1.setDropDownViewResource(R.layout.text_pad);
        spinner.setAdapter(adapter1);

    }


    private void show_progress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hide_progress() {
        progressBar.setVisibility(View.INVISIBLE);
    }





    private void message2(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

}