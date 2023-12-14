package com.example.webstore;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class WebStoreApplication {

    public static void main(String[] args) throws IOException {

        if (FirebaseApp.getApps().isEmpty()){
            FileInputStream serviceAccount=new FileInputStream("./serviceAccountKey.json");


            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://android-watches-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();
            FirebaseApp.initializeApp(options);
        }
        else{
            FirebaseApp.getInstance();
        }

        SpringApplication.run(WebStoreApplication.class, args);
    }


}
