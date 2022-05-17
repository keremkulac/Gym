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
        // Giriş yap ekranı açılırken yapılacaklr
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
                        // Kayıt ol butonuna tıklandığında kullanıcının yazdığı email,şifre,ad, soyad
                        // telefon numarası, mevcut tarihi al
                        email = registerEmail.getText().toString().trim();
                        password = registerPassword.getText().toString().trim();
                        name = registerName.getText().toString().trim();
                        lastname = registerLastname.getText().toString().trim();
                        phoneNumber = registerPhoneNumber.getText().toString().trim();
                            currentDate= formatter.format(date);
                        if(email.equals("") || password.equals("") || name.equals("") || lastname.equals("") || phoneNumber.equals("")  ){
                            Toast.makeText(getActivity(),"Lütfen bilgilerinizi giriniz",Toast.LENGTH_LONG).show();
                        }else{
                            // Kullanıcının profil resmini Firebase e kaydederken kullanmak
                            // için random isim oluşturur
                            UUID uuid = UUID.randomUUID();
                            pictureName = "images/"+uuid+".jpg";
                            // Kullanıcı bir fotoğraf seçmişse kullanıcı kaydı ve email şifre kaydı oluşturulur
                            if(imageData!=null){
                                userInfoRegister();
                                userMailPasswordRegister();
                            }else{
                                Toast.makeText(getActivity(),"Lütfen bir fotoğraf seçiniz2",Toast.LENGTH_LONG).show();
                            }
                        }
                        registerProgressButton.Finished();
                    }
                },2000);
            }
        });
    }

    public void userInfoRegister(){
        // Kullanıcının profil resminine rastgele oluşturulan isimle resmi kaydeder
        storageReference.child(pictureName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pictureReference = firebaseStorage.getReference(pictureName);
                pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Kullanıcının email ini, adını, soyadını, telefon numarasını, kayıt tarihini
                        // kayıtlı profil resminin url ini User collection una kullanıcının ID sine göre kayıt eder
                        String pictureUrl = uri.toString();
                        HashMap<String,Object> registerUser = new HashMap<>();
                        registerUser.put("userEmail",email);
                        registerUser.put("userName",name);
                        registerUser.put("userLastname",lastname);
                        registerUser.put("userPhoneNumber",phoneNumber);
                        registerUser.put("userRegisterDate", currentDate);
                        registerUser.put("downloadUrl",pictureUrl);
                     //   registerUser.put("ID",currentUser.getUid());

                        // Kullanıcı title ı boşsa Eğitmen title ını alır
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
                                // Kullanıcı 6 karaktersen kısa bir şifre girerse ekrana uyarı çıkar
                                // ve şifre girme kısmına odaklar
                                catch(FirebaseAuthWeakPasswordException ex) {
                                    Toast.makeText(getActivity(),"Lütfen 6 karakterden uzun bir şifre giriniz",Toast.LENGTH_LONG).show();
                                    registerPassword.requestFocus();
                                }
                                // Eğer kullanıcının girdiği kullanıcı ile daha önce kayıt olmuş bir kullanıcı
                                // varsa ekrana uyarı çıkar. Email girme kısmına odaklar
                                catch (FirebaseAuthUserCollisionException ex) {
                                    Toast.makeText(getActivity(),"Bu email ile kayıtlı bir kullanıcı bulunmaktadır",Toast.LENGTH_LONG).show();
                                    registerEmail.requestFocus();
                                }catch(Exception ex) {
                                    Toast.makeText(getActivity(),"Kayıt olma işlemi başarısız",Toast.LENGTH_LONG).show();
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
        // Kullanıcı resim seçme butonuna tıkladığında seçilen resmi ImageView içine koyar
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });
    }

    public void back(){
        // Geri butonuna tıklandığında Giriş sayfasına gider
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
    public void userMailPasswordRegister(){
        // Kullanıcının email ve şifresini Firebase de Yetkilendirme  kısmına ekler
        // ve Giriş ekranı açılır
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getActivity(),"Kayıt başarılı",Toast.LENGTH_LONG).show();
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
        // Kullanıcının profil fotoğrafı seçmek için Galeriye ulaşması için izin istenir
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye ulaşmak için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
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
                                // SDK versiyonu 28 den büyük ise Kullanıcının seçtiği fotoğraf Picasso kütüphanesi
                                // kullanılarak verilen boyutlarda ImageView a içine koyulur
                                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                                Picasso.get().load(imageData).resize(150,120).into(profileImage);
                            }else{
                                // SDK versiyonu 28 den küçük ise Kullanıcının seçtiği fotoğraf Picasso kütüphanesi
                                // kullanılarak verilen boyutlarda ImageView a içine koyulur
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
                    // Kullanıcı izin verirse galeriye gider
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(getActivity(), "İzin gerekli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}