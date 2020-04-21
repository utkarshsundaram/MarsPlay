package com.example.marsplay.imagecrop;

import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.marsplay.model.ImageUploadedModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PickImagePresenter implements PickImageContract.Presenter {
  public PickImageViewDelegate pickImageViewDelegate;
  public static final String TAG=PickImagePresenter.class.getName();
    private DatabaseReference mDatabase;

    public PickImagePresenter(){
        pickImageViewDelegate=new PickImageViewDelegate();
    }
    @Override
    public void onImageFromGallery() {
        pickImageViewDelegate.takeImageFromGallery();
    }

    @Override
    public void onImageFromCamera() {
        pickImageViewDelegate.takeImageFromCamera();
    }

    @Override
    public void uploadImage(Uri path, StorageReference mStorageReference) {
        String uploadedTimeStamp=String.valueOf(System.currentTimeMillis());
        StorageReference riversRef = mStorageReference.child("images/"+uploadedTimeStamp);

        riversRef.putFile(path)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        if (url == null) {
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                            downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    saveToDataBase(imageUrl,uploadedTimeStamp);

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            pickImageViewDelegate.onImageUploadedFailed();

                                        }
                                    });
                        }else{
                            saveToDataBase(url,uploadedTimeStamp);

                        }
                    }
                });
    }

    private void saveToDataBase(String url,String  uploadedTimeStamp) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ImageUploadedModel imageUploadedModel =new ImageUploadedModel();
        imageUploadedModel.setName(uploadedTimeStamp);
        imageUploadedModel.setFilePath(url);
        mDatabase.child("users").child(uploadedTimeStamp).setValue(imageUploadedModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pickImageViewDelegate.onImageUploadedSucessfully();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pickImageViewDelegate.onImageUploadedFailed();

                    }
                });

    }
    @Override
    public void bindView(PickImageContract.View view) {
     if(pickImageViewDelegate!=null){
         pickImageViewDelegate.setView(view);
     }
    }

    @Override
    public void unbindView() {
        if(pickImageViewDelegate!=null){
            pickImageViewDelegate.setView(null);
        }
    }
}
