package com.example.chauvendor.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.example.chauvendor.R;
import com.example.chauvendor.util.utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Top_section extends Fragment {


    private FrameLayout frameLayout;
    private PopupMenu popup;
    private Bundle bundle;
    private ImageButton sign_in;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_top_section, container, false);

        frameLayout = (FrameLayout) view.findViewById(R.id.frame_use);
        sign_in = (ImageButton) view.findViewById(R.id.sign_ins);

        frameLayout.setOnClickListener(i -> {
        });


        sign_in.setOnClickListener(i -> {
            popup = new PopupMenu(getContext(), i);
            MenuInflater inflater1 = popup.getMenuInflater();
            inflater1.inflate(R.menu.signuser, popup.getMenu());
            popup.show();

            popup.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {

                    case R.id.search:
                        search();
                        return true;


                    case R.id.sign_in:
                        if (FirebaseAuth.getInstance().getUid() == null)
                            signin();
                        else
                            new utils().message2("Pls Sign Out", requireActivity());
                        return true;


                    case R.id.sign_out:
                        signout();
                        return true;
                }
                return false;
            });
        });


        return view;
    }


    public void signin() {
        if (FirebaseAuth.getInstance().getUid() == null)
            startActivity(new Intent(requireContext(), Login.class).putExtra("check_view", String.valueOf(2)));
        else
            new utils().message2("Pls Sign out ", requireActivity());
    }


    public void signout() {
        if (FirebaseAuth.getInstance().getUid() != null)
            UPDATE_DEVICE();
        else
            new utils().message2("Already Signed Out", requireActivity());

    }


    private void UPDATE_DEVICE() {
        new utils().message2(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail() + " Signed Out", requireActivity());
        new utils().init(requireContext()).edit().putString(getString(R.string.LAST_SIGN_IN_VENDOR), FirebaseAuth.getInstance().getUid()).apply();
        new utils().CACHE_VENDOR(null, requireContext(), 0, getString(R.string.VENDOR));
        FirebaseAuth.getInstance().signOut();
    }


    public void search() {
        bundle = new Bundle();
        bundle.putString("ID", "S");
        new utils().open_Fragment(new Search(), "Search", requireContext(), bundle, R.id.frameLayout);
    }
}