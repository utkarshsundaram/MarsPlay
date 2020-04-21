package com.example.marsplay.imagecrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.marsplay.R;
import com.example.marsplay.base.BaseActivity;
import com.example.marsplay.utils.OtherUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static com.example.marsplay.utils.Constants.REQUEST_GALLERY_IMAGE;
import static com.example.marsplay.utils.Constants.REQUEST_IMAGE_CAPTURE;
import static com.example.marsplay.utils.OtherUtils.getCacheImagePath;

public class PickImageActivity extends BaseActivity<PickImageContract.View,PickImageContract.Presenter>implements PickImageContract.View, View.OnClickListener {
    private static final String TAG = PickImageActivity.class.getName();
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 80;
    public static String fileName;
    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private StorageReference mStorageRef;
    private Button mButtonChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mButtonChoose=findViewById(R.id.button_chose);
        mButtonChoose.setOnClickListener(this);
    }

    @Override
    protected PickImageContract.Presenter createMvpPresenter() {
        return new PickImagePresenter();
    }

    @Override
    protected PickImageContract.View getMvpView() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void takeImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName,this));
                } else {
                    setResultCancelled();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();
        }
    }
    @Override
    public void takeImageFromCamera() {
        fileName = System.currentTimeMillis() + ".jpg";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName,this));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onImageUploadedSucessfully() {
        OtherUtils.cancelProgressDialog();
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onImageUploadedFailed() {
        OtherUtils.cancelProgressDialog();
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void showImagePickerOptions(Context context) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.choose_image));

        // add a list
        String[] list = {context.getString(R.string.take_camera_picture), context.getString(R.string.choose_from_gallery)};
        builder.setItems(list, (dialog, which) -> {
            switch (which) {
                case 0:
                    mPresenter.onImageFromCamera();
                    break;
                case 1:
                    mPresenter.onImageFromGallery();
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), OtherUtils.queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        OtherUtils.showProgressDialog(this);
        mPresenter.uploadImage(imagePath,mStorageRef);
    }

    private void setResultCancelled() {
        OtherUtils.cancelProgressDialog();
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        showImagePickerOptions(this);
    }
}
