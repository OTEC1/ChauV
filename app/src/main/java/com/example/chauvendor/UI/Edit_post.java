package com.example.chauvendor.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.chauvendor.R;
import com.example.chauvendor.constant.Constants;
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Edit_post extends Fragment {


    private EditText prices, food_name;
    private ImageView imageView;
    private ProgressBar progressBar2, mprogressBar4;
    private Button delete, update;


    private String docs, img = "", TAG = "Edit_post";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);


        prices = (EditText) view.findViewById(R.id.prices);
        food_name = (EditText) view.findViewById(R.id.food_name);
        delete = (Button) view.findViewById(R.id.delete);
        update = (Button) view.findViewById(R.id.post_update);
        imageView = (ImageView) view.findViewById(R.id.img_urls);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);
        mprogressBar4 = (ProgressBar) view.findViewById(R.id.bastard_variable);


        assert getArguments() != null;
        String[] x = getArguments().getStringArray("data");
        for (int y = 0; y < x.length; y++) {
            docs = x[0];
            food_name.setText(x[1]);
            prices.setText(x[2]);
            img = x[3];
            if (!img.trim().isEmpty())
                new utils().img_load(getContext(), Constants.IMG_URL + img, progressBar2, imageView);
        }


        update.setOnClickListener(q -> {
            mprogressBar4.setVisibility(View.VISIBLE);
            if (!prices.getText().toString().trim().isEmpty() && !food_name.getText().toString().trim().isEmpty())
                FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid()).document(docs)
                        .update(MAP()).addOnCompleteListener(h -> {
                    if (h.isSuccessful()) {
                        new utils().message2("Updated successfully ", requireActivity());
                        mprogressBar4.setVisibility(View.INVISIBLE);
                    }
                    else
                        new utils().message2("error occurred " + h.getException(), requireActivity());
                });
            else
                new utils().message2("Pls fill out both field !", requireActivity());

        });


        delete.setOnClickListener(h -> {
            mprogressBar4.setVisibility(View.VISIBLE);
            FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid()).document(docs)
                    .delete().addOnCompleteListener(a -> {
                if (a.isSuccessful())
                    Drop_reviews();
                else
                    new utils().message2("error occurred " + a.getException(), requireActivity());

            });
        });


        return view;
    }

    private void Drop_reviews() {
        FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid()).document(docs).collection("reviews")
                .get().addOnCompleteListener(b -> {
            if (b.isSuccessful()) {
                List<DocumentSnapshot> oj = b.getResult().getDocuments();
                for (DocumentSnapshot s : oj) {
                    FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid()).document(s.getId())
                            .delete().addOnCompleteListener(a -> {
                        if (a.isSuccessful()) {
                            Log.d(TAG, " Deleted ");
                            credentials();
                        }
                        else
                            Log.d(TAG, "Error occurred " + a.getException());

                        mprogressBar4.setVisibility(View.INVISIBLE);
                    });
                }
            } else
                Log.d(TAG, "onComplete: Bad request");

        });


    }


    private void credentials() {
        FirebaseFirestore.getInstance().collection("east").document("lab")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        if (Objects.requireNonNull(task.getResult().getString("p1")).length() > 0 && Objects.requireNonNull(task.getResult().getString("p2")).length() > 0 && Objects.requireNonNull(task.getResult().getString("p3")).length() > 0)
                            S3_drop(task.getResult().getString("p1"), task.getResult().getString("p2"), task.getResult().getString("p3"));
                    } catch (Exception e) {
                        new utils().message2(e.toString(), requireActivity());
                        Log.d(TAG, e.toString());
                        mprogressBar4.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
    }


    private void S3_drop(String p1, String p2, String p3) {

        AWSCredentials credentials = new BasicAWSCredentials(p1, p2);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
        s3.setRegion(Region.getRegion(Regions.EU_WEST_3));
        if (s3.doesObjectExist(p3, img.substring(img.lastIndexOf("/") + 1))) {
            s3.deleteObject(p3, img.substring(img.lastIndexOf("/") + 1));
            startActivity(new Intent(getContext(), MainActivity.class));
        }else
            new utils().message2("File Doesn't exist", requireActivity());
    }

    private Map<String, Object> MAP() {
        Map<String, Object> o = new HashMap<>();
        o.put("food_name", food_name.getText().toString());
        o.put("food_price", Long.valueOf(prices.getText().toString()));
        return o;
    }




}