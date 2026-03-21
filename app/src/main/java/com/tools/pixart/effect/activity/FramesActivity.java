package com.tools.pixart.effect.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.tools.pixart.R;
import com.tools.pixart.effect.add_text.adapter.ColorPickerAdapter;
import com.tools.pixart.effect.custom.stickerView.DrawableSticker;
import com.tools.pixart.effect.custom.stickerView.Sticker;
import com.tools.pixart.effect.custom.stickerView.StickerView;
import com.tools.pixart.effect.custom.DHANVINE_MultiTouchListener;
import com.tools.pixart.effect.callBack.MenuItemClickLister;
import com.tools.pixart.effect.adapter.NeonEffectListAdapter;
import com.tools.pixart.effect.adapter.StickerAdapter;
import com.tools.pixart.effect.ads.FullScreenAdManager;
import com.tools.pixart.effect.custom.CustomTextView;

import com.tools.pixart.effect.erase_tool.StickerEraseActivity;
import com.tools.pixart.effect.callBack.StickerClickListener;
import com.tools.pixart.effect.crop_img.newCrop.MLCropAsyncTask;
import com.tools.pixart.effect.crop_img.newCrop.MLOnCropTaskCompleted;
import com.tools.pixart.effect.support.Constants;
import com.tools.pixart.effect.support.SupportedClass;
import com.tools.pixart.effect.support.FastBlur;
import com.tools.pixart.effect.support.ImageUtils;
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
import java.util.Objects;

import static com.tools.pixart.effect.activity.PixLabActivity.notifyMediaScannerService;


public class FramesActivity extends BaseActivity implements MenuItemClickLister, OnClickListener {

    public static ImageView setfront;
    public static Bitmap eraserResultBmp;
    private static Bitmap faceBitmap;
    public int mCount = 0;
    boolean isFirstTime = true;
    SeekBar sbBackgroundOpacity;
    private Bitmap selectedBit, cutBit;
    private Context mContext;

    private Animation slideUpAnimation, slideDownAnimation;
    private NeonEffectListAdapter neonEffectListAdapter;
    private int  shape = 18;
    private RecyclerView recyclerNeonEffect, recyclerSticker;
    private ArrayList<String> shapeEffect = new ArrayList<String>();
    private ImageView iv_face, setback, setimg;
    private RelativeLayout mContentRootView;
    private Uri savedImageUri;
    private StickerView stickerView;
    private Sticker currentSticker;
    private LinearLayout linThirdDivisionOption, linEffect, linBackgroundBlur;
    private TabLayout tabLayout;
    private String oldSavedFileName;


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

    private BitmapStickerIcon deleteIcon;
    private  BitmapStickerIcon zoomIcon;
    private  BitmapStickerIcon flipIcon;
    private int mColorCode = 0;
    private TextSticker selectedSticker;

    private RecyclerView addTextColorPickerRecyclerView;


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

        changeFontTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("poppins_medium.ttf", "Poppins Medium");
            }
        });

        changeFontTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("poppins_semibold.ttf", "Poppins Semibold");
            }
        });

        changeFontTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("minya.ttf", "Minya");
            }
        });

        changeFontTv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("karumbi_regular.ttf", "Karumbi Regular");
            }
        });

        changeFontTv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("helvetica_bold.ttf", "Helvetica Bold");
            }
        });

        changeFontTv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("pricedown.ttf", "Price Down");
            }
        });

        changeFontTv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("roboto_bold.ttf", "Roboto Bold");
            }
        });

        changeFontTv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("roboto_medium.ttf", "Roboto Medium");
            }
        });

        changeFontTv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFonts("roboto_regular.ttf", "Roboto Regular");
            }
        });

        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findViewById(R.id.lout_header).setVisibility(View.VISIBLE);
                    findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);

                    String textSticker = addTextEditText.getText().toString();

                    if (!textSticker.isEmpty()) {
                        TextSticker sticker = new TextSticker(FramesActivity.this);
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
                        findViewById(R.id.lout_header).setVisibility(View.VISIBLE);
                        findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    findViewById(R.id.lout_header).setVisibility(View.VISIBLE);
                    findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
                }
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.lout_header).setVisibility(View.VISIBLE);
                findViewById(R.id.text_dialog).setVisibility(View.INVISIBLE);
            }
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
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mColorCode = colorCode;
                addTextEditText.setTextColor(colorCode);
            }
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

        textStickerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                textStickerView.setLocked(false);
                textStickerView.setConstrained(true);
                findViewById(R.id.lout_header).setVisibility(View.INVISIBLE);

            }
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

                    selectedSticker = null;
                    selectedSticker = (TextSticker) sticker;
                    textStickerView.replace(sticker);
                }
            }

            @Override
            public void onStickerDeleted(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
                textStickerView.setLocked(false);
                findViewById(R.id.lout_header).setVisibility(View.VISIBLE);

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
                findViewById(R.id.lout_header).setVisibility(View.VISIBLE);

            }
        });
    }
    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wings_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(FramesActivity.this));
        mContext = this;

        selectedBit = faceBitmap;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                setimg.post(new Runnable() {
                    public void run() {
                        if (isFirstTime && selectedBit != null) {
                            isFirstTime = false;
                            initBMPNew();
                        }
                    }
                });
            }
        }, 1000);


        shapeEffect.add("none");

        for (int i = 1; i <= shape; i++) {
            shapeEffect.add("f_" + i);
        }
        //sticker list create
        setStickerImages(30);

        Init();
        setTollbarData();
        initViews();
        showBanner();
    }

    public void setStickerImages(int size) {
        final ArrayList<Integer> stickerArrayList = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            Resources resources = getResources();
            stickerArrayList.add(Integer.valueOf(resources.getIdentifier("sticker_n" + i, "drawable", getPackageName())));
        }
        recyclerSticker = findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerSticker.setAdapter(new StickerAdapter(this, stickerArrayList, new StickerClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                Drawable drawable = getResources().getDrawable(stickerArrayList.get(position));
                stickerView.addSticker(new DrawableSticker(drawable));
            }
        }));

    }


    private void initBMPNew() {
        if (faceBitmap != null) {
            selectedBit = ImageUtils.getBitmapResize(mContext, faceBitmap, setback.getWidth(), setback.getHeight());
            mContentRootView.setLayoutParams(new LinearLayout.LayoutParams(selectedBit.getWidth(), selectedBit.getHeight()));
            if (selectedBit != null && iv_face != null) {
                iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, sbBackgroundOpacity.getProgress() == 0 ? 1 : sbBackgroundOpacity.getProgress()));
            }
            cutmaskNew();
        }
    }

    private void Init() {
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        mContentRootView = findViewById(R.id.mContentRootView);
        setfront = findViewById(R.id.setfront);
        setback = findViewById(R.id.setback);
        iv_face = findViewById(R.id.iv_face);
        setimg = findViewById(R.id.setimg);

        iv_face.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickerView.getCurrentSticker() != null) {
                    stickerView.getCurrentSticker().release();
                }
            }
        });
        linEffect = (LinearLayout) findViewById(R.id.linEffect);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        createTabIcons();
        tabLayout.getTabAt(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }
        });

        recyclerNeonEffect = (RecyclerView) findViewById(R.id.recyclerNeonEffect);
        recyclerNeonEffect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        linBackgroundBlur = findViewById(R.id.linBackgroundBlur);
        setUpBottomList();

        AppCompatImageView ivCheckMark = (AppCompatImageView) findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(this);
        AppCompatImageView ivClose = (AppCompatImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        recyclerSticker = (RecyclerView) findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        linThirdDivisionOption = (LinearLayout) findViewById(R.id.linThirdDivisionOption);
        initMainStickerViewMan();


        tabLayout.setVisibility(View.VISIBLE);
        linEffect.setVisibility(View.GONE);
        linBackgroundBlur.setVisibility(View.GONE);



        neonEffectListAdapter.addData(shapeEffect);

        iv_face.setRotationY(0.0f);

        setimg.post(new Runnable() {
            public void run() {
                initBMPNew();
            }
        });

        SeekBar sbFrameOpacity = findViewById(R.id.sbFrameOpacity);
        sbFrameOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (setback != null && setfront != null) {
                    float f = ((float) i) * 0.01f;
                    setback.setAlpha(f);
                    setfront.setAlpha(f);
                }
            }
        });
        sbBackgroundOpacity = findViewById(R.id.sbBackgroundOpacity);
        sbBackgroundOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (selectedBit != null && iv_face != null) {
                    iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, seekBar.getProgress() == 0 ? 1 : seekBar.getProgress()));
                }
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }
        });
    }

    private void onBottomTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            viewSlideUpDown(linEffect, tabLayout);

        } else if (tab.getPosition() == 1) {
            itemSelectFromList(linThirdDivisionOption, recyclerSticker, true);

        } else if (tab.getPosition() == 2) {
            StickerEraseActivity.b = cutBit;
            Intent intent = new Intent(this, StickerEraseActivity.class);
            intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_FRAME);
            startActivityForResult(intent, 1024);

        } else if (tab.getPosition() == 3) {
            viewSlideUpDown(linBackgroundBlur, tabLayout);
        } else if (tab.getPosition() == 4) {
            findViewById(R.id.lout_header).setVisibility(View.INVISIBLE);
            addTextEditText.setHint("Type text here");
//            addTextEditText.setTextColor(Color.RED);
            findViewById(R.id.text_dialog).setVisibility(View.VISIBLE);
//            viewSlideUpDown(findViewById(R.id.text_dialog), tabLayout);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBit = eraserResultBmp;
                setimg.setImageBitmap(cutBit);
            }
        }
    }

    private void createTabIcons() {
        View vieww = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textOne = (CustomTextView) vieww.findViewById(R.id.text);
        ImageView ImageOne = (ImageView) vieww.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_effect));
        ImageOne.setImageResource(R.drawable.ic_neon_effect_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(vieww));

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text3 = (CustomTextView) view3.findViewById(R.id.text);
        ImageView Image3 = (ImageView) view3.findViewById(R.id.image);
        text3.setText(getString(R.string.txt_stickers));
        Image3.setImageResource(R.drawable.ic_stickers);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_erase));
        ImageTwo.setImageResource(R.drawable.ic_erase);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text4 = (CustomTextView) view4.findViewById(R.id.text);
        ImageView Image4 = (ImageView) view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_background));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));


        View view7 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textfive = (CustomTextView) view7.findViewById(R.id.text);
        ImageView Image5 = (ImageView) view7.findViewById(R.id.image);
        textfive.setText(getString(R.string.add_txt));
        Image5.setImageResource(R.drawable.ic_neon_effect_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view7));

    }

    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        //recyclerView.setVisibility(View.VISIBLE);
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
                    // recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    public void viewSlideUpDown(final View showLayout, final View hideLayout) {
        showLayout.setVisibility(View.VISIBLE);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        showLayout.startAnimation(slideUpAnimation);
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        hideLayout.startAnimation(slideDownAnimation);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout.setVisibility(View.GONE);
                // recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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

    @Override
    public void onBackPressed() {
        if (linEffect.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linEffect);
        } else if (linBackgroundBlur.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linBackgroundBlur);
        } else if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
            findViewById(R.id.ivClose).performClick();
        } else {
            showBackDialog();
        }
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
        if (stickerView.getStickerCount() == 0) {

        } else {
            currentSticker = stickerView.getLastSticker();
        }
    }

    public void setTollbarData() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getResources().getString(R.string.txt_frame));
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                showBackDialog();
            }
        });
        findViewById(R.id.tv_applay).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new saveImageTaskMaking().execute();
            }
        });
    }

    private void showBackDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_leave);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView button2 = (TextView) dialog.findViewById(R.id.btn_no);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    public void openShareActivity() {
        Intent intent = new Intent(FramesActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void setUpBottomList() {
        neonEffectListAdapter = new NeonEffectListAdapter(mContext);
        neonEffectListAdapter.setClickListener(this);
        recyclerNeonEffect.setAdapter(neonEffectListAdapter);
        neonEffectListAdapter.addData(shapeEffect);
    }

    public void onMenuListClick(View view, int i) {
        if (i != 0) {
            Bitmap backspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/back/" + neonEffectListAdapter.getItemList().get(i)
                    + "_back.png");
            Bitmap fronspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/front/" + neonEffectListAdapter.getItemList().get(i) + "_front.png");
            setback.setImageBitmap(backspiral);
            setfront.setImageBitmap(fronspiral);
        } else {
            setback.setImageResource(0);
            setfront.setImageResource(0);
        }
        setback.setOnTouchListener(new DHANVINE_MultiTouchListener(this, true));
    }

    public void cutmaskNew() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar2 = progressBar;
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
            }

            public void onTick(long j) {
                int unused = mCount = mCount + 1;
                if (progressBar2.getProgress() <= 90) {
                    progressBar2.setProgress(mCount * 5);
                }
            }
        }.start();

        new MLCropAsyncTask(new MLOnCropTaskCompleted() {
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int left, int top) {
                int[] iArr = {0, 0, selectedBit.getWidth(), selectedBit.getHeight()};
                int width = selectedBit.getWidth();
                int height = selectedBit.getHeight();
                int i = width * height;
                selectedBit.getPixels(new int[i], 0, width, 0, 0, width, height);
                int[] iArr2 = new int[i];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height);
                cutBit = ImageUtils.getMask(mContext, selectedBit, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBit.getWidth(), cutBit.getHeight(), false);
                cutBit = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBit).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(FramesActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        setimg.setImageBitmap(cutBit);
                    }
                });


            }
        }, this, progressBar).execute(new Void[0]);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
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


    private class saveImageTaskMaking extends android.os.AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stickerView.setLocked(true);
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }


        @Override
        protected Exception doInBackground(String... strings) {
            mContentRootView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = mContentRootView.getDrawingCache();
            Bitmap bitmap = getBitmapFromView(mContentRootView);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    try (java.io.OutputStream fos = Objects.requireNonNull(contentResolver.openOutputStream(Objects.requireNonNull(uri)))) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    }
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(FramesActivity.this, uri.getPath());

                } else {
                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                    if (!myDir.exists())
                        myDir.mkdirs();
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
                    Uri uri = SupportedClass.addImageToGallery(FramesActivity.this, file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(FramesActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
//            Exception e = new UnsupportedOperationException();
                return e;
            } finally {
                mContentRootView.setDrawingCacheEnabled(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {


                FullScreenAdManager.fullScreenAdsCheckPref(FramesActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        openShareActivity();
                    }
                });


            } else {
                Toast.makeText(FramesActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
