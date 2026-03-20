package com.tools.pixart.effect.activity.crop_fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tools.pixart.BuildConfig;
import com.tools.pixart.R;

import com.tools.pixart.effect.activity.CropPhotoActivity;
import com.tools.pixart.effect.crop_img.CropImageView;
import com.tools.pixart.effect.crop_img.callback.CropCallbackElegantPhoto;
import com.tools.pixart.effect.crop_img.callback.LoadCallbackElegantPhoto;
import com.tools.pixart.effect.crop_img.callback.SaveCallbackElegantPhoto;
import com.tools.pixart.effect.crop_img.utilElegantPhoto.Utils;

import java.io.File;
import java.io.IOException;

public class CropImageFragmentPix extends Fragment {
    private final String TAG = "CropFragmentPix";

    private final int REQUEST_PICK_IMAGE = 10011;
    private final int REQUEST_SAF_PICK_IMAGE = 10012;
    private final String PROGRESS_DIALOG = "ProgressDialog";
    private final String KEY_FRAME_RECT = "FrameRect";
    private final String KEY_SOURCE_URI = "SourceUri";

    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;

    // Note: only the system can call this constructor by reflection.
    public CropImageFragmentPix() {
    }

    public static CropImageFragmentPix newInstance() {
        CropImageFragmentPix fragment = new CropImageFragmentPix();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bind Views
        bindViews(view);

        if (savedInstanceState != null) {
            // restore data
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }

        if (mSourceUri == null && getActivity() instanceof CropPhotoActivity) {
            mSourceUri = ((CropPhotoActivity) getActivity()).currentImgUri;
        }

        Log.d(TAG, "onViewCreated: sourceUri = " + mSourceUri);

        // load image
        if (mSourceUri != null) {
            mCropView.load(mSourceUri)
                    .initialFrameRect(mFrameRect)
                    .useThumbnail(true)
                    .execute(mLoadCallback);
        }

        mCropView.setCropMode(CropImageView.CropMode.SQUARE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        if (mCropView != null) {
            outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
            outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
        }
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////
    private void bindViews(View view) {
        mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonCancel).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);

    }

    public void cropImage() {
        Log.d(TAG, "cropImage() called");
        showProgress();
        // Use the loaded bitmap (crop(null)) instead of the original file (crop(mSourceUri))
        // to bypass potential high-resolution decoder hangs on some devices/images.
        mCropView.crop(mSourceUri).execute(mCropCallback);
    }

    public void showProgress() {
        try {
            FragmentManager fm = getParentFragmentManager();
            if (fm.findFragmentByTag(PROGRESS_DIALOG) == null) {
                ProgressDialogFragmentPix f = ProgressDialogFragmentPix.getInstance();
                f.show(fm, PROGRESS_DIALOG);
            }
        } catch (Exception e) {
            Log.e(TAG, "showProgress failed", e);
        }
    }

    public void dismissProgress() {
        try {
            if (!isAdded()) return;
            FragmentManager fm = getParentFragmentManager();
            DialogFragment f = (DialogFragment) fm.findFragmentByTag(PROGRESS_DIALOG);
            if (f != null) {
                f.dismissAllowingStateLoss();
            }
        } catch (Exception e) {
            Log.e(TAG, "dismissProgress failed", e);
        }
    }

    public Uri createSaveUri() {
        try {
            return createNewUri(getContext());
        } catch (Exception e) {
            Log.e(TAG, "createSaveUri failed", e);
            return null;
        }
    }

    public Uri createNewUri(Context context) {
        try {
            File cacheDir = new File(context.getCacheDir(), "crops");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File tempFile = new File(cacheDir, "cropped_" + System.currentTimeMillis() + ".jpg");
            if (tempFile.createNewFile()) {
                Log.d(TAG, "Temp file created: " + tempFile.getAbsolutePath());
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(tempFile);
            } else {
                return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", tempFile);
            }
        } catch (IOException e) {
            Log.e(TAG, "createNewUri failed", e);
            return null;
        }
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////
    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.buttonCancel) {
                if (getActivity() != null) {
                    ((CropPhotoActivity) getActivity()).cancelCropping();
                }
            } else if (id == R.id.buttonRotateLeft) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            } else if (id == R.id.buttonRotateRight) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            } else if (id == R.id.buttonDone) {
                cropImage();
            }
        }
    };

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////
    private final LoadCallbackElegantPhoto mLoadCallback = new LoadCallbackElegantPhoto() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "Image loaded successfully");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to load image", e);
        }
    };

    private final CropCallbackElegantPhoto mCropCallback = new CropCallbackElegantPhoto() {
        @Override
        public void onSuccess(Bitmap cropped) {
            Log.d(TAG, "Image cropped successfully, starting save...");
            Uri saveUri = createSaveUri();
            if (saveUri != null) {
                mCropView.save(cropped)
                        .compressFormat(mCompressFormat)
                        .compressQuality(90)
                        .execute(saveUri, mSaveCallback);
            } else {
                Log.e(TAG, "Failed to create save Uri, dismissing progress");
                dismissProgress();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Crop failed", e);
            dismissProgress();
        }
    };

    private final SaveCallbackElegantPhoto mSaveCallback = new SaveCallbackElegantPhoto() {
        @Override
        public void onSuccess(Uri outputUri) {
            Log.d(TAG, "Image saved successfully: " + outputUri);
            dismissProgress();
            if (getActivity() != null && !getActivity().isFinishing()) {
                ((CropPhotoActivity) getActivity()).startResultActivity(outputUri);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to save image", e);
            dismissProgress();
        }
    };
}
