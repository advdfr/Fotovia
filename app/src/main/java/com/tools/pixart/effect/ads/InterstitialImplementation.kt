package com.tools.pixart.effect.ads

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.tools.pixart.R


object InterstitialImplementation {

    var considerIfGotRemoteValues = false

    @JvmField
    var mAdIsLoading: Boolean = false

    @JvmField
    var mInterstitialAd: InterstitialAd? = null

    @JvmStatic
    fun loadAdInterstitial(context: Context) {
        if (mInterstitialAd == null && !mAdIsLoading) {
            val adRequest = AdRequest.Builder().build()
            try {
                InterstitialAd.load(
                    context, context.getString(R.string.interstitial_id), adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d("ffnet", "Error: loading ${adError.responseInfo}")
                            mInterstitialAd = null
                            mAdIsLoading = false

//                        showAd()

                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                            log("Ad loaded.")
                            mInterstitialAd = interstitialAd
                            mAdIsLoading = true
                        }
                    }
                )
            } catch (e: Exception) {

            }
        }
    }


    @JvmStatic
    fun showInterstitialAd(activity: AppCompatActivity) {

        if (mInterstitialAd != null && mAdIsLoading) {

            mInterstitialAd?.show(activity)
            mInterstitialAd?.fullScreenContentCallback =

                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        mInterstitialAd = null
                        mAdIsLoading = false
                        loadAdInterstitial(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        Log.d("ffnet","Ad failed to show ${p0.message}")
                        mInterstitialAd = null
                        mAdIsLoading = false
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
//                        log("Ad showed fullscreen content.")

                    }
                }

        } else {
            loadAdInterstitial(activity)
        }


    }
}