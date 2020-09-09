package com.bil.bilmobileads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bil.bilmobileads.entity.ADFormat;
import com.bil.bilmobileads.entity.AdInfor;
import com.bil.bilmobileads.entity.AdUnitObj;
import com.bil.bilmobileads.entity.TimerRecall;
import com.bil.bilmobileads.interfaces.AdDelegate;
import com.bil.bilmobileads.interfaces.ResultCallback;
import com.bil.bilmobileads.interfaces.TimerCompleteListener;
import com.bil.bilmobileads.nativetemplates.TemplateView;
import com.consentmanager.sdk.CMPConsentTool;
import com.consentmanager.sdk.callbacks.OnCloseCallback;
import com.consentmanager.sdk.model.CMPConfig;
import com.consentmanager.sdk.storage.CMPStorageConsentManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.OnPublisherAdViewLoadedListener;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
//import com.google.android.gms.ads.formats.ADNativeView;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.prebid.mobile.AdUnit;
import org.prebid.mobile.NativeAdUnit;
import org.prebid.mobile.NativeDataAsset;
import org.prebid.mobile.NativeEventTracker;
import org.prebid.mobile.NativeImageAsset;
import org.prebid.mobile.NativeTitleAsset;
import org.prebid.mobile.OnCompleteListener;
import org.prebid.mobile.ResultCode;
import org.prebid.mobile.TargetingParams;

import java.util.ArrayList;

public class ADNative {

    // MARK: - View
    private TemplateView templateView;
    private FrameLayout adViewFrame;
    private AdDelegate adDelegate;

    // MARK: - AD
    private PublisherAdRequest amRequest;
    private NativeAdUnit adUnit;
    private PublisherAdView amNative;
    private AdLoader amNativeDFP;
    private UnifiedNativeAd unifiedNativeAdObj;
    // MARK: - AD Info
    private String placement;
    private AdUnitObj adUnitObj;

    // MARK: - Properties
    private ADFormat adFormatDefault;
    private int timeAutoRefresh = Constants.BANNER_AUTO_REFRESH_DEFAULT;
    private boolean isLoadNativeSucc = false; // true => banner đang chạy
    private boolean isRecallingPreload = false; // Check đang đợi gọi lại preload
    private TimerRecall timerRecall; // Recall load func AD

    public ADNative(FrameLayout adViewFrame, String placement) {
        if (adViewFrame == null || placement == null) {
            PBMobileAds.getInstance().log("FrameLayout and placement is not nullable");
            throw new NullPointerException();
        }
        PBMobileAds.getInstance().log("AD Native Init: " + placement);

        this.adViewFrame = adViewFrame;
        this.placement = placement;

        setupAdNative();
    }

    private ADNative(TemplateView templateView, String placement) {
        if (templateView == null || placement == null) {
            PBMobileAds.getInstance().log("TemplateView and placement is not nullable");
            throw new NullPointerException();
        }
        PBMobileAds.getInstance().log("AD Native Init: " + placement);

        this.templateView = templateView;
        this.placement = placement;

        setupAdNative();
    }

    private void setupAdNative() {
        this.timerRecall = new TimerRecall(Constants.RECALL_CONFIGID_SERVER, 1000, new TimerCompleteListener() {
            @Override
            public void doWork() {
                isRecallingPreload = false;
                load();
            }
        });

        // Get AdUnit
        if (this.adUnitObj == null) {
            this.adUnitObj = PBMobileAds.getInstance().getAdUnitObj(this.placement);
            if (this.adUnitObj == null) {
                PBMobileAds.getInstance().getADConfig(this.placement, new ResultCallback<AdUnitObj, Exception>() {
                    @Override
                    public void success(AdUnitObj data) {
                        adUnitObj = data;

                        final Context contextApp = PBMobileAds.getInstance().getContextApp();
                        if (PBMobileAds.getInstance().gdprConfirm && CMPConsentTool.needShowCMP(contextApp)) {
                            String appName = contextApp.getApplicationInfo().loadLabel(contextApp.getPackageManager()).toString();

                            CMPConfig cmpConfig = CMPConfig.createInstance(15029, "consentmanager.mgr.consensu.org", appName, "EN");
                            CMPConsentTool.createInstance(contextApp, cmpConfig, new OnCloseCallback() {
                                @Override
                                public void onWebViewClosed() {
                                    PBMobileAds.getInstance().log("ConsentString: " + CMPStorageConsentManager.getConsentString(contextApp));
                                    load();
                                }
                            });
                        } else {
                            load();
                        }
                    }

                    @Override
                    public void failure(Exception error) {
                        PBMobileAds.getInstance().log(error.getLocalizedMessage());
                    }
                });
            }
        }
    }

