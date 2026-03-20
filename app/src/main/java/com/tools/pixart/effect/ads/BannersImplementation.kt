package com.tools.pixart.effect.ads

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.tools.pixart.R;


object BannersImplementation {


    @JvmStatic
    fun showBannerAd(
        activity: AppCompatActivity,
        adLoading: TextView,
        adContainer: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        adSize: Int
    ) {
        try {

            val adRequest: AdRequest = AdRequest.Builder().build()
            val adView = AdView(activity)
            when (adSize) {
                1 -> adView.setAdSize(getAdSize(activity))  // adaptive
                2 -> adView.setAdSize(AdSize.LARGE_BANNER) // large
                3 -> adView.setAdSize(AdSize.MEDIUM_RECTANGLE) // large
                else -> adView.setAdSize(AdSize.MEDIUM_RECTANGLE) // rectangular
            }

            adView.adUnitId = activity.getString(R.string.banner_id)

            adView.adListener = object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)

                    Log.d("banner", "Failed load banner:${loadAdError.message}")
//                   loadAndShowAppLovinBanner(activity, adLoading, adContainer, shimmerFrameLayout)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("banner", "loaded banner")

                    adContainer.removeAllViews()
                    adContainer.addView(adView)
                    shimmerFrameLayout.visibility = View.GONE
                    adLoading.visibility = View.GONE

                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }
            }
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getAdSize(context: Activity): AdSize {
        val display: Display = context.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

}