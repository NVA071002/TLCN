package com.example.watchesstore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchesstore.R;
import com.example.watchesstore.models.Photo;

import java.util.List;

public class ImageSlideHomeAdapter extends  RecyclerView.Adapter<ImageSlideHomeAdapter.PhotoViewHolder> {

    private List<Photo> mListPhoto;

    public ImageSlideHomeAdapter(List<Photo> mListPhoto) {
        this.mListPhoto = mListPhoto;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_image_news,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo=mListPhoto.get(position);
        if(photo==null){
            return;
        }
        holder.img.setImageResource(photo.getResourceId());

    }

    @Override
    public int getItemCount() {
        if(mListPhoto!=null){
            return mListPhoto.size();
        }
        return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img_news);
        }
    }
}
