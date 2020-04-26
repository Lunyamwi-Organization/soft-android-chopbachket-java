package com.example.chopbachktet.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chopbachktet.Buyer.MainActivity;
import com.example.chopbachktet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {
private Button sellerRegisterButton,sellerLoginButton;
private EditText nameInput,phoneInput,addressInput,emailInput,passwordInput;
private FirebaseAuth mAuth;
private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth=FirebaseAuth.getInstance();

        loadingBar=new ProgressDialog(this);
        sellerLoginButton=(Button) findViewById(R.id.already_have_account_btn);
        sellerRegisterButton=(Button) findViewById(R.id.seller_register_btn);

        nameInput=(EditText)findViewById(R.id.seller_name);
        phoneInput=(EditText)findViewById(R.id.seller_phone);
        addressInput=(EditText)findViewById(R.id.seller_address);
        emailInput=(EditText)findViewById(R.id.seller_email);
        passwordInput=(EditText)findViewById(R.id.seller_password);

        sellerLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
        sellerRegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               registerSeller();
            }
        });

    }

    private void registerSeller()
    {
        final String name=nameInput.getText().toString();
        final String address=addressInput.getText().toString();
        String password=passwordInput.getText().toString();
        final String phone=phoneInput.getText().toString();
        final String email=emailInput.getText().toString();
       //validation of input
        if(!name.equals("")&&!phone.equals("")&&!password.equals("")&&!address.equals("")&&!email.equals(""))
        {
            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage(name+",please wait, we set up your account ):");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task)
                      {
                         if(task.isSuccessful())
                         {
                             //save the user in the real time database
                             final DatabaseReference rootRef;
                             rootRef= FirebaseDatabase.getInstance().getReference();
                            //get the current users unique ID
                             final String sID=mAuth.getCurrentUser().getUid();
                             rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                 {
                                     if (!(dataSnapshot.child("Sellers").child(sID).exists()))
                                     {
                                         //create a hash map for the storage
                                         HashMap<String,Object> sellerMap =new HashMap<>();
                                         sellerMap.put("sID",sID);
                                         sellerMap.put("phone",phone);
                                         sellerMap.put("email",email);
                                         sellerMap.put("address",address);
                                         sellerMap.put("name",name);
                                         //creating a sellers reference from the root reference
                                         rootRef.child("Sellers").child(sID).updateChildren(sellerMap)
                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task)
                                                     {
                                                         if(task.isSuccessful())
                                                         {
                                                             loadingBar.dismiss();
                                                             Toast.makeText(SellerRegistrationActivity.this,"Registered Succesfully, Welcome "+name,Toast.LENGTH_SHORT).show();

                                                             Intent intent=new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                             startActivity(intent);
                                                             finish();
                                                         }
                                                         else
                                                             {
                                                                 loadingBar.dismiss();
                                                                 Toast.makeText(SellerRegistrationActivity.this,"Registered Failed,try again later "+name,Toast.LENGTH_SHORT).show();

                                                                 Intent intent=new Intent(SellerRegistrationActivity.this, MainActivity.class);
                                                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                 startActivity(intent);
                                                             }

                                                     }
                                                 });
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });

                         }
                      }
                  });

        }
        //
        else
            {
                Toast.makeText(SellerRegistrationActivity.this,"Please Fill all the Fields",Toast.LENGTH_SHORT).show();
            }

    }
}
