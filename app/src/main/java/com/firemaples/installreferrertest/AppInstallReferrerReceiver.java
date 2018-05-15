package com.firemaples.installreferrertest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.firemaples.installreferrertest.events.OnReceivedEvent;
import com.mixpanel.android.mpmetrics.InstallReferrerReceiver;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInstallReferrerReceiver extends InstallReferrerReceiver {
    private final Logger logger = LoggerFactory.getLogger(AppInstallReferrerReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Bundle extras = intent.getExtras();
        if (null != extras) {
            String referrer = extras.getString("referrer");
            if (null != referrer) {
                InstallReferrerResult installReferrerResult = new InstallReferrerResult(referrer);

                logger.debug("InstallReferrer, result: " + installReferrerResult.toString());

                InstallReferrerUtil
                        .setInstallReferrerReceiverResult(context, installReferrerResult);

                EventBus.getDefault().post(new OnReceivedEvent(installReferrerResult));
            }
        }
    }
}