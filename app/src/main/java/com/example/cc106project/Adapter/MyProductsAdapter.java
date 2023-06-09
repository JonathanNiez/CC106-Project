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
import com.example.cc106project.Model.MyProductsModel;
import com.example.cc106project.R;
import com.example.cc106project.ViewProductInfo;

import java.util.ArrayList;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.MyProductsAdapterViewHolder> {

    Context context;
    ArrayList<MyProductsModel> myProductsModelArrayList;

    public MyProductsAdapter(Context context, ArrayList<MyProductsModel> myProductsModelArrayList) {
        this.context = context;
        this.myProductsModelArrayList = myProductsModelArrayList;
    }

    @NonNull
    @Override
    public MyProductsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_products, parent, false);
        return new MyProductsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductsAdapter.MyProductsAdapterViewHolder holder, int position) {
        MyProductsModel myProductsModel = myProductsModelArrayList.get(position);

        holder.itemName.setText(myProductsModel.getItemName());
        holder.itemPrice.setText("₱" + String.valueOf(myProductsModel.getItemPrice()));
        holder.itemStock.setText("Stock: " + String.valueOf(myProductsModel.getItemStock()));

        if (myProductsModel.getItemImage() != null) {
            Glide.with(context).load(myProductsModel.getItemImage()).centerCrop().into(holder.itemImage);
        }else{
            holder.itemImage.setImageResource(R.drawable.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewProductInfo.class);
            intent.putExtra("productID", String.valueOf(myProductsModel.getProductID()));
            intent.putExtra("sellerID", myProductsModel.getSellerID());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return myProductsModelArrayList.size();
    }

    public static class MyProductsAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName, itemPrice, itemStock;
        private ImageView itemImage;

        public MyProductsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.rMyItemName);
            itemPrice = itemView.findViewById(R.id.rMyItemPrice);
            itemImage = itemView.findViewById(R.id.rMyItemImage);
            itemStock = itemView.findViewById(R.id.rMyItemStock);

        }
    }
}
