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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.UserFragments.TrainerFragment;

import java.util.Map;


public class EmailPasswordUpdateFragment extends Fragment {
    EditText profileEmail2,profileOldPassword,profileNewPassword;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    Button updateButton;
    ImageView emailPasswordUpdateBackButton;
    String title,name,lastname,trainerNameAndLastname;
    FirebaseFirestore firebaseFirestore;
    HomeFragment homeFragment ;
    TrainerFragment trainerFragment ;
    ProfileFragment profileFragment;
    Bundle bundle ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Şifre değiştirme ekranı açılırken yapılacaklr
        View view = inflater.inflate(R.layout.fragment_email_password_update, container, false);
        Init(view);
        backButton();
        passwordUpdate();
        getTitle();
        return view;
    }
    public void getTitle(){
        // Giriş yapmış kullanıcın emailine göre title ı adı ve soyadı alır
        firebaseFirestore.collection("User")
                .whereEqualTo("userEmail",currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String,Object> data = document.getData();
                    title = document.getData().get("userTitle").toString();
                    name = document.getData().get("userName").toString();
                    lastname = document.getData().get("userLastname").toString();
                    trainerNameAndLastname = name + " " + lastname;
                }
            }
        });
    }

    public void Init(View view){
        // Instance lar oluşturuluyor
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        homeFragment = new HomeFragment();
        trainerFragment = new TrainerFragment();
        profileFragment = new ProfileFragment();
        currentUser = auth.getCurrentUser();
        profileEmail2 = view.findViewById(R.id.profileEmail2);
        profileOldPassword = view.findViewById(R.id.profileOldPassword);
        profileNewPassword = view.findViewById(R.id.profileNewPassword);
        updateButton = view.findViewById(R.id.updateButton);
        emailPasswordUpdateBackButton = view.findViewById(R.id.emailPasswordUpdateBackButton);
        bundle = new Bundle();
    }

    public void passwordUpdate(){

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kullanıcının emailini eski ve yeni şifresini girilmesini ister
                //  email, eski ve yeni şifre boş değilse şifre başarıyla değiştirilir
                String email = profileEmail2.getText().toString();
                String oldPassword = profileOldPassword.getText().toString();
                String newPassword = profileNewPassword.getText().toString();
                if(email.equals("") || oldPassword.equals("") || newPassword.equals("")) {
                    Toast.makeText(getActivity(),"Lütfen bilgilerinizi giriniz",Toast.LENGTH_LONG).show();
                }else{
                    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        currentUser.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(getActivity(),"Şifreniz başarıyla değiştirildi",Toast.LENGTH_LONG).show();
                                                            // Şifre değiştirme başarılıysa ve kullanıcının title ı Sporcu ise anasayfaya gider
                                                            if(title.equals("Sporcu")){
                                                                setFragments(homeFragment,R.id.main_activity_frame_layout);
                                                            }else{
                                                                // Şifre değiştirme başarılıysa ve kullanıcının title ı Eğitmen ise
                                                                // Eğitmen sayfasına gider ve kullanıcının adını ve soyadını götürür
                                                                setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,trainerNameAndLastname);
                                                            }
                                                        }else{
                                                            Toast.makeText(getActivity(),"Şifreniz değiştirilemedi",Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(getActivity(),"Kimlik doğrulama başarısız",Toast.LENGTH_LONG).show();;
                                    }
                                }
                            });
                }
            }
        });
    }

    public void backButton(){
        emailPasswordUpdateBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Geri butonuna tıklandığında kullanıcının title ı Sporcu ise Profil sayfasına geri gider
                if(title.equals("Sporcu")){
                    setFragmentsBundle(profileFragment,bundle,R.id.main_activity_frame_layout,"Sporcu");
                 }else if(title.equals("Eğitmen")){
                    //  Geri butonuna tıklandığında kullanıcının title ı Eğitmen ise Eğitmen sayfasına geri gider
                    // ve kullanıcının adını soyadını götürür
                    setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,trainerNameAndLastname);
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