package com.bil.bilmobileads.nativetemplates;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.formats.AdChoicesView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
//import com.google.android.gms.ads.formats.zzd;
//import com.google.android.gms.ads.formats.zze;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzabm;
import com.google.android.gms.internal.ads.zzaxi;
import com.google.android.gms.internal.ads.zzuv;
import com.google.android.gms.internal.ads.zzyp;
import com.google.android.gms.internal.ads.zzza;

public final class ADNativeView extends FrameLayout {
    private final FrameLayout zzbkb;
    private final zzabm zzbkc;

    public ADNativeView(Context var1) {
        super(var1);
        this.zzbkb = this.zzd(var1);
        this.zzbkc = this.zzjf();
    }

    public ADNativeView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.zzbkb = this.zzd(var1);
        this.zzbkc = this.zzjf();
    }

    public ADNativeView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.zzbkb = this.zzd(var1);
        this.zzbkc = this.zzjf();
    }

    @TargetApi(21)
    public ADNativeView(Context var1, AttributeSet var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.zzbkb = this.zzd(var1);
        this.zzbkc = this.zzjf();
    }

    private final void zza(String var1, View var2) {
        try {
            this.zzbkc.zzc(var1, ObjectWrapper.wrap(var2));
        } catch (RemoteException var4) {
            zzaxi.zzc("Unable to call setAssetView on delegate", var4);
        }
    }

    public final void setHeadlineView(View var1) {
        this.zza("3001", var1);
    }

    public final void setCallToActionView(View var1) {
        this.zza("3002", var1);
    }

    public final void setIconView(View var1) {
        this.zza("3003", var1);
    }

    public final void setBodyView(View var1) {
        this.zza("3004", var1);
    }

    public final void setAdvertiserView(View var1) {
        this.zza("3005", var1);
    }

    public final void setStoreView(View var1) {
        this.zza("3006", var1);
    }

    public final void setClickConfirmingView(View var1) {
        try {
            this.zzbkc.zzi(ObjectWrapper.wrap(var1));
        } catch (RemoteException var3) {
            zzaxi.zzc("Unable to call setClickConfirmingView on delegate", var3);
        }
    }

    public final void setPriceView(View var1) {
        this.zza("3007", var1);
    }

    public final void setImageView(View var1) {
        this.zza("3008", var1);
    }

    public final void setStarRatingView(View var1) {
        this.zza("3009", var1);
    }

    public final void setMediaView(MediaView var1) {
        this.zza("3010", var1);
        if (var1 != null) {
//            var1.zza(new zzd(this));
//            var1.zza(new zze(this));
        }

    }

    public final void setAdChoicesView(AdChoicesView var1) {
        this.zza("3011", var1);
    }

    public final void setNativeAd(UnifiedNativeAd var1) {
//        try {
//            this.zzbkc.zze((IObjectWrapper)var1.zzjd());
//        } catch (RemoteException var3) {
//            zzaxi.zzc("Unable to call setNativeAd on delegate", var3);
//        }
    }

    private final View zzbk(String var1) {
        try {
            IObjectWrapper var2;
            if ((var2 = this.zzbkc.zzcj(var1)) != null) {
                return (View)ObjectWrapper.unwrap(var2);
            }
        } catch (RemoteException var3) {
            zzaxi.zzc("Unable to call getAssetView on delegate", var3);
        }

        return null;
    }

    public final View getHeadlineView() {
        return this.zzbk("3001");
    }

    public final View getCallToActionView() {
        return this.zzbk("3002");
    }

    public final View getIconView() {
        return this.zzbk("3003");
    }

    public final View getBodyView() {
        return this.zzbk("3004");
    }

    public final View getStoreView() {
        return this.zzbk("3006");
    }

    public final View getPriceView() {
        return this.zzbk("3007");
    }

    public final View getAdvertiserView() {
        return this.zzbk("3005");
    }

    public final View getImageView() {
        return this.zzbk("3008");
    }

    public final View getStarRatingView() {
        return this.zzbk("3009");
    }

    public final MediaView getMediaView() {
        View var1;
        if ((var1 = this.zzbk("3010")) instanceof MediaView) {
            return (MediaView)var1;
        } else {
            if (var1 != null) {
                zzaxi.zzdv("View is not an instance of MediaView");
            }

            return null;
        }
    }

    public final AdChoicesView getAdChoicesView() {
        View var1;
        return (var1 = this.zzbk("3011")) instanceof AdChoicesView ? (AdChoicesView)var1 : null;
    }

    public final void destroy() {
        try {
            this.zzbkc.destroy();
        } catch (RemoteException var2) {
            zzaxi.zzc("Unable to destroy native ad view", var2);
        }
    }

    private final FrameLayout zzd(Context var1) {
        FrameLayout var2;
        (var2 = new FrameLayout(var1)).setLayoutParams(new LayoutParams(-1, -1));
        this.addView(var2);
        return var2;
    }

    private final zzabm zzjf() {
        Preconditions.checkNotNull(this.zzbkb, "createDelegate must be called after overlayFrame has been created");
        return this.isInEditMode() ? null : zzuv.zzok().zza(this.zzbkb.getContext(), this, this.zzbkb);
    }

    public final void addView(View var1, int var2, android.view.ViewGroup.LayoutParams var3) {
        super.addView(var1, var2, var3);
        super.bringChildToFront(this.zzbkb);
    }

    public final void removeView(View var1) {
        if (this.zzbkb != var1) {
            super.removeView(var1);
        }
    }

    public final void removeAllViews() {
        super.removeAllViews();
        super.addView(this.zzbkb);
    }

    public final void bringChildToFront(View var1) {
        super.bringChildToFront(var1);
        if (this.zzbkb != var1) {
            super.bringChildToFront(this.zzbkb);
        }

    }

    public final void onVisibilityChanged(View var1, int var2) {
        super.onVisibilityChanged(var1, var2);
        if (this.zzbkc != null) {
            try {
                this.zzbkc.zzc(ObjectWrapper.wrap(var1), var2);
                return;
            } catch (RemoteException var4) {
                zzaxi.zzc("Unable to call onVisibilityChanged on delegate", var4);
            }
        }

    }

    public final boolean dispatchTouchEvent(MotionEvent var1) {
        zzyp var3 = zzza.zzcnp;
        if ((Boolean)zzuv.zzon().zzd(var3) && this.zzbkc != null) {
            try {
                this.zzbkc.zzj(ObjectWrapper.wrap(var1));
            } catch (RemoteException var4) {
                zzaxi.zzc("Unable to call handleTouchEvent on delegate", var4);
            }
        }

        return super.dispatchTouchEvent(var1);
    }
}
