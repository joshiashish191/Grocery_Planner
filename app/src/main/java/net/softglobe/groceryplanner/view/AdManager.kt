package net.softglobe.groceryplanner.view

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import net.softglobe.groceryplanner.R

class AdManager {
    companion object {
        private var instance: AdManager? = null
        fun getInstance(): AdManager? {
            if (instance == null) {
                instance = AdManager()
            }
            return instance
        }

        fun initializeAd(context: Context) {
            MobileAds.initialize(context) {}
        }

        var res : AdRequest? = null
        fun getAdRequest(): AdRequest {
            if (res == null)
                res = AdRequest.Builder().build()
            return res!!
        }

        private var rewardedAd: RewardedAd? = null

        fun loadRewardedAd(context: Context){
            RewardedAd.load(
                context,
                context.resources.getString(R.string.rewarded_ad_unit_ad),
                getAdRequest(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                    }
                })
        }

        fun getRewardedAd() : RewardedAd? {
            return rewardedAd
        }
    }
}