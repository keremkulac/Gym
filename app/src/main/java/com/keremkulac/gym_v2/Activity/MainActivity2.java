package com.keremkulac.gym_v2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.keremkulac.gym_v2.Fragments.AppointmentFragment;
import com.keremkulac.gym_v2.Fragments.HomeFragment;
import com.keremkulac.gym_v2.Fragments.LocationFragment;
import com.keremkulac.gym_v2.Fragments.ProfileFragment;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.UserFragments.TrainerFragment;

public class MainActivity2 extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    HomeFragment homeFragment ;
    TrainerFragment trainerFragment ;
    ProfileFragment profileFragment;
    LocationFragment locationFragment;
    AppointmentFragment appointmentFragment ;
    Bundle bundle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Main aktivitesi açılırken yapılacaklar
        super.onCreate(savedInstanceState);
        // Main aktivitesi açıldığında Ana sayfa fragment ını main_activity_frame_layout frame inde açar
        setContentView(R.layout.activity_main2);
        homeFragment = new HomeFragment();
        setFragments(homeFragment,R.id.main_activity_frame_layout);
        Init();
    }
    public void Init(){
        navigationBarSelect();
        auth = FirebaseAuth.getInstance();
        trainerFragment = new TrainerFragment();
        profileFragment = new ProfileFragment();
        locationFragment = new LocationFragment();
        appointmentFragment = new AppointmentFragment();
        bundle = new Bundle();
    }

    public void navigationBarSelect(){
        // Kullanıcının Main aktivitesinde bulunan bottombar da seçtiği sayfayı açar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_activity_bottombar_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottombar_home:
                        goToHomeFragment();
                        return true;
                    case R.id.bottombar_account:
                        goToProfileFragment();
                        Toast.makeText(getApplicationContext(),"Account",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottombar_appointment:
                        goToAppointmentFragment();
                        return true;
                    case R.id.bottombar_location:
                        goToLocationFragment();
                        return true;

                    case R.id.bottombar_exit:
                        exitApp();
                        return true;
                    default:
                        return  false;
                }
            }
        });
    }

    public void goToAppointmentFragment(){
        setFragments(appointmentFragment,R.id.main_activity_frame_layout);
    }
    public void goToHomeFragment() {

        setFragments(homeFragment,R.id.main_activity_frame_layout);
    }
    public void goToProfileFragment(){
        setFragments(profileFragment,R.id.main_activity_frame_layout);
    }
    public void goToLocationFragment(){
        setFragments(locationFragment,R.id.main_activity_frame_layout);
    }
    public void exitApp(){
        // Kullanıcı Exit butonua tıkladığında Exit içine true değeri koyar ve Giriş  aktivitesini açar
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit",true);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Ekranın sağ üstündeki Menü den kullanıcının seçinine göre işlemleri yapar
        if(item.getItemId() == R.id.logout){
            // Kullanıcı logout kısmını seçerse kullanıcı çıkışı sağlanır ve Giriş aktivitesi açılır
            auth.signOut();
            Intent loginIntent = new Intent(MainActivity2.this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    // Verilen frame de seçilen fragment açılır
    public void setFragments(Fragment fragment,int layoutID){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutID,fragment).commit();
    }
}