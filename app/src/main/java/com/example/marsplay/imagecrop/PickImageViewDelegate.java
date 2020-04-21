package com.example.marsplay.imagecrop;

import com.example.marsplay.base.MvpView;

public class PickImageViewDelegate implements PickImageContract.View {
    public PickImageContract.View mView;
    public PickImageContract.View setView(PickImageContract.View mView){
        this.mView=mView;
        return mView;
    }
    @Override
    public void takeImageFromGallery() {
        if(mView!=null){
            mView.takeImageFromGallery();
        }
    }

    @Override
    public void takeImageFromCamera() {
        if(mView!=null){
            mView.takeImageFromCamera();
        }
    }

    @Override
    public void onImageUploadedSucessfully() {
        if(mView!=null){
            mView.onImageUploadedSucessfully();
        }
    }

    @Override
    public void onImageUploadedFailed() {
        if(mView!=null){
            mView.onImageUploadedFailed();
        }
    }
}
