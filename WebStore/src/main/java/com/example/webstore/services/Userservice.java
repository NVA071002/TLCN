package com.example.webstore.services;

import com.example.webstore.entities.User;
import com.example.webstore.model.UpdateUserRequest;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class Userservice {
    private final Firestore db = FirestoreClient.getFirestore();
    public List<User> getListUser() throws ExecutionException, InterruptedException {
        List<User> listUser=new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection("Users").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            User tmp=document.toObject(User.class);
            listUser.add(tmp);
        }
        return listUser;

    }
    public List<String> getListEmail() throws ExecutionException, InterruptedException {
        List<String> listUser=new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection("Users").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            User tmp=document.toObject(User.class);
            listUser.add(tmp.getEmail());
        }
        return listUser;

    }
    public List<String> getListName() throws ExecutionException, InterruptedException {
        List<String> listUser=new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection("Users").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            User tmp=document.toObject(User.class);
            listUser.add(tmp.getUserName());
        }
        return listUser;

    }
    public List<String> getListUserId() throws ExecutionException, InterruptedException {
        List<String> list=new ArrayList<>();
        List<QueryDocumentSnapshot> query = db.collection("Users").get().get().getDocuments();
        for (QueryDocumentSnapshot document : query) {
            list.add(document.getId()); // getId() returns the document name
        }
        return list;

    }
    public String insertUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("Users").document("Random");
        ApiFuture<WriteResult> result = docRef.set(user);
        String mess=handleException(result);
        return mess;
    }
    public String deleteUser(String userEmail) throws ExecutionException, InterruptedException, FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userEmail);
        DocumentReference docRef = db.collection("Users").document(userRecord.getUid());
        ApiFuture<WriteResult> result = docRef.delete();
        return handleException(result);

    }
    public String updateUser(UpdateUserRequest updateUserRequest) throws ExecutionException, InterruptedException, FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(updateUserRequest.getUserEmail());
//        System.out.println("Successfully fetched user data: " + userRecord.getUid());
        DocumentReference docRef = db.collection("Users").document(userRecord.getUid());
        Map<String,Object> tmp=new HashMap<>();
        if(updateUserRequest.getIsUser()!=null){
            tmp.put("isUser",updateUserRequest.getIsUser());
        }
        if(StringUtils.isNotBlank(updateUserRequest.getAddress())){
            tmp.put("address",updateUserRequest.getAddress());
        }
        if(StringUtils.isNotBlank(updateUserRequest.getPhoneNumber())){
            tmp.put("phoneNumber",updateUserRequest.getPhoneNumber());
        }

        ApiFuture<WriteResult> result = docRef.update(tmp);
        return handleException(result);

    }
    private String handleException(ApiFuture<WriteResult> tmp) throws ExecutionException, InterruptedException {
        CompletableFuture<String> message = new CompletableFuture<>();
        ApiFutures.addCallback(tmp, new ApiFutureCallback<WriteResult>() {
            @Override
            public void onFailure(Throwable throwable) {
                message.complete("Failure");
            }

            @Override
            public void onSuccess(WriteResult writeResult) {
                message.complete("Success");
            }
        });
        return message.get();
    }
}
