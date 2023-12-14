package com.example.watchesstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.watchesstore.adapter.ShowAllProductAdapter;
import com.example.watchesstore.models.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ShowAllProduct extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllProductAdapter showAllProductAdapter;
    List<Product> productList;

    SearchView searchView;
    ChildEventListener childEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_product);
        initUI();
        String type=getIntent().getStringExtra("type");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        if(type!=null){
            showProductIntoCategory(type);
        }
        else{
            showAllProduct();
        }

    }
    private void initUI(){
        recyclerView=findViewById(R.id.recShowAllProduct);
        searchView = findViewById(R.id.search_bar);
        searchView.clearFocus();
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        productList=new ArrayList<>();
        showAllProductAdapter=new ShowAllProductAdapter(this,productList);
        recyclerView.setAdapter(showAllProductAdapter);
    }
    private void showProductIntoCategory(String type){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Products");
        Query query=databaseReference.orderByChild("type").equalTo(type);
        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product=snapshot.getValue(Product.class);
                if(product!=null){
                    productList.add(product);
                }
                showAllProductAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    public void searchList(String text){
        ArrayList<Product> searchList = new ArrayList<>();
        for (Product prod: productList){
            if (prod.getName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(prod);
            }
        }
        showAllProductAdapter.searchProductList(searchList);
    }
    private void showAllProduct(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Products");
        childEventListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product=snapshot.getValue(Product.class);
                if(product!=null){
                    productList.add(product);
                }
                showAllProductAdapter.notifyDataSetChanged();
                //show home layout and close dialog

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product=snapshot.getValue(Product.class);
                if(product ==null||productList==null||productList.isEmpty()){
                    return;
                }
                for (int i=0;i<productList.size();i++){
                    //dua vao name de thay doi image.
                    if(product.getId()==productList.get(i).getId()){
                        productList.set(i,product);
                    }
                }
                showAllProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product=snapshot.getValue(Product.class);
                if(product ==null||productList==null||productList.isEmpty()){
                    return;
                }
                for (int i=0;i<productList.size();i++){
                    //dua vao name de thay doi image.
                    if(product.getId()==productList.get(i).getId()){
                        productList.remove(i);
                        break;
                    }
                }
                showAllProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}