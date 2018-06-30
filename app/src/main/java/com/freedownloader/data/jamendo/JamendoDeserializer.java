package com.freedownloader.data.jamendo;

import com.freedownloader.bean.JamendoBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by liyanju on 2018/5/16.
 */

public class JamendoDeserializer implements JsonDeserializer<JamendoBean> {

    @Override
    public JamendoBean deserialize(JsonElement jsonElement, Type typeOfT,
                                   JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();

        JamendoBean jamendoModel = new JamendoBean();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject trackJO = jsonArray.get(i).getAsJsonObject();
            JamendoBean.JamendoResult jamendoResult = context.deserialize(trackJO,
                    JamendoBean.JamendoResult.class);
            jamendoResult.duration = jamendoResult.duration * 1000;
            jamendoModel.arrayList.add(jamendoResult);
        }
        return jamendoModel;
    }
}
