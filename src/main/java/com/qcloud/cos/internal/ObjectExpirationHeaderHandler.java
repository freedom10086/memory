package com.qcloud.cos.internal;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qcloud.cos.Headers;
import com.qcloud.cos.http.CosHttpResponse;
import com.qcloud.cos.utils.DateUtils;

public class ObjectExpirationHeaderHandler<T extends ObjectExpirationResult>
        implements HeaderHandler<T> {

    /*
     * expiry-date="Sun, 11 Dec 2012 00:00:00 GMT", rule-id="baz rule"
     */

    private static final Pattern datePattern = Pattern.compile("expiry-date=\"(.*?)\"");
    private static final Pattern rulePattern = Pattern.compile("rule-id=\"(.*?)\"");

    private static final Logger log = LoggerFactory.getLogger(ObjectExpirationHeaderHandler.class);

    @Override
    public void handle(T result, CosHttpResponse response) {
        String expirationHeader = response.getHeaders().get(Headers.EXPIRATION);

        if (expirationHeader != null) {
            result.setExpirationTime(parseDate(expirationHeader));
            result.setExpirationTimeRuleId(parseRuleId(expirationHeader));
        }
    }

    private String parseRuleId(String expirationHeader) {
        Matcher matcher = rulePattern.matcher(expirationHeader);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Date parseDate(String expirationHeader) {
        Matcher matcher = datePattern.matcher(expirationHeader);
        if (matcher.find()) {
            String date = matcher.group(1);
            try {
                return DateUtils.parseRFC822Date(date);
            } catch (Exception exception) {
                log.warn("Error parsing expiry-date from x-cos-expiration " + "header.", exception);
            }
        }

        return null;
    }
}
