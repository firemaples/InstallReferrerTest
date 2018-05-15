package com.firemaples.installreferrertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class InstallReferrerUtil {
    private final Logger logger = LoggerFactory.getLogger(InstallReferrerUtil.class);

    public void getInstallReferrerInfo(Context context, final OnInstallReferrerUtilCallback callback) {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            return;
        }

        final InstallReferrerClient client = InstallReferrerClient.newBuilder(context).build();
        client.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                logger.debug("onInstallReferrerSetupFinished(), responseCode: " + responseCode);

                String error;
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established
                        _doGetInstallReferrerInfo(client, callback);
                        return;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app
                        error = "FEATURE_NOT_SUPPORTED";
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection could not be established
                        error = "SERVICE_UNAVAILABLE";
                        break;
                    default:
                        error = "UNKNOWN_ERROR";
                        break;
                }

                callback.onResult(false, null, new IllegalStateException(error));
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

                logger.debug("onInstallReferrerServiceDisconnected()");
            }
        });
    }

    private void _doGetInstallReferrerInfo(InstallReferrerClient client, OnInstallReferrerUtilCallback callback) {
        try {
            ReferrerDetails installReferrer = client.getInstallReferrer();

            logger.info(String.format(
                    Locale.US,
                    "_doGetInstallReferrerInfo(), install referrer: %s," +
                            " referrer click timestamp(sec): %d," +
                            " install begin timestamp(sec): %d",
                    installReferrer.getInstallReferrer(),
                    installReferrer.getReferrerClickTimestampSeconds(),
                    installReferrer.getInstallBeginTimestampSeconds()
            ));

            callback.onResult(true, installReferrer, null);
        } catch (RemoteException e) {
            e.printStackTrace();

            callback.onResult(false, null, e);
        }
    }

    @SuppressLint("ApplySharedPref")
    public static void setInstallReferrerReceiverResult(
            Context context,
            InstallReferrerResult installReferrerResult) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE);

        String json = new JsonUtil<InstallReferrerResult>().writeJson(installReferrerResult);

        sp.edit().putString("InstallReferrerReceiverResult", json).commit();
    }

    public static InstallReferrerResult getInstallReferrerReceiverResult(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE);

        String json = sp.getString("InstallReferrerReceiverResult", null);

        if (json != null) {
            //noinspection UnnecessaryLocalVariable
            InstallReferrerResult result =
                    new JsonUtil<InstallReferrerResult>()
                            .parseJson(json, InstallReferrerResult.class);
            return result;
        } else {
            return null;
        }
    }

    public interface OnInstallReferrerUtilCallback {
        void onResult(boolean success, ReferrerDetails installReferrer, Throwable error);
    }
}
