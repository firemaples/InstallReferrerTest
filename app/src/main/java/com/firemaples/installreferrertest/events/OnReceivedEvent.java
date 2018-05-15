package com.firemaples.installreferrertest.events;

import com.firemaples.installreferrertest.InstallReferrerResult;

public class OnReceivedEvent {
    private final InstallReferrerResult installReferrerResult;

    public OnReceivedEvent(InstallReferrerResult installReferrerResult) {
        this.installReferrerResult = installReferrerResult;
    }

    public InstallReferrerResult getInstallReferrerResult() {
        return installReferrerResult;
    }
}
