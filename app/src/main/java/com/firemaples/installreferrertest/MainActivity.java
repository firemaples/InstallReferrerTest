package com.firemaples.installreferrertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.firemaples.installreferrertest.events.OnReceivedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {
    private final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private final String HTML_NEW_LINE = "<br/>";

    private TextView tv_text;

    private String fromGoogleApi;
    private String fromReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_text = findViewById(R.id.tv_text);

        getInfoFromGoogleApi();
        updateFromReceiver(InstallReferrerUtil.getInstallReferrerReceiverResult(this));
        updateText();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateText();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void updateFromReceiver(InstallReferrerResult result) {
        fromReceiver = decorateBold("From receiver:") + HTML_NEW_LINE;
        if (result != null) {
            fromReceiver += decorateBold("referrer: ") + result.referrer + HTML_NEW_LINE;
            fromReceiver += decorateBold("utm_source: ") + result.utm_source + HTML_NEW_LINE;
            fromReceiver += decorateBold("utm_medium: ") + result.utm_medium + HTML_NEW_LINE;
            fromReceiver += decorateBold("utm_campaign: ") + result.utm_campaign + HTML_NEW_LINE;
            fromReceiver += decorateBold("utm_content: ") + result.utm_content + HTML_NEW_LINE;
            fromReceiver += decorateBold("utm_term: ") + result.utm_term;
        } else {
            fromReceiver += "NULL";
        }
    }

    private void updateText() {
        String divider = HTML_NEW_LINE + HTML_NEW_LINE + "======================" +
                HTML_NEW_LINE + HTML_NEW_LINE;

        String fullText = fromGoogleApi +
                divider + fromReceiver;

        tv_text.setText(Html.fromHtml(fullText));
    }

    private void getInfoFromGoogleApi() {
        new InstallReferrerUtil().getInstallReferrerInfo(this, (success, installReferrer, error) -> {
            String text = decorateBold("From Google API:") + HTML_NEW_LINE;
            if (success) {
                logger.info("Get install referrer success");

                text += decorateBold("referrer: ") + installReferrer.getInstallReferrer() + HTML_NEW_LINE;
                text += decorateBold("click timestamp(sec): ") +
                        installReferrer.getReferrerClickTimestampSeconds() + HTML_NEW_LINE;
                text += decorateBold("install begin timestamp(sec): ") +
                        installReferrer.getInstallBeginTimestampSeconds();
            } else {
                logger.error("Get install referrer failed");
                text += decorateBold("Failed: ");
                if (error != null) {
                    text += error.getMessage();
                } else {
                    text += "Unknown";
                }
            }

            fromGoogleApi = text;

            updateText();
        });
    }

    private static String decorateBold(String text) {
        return "<b>" + text + "</b>";
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnReceivedEvent event) {
        updateFromReceiver(event.getInstallReferrerResult());
        updateText();
    }
}
