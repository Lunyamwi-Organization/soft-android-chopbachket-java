package com.example.chopbachktet.AdminSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chopbachktet.Interface.ItemClickListener;
import com.example.chopbachktet.Model.Products;
import com.example.chopbachktet.R;
import com.example.chopbachktet.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminCheckApproveProductsActivity extends AppCompatActivity {
private RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
private DatabaseReference unapprovedProductsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_approve_products);

        unapprovedProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView=(RecyclerView)findViewById(R.id.admin_products_approve);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unapprovedProductsRef.orderByChild("productStatus").equalTo("Not Approved"),Products.class)
                .build();

        FirebaseRecyclerAdapter <Products,ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + "KES");
                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                //get product ID
                                final String productID = model.getPid();
                                CharSequence options [] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckApproveProductsActivity.this);
                                builder.setTitle("Are you sure you want to approve this item");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which == 0)
                                        {
                                            changeProductStatus(productID);
                                        }
                                        if(which == 1)
                                        {

                                        }
                                    }
                                });

                                builder.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductStatus(String productID)
    {
        unapprovedProductsRef.child(productID).child("productStatus").setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                     if(task.isSuccessful())
                     {
                         Toast.makeText(AdminCheckApproveProductsActivity.this,"The product is approved successfully available for sale to users",Toast.LENGTH_SHORT).show();
                     }
                    }
                });
    }
}
