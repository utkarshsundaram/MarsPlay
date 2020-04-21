package com.example.marsplay.imagelist;

import com.example.marsplay.base.MvpPresenter;
import com.example.marsplay.base.MvpView;
import com.example.marsplay.model.ImageUploadedModel;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public interface ImageListContract
{
    interface View extends MvpView{
        void showImageList(List<ImageUploadedModel> storageReferences);
        void shouldAskPermisssion();
        void errorImageList();
    }

    interface Presenter extends MvpPresenter<View>{
        void askForPermission();
        void getDataFromServer(StorageReference storageReference);
    }
}
