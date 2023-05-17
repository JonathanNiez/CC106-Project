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
import com.example.cc106project.ChatWithProduct;
import com.example.cc106project.Model.Products;
import com.example.cc106project.R;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    Context context;
    private ArrayList<Products> productsArrayList;

    public ProductsAdapter(Context context, ArrayList<Products> productsArrayList) {
        this.context = context;
        this.productsArrayList = productsArrayList;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Products products = productsArrayList.get(position);
        holder.rItemName.setText(products.getItemName());
        holder.rItemPrice.setText("₱" + products.getItemPrice());
        holder.rItemCategory.setText(products.getItemCategory());


        if (products.getItemImage() != null) {
            Glide.with(context).load(products.getItemImage()).centerCrop().into(holder.rItemImage);
        }else{
            holder.rItemImage.setImageResource(R.drawable.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatWithProduct.class);
            intent.putExtra("productID", String.valueOf(products.getProductID()));
            intent.putExtra("sellerID", products.getSellerID());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }


    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView rItemName, rItemPrice, rItemCategory;
        private ImageView rItemImage;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            rItemCategory = itemView.findViewById(R.id.rItemCategory);
            rItemName = itemView.findViewById(R.id.rItemName);
            rItemPrice = itemView.findViewById(R.id.rItemPrice);
            rItemImage = itemView.findViewById(R.id.rItemImage);

        }
    }
}


