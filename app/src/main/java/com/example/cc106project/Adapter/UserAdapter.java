package com.example.cc106project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean isOnline;

    public UserAdapter(Context context, ArrayList<UsersModel> usersModelArrayList, boolean isOnline) {
        this.context = context;
        this.usersModelArrayList = usersModelArrayList;
        this.isOnline = isOnline;
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

        if (usersModel.getProfilePicUrl() != null) {
            Glide.with(context).load(usersModel.getProfilePicUrl()).centerCrop().into(holder.profilePic);
        }else {
            holder.profilePic.setImageResource(R.drawable.user_icon100);
        }

        if (isOnline){
            if (!usersModel.isOnline()){
                holder.offlineStatus.setVisibility(View.VISIBLE);
                holder.onlineStatus.setVisibility(View.GONE);
            }else {
                holder.onlineStatus.setVisibility(View.VISIBLE);
                holder.offlineStatus.setVisibility(View.GONE);
            }
            Log.i("UserAdapter", String.valueOf(usersModel.isOnline()));

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
        private ImageView profilePic, onlineStatus, offlineStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            profilePic = itemView.findViewById(R.id.profilePic);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);
            offlineStatus = itemView.findViewById(R.id.offlineStatus);
        }
    }

}
