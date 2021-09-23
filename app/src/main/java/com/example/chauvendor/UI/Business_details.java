package com.example.chauvendor.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chauvendor.R;


public class Business_details extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_business_details, container, false);


        onComplete_registration();
        return view;
    }

    private void onComplete_registration() {

        startActivity(new Intent(getContext(), Login.class).putExtra("check_view", String.valueOf(1)));

    }
}