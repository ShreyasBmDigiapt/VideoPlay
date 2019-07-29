package com.example.videoplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.source.MediaSource;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageButton mBtnCamera, mBtnGallery;
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private LinearLayout mMainLL;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final String TAG = "MainActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mMainLL = findViewById(R.id.mainLL);

        mBtnGallery = findViewById(R.id.btnGallery);
        mBtnCamera = findViewById(R.id.btnCamera);

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mMainLL.setOrientation(LinearLayout.HORIZONTAL);
//        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mMainLL.setOrientation(LinearLayout.VERTICAL);
//        }

        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                    videoPermission();
                    return;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mainFrame, new Camera2VideoFragment());
                transaction.addToBackStack("camera");
                transaction.commit();
            }
        });

        mBtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                    videoPermission();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);

            }
        });


    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
    // To handle when an image is selected from the browser, add the following to your Activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri galleyPathUri = data.getData();
            String galleryPath = getPath(galleyPathUri);
            if (galleryPath != null) {
                Intent galleryIntent = new Intent(MainActivity.this, ExoPlayer.class);
                galleryIntent.putExtra("path", galleryPath);
                startActivity(galleryIntent);
            }
        }
    }

    private String getPath(Uri galleyPathUri) {
        Cursor cursor = null;
        try {
            String[] projections = {MediaStore.Video.Media.DATA};
            cursor = getContentResolver().query(galleyPathUri, projections, null, null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // And to convert the image URI to the direct file system path of the image file
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Boolean shouldShowRequest(String[] permissions) {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    private void videoPermission() {
        if (shouldShowRequest(VIDEO_PERMISSIONS)) {
            new ConfirmationDialog(this).show();
        } else {
            requestPermissions(VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_VIDEO_PERMISSIONS && grantResults.length == VIDEO_PERMISSIONS.length) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    new ConfirmationDialog(this).show();
                    break;
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public class ConfirmationDialog extends AlertDialog {

        protected ConfirmationDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setMessage(R.string.permission_request).setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestPermissions(VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
                }
            }).setNegativeButton(android.R.string.no, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.show().create();
            super.onCreate(savedInstanceState);
        }
    }
}
