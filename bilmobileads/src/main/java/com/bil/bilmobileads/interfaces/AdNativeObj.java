package com.bil.bilmobileads.interfaces;

import android.os.Bundle;

import com.google.android.gms.ads.MuteThisAdListener;
import com.google.android.gms.ads.MuteThisAdReason;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.List;

public class AdNativeObj extends UnifiedNativeAd {
    @Override
    public String getHeadline() {
        return null;
    }

    @Override
    public List<NativeAd.Image> getImages() {
        return null;
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public NativeAd.Image getIcon() {
        return null;
    }

    @Override
    public String getCallToAction() {
        return null;
    }

    @Override
    public String getAdvertiser() {
        return null;
    }

    @Override
    public Double getStarRating() {
        return null;
    }

    @Override
    public String getStore() {
        return null;
    }

    @Override
    public String getPrice() {
        return null;
    }

    @Override
    public VideoController getVideoController() {
        return null;
    }

    @Override
    public NativeAd.AdChoicesInfo getAdChoicesInfo() {
        return null;
    }

    @Override
    public String getMediationAdapterClassName() {
        return null;
    }

    @Override
    public boolean isCustomMuteThisAdEnabled() {
        return false;
    }

    @Override
    public List<MuteThisAdReason> getMuteThisAdReasons() {
        return null;
    }

    @Override
    public void muteThisAd(MuteThisAdReason muteThisAdReason) {

    }

    @Override
    public void setMuteThisAdListener(MuteThisAdListener muteThisAdListener) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setUnconfirmedClickListener(UnconfirmedClickListener unconfirmedClickListener) {

    }

    @Override
    public void cancelUnconfirmedClick() {

    }

    @Override
    public void enableCustomClickGesture() {

    }

    @Override
    public boolean isCustomClickGestureEnabled() {
        return false;
    }

    @Override
    public void recordCustomClickGesture() {

    }

    @Override
    public void performClick(Bundle bundle) {

    }

    @Override
    public boolean recordImpression(Bundle bundle) {
        return false;
    }

    @Override
    public void reportTouchEvent(Bundle bundle) {

    }

    @Override
    public MediaContent getMediaContent() {
        return null;
    }

    @Override
    protected Object zzjd() {
        return null;
    }

    @Override
    public Object zzji() {
        return null;
    }
}
