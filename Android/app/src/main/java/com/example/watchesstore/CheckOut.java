package com.example.watchesstore;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchesstore.adapter.CartAdapter;
import com.example.watchesstore.fragment.Home;
import com.example.watchesstore.models.ItemsCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class CheckOut extends AppCompatActivity {
    private FirebaseUser mAuth;
    private EditText edEmail;
    private EditText edPhone;
    private EditText edAddress;
    private Button btnPay;
    private TextView tvTotal;
    CartAdapter cartAdapter;
    List<ItemsCart> cartList;

    private int totalPriceOfCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        initUi();
        initListener();
    }

    private void initUi() {
        edEmail = findViewById(R.id.payer_email);
        edPhone = findViewById(R.id.payer_phone);
        edAddress = findViewById(R.id.payer_address);
        btnPay = findViewById(R.id.pay);
        tvTotal = findViewById(R.id.total);
        edEmail.setText(mAuth.getEmail());
        cartAdapter = new CartAdapter(CheckOut.this, cartList);
        cartList = (List<ItemsCart>) getIntent().getSerializableExtra("cart");
        totalPriceOfCart = (int) getIntent().getSerializableExtra("totalPrice");
        tvTotal.setText("TOTAL: " + String.valueOf(totalPriceOfCart));
        setUserInfor();
    }

    private void initListener() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOut();
            }

        });
    }

    private void checkOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        CollectionReference invoicesCollection = fStore.collection("AllInvoices").document(user.getUid()).collection("invoices");
        invoicesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int newInvoiceIndex = 0;

                    if (task.getResult().size() == 0) {
                        newInvoiceIndex = 1;
                    } else {
                        newInvoiceIndex = task.getResult().size() + 1;
                    }
                    Log.d("NEW INDEX", String.valueOf(newInvoiceIndex));

                    Map<String, Object> newInvoice = new HashMap<>();
                    newInvoice.put("id", newInvoiceIndex);
                    newInvoice.put("userEmail", user.getEmail());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    List<ItemsCart> listItemCart = new ArrayList<>();

                    newInvoice.put("date", dtf.format(now));

                    newInvoice.put("totalPrice", totalPriceOfCart);
                    newInvoice.put("listProduct", cartList);
                    newInvoice.put("status", "Pending");


                    invoicesCollection.document(String.valueOf(newInvoiceIndex))
                            .set(newInvoice)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("NEW INVOICE", "Document added with index: ");
                                    deleteCartData(user.getUid());
                                    updateUserInfoInFireStore(user.getEmail(), edAddress.getText().toString(), edPhone.getText().toString());
//                                    finish();
                                    Intent i=new Intent(CheckOut.this ,MenuSelection.class);
                                    startActivity(i);
                                }
                            });
                }
            }
        });


    }

    private void deleteCartData(String userId) {
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        // Remove the entire "cart" node for the specified user
        cartReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Successfully deleted cart data
                    // You can handle success here, if needed
                } else {
                    // Failed to delete cart data
                    // You can handle failure here, if needed
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.e("Firebase", "Failed to delete cart data: " + exception.getMessage());
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUserInfor() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(user.getUid());

        // Fetch the document
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Document exists, you can access the data
                    String address = documentSnapshot.getString("address");
                    if (address != null) {
                        edAddress.setText(address);

                    } else {
                        // Address field is null or doesn't exist
                        Log.d("Firestore", "User address is null or does not exist");
                    }
                    String phone = documentSnapshot.getString("phoneNumber");
                    if (phone != null) {
                        edPhone.setText(phone);
                    } else {
                        // Address field is null or doesn't exist
                        Log.d("Firestore", "User address is null or does not exist");
                    }

                } else {
                    // Document does not exist
                    Log.d("Firestore", "User document does not exist");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                Log.e("Firestore", "Error getting user document: " + e.getMessage());
            }
        });
    }

    private void updateUserInfoInFireStore(String email, String address, String phoneNumber) {
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("address", address);
        userDetail.put("phoneNumber", phoneNumber);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            fStore.collection("Users")
                                    .document(documentId)
                                    .update(userDetail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                    }
                });


    }


}