package com.motorola.extensions.internal;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class DynamicPreferenceParser {
    public static DynamicPreferenceAttrHandler parse(Context context, XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
        int eventType = xmlResourceParser.getEventType();
        while (eventType != 2 && eventType != 1) {
            eventType = xmlResourceParser.next();
        }
        return parseChildren(context, xmlResourceParser, Xml.asAttributeSet(xmlResourceParser));
    }

    private static DynamicPreferenceAttrHandler parseChildren(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        String name = xmlResourceParser.getName();
        DynamicPreferenceAttrHandler attrHandler = getAttrHandler(context, name);
        if (attrHandler != null) {
            attrHandler.readAttrs(attributeSet);
            int next = xmlResourceParser.next();
            while (true) {
                String name2 = xmlResourceParser.getName();
                if (next != 1) {
                    if (next != 2) {
                        if (next == 3 && name2.equals(name)) {
                            return attrHandler;
                        }
                    } else if (name2.equals("intent")) {
                        attrHandler.setIntent(Intent.parseIntent(context.getResources(), xmlResourceParser, attributeSet));
                    } else if (name2.equals(name)) {
                        throw new IllegalArgumentException("Can not have " + name2 + " directly under " + name);
                    } else if (attrHandler instanceof DynamicCategoryPreferenceAttrHandler) {
                        ((DynamicCategoryPreferenceAttrHandler) attrHandler).addChild(parseChildren(context, xmlResourceParser, attributeSet));
                    } else {
                        throw new RuntimeException(xmlResourceParser.getPositionDescription() + ": <" + name2 + "> is not supported, ");
                    }
                    next = xmlResourceParser.next();
                } else {
                    throw new RuntimeException("Unexpected end of document");
                }
            }
        } else {
            throw new RuntimeException(xmlResourceParser.getPositionDescription() + ": <" + name + "> is not supported, ");
        }
    }

    private static DynamicPreferenceAttrHandler getAttrHandler(Context context, String str) {
        if (str.equals("dynamic-category-preference")) {
            return new DynamicCategoryPreferenceAttrHandler(context);
        }
        if (str.equals("dynamic-preference")) {
            return new DynamicPreferenceAttrHandler(context);
        }
        if (str.equals("dynamic-checkbox-preference")) {
            return new DynamicCheckboxPreferenceAttrHandler(context);
        }
        if (str.equals("dynamic-switch-preference")) {
            return new DynamicSwitchPreferenceAttrHandler(context);
        }
        if (str.equals("dynamic-list-preference")) {
            return new DynamicListPreferenceAttrHandler(context);
        }
        return null;
    }
}