    // MARK: - Private FUNC
    void addFirstPartyData(AdUnit adUnit) {
        //        Access Control List
        //        Targeting.shared.addBidderToAccessControlList(Prebid.bidderNameAppNexus)

        //global user data
        TargetingParams.addUserData("globalUserDataKey1", "globalUserDataValue1");

        //global context data
        TargetingParams.addContextData("globalContextDataKey1", "globalContextDataValue1");

        //adunit context data
        adUnit.addContextData("adunitContextDataKey1", "adunitContextDataValue1");

        //global context keywords
        TargetingParams.addContextKeyword("globalContextKeywordValue1");
        TargetingParams.addContextKeyword("globalContextKeywordValue2");

        //global user keywords
        TargetingParams.addUserKeyword("globalUserKeywordValue1");
        TargetingParams.addUserKeyword("globalUserKeywordValue2");

        //adunit context keywords
        adUnit.addContextKeyword("adunitContextKeywordValue1");
        adUnit.addContextKeyword("adunitContextKeywordValue2");
    }

    void deplayCallPreload() {
        this.isRecallingPreload = true;
        this.timerRecall.start();
    }

    AdInfor getAdInfor(boolean isVideo) {
        for (AdInfor infor : this.adUnitObj.adInfor) {
            if (infor.isVideo == isVideo) {
                return infor;
            }
        }
        return null;
    }

    void resetAD() {
        if (this.adUnit == null || this.amNative == null) return;

        this.isRecallingPreload = false;
        this.isLoadNativeSucc = false;
        this.adViewFrame.removeAllViews();

        this.adUnit.stopAutoRefresh();
        this.adUnit = null;

        this.amNative.destroy();
        this.amNative.setAdListener(null);
        this.amNative = null;

        this.amNativeDFP = null;
    }

    void handerResult(ResultCode resultCode) {
        if (resultCode == ResultCode.SUCCESS) {
//            this.amNative.loadAd(this.amRequest);
            this.amNativeDFP.loadAd(this.amRequest);
        } else {
            if (resultCode == ResultCode.NO_BIDS) {
                this.deplayCallPreload();
            } else if (resultCode == ResultCode.TIMEOUT) {
                this.deplayCallPreload();
            }
        }
    }

