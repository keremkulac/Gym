package com.keremkulac.gym_v2.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.UserFragments.TrainerFragment;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    EditText profileName, profileLastname,profileEmail,profilePhoneNumber,profileRegisterDate;
    ImageView profileFragmentProfileImage,profileFragmentBackButton;
    Button profileUpdate;
    String name,lastname,phoneNumber,email,currentID,url,registerDate,title,trainerNameAndLastname,currentEmail;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firebaseFirestore;
    TextView passwordUpdateButton;
    String nameLastname;
    Bundle bundle ;
    HomeFragment homeFragment ;
    TrainerFragment trainerFragment ;
    EmailPasswordUpdateFragment emailPasswordUpdateFragment ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Profil ekranı açılırken yapılacaklr
       View view = inflater.inflate(R.layout.fragment_profile, container, false);
       Init(view);
       getData();
       emailPasswordUpdate();
       update();
       back();
       return view;
    }
    public void Init(View view){
        profileName = view.findViewById(R.id.profileName);
        profileLastname = view.findViewById(R.id.profileLastname);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePhoneNumber = view.findViewById(R.id.profilePhoneNumber);
        profileRegisterDate = view.findViewById(R.id.profileRegisterDate);
        profileFragmentBackButton = view.findViewById(R.id.profileFragmentBackButton);
        profileFragmentProfileImage = view.findViewById(R.id.profileFragmentProfileImage);
        profileUpdate = view.findViewById(R.id.profileUpdate);
        passwordUpdateButton = view.findViewById(R.id.passwordUpdateButton);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        homeFragment = new HomeFragment();
        trainerFragment = new TrainerFragment();
        emailPasswordUpdateFragment = new EmailPasswordUpdateFragment();
        bundle = new Bundle();
    }


    public void getData(){
        // Kullanıcının kayıt ID si ve email i alınır
        currentID = currentUser.getUid();
        currentEmail = currentUser.getEmail();
        // Kullanıcının email ine göre kullanıcının adı,soyadı,telefon numarası,email i kayıt tarihi
        // ve kullanıcının  alınır ve TextView, EditText lere yazılır
        // Kullanıcının profil fotoğrafının url si alıp Picasso kütüphanesini kullanarak ImageView içine konulur
        firebaseFirestore.collection("User")
                .whereEqualTo("userEmail",currentEmail)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name = document.getData().get("userName").toString();
                        lastname = document.getData().get("userLastname").toString();
                        phoneNumber = document.getData().get("userPhoneNumber").toString();
                        email = document.getData().get("userEmail").toString();
                        registerDate = document.getData().get("userRegisterDate").toString();
                        url = document.getData().get("downloadUrl").toString();
                        title = document.getData().get("userTitle").toString();
                        Picasso.get().load(url).resize(150,120).into(profileFragmentProfileImage);
                        profileName.setText(name);
                        profileLastname.setText(lastname);
                        profileEmail.setText(email);
                        profilePhoneNumber.setText(phoneNumber);
                        profileRegisterDate.setText(registerDate);
                        trainerNameAndLastname = name + " " + lastname;
                        // Kullanıcının title ı eğitmen isi geri butonu görünür olur
                        if(title.equals("Eğitmen")){
                            profileFragmentBackButton.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void update(){
        // Kişisel bilgileri güncelle butonuna tıklandığında ad, soyad ve telefon numarası
        // User collection unda kullanıcının ID sine göre günceller
        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("User").document(currentID)
                        .update(
                                "userName",profileName.getText().toString(),
                                "userLastname",profileLastname.getText().toString(),
                                "userPhoneNumber",profilePhoneNumber.getText().toString());
                nameLastname = (profileName.getText().toString())+" "+(profileLastname.getText().toString());
                if(title.equals("Sporcu")){
                    // Kullanıcının title ı Sporcu ise güncelle butonuna tıklandığında Ana sayfa sayfasına geçer
                    Toast.makeText(getActivity(),"Güncelleme işlemi başarılı",Toast.LENGTH_LONG).show();
                    setFragments(homeFragment,R.id.main_activity_frame_layout);
                }else{
                    // Kullanıcının title ı Eğitmen ise güncelle butonuna tıklandığında Eğitmen sayfasına
                    //kullanıcı adı ve soyadını alarak geçer
                    Toast.makeText(getActivity(),"Güncelleme işlemi başarılı",Toast.LENGTH_LONG).show();
                    setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,nameLastname);
                }
            }
         });
    }

    public void emailPasswordUpdate(){
        passwordUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Şifre değiştir butonuna tıklandığında kullanıcının title ı Sporcu ise Şifre değiştirme sayfası
                // main_activity_frame_layout Frame inde açılır
                if(title.equals("Sporcu")){
                    setFragments(emailPasswordUpdateFragment,R.id.main_activity_frame_layout);
                }else{
                    // Şifre değiştir butonuna tıklandığında kullanıcının title ı Eğitmen ise Şifre değiştirme sayfası
                    // frame_layout_login Frame inde açılır
                    setFragments(emailPasswordUpdateFragment,R.id.frame_layout_login);
                }
            }
        });
    }

    public void back(){
        profileFragmentBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.equals("Eğitmen")) {
                    // Geri butonuna tıklandığında kullanıcının title ı Eğitmen ise Eğitmen sayfasına gider
                    setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,trainerNameAndLastname);
                }
                else if(title.equals("Sporcu")){
                    // Geri butonuna tıklandığında kullanıcının title ı Sporcu ise Ana sayfa sayfasına gider
                    setFragments(homeFragment,R.id.main_activity_frame_layout);
                }
            }
        });
    }
    // İstenilen Fragmentı açar
    public void setFragments(Fragment fragment,int layoutID){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutID,fragment).commit();
    }

    // İstenilen Fragmentı yanında verilen String ile açar
    public void setFragmentsBundle(Fragment fragment, Bundle bundle, int layoutID, String putString){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(putString.equals("Eğitmen") || putString.equals("Sporcu")){
            bundle.putString("userTitle",putString);
        }else{
            bundle.putString("trainerNameLastname",putString);
        }
        fragment.setArguments(bundle);
        transaction.replace(layoutID,fragment).commit();
    }
}