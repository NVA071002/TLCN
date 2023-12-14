package com.example.watchesstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private EditText edEmail, edUserName, edPassword, edPhoneNumber, edAddress;
    private Button btnSignUp;
    private TextView tvSignIn;
    boolean validInput = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initUI();
        checkField(edEmail);
        checkField(edUserName);
        checkField(edPassword);
/*        checkField(edPhoneNumber);
        checkField(edAddress);*/

//        if (validInput){
//        initListener();}
        initListener();
    }

    private void initUI() {
        tvSignIn = findViewById(R.id.tvSignUp);
        edEmail = findViewById(R.id.edEmail);
        edUserName = findViewById(R.id.edUserName);
        edPassword = findViewById(R.id.edPassword);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);
        edAddress = findViewById(R.id.edAddress);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    private void initListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
//        tvSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(Signup.this,Signup.class);
//                startActivity(i);
//            }
//        });

    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
//            textField.setT("Input Required");
            validInput = false;
        } else {
            validInput = true;
        }

        return validInput;
    }

    private void onClickSignUp() {
        String strEmail = edEmail.getText().toString().trim();
        if (strEmail.equals("")) {
            Toast.makeText(Signup.this, "Please fill the email", Toast.LENGTH_LONG).show();
        } else {
            String strPassword = edPassword.getText().toString().trim();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
           FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i = new Intent(Signup.this, MenuSelection.class);
                                DocumentReference df = fStore.collection("Users").document(user.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("userName", edUserName.getText().toString());
                                userInfo.put("email", edEmail.getText().toString());
                                userInfo.put("phoneNumber", edPhoneNumber.getText().toString());
                                userInfo.put("address", edAddress.getText().toString());
                                userInfo.put("isUser", 1);
                                df.set(userInfo);
                                startActivity(i);

                                finishAffinity();//dong tat ca activity bao gom(signup va man hinh signin o phia truoc)
                            } else {
                                // If sign in fails, display a message to the user.
                                if (strPassword.length() < 6) {
                                    Toast.makeText(Signup.this, "Password must have more than 6 characters!!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Signup.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }

    }
}