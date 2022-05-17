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

public class LoginProgressButton {
     CardView loginCardView;
     ProgressBar loginProgressBar;
     ConstraintLayout login_constraint_layout;
     TextView progressLogin;
     Animation fade_in;

    public LoginProgressButton(Context context, View view){
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        loginCardView = view.findViewById(R.id.loginCardView);
        login_constraint_layout = view.findViewById(R.id.login_constraint_layout);
        loginProgressBar = view.findViewById(R.id.loginProgressBar);
        progressLogin = view.findViewById(R.id.progressLogin);
    }

    public void Activated(){
        loginProgressBar.setAnimation(fade_in);
        loginProgressBar.setVisibility(View.VISIBLE);
        progressLogin.setAnimation(fade_in);
        progressLogin.setText("GİRİŞ YAPILIYOR");
    }
    public void Finished(){
        loginProgressBar.setVisibility(View.GONE);
        progressLogin.setText("GİRİŞ YAP");
    }

}
