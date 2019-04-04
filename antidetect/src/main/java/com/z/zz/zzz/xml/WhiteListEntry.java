package com.z.zz.zzz.xml;

public final class WhiteListEntry {

    public final String androidId;

    WhiteListEntry(String androidId) {
        this.androidId = androidId;
    }

    @Override
    public String toString() {
        return "androidId: " + androidId;
    }
}
