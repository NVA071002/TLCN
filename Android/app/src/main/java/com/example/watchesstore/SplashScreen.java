package com.example.watchesstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    Handler h=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        },1500);
    }
    private void nextActivity(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        if(user==null){
//            //not logged login
//            Intent i=new Intent(SplashScreen.this,Signin.class);
//            startActivity(i);
//        }
//        else{
            //already logged in
            Intent i=new Intent(SplashScreen.this,MenuSelection.class);
            startActivity(i);
//        }
        finish();
    }

}