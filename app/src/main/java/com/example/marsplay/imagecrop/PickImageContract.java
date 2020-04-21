package com.example.marsplay.imagecrop;

import android.net.Uri;

import com.example.marsplay.base.MvpPresenter;
import com.example.marsplay.base.MvpView;
import com.google.firebase.storage.StorageReference;

public interface PickImageContract
{
    interface View extends MvpView{
        void takeImageFromGallery();
        void takeImageFromCamera();
        void onImageUploadedSucessfully();
        void onImageUploadedFailed();

    }
    interface Presenter extends MvpPresenter<View>{
        void onImageFromGallery();
        void onImageFromCamera();
        void uploadImage(Uri path, StorageReference mStorageReference);

    }
}
