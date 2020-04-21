package com.example.chopbachktet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chopbachktet.Prevalent.Prevalent;
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
    //retrieeve previous answers
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
    }

