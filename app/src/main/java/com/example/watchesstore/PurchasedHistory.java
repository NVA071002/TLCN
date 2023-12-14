package com.example.watchesstore;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchesstore.adapter.CartAdapter;
import com.example.watchesstore.adapter.InvoiceAdapter;
import com.example.watchesstore.models.Invoice;
import com.example.watchesstore.models.ItemsCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PurchasedHistory extends AppCompatActivity {
    private FirebaseUser mAuth;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;
    InvoiceAdapter invoiceAdapter;
    private Button btnBack;

    List<Invoice> listInvoice = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        showListInvoice();
        initUi();
        initListener();
    }

    private void initListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initUi() {
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.rec_ShowInvoice);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(PurchasedHistory.this, RecyclerView.VERTICAL, false));
        invoiceAdapter = new InvoiceAdapter(PurchasedHistory.this, listInvoice);
        recyclerView.setAdapter(invoiceAdapter);


    }

    private void showListInvoice() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference invoicesCollection = db.collection("AllInvoices").document(id).collection("invoices");

        invoicesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // The query was successful, process the documents
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert each document to YourObject
                        Invoice invoice = document.toObject(Invoice.class);
                        listInvoice.add(invoice);
                        invoiceAdapter.notifyDataSetChanged();

                    }


                } else {
                    // Handle the error
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

    }

}
