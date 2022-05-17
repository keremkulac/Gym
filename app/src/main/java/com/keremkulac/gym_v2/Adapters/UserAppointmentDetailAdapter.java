package com.keremkulac.gym_v2.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.UserAppointmentDetail;

import java.util.ArrayList;

public class UserAppointmentDetailAdapter extends RecyclerView.Adapter<UserAppointmentDetailAdapter.AppointmentDetailHolder>{
    public UserAppointmentDetailAdapter(ArrayList<UserAppointmentDetail> userAppointmentDetailArrayList) {
        this.userAppointmentDetailArrayList = userAppointmentDetailArrayList;
    }

    public void setUserAppointmentDetailArrayList(ArrayList<UserAppointmentDetail> userAppointmentDetailArrayList) {
        this.userAppointmentDetailArrayList = userAppointmentDetailArrayList;
    }

    private ArrayList<UserAppointmentDetail> userAppointmentDetailArrayList;

    @NonNull
    @Override
    public AppointmentDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_appointment_detail,parent,false);
        AppointmentDetailHolder appointmentDetailHolder = new AppointmentDetailHolder(view);
        return  appointmentDetailHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDetailHolder holder, int position) {
        // Veritabanından alınan veriler recyclerview içinde gereken yerlere yazılır
        holder.date.setText(userAppointmentDetailArrayList.get(position).getUserAppointmentDate());
        holder.name.setText(userAppointmentDetailArrayList.get(position).getNameOfMakingAppointment());
        holder.time.setText(userAppointmentDetailArrayList.get(position).getUserAppointmentTime());
        holder.lastname.setText(userAppointmentDetailArrayList.get(position).getLastnameOfMakingAppointment());
        holder.phoneNumber.setText(userAppointmentDetailArrayList.get(position).getPhoneNumberMakingAppointment());
    }

    @Override
    public int getItemCount() { return userAppointmentDetailArrayList.size(); }

    public class AppointmentDetailHolder  extends RecyclerView.ViewHolder{
        TextView date,time,phoneNumber,name,lastname;
        public AppointmentDetailHolder(@NonNull View itemView) {
            // Recyclerview içindedeki companent ler tanımlanır
            super(itemView);
            date = itemView.findViewById(R.id.cardAppointmentDate);
            time = itemView.findViewById(R.id.cardAppointmentTime);
            phoneNumber = itemView.findViewById(R.id.cardPhoneNumberOfMakingAppointment);
            name = itemView.findViewById(R.id.cardNameOfMakingAppointment);
            lastname = itemView.findViewById(R.id.cardLastNameOfMakingAppointment);
            //trainer = itemView.findViewById(R.id.cardAppointmentTrainer);
        }
    }
}
