package com.example.watchesstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.watchesstore.R;
import com.example.watchesstore.models.ItemsCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private Context context;
    private List<ItemsCart> listItems;
    private Double totalCost=0.0;
    private FirebaseUser mAuth;
    private DatabaseReference databaseReference;
//    SendTotalPrice sendData;
    //swipe to delete item
    private ViewBinderHelper viewBinderHelper=new ViewBinderHelper();


    public CartAdapter(Context context, List<ItemsCart> listItems) {
        this.context = context;
        this.listItems = listItems;
//        this.sendData=sendData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //items_cart: not use swipe to delete # items_cart2: use swipe to delete
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items_cart2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        viewBinderHelper.bind(holder.swipeRevealLayout,String.valueOf(listItems.get(position).getId()));

        Glide.with(context).load(listItems.get(position).getImgUrl()).into(holder.productImg);
        holder.productName.setText(listItems.get(position).getProductName());
        holder.productTotalPrice.setText("$ "+listItems.get(position).getTotalPrice());
        holder.quantity.setText(String.valueOf(listItems.get(position).getTotalQuantity()));
        //send Total Price in Cart
//        totalCost+=listItems.get(position).getTotalPrice();
//        sendData.setTotalPrice(totalCost);
        int index=holder.getAdapterPosition();
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEventPlusAndMinus(0,index);

            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEventPlusAndMinus(1,index);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemsCart item=listItems.get(index);
                sendTotalCartToCartActivivty(2,item);
            }
        });



    }



    private void handleEventPlusAndMinus(int a,int position){
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String id = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart/" + id);
        HashMap<String, Object> cartMap = new HashMap<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String idProduct = data.getKey();//id of product on firebase
                    if (idProduct.equals(String.valueOf(listItems.get(position).getId()))) {
                        int number = data.child("totalQuantity").getValue(Integer.class);
                        if(a==0){
                            if(number>1){
                                number--;
                                sendTotalCartToCartActivivty(0,null);
                            }
                        }
                        else{
                            //how to determine the limit of number when increased ?
                            number++;
                            sendTotalCartToCartActivivty(1,null);
                        }
                        Double totalCost = Double.valueOf(number * listItems.get(position).getProductPrice());
                        cartMap.put("totalQuantity", number);
                        cartMap.put("totalPrice", totalCost);
                        databaseReference.child(idProduct).updateChildren(cartMap);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendTotalCartToCartActivivty(int a,ItemsCart b){
        Intent i=new Intent("send_totalPrice");
        Bundle bundle=new Bundle();
        bundle.putInt("checkChange",a);
        if(b!=null){
            bundle.putSerializable("ItemCart",b);
        }
        i.putExtras(bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);

    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImg;
        TextView productName,quantity;
        TextView productTotalPrice;
        ImageView btnMinus,btnPlus;
//        SendTotalPrice sendTotalPrice;
        //swipe to delete items
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDelete;
        private Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            this.sendTotalPrice=sendData;
            productImg=itemView.findViewById(R.id.imgProduct);
            productName=itemView.findViewById(R.id.tvProductName);
            productTotalPrice=itemView.findViewById(R.id.tvProducTotalPrice);
            quantity=itemView.findViewById(R.id.tvQuantityProduct);
            btnMinus=itemView.findViewById(R.id.imgMinus);
            btnPlus=itemView.findViewById(R.id.imgPlus);
            //swipe to delete
            swipeRevealLayout=itemView.findViewById(R.id.swipeLayout);
            layoutDelete=itemView.findViewById(R.id.layoutDelete);
            btnDelete=itemView.findViewById(R.id.btnDelete);
        }
    }
//    public interface SendTotalPrice{
//        void setTotalPrice(Double a);
//    }

}
