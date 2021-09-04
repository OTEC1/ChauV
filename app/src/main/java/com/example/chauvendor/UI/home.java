package com.example.chauvendor.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.chauvendor.Adapter.home_apdater;
import com.example.chauvendor.R;
import com.example.chauvendor.model.Category;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class home extends Fragment {

    private CollectionReference reference;
    private RecyclerView recyclerView;
    private home_apdater adapter;
    private ProgressBar progressBar;
    private FrameLayout realLayout;


    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getUid() != null)
            adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

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
        Query query = reference = FirebaseFirestore.getInstance().collection(getString(R.string.VENDORS_UPLOAD)).document("room").collection(FirebaseAuth.getInstance().getUid());
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        set_layout(options);
    }

    private void set_layout(FirestoreRecyclerOptions<Category> options) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new home_apdater(options);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }
}