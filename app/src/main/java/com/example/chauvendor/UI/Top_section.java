package com.example.chauvendor.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.chauvendor.R;


public class Top_section extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view =(ViewGroup) inflater.inflate(R.layout.fragment_top_section, container, false);
        return view;
    }



}