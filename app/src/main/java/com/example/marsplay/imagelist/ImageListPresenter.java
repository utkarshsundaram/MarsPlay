package com.example.marsplay.imagelist;

import android.Manifest;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.marsplay.model.ImageUploadedModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageListPresenter implements ImageListContract.Presenter {
    private static final String TAG = ImageListPresenter.class.getName();
    public ImageListViewDelegate mImageListViewDelegate;
   public ImageListPresenter (){
       mImageListViewDelegate=new ImageListViewDelegate();

   }
    @Override
    public void askForPermission() {
      mImageListViewDelegate.shouldAskPermisssion();
    }

    @Override
    public void getDataFromServer(StorageReference storageReference) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectAllData((Map<String,ImageUploadedModel>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mImageListViewDelegate.errorImageList();
                    }
                });
    }
    private void collectAllData(Map<String,ImageUploadedModel> users) {

        ArrayList<ImageUploadedModel> models = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, ImageUploadedModel> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            ImageUploadedModel imageUploadedModel =new ImageUploadedModel();
            imageUploadedModel.setFilePath(String.valueOf(singleUser.get("filePath")));
            imageUploadedModel.setName(String.valueOf(singleUser.get("name")));
            models.add(imageUploadedModel);
            //Get phone field and append to list
//            models.add((ImageUploadedModel)singleUser.get("name"));
//            models.add((ImageUploadedModel)singleUser.get("FilePath"));
            //models.add((Long) singleUser.get("phone"));
        }
        mImageListViewDelegate.showImageList(models);
        Log.d(TAG,models.get(0).getFilePath());
    }
    @Override
    public void bindView(ImageListContract.View view) {
     if(mImageListViewDelegate!=null){
         mImageListViewDelegate.setView(view);
     }
    }

    @Override
    public void unbindView() {
        if(mImageListViewDelegate!=null){
            mImageListViewDelegate.setView(null);
        }
    }
}
