/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package com.z.zz.zzz.xml;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WhiteListXmlParser {
    private static String TAG = "WhiteListXmlParser";
    private String androidId = "";
    private List<WhiteListEntry> pluginEntries = new LinkedList<>();

    public List<WhiteListEntry> getPluginEntries() {
        return pluginEntries;
    }

    public void parse(Context action) {
        // First checking the class namespace for config.xml
        int id = action.getResources().getIdentifier("anti_detect_white_list", "xml", action.getClass().getPackage().getName());
        if (id == 0) {
            // If we couldn't find config.xml there, we'll look in the namespace from AndroidManifest.xml
            id = action.getResources().getIdentifier("anti_detect_white_list", "xml", action.getPackageName());
            if (id == 0) {
                Log.e(TAG, "res/xml/anti_detect_white_list.xml is missing!");
                return;
            }
        }
        parse(action.getResources().getXml(id));
    }

    private void parse(XmlPullParser xml) {
        int eventType = -1;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag(xml);
            } else if (eventType == XmlPullParser.END_TAG) {
                handleEndTag(xml);
            }
            try {
                eventType = xml.next();
            } catch (XmlPullParserException e) {
                Log.e(TAG, "parse error", e);
            } catch (IOException e) {
                Log.e(TAG, "parse error", e);
            }
        }

        Log.d(TAG, "parse: " + pluginEntries);
    }

    private void handleStartTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if ("android_id".equals(strNode)) {
            androidId = xml.getAttributeValue(null, "id");
        }
    }

    private void handleEndTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if ("android_id".equals(strNode)) {
            pluginEntries.add(new WhiteListEntry(androidId));

            androidId = "";
        }
    }

}
