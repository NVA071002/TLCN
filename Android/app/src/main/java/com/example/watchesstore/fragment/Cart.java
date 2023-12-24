package com.example.watchesstore.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.watchesstore.R;
import com.example.watchesstore.adapter.CartAdapter;
import com.example.watchesstore.models.ItemsCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Cart extends Fragment  {
    private View mView;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    List<ItemsCart> cartList;
    private TextView tvTotalPrice;
    private Button btnCheckOut;

    public Cart() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_cart2,container,false);
        initUi();
        showListItems();

        return mView;
    }
    private void initUi(){
        tvTotalPrice=mView.findViewById(R.id.tvTotalPrice);
        btnCheckOut=mView.findViewById(R.id.btnCheckOut);
        recyclerView=mView.findViewById(R.id.rec_ShowMyCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        //Cart
        cartList=new ArrayList<>();
        cartAdapter=new CartAdapter(getContext(),cartList);
        recyclerView.setAdapter(cartAdapter);
    }
    private void initListener(){

    }
    private void showListItems(){
        FirebaseUser mAuth= FirebaseAuth.getInstance().getCurrentUser();
        if(mAuth!=null){
        String id=mAuth.getUid();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Cart/"+id);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ItemsCart item=snapshot.getValue(ItemsCart.class);
                if(item!=null){
                    cartList.add(item);
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ItemsCart cat=snapshot.getValue(ItemsCart.class);
                if(cat ==null||cartList==null||cartList.isEmpty()){
                    return;
                }
                for (int i=0;i<cartList.size();i++){
                    //dua vao name de thay doi image.
                    if(cat.getId()==cartList.get(i).getId()){
                        cartList.set(i,cat);
                    }
                }
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
    }}
//    @Override
//    public void setTotalPrice(Double a){
//        tvTotalPrice.setText("$ "+String.valueOf(a));
//    }
}