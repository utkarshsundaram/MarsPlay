package com.example.marsplay.imagelist;

import com.example.marsplay.model.ImageUploadedModel;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImageListViewDelegate implements ImageListContract.View {
    public ImageListContract.View mView;

    public ImageListContract.View setView(ImageListContract.View view){
        mView=view;
        return this;
    }

    @Override
    public void showImageList(List<ImageUploadedModel> storageReferences) {
      if(mView!=null){
          mView.showImageList(storageReferences);
      }
    }

    @Override
    public void shouldAskPermisssion() {
        if(mView!=null){
            mView.shouldAskPermisssion();
        }
    }

    @Override
    public void errorImageList() {
        if(mView!=null){
            mView.errorImageList();
        }
    }

    public ImageListContract.View getmView() {
        return mView;
    }
}
