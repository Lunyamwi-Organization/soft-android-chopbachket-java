package com.example.chopbachktet.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chopbachktet.Interface.ItemClickListener;
import com.example.chopbachktet.R;

public class SellerItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice,txtProductStatus;
    public ImageView imageView;
    public ItemClickListener listener;

    public SellerItemsViewHolder(@NonNull View itemView) {

        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.seller_product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.seller_product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.seller_product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.seller_product_price);
        txtProductStatus = (TextView) itemView.findViewById(R.id.seller_product_status);
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
