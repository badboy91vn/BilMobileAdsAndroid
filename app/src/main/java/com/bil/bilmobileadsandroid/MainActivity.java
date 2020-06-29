package com.bil.bilmobileadsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.bil.bilmobileads.ADBanner;
import com.bil.bilmobileads.ADInterstitial;
import com.bil.bilmobileads.ADRewarded;
import com.bil.bilmobileads.PBMobileAds;
import com.bil.bilmobileads.entity.BannerSize;
import com.bil.bilmobileads.interfaces.AdDelegate;

public class MainActivity extends AppCompatActivity {

    ADBanner adBanner;
    ADInterstitial adInterstitial;
    ADRewarded adRewarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PBMobileAds.getInstance().initialize(getApplicationContext(), "21258", true);

        FrameLayout bannerView = findViewById(R.id.bannerView);
        // init banner
        this.adBanner = new ADBanner(bannerView, "32588");
        this.adBanner.setAdSize(BannerSize.Banner300x250);
        this.adBanner.setAutoRefreshMillis(30000);
        this.adBanner.load();
        this.adBanner.setListener(new AdDelegate() {
            @Override
            public void onAdLoaded(String data) {
                PBMobileAds.getInstance().log(data);
            }

            @Override
            public void onAdImpression(String data) {
                PBMobileAds.getInstance().log(data);
            }

            @Override
            public void onAdClosed(String data) {
                PBMobileAds.getInstance().log(data);
            }

            @Override
            public void onAdFailedToLoad(String err) {
                PBMobileAds.getInstance().log(err);
            }

            @Override
            public void onAdLeftApplication(String data) {
                PBMobileAds.getInstance().log(data);
            }
        });

////         init full
//        this.adInterstitial = new ADInterstitial("32593");
//        this.adInterstitial.preLoad();
//        this.adInterstitial.setListener(new AdDelegate() {
//            @Override
//            public void onAdLoaded(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onAdImpression(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onAdClosed(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onAdFailedToLoad(String err) {
//                PBMobileAds.getInstance().log(err);
//            }
//
//            @Override
//            public void onAdLeftApplication(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//        });

//        this.adRewarded = new ADRewarded(this, "32594");
//        this.adRewarded.setListener(new AdRewardedDelegate() {
//            @Override
//            public void onRewardedAdOpened(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onRewardedAdClosed(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onUserEarnedReward(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//
//            @Override
//            public void onRewardedAdFailedToShow(String data) {
//                PBMobileAds.getInstance().log(data);
//            }
//        });
    }
}
