package com.tools.pixart.effect.activity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.tools.pixart.effect.color_splash_tool.ColorSplashActivity.REQUEST_CODE_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.tools.pixart.BuildConfig;
import com.tools.pixart.R;
import com.tools.pixart.effect.ads.FullScreenAdManager;
import com.tools.pixart.effect.anim.SlideDownAnimation;
import com.tools.pixart.effect.blur_tool.BlurActivity;
import com.tools.pixart.effect.color_splash_tool.ColorSplashActivity;
import com.tools.pixart.effect.crop_img.newCrop.StoreManager;
import com.tools.pixart.effect.motion_tool.MotionEffectActivity;
import com.tools.pixart.effect.rate.AppRate;
import com.tools.pixart.effect.rate.OnClickButtonListener;
import com.tools.pixart.effect.support.Constants;
import com.tools.pixart.effect.support.MyExceptionHandlerPix;
import com.tools.pixart.effect.support.SupportedClass;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends BaseActivity {
    protected static final int MY_PERMISSIONS_REQUEST_CAMERA_STORAGE = 0x1;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    protected static final int REQUEST_CODE_CROPPING = 0x4;
    public static int screenHeight;
    public static int screenWidth;
    public Uri mSelectedImageUri;
    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    FEATURES selectedFeatures = FEATURES.PIX_LAB_EFFECT;
    ShapeableImageView lin_pix;
    ShapeableImageView lin_b_w_effect;
    ShapeableImageView lin_neon_effect;
    ShapeableImageView lin_wings_effect;
    ShapeableImageView lin_frame_effect;
    ShapeableImageView lin_blur_effect;
    ShapeableImageView lin_drip_effect;
    ShapeableImageView lin_my_photos;
    ShapeableImageView lin_motion_effect;
    View card_ai_magic;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(HomeActivity.this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
                return;
            ActivityResultLauncher<String> launcher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {

                        }
                    }
            );
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
//        RelativeLayout mAdView = findViewById(R.id.adView);
//        loadBannerAds(mAdView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels - Constants.dpToPx(this, 4);
        screenHeight = displayMetrics.heightPixels - Constants.dpToPx(this, 109);

        StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
        StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);
        lin_pix = findViewById(R.id.lin_pix);
        lin_b_w_effect = findViewById(R.id.lin_b_w_effect);
        lin_blur_effect = findViewById(R.id.lin_blur_effect);
        lin_neon_effect = findViewById(R.id.lin_neon_effect);
        lin_wings_effect = findViewById(R.id.lin_wings_effect);
        lin_frame_effect = findViewById(R.id.lin_frame_effect);
        lin_drip_effect = findViewById(R.id.lin_drip_effect);
        lin_my_photos = findViewById(R.id.lin_my_photos);
        lin_motion_effect = findViewById(R.id.lin_motion_effect);
        card_ai_magic = findViewById(R.id.card_ai_magic);
        //rating app
        showRateDialog();

        //click for all effect
        lin_pix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.PIX_LAB_EFFECT;

                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();

            }
        });
        lin_b_w_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.B_W_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();

            }
        });

        lin_blur_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.BLUR_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();

            }
        });

        lin_neon_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.NEON_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });
        lin_wings_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.WINGS_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });
        lin_frame_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.FRAME_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });

        lin_drip_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.DRIP_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });

        lin_my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.MY_PHOTOS;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });

        lin_motion_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.MOTION_EFFECT;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });

        card_ai_magic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                selectedFeatures = FEATURES.AI_MAGIC;
                if (isPermissionGranted())
                    takeAction();
                else
                    takePermission();
            }
        });

        findViewById(R.id.container_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

            }
        });
        findViewById(R.id.scroll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

            }
        });
        hamburgerSetup();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.linearLayout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void hamburgerSetup() {
        findViewById(R.id.hamburger_ic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.close_ic).setVisibility(View.VISIBLE);
                findViewById(R.id.hamburger_ic).setVisibility(View.INVISIBLE);

                SlideDownAnimation slideDownAnim = new SlideDownAnimation(findViewById(R.id.linearLayout));
                slideDownAnim.setDuration(500);
                view.startAnimation(slideDownAnim);

            }
        });

        findViewById(R.id.close_ic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.close_ic).setVisibility(View.INVISIBLE);
                findViewById(R.id.hamburger_ic).setVisibility(View.VISIBLE);
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);


            }
        });

        findViewById(R.id.rate_us_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                }
            }
        });
        findViewById(R.id.share_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubject = "Check out this app!";
                    String shareText = "I'm using this awesome app and thought you might like it too!\n\n" +
                            "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                } catch (Exception e) {

                }
            }
        });
        findViewById(R.id.privacy_policy_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);

                Uri uri = Uri.parse("https://sites.google.com/view/fgtfhfjik/%D8%A7%D9%84%D8%B5%D9%81%D8%AD%D8%A9-%D8%A7%D9%84%D8%B1%D8%A6%D9%8A%D8%B3%D9%8A%D8%A9");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public boolean isPermissionGranted() {
        //checking permission

        int cameraePermission = ContextCompat.checkSelfPermission(HomeActivity.this, CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return cameraePermission == PackageManager.PERMISSION_GRANTED;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //For Android 11
                return cameraePermission == PackageManager.PERMISSION_GRANTED;

            } else {
                // For 10 or Below
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(HomeActivity.this, WRITE_EXTERNAL_STORAGE);

                return cameraePermission == PackageManager.PERMISSION_GRANTED &&
                        writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    private void takePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA_STORAGE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA_STORAGE);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_STORAGE) {
                boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (camera) {
                        takeAction();
                    } else {
                        Log.e("PermissionsResult", "Permission Denied 10 or Higher!!");
                    }
                } else {
                    boolean writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera && writeExternalStorage) {
                        takeAction();
                    } else {
                        Log.e("PermissionsResult", "Permission Denied 9 or below!!");
                    }

                }
            }
        }
    }


    public void takeAction() {
        if (selectedFeatures == FEATURES.PIX_LAB_EFFECT || selectedFeatures == FEATURES.AI_MAGIC) {
            pixDialog();

        } else if (selectedFeatures == FEATURES.B_W_EFFECT) {
            Intent intent = new Intent(HomeActivity.this, ColorSplashActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (selectedFeatures == FEATURES.BLUR_EFFECT) {
            Intent intent = new Intent(HomeActivity.this, BlurActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (selectedFeatures == FEATURES.NEON_EFFECT) {
            pixDialog();

        } else if (selectedFeatures == FEATURES.WINGS_EFFECT) {
            pixDialog();

        } else if (selectedFeatures == FEATURES.FRAME_EFFECT) {
            pixDialog();

        } else if (selectedFeatures == FEATURES.DRIP_EFFECT) {
            pixDialog();

        } else if (selectedFeatures == FEATURES.MY_PHOTOS) {
            openMyCreatePhotos();
        } else if (selectedFeatures == FEATURES.MOTION_EFFECT) {
            pixDialog();
        }
    }

    public void pixDialog() {
        final Dialog pixDialog = new Dialog(this);
        pixDialog.setContentView(R.layout.dialog_select_photo);
        pixDialog.setCancelable(false);
        pixDialog.setCanceledOnTouchOutside(false);
        Window window = pixDialog.getWindow();
        window.setLayout(((SupportedClass.getWidth(HomeActivity.this) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        pixDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout camera_item = pixDialog.findViewById(R.id.camera_item);
        LinearLayout gallery_item = pixDialog.findViewById(R.id.gallery_item);
        ImageView btnDismiss = pixDialog.findViewById(R.id.cancel);
        gallery_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.txt_select_picture)), REQUEST_CODE_GALLERY);
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
            }
        });
        camera_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
            }
        });
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pixDialog.dismiss();
            }
        });

        pixDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            mSelectedImagePath = mSelectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                File fileImageClick = new File(mSelectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mSelectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        mSelectedImageUri = FileProvider.getUriForFile(HomeActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    onPhotoTakenApp();
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(HomeActivity.this, mSelectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectedImagePath = mSelectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                onPhotoTakenApp();
            }

        } else if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_CROPPING && (selectedFeatures == FEATURES.PIX_LAB_EFFECT || selectedFeatures == FEATURES.AI_MAGIC)) {
            if (data.hasExtra("croppedUri")) {
                mSelectedImageUri = data.getParcelableExtra("croppedUri");
                if (mSelectedImageUri != null) {
                    FullScreenAdManager.fullScreenAdsCheckPref(HomeActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_FIRST_PIX_SCREEN, new FullScreenAdManager.GetBackPointer() {
                        @Override
                        public void returnAction() {
                            Intent intent = new Intent(HomeActivity.this, PixLabActivity.class);
                            intent.putExtra(Constants.KEY_FROM_MAIN, getString(R.string.txt_gallery));
                            intent.putExtra(PixLabActivity.EXTRA_TEMP_IMAGE_URI, mSelectedImageUri.toString());
                            if (selectedFeatures == FEATURES.AI_MAGIC) {
                                intent.putExtra("startGemini", true);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    });
                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_CROPPING && selectedFeatures == FEATURES.DRIP_EFFECT) {
            try {
                mSelectedImageUri = data.getParcelableExtra("croppedUri");
                StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
                StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);

                Bitmap bitmap = Constants.getBitmapFromUriDrip(HomeActivity.this, mSelectedImageUri, (float) screenWidth, (float) screenHeight);
                DripEffectActivity.setFaceBitmap(bitmap);
                StoreManager.setCurrentOriginalBitmap(this, bitmap);

                startActivity(new Intent(HomeActivity.this, DripEffectActivity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_CROPPING && selectedFeatures == FEATURES.MOTION_EFFECT) {
            try {
                mSelectedImageUri = data.getParcelableExtra("croppedUri");
                StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
                StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);

                Bitmap bitmap = Constants.getBitmapFromUriDrip(HomeActivity.this, mSelectedImageUri, (float) screenWidth, (float) screenHeight);
                MotionEffectActivity.setFaceBitmap(bitmap);
                StoreManager.setCurrentOriginalBitmap(this, bitmap);

                startActivity(new Intent(HomeActivity.this, MotionEffectActivity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "");
        }
    }

    public void onPhotoTakenApp() {
        if (selectedFeatures == FEATURES.PIX_LAB_EFFECT || selectedFeatures == FEATURES.DRIP_EFFECT || selectedFeatures == FEATURES.MOTION_EFFECT || selectedFeatures == FEATURES.AI_MAGIC) {
            Intent intent = new Intent(HomeActivity.this, CropPhotoActivity.class);
            intent.putExtra("cropUri", mSelectedImageUri.toString());
            startActivityForResult(intent, REQUEST_CODE_CROPPING);

        } else if (selectedFeatures == FEATURES.NEON_EFFECT) {
            try {
                StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
                StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);

                Bitmap bitmap = Constants.getBitmapFromUri(HomeActivity.this, mSelectedImageUri, (float) screenWidth, (float) screenHeight);
                NeonActivity.setFaceBitmap(bitmap);
                StoreManager.setCurrentOriginalBitmap(this, bitmap);

                startActivity(new Intent(this, NeonActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (selectedFeatures == FEATURES.WINGS_EFFECT) {
            try {
                StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
                StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);

                Bitmap bitmap = Constants.getBitmapFromUri(HomeActivity.this, mSelectedImageUri, (float) screenWidth, (float) screenHeight);
                WingsActivity.setFaceBitmap(bitmap);
                StoreManager.setCurrentOriginalBitmap(this, bitmap);

                startActivity(new Intent(this, WingsActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (selectedFeatures == FEATURES.FRAME_EFFECT) {
            try {
                StoreManager.setCurrentCropedBitmap(HomeActivity.this, (Bitmap) null);
                StoreManager.setCurrentCroppedMaskBitmap(HomeActivity.this, (Bitmap) null);

                Bitmap bitmap = Constants.getBitmapFromUri(HomeActivity.this, mSelectedImageUri, (float) screenWidth, (float) screenHeight);
                FramesActivity.setFaceBitmap(bitmap);
                StoreManager.setCurrentOriginalBitmap(this, bitmap);

                startActivity(new Intent(this, FramesActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void forAds(String packageString, String path) {
        boolean isAppInstalled = appInstalledOrNot(packageString);

        if (isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            Intent LaunchIntent = getPackageManager()
                    .getLaunchIntentForPackage(packageString);
            startActivity(LaunchIntent);
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            openPlaystoreApps(path);
        }
    }

    protected void openMyCreatePhotos() {
        startActivity(new Intent(this, MyCreatePhotosActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return false;
    }

    private File createImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + BuildConfig.APPLICATION_ID + "/CamPic/");
        storageDir.mkdirs();
        File image = null;
        try {
            image = new File(storageDir, getString(R.string.app_folder3));
            if (image.exists())
                image.delete();
            image.createNewFile();

            mSelectedOutputPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    public void showRateDialog() {
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(false) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(HomeActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

    }


    enum FEATURES {
        //effect wise flag
        PIX_LAB_EFFECT,
        B_W_EFFECT,
        BLUR_EFFECT,
        NEON_EFFECT,
        WINGS_EFFECT,
        FRAME_EFFECT,
        DRIP_EFFECT,
        MY_PHOTOS,
        MOTION_EFFECT,
        AI_MAGIC
    }
}
