package com.keremkulac.gym_v2.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keremkulac.gym_v2.R;
import com.keremkulac.gym_v2.User;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    public UserAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }
    private ArrayList<User> userArrayList;
    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user,parent,false);
        UserHolder userHolder = new UserHolder(view);
        return  userHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        // Veritabanından alınan veriler recyclerview içinde gereken yerlere yazılır
        holder.name.setText(userArrayList.get(position).getName());
        holder.email.setText(userArrayList.get(position).getEmail());
        holder.lastname.setText(userArrayList.get(position).getLastName());
        holder.date.setText(userArrayList.get(position).getRegisterDate());
        holder.phoneNumber.setText(userArrayList.get(position).getPhoneNumber());
        holder.title.setText(userArrayList.get(position).getTitle());
        // Picasso kütüphanesi kullanılarak veritabanından alınan kullanıcının profil resminin url si alınıp
        // ImageView içine belirtilen ölçülerde profil resmi koyulur
        Picasso.get().load(userArrayList.get(position).getDownloadUrl()).resize(150,120).into(holder.picture);
    }



    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{
        // Recyclerview içindedeki companent ler tanımlanır
        TextView email,name,lastname,date,phoneNumber,title;
        ImageView picture;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.cardImage);
            name = itemView.findViewById(R.id.cardName);
            email = itemView.findViewById(R.id.cardEmail);
            lastname = itemView.findViewById(R.id.cardLastname);
            date = itemView.findViewById(R.id.cardDate);
            phoneNumber = itemView.findViewById(R.id.cardPhoneNumber);
            title = itemView.findViewById(R.id.cardTitle);

        }
    }
}
