package com.example.videoplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.source.MediaSource;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private FrameLayout mMainFrame;
    private Button mBtnCamera, mBtnGallery;

    private static final String FRAGMENT_DIALOG = "dialog";
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final String TAG = "MainActivity1";
    private Object selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = findViewById(R.id.mainFrame);
        mBtnGallery = findViewById(R.id.btnGallery);
        mBtnCamera = findViewById(R.id.btnCamera);

        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                    videoPermission();
                    return;
                }
                getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, new Camera2VideoFragment()).commit();
            }
        });

        mBtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                    videoPermission();
                    return;
                }
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY_REQUEST_CODE);

            }
        });


    }
//////////////////////////////////////////////////////////////////////////////////////////////////////


    // To handle when an image is selected from the browser, add the following to your Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                Log.d(TAG, "onActivityResult: real   "+ RealPathUtil.getRealPathFromURI_API19(getApplicationContext(), data.getData()));
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
        }else {
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



    public  class ConfirmationDialog extends AlertDialog {

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


    public static class RealPathUtil {

        @SuppressLint("NewApi")
        public static String getRealPathFromURI_API19(Context context, Uri uri) {
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);
            Log.d(TAG, "getRealPathFromURI_API19: idx"+ columnIndex);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            Log.d(TAG, "getRealPathFromURI_API19: "+ filePath.isEmpty());
            return filePath;
        }
    }


}
