package com.keremkulac.gym_v2.ProgressButtons;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.keremkulac.gym_v2.R;

public class AppointmentProgressButton {
    CardView appointmentCardView;
    ProgressBar appointmentProgressBar;
    ConstraintLayout appointment_constraint_layout;
    TextView progressAppointment;
    Animation fade_in;

    public AppointmentProgressButton(Context context, View view){
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        appointmentCardView = view.findViewById(R.id.appointmentCardView);
        appointment_constraint_layout = view.findViewById(R.id.appointment_constraint_layout);
        appointmentProgressBar = view.findViewById(R.id.appointmentProgressBar);
        progressAppointment = view.findViewById(R.id.progressAppointment);
    }

    public void Activated(){
        appointmentProgressBar.setAnimation(fade_in);
        appointmentProgressBar.setVisibility(View.VISIBLE);
        progressAppointment.setAnimation(fade_in);
        progressAppointment.setText("Randevu Oluşturuluyor");
    }
    public void Finished(){
        appointmentProgressBar.setVisibility(View.GONE);
        progressAppointment.setText("Randevu Oluştur");
    }
}
