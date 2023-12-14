package com.example.watchesstore;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watchesstore.adapter.CartAdapter;
import com.example.watchesstore.models.ItemsCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyCart extends AppCompatActivity {
    private FirebaseUser mAuth;
    private DatabaseReference databaseReference;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    List<ItemsCart> cartList = new ArrayList<>();
    private TextView tvTotalPrice;
    private Button btnCheckOut;
    Toolbar toolbar;
    private int totalPriceOfCart, checkChange;
    private ItemsCart itemsCart;
    //Receiver totalPrice from CartAdapter when change quantity in cart
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            } else {
                itemsCart = (ItemsCart) bundle.get("ItemCart");
                checkChange = (int) bundle.get("checkChange");
            }
            if (itemsCart != null) {
//                openSheetDialog();
                                removeItem();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_totalPrice"));
        showListItems();
        initUi();
        initListener();
    }

    private void initUi() {
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        recyclerView = findViewById(R.id.rec_ShowMyCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCart.this, RecyclerView.VERTICAL, false));
        //Cart
        cartAdapter = new CartAdapter(MyCart.this, cartList);
        recyclerView.setAdapter(cartAdapter);
        //toolbar
        toolbar = findViewById(R.id.tb_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initListener() {
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkOut()) {
                    Intent intent = new Intent(MyCart.this, CheckOut.class);
                    intent.putExtra("totalPrice", totalPriceOfCart);
                    intent.putExtra("cart", (Serializable) cartList);
                    startActivity(intent);
                }
            }

        });
    }

    private boolean checkOut() {
        if (cartList.isEmpty()){
            Toast.makeText(this, "CART IS EMPTY",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();

    }
//    private void openSheetDialog() {
//        View viewDialog = getLayoutInflater().inflate(R.layout.bottom_sheet_confirm_delete, null);
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        bottomSheetDialog.setContentView(viewDialog);
//        bottomSheetDialog.show();
////        bottomSheetDialog.setCancelable(false);//dung de chan nguoi dung tat dialog nay di (= scroll/ click ra ngoai...)
//        Button btnCancel = viewDialog.findViewById(R.id.btnCancel);
//        Button btnRemove = viewDialog.findViewById(R.id.btnRemove);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bottomSheetDialog.dismiss();
//            }
//        });
//        btnRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeItem();
//                bottomSheetDialog.dismiss();
//            }
//        });
//    }

    private void showListItems() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart/" + id);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ItemsCart item = snapshot.getValue(ItemsCart.class);
                if (item != null) {
                    cartList.add(item);
                    //set TotalPrice for MyCart page
                    totalPriceOfCart += item.getTotalPrice();
                    tvTotalPrice.setText("$ " + String.valueOf(totalPriceOfCart));
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ItemsCart cat = snapshot.getValue(ItemsCart.class);
                if (cat == null || cartList == null || cartList.isEmpty()) {
                    cartAdapter.notifyDataSetChanged();
                    return;
                }
                for (int i = 0; i < cartList.size(); i++) {
                    //dua vao name de thay doi image.
                    if (cat.getId() == cartList.get(i).getId()) {
                        cartList.set(i, cat);
                        if (checkChange == 1) {
                            totalPriceOfCart += cat.getProductPrice();
                        }
                        if (checkChange == 0) {
                            totalPriceOfCart -= cat.getProductPrice();
                        }
                    }
                }
                tvTotalPrice.setText("$ " + String.valueOf(totalPriceOfCart));
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //    @Override
//    public void setTotalPrice(Double a){
//        tvTotalPrice.setText("$ "+String.valueOf(a));
//    }
    //remove item from cart
    private void removeItem() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart/" + id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String idProduct = data.getKey();//id of product on firebase
                    if (idProduct.equals(String.valueOf(itemsCart.getId()))) {
                        cartList.remove(itemsCart);
                        totalPriceOfCart -= itemsCart.getTotalPrice();
                        tvTotalPrice.setText("$ " + String.valueOf(totalPriceOfCart));
                        data.getRef().removeValue();
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}