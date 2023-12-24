package com.example.watchesstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.watchesstore.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DetailedProduct extends AppCompatActivity {
    ImageView imgDetail, imgPlus, imgMinus;
    TextView name, description, price, quantity;
    RatingBar rating;
    Button btnAddToCart;
    Toolbar toolbar;
    private Product product = null;
    private int totalQuantity = 1;
    private Double currProductPrice;
    private int productId;
    private String imgUrl;
    private boolean checkItemAlreadyHad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product);

        initUI();
        initListener();
        showDetailProduct();

    }

    private void initUI() {
        imgDetail = findViewById(R.id.imgProduct);
        rating = findViewById(R.id.rbRating);
        name = findViewById(R.id.tvProductName);
        description = findViewById(R.id.tvContextOfDescription);
        price = findViewById(R.id.tvTotalPrice);
        quantity = findViewById(R.id.tvQuantityProduct);
        //button
        btnAddToCart = findViewById(R.id.btnAddToCart);
        imgPlus = findViewById(R.id.imgPlus);
        imgMinus = findViewById(R.id.imgMinus);
        //toolbar
        toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private void initListener() {
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Toast.makeText(DetailedProduct.this, "Please Login First", Toast.LENGTH_LONG).show();

        }
        else{
                addToCart2();
            }}
        });
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 10) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    if (product != null) {
                        price.setText("$ " + String.valueOf(currProductPrice * totalQuantity));
                    }

                }

            }
        });
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    if (product != null) {
                        price.setText("$ " + String.valueOf(currProductPrice * totalQuantity));
                    }
                }
            }
        });
    }

    private void addToCart2() {
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
//
//
        HashMap<String, Object> cartMap = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart/" + id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String idProduct = data.getKey();//id of product on firebase
                    if (idProduct.equals(String.valueOf(productId))) {
                        checkItemAlreadyHad = true;
                        int number = data.child("totalQuantity").getValue(Integer.class);
                        Double totalCost = (number + totalQuantity) * currProductPrice;
                        cartMap.put("totalQuantity", number + totalQuantity);
                        cartMap.put("totalPrice", totalCost);
                        cartMap.put("currentDate", saveCurrentDate);
                        cartMap.put("currentTime", saveCurrentTime);
                        databaseReference.child(idProduct).updateChildren(cartMap);
                        Toast.makeText(DetailedProduct.this, idProduct, Toast.LENGTH_LONG).show();
                    }
                }
                if (checkItemAlreadyHad == false) {
                    cartMap.put("id", productId);
                    cartMap.put("productName", name.getText().toString());
                    cartMap.put("productPrice", currProductPrice);
                    cartMap.put("totalQuantity", totalQuantity);
                    cartMap.put("totalPrice", totalQuantity * currProductPrice);
                    cartMap.put("imgUrl", imgUrl);
                    cartMap.put("currentDate", saveCurrentDate);
                    cartMap.put("currentTime", saveCurrentTime);
                    databaseReference.child(String.valueOf(productId)).setValue(cartMap);
                    Toast.makeText(DetailedProduct.this, "Add cai moi", Toast.LENGTH_LONG).show();

                }
                checkItemAlreadyHad=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        Query query=databaseReference.orderByChild("id").equalTo(productId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int number=snapshot.getValue(Integer.class);
//                cartMap.put("totalQuantity",totalQuantity+1);
//                cartMap.put("totalPrice",price.getText().toString());
//                cartMap.put("currentDate",saveCurrentDate);
//                cartMap.put("currentTime",saveCurrentTime);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    private void addToCart() {
        List<Product> list;
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("productName", name.getText().toString());
        cartMap.put("productPrice", currProductPrice);
        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", price.getText().toString());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart/" + id);
        databaseReference.setValue(cartMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(DetailedProduct.this, "Add Completed", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void showDetailProduct() {
        Object obj = getIntent().getSerializableExtra("detail");
        if (obj instanceof Product) {
            product = (Product) obj;
        }
        if (product != null) {
            Glide.with(getApplicationContext()).load(product.getImageUrl()).into(imgDetail);
            name.setText(product.getName());
            description.setText(product.getDescription());
            price.setText("$ " + String.valueOf(product.getPrice()));
            //get current Product value--> to calculate total Price
            imgUrl = product.getImageUrl();
            currProductPrice = product.getPrice();
            productId = product.getId();
        }
    }

    //handle back event on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}