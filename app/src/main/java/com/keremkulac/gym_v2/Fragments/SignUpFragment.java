package com.keremkulac.gym_v2.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.keremkulac.gym_v2.Activity.LoginActivity;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.ProgressButtons.RegisterProgressButton;
import com.keremkulac.gym_v2.UserFragments.LoginFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class SignUpFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    Uri imageData;
    ImageView profileImage,backButton;
    EditText registerEmail,registerPassword,registerName,registerLastname,registerPhoneNumber;
    SimpleDateFormat formatter;
    Date date ;
    String currentDate,userTitle,trainerTitle,email,password,name,lastname,phoneNumber,pictureName;
    View progress_button_register;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Giri?? yap ekran?? a????l??rken yap??lacaklr
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Init(view);
        profileImageChange();
        back();
        registerLauncher(view);
        register();
        return view;
    }
    public void Init(View view){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        registerEmail = view.findViewById(R.id.registerEmail);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerName = view.findViewById(R.id.registerName);
        registerLastname = view.findViewById(R.id.registerLastName);
        registerPhoneNumber = view.findViewById(R.id.registerPhoneNumber);
        backButton = view.findViewById(R.id.backButton);
        profileImage = view.findViewById(R.id.profileImage);
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = new Date();
        progress_button_register = view.findViewById(R.id.progressRegisterButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        assert getArguments() != null;
        userTitle = getArguments().getString("userTitle");
        trainerTitle = getArguments().getString("trainerTitle");
        System.out.println(userTitle);
        System.out.println(trainerTitle);
    }
    public void register(){
        progress_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RegisterProgressButton registerProgressButton = new RegisterProgressButton(getActivity(),view);
                registerProgressButton.Activated();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Kay??t ol butonuna t??kland??????nda kullan??c??n??n yazd?????? email,??ifre,ad, soyad
                        // telefon numaras??, mevcut tarihi al
                        email = registerEmail.getText().toString().trim();
                        password = registerPassword.getText().toString().trim();
                        name = registerName.getText().toString().trim();
                        lastname = registerLastname.getText().toString().trim();
                        phoneNumber = registerPhoneNumber.getText().toString().trim();
                            currentDate= formatter.format(date);
                        if(email.equals("") || password.equals("") || name.equals("") || lastname.equals("") || phoneNumber.equals("")  ){
                            Toast.makeText(getActivity(),"L??tfen bilgilerinizi giriniz",Toast.LENGTH_LONG).show();
                        }else{
                            // Kullan??c??n??n profil resmini Firebase e kaydederken kullanmak
                            // i??in random isim olu??turur
                            UUID uuid = UUID.randomUUID();
                            pictureName = "images/"+uuid+".jpg";
                            // Kullan??c?? bir foto??raf se??mi??se kullan??c?? kayd?? ve email ??ifre kayd?? olu??turulur
                            if(imageData!=null){
                                userInfoRegister();
                                userMailPasswordRegister();
                            }else{
                                Toast.makeText(getActivity(),"L??tfen bir foto??raf se??iniz2",Toast.LENGTH_LONG).show();
                            }
                        }
                        registerProgressButton.Finished();
                    }
                },2000);
            }
        });
    }

    public void userInfoRegister(){
        // Kullan??c??n??n profil resminine rastgele olu??turulan isimle resmi kaydeder
        storageReference.child(pictureName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pictureReference = firebaseStorage.getReference(pictureName);
                pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Kullan??c??n??n email ini, ad??n??, soyad??n??, telefon numaras??n??, kay??t tarihini
                        // kay??tl?? profil resminin url ini User collection una kullan??c??n??n ID sine g??re kay??t eder
                        String pictureUrl = uri.toString();
                        HashMap<String,Object> registerUser = new HashMap<>();
                        registerUser.put("userEmail",email);
                        registerUser.put("userName",name);
                        registerUser.put("userLastname",lastname);
                        registerUser.put("userPhoneNumber",phoneNumber);
                        registerUser.put("userRegisterDate", currentDate);
                        registerUser.put("downloadUrl",pictureUrl);
                     //   registerUser.put("ID",currentUser.getUid());

                        // Kullan??c?? title ?? bo??sa E??itmen title ??n?? al??r
                        if(userTitle == null){
                            registerUser.put("userTitle",trainerTitle);
                        }else{
                            registerUser.put("userTitle",userTitle);
                        }
                        firebaseFirestore.collection("User")
                                .document(currentUser.getUid())
                                .set(registerUser)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                try {
                                    throw e;
                                }
                                // Kullan??c?? 6 karaktersen k??sa bir ??ifre girerse ekrana uyar?? ????kar
                                // ve ??ifre girme k??sm??na odaklar
                                catch(FirebaseAuthWeakPasswordException ex) {
                                    Toast.makeText(getActivity(),"L??tfen 6 karakterden uzun bir ??ifre giriniz",Toast.LENGTH_LONG).show();
                                    registerPassword.requestFocus();
                                }
                                // E??er kullan??c??n??n girdi??i kullan??c?? ile daha ??nce kay??t olmu?? bir kullan??c??
                                // varsa ekrana uyar?? ????kar. Email girme k??sm??na odaklar
                                catch (FirebaseAuthUserCollisionException ex) {
                                    Toast.makeText(getActivity(),"Bu email ile kay??tl?? bir kullan??c?? bulunmaktad??r",Toast.LENGTH_LONG).show();
                                    registerEmail.requestFocus();
                                }catch(Exception ex) {
                                    Toast.makeText(getActivity(),"Kay??t olma i??lemi ba??ar??s??z",Toast.LENGTH_LONG).show();
                                }
                                //Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    public void profileImageChange(){
        // Kullan??c?? resim se??me butonuna t??klad??????nda se??ilen resmi ImageView i??ine koyar
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });
    }

    public void back(){
        // Geri butonuna t??kland??????nda Giri?? sayfas??na gider
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
    public void userMailPasswordRegister(){
        // Kullan??c??n??n email ve ??ifresini Firebase de Yetkilendirme  k??sm??na ekler
        // ve Giri?? ekran?? a????l??r
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getActivity(),"Kay??t ba??ar??l??",Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LoginFragment loginFragment = new LoginFragment();
                fragmentTransaction.replace(R.id.frame_layout_login,loginFragment).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void selectImage(View view){
        // Kullan??c??n??n profil foto??raf?? se??mek i??in Galeriye ula??mas?? i??in izin istenir
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye ula??mak i??in izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("??zin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    public void registerLauncher(View view){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        intentFromResult.getData();
                        imageData = intentFromResult.getData();
                        try {
                            if(Build.VERSION.SDK_INT >= 28){
                                // SDK versiyonu 28 den b??y??k ise Kullan??c??n??n se??ti??i foto??raf Picasso k??t??phanesi
                                // kullan??larak verilen boyutlarda ImageView a i??ine koyulur
                                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                                Picasso.get().load(imageData).resize(150,120).into(profileImage);
                            }else{
                                // SDK versiyonu 28 den k??????k ise Kullan??c??n??n se??ti??i foto??raf Picasso k??t??phanesi
                                // kullan??larak verilen boyutlarda ImageView a i??ine koyulur
                                Picasso.get().load(imageData).resize(150,120).into(profileImage);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // Kullan??c?? izin verirse galeriye gider
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(getActivity(), "??zin gerekli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}