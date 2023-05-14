package com.example.cc106project.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cc106project.Model.ChatModel;
import com.example.cc106project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {

    private Context context;
    private ArrayList<ChatModel> chatModelArrayList;
    private String imageURL;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser currentUser;

    public ChatAdapter(Context context, ArrayList<ChatModel> chatModelArrayList, String imageURL) {
        this.context = context;
        this.chatModelArrayList = chatModelArrayList;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ChatAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapterViewHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapterViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatAdapterViewHolder holder, int position) {
        ChatModel chatModel = chatModelArrayList.get(position);
        holder.chatUserMessage.setText(chatModel.getMessage());

      if (imageURL != null){
          Log.i("ChatAdapter", "xxxxxxxxxxxxxxxxxxxxxxxxxxxx" + imageURL);

//          Glide.with(context).load(imageURL).centerCrop().into(holder.chatUserProfilePic);
      }else{
          Log.i("ChatAdapter", "profilepis iisssssss asdasda null");
          holder.chatUserProfilePic.setImageResource(R.drawable.user_icon100);
      }

    }

    @Override
    public int getItemCount() {
        return chatModelArrayList.size();
    }

    public static class ChatAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView chatUserMessage;
        private ImageView chatUserProfilePic;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            chatUserMessage = itemView.findViewById(R.id.chatUserMessage);
            chatUserProfilePic = itemView.findViewById(R.id.chatUserProfilePic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String userID = currentUser.getUid();

        if (chatModelArrayList.get(position).getSender().equals(userID)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
