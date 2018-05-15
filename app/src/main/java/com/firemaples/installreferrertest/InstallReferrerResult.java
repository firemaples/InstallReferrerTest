package com.firemaples.installreferrertest;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstallReferrerResult {
    private static final String TAG = "InstallReferrerResult";

    private static final Pattern UTM_SOURCE_PATTERN = Pattern.compile("(^|&)utm_source=([^&#=]*)([#&]|$)");
    private static final Pattern UTM_MEDIUM_PATTERN = Pattern.compile("(^|&)utm_medium=([^&#=]*)([#&]|$)");
    private static final Pattern UTM_CAMPAIGN_PATTERN = Pattern.compile("(^|&)utm_campaign=([^&#=]*)([#&]|$)");
    private static final Pattern UTM_CONTENT_PATTERN = Pattern.compile("(^|&)utm_content=([^&#=]*)([#&]|$)");
    private static final Pattern UTM_TERM_PATTERN = Pattern.compile("(^|&)utm_term=([^&#=]*)([#&]|$)");

    public String referrer;
    public String utm_source;
    public String utm_medium;
    public String utm_campaign;
    public String utm_content;
    public String utm_term;

    public InstallReferrerResult(String referrer) {
        this.referrer = referrer;

        parse();
    }

    private void parse() {
        if (null == referrer) {
            throw new IllegalArgumentException("Referrer should not be null");
        } else {
            Matcher sourceMatcher = UTM_SOURCE_PATTERN.matcher(referrer);
            this.utm_source = this.find(sourceMatcher);

            Matcher mediumMatcher = UTM_MEDIUM_PATTERN.matcher(referrer);
            this.utm_medium = this.find(mediumMatcher);

            Matcher campaignMatcher = UTM_CAMPAIGN_PATTERN.matcher(referrer);
            this.utm_campaign = this.find(campaignMatcher);

            Matcher contentMatcher = UTM_CONTENT_PATTERN.matcher(referrer);
            this.utm_content = this.find(contentMatcher);

            Matcher termMatcher = UTM_TERM_PATTERN.matcher(referrer);
            this.utm_term = this.find(termMatcher);
        }
    }

    private String find(Matcher matcher) {
        if (matcher.find()) {
            String encoded = matcher.group(2);
            if (null != encoded) {
                try {
                    return URLDecoder.decode(encoded, "UTF-8");
                } catch (UnsupportedEncodingException var4) {
                    Log.e(TAG, "Could not decode a parameter into UTF-8");
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(getClass().getName());
            sb.append('@');
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append("[");
            sb.append("reference=").append(referrer);
            sb.append(",").append("utm_source=").append(utm_source);
            sb.append(",").append("utm_medium=").append(utm_medium);
            sb.append(",").append("utm_campaign=").append(utm_campaign);
            sb.append(",").append("utm_content=").append(utm_content);
            sb.append(",").append("utm_term=").append(utm_term);
            sb.append("]");
        } catch (Exception e) {
            Log.w(TAG, "dbgstr failed", e);
            // ignore
        }
        return sb.toString();
    }
}
