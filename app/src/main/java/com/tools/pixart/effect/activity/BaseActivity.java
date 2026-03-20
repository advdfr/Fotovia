package com.tools.pixart.effect.activity;

import static com.tools.pixart.effect.ads.BannersImplementation.showBannerAd;
import static com.tools.pixart.effect.ads.InterstitialImplementation.showInterstitialAd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.tools.pixart.R;
import com.tools.pixart.effect.model.PathModelPix;
import com.tools.pixart.effect.support.Constants;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    public LinearLayout adView;

    public void openPlaystoreApps(String applicationUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(applicationUrl));
        try {
            startActivity(intent);
        } catch (Exception e) {
            intent.setData(Uri.parse(applicationUrl));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FullScreenAdManager.initFullScreenAds(this);
    }


    public void showBanner() {
        try {
            showBannerAd(this, findViewById(R.id.loading_tv_),  findViewById(R.id.banner_container), findViewById(R.id.shimmer_view_container),1);
        } catch (Exception e) {
            Log.d("banner", "showBanner: "+e.toString());
//        throw new RuntimeException(e);
        }
    }
    public void showInterstitial(){
        try {
            showInterstitialAd(this);
        } catch (Exception e) {
//        throw new RuntimeException(e);
        }
    }

    public ArrayList<PathModelPix> getIconAllFrames() {
        ArrayList<PathModelPix> arrSticker = new ArrayList<>();
        for (int i = 1; i <= Constants.PIX_CATEGORY_SIZE; i++) {
            PathModelPix pathModel = new PathModelPix();
            pathModel.setPathInt(getResources().getIdentifier(Constants.PIX_ICON_FILE_NAME + i, "drawable", getPackageName()));
            arrSticker.add(pathModel);
        }
        return arrSticker;
    }

    public ArrayList<PathModelPix> getMaskAll() {
        ArrayList<PathModelPix> arrSticker = new ArrayList<>();
        for (int i = 1; i <= Constants.PIX_CATEGORY_SIZE; i++) {
            PathModelPix pathModel = new PathModelPix();
            pathModel.setPathInt(getResources().getIdentifier(Constants.PIX_MASK_FILE_NAME + i, "drawable", getPackageName()));
            arrSticker.add(pathModel);
        }
        return arrSticker;
    }


}
