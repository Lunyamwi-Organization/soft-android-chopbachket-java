package com.example.chopbachktet.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class SellerLoginActivity extends AppCompatActivity {
    private Button sellerLoginButton;
    private EditText emailInput,passwordInput;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        sellerLoginButton=(Button) findViewById(R.id.seller_login_btn);
        emailInput=(EditText)findViewById(R.id.seller_login_email);
        passwordInput=(EditText)findViewById(R.id.seller_login_password);

        sellerLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginSeller();
            }
        });
    }

    private void loginSeller()
    {
        final String email=emailInput.getText().toString().trim();
        final String password=passwordInput.getText().toString().trim();
        if(password.equals(""))
        {
            passwordInput.setError("password cannot be empty");
            passwordInput.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            passwordInput.setError("password should have at least 6 characters");
            passwordInput.requestFocus();
            return;
        }
        if(email.equals(""))
        {
            emailInput.setError("email address cannot be empty");
            emailInput.requestFocus();
            return;
        }
        //validate the email format
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailInput.setError("invalid email format");
            emailInput.requestFocus();
            return;
        }
        else
            {
                loadingBar.setTitle("Seller Login Account");
                loadingBar.setMessage("Please wait, we verify your credentials ):");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                               if(task.isSuccessful())
                               {
                                   loadingBar.dismiss();
                                   Intent intent=new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                               }
                               else
                                   {
                                       loadingBar.dismiss();
                                       Toast.makeText(SellerLoginActivity.this, "Authentication failed." + task.getException(),
                                           Toast.LENGTH_SHORT).show();

                                       Intent intent=new Intent(SellerLoginActivity.this, MainActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                  }
                            }
                        });

            }

    }
}
