package com.example.cc106project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cc106project.Chat;
import com.example.cc106project.Model.UsersModel;
import com.example.cc106project.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<UsersModel> usersModelArrayList;

    public UserAdapter(Context context, ArrayList<UsersModel> usersModelArrayList) {
        this.context = context;
        this.usersModelArrayList = usersModelArrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UsersModel usersModel = usersModelArrayList.get(position);
        holder.firstName.setText(usersModel.getFirstName());
        holder.lastName.setText(usersModel.getLastName());

        if (usersModel.getProfilePic() != null) {
            Glide.with(context).load(usersModel.getProfilePic()).centerCrop().into(holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("userID", usersModel.getUserID());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
//        return usersList == null ? 0 : usersList.size();
        return usersModelArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView firstName, lastName;
        private ImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }

}
