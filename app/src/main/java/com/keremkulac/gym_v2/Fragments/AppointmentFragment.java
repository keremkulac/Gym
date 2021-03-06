package com.keremkulac.gym_v2.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.messaging.FirebaseMessaging;
import com.keremkulac.gym_v2.ProgressButtons.AppointmentProgressButton;
import com.keremkulac.gym_v2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class AppointmentFragment extends Fragment {
    EditText selectedTime;
    Calendar calendar;
    EditText selectedDate;
    Button dateButton,timeButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;
    FirebaseAuth auth;
    String userEmail,nameOfMakingAppointment ,lastnameOfMakingAppointment,phoneNumberOfMakingAppointment,trainerName,trainerLastname;
    DatePickerDialog datePickerDialog;
    int year,month,dayOfMonth,hour,minute;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> trainersNameList;
    ArrayAdapter<String> arrayAdapter ;
    View progressAppointmentButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Randevu al ekran?? a????l??rken yap??lacaklr
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        Init(view);
        timePick();
        datePick();
        getTrainersName();
        addAppointmentDetailToDatabase();
        return view;
    }
    private void Init(View view){
        // Instance lar olu??turuluyor
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser= auth.getCurrentUser();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        calendar= Calendar.getInstance();
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView3);
        timeButton = view.findViewById(R.id.timeButton);
        dateButton = view.findViewById(R.id.dateButton);
        selectedTime = view.findViewById(R.id.selectedTime);
        selectedDate = view.findViewById(R.id.selectedDate);
        trainersNameList = new ArrayList<String>();
        progressAppointmentButton = view.findViewById(R.id.progressAppointmentButton);
        getCurrentUser();
    }

    public void getTrainersName(){
        // Veritaban??ndan title ?? e??itmen olan kullan??c??lar??n isimlerini al??p, Randevu al ekran??ndaki
        // e??itmen se??im listesine ekliyor
        firebaseFirestore.collection("User")
                .whereEqualTo("userTitle","E??itmen")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        trainerName = document.getData().get("userName").toString();
                        trainerLastname = document.getData().get("userLastname").toString();
                        String trainerNameLastname = trainerName+" "+trainerLastname;
                        trainersNameList.add(trainerNameLastname);
                        arrayAdapter = new ArrayAdapter<String >(getActivity(),R.layout.dropdown_item,trainersNameList);
                        autoCompleteTextView.setAdapter(arrayAdapter);
                    }
                }else{
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void getCurrentUser(){
        // Giri?? yapm???? olan kullan??c??n??n emalinie g??re ad,soyad ve telefon numaras??n?? al??yor
        userEmail = auth.getCurrentUser().getEmail();
        firebaseFirestore.collection("User")
                .whereEqualTo("userEmail",userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                         nameOfMakingAppointment = document.getData().get("userName").toString();
                         lastnameOfMakingAppointment = document.getData().get("userLastname").toString();
                         phoneNumberOfMakingAppointment = document.getData().get("userPhoneNumber").toString();
                    }
                }else{
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void addAppointmentDetailToDatabase(){
        // Randevu olu??tur butonuna t??kland??????nda se??ilen tarih,saat, e??itmen ad??,
        // randevu alan??n ad?? soyad??n?? veritaban??na yaz??yor
        progressAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppointmentProgressButton appointmentProgressButton = new AppointmentProgressButton(getActivity(),view);
                appointmentProgressButton.Activated();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String userSelectedDate = selectedDate.getText().toString();
                        String userSelectedTime = selectedTime.getText().toString();
                        String userSelectedTrainer =autoCompleteTextView.getText().toString();
                        HashMap<String,Object> appointmentDetail = new HashMap<>();
                        appointmentDetail.put("userSelectedDate",userSelectedDate);
                        appointmentDetail.put("userSelectedTime",userSelectedTime);
                        appointmentDetail.put("userSelectedTrainer",userSelectedTrainer);
                        appointmentDetail.put("nameOfMakingAppointment",nameOfMakingAppointment);
                        appointmentDetail.put("lastnameOfMakingAppointment",lastnameOfMakingAppointment);
                        appointmentDetail.put("phoneNumberOfMakingAppointment",phoneNumberOfMakingAppointment);

                        if(userSelectedTime.equals("") || userSelectedDate.equals("") || userSelectedTrainer.equals("")){
                            Toast.makeText(getActivity(),"L??tfen bilgilerinizi giriniz",Toast.LENGTH_LONG).show();
                        }else{
                            // Firebase veritaban??nda AppointmentDetail collectionu olu??turup randevu bilgilerini yazar
                            firebaseFirestore.collection("AppointmentDetail").add(appointmentDetail).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Veritaban??na ekleme ba??ar??l??ysa Anasayfaya ge??er
                                    Toast.makeText(getActivity(),"Kay??t ba??ar??l??",Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    HomeFragment homeFragment = new HomeFragment();
                                    fragmentTransaction.replace(R.id.main_activity_frame_layout, homeFragment).commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        appointmentProgressButton.Finished();
                    }
                },2000);
            }
        });
    }

    public void datePick(){
        // Tarih se?? butonuna t??kland??????nda se??ilen tarihin y??l g??n ve ay??n?? al??p EditText e yaz??yor
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            selectedDate.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,dayOfMonth);
                datePickerDialog.show();
            }
        });
    }
    public void timePick(){
        // Saat se?? butonuna t??kland??????nda se??ilen saatin, saat ve dakikas??n?? al??p EditText e yaz??yor
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, "Se??", timePickerDialog);
                timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "??ptal", timePickerDialog);
                timePickerDialog.show();
            }
        });
    }
}