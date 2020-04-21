package com.example.marsplay.imagelist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.marsplay.R;
import com.example.marsplay.adapter.ImageListAdapter;
import com.example.marsplay.base.BaseActivity;
import com.example.marsplay.imagecrop.PickImageActivity;
import com.example.marsplay.imagelist.ImageListContract;
import com.example.marsplay.imagelist.ImageListPresenter;
import com.example.marsplay.model.ImageUploadedModel;
import com.example.marsplay.utils.Constants;
import com.example.marsplay.utils.OtherUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class MainActivity extends BaseActivity<ImageListContract.View,ImageListContract.Presenter> implements ImageListContract.View, View.OnClickListener {
    private StorageReference mStorageRef;
    private Button mButton;
    private RecyclerView mRecyclerView;
    public ImageListAdapter imageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton=findViewById(R.id.button);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mButton.setOnClickListener(this);
        mRecyclerView= findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onResume() {
        super.onResume();
        OtherUtils.showProgressDialog(this);
        mPresenter.getDataFromServer(mStorageRef);

    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.need_permission));
        builder.setMessage(getResources().getString(R.string.needs_permission_for_feature));
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected ImageListContract.Presenter createMvpPresenter() {
        return new ImageListPresenter();
    }


    @Override
    protected ImageListContract.View getMvpView() {
        return this;
    }

    @Override
    public void showImageList(List<ImageUploadedModel>storageReferences) {
        OtherUtils.cancelProgressDialog();
        imageListAdapter=new ImageListAdapter();
        imageListAdapter.setmStorageReference(storageReferences);
        mRecyclerView.setAdapter(imageListAdapter);
        imageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void shouldAskPermisssion() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                              Intent intent = new Intent(MainActivity.this, PickImageActivity.class);
                              startActivityForResult(intent, Constants.REQUEST_IMAGE);
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void errorImageList() {
        Toast.makeText(this,"couldn't load images form server",Toast.LENGTH_LONG).show();
        OtherUtils.cancelProgressDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.getDataFromServer(mStorageRef);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mPresenter.askForPermission();
    }
}
