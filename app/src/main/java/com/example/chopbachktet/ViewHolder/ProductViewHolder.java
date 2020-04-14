package com.example.chopbachktet.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chopbachktet.R;
import com.example.chopbachktet.Interface.ItemClickListener;
public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //we access our product item layout in here
    //we also access our layout variables
    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;//accessing our listener interface
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);

    }
    public void setItemClickListner(ItemClickListener listener)
    {
        this.listener = listener;
    }



    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }
}
