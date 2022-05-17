package com.keremkulac.gym_v2.UserFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.os.Handler;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.keremkulac.gym_v2.Activity.MainActivity2;
import com.keremkulac.gym_v2.Fragments.SignUpFragment;
import com.keremkulac.gym_v2.ProgressButtons.LoginProgressButton;
import com.keremkulac.gym_v2.R;

public class LoginFragment extends Fragment {
    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    Button signUpButton,signInButton;
    EditText loginEmail,loginPassword;
    String trainerName,trainerLastname,trainerNameAndLastname,email,title,password,loggedUserTitle,currentEmail;
    SignUpFragment signUpFragment ;
    AdminFragment adminFragment ;
    TrainerFragment trainerFragment ;
    Bundle bundle ;
    View progressLoginButton;
    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Init(view);
        loggedUser();
        signUp();
        signIn();
        return view;
    }

    public void Init(View view){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        bundle = new Bundle();
        signUpFragment = new SignUpFragment();
        adminFragment = new AdminFragment();
        trainerFragment = new TrainerFragment();
        firebaseFirestore = FirebaseFirestore.getInstance();
        signUpButton = view.findViewById(R.id.signUpButton);
        loginEmail = view.findViewById(R.id.loginEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
        progressLoginButton = view.findViewById(R.id.progressLoginButton);


    }

    public void loggedUser(){
        if(currentUser != null){
            currentEmail = currentUser.getEmail();
            firebaseFirestore.collection("User")
                    .whereEqualTo("userEmail",currentEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                loggedUserTitle = document.getData().get("userTitle").toString();
                                String name = document.getData().get("userName").toString();
                                String lastname = document.getData().get("userLastname").toString();
                                trainerNameAndLastname = name+" "+lastname;
                                System.out.println("LoggedUserTitle"+loggedUserTitle);
                                if("Sporcu".equals(loggedUserTitle)){
                                    Toast.makeText(getActivity(),"Sporcu girişi başarılı",Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(getActivity(), MainActivity2.class);
                                    mainIntent.putExtra("userTitle","Sporcu");
                                    startActivity(mainIntent);
                                }else if("Eğitmen".equals(loggedUserTitle)){
                                    Toast.makeText(getActivity(),"Eğitmen girişi başarılı",Toast.LENGTH_LONG).show();
                                    setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,trainerNameAndLastname);
                                }else{
                                    Toast.makeText(getActivity(),"Admin girişi başarılı",Toast.LENGTH_SHORT).show();
                                    setFragments(adminFragment,R.id.frame_layout_login);
                                }
                            }
                        }else{
                            Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

                    }
            }

    public void signUp(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragmentsBundle(signUpFragment,bundle,R.id.frame_layout_login,"Sporcu");
            }
        });
    }

    public void signIn(){
        progressLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginProgressButton loginProgressButton = new LoginProgressButton(getActivity(),view);
                loginProgressButton.Activated();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        email = loginEmail.getText().toString();
                        password = loginPassword.getText().toString();
                        firebaseFirestore.collection("User")
                                .whereEqualTo("userEmail",email)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                title = document.getData().get("userTitle").toString();
                                                trainerName = document.getData().get("userName").toString();
                                                trainerLastname = document.getData().get("userLastname").toString();
                                                trainerNameAndLastname = (trainerName+" "+trainerLastname);
                                                System.out.println(title);
                                            }
                                        }else{
                                            Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        if(email.equals("") || password.equals("")){
                            Toast.makeText(getActivity(),"Lütfen email ve şifrenizi giriniz",Toast.LENGTH_LONG).show();
                        }else{
                            if(email.equals("admin@gmail.com" ) && password.equals("admin123")){
                                Toast.makeText(getActivity(),"Admin girişi başarılı",Toast.LENGTH_SHORT).show();
                                setFragments(adminFragment,R.id.frame_layout_login);
                            }else{
                                auth.signInWithEmailAndPassword(email,password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                //   currentUserEmail = auth.getCurrentUser().getEmail();
                                                //  System.out.println(currentUserEmail);

                                                if("Sporcu".equals(title)){
                                                    Toast.makeText(getActivity(),"Sporcu girişi başarılı",Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(getActivity(), MainActivity2.class);
                                                    startActivity(mainIntent);
                                                }else if("Eğitmen".equals(title)){
                                                    Toast.makeText(getActivity(),"Eğitmen girişi başarılı",Toast.LENGTH_LONG).show();
                                                    setFragmentsBundle(trainerFragment,bundle,R.id.frame_layout_login,trainerNameAndLastname);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        try {
                                            throw e;
                                        } catch(FirebaseAuthInvalidUserException ex) {
                                            Toast.makeText(getActivity(),"Lütfen geçerli kayıtlı bir email adresi giriniz",Toast.LENGTH_LONG).show();
                                            loginEmail.requestFocus();
                                        } catch(FirebaseNetworkException ex) {
                                            Toast.makeText(getActivity(),"Internetinizi kontrol ediniz",Toast.LENGTH_LONG).show();
                                        } catch(Exception ex) {
                                            Toast.makeText(getActivity(),"Giriş işlemi başarısız",Toast.LENGTH_LONG).show();
                                        }
                                        // Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                        loginProgressButton.Finished();
                    }
                },2000);
            }
        });
    }
    public void setFragments(Fragment fragment,int layoutID){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutID,fragment).commit();
    }
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