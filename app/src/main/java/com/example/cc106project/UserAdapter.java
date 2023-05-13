package com.example.cc106project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<Users> usersList;

    public UserAdapter(Context context, ArrayList<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.firstName.setText(users.getFirstName());
        holder.lastName.setText(users.getLastName());

        if (users.getProfilePic() != null){
            Glide.with(context).load(users.getProfilePic()).centerCrop().into(holder.profilePic);

        }
    }


    @Override
    public int getItemCount() {
//        return usersList == null ? 0 : usersList.size();
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView firstName, lastName;
        public ImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }

}
