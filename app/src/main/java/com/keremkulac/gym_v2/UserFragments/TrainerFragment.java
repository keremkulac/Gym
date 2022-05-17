package com.keremkulac.gym_v2.UserFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.keremkulac.gym_v2.Activity.LoginActivity;
import com.keremkulac.gym_v2.Adapters.UserAppointmentDetailAdapter;
import com.keremkulac.gym_v2.Fragments.ProfileFragment;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.UserAppointmentDetail;

import java.util.ArrayList;
import java.util.Map;


public class TrainerFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    ArrayList<UserAppointmentDetail> userAppointmentDetailArrayList;
    RecyclerView recyclerView;
    UserAppointmentDetailAdapter userAppointmentDetailAdapter;
    FirebaseAuth auth;
    String trainerNameAndLastname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);
        Init(view);
        getData();
        return view;
    }

    public void Init(View view){
        recyclerView = view.findViewById(R.id.recyclerViewTrainer);
        userAppointmentDetailArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAppointmentDetailAdapter = new UserAppointmentDetailAdapter(userAppointmentDetailArrayList);
        recyclerView.setAdapter(userAppointmentDetailAdapter);
        auth = FirebaseAuth.getInstance();
        trainerNameAndLastname = getArguments().getString("trainerNameLastname");
    }

    public void getData(){
        firebaseFirestore.collection("AppointmentDetail")
                .whereEqualTo("userSelectedTrainer",trainerNameAndLastname)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,Object> data = document.getData();
                        String date = (String) data.get("userSelectedDate");
                        String time = (String) data.get("userSelectedTime");
                        String lastname = (String) data.get("lastnameOfMakingAppointment");
                        String name =(String)data.get("nameOfMakingAppointment");
                        String phoneNumber =(String)data.get("phoneNumberOfMakingAppointment");
                        UserAppointmentDetail userAppointmentDetail = new UserAppointmentDetail(date,time,name,lastname,phoneNumber);
                        userAppointmentDetailArrayList.add(userAppointmentDetail);
                    }
                    userAppointmentDetailAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.trainer_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.viewMyInfo){
            Bundle bundle = new Bundle();
            bundle.putString("userTitle","Eğitmen");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frame_layout_login, profileFragment).commit();
        }else if(item.getItemId() == R.id.logoutTrainer){
            auth.signOut();
            Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}