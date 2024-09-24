package com.coloz.wifi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.SupplicantState;
import android.util.Log;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.espressif.iot.esptouch2.provision.TouchNetUtil;

import java.net.InetAddress;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class wifi extends CordovaPlugin {

    private CallbackContext wifiCallbackContext;
    private static final String TAG = "wifi";
    private WifiManager mWifiManager;
    private LocationManager mLocationManager;
    private static final int REQUEST_LOCATION = 1;


    protected void checkLocation() {
        JSONObject result = new JSONObject();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Context context = this.cordova.getContext();
            Activity activity = (Activity) context;
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            String messagePermission = "У приложения нет доступа к геопозиции.\n" + "Разрешите доступ к Вашей геопозиции в настройках устройства.";
            DialogInterface.OnClickListener onCancelListener =  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wifiCallbackContext.error("NOT_GRANTED");
                }
            };

            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(messagePermission).setPositiveButton(activity.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_LOCATION);
                        }
                    }).setNegativeButton(activity.getResources().getString(android.R.string.cancel), onCancelListener).show();
                } else {
                ActivityCompat.requestPermissions((Activity) this.cordova.getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);}
            } else {
                if (gps || network){
                    wifiCallbackContext.success(1);
                } else {
                    wifiCallbackContext.success(0);
                }
            }
            return;
        }
        wifiCallbackContext.success(1);
    }


    protected void getConnectedInfo() {
        JSONObject result = new JSONObject();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        try {
            if (!TouchNetUtil.isWifiConnected(mWifiManager)) {
                result.put("state", "NotConnected");
                wifiCallbackContext.error(result);
                return;
            }
            if (!wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED)) {
                result.put("state", "Connecting");
                wifiCallbackContext.error(result);
                return;
            }
            String ssid = TouchNetUtil.getSsidString(wifiInfo);
            InetAddress ip;
            int ipValue = wifiInfo.getIpAddress();
            if (ipValue != 0) {
                ip = TouchNetUtil.getAddress(wifiInfo.getIpAddress());
            } else {
                ip = TouchNetUtil.getIPv4Address();
                if (ip == null) {
                    ip = TouchNetUtil.getIPv6Address();
                }
            }
            result.put("ip", ip);
            result.put("is5G", TouchNetUtil.is5G(wifiInfo.getFrequency()));
            result.put("ssid", ssid);
            result.put("bssid", wifiInfo.getBSSID());
            result.put("state", "Connected");
            wifiCallbackContext.success(result);
        } catch (JSONException e) {
            Log.e(TAG, "unexpected JSON exception", e);
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mWifiManager = (WifiManager) cordova.getActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mLocationManager= (LocationManager) cordova.getActivity().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        wifiCallbackContext = callbackContext;

        if (action.equals("checkLocation")) {
            checkLocation();
        } else if (action.equals("getConnectedInfo")) {
            getConnectedInfo();
        } else if (action.equals("scan")) {

            callbackContext.success();
        } else if (action.equals("connect")) {

            callbackContext.success();
        } else if (action.equals("disconnect")) {

            callbackContext.success();
        } else {
            callbackContext.error("can not find the function " + action);
        }
        return true;
    }
}