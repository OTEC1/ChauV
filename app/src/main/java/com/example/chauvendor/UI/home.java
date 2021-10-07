package com.example.chauvendor.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.chauvendor.Adapter.home_apdater;
import com.example.chauvendor.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home extends Fragment {


    private RecyclerView recyclerView;
    private home_apdater adapter;
    private ProgressBar progressBar;
    private FrameLayout realLayout;


    private List<Map<String, Object>> options;
    private  String TAG = "home";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        realLayout = (FrameLayout) view.findViewById(R.id.frame);
        realLayout.setOnClickListener(k -> {

        });
        if (FirebaseAuth.getInstance().getUid() != null)
            api_call2();

        return view;
    }


    private void api_call2() {
        options = new ArrayList<>();
        FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room")
                .collection(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(u -> {
            if (u.isSuccessful()) {
                List<DocumentSnapshot> s = u.getResult().getDocuments();
                for (DocumentSnapshot z : s) {
                    Map<String, Object> o = new HashMap<>();
                    o.put("category", z.get("category"));
                    o.put("food_price", z.get("food_price"));
                    o.put("food_name", z.get("food_name"));
                    o.put("doc", z.get("doc"));
                    o.put("img_url", z.get("img_url"));
                    options.add(o);
                    set_layout(options);
                }
            }
            else
                Log.d(TAG, "api_call2:  Error Occurred "+u.getException());
        });

    }

    private void set_layout(List<Map<String, Object>> options) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new home_apdater(options,getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }
}