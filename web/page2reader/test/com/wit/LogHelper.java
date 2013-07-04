package com.wit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wit.base.BaseConstants;
import com.wit.base.Log;

public class LogHelper {

    public static void assertLogInfoType(Log log, String type,
            String value, boolean isValid, String msg) {
        try {
            String json = log.getJSONString();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (type.equals(jsonObject.getString(BaseConstants.TYPE))
                        && isValid == jsonObject.getBoolean(BaseConstants.IS_VALID)) {
                    // If value is null, no this property in JsonObject.
                    if (value == null) {
                        try {
                            jsonObject.getString(BaseConstants.VALUE);
                            fail();
                        } catch (JSONException e) { }
                    } else {
                        assertEquals(value, jsonObject.getString(BaseConstants.VALUE));
                    }

                    if (msg == null) {
                        try {
                            jsonObject.getString(BaseConstants.MSG);
                            fail();
                        } catch (JSONException e) { }
                    } else {
                        assertEquals(msg, jsonObject.getString(BaseConstants.MSG));
                    }
                    return;
                }
            }
            fail();
        } catch (JSONException e) {
            fail();
        }
    }

    public static boolean isLogInfoTypeValid(Log log, String type) {
        try {
            String json = log.getJSONString();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (type.equals(jsonObject.getString(BaseConstants.TYPE))) {
                    return jsonObject.getBoolean(BaseConstants.IS_VALID);
                }
            }
        } catch (JSONException e) {

        }
        return false;
    }

    public static String getLogInfoTypeValue(Log log, String type) {
        try {
            String json = log.getJSONString();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (type.equals(jsonObject.getString(BaseConstants.TYPE))) {
                    return jsonObject.getString(BaseConstants.VALUE);
                }
            }
        } catch (JSONException e) {

        }
        return null;
    }

    public static String getLogInfoTypeMsg(Log log, String type, String value) {
        try {
            String json = log.getJSONString();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (type.equals(jsonObject.getString(BaseConstants.TYPE))) {
                    if (value.equals(jsonObject.getString(BaseConstants.VALUE))) {
                        return jsonObject.getString(BaseConstants.MSG);
                    }
                }
            }
        } catch (JSONException e) {

        }
        return null;
    }
}
