package com.wit.page2reader.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wit.page2reader.Constants;

public class Log {

    public static class LogInfo {
        public String type;
        public boolean isValid;
        public String value;
        public String msg;

        public LogInfo(String type, boolean isValid, String value, String msg) {
            this.type = type;
            this.isValid = isValid;
            this.value = value;
            this.msg = msg;
        }
    }

    private ArrayList<LogInfo> logInfoList;

    public Log() {
        logInfoList = new ArrayList<LogInfo>();
    }

    public Log(String jsonString) {
        this();

        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String type = jsonObj.getString(Constants.TYPE);
            boolean isValid = jsonObj.getBoolean(Constants.IS_VALID);
            String value = null;
            if (jsonObj.has(Constants.VALUE)) value = jsonObj.getString(Constants.VALUE);
            String msg = null;
            if (jsonObj.has(Constants.MSG)) msg = jsonObj.getString(Constants.MSG);

            LogInfo logInfo = new LogInfo(type, isValid, value, msg);
            logInfoList.add(logInfo);
        }
    }

    public void addLogInfo(String type, boolean isValid, String value, String msg) {
        LogInfo logInfo = new LogInfo(type, isValid, value, msg);
        logInfoList.add(logInfo);
    }

    public boolean isValid() {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.isValid == false) {
                return false;
            }
        }
        return true;
    }

    public LogInfo getLogInfo(String type, boolean isValid, String value) {
        for (LogInfo logInfo : logInfoList) {
            if (logInfo.type.equals(type)) {
                if (logInfo.isValid == isValid) {
                    if (value != null) {
                        if (logInfo.value.equals(value)) {
                            return logInfo;
                        }
                    } else {
                        return logInfo;
                    }
                }
            }
        }
        return null;
    }

    public String getValue(String type, boolean isValid) {
        LogInfo logInfo = getLogInfo(type, isValid, null);
        return logInfo == null ? null : logInfo.value;
    }

    public String getMsg(String type, boolean isValid, String value) {
        LogInfo logInfo = getLogInfo(type, isValid, value);
        return logInfo == null ? null : logInfo.msg;
    }
}
