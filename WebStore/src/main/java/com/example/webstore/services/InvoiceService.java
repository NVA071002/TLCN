package com.example.webstore.services;

import com.example.webstore.entities.Invoice;
import com.example.webstore.entities.User;
import com.example.webstore.model.UpdateInvoiceRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class InvoiceService {
    private final Firestore db = FirestoreClient.getFirestore();
    @Autowired
    Userservice userservice;
    public void updateStatusInvoice(UpdateInvoiceRequest updateInvoiceRequest) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(updateInvoiceRequest.getUserEmail());
        Map<String,Object> tmp=new HashMap<>();
        tmp.put("status",updateInvoiceRequest.getStatus());

        ApiFuture<WriteResult> query = db.collection("AllInvoices").document(userRecord.getUid()).collection("invoices")
                .document(String.valueOf(updateInvoiceRequest.getId())).update(tmp);


    }
    public List<Integer> getPriceByUser() throws FirebaseAuthException, ExecutionException, InterruptedException {

        List<Integer> list=new ArrayList<>();
        List<String> user=userservice.getListEmail();
        for(String email:user){
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            ApiFuture<QuerySnapshot> query = db.collection("AllInvoices").document(userRecord.getUid()).collection("invoices").get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            int total = 0;
            for (QueryDocumentSnapshot document : documents) {
                Invoice tmp=document.toObject(Invoice.class);
                total+=tmp.getTotalPrice();
            }
            if(total!=0){
                list.add(total);
            }

        }
        return list;
    }
    public List<Invoice> getListInvoice(String userEmail) throws ExecutionException, InterruptedException, FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userEmail);
        List<Invoice> listInvoice=new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection("AllInvoices").document(userRecord.getUid()).collection("invoices").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Invoice tmp=document.toObject(Invoice.class);
            listInvoice.add(tmp);
        }
        return listInvoice;

    }
    public List<Invoice> getListInvoice() throws ExecutionException, InterruptedException, FirebaseAuthException {
        List<User> users=userservice.getListUser();
        List<Invoice> listInvoice=new ArrayList<>();
        for (User user : users) {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(user.getEmail());
            ApiFuture<QuerySnapshot> query = db.collection("AllInvoices").document(userRecord.getUid()).collection("invoices").get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Invoice invoices=document.toObject(Invoice.class);
                listInvoice.add(invoices);
            }
        }

        return listInvoice;

    }
}
