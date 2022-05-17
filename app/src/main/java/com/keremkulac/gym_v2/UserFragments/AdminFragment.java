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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.keremkulac.gym_v2.Activity.LoginActivity;
import com.keremkulac.gym_v2.Activity.MainActivity2;
import com.keremkulac.gym_v2.Fragments.SignUpFragment;
import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.User;
import com.keremkulac.gym_v2.Adapters.UserAdapter;

import java.util.ArrayList;
import java.util.Map;

public class AdminFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    ArrayList<User> userArrayList;
    UserAdapter userAdapter;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        Init(view);
        getData();
        return view;
    }

    public void Init(View view){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        recyclerView = view.findViewById(R.id.recyclerViewUser);
        userArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter = new UserAdapter(userArrayList);
        recyclerView.setAdapter(userAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    private void getData( ){
        firebaseFirestore.collection("User")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(getActivity(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        Map<String ,Object> data = documentSnapshot.getData();
                        String userEmail = (String) data.get("userEmail");
                        String userName = (String)data.get("userName");
                        String userLastname = (String)data.get("userLastname");
                        String downloadUrl = (String)data.get("downloadUrl");
                        String userPhoneNumber = (String)data.get("userPhoneNumber");
                        String userRegisterDate = (String)data.get("userRegisterDate");
                        String userTitle = (String)data.get("userTitle");
                        User user = new User(userEmail,userName,userLastname,userPhoneNumber,downloadUrl,userRegisterDate,userTitle);
                        userArrayList.add(user);
                    }
                     userAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAddTrainer){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            SignUpFragment signUpFragment = new SignUpFragment();
            bundle.putString("trainerTitle","Eğitmen");
            signUpFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frame_layout_login,signUpFragment).commit();
        }else if(item.getItemId() == R.id.logoutAdmin){
            auth.signOut();
            Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}