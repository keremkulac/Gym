package com.keremkulac.gym_v2.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keremkulac.gym_v2.R;


public class HomeFragment extends Fragment {
    ImageView phone;
    TextView gymPhoneNumber;
    private static final int REQUEST_CALL = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Ana sayfa  ekranı açılırken yapılacaklr
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        phone = view.findViewById(R.id.homePhone);
        gymPhoneNumber = view.findViewById(R.id.gymPhoneNumber);
        phoneCall();
        phoneCall2();
        return view;
    }
    public void phoneCall2(){
        // Telefon numarasına tıklandığıda yapılacak
        gymPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
    }

    public void phoneCall(){
        // Telefon iconuna tıklandığında yapılacak
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
    }

    // Telefon numarasını alıp gerekli izin verilirse arama yapar
    public void makePhoneCall(){
        String number = gymPhoneNumber.getText().toString();
        if (number.trim().length() > 0){
            // Kullanıcının telefon ile arama yapması için gerekli izinler alınır
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else{
                String dial = "tel:"+ number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }else{
            Toast.makeText(getActivity(),"Lütfen telefon numarası giriniz",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            // İzin verilmişse arama yapılır
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }else{
                Toast.makeText(getActivity(),"İzin gerekli",Toast.LENGTH_LONG).show();
            }
        }
    }
}