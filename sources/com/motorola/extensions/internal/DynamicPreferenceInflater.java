package com.motorola.extensions.internal;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.XmlResourceParser;
import android.view.InflateException;
import com.motorola.extensions.internal.DynamicPreferenceAttrHandler;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

public class DynamicPreferenceInflater {
    private static HashMap<String, Integer> sAttributeMap;
    private final ActivityInfo mActivityInfo;
    Context mTargetContext;

    static {
        HashMap<String, Integer> hashMap = new HashMap<>();
        sAttributeMap = hashMap;
        hashMap.put("title", 1);
        sAttributeMap.put("summary", 2);
        sAttributeMap.put("key", 3);
        sAttributeMap.put("replace", 4);
        sAttributeMap.put("orderAbove", 5);
        sAttributeMap.put("orderBelow", 6);
        sAttributeMap.put("icon", 7);
        sAttributeMap.put("remove", 8);
        sAttributeMap.put("orderFirst", 9);
        sAttributeMap.put("summaryOn", 10);
        sAttributeMap.put("summaryOff", 11);
        sAttributeMap.put("dataAuthority", 12);
        sAttributeMap.put("enabled", 13);
        sAttributeMap.put("dependency", 14);
        sAttributeMap.put("entries", 15);
        sAttributeMap.put("entryValues", 16);
        sAttributeMap.put("dialogTitle", 17);
        sAttributeMap.put("dialogIcon", 18);
        sAttributeMap.put("positiveButtonText", 19);
        sAttributeMap.put("negativeButtonText", 20);
        sAttributeMap.put("summaryEntries", 21);
        sAttributeMap.put("style", 22);
        sAttributeMap.put("autoRefresh", 23);
        sAttributeMap.put("intercept", 24);
        sAttributeMap.put("onIntercept", 25);
        sAttributeMap.put("offIntercept", 26);
        sAttributeMap.put("selectable", 27);
        sAttributeMap.put("class", 28);
        sAttributeMap.put("redirectDependencyLinkage", 29);
        sAttributeMap.put("titleMaxlines", 30);
        sAttributeMap.put("orderPriority", 31);
        sAttributeMap.put("disable", 32);
        sAttributeMap.put("fragment", 33);
        sAttributeMap.put("showHtmlTitle", 34);
        sAttributeMap.put("showHtmlSummary", 35);
    }

    public DynamicPreferenceInflater(Context context, ActivityInfo activityInfo) {
        this.mTargetContext = context;
        this.mActivityInfo = activityInfo;
    }

    public static int getAttrCode(String str) {
        Integer num = sAttributeMap.get(str);
        if (num == null) {
            num = 0;
        }
        return num.intValue();
    }

    public void inflate(String str, DynamicPreferenceAttrHandler.Holder holder) {
        if (holder == null || holder.root == null) {
            throw new RuntimeException("Host preference screen is needed");
        }
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = this.mActivityInfo.loadXmlMetaData(this.mTargetContext.getPackageManager(), str);
            if (xmlResourceParser != null) {
                DynamicPreferenceParser.parse(this.mTargetContext, xmlResourceParser).inflate(holder.root.getContext(), holder);
                xmlResourceParser.close();
                return;
            }
            throw new XmlPullParserException("XML resource could not be found");
        } catch (XmlPullParserException e) {
            throw new InflateException("Error inflating DynamicPreference XML", e);
        } catch (IOException e2) {
            throw new InflateException("Error inflating DynamicPreference XML", e2);
        } catch (Throwable th) {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }
}
