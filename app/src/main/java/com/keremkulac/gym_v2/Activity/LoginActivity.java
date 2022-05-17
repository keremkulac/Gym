package com.keremkulac.gym_v2.Activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

import com.keremkulac.gym_v2.UserFragments.LoginFragment;
import com.keremkulac.gym_v2.R;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Giriş aktivitesi açılınca yapılacaklar
        super.onCreate(savedInstanceState);
        // Giriş aktivitesi açıldığında Giriş fragmentını açar
        setContentView(R.layout.activity_login);
        setMainLayout();
        // Giriş aktivitesi açılırken Exit içinde gelen Bool değeri true ise uygulama kapatılır
        if(getIntent().getBooleanExtra("Exit",false)){
            finish();
        }
    }

    public void setMainLayout(){
        // frame_layout_login frame inde  Giriş fragmentını açar
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.replace(R.id.frame_layout_login,loginFragment).commit();
    }
}