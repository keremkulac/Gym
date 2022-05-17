package com.keremkulac.gym_v2.ProgressButtons;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.keremkulac.gym_v2.R;

public class RegisterProgressButton {
    CardView registerCardView;
    ProgressBar registerProgressBar;
    ConstraintLayout register_constraint_layout;
    TextView progressRegister;
    Animation fade_in;

    public RegisterProgressButton(Context context, View view){
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        registerCardView = view.findViewById(R.id.registerCardView);
        register_constraint_layout = view.findViewById(R.id.register_constraint_layout);
        registerProgressBar = view.findViewById(R.id.registerProgressBar);
        progressRegister = view.findViewById(R.id.progressRegister);
    }

    public void Activated(){
        registerProgressBar.setAnimation(fade_in);
        registerProgressBar.setVisibility(View.VISIBLE);
        progressRegister.setAnimation(fade_in);
        progressRegister.setText("KAYIT OLUŞTURULUYOR");
    }
    public void Finished(){
        registerProgressBar.setVisibility(View.GONE);
        progressRegister.setText("KAYIT OL");
    }
}
