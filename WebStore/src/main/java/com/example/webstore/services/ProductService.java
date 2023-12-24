package com.example.webstore.services;

import com.example.webstore.entities.Product;
import com.example.webstore.entities.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ProductService {
    public CompletableFuture<Product> findProductById(String id) throws ExecutionException, InterruptedException {
        CompletableFuture<Product> future = new CompletableFuture<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Products/"+id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                future.complete(product);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return future;
    }
    public CompletableFuture<List<Product>> getListProduct() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Products");
        databaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> list=new ArrayList<>();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Product product = data.getValue(Product.class);
                    list.add(product);
                }

                future.complete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return future;
    }

    public String insertProuct(Product product){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Products");
        ApiFuture<Void> future= databaseReference.child(String.valueOf(product.getId())).setValueAsync(product);
        try {
            // Wait for the operation to complete
            future.get();
            // If successful, return a success message
            return "Success";
        } catch (Exception e) {
            // If there's an exception, return a failure message
            return "Failed to insert product: " + e.getMessage();
        }
    }
    public String updateProduct(Product product){

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Products");
        Map<String,Object> tmp=new HashMap<>();
        tmp.put("id",product.getId());
        tmp.put("name",product.getName());
        tmp.put("price",product.getPrice());
        tmp.put("description",product.getDescription());
        tmp.put("imageUrl",product.getImageUrl());
        tmp.put("type",product.getType());
        ApiFuture<Void> future=databaseReference.child(String.valueOf(product.getId())).updateChildrenAsync(tmp);
        try {
            // Wait for the operation to complete
            future.get();
            // If successful, return a success message
            return "Success";
        } catch (Exception e) {
            // If there's an exception, return a failure message
            return "Failed to insert product: " + e.getMessage();
        }
    }
    public void deleteProduct(int id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        databaseReference.child(String.valueOf(id)).removeValue(new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be deleted: " + databaseError.getMessage());
                } else {
                    System.out.println("Data deleted successfully.");
                }
            }
        });
    }

}
