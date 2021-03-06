/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The default {@link SpdySettingsFrame} implementation.
 */
public class DefaultSpdySettingsFrame implements SpdySettingsFrame {

    private boolean clear;
    private final Map<Integer, Setting> settingsMap = new TreeMap<Integer, Setting>();

    /**
     * Creates a new instance.
     */
    public DefaultSpdySettingsFrame() {
    }

    @Override
    public Set<Integer> getIDs() {
        return settingsMap.keySet();
    }

    @Override
    public boolean isSet(int ID) {
        Integer key = new Integer(ID);
        return settingsMap.containsKey(key);
    }

    @Override
    public int getValue(int ID) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            return settingsMap.get(key).getValue();
        } else {
            return -1;
        }
    }

    @Override
    public void setValue(int ID, int value) {
        setValue(ID, value, false, false);
    }

    @Override
    public void setValue(int ID, int value, boolean persistValue, boolean persisted) {
        if (ID <= 0 || ID > SpdyCodecUtil.SPDY_SETTINGS_MAX_ID) {
            throw new IllegalArgumentException("Setting ID is not valid: " + ID);
        }
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            Setting setting = settingsMap.get(key);
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
        } else {
            settingsMap.put(key, new Setting(value, persistValue, persisted));
        }
    }

    @Override
    public void removeValue(int ID) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            settingsMap.remove(key);
        }
    }

    @Override
    public boolean persistValue(int ID) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            return settingsMap.get(key).getPersist();
        } else {
            return false;
        }
    }

    @Override
    public void setPersistValue(int ID, boolean persistValue) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            settingsMap.get(key).setPersist(persistValue);
        }
    }

    @Override
    public boolean isPersisted(int ID) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            return settingsMap.get(key).getPersisted();
        } else {
            return false;
        }
    }

    @Override
    public void setPersisted(int ID, boolean persisted) {
        Integer key = new Integer(ID);
        if (settingsMap.containsKey(key)) {
            settingsMap.get(key).setPersisted(persisted);
        }
    }

    @Override
    public boolean clearPreviouslyPersistedSettings() {
        return clear;
    }

    @Override
    public void setClearPreviouslyPersistedSettings(boolean clear) {
        this.clear = clear;
    }

    private Set<Map.Entry<Integer, Setting>> getSettings() {
        return settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder buf) {
        for (Map.Entry<Integer, Setting> e: getSettings()) {
            Setting setting = e.getValue();
            buf.append("--> ");
            buf.append(e.getKey().toString());
            buf.append(":");
            buf.append(setting.getValue());
            buf.append(" (persist value: ");
            buf.append(setting.getPersist());
            buf.append("; persisted: ");
            buf.append(setting.getPersisted());
            buf.append(')');
            buf.append(StringUtil.NEWLINE);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append(StringUtil.NEWLINE);
        appendSettings(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    private static final class Setting {

        private int value;
        private boolean persist;
        private boolean persisted;

        public Setting(int value, boolean persist, boolean persisted) {
            this.value = value;
            this.persist = persist;
            this.persisted = persisted;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean getPersist() {
            return persist;
        }

        public void setPersist(boolean persist) {
            this.persist = persist;
        }

        public boolean getPersisted() {
            return persisted;
        }

        public void setPersisted(boolean persisted) {
            this.persisted = persisted;
        }
    }
}
