package com.example.chauvendor.widget;

import android.view.View;
import android.widget.LinearLayout;

public class ViewHeightAnimation {
    private View view;

    public ViewHeightAnimation(View view){
        if(view.getLayoutParams() instanceof LinearLayout.LayoutParams)
            this.view = view;
        else
            throw  new IllegalArgumentException("Linear Layout expected");
    }



    public  void setWeight(float weight){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.getParent().requestLayout();
    }

    public  float getWeight(){
        return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    }
}
