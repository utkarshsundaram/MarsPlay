package com.example.marsplay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marsplay.R;
import com.example.marsplay.model.ImageUploadedModel;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

public List<ImageUploadedModel>mStorageReference=new ArrayList<>();
public Context mContext;
private final String TAG=ImageListAdapter.class.getName();
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list,parent,false);
        mContext=parent.getContext();
       return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
       if(mStorageReference.size()>0) {
           Picasso.get()
                   .load(mStorageReference.get(position).getFilePath())
                   .into(holder.imageView, new Callback() {
                       @Override
                       public void onSuccess() {
                           Log.e(TAG,"success");
                       }

                       @Override
                       public void onError(Exception e) {
                           Log.e(TAG,e.getMessage());
                       }
                   });

           holder.mTextView.setText(mStorageReference.get(position).getName());

       }
    }

    public List<ImageUploadedModel> getmStorageReference() {
        return mStorageReference;
    }

    public void setmStorageReference(List<ImageUploadedModel> mStorageReference) {
        this.mStorageReference = mStorageReference;
    }

    @Override
    public int getItemCount() {
        return mStorageReference.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public ZoomageView imageView;
        public TextView mTextView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img_zoom_view);
            mTextView=itemView.findViewById(R.id.txv_name);
        }
    }
}
