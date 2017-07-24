package com.iflytech;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Alpha on 2017/3/7.
 */

public class SpeechResultParser {
    public static String parseIatResult(String json) {
        StringBuffer buffer = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject jResult = new JSONObject(tokener);
            JSONArray words = jResult.getJSONArray("ws");//得到 ws ，也就是所有词的集合
            for (int i=0;i<words.length();i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");//每个词的中文分词
                JSONObject obj = items.getJSONObject(0);
                buffer.append(obj.getString("w"));//具体的中文汉字
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
