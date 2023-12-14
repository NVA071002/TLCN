package com.example.watchesstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.watchesstore.DetailedProduct;
import com.example.watchesstore.R;
import com.example.watchesstore.models.Product;

import java.util.List;

public class ProductAdapter  extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> listProduct;
//Product Adapter dung de hien thi cac san pham len tren trang home
    public ProductAdapter(Context context, List<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(listProduct.get(position).getImageUrl()).into(holder.productImg);
        holder.productName.setText(listProduct.get(position).getName());
        holder.productPrice.setText(listProduct.get(position).getPrice().toString());
        int id=holder.getAdapterPosition();
        //set onclick move to detail page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, DetailedProduct.class);
                i.putExtra("detail",listProduct.get(id));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView productName;
        TextView productPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg=itemView.findViewById(R.id.imgProduct);
            productName=itemView.findViewById(R.id.tvProductName);
            productPrice=itemView.findViewById(R.id.tvProducPrice);
        }
    }
}
