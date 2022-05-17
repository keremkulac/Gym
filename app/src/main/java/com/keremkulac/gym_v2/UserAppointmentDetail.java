package com.keremkulac.gym_v2;

public class UserAppointmentDetail {
    String userAppointmentDate;
    String userAppointmentTime;
    String nameOfMakingAppointment;
    String lastnameOfMakingAppointment;
    String phoneNumberMakingAppointment;

    public UserAppointmentDetail(String userAppointmentDate, String userAppointmentTime, String nameOfMakingAppointment, String lastnameOfMakingAppointment, String phoneNumberMakingAppointment) {
        this.userAppointmentDate = userAppointmentDate;
        this.userAppointmentTime = userAppointmentTime;
        this.nameOfMakingAppointment = nameOfMakingAppointment;
        this.lastnameOfMakingAppointment = lastnameOfMakingAppointment;
        this.phoneNumberMakingAppointment = phoneNumberMakingAppointment;
    }
    public String getUserAppointmentDate() {
        return userAppointmentDate;
    }

    public void setUserAppointmentDate(String userAppointmentDate) {
        this.userAppointmentDate = userAppointmentDate;
    }

    public String getUserAppointmentTime() {
        return userAppointmentTime;
    }

    public void setUserAppointmentTime(String userAppointmentTime) {
        this.userAppointmentTime = userAppointmentTime;
    }

    public String getNameOfMakingAppointment() {
        return nameOfMakingAppointment;
    }

    public void setNameOfMakingAppointment(String nameOfMakingAppointment) {
        this.nameOfMakingAppointment = nameOfMakingAppointment;
    }

    public String getLastnameOfMakingAppointment() {
        return lastnameOfMakingAppointment;
    }

    public void setLastnameOfMakingAppointment(String lastnameOfMakingAppointment) {
        this.lastnameOfMakingAppointment = lastnameOfMakingAppointment;
    }

    public String getPhoneNumberMakingAppointment() {
        return phoneNumberMakingAppointment;
    }

    public void setPhoneNumberMakingAppointment(String phoneNumberMakingAppointment) {
        this.phoneNumberMakingAppointment = phoneNumberMakingAppointment;
    }

}