    void setupNativeAsset() {
        // Add event trackers requirements, this is required
        ArrayList<NativeEventTracker.EVENT_TRACKING_METHOD> methods = new ArrayList<>();
        methods.add(NativeEventTracker.EVENT_TRACKING_METHOD.IMAGE);
        methods.add(NativeEventTracker.EVENT_TRACKING_METHOD.JS);
        try {
            NativeEventTracker tracker = new NativeEventTracker(NativeEventTracker.EVENT_TYPE.IMPRESSION, methods);
            this.adUnit.addEventTracker(tracker);
        } catch (Exception e) {
            e.printStackTrace();
            PBMobileAds.getInstance().log(e.getLocalizedMessage());
        }
        // Require a title asset
        NativeTitleAsset title = new NativeTitleAsset();
        title.setLength(90);
        title.setRequired(true);
        this.adUnit.addAsset(title);
        // Require an icon asset
        NativeImageAsset icon = new NativeImageAsset();
        icon.setImageType(NativeImageAsset.IMAGE_TYPE.ICON);
        icon.setWMin(20);
        icon.setHMin(20);
        icon.setRequired(true);
        this.adUnit.addAsset(icon);
        // Require an main image asset
        NativeImageAsset image = new NativeImageAsset();
        image.setImageType(NativeImageAsset.IMAGE_TYPE.MAIN);
        image.setHMin(200);
        image.setWMin(200);
        image.setRequired(true);
        this.adUnit.addAsset(image);
        // Require sponsored text
        NativeDataAsset data = new NativeDataAsset();
        data.setLen(90);
        data.setDataType(NativeDataAsset.DATA_TYPE.SPONSORED);
        data.setRequired(true);
        this.adUnit.addAsset(data);
        // Require main description
        NativeDataAsset body = new NativeDataAsset();
        body.setRequired(true);
        body.setDataType(NativeDataAsset.DATA_TYPE.DESC);
        this.adUnit.addAsset(body);
        // Require call to action
        NativeDataAsset cta = new NativeDataAsset();
        cta.setRequired(true);
        cta.setDataType(NativeDataAsset.DATA_TYPE.CTATEXT);
        this.adUnit.addAsset(cta);
    }

    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private void populateADNativeView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }

    // MARK: - Public FUNC
    public boolean load() {
        PBMobileAds.getInstance().log("ADNative Placement '" + this.placement + "' - isLoaded: " + this.isLoaded() + " |  isRecallingPreload: " + this.isRecallingPreload);
        if (this.adUnitObj == null || this.isLoaded() == true || this.isRecallingPreload == true) {
            return false;
        }
        this.resetAD();

        // Check Active
        if (!this.adUnitObj.isActive || this.adUnitObj.adInfor.size() <= 0) {
            PBMobileAds.getInstance().log("AdNative Placement '" + this.placement + "' is not active or not exist");
            return false;
        }

        // Check and set default
        this.adFormatDefault = this.adUnitObj.defaultFormat;
        // set adformat theo loại duy nhất có
        if (adUnitObj.adInfor.size() < 2) {
            this.adFormatDefault = this.adUnitObj.adInfor.get(0).isVideo ? ADFormat.VAST : ADFormat.HTML;
        }

        // Get AdInfor
        boolean isVideo = this.adFormatDefault == ADFormat.VAST;
        AdInfor adInfor = this.getAdInfor(isVideo);
        if (adInfor == null) {
            PBMobileAds.getInstance().log("AdInfor of ADNative Placement '" + this.placement + "' is not exist");
            return false;
        }

        PBMobileAds.getInstance().log("Load ADNative Placement: " + this.placement);
        // Set GDPR
        if (PBMobileAds.getInstance().gdprConfirm) {
            String consentStr = CMPStorageConsentManager.getConsentString(PBMobileAds.getInstance().getContextApp());
            if (consentStr != null && consentStr != "") {
                TargetingParams.setSubjectToGDPR(true);
                TargetingParams.setGDPRConsentString(CMPStorageConsentManager.getConsentString(PBMobileAds.getInstance().getContextApp()));
            }
        }

        // Setup Host
        PBMobileAds.getInstance().setupPBS(adInfor.host);
        PBMobileAds.getInstance().log("[ADNative] - configID: " + adInfor.configId + " | adUnitID: " + adInfor.adUnitID);

        // Create AdUnit
        this.adUnit = new NativeAdUnit(adInfor.configId);
        this.adUnit.setContextType(NativeAdUnit.CONTEXT_TYPE.SOCIAL_CENTRIC);
        this.adUnit.setPlacementType(NativeAdUnit.PLACEMENTTYPE.CONTENT_FEED);
        this.adUnit.setContextSubType(NativeAdUnit.CONTEXTSUBTYPE.GENERAL_SOCIAL);
        this.adUnit.setAutoRefreshPeriodMillis(this.timeAutoRefresh);

        setupNativeAsset();

        // Create AdView
        //        this.amNative = new PublisherAdView(PBMobileAds.getInstance().getContextApp());
        //        this.amNative.setAdUnitId(adInfor.adUnitID);
        //        this.amNative.setAdSizes(AdSize.FLUID);
        this.amNativeDFP = new AdLoader.Builder(PBMobileAds.getInstance().getContextApp(), adInfor.adUnitID)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        PBMobileAds.getInstance().log("onUnifiedNativeAdLoaded: ADNative Placement '" + placement + "'");

                        if (unifiedNativeAdObj != null) unifiedNativeAdObj.destroy();
                        unifiedNativeAdObj = unifiedNativeAd;

                        UnifiedNativeAdView ADNativeView = (UnifiedNativeAdView) LayoutInflater.from(PBMobileAds.getInstance().getContextApp()).inflate(
                                R.layout.adview_native_unified, null);
                        populateADNativeView(unifiedNativeAd, ADNativeView);

                        // Remove + Add Ad to view
                        adViewFrame.removeAllViews();
                        adViewFrame.addView(ADNativeView);

                        //                        ColorDrawable colorDrawable = new ColorDrawable();
                        //                        NativeTemplateStyle styles = new
                        //                                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                        //                        templateView.setNativeAd(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();

                        isLoadNativeSucc = true;
                        PBMobileAds.getInstance().log("onAdLoaded: ADNative Placement '" + placement + "'");
                        if (adDelegate == null) return;
                        adDelegate.onAdLoaded("onAdLoaded: ADNative Placement '" + placement + "'");
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();

                        PBMobileAds.getInstance().log("onAdOpened: ADNative Placement '" + placement + "'");
                        if (adDelegate == null) return;
                        adDelegate.onAdOpened("onAdOpened: ADNative Placement '" + placement + "'");
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();

                        PBMobileAds.getInstance().log("onAdClosed: ADNative Placement '" + placement + "'");
                        if (adDelegate == null) return;
                        adDelegate.onAdOpened("onAdClosed: ADNative Placement '" + placement + "'");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();

                        PBMobileAds.getInstance().log("onAdClicked: ADNative Placement '" + placement + "'");
                        if (adDelegate == null) return;
                        adDelegate.onAdOpened("onAdClicked: ADNative Placement '" + placement + "'");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();

                        PBMobileAds.getInstance().log("onAdLeftApplication: ADNative Placement '" + placement + "'");
                        if (adDelegate == null) return;
                        adDelegate.onAdOpened("onAdLeftApplication: ADNative Placement '" + placement + "'");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        super.onAdFailedToLoad(errorCode);

                        String messErr = PBMobileAds.getInstance().getADError(errorCode);
                        PBMobileAds.getInstance().log("onAdFailedToLoad: ADNative Placement '" + placement + "' with error: " + messErr);
                        if (adDelegate == null) return;
                        adDelegate.onAdFailedToLoad("onAdFailedToLoad: ADNative Placement '" + placement + "' with error: " + messErr);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        // Create Request PBS
        final PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();
        if (PBMobileAds.getInstance().isTestMode) {
            builder.addTestDevice(Constants.DEVICE_ID_TEST);
        }
        this.amRequest = builder.build();
        this.adUnit.fetchDemand(this.amRequest, new OnCompleteListener() {
            @Override
            public void onComplete(ResultCode resultCode) {
                PBMobileAds.getInstance().log("Prebid demand fetch ADNative placement '" + placement + "' for DFP: " + resultCode.name());
                handerResult(resultCode);
            }
        });

        return true;
    }

    public void destroy() {
        PBMobileAds.getInstance().log("Destroy ADNative Placement: " + this.placement);

        this.resetAD();
        if (this.timerRecall != null) {
            this.timerRecall.cancel();
            this.timerRecall = null;
        }
    }

    public void setListener(AdDelegate adDelegate) {
        this.adDelegate = adDelegate;
    }

    public int getWidth() {
        return this.amNative.getWidth();
    }

    public int getHeight() {
        return this.amNative.getHeight();
    }

    public void setAutoRefreshMillis(int timeMillis) {
        this.timeAutoRefresh = timeMillis;
        if (this.adUnit != null) {
            this.adUnit.setAutoRefreshPeriodMillis(timeMillis);
        }
    }

    public boolean isLoaded() {
        return this.isLoadNativeSucc;
    }

}

