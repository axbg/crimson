package com.axbg.crimson.utility;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.axbg.crimson.BuildConfig;
import com.axbg.crimson.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    public static void requestCameraPermissionLauncher(Fragment fragment, FragmentActivity fragmentActivity, Context context) {
        fragmentActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        try {
                            takePictureFromCamera(fragment, fragmentActivity, context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, R.string.CAMERA_PERMISSION_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }).launch(Manifest.permission.CAMERA);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void takePictureFromCamera(Fragment fragment, FragmentActivity fragmentActivity, Context context) throws IOException {
        File tempPicture = new File(context.getFilesDir(), "temp.png");

        if (!tempPicture.exists()) {
            tempPicture.createNewFile();
        }

        Uri picture = FileProvider.getUriForFile(fragmentActivity, BuildConfig.APPLICATION_ID.concat(".provider"), tempPicture);

        takePicture(fragment, fragmentActivity, context, picture);
    }

    public static void pickPictureFromGallery(Fragment fragment, FragmentActivity fragmentActivity, Context context) {
        fragmentActivity.registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> cropPicture(uri, context, fragment)).launch("image/*");
    }

    private static void takePicture(Fragment fragment, FragmentActivity fragmentActivity, Context context, Uri picture) {
        fragmentActivity.registerForActivityResult(new ActivityResultContracts.TakePicture(),
                saved -> {
                    if (saved) {
                        cropPicture(picture, context, fragment);
                    }
                }).launch(picture);
    }

    private static void cropPicture(Uri picture, Context context, Fragment fragment) {
        CropImage.activity(picture).start(context, fragment);
    }
}
