package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.chauvendor.Adapter.Reviews_adapter;
import com.example.chauvendor.R;
import com.example.chauvendor.model.Review_models;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class Reviews extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar mprogressBar7;
    private Reviews_adapter reviews_adapter;



    private  String TAG = "Reviews";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mprogressBar7 = (ProgressBar) findViewById(R.id.progressBar7);
        REQUEST_REVIEWS();
}

    private void REQUEST_REVIEWS() {
        FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid()).document(getIntent().getStringExtra("data")).collection("reviews").get().addOnCompleteListener(o -> {
            if(o.isSuccessful())
                setLayout(o.getResult());
            else
                Log.d(TAG, "Error Occurred onCreate: ");
        });
    }



    private void setLayout(QuerySnapshot o) {
        List<Review_models> reviews = o.toObjects(Review_models.class);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        reviews_adapter = new Reviews_adapter(getApplicationContext(),reviews);
        recyclerView.setAdapter(reviews_adapter);
        mprogressBar7.setVisibility(View.GONE);


    }
    }