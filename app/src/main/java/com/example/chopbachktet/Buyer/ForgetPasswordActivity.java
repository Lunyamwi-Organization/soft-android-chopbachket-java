package com.example.chopbachktet.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chopbachktet.Prevalent.Prevalent;
import com.example.chopbachktet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ForgetPasswordActivity extends AppCompatActivity {

    private String check="";
    private TextView pageTitle,titleQuestions;
    private EditText phoneNumber,question1,question2;
    private Button verifyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        check = getIntent().getStringExtra("check");

        pageTitle=(TextView)findViewById(R.id.page_title);
        titleQuestions=(TextView)findViewById(R.id.title_questions);
        phoneNumber=(EditText) findViewById(R.id.find_phone_number);
        question1=(EditText) findViewById(R.id.question_1);
        question2=(EditText) findViewById(R.id.question_2);
        verifyButton=(Button) findViewById(R.id.verify_btn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);
        if (check.equals("settings"))
        {
          pageTitle.setText("Set Questions");
          titleQuestions.setText(" Please set answers for the Following Security Questions?");
          phoneNumber.setVisibility(View.GONE);
          verifyButton.setText("Set");
            //display previous security answers
            displaySecurityQuestions();
          verifyButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v)
              {
               setSecurityAnswers();
              }

          });
        }
        else if (check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                   verifyUser();
                }
            });
        }
    }



    private void setSecurityAnswers()
    {
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();
        //check whether the strings are empty
        if(question1.equals("")||question2.equals(""))
        {
            Toast.makeText(ForgetPasswordActivity.this,"Please set both Questions",Toast.LENGTH_SHORT).show();
        }
        else
        {
            //set up database reference in the online users node
            DatabaseReference securityRef= FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String, Object> securitydataMap = new HashMap<>();
            securitydataMap.put("Answer1", answer1);
            securitydataMap.put("Answer2", answer2);
            //store the data in the database
            securityRef.child("Security Questions").updateChildren(securitydataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgetPasswordActivity.this,"Answers set succesfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this, HomActivity.class);
                        startActivity(intent);
                    }

                }
            });
        }
    }
    //retrieve previous answers
    private void displaySecurityQuestions()
    {
        DatabaseReference securityRef= FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        securityRef.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                  String ans1= dataSnapshot.child("Answer1").getValue().toString();
                  String ans2= dataSnapshot.child("Answer2").getValue().toString();
                  question1.setText(ans1);
                  question2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    private void verifyUser()
    {
      //get the input fields from user first
        final String answer1=question1.getText().toString().toLowerCase();
        final String answer2=question2.getText().toString().toLowerCase();
        final String phoneNum=phoneNumber.getText().toString();
        //verify if the above parameters are null otherwise you get a crash
        if(!phoneNum.equals("")&& !answer1.equals("")&& !answer2.equals(""))
        {

            final DatabaseReference securityRef= FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(phoneNum);//because user is not logged in already so we get it from the edit text
            //check whether user and security questions exist
            securityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        //retrieve phone number from database
                        String mPhone=dataSnapshot.child("phone").getValue().toString();
                        //no need to check in case users shipment number is not equal to their unique key phone number since they are not logged in
                        //check whether the security questions are there
                        if(dataSnapshot.hasChild("Security Questions"))
                        {
                            //retrieve answers for comparison go into the security questions node, add another child
                            String ans1= dataSnapshot.child("Security Questions").child("Answer1").getValue().toString();
                            String ans2= dataSnapshot.child("Security Questions").child("Answer2").getValue().toString();
                            if(!ans1.equals(answer1))
                            {
                                Toast.makeText(ForgetPasswordActivity.this,"First answer is incorrect",Toast.LENGTH_SHORT).show();
                            }
                            else if(!ans2.equals(answer2))
                            {
                                Toast.makeText(ForgetPasswordActivity.this,"Second answer is incorrect",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //allow user to change password
                                AlertDialog.Builder builder=new AlertDialog.Builder(ForgetPasswordActivity.this);
                                builder.setTitle("New Password");
                                //new edit text for the new password entry
                                final EditText newPassword=new EditText(ForgetPasswordActivity.this);
                                newPassword.setHint("write new password here");
                                builder.setView(newPassword);
                                //
                                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(!newPassword.getText().toString().equals(""))
                                        {
                                            securityRef.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(ForgetPasswordActivity.this,"password reset successfully",Toast.LENGTH_SHORT).show();
                                                                //send user to the login screen
                                                                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                        //naughty users with empty password fields
                                        else
                                            {
                                                Toast.makeText(ForgetPasswordActivity.this,"Password field cannot be empty",Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                        else
                        {
                            //found no security questions node in their account
                            Toast.makeText(ForgetPasswordActivity.this,"You did not set security questions, please contact customer care",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        //if user inputs a wrong number
                        Toast.makeText(ForgetPasswordActivity.this,"The user phone number does not exist",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
        //in case any of the fields are empty
        else
            {
                Toast.makeText(ForgetPasswordActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
            }
    }

}

