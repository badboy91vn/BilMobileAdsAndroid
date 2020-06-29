
package com.bil.bilmobileads;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.bil.bilmobileads.entity.ADFormat;
import com.bil.bilmobileads.entity.AdInfor;
import com.bil.bilmobileads.entity.AdUnitObj;
import com.bil.bilmobileads.entity.HostCustom;
import com.bil.bilmobileads.entity.TimerRecall;
import com.bil.bilmobileads.interfaces.ResultCallback;
import com.bil.bilmobileads.interfaces.TimerCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.prebid.mobile.Host;
import org.prebid.mobile.PrebidMobile;
import org.prebid.mobile.TargetingParams;

import java.util.ArrayList;

public class PBMobileAds {

    private static final PBMobileAds instance = new PBMobileAds();

    // MARK: Context app
    private Context contextApp;

    // MARK: List Config
    ArrayList<AdUnitObj> listAdUnitObj = new ArrayList<AdUnitObj>();

    // MARK: List AD
    ArrayList<ADBanner> listADBanner = new ArrayList<ADBanner>();
    ArrayList<ADInterstitial> listADIntersititial = new ArrayList<ADInterstitial>();
    ArrayList<ADRewarded> listADRewarded = new ArrayList<ADRewarded>();

    // MARK: api
    boolean isLog = true;
    boolean isConfigSucc = false;
    TimerRecall timerRecall;

    private String pbServerEndPoint = "";
    private String configId;


    private PBMobileAds() {
        this.log("PBMobileAds Init");

        timerRecall = new TimerRecall(Constants.RECALL_CONFIGID_SERVER, 1000);
        timerRecall.setListener(new TimerCompleteListener() {
            @Override
            public void doWork() {
                getADConfig();
            }
        });
    }

    public static PBMobileAds getInstance() {
        return instance;
    }

    // MARK: - initialize
    public void initialize(Context context, String configId, boolean testMode) {
//      if !isLog { PrebidMobile.logLevel = .error }

        this.contextApp = context;

        this.configId = configId;

        //Declare in init to the user agent could be passed in first call
        PrebidMobile.setShareGeoLocation(true);
        PrebidMobile.setApplicationContext(context);
        WebView obj = new WebView(context);
        obj.clearCache(true);

        // Setup Test Mode
        if (testMode == true) {
//            GADMobileAds.sharedInstance().requestConfiguration.testDeviceIdentifiers =  [ (kGADSimulatorID as! String), "cc7ca766f86b43ab6cdc92bed424069b"];
        }

        getADConfig();
    }

    public Context getContextApp() {
        return this.contextApp;
    }

    // MARK: - Call API AD
    private void getADConfig() {
        this.log("Start Request Config");

        HttpApi httpApi = new HttpApi<JSONObject>(Constants.GET_DATA_CONFIG + this.configId, new ResultCallback<JSONObject, Exception>() {
            @Override
            public void success(JSONObject dataJSON) {
//                log("Data: " + dataJSON.toString());

                timerRecall.cancel();
                isConfigSucc = true;
                try {
                    pbServerEndPoint = dataJSON.getString("pbServerEndPoint");
                    JSONArray adunitArray = dataJSON.getJSONArray("adunit");

                    // Set all ad type config
                    for (int i = 0; i < adunitArray.length(); i++) {
                        JSONObject adunitJsonObj = adunitArray.getJSONObject(i);

                        String placement = adunitJsonObj.getString("placement");
                        String type = adunitJsonObj.getString("type");
                        ADFormat defaultType = adunitJsonObj.getString("defaultType").equalsIgnoreCase(ADFormat.HTML.toString()) ? ADFormat.HTML : ADFormat.VAST;
                        boolean isActive = adunitJsonObj.getBoolean("isActive");

                        // Create AdInfor
                        ArrayList<AdInfor> adInforList = new ArrayList<AdInfor>();
                        JSONArray adInforArray = adunitJsonObj.getJSONArray("adInfor");
                        for (int j = 0; j < adInforArray.length(); j++) {
                            JSONObject adInforObj = adInforArray.getJSONObject(j);
                            boolean isVideo = adInforObj.getBoolean("isVideo");
                            String configId = adInforObj.getString("configId");
                            String adUnitID = adInforObj.getString("adUnitID");
                            // Create Host
                            JSONObject hostObj = adInforObj.getJSONObject("host");
                            String pbHost = hostObj.getString("pbHost");
                            String pbAccountId = hostObj.getString("pbAccountId");
                            String storedAuctionResponse = hostObj.getString("storedAuctionResponse");
                            HostCustom hostCustom = new HostCustom(pbHost, pbAccountId, storedAuctionResponse);

                            AdInfor adInfor = new AdInfor(isVideo, hostCustom, configId, adUnitID);
                            adInforList.add(adInfor);
                        }

                        AdUnitObj adUnitObj = new AdUnitObj(placement, type, defaultType, isActive, adInforList);
                        listAdUnitObj.add(adUnitObj);
                    }

                    // Call all ad init before
                    log("Banner Count: " + listADBanner.size());
                    for (ADBanner ad : listADBanner) {
                        ad.load();
                    }
                    log("Full Count: " + listADIntersititial.size());
                    for (ADInterstitial ad : listADIntersititial) {
                        ad.preLoad();
                    }
                    log("Rewarded Count: " + listADRewarded.size());
                    for (ADRewarded ad : listADRewarded) {
                        ad.preLoad();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    PBMobileAds.getInstance().log(e.getLocalizedMessage());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    PBMobileAds.getInstance().log(e.getLocalizedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    PBMobileAds.getInstance().log(e.getLocalizedMessage());
                }
            }

            @Override
            public void failure(Exception error) {
                log("Err: " + error.getLocalizedMessage());

                isConfigSucc = false;
                timerRecall.start();
            }
        });
        httpApi.execute();
    }

    // MARK: - Get Data Config
    AdUnitObj getAdUnitObj(String placement) {
        for (AdUnitObj config : this.listAdUnitObj) {
            if (config.placement.equalsIgnoreCase(placement)) {
                return config;
            }
        }
        return null;
    }

    // MARK: Setup PBS
    public void setupPBS(HostCustom hostCus) {
        this.log("Host: " + hostCus.pbHost + " | AccountId: " + hostCus.pbAccountId + " | storedAuctionResponse: " + hostCus.storedAuctionResponse);
        if (hostCus.pbHost.equalsIgnoreCase("Appnexus")) {
            PrebidMobile.setPrebidServerHost(Host.APPNEXUS);
        } else if (hostCus.pbHost.equalsIgnoreCase("Rubicon")) {
            PrebidMobile.setPrebidServerHost(Host.RUBICON);
        } else if (hostCus.pbHost.equalsIgnoreCase("Custom")) {
            this.log("Custom URL: " + this.pbServerEndPoint);
            Host.CUSTOM.setHostUrl(this.pbServerEndPoint);
            PrebidMobile.setPrebidServerHost(Host.CUSTOM);
        }

        PrebidMobile.setPrebidServerAccountId(hostCus.pbAccountId);
        PrebidMobile.setStoredAuctionResponse(hostCus.storedAuctionResponse);
    }

    public boolean isInitialize() {
        return isConfigSucc;
    }

    public void enableCOPPA() {
        TargetingParams.setSubjectToGDPR(true);
    }

    public void disableCOPPA() {
        TargetingParams.setSubjectToGDPR(false);
    }

    public boolean log(String object) {
        if (!isLog) {
            return false;
        }
        Log.d("PBMobileAds", object);
        return false;
    }
}
