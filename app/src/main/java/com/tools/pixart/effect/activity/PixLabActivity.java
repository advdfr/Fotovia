package com.tools.pixart.effect.activity;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.*;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.tools.pixart.R;
import com.tools.pixart.effect.add_text.adapter.ColorPickerAdapter;
import com.tools.pixart.effect.custom.stickerView.DrawableSticker;
import com.tools.pixart.effect.custom.stickerView.Sticker;
import com.tools.pixart.effect.custom.stickerView.StickerView;
import com.tools.pixart.effect.adapter.FiltersForegroundAdapter;
import com.tools.pixart.effect.adapter.StickerCategoryListAdapter;
import com.tools.pixart.effect.adapter.StyleAdapter;
import com.tools.pixart.effect.ads.FullScreenAdManager;
import com.tools.pixart.effect.custom.CustomTextView;
import com.tools.pixart.effect.custom.MultiTouchListener;

import com.tools.pixart.effect.callBack.PIXStyleClickListener;
import com.tools.pixart.effect.callBack.StickerClickListener;
import com.tools.pixart.effect.model.PathModelPix;
import com.tools.pixart.effect.support.Constants;
import com.tools.pixart.effect.support.MyExceptionHandlerPix;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PixLabActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_TEMP_IMAGE_URI = "pixlab_temp_image_uri";

    public ArrayList<PathModelPix> arrIcon, arrMask;
    public Animation slideUpAnimation, slideDownAnimation;
    private RecyclerView mRecyclerPIXStyle, mRecyclerForegroundFilter, recyclerStickerCategory, recyclerSticker;
    private String oldSavedFileName;
    private Uri savedImageUri;
    private static Bitmap bmpPic = null;
    private Bitmap filteredForegroundBitmap3;
    private int pos = 0, lastSelectedPosTab = 0, displayWidth;
    private FiltersForegroundAdapter filtersForegroundAdapter;
    private ImageView mMovImage, mMainFrame;
    private StickerView stickerView;
    private LinearLayout linThirdDivisionOption;
    private Sticker currentSticker;
    private RelativeLayout rel_pix;

    private TabLayout tabLayout;

    private EditText addTextEditText;
    private ImageView cancelText;
    private ImageView doneText;
    private TextView changeFontTv;
    private TextView changeFontTv2;
    private TextView changeFontTv3;
    private TextView changeFontTv4;
    private TextView changeFontTv5;
    private TextView changeFontTv6;
    private TextView changeFontTv7;
    private TextView changeFontTv8;
    private TextView changeFontTv9;
    private Typeface typef;
    private com.xiaopo.flying.sticker.StickerView textStickerView;

    private  BitmapStickerIcon deleteIcon;
    private  BitmapStickerIcon zoomIcon;
    private  BitmapStickerIcon flipIcon;
    private int mColorCode = 0;

    private RecyclerView addTextColorPickerRecyclerView;
    private ProgressDialog progressDialog;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    static public void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"}, null);
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        bmpPic = bitmap;
    }

    private Bitmap getInputBitmap() {
        String tempImageUri = getIntent().getStringExtra(EXTRA_TEMP_IMAGE_URI);
        if (tempImageUri != null && !tempImageUri.trim().isEmpty()) {
            try {
                Uri uri = Uri.parse(tempImageUri);
                Bitmap bitmap = Constants.getBitmapFromUriDrip(this, uri, 1080, 1080);
                if (bitmap != null) {
                    return bitmap;
                }
            } catch (Exception e) {
                Log.e("PixLabActivity", "Unable to load temp PixLab image", e);
            }
            return null;
        }
        return bmpPic;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pix_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(PixLabActivity.this));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;

        arrIcon = new ArrayList<>();
        arrMask = new ArrayList<>();

        mRecyclerPIXStyle = findViewById(R.id.recyclerPIXStyle);
        mRecyclerForegroundFilter = findViewById(R.id.recyclerForegroundFilter);
        recyclerStickerCategory = findViewById(R.id.recyclerStickerCategory);
        recyclerStickerCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabLayout = findViewById(R.id.tabs);
        stickerView = findViewById(R.id.sticker_view);
        mMovImage = findViewById(R.id.iv_mov);
        mMainFrame = findViewById(R.id.main_frame);
        recyclerSticker = findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        mMovImage.setOnTouchListener(new MultiTouchListener());
        linThirdDivisionOption = findViewById(R.id.linThirdDivisionOption);
        rel_pix = findViewById(R.id.rel_pix);
        linThirdDivisionOption.setVisibility(View.GONE);
        AppCompatImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        ImageView mIvSave = findViewById(R.id.iv_save);
        AppCompatImageView ivCheckMark = findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(this);

        initViews();
        // Create all option
        createTabIcons();
        tabLayout.getTabAt(0);

        initMainStickerViewMan();

        pos = getIntent().getIntExtra(Constants.KEY_SELECTED_PIX_STYLE, 0);
        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_FROM_MAIN)) {
            String fromMain = getIntent().getStringExtra(Constants.KEY_FROM_MAIN);
            if (fromMain != null && fromMain.equalsIgnoreCase(getString(R.string.txt_gallery))) {
                arrIcon = getIconAllFrames();
                arrMask = getMaskAll();
            }
        }
        int Measuredwidth = 0;
        Point size = new Point();
        WindowManager w = getWindowManager();

        w.getDefaultDisplay().getSize(size);
        Measuredwidth = size.x;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Measuredwidth, Measuredwidth);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        stickerView.setLayoutParams(layoutParams);
        rel_pix.setLayoutParams(layoutParams);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.txt_loading));
        progressDialog.setCancelable(false);

        bmpPic = getInputBitmap();

        if (bmpPic != null) {
            progressDialog.show();
            filteredForegroundBitmap3 = bmpPic.copy(Bitmap.Config.ARGB_8888, true);


            mRecyclerPIXStyle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerForegroundFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            
            executor.execute(() -> {
                try {
                    Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bmpPic, 128, 128);
                    prepareThumbnailBackground(thumbBitmap);
                } catch (Exception e) {
                   runOnUiThread(() -> {
                       if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                   });
                }

                runOnUiThread(() -> {
                    StyleAdapter pixStyleAdapter = new StyleAdapter(PixLabActivity.this, arrIcon, position -> {
                        if (pos != position) {
                            pos = position;
                            makePixLab(arrMask.get(pos));
                        }
                    });

                    mRecyclerPIXStyle.setAdapter(pixStyleAdapter);
                    pixStyleAdapter.setSelectedPos(pos);
                    mRecyclerPIXStyle.scrollToPosition(pos);

                    mIvSave.setOnClickListener(view -> saveImage());

                    (findViewById(R.id.iv_back)).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

                    // Sticker list create
                    setStickerImages(30);

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            if (tab.getPosition() == 0) {
                                lastSelectedPosTab = 0;
                                mRecyclerPIXStyle.setVisibility(View.VISIBLE);
                                mRecyclerForegroundFilter.setVisibility(View.GONE);
                                recyclerStickerCategory.setVisibility(View.GONE);
                            } else if (tab.getPosition() == 1) {
                                lastSelectedPosTab = 1;
                                mRecyclerPIXStyle.setVisibility(View.GONE);
                                mRecyclerForegroundFilter.setVisibility(View.VISIBLE);
                                recyclerStickerCategory.setVisibility(View.GONE);
                            } else if (tab.getPosition() == 2) {
                                lastSelectedPosTab = 2;
                                mRecyclerPIXStyle.setVisibility(View.GONE);
                                mRecyclerForegroundFilter.setVisibility(View.GONE);
                                recyclerStickerCategory.setVisibility(View.VISIBLE);
                            } else if (tab.getPosition() == 3) {
                                lastSelectedPosTab = 3;
                                ColorPicker();
                            } else if (tab.getPosition() == 4) {
                                lastSelectedPosTab = 4;
                                findViewById(R.id.top_layout).setVisibility(View.INVISIBLE);
                                addTextEditText.setHint("Type text here");
                                addTextEditText.setTextColor(Color.RED);
                                findViewById(R.id.text_dialog).setVisibility(View.VISIBLE);
                            } else if (tab.getPosition() == 5) {
                                lastSelectedPosTab = 5;
                                startGeminiAnalysis();
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {}

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {}
                    });

                    if (mRecyclerPIXStyle.getLayoutManager() != null) {
                        mRecyclerPIXStyle.getLayoutManager().scrollToPosition(pos);
                    }
                    makePixLab(arrMask.get(pos));

                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(filteredForegroundBitmap3, displayWidth, displayWidth, true);
                    mMovImage.setImageBitmap(createScaledBitmap);
                    
                    if (getIntent().getBooleanExtra("startGemini", false)) {
                        startGeminiAnalysis();
                    }
                });
            });

        } else {
            Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
            finish();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        showBanner();
    }

    public void setStickerImages(int size) {
        final ArrayList<Integer> stickerArrayList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Resources resources = getResources();
            stickerArrayList.add(resources.getIdentifier("sticker_n" + i, "drawable", getPackageName()));
        }
        recyclerStickerCategory = findViewById(R.id.recyclerStickerCategory);
        recyclerStickerCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerStickerCategory.setAdapter(new StickerCategoryListAdapter(this, stickerArrayList, (v, position) -> {
            Drawable drawable = ContextCompat.getDrawable(this, stickerArrayList.get(position));
            if (drawable != null) {
                stickerView.addSticker(new DrawableSticker(drawable));
            }
        }));

    }


    public void ColorPicker() {
        TabLayout.Tab tab = tabLayout.getTabAt(lastSelectedPosTab);
        if (tab != null) tab.select();

        ColorPickerDialogBuilder
                .with(PixLabActivity.this)
                .setTitle("Choose color")
                .initialColor(-726683729)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(getString(R.string.txt_ok), (dialog, selectedColor, allColors) -> mMainFrame.setColorFilter(selectedColor))
                .setNegativeButton(getString(R.string.txt_cancel), (dialog, which) -> {
                })
                .build()
                .show();
    }


    public void makePixLab(PathModelPix pathModel) {
        mMainFrame.setImageResource(pathModel.getPathInt());
        mMainFrame.setScaleX(1.1f);
        mMainFrame.setScaleY(1.1f);
    }

    private void initMainStickerViewMan() {
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                removeStickerWithDeleteIcon();
                Log.e("TAG", "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull final Sticker sticker) {
                stickerOptionTaskPerformMan(sticker);
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.e("TAG", "onDoubleTapped: double tap will be with two click");
            }
        });
    }

    public void stickerOptionTaskPerformMan(Sticker sticker) {
        stickerView.setLocked(false);
        currentSticker = sticker;
        stickerView.sendToLayer(stickerView.getStickerPosition(currentSticker));
        Log.e("TAG", "onStickerTouchedDown");
    }

    private void removeStickerWithDeleteIcon() {
        stickerView.remove(currentSticker);
        currentSticker = null;
        if (stickerView.getStickerCount() != 0) {
            currentSticker = stickerView.getLastSticker();
        }
    }

    private void createTabIcons() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textOne = view.findViewById(R.id.text);
        ImageView ImageOne = view.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_frame));
        ImageOne.setImageResource(R.drawable.ic_module);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textTwo = view2.findViewById(R.id.text);
        ImageView ImageTwo = view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_filter));
        ImageTwo.setImageResource(R.drawable.ic_foreground);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView text3 = view3.findViewById(R.id.text);
        ImageView Image3 = view3.findViewById(R.id.image);
        text3.setText(getString(R.string.txt_stickers));
        Image3.setImageResource(R.drawable.ic_stickers);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView text4 = view4.findViewById(R.id.text);
        ImageView Image4 = view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_frame_color));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));

        View view7 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textfive = view7.findViewById(R.id.text);
        ImageView Image5 = view7.findViewById(R.id.image);
        textfive.setText(getString(R.string.add_txt));
        Image5.setImageResource(R.drawable.ic_neon_effect_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view7));

        View viewAI = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textAI = viewAI.findViewById(R.id.text);
        ImageView ImageAI = viewAI.findViewById(R.id.image);
        textAI.setText("AI Magic");
        ImageAI.setImageResource(R.drawable.ic_neon_effect_svg); // You can change this icon later
        tabLayout.addTab(tabLayout.newTab().setCustomView(viewAI));
    }

    public void prepareThumbnailBackground(final Bitmap thumbBitmaps) {
        executor.execute(() -> {
            try {
                List<Bitmap> previews = new ArrayList<>();
                List<String> names = new ArrayList<>();

                GPUImage previewEngine = new GPUImage(PixLabActivity.this);
                previewEngine.setImage(thumbBitmaps);

                previews.add(thumbBitmaps);
                names.add("Normal");

                previewEngine.setFilter(new GPUImageSepiaToneFilter());
                previews.add(previewEngine.getBitmapWithFilterApplied());
                names.add("Sepia");

                runOnUiThread(() -> {
                    filtersForegroundAdapter = new FiltersForegroundAdapter(previews, names, position -> {
                        applyGPUFilter(position);
                    });
                    mRecyclerForegroundFilter.setAdapter(filtersForegroundAdapter);
                });

            } catch (Exception e) {
                Log.e("PixLab", "Error processing thumbnails: " + e.getMessage());
            } finally {
                runOnUiThread(() -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void applyGPUFilter(int position) {
        GPUImage mainEngine = new GPUImage(this);
        mainEngine.setImage(bmpPic);
        if (position == 0) {
            mainEngine.setFilter(new GPUImageFilter());
        } else if (position == 1) {
            mainEngine.setFilter(new GPUImageSepiaToneFilter());
        }
        filteredForegroundBitmap3 = mainEngine.getBitmapWithFilterApplied();
        Bitmap scaled = Bitmap.createScaledBitmap(filteredForegroundBitmap3, displayWidth, displayWidth, true);
        mMovImage.setImageBitmap(scaled);
    }


    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        if (upAnimation) {
            linLayout.setVisibility(View.VISIBLE);
            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            linLayout.startAnimation(slideUpAnimation);
            recyclerView.scrollToPosition(0);
        } else {
            slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            linLayout.startAnimation(slideDownAnimation);
            slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    protected void onDestroy() {
        mMainFrame.setImageBitmap(null);
        mMovImage.setImageBitmap(null);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivCheckMark || id == R.id.ivClose) {
            if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                if (currentSticker == null)
                    currentSticker = stickerView.getCurrentSticker();

                if (recyclerSticker.getVisibility() == View.VISIBLE) {
                    itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                }
            }
        }
    }

    private void saveImage() {
        executor.execute(() -> {
            Bitmap bitmap = Bitmap.createBitmap(rel_pix.getWidth(), rel_pix.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rel_pix.draw(canvas);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            Exception error = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+ getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    if (uri != null) {
                        try (FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(uri)) {
                            if (fos != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            }
                        }
                        savedImageUri = uri;
                        notifyMediaScannerService(PixLabActivity.this, uri.getPath());
                    }

                } else {
                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File file = new File(myDir, fileName);
                    if (oldSavedFileName != null) {
                        File oldFile = new File(myDir, oldSavedFileName);
                        if (oldFile.exists()) oldFile.delete();
                    }
                    oldSavedFileName = fileName;

                    try (FileOutputStream out = new FileOutputStream(file)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                    }
                    Uri uri = addImageToGallery(file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(PixLabActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
                error = e;
            }

            Exception finalError = error;
            runOnUiThread(() -> {
                if (finalError == null) {
                    FullScreenAdManager.fullScreenAdsCheckPref(PixLabActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, this::openShareActivity);
                } else {
                    Toast.makeText(PixLabActivity.this, finalError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public Uri addImageToGallery(final String filePath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void openShareActivity() {
        Intent intent = new Intent(PixLabActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void initViews() {
        changeFontTv = findViewById(R.id.change_font_tv);
        changeFontTv2 = findViewById(R.id.change_font_tv2);
        changeFontTv3 = findViewById(R.id.change_font_tv3);
        changeFontTv4 = findViewById(R.id.change_font_tv4);
        changeFontTv5 = findViewById(R.id.change_font_tv5);
        changeFontTv6 = findViewById(R.id.change_font_tv6);
        changeFontTv7 = findViewById(R.id.change_font_tv7);
        changeFontTv8 = findViewById(R.id.change_font_tv8);
        changeFontTv9 = findViewById(R.id.change_font_tv9);
        addTextEditText = findViewById(R.id.add_text_edit_text);
        doneText = findViewById(R.id.add_text_done_tv);
        cancelText = findViewById(R.id.cancel_iv);
        textStickerView = findViewById(R.id.text_sticker_view);
        addTextColorPickerRecyclerView = findViewById(R.id.add_text_color_picker_recycler_view);

        fontChangeOnClickListener();
        colorAdapterRecyclerView();
        TextStickerSetup();
    }


    private void fontChangeOnClickListener() {

        changeFontTv.setOnClickListener(v -> setFonts("poppins_medium.ttf", "Poppins Medium"));

        changeFontTv2.setOnClickListener(v -> setFonts("poppins_semibold.ttf", "Poppins Semibold"));

        changeFontTv3.setOnClickListener(v -> setFonts("minya.ttf", "Minya"));

        changeFontTv4.setOnClickListener(v -> setFonts("karumbi_regular.ttf", "Karumbi Regular"));

        changeFontTv5.setOnClickListener(v -> setFonts("helvetica_bold.ttf", "Helvetica Bold"));

        changeFontTv6.setOnClickListener(v -> setFonts("pricedown.ttf", "Price Down"));

        changeFontTv7.setOnClickListener(v -> setFonts("roboto_bold.ttf", "Roboto Bold"));

        changeFontTv8.setOnClickListener(v -> setFonts("roboto_medium.ttf", "Roboto Medium"));

        changeFontTv9.setOnClickListener(v -> setFonts("roboto_regular.ttf", "Roboto Regular"));

        doneText.setOnClickListener(v -> {
            try {
                findViewById(R.id.top_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);

                String textSticker = addTextEditText.getText().toString();

                if (!textSticker.isEmpty()) {
                    TextSticker sticker = new TextSticker(PixLabActivity.this);
                    sticker.setText(textSticker);
                    if (mColorCode == 0) {
                        sticker.setTextColor(Color.BLACK);
                    }else {
                        sticker.setTextColor(mColorCode);
                    }
                    sticker.resizeText();
                    sticker.setTypeface(typef);
                    textStickerView.addSticker(sticker);
                } else {
                    findViewById(R.id.top_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                findViewById(R.id.top_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
            }
        });

        cancelText.setOnClickListener(v -> {
            findViewById(R.id.top_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
        });
    }
    private void setFonts(String s, String staticNameFonts) {
        typef = Typeface.createFromAsset(getAssets(), s);
        changeFontTv.setText(staticNameFonts);
        changeFontTv.setTypeface(typef);
        addTextEditText.setTypeface(typef);
    }

    private void colorAdapterRecyclerView() {

        // Setup the color picker for text color
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);

        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this);
        // This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            mColorCode = colorCode;
            addTextEditText.setTextColor(colorCode);
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
    }

    private void TextStickerSetup() {

        deleteIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.close_text_stickers
                ),
                BitmapStickerIcon.RIGHT_TOP
        );
        deleteIcon.setIconEvent(new DeleteIconEvent());
        zoomIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.scale_stickers
                ),
                BitmapStickerIcon.RIGHT_BOTOM
        );
        zoomIcon.setIconEvent(new ZoomIconEvent());
        flipIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(
                        this,
                        R.drawable.flip_stickers
                ),
                BitmapStickerIcon.LEFT_TOP
        );
        flipIcon.setIconEvent(new FlipHorizontallyEvent());
        textStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));

        textStickerView.setLocked(false);
        textStickerView.setConstrained(true);

        TextSticker sticker = new TextSticker(this);
        sticker.setDrawable(ContextCompat.getDrawable(this, R.drawable.transparent_bg_text_sticker));
        sticker.setText("Hello, world!");
        sticker.setTextColor(Color.BLACK);
        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();

        textStickerView.setOnClickListener(view -> {

            textStickerView.setLocked(false);
            textStickerView.setConstrained(true);
            findViewById(R.id.top_layout).setVisibility(View.INVISIBLE);

        });
        textStickerView.setOnStickerOperationListener(new com.xiaopo.flying.sticker.StickerView.OnStickerOperationListener() {

            @Override
            public void onStickerAdded(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

            }

            @Override
            public void onStickerClicked(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                textStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
                if (sticker instanceof TextSticker) {
                    findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
                    textStickerView.replace(sticker);
                }
            }

            @Override
            public void onStickerDeleted(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                textStickerView.setLocked(false);
                findViewById(R.id.top_layout).setVisibility(View.VISIBLE);

            }

            @Override
            public void onStickerDragFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

            }

            @Override
            public void onStickerTouchedDown(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                textStickerView.setLocked(true);
                findViewById(R.id.top_layout).setVisibility(View.VISIBLE);

            }
        });
    }
private void startGeminiAnalysis() {
        if (bmpPic == null) return;

        Toast.makeText(this, "Gemini AI is analyzing your photo...", Toast.LENGTH_SHORT).show();

        // 1. Initialize the Model (Replace YOUR_API_KEY with your key from AI Studio)
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDKUbfCw2Rs9-sA9jSaglKmaDfYz7sb5HM");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // 2. Prepare the AI request with the photo
        Content content = new Content.Builder()
                .addText("Analyze this photo. Give me one professional editing tip to make it look better and suggest 3 hashtags for Instagram.")
                .addImage(bmpPic)
                .build();

        // 3. Call the AI
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String advice = result.getText();
                runOnUiThread(() -> {
                    // Show the AI's response in a popup dialog
                    new androidx.appcompat.app.AlertDialog.Builder(PixLabActivity.this)
                            .setTitle("Gemini AI Photo Advice")
                            .setMessage(advice)
                            .setPositiveButton("Awesome", null)
                            .show();
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                runOnUiThread(() -> 
                    Toast.makeText(PixLabActivity.this, "AI Error: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }, ContextCompat.getMainExecutor(this));
    }


}
