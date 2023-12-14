package com.example.watchesstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.watchesstore.ShowAllProduct;
import com.example.watchesstore.models.Category;
import com.example.watchesstore.models.Invoice;
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

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder>{
    private Context context;
    private List<Invoice> listInvoice;

    public InvoiceAdapter(Context context, List<Invoice> listInvoice) {
        this.context = context;
        this.listInvoice = listInvoice;
    }

    @NonNull
    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_history,parent,false);
        return new InvoiceAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.ViewHolder holder, int position) {
        holder.tvId.setText("Order Number: "+listInvoice.get(position).getId());
        holder.tvDate.setText("Date: "+listInvoice.get(position).getDate());
        holder.tvTotalPrice.setText("$"+listInvoice.get(position).getTotalPrice());
        if(listInvoice.get(position).getStatus().equals("Pending")){
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/android-watches.appspot.com/o/invoiceStatus%2Ftime.png?alt=media&token=556a8f91-b25e-4a5b-b44f-775da678488c").into(holder.status);
        }
        if(listInvoice.get(position).getStatus().equals("Paid") ){
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/android-watches.appspot.com/o/invoiceStatus%2Fchecked.png?alt=media&token=dbd17428-a4d1-498e-992d-e837fc663512").into(holder.status);
        }
        if(listInvoice.get(position).getStatus().equals("Cancelled")){
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/android-watches.appspot.com/o/invoiceStatus%2Fcross.png?alt=media&token=077d167f-0178-4d02-9281-e9cadc4b39b9").into(holder.status);
        }


    }

    @Override
    public int getItemCount() {
        return listInvoice.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView status;

        TextView tvId;

        TextView tvDate;
        TextView tvTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status=itemView.findViewById(R.id.invoiceImg);
            tvId=itemView.findViewById(R.id.tvInvoiceId);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvTotalPrice=itemView.findViewById(R.id.tvTotalPrice);

        }
    }

}
